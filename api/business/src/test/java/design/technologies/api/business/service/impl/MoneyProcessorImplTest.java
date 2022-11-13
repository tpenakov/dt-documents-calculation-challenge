package design.technologies.api.business.service.impl;

import design.technologies.api.core.model.DtMoney;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static design.technologies.api.test.model.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Created by triphon 12.11.22 г.
 */
@Getter(AccessLevel.PACKAGE)
@Slf4j
class MoneyProcessorImplTest {

  MoneyProcessorImpl processor;

  @BeforeEach
  void beforeEach() {
    processor = new MoneyProcessorImpl();
  }

  @Test
  void convertTest() {
    final DtMoney input =
        DtMoney.builder().amount(BigDecimal.TEN.multiply(BigDecimal.TEN)).currency(EUR).build();
    log.info("input: {}", input);

    final DtMoney output = getProcessor().convert(input, BGN, EXCHANGE_RATE);
    log.info("EUR to BGN: {}", output);

    final DtMoney output1 = getProcessor().convert(input, EUR, EXCHANGE_RATE);
    log.info("BGN to EUR: {}", output1);

    assertEquals(output1.getAmount(), getProcessor().toCurrencyScale(input.getAmount()));
    assertEquals(output1.getCurrency(), input.getCurrency());
  }

  @Test
  void addTest() {
    final DtMoney input =
        DtMoney.builder().amount(BigDecimal.TEN.multiply(BigDecimal.TEN)).currency(EUR).build();
    log.info("input: {}", input);
    final DtMoney input1 =
        DtMoney.builder().amount(BigDecimal.TEN.multiply(BigDecimal.TEN)).currency(BGN).build();
    log.info("input: {}", input1);

    final DtMoney output = getProcessor().add(input, input1, EXCHANGE_RATE);
    log.info("output: {}", output);

    assertEquals(BigDecimal.valueOf(151.13), output.getAmount());
  }

  @Test
  void subtractTest() {
    final DtMoney input =
        DtMoney.builder().amount(BigDecimal.TEN.multiply(BigDecimal.TEN)).currency(EUR).build();
    log.info("input: {}", input);
    final DtMoney input1 =
        DtMoney.builder().amount(BigDecimal.TEN.multiply(BigDecimal.TEN)).currency(BGN).build();
    log.info("input: {}", input1);

    final DtMoney output = getProcessor().subtract(input, input1, EXCHANGE_RATE);
    log.info("output: {}", output);

    assertEquals(BigDecimal.valueOf(48.87), output.getAmount());
  }
}
