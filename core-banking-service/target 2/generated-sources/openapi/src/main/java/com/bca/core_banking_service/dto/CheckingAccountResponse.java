package com.bca.core_banking_service.dto;

import java.net.URI;
import java.util.Objects;
import com.bca.core_banking_service.dto.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckingAccountResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public class CheckingAccountResponse implements AccountPolymorphicResponse {

  private String id;

  private String customerId;

  private AccountType type;

  private String currency;

  private Float balance;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    ACTIVE("ACTIVE"),
    
    INACTIVE("INACTIVE");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  private Integer maxMonthlyTransactions;

  private BigDecimal maintenanceCommission;

  public CheckingAccountResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CheckingAccountResponse(String id, String customerId, AccountType type, String currency, Float balance, StatusEnum status) {
    this.id = id;
    this.customerId = customerId;
    this.type = type;
    this.currency = currency;
    this.balance = balance;
    this.status = status;
  }

  public CheckingAccountResponse id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CheckingAccountResponse customerId(String customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
  */
  @NotNull 
  @Schema(name = "customerId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("customerId")
  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public CheckingAccountResponse type(AccountType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @NotNull @Valid 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public AccountType getType() {
    return type;
  }

  public void setType(AccountType type) {
    this.type = type;
  }

  public CheckingAccountResponse currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
  */
  @NotNull 
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public CheckingAccountResponse balance(Float balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
   * @return balance
  */
  @NotNull 
  @Schema(name = "balance", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("balance")
  public Float getBalance() {
    return balance;
  }

  public void setBalance(Float balance) {
    this.balance = balance;
  }

  public CheckingAccountResponse status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @NotNull 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public CheckingAccountResponse maxMonthlyTransactions(Integer maxMonthlyTransactions) {
    this.maxMonthlyTransactions = maxMonthlyTransactions;
    return this;
  }

  /**
   * Get maxMonthlyTransactions
   * @return maxMonthlyTransactions
  */
  
  @Schema(name = "maxMonthlyTransactions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxMonthlyTransactions")
  public Integer getMaxMonthlyTransactions() {
    return maxMonthlyTransactions;
  }

  public void setMaxMonthlyTransactions(Integer maxMonthlyTransactions) {
    this.maxMonthlyTransactions = maxMonthlyTransactions;
  }

  public CheckingAccountResponse maintenanceCommission(BigDecimal maintenanceCommission) {
    this.maintenanceCommission = maintenanceCommission;
    return this;
  }

  /**
   * Get maintenanceCommission
   * @return maintenanceCommission
  */
  @Valid 
  @Schema(name = "maintenanceCommission", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maintenanceCommission")
  public BigDecimal getMaintenanceCommission() {
    return maintenanceCommission;
  }

  public void setMaintenanceCommission(BigDecimal maintenanceCommission) {
    this.maintenanceCommission = maintenanceCommission;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckingAccountResponse checkingAccountResponse = (CheckingAccountResponse) o;
    return Objects.equals(this.id, checkingAccountResponse.id) &&
        Objects.equals(this.customerId, checkingAccountResponse.customerId) &&
        Objects.equals(this.type, checkingAccountResponse.type) &&
        Objects.equals(this.currency, checkingAccountResponse.currency) &&
        Objects.equals(this.balance, checkingAccountResponse.balance) &&
        Objects.equals(this.status, checkingAccountResponse.status) &&
        Objects.equals(this.maxMonthlyTransactions, checkingAccountResponse.maxMonthlyTransactions) &&
        Objects.equals(this.maintenanceCommission, checkingAccountResponse.maintenanceCommission);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, customerId, type, currency, balance, status, maxMonthlyTransactions, maintenanceCommission);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckingAccountResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    maxMonthlyTransactions: ").append(toIndentedString(maxMonthlyTransactions)).append("\n");
    sb.append("    maintenanceCommission: ").append(toIndentedString(maintenanceCommission)).append("\n");
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

