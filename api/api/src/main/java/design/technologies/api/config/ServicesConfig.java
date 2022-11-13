package design.technologies.api.config;

import design.technologies.api.business.service.impl.DocumentProcessorImpl;
import design.technologies.api.business.service.impl.DocumentServiceImpl;
import design.technologies.api.business.service.impl.MoneyProcessorImpl;
import design.technologies.api.core.service.DocumentParser;
import design.technologies.api.core.service.DocumentService;
import design.technologies.api.csv.service.CsvDocumentParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;

/*
 * Created by triphon 12.11.22 г.
 */
@Configuration
public class ServicesConfig {

  @Bean
  public DocumentParser documentParser() {
    return new CsvDocumentParser();
  }

  @Bean
  public DocumentService documentService(final Validator validator) {
    return new DocumentServiceImpl(new DocumentProcessorImpl(new MoneyProcessorImpl(), validator));
  }
}
