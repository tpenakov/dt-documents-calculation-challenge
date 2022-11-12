package design.technologies.api.business.service.impl;

import design.technologies.api.core.exception.InvalidInputException;
import design.technologies.api.core.model.DtDocument;
import design.technologies.api.core.model.DtMoney;
import design.technologies.api.core.service.DocumentProcessor;
import design.technologies.api.core.service.MoneyProcessor;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static design.technologies.api.business.model.BusinessConstants.*;

/*
 * Created by triphon 12.11.22 г.
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@Slf4j
public class DocumentProcessorImpl implements DocumentProcessor {

  public static final String EXCHANGE_RATE_SEPARATOR = ":";

  private final MoneyProcessor moneyProcessor;
  private final Validator validator;

  ThreadLocal<List<DtDocument>> documentStorage = ThreadLocal.withInitial(List::of);

  @Override
  public void isValid(
      final List<DtDocument> documents,
      final List<String> exchangeRates,
      final String outputCurrency) {
    checkForEmptyInput(documents, outputCurrency);
    validateDocuments(documents);
    validateCurrencies(documents, exchangeRates);
  }

  @Override
  public void save(final List<DtDocument> documents) {
    getDocumentStorage().set(documents);
  }

  @Override
  public List<DtDocument> extract(
      final List<String> exchangeRates, final String outputCurrency, final String customerVat) {
    final Map<String, BigDecimal> exchangeRatesMap =
        exchangeRates.stream()
            .map(DocumentProcessorImpl::toExchangeRate)
            .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));

    final boolean filterByVat = StringUtils.isNotBlank(customerVat);

    final Function<DtDocument, String> getKeyFn = dtDocument -> dtDocument.getCustomer().getVat();
    final Function<DtDocument, DtDocument> getValueFn =
        dtDocument ->
            DtDocument.Type.CREDIT_NOTE.equals(dtDocument.getType())
                ? negateAmount(dtDocument)
                : dtDocument;
    final BinaryOperator<DtDocument> sumFn =
        (dtDocument, dtDocument2) ->
            DtDocument.Type.CREDIT_NOTE.equals(dtDocument2.getType())
                ? subtractAmount(dtDocument, dtDocument2, exchangeRatesMap)
                : addAmount(dtDocument, dtDocument2, exchangeRatesMap);
    final Map<String, DtDocument> resultMap =
        getDocumentStorage().get().stream()
            .filter(
                dtDocument ->
                    filterByVat
                        ? StringUtils.equals(customerVat, getKeyFn.apply(dtDocument))
                        : true)
            .collect(Collectors.toMap(getKeyFn, getValueFn, sumFn));

    return resultMap.values().stream()
        .peek(
            dtDocument -> {
              dtDocument.setType(DtDocument.Type.INVOICE);
              dtDocument.setBalance(
                  getMoneyProcessor()
                      .convert(dtDocument.getBalance(), outputCurrency, exchangeRatesMap));
            })
        .collect(Collectors.toList());
  }

  DtDocument negateAmount(final DtDocument input) {
    final DtMoney zeroBalance =
        DtMoney.builder()
            .amount(BigDecimal.ZERO)
            .currency(input.getBalance().getCurrency())
            .build();
    input.setBalance(getMoneyProcessor().subtract(zeroBalance, input.getBalance(), Map.of()));
    return input;
  }

  DtDocument addAmount(
      final DtDocument input,
      final DtDocument input1,
      final Map<String, BigDecimal> exchangeRatesMap) {
    input.setBalance(
        getMoneyProcessor().add(input.getBalance(), input1.getBalance(), exchangeRatesMap));
    return input;
  }

  DtDocument subtractAmount(
      final DtDocument input,
      final DtDocument input1,
      final Map<String, BigDecimal> exchangeRatesMap) {
    input.setBalance(
        getMoneyProcessor().subtract(input.getBalance(), input1.getBalance(), exchangeRatesMap));
    return input;
  }

  static void checkForEmptyInput(final List<DtDocument> documents, final String outputCurrency) {
    if (CollectionUtils.isEmpty(documents)) {
      throw new InvalidInputException(DOCUMENTS_ARE_MISSING);
    }

    if (CollectionUtils.isEmpty(documents)) {
      throw new InvalidInputException(MISSING_EXCHANGE_RATES);
    }

    if (StringUtils.isBlank(outputCurrency)) {
      throw new InvalidInputException(MISSING_OUTPUT_CURRENCY);
    }
  }

  void validateDocuments(final List<DtDocument> documents) {
    final Map<String, DtDocument> documentsById = new HashMap<>(documents.size());
    final Set<String> parents = new HashSet<>();

    documents.forEach(
        dtDocument -> {
          if (Objects.isNull(dtDocument)) {
            throw new InvalidInputException(NULL_DOCUMENT);
          }
          getValidator().validate(dtDocument);
          final String number = dtDocument.getNumber();
          if (documentsById.containsKey(number)) {
            throw new InvalidInputException(DUPLICATE_NUMBER + number);
          }

          documentsById.put(number, dtDocument);
          Optional.ofNullable(dtDocument.getParent())
              .map(DtDocument::getNumber)
              .filter(StringUtils::isNotBlank)
              .ifPresent(parents::add);
        });

    parents.forEach(
        parentId -> {
          if (!documentsById.containsKey(parentId)) {
            throw new InvalidInputException("Missing document for parentId=" + parentId);
          }
        });
  }

  static void validateCurrencies(
      final List<DtDocument> documents, final List<String> exchangeRates) {
    final AtomicReference<String> defaultCurrency = new AtomicReference<>();
    final Map<String, BigDecimal> exchangeRatesMap = new HashMap<>(exchangeRates.size());
    exchangeRates.forEach(
        exchangeRateRaw -> {
          final Tuple2<String, BigDecimal> exchangeRate = toExchangeRate(exchangeRateRaw);
          final BigDecimal rate = exchangeRate._2();
          final String currency = exchangeRate._1();
          if (exchangeRatesMap.containsKey(currency)) {
            throw new InvalidInputException(DUPLICATED_CURRENCY + currency);
          }
          exchangeRatesMap.put(currency, rate);
          if (BigDecimal.ONE.equals(rate)) {
            defaultCurrency.set(currency);
          }
        });
    if (StringUtils.isBlank(defaultCurrency.get())) {
      throw new InvalidInputException(MISSING_DEFAULT_CURRENCY);
    }
    documents.stream()
        .map(DtDocument::getBalance)
        .map(DtMoney::getCurrency)
        .forEach(
            currency -> {
              if (!exchangeRatesMap.containsKey(currency)) {
                throw new InvalidInputException("Missing currency: " + currency);
              }
            });
  }

  static Tuple2<String, BigDecimal> toExchangeRate(final String exchangeRateRaw) {
    final String[] exchangeRateArray = StringUtils.split(exchangeRateRaw, EXCHANGE_RATE_SEPARATOR);
    if (ArrayUtils.isEmpty(exchangeRateArray) || exchangeRateArray.length != 2) {
      throw new InvalidInputException(WRONG_EXCHANGE_RATE_RAW_DATA + exchangeRateRaw);
    }

    return Tuple.of(exchangeRateArray[0], NumberUtils.createBigDecimal(exchangeRateArray[1]));
  }
}
