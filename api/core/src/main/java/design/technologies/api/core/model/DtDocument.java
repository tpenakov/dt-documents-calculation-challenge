package design.technologies.api.core.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/*
 * Created by triphon 12.11.22 г.
 */
@Data
@Builder
public class DtDocument {

  public enum Type {
    INVOICE,
    CREDIT_NOTE,
    DEBIT_NOTE
  }

  @NotBlank private String number;
  @NotNull private Type type;
  @NotBlank private DtCustomer customer;

  private DtDocument parent;

  @NotBlank
  @Pattern(regexp = "^([A-Z]){3}$")
  private String currencyCode;

  @NotNull private BigDecimal amount;
}
