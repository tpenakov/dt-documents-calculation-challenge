package design.technologies.api.business.service.impl;

import design.technologies.api.business.utils.AllTestUtils;
import design.technologies.api.core.service.DocumentProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * Created by triphon 12.11.22 г.
 */
@Getter(AccessLevel.PACKAGE)
@Slf4j
class DocumentServiceImplTest {

  DocumentServiceImpl service;
  AllTestUtils allTestUtils;
  DocumentProcessor documentProcessor;

  @BeforeEach
  void beforeEach() {
    allTestUtils = new AllTestUtils();
    documentProcessor = spy(DocumentProcessor.class);
    service = new DocumentServiceImpl(getDocumentProcessor());
  }

  @Test
  void processTest() {
    doNothing().when(getDocumentProcessor()).isValid(any(), any(), any());
    doNothing().when(getDocumentProcessor()).save(any());
    doReturn(List.of()).when(getDocumentProcessor()).extract(any(), any(), any());

    assertNotNull(getService().process(List.of(), List.of(), "", null));
    verify(getDocumentProcessor(), times(1)).isValid(any(), any(), any());
    verify(getDocumentProcessor(), times(1)).save(any());
    verify(getDocumentProcessor(), times(1)).extract(any(), any(), any());
  }
}
