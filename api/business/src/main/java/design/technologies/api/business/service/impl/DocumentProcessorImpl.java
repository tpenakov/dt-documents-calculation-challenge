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

import javax.validation.ConstraintViolation;
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

  private final MoneyProcessor moneyProcessor;
  private final Validator validator;

  ThreadLocal<List<DtDocument>> documentStorage = ThreadLocal.withInitial(List::of);

  @Override
  public void isValid(
      final List<DtDocument> documents,
      final List<String> exchangeRates,
      final String outputCurrency) {
    checkForEmptyInput(documents, exchangeRates, outputCurrency);
    validateDocuments(documents);
    validateCurrencies(documents, exchangeRates);
  }

  @Override
  public void save(final List<DtDocument> documents) {
    getDocumentStorage().set(documents);
  }

  @Override
  public List<DtDocument> extract(
      final List<String> exchangeRates, final String outputCurrency, final String customerVatRaw) {
    final Map<String, BigDecimal> exchangeRatesMap = toExchangeRatesMap(exchangeRates);

    final String customerVat = StringUtils.trim(customerVatRaw);
    final boolean filterByVat = StringUtils.isNotBlank(customerVat);

    final Function<DtDocument, String> getKeyFn = dtDocument -> dtDocument.getCustomer().getVat();
    final Function<DtDocument, DtDocument> getValueFn =
        dtDocument ->
            DtDocument.Type.CREDIT_NOTE.equals(dtDocument.getType())
                ? negateAmount(dtDocument)
                : dtDocument;
    final BinaryOperator<DtDocument> sumFn =
        (dtDocument, dtDocument2) -> addAmount(dtDocument, dtDocument2, exchangeRatesMap);
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
              dtDocument.setParent(null);
            })
        .collect(Collectors.toList());
  }

  Map<String, BigDecimal> toExchangeRatesMap(final List<String> exchangeRates) {
    return exchangeRates.stream()
        .map(DocumentProcessorImpl::toExchangeRate)
        .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
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

  void checkForEmptyInput(
      final List<DtDocument> documents,
      final List<String> exchangeRates,
      final String outputCurrency) {
    if (CollectionUtils.isEmpty(documents)) {
      throw new InvalidInputException(DOCUMENTS_ARE_MISSING);
    }

    if (CollectionUtils.isEmpty(exchangeRates)) {
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
          final Set<ConstraintViolation<DtDocument>> validateResult =
              getValidator().validate(dtDocument);
          if (CollectionUtils.isNotEmpty(validateResult)) {
            throw new InvalidInputException(validateResult.toString());
          }
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
            throw new InvalidInputException(MISSING_DOCUMENT_FOR_PARENT_ID + parentId);
          }
        });
  }

  void validateCurrencies(final List<DtDocument> documents, final List<String> exchangeRates) {
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
          if (getMoneyProcessor()
              .toInternalScale(BigDecimal.ONE)
              .equals(getMoneyProcessor().toInternalScale(rate))) {
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
                throw new InvalidInputException(MISSING_CURRENCY + currency);
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
