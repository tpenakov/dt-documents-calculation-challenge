package design.technologies.api.business.model;

import java.math.BigDecimal;
import java.util.Map;

/*
 * Created by triphon 12.11.22 г.
 */
public class TestConstants {
  public static final String BGN = "BGN";
  public static final String EUR = "EUR";
  public static final String USD = "USD";
  public static final String GBP = "GBP";
  public static final Map<String, BigDecimal> EXCHANGE_RATE =
      Map.of(
          EUR,
          BigDecimal.ONE,
          BGN,
          BigDecimal.valueOf(1.95583),
          USD,
          BigDecimal.valueOf(0.987),
          GBP,
          BigDecimal.valueOf(0.878));
}
