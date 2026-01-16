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
 * FixedTermAccountResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public class FixedTermAccountResponse implements AccountPolymorphicResponse {

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

  private Integer allowedDay;

  private BigDecimal interestRate;

  private Boolean maintenanceFeeFree;

  private Integer allowedMovementDay;

  private Integer movementsThisMonth;

  public FixedTermAccountResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FixedTermAccountResponse(String id, String customerId, AccountType type, String currency, Float balance, StatusEnum status) {
    this.id = id;
    this.customerId = customerId;
    this.type = type;
    this.currency = currency;
    this.balance = balance;
    this.status = status;
  }

  public FixedTermAccountResponse id(String id) {
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

  public FixedTermAccountResponse customerId(String customerId) {
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

  public FixedTermAccountResponse type(AccountType type) {
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

  public FixedTermAccountResponse currency(String currency) {
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

  public FixedTermAccountResponse balance(Float balance) {
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

  public FixedTermAccountResponse status(StatusEnum status) {
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

  public FixedTermAccountResponse allowedDay(Integer allowedDay) {
    this.allowedDay = allowedDay;
    return this;
  }

  /**
   * Get allowedDay
   * @return allowedDay
  */
  
  @Schema(name = "allowedDay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowedDay")
  public Integer getAllowedDay() {
    return allowedDay;
  }

  public void setAllowedDay(Integer allowedDay) {
    this.allowedDay = allowedDay;
  }

  public FixedTermAccountResponse interestRate(BigDecimal interestRate) {
    this.interestRate = interestRate;
    return this;
  }

  /**
   * Get interestRate
   * @return interestRate
  */
  @Valid 
  @Schema(name = "interestRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("interestRate")
  public BigDecimal getInterestRate() {
    return interestRate;
  }

  public void setInterestRate(BigDecimal interestRate) {
    this.interestRate = interestRate;
  }

  public FixedTermAccountResponse maintenanceFeeFree(Boolean maintenanceFeeFree) {
    this.maintenanceFeeFree = maintenanceFeeFree;
    return this;
  }

  /**
   * Get maintenanceFeeFree
   * @return maintenanceFeeFree
  */
  
  @Schema(name = "maintenanceFeeFree", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maintenanceFeeFree")
  public Boolean getMaintenanceFeeFree() {
    return maintenanceFeeFree;
  }

  public void setMaintenanceFeeFree(Boolean maintenanceFeeFree) {
    this.maintenanceFeeFree = maintenanceFeeFree;
  }

  public FixedTermAccountResponse allowedMovementDay(Integer allowedMovementDay) {
    this.allowedMovementDay = allowedMovementDay;
    return this;
  }

  /**
   * Get allowedMovementDay
   * @return allowedMovementDay
  */
  
  @Schema(name = "allowedMovementDay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowedMovementDay")
  public Integer getAllowedMovementDay() {
    return allowedMovementDay;
  }

  public void setAllowedMovementDay(Integer allowedMovementDay) {
    this.allowedMovementDay = allowedMovementDay;
  }

  public FixedTermAccountResponse movementsThisMonth(Integer movementsThisMonth) {
    this.movementsThisMonth = movementsThisMonth;
    return this;
  }

  /**
   * Get movementsThisMonth
   * @return movementsThisMonth
  */
  
  @Schema(name = "movementsThisMonth", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("movementsThisMonth")
  public Integer getMovementsThisMonth() {
    return movementsThisMonth;
  }

  public void setMovementsThisMonth(Integer movementsThisMonth) {
    this.movementsThisMonth = movementsThisMonth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FixedTermAccountResponse fixedTermAccountResponse = (FixedTermAccountResponse) o;
    return Objects.equals(this.id, fixedTermAccountResponse.id) &&
        Objects.equals(this.customerId, fixedTermAccountResponse.customerId) &&
        Objects.equals(this.type, fixedTermAccountResponse.type) &&
        Objects.equals(this.currency, fixedTermAccountResponse.currency) &&
        Objects.equals(this.balance, fixedTermAccountResponse.balance) &&
        Objects.equals(this.status, fixedTermAccountResponse.status) &&
        Objects.equals(this.allowedDay, fixedTermAccountResponse.allowedDay) &&
        Objects.equals(this.interestRate, fixedTermAccountResponse.interestRate) &&
        Objects.equals(this.maintenanceFeeFree, fixedTermAccountResponse.maintenanceFeeFree) &&
        Objects.equals(this.allowedMovementDay, fixedTermAccountResponse.allowedMovementDay) &&
        Objects.equals(this.movementsThisMonth, fixedTermAccountResponse.movementsThisMonth);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, customerId, type, currency, balance, status, allowedDay, interestRate, maintenanceFeeFree, allowedMovementDay, movementsThisMonth);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FixedTermAccountResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    allowedDay: ").append(toIndentedString(allowedDay)).append("\n");
    sb.append("    interestRate: ").append(toIndentedString(interestRate)).append("\n");
    sb.append("    maintenanceFeeFree: ").append(toIndentedString(maintenanceFeeFree)).append("\n");
    sb.append("    allowedMovementDay: ").append(toIndentedString(allowedMovementDay)).append("\n");
    sb.append("    movementsThisMonth: ").append(toIndentedString(movementsThisMonth)).append("\n");
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

