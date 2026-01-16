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
 * VipSavingsAccountResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public class VipSavingsAccountResponse implements AccountPolymorphicResponse {

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

  private Integer currentTransactions;

  private BigDecimal maintenanceCommission;

  private BigDecimal minimumDailyAverage;

  public VipSavingsAccountResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public VipSavingsAccountResponse(String id, String customerId, AccountType type, String currency, Float balance, StatusEnum status, BigDecimal minimumDailyAverage) {
    this.id = id;
    this.customerId = customerId;
    this.type = type;
    this.currency = currency;
    this.balance = balance;
    this.status = status;
    this.minimumDailyAverage = minimumDailyAverage;
  }

  public VipSavingsAccountResponse id(String id) {
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

  public VipSavingsAccountResponse customerId(String customerId) {
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

  public VipSavingsAccountResponse type(AccountType type) {
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

  public VipSavingsAccountResponse currency(String currency) {
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

  public VipSavingsAccountResponse balance(Float balance) {
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

  public VipSavingsAccountResponse status(StatusEnum status) {
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

  public VipSavingsAccountResponse maxMonthlyTransactions(Integer maxMonthlyTransactions) {
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

  public VipSavingsAccountResponse currentTransactions(Integer currentTransactions) {
    this.currentTransactions = currentTransactions;
    return this;
  }

  /**
   * Get currentTransactions
   * @return currentTransactions
  */
  
  @Schema(name = "currentTransactions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currentTransactions")
  public Integer getCurrentTransactions() {
    return currentTransactions;
  }

  public void setCurrentTransactions(Integer currentTransactions) {
    this.currentTransactions = currentTransactions;
  }

  public VipSavingsAccountResponse maintenanceCommission(BigDecimal maintenanceCommission) {
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

  public VipSavingsAccountResponse minimumDailyAverage(BigDecimal minimumDailyAverage) {
    this.minimumDailyAverage = minimumDailyAverage;
    return this;
  }

  /**
   * Get minimumDailyAverage
   * @return minimumDailyAverage
  */
  @NotNull @Valid 
  @Schema(name = "minimumDailyAverage", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("minimumDailyAverage")
  public BigDecimal getMinimumDailyAverage() {
    return minimumDailyAverage;
  }

  public void setMinimumDailyAverage(BigDecimal minimumDailyAverage) {
    this.minimumDailyAverage = minimumDailyAverage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VipSavingsAccountResponse vipSavingsAccountResponse = (VipSavingsAccountResponse) o;
    return Objects.equals(this.id, vipSavingsAccountResponse.id) &&
        Objects.equals(this.customerId, vipSavingsAccountResponse.customerId) &&
        Objects.equals(this.type, vipSavingsAccountResponse.type) &&
        Objects.equals(this.currency, vipSavingsAccountResponse.currency) &&
        Objects.equals(this.balance, vipSavingsAccountResponse.balance) &&
        Objects.equals(this.status, vipSavingsAccountResponse.status) &&
        Objects.equals(this.maxMonthlyTransactions, vipSavingsAccountResponse.maxMonthlyTransactions) &&
        Objects.equals(this.currentTransactions, vipSavingsAccountResponse.currentTransactions) &&
        Objects.equals(this.maintenanceCommission, vipSavingsAccountResponse.maintenanceCommission) &&
        Objects.equals(this.minimumDailyAverage, vipSavingsAccountResponse.minimumDailyAverage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, customerId, type, currency, balance, status, maxMonthlyTransactions, currentTransactions, maintenanceCommission, minimumDailyAverage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VipSavingsAccountResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    maxMonthlyTransactions: ").append(toIndentedString(maxMonthlyTransactions)).append("\n");
    sb.append("    currentTransactions: ").append(toIndentedString(currentTransactions)).append("\n");
    sb.append("    maintenanceCommission: ").append(toIndentedString(maintenanceCommission)).append("\n");
    sb.append("    minimumDailyAverage: ").append(toIndentedString(minimumDailyAverage)).append("\n");
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

