package design.technologies.api.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import design.technologies.api.generated.model.Customer;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CalculateResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-12T01:08:28.986611Z[Etc/UTC]")
public class CalculateResponse {

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("customers")
  @Valid
  private List<Customer> customers = null;

  public CalculateResponse currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
  */
  @Pattern(regexp = "^([A-Z]){3}$") 
  @Schema(name = "currency", required = false)
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public CalculateResponse customers(List<Customer> customers) {
    this.customers = customers;
    return this;
  }

  public CalculateResponse addCustomersItem(Customer customersItem) {
    if (this.customers == null) {
      this.customers = new ArrayList<>();
    }
    this.customers.add(customersItem);
    return this;
  }

  /**
   * Get customers
   * @return customers
  */
  @Valid 
  @Schema(name = "customers", required = false)
  public List<Customer> getCustomers() {
    return customers;
  }

  public void setCustomers(List<Customer> customers) {
    this.customers = customers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CalculateResponse calculateResponse = (CalculateResponse) o;
    return Objects.equals(this.currency, calculateResponse.currency) &&
        Objects.equals(this.customers, calculateResponse.customers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, customers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CalculateResponse {\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    customers: ").append(toIndentedString(customers)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

