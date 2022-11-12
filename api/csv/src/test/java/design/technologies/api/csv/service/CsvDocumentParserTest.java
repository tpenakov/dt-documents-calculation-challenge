package design.technologies.api.csv.service;

import design.technologies.api.core.model.DtDocument;
import design.technologies.api.test.utils.UnitTestUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

/*
 * Created by triphon 12.11.22 г.
 */
@Getter(AccessLevel.PACKAGE)
@Slf4j
class CsvDocumentParserTest {

  public static final String WRONG = "wrong";
  UnitTestUtils unitTestUtils;
  CsvDocumentParser parser;

  @BeforeEach
  void beforeEach() {
    unitTestUtils = UnitTestUtils.of();
    parser = spy(new CsvDocumentParser());
  }

  @Test
  void parseTest() {
    final String data = getUnitTestUtils().readFromTextFile("input/challenge-sample-documents.csv");
    final List<DtDocument> documents = getParser().parse(data);
    assertTrue(CollectionUtils.isNotEmpty(documents));
  }

  @Test
  void parse_BlankInput_OkTest() {
    Assertions.assertTrue(CollectionUtils.isEmpty(getParser().parse(StringUtils.EMPTY)));
  }

  //  @Test
  //  void parse_Throws_Test() {
  //    doThrow(new RuntimeException( WRONG)).when(getParser()).
  //  }

}
