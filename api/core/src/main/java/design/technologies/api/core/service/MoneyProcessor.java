package design.technologies.api.core.service;

import design.technologies.api.core.model.DtMoney;

import java.math.BigDecimal;
import java.util.Map;

/*
 * Created by triphon 12.11.22 г.
 */
public interface MoneyProcessor {
  DtMoney convert(
      final DtMoney input, final String outputCurrency, final Map<String, BigDecimal> exchangeRate);

  DtMoney add(final DtMoney input, final DtMoney input1, Map<String, BigDecimal> exchangeRate);

  DtMoney subtract(final DtMoney input, final DtMoney input1, Map<String, BigDecimal> exchangeRate);

  BigDecimal toInternalScale(BigDecimal input);

  BigDecimal toCurrencyScale(BigDecimal input);
}
