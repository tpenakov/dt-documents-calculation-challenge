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
public class DtMoney {
  @NotBlank
  @Pattern(regexp = "^([A-Z]){3}$")
  private String currency;

  @NotNull private BigDecimal amount;
}
