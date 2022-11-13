package design.technologies.api.core.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/*
 * Created by triphon 12.11.22 г.
 */
@Data
@Builder
public class DtCustomer {
  @NotBlank String name;
  @NotBlank String vat;
}
