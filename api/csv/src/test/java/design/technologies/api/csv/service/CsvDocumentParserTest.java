package design.technologies.api.csv.service;

import design.technologies.api.core.model.DtDocument;
import design.technologies.api.test.utils.UnitTestUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
  MockedStatic<CsvDocumentParser> mockStatic;

  @BeforeEach
  void beforeEach() {
    unitTestUtils = UnitTestUtils.of();
    parser = spy(new CsvDocumentParser());
    mockStatic = null;
  }

  @AfterEach
  void afterEach() {
    if (Objects.nonNull(getMockStatic())) {
      getMockStatic().close();
    }
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

  @Test
  void parse_Throws_Test() {
    mockStatic = Mockito.mockStatic(CsvDocumentParser.class);
    getMockStatic()
        .when(() -> CsvDocumentParser.toDocument(any()))
        .thenThrow(new RuntimeException(WRONG));
    final String data = getUnitTestUtils().readFromTextFile("input/challenge-sample-documents.csv");
    final RuntimeException exception =
        assertThrows(RuntimeException.class, () -> getParser().parse(data));
    assertEquals(WRONG, exception.getCause().getMessage());
  }

  @Test
  void toType_MissingType_ThrowTest() {
    final RuntimeException exception =
        assertThrows(RuntimeException.class, () -> CsvDocumentParser.toType(null));
    assertEquals(CsvDocumentParser.MISSING_DOCUMENT_TYPE, exception.getMessage());
  }

  @Test
  void toType_UnsupportedType_ThrowTest() {
    final RuntimeException exception =
        assertThrows(RuntimeException.class, () -> CsvDocumentParser.toType("unsupported"));
    assertTrue(
        StringUtils.startsWith(
            exception.getMessage(), CsvDocumentParser.UNSUPPORTED_DOCUMENT_TYPE));
  }
}
