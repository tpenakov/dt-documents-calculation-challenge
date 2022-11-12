package design.technologies.api.business.service.impl;

import design.technologies.api.core.model.DtDocument;
import design.technologies.api.core.service.DocumentProcessor;
import design.technologies.api.core.service.DocumentService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/*
 * Created by triphon 12.11.22 г.
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@Slf4j
public class DocumentServiceImpl implements DocumentService {

  private final DocumentProcessor documentProcessor;

  @Override
  public List<DtDocument> process(
      final List<DtDocument> documents,
      final List<String> exchangeRates,
      final String outputCurrency,
      final String customerVat) {
    getDocumentProcessor().isValid(documents, exchangeRates, outputCurrency);
    getDocumentProcessor().save(documents);
    return getDocumentProcessor().extract(exchangeRates, outputCurrency, customerVat);
  }
}
