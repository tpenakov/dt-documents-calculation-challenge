package design.technologies.api.business.service.impl;

import design.technologies.api.core.model.DtMoney;
import design.technologies.api.core.service.MoneyProcessor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.function.BiFunction;

/*
 * Created by triphon 12.11.22 г.
 */
public class MoneyProcessorImpl implements MoneyProcessor {
  public static final int EXCHANGE_RATE_SCALE = 10;
  public static final int CURRENCY_SCALE = 2;

  @Override
  public DtMoney convert(
      final DtMoney input,
      final String outputCurrency,
      final Map<String, BigDecimal> exchangeRate) {
    final String inputCurrency = input.getCurrency();

    if (StringUtils.equals(inputCurrency, outputCurrency)) {
      input.setAmount(toCurrencyScale(input.getAmount()));
      return input;
    }

    final BigDecimal finalAmountInternal =
        toInternalScale(input.getAmount())
            .divide(exchangeRate.get(inputCurrency), EXCHANGE_RATE_SCALE, RoundingMode.HALF_UP)
            .multiply(toInternalScale(exchangeRate.get(outputCurrency)));

    return DtMoney.builder()
        .amount(toCurrencyScale(finalAmountInternal))
        .currency(outputCurrency)
        .build();
  }

  @Override
  public DtMoney add(
      final DtMoney input, final DtMoney input1, final Map<String, BigDecimal> exchangeRate) {
    return action(
        input, input1, exchangeRate, (bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2));
  }

  @Override
  public DtMoney subtract(
      final DtMoney input, final DtMoney input1, final Map<String, BigDecimal> exchangeRate) {
    return action(
        input, input1, exchangeRate, (bigDecimal, bigDecimal2) -> bigDecimal.subtract(bigDecimal2));
  }

  @Override
  public BigDecimal toInternalScale(final BigDecimal input) {
    return input.setScale(EXCHANGE_RATE_SCALE, RoundingMode.CEILING);
  }

  @Override
  public BigDecimal toCurrencyScale(final BigDecimal input) {
    return input.setScale(CURRENCY_SCALE, RoundingMode.CEILING);
  }

  private DtMoney action(
      final DtMoney input,
      final DtMoney input1,
      final Map<String, BigDecimal> exchangeRate,
      final BiFunction<BigDecimal, BigDecimal, BigDecimal> actionFn) {
    final DtMoney converted = convert(input1, input.getCurrency(), exchangeRate);
    return DtMoney.builder()
        .currency(input.getCurrency())
        .amount(actionFn.apply(toCurrencyScale(input.getAmount()), converted.getAmount()))
        .build();
  }
}
