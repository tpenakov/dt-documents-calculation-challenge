package design.technologies.api.controller;

import design.technologies.api.generated.api.ApiApi;
import design.technologies.api.generated.model.CalculateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("${web.api.base-path:}")
public class ApiController implements ApiApi {

  private final NativeWebRequest request;

  @Autowired
  public ApiController(final NativeWebRequest request) {
    this.request = request;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<CalculateResponse> sumInvoices(
          final MultipartFile file, final List<String> exchangeRates, final String outputCurrency, final String customerVat) {
    return ApiApi.super.sumInvoices(file, exchangeRates, outputCurrency, customerVat);
  }
}
