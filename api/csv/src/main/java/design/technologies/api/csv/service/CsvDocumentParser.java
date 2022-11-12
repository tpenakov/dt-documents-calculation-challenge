package design.technologies.api.csv.service;

import design.technologies.api.core.model.DtCustomer;
import design.technologies.api.core.model.DtDocument;
import design.technologies.api.core.model.DtMoney;
import design.technologies.api.core.service.DocumentParser;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Created by triphon 12.11.22 г.
 */
@Getter(AccessLevel.PROTECTED)
@Slf4j
public class CsvDocumentParser implements DocumentParser {

  public static final String DOCUMENT_NUMBER = "Document number";
  public static final String MISSING_DOCUMENT_TYPE = "Missing document type";
  public static final String TYPE = "Type";
  public static final String CUSTOMER = "Customer";
  public static final String VAT_NUMBER = "Vat number";
  public static final String PARENT_DOCUMENT = "Parent document";
  public static final String UNSUPPORTED_DOCUMENT_TYPE = "Unsupported document type: ";
  public static final String CURRENCY = "Currency";
  public static final String TOTAL = "Total";
  private final CSVFormat format;

  public CsvDocumentParser() {
    format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
  }

  @Override
  public List<DtDocument> parse(final String data) {

    if (StringUtils.isBlank(data)) {
      return List.of();
    }

    final Reader reader = new StringReader(data);

    return Try.of(() -> CSVParser.parse(reader, getFormat()))
        .map(
            csvRecords ->
                csvRecords.stream().map(CsvDocumentParser::toDocument).collect(Collectors.toList()))
        .getOrElseThrow(throwable -> new RuntimeException(throwable));
  }

  static DtDocument toDocument(final CSVRecord row) {
    return DtDocument.builder()
        .number(row.get(DOCUMENT_NUMBER))
        .type(toType(row.get(TYPE)))
        .customer(DtCustomer.builder().name(row.get(CUSTOMER)).vat(row.get(VAT_NUMBER)).build())
        .parent(
            Optional.ofNullable(row.get(PARENT_DOCUMENT))
                .filter(StringUtils::isNotBlank)
                .map(s -> DtDocument.builder().number(s).build())
                .orElse(null))
        .balance(
            DtMoney.builder()
                .currency(row.get(CURRENCY))
                .amount(NumberUtils.createBigDecimal(row.get(TOTAL)))
                .build())
        .build();
  }

  static DtDocument.Type toType(final String type) {
    if (StringUtils.isBlank(type)) {
      throw new RuntimeException(MISSING_DOCUMENT_TYPE);
    }

    switch (type) {
      case "1":
        return DtDocument.Type.INVOICE;
      case "2":
        return DtDocument.Type.CREDIT_NOTE;
      case "3":
        return DtDocument.Type.DEBIT_NOTE;
      default:
        throw new RuntimeException(UNSUPPORTED_DOCUMENT_TYPE + type);
    }
  }
}
