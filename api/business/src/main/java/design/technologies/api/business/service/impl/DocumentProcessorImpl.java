package design.technologies.api.business.service.impl;

import design.technologies.api.core.exception.InvalidInputException;
import design.technologies.api.core.model.DtDocument;
import design.technologies.api.core.service.DocumentProcessor;
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

import static design.technologies.api.business.model.BusinessConstants.*;

/*
 * Created by triphon 12.11.22 г.
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@Slf4j
public class DocumentProcessorImpl implements DocumentProcessor {

  public static final String EXCHANGE_RATE_SEPARATOR = ":";
  private final Validator validator;

  ThreadLocal<List<DtDocument>> documentStorage = ThreadLocal.withInitial(() -> List.of());

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
    return null;
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
              .ifPresent(parentId -> parents.add(parentId));
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
          final String[] exchangeRateArray =
              StringUtils.split(exchangeRateRaw, EXCHANGE_RATE_SEPARATOR);
          if (ArrayUtils.isEmpty(exchangeRateArray) || exchangeRateArray.length != 2) {
            throw new InvalidInputException(WRONG_EXCHANGE_RATE_RAW_DATA + exchangeRateRaw);
          }

          final BigDecimal rate = NumberUtils.createBigDecimal(exchangeRateArray[1]);
          final String currency = exchangeRateArray[0];
          exchangeRatesMap.put(currency, rate);
          if (BigDecimal.ONE.equals(rate)) {
            defaultCurrency.set(currency);
          }
        });
    if (StringUtils.isBlank(defaultCurrency.get())) {
      throw new InvalidInputException(MISSING_DEFAULT_CURRENCY);
    }
    documents.stream()
        .map(DtDocument::getCurrencyCode)
        .forEach(
            currencyCode -> {
              if (!exchangeRatesMap.containsKey(currencyCode)) {
                throw new InvalidInputException("Missing currency: " + currencyCode);
              }
            });
  }
}
