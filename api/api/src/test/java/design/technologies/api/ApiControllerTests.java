package design.technologies.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import design.technologies.api.api.utils.AllTestUtils;
import design.technologies.api.controller.ApiController;
import design.technologies.api.generated.model.CalculateResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;

import static design.technologies.api.test.model.TestConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/*
 * Created by triphon 13.11.22 г.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApiController.class)
@Getter(AccessLevel.PACKAGE)
@Slf4j
public class ApiControllerTests {
  public static final String FILE_NAME = "challenge-sample-documents.csv";
  public static final String URL_TEMPLATE = "/api/v1/sumInvoices";
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired ApiController apiController;

  AllTestUtils allTestUtils;
  String csvData;
  File destinationFile;

  @BeforeEach
  void beforeEach() {
    allTestUtils = new AllTestUtils();
    csvData = getAllTestUtils().getUnitTestUtils().getValidCsvFile();
    destinationFile = new File(FileUtils.getTempDirectory(), FILE_NAME);
    FileUtils.deleteQuietly(destinationFile);
  }

  @AfterEach
  void afterEach() {
    FileUtils.deleteQuietly(destinationFile);
  }

  @Test
  void sumInvoicesTest() throws Exception {

    final MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", FILE_NAME, "text/csv", csvData.getBytes());

    final ResultMatcher ok = MockMvcResultMatchers.status().isOk();
    final ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

    log.info("will send invalid request");
    mockMvc
        .perform(
            multipart(URL_TEMPLATE)
                .file(mockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(badRequest)
        .andDo(MockMvcResultHandlers.print());
    FileUtils.deleteQuietly(destinationFile);

    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("exchangeRates", getAllTestUtils().getExchangeRatesRaw());
    params.add("outputCurrency", USD);
    params.add("customerVat", "987654321");
    log.info("will send valid request");
    final byte[] responseBody =
        mockMvc
            .perform(
                multipart(URL_TEMPLATE)
                    .file(mockMultipartFile)
                    .queryParams(params)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(ok)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
    FileUtils.deleteQuietly(destinationFile);

    final CalculateResponse calculateResponse =
        getObjectMapper().readValue(responseBody, CalculateResponse.class);
    assertEquals(USD, calculateResponse.getCurrency());
    assertEquals(1, calculateResponse.getCustomers().size());
    assertEquals("Vendor 2", calculateResponse.getCustomers().get(0).getName());
    assertEquals(
        NumberUtils.createBigDecimal("688.31"),
        calculateResponse.getCustomers().get(0).getBalance());
  }

  @Test
  void sumInvoices_WrongVat_AndReturnNotFoundTest() throws Exception {

    final MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", FILE_NAME, "text/csv", csvData.getBytes());

    final ResultMatcher notFound = MockMvcResultMatchers.status().isNotFound();

    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("exchangeRates", getAllTestUtils().getExchangeRatesRaw());
    params.add("outputCurrency", USD);
    params.add("customerVat", "MISSING");
    log.info("will send valid request");
    mockMvc
        .perform(
            multipart(URL_TEMPLATE)
                .file(mockMultipartFile)
                .queryParams(params)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(notFound)
        .andDo(MockMvcResultHandlers.print());
    FileUtils.deleteQuietly(destinationFile);
  }

  @Test
  void sumInvoices_CsvFileWithMissingValues_AndReturnBadRequestTest() throws Exception {

    final String data = StringUtils.replace(getCsvData(), "987654321", StringUtils.EMPTY);
    final MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", FILE_NAME, "text/csv", data.getBytes());

    final ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("exchangeRates", getAllTestUtils().getExchangeRatesRaw());
    params.add("outputCurrency", USD);
    params.add("customerVat", "987654321");
    log.info("will send invalid request");
    mockMvc
        .perform(
            multipart(URL_TEMPLATE)
                .file(mockMultipartFile)
                .queryParams(params)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(badRequest)
        .andDo(MockMvcResultHandlers.print());
  }

  @Test
  void initTest() {
    assertTrue(getApiController().getRequest().isPresent());
  }
}
