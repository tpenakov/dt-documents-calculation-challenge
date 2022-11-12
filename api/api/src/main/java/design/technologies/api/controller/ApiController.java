package design.technologies.api.controller;

import design.technologies.api.core.model.DtDocument;
import design.technologies.api.core.service.DocumentParser;
import design.technologies.api.core.service.DocumentService;
import design.technologies.api.generated.api.ApiApi;
import design.technologies.api.generated.model.CalculateResponse;
import design.technologies.api.generated.model.Customer;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
@RequestMapping("${web.api.base-path:}")
@Slf4j
public class ApiController implements ApiApi {

  private final NativeWebRequest request;
  private final DocumentParser documentParser;
  private final DocumentService documentService;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<CalculateResponse> sumInvoices(
      final MultipartFile file,
      final List<String> exchangeRates,
      final String outputCurrency,
      final String customerVat) {
    return Try.of(file::getBytes)
        .map(String::new)
        .map(documentParser::parse)
        .map(
            documents ->
                getDocumentService().process(documents, exchangeRates, outputCurrency, customerVat))
        .map(ApiController::toResponseEntity)
        .getOrElseGet(ApiController::toBadRequestResponseEntity);
  }

  static ResponseEntity<CalculateResponse> toBadRequestResponseEntity(final Throwable throwable) {
    log.error("unable to process request", throwable);
    return ResponseEntity.badRequest().build();
  }

  static ResponseEntity<CalculateResponse> toResponseEntity(final List<DtDocument> documents) {
    if (CollectionUtils.isEmpty(documents)) {
      return ResponseEntity.notFound().build();
    }

    final CalculateResponse calculateResponse = new CalculateResponse();
    calculateResponse.currency(
        documents.stream().findAny().map(DtDocument::getCurrencyCode).orElse(null));
    calculateResponse.customers(
        documents.stream()
            .map(
                dtDocument -> {
                  final Customer customer = new Customer();
                  customer.setBalance(dtDocument.getAmount());
                  customer.setName(dtDocument.getCustomer().getName());
                  return customer;
                })
            .collect(Collectors.toList()));
    return ResponseEntity.ok(calculateResponse);
  }
}
