package design.technologies.api.core.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
  @NotNull @Valid private DtCustomer customer;

  private DtDocument parent;

  @NotNull @Valid private DtMoney balance;
}
