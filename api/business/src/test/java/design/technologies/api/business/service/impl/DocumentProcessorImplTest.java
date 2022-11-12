package design.technologies.api.business.service.impl;

import design.technologies.api.business.model.BusinessConstants;
import design.technologies.api.business.model.TestConstants;
import design.technologies.api.business.utils.AllTestUtils;
import design.technologies.api.core.exception.InvalidInputException;
import design.technologies.api.core.model.DtDocument;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static design.technologies.api.business.model.BusinessConstants.MISSING_DOCUMENT_FOR_PARENT_ID;
import static design.technologies.api.business.model.TestConstants.BGN;
import static design.technologies.api.business.model.TestConstants.USD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * Created by triphon 12.11.22 г.
 */
@Getter(AccessLevel.PACKAGE)
@Slf4j
class DocumentProcessorImplTest {

  public static final String WRONG = "WRONG";
  DocumentProcessorImpl processor;

  AllTestUtils allTestUtils;

  @BeforeEach
  void beforeEach() {
    allTestUtils = new AllTestUtils();
    processor =
        spy(
            new DocumentProcessorImpl(
                getAllTestUtils().getMoneyProcessor(), getAllTestUtils().getValidator()));
  }

  @Test
  void isValidTest() {
    doNothing().when(getProcessor()).checkForEmptyInput(any(), any(), any());
    doNothing().when(getProcessor()).validateDocuments(any());
    doNothing().when(getProcessor()).validateCurrencies(any(), any());

    getProcessor().isValid(getAllTestUtils().getValidDocuments(), List.of(), BGN);
  }

  @Test
  void saveTest() {
    final List<DtDocument> validDocuments = getAllTestUtils().getValidDocuments();
    getProcessor().save(validDocuments);
    assertEquals(validDocuments, getProcessor().getDocumentStorage().get());
  }

  @Test
  void extractTest() {
    saveTest();
    doReturn(TestConstants.EXCHANGE_RATE).when(getProcessor()).toExchangeRatesMap(any());
    final List<DtDocument> documents = getProcessor().extract(List.of(), USD, null);
    log.info("documents: {}", documents);
    assertTrue(CollectionUtils.isNotEmpty(documents));
    documents.forEach(dtDocument -> assertEquals(USD, dtDocument.getBalance().getCurrency()));
    assertEquals(3, documents.size());
  }

  @Test
  void extract_ByCustomerVat_OkTest() {
    saveTest();
    final String customerVat = "123456789";
    final List<DtDocument> documents =
        getProcessor().extract(getAllTestUtils().getExchangeRatesRaw(), USD, customerVat);
    log.info("documents: {}", documents);
    assertTrue(CollectionUtils.isNotEmpty(documents));
    documents.forEach(dtDocument -> assertEquals(USD, dtDocument.getBalance().getCurrency()));
    assertEquals(1, documents.size());
    assertEquals(customerVat, documents.get(0).getCustomer().getVat());
  }

  @Test
  void checkForEmptyInputTest() {
    getProcessor()
        .checkForEmptyInput(
            getAllTestUtils().getValidDocuments(), getAllTestUtils().getExchangeRatesRaw(), USD);
  }

  @Test
  void checkForEmptyInput_EmptyDocuments_ThrowTest() {
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class,
            () ->
                getProcessor()
                    .checkForEmptyInput(null, getAllTestUtils().getExchangeRatesRaw(), USD));
    Assertions.assertEquals(BusinessConstants.DOCUMENTS_ARE_MISSING, exception.getMessage());
  }

  @Test
  void checkForEmptyInput_EmptyExchangeRates_ThrowTest() {
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class,
            () ->
                getProcessor()
                    .checkForEmptyInput(getAllTestUtils().getValidDocuments(), null, USD));
    Assertions.assertEquals(BusinessConstants.MISSING_EXCHANGE_RATES, exception.getMessage());
  }

  @Test
  void checkForEmptyInput_MissingOutputCurrency_ThrowTest() {
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class,
            () ->
                getProcessor()
                    .checkForEmptyInput(
                        getAllTestUtils().getValidDocuments(),
                        getAllTestUtils().getExchangeRatesRaw(),
                        StringUtils.EMPTY));
    Assertions.assertEquals(BusinessConstants.MISSING_OUTPUT_CURRENCY, exception.getMessage());
  }

  @Test
  void validateDocumentsTest() {
    final Validator validator = getAllTestUtils().getValidator();
    doReturn(Set.of()).when(validator).validate(any());

    final List<DtDocument> documents = getAllTestUtils().getValidDocuments();
    getProcessor().validateDocuments(documents);
    verify(validator, times(documents.size())).validate(any());
  }

  @Test
  void validateDocuments_NullDocument_ThrowTest() {
    final Validator validator = getAllTestUtils().getValidator();
    doReturn(Set.of()).when(validator).validate(any());

    final List<DtDocument> documents = new ArrayList<>(getAllTestUtils().getValidDocuments());
    documents.add(null);
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class, () -> getProcessor().validateDocuments(documents));
    assertEquals(BusinessConstants.NULL_DOCUMENT, exception.getMessage());
  }

  @Test
  void validateDocuments_DuplicateNumber_ThrowTest() {
    final Validator validator = getAllTestUtils().getValidator();
    doReturn(Set.of()).when(validator).validate(any());

    final List<DtDocument> documents = new ArrayList<>(getAllTestUtils().getValidDocuments());
    final DtDocument document = documents.get(0);
    documents.add(document);
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class, () -> getProcessor().validateDocuments(documents));
    assertTrue(StringUtils.startsWith(exception.getMessage(), BusinessConstants.DUPLICATE_NUMBER));
    assertTrue(StringUtils.endsWith(exception.getMessage(), document.getNumber()));
  }

  @Test
  void validateDocuments_MissingParent_ThrowTest() {
    final Validator validator = getAllTestUtils().getValidator();
    doReturn(Set.of()).when(validator).validate(any());

    final List<DtDocument> documents = new ArrayList<>(getAllTestUtils().getValidDocuments());
    final DtDocument document = documents.get(0);
    document.setParent(DtDocument.builder().number("MISSING").build());

    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class, () -> getProcessor().validateDocuments(documents));
    assertTrue(StringUtils.startsWith(exception.getMessage(), MISSING_DOCUMENT_FOR_PARENT_ID));
    assertTrue(StringUtils.endsWith(exception.getMessage(), document.getParent().getNumber()));
  }

  @Test
  void validateCurrenciesTest() {
    getProcessor()
        .validateCurrencies(
            getAllTestUtils().getValidDocuments(), getAllTestUtils().getExchangeRatesRaw());
  }

  @Test
  void validateCurrencies_DuplicateCurrency_ThrowTest() {
    final List<String> exchangeRatesRaw = new ArrayList<>(getAllTestUtils().getExchangeRatesRaw());
    final String rate = exchangeRatesRaw.get(0);
    exchangeRatesRaw.add(rate);
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class,
            () ->
                getProcessor()
                    .validateCurrencies(getAllTestUtils().getValidDocuments(), exchangeRatesRaw));

    assertTrue(
        StringUtils.startsWith(exception.getMessage(), BusinessConstants.DUPLICATED_CURRENCY));
    assertTrue(StringUtils.endsWith(exception.getMessage(), rate.substring(0, 3)));
  }

  @Test
  void validateCurrencies_MissingDefaultCurrency_ThrowTest() {
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class,
            () ->
                getProcessor()
                    .validateCurrencies(getAllTestUtils().getValidDocuments(), List.of()));

    assertEquals(exception.getMessage(), BusinessConstants.MISSING_DEFAULT_CURRENCY);
  }

  @Test
  void validateCurrencies_MissingCurrency_ThrowTest() {
    final List<DtDocument> documents = getAllTestUtils().getValidDocuments();
    documents.get(0).getBalance().setCurrency(WRONG);
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class,
            () ->
                getProcessor()
                    .validateCurrencies(documents, getAllTestUtils().getExchangeRatesRaw()));

    assertTrue(StringUtils.startsWith(exception.getMessage(), BusinessConstants.MISSING_CURRENCY));
    assertTrue(StringUtils.endsWith(exception.getMessage(), WRONG));
  }

  @Test
  void toExchangeRate_NullInput_ThrowTest() {
    final InvalidInputException exception =
        assertThrows(InvalidInputException.class, () -> DocumentProcessorImpl.toExchangeRate(null));
    assertTrue(
        StringUtils.startsWith(
            exception.getMessage(), BusinessConstants.WRONG_EXCHANGE_RATE_RAW_DATA));
    assertTrue(StringUtils.endsWith(exception.getMessage(), "null"));
  }

  @Test
  void toExchangeRate_WrongInput_ThrowTest() {
    final String exchangeRateRaw = "EUR:EUR:11";
    final InvalidInputException exception =
        assertThrows(
            InvalidInputException.class,
            () -> DocumentProcessorImpl.toExchangeRate(exchangeRateRaw));
    assertTrue(
        StringUtils.startsWith(
            exception.getMessage(), BusinessConstants.WRONG_EXCHANGE_RATE_RAW_DATA));
    assertTrue(StringUtils.endsWith(exception.getMessage(), exchangeRateRaw));
  }
}

