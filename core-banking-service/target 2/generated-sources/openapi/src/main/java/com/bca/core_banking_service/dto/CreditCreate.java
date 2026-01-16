package com.bca.core_banking_service.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreditCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public class CreditCreate {

  private String customerId;

  /**
   * Gets or Sets creditType
   */
  public enum CreditTypeEnum {
    PERSONAL_LOAN("PERSONAL_LOAN"),
    
    MORTGAGE("MORTGAGE"),
    
    AUTO_LOAN("AUTO_LOAN");

    private String value;

    CreditTypeEnum(String value) {
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
    public static CreditTypeEnum fromValue(String value) {
      for (CreditTypeEnum b : CreditTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private CreditTypeEnum creditType;

  private Float amount;

  private Integer termMonths;

  private Float interestRate;

  public CreditCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreditCreate(String customerId, CreditTypeEnum creditType, Float amount, Integer termMonths, Float interestRate) {
    this.customerId = customerId;
    this.creditType = creditType;
    this.amount = amount;
    this.termMonths = termMonths;
    this.interestRate = interestRate;
  }

  public CreditCreate customerId(String customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
  */
  @NotNull 
  @Schema(name = "customerId", example = "cus-1001", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("customerId")
  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public CreditCreate creditType(CreditTypeEnum creditType) {
    this.creditType = creditType;
    return this;
  }

  /**
   * Get creditType
   * @return creditType
  */
  @NotNull 
  @Schema(name = "creditType", example = "PERSONAL_LOAN", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("creditType")
  public CreditTypeEnum getCreditType() {
    return creditType;
  }

  public void setCreditType(CreditTypeEnum creditType) {
    this.creditType = creditType;
  }

  public CreditCreate amount(Float amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
  */
  @NotNull 
  @Schema(name = "amount", example = "10000.0", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public CreditCreate termMonths(Integer termMonths) {
    this.termMonths = termMonths;
    return this;
  }

  /**
   * Get termMonths
   * @return termMonths
  */
  @NotNull 
  @Schema(name = "termMonths", example = "24", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("termMonths")
  public Integer getTermMonths() {
    return termMonths;
  }

  public void setTermMonths(Integer termMonths) {
    this.termMonths = termMonths;
  }

  public CreditCreate interestRate(Float interestRate) {
    this.interestRate = interestRate;
    return this;
  }

  /**
   * Get interestRate
   * @return interestRate
  */
  @NotNull 
  @Schema(name = "interestRate", example = "18.5", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("interestRate")
  public Float getInterestRate() {
    return interestRate;
  }

  public void setInterestRate(Float interestRate) {
    this.interestRate = interestRate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreditCreate creditCreate = (CreditCreate) o;
    return Objects.equals(this.customerId, creditCreate.customerId) &&
        Objects.equals(this.creditType, creditCreate.creditType) &&
        Objects.equals(this.amount, creditCreate.amount) &&
        Objects.equals(this.termMonths, creditCreate.termMonths) &&
        Objects.equals(this.interestRate, creditCreate.interestRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerId, creditType, amount, termMonths, interestRate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreditCreate {\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    creditType: ").append(toIndentedString(creditType)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    termMonths: ").append(toIndentedString(termMonths)).append("\n");
    sb.append("    interestRate: ").append(toIndentedString(interestRate)).append("\n");
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

