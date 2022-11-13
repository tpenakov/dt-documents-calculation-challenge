package design.technologies.api.business.utils;

import design.technologies.api.business.model.BusinessConstants;
import design.technologies.api.business.service.impl.MoneyProcessorImpl;
import design.technologies.api.core.model.DtDocument;
import design.technologies.api.core.service.DocumentParser;
import design.technologies.api.core.service.MoneyProcessor;
import design.technologies.api.csv.service.CsvDocumentParser;
import design.technologies.api.test.model.TestConstants;
import design.technologies.api.test.utils.UnitTestUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;

import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Created by triphon 12.11.22 г.
 */
@Getter
@Slf4j
public class AllTestUtils {

  private final UnitTestUtils unitTestUtils;
  private final DocumentParser documentParser;
  private final MoneyProcessor moneyProcessor;
  private final Validator validator;

  public AllTestUtils() {
    unitTestUtils = UnitTestUtils.of();
    documentParser = new CsvDocumentParser();
    moneyProcessor = new MoneyProcessorImpl();
    validator = Mockito.spy(Validator.class);
  }

  public List<DtDocument> getValidDocuments() {
    return getDocumentParser().parse(getUnitTestUtils().getValidCsvFile());
  }

  public List<String> getExchangeRatesRaw() {
    return TestConstants.EXCHANGE_RATE.entrySet().stream()
        .map(
            entry ->
                entry.getKey()
                    + BusinessConstants.EXCHANGE_RATE_SEPARATOR
                    + entry.getValue().toPlainString())
        .collect(Collectors.toList());
  }
}
