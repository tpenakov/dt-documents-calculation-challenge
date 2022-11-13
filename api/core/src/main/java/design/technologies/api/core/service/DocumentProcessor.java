package design.technologies.api.core.service;

import design.technologies.api.core.model.DtDocument;

import java.util.List;

/*
 * Created by triphon 12.11.22 г.
 */
@SuppressWarnings("unused")
public interface DocumentProcessor {
  void isValid(
      final List<DtDocument> documents,
      final List<String> exchangeRates,
      final String outputCurrency);

  void save(final List<DtDocument> documents);

  List<DtDocument> extract(
      final List<String> exchangeRates, final String outputCurrency, final String customerVat);
}
