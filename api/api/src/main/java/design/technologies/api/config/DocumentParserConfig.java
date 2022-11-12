package design.technologies.api.config;

import design.technologies.api.core.service.DocumentParser;
import design.technologies.api.csv.service.CsvDocumentParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Created by triphon 12.11.22 г.
 */
@Configuration
public class DocumentParserConfig {

  @Bean
  public DocumentParser documentParser() {
    return new CsvDocumentParser();
  }
}
