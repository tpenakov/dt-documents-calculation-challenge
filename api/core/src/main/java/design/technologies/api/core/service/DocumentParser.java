package design.technologies.api.core.service;

import design.technologies.api.core.model.DtDocument;

import java.util.List;

/*
 * Created by triphon 12.11.22 г.
 */
@SuppressWarnings("unused")
public interface DocumentParser {
  List<DtDocument> parse(String data);
}
