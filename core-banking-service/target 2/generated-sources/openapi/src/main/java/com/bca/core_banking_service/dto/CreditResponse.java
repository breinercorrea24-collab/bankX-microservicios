package com.bca.core_banking_service.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreditResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public class CreditResponse {

  private String id;

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

  private Double originalAmount;

  private Double pendingDebt;

  private Double interestRate;

  private Integer termMonths;

  private String status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public CreditResponse id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "cred-9001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CreditResponse customerId(String customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
  */
  
  @Schema(name = "customerId", example = "cus-1001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerId")
  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public CreditResponse creditType(CreditTypeEnum creditType) {
    this.creditType = creditType;
    return this;
  }

  /**
   * Get creditType
   * @return creditType
  */
  
  @Schema(name = "creditType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creditType")
  public CreditTypeEnum getCreditType() {
    return creditType;
  }

  public void setCreditType(CreditTypeEnum creditType) {
    this.creditType = creditType;
  }

  public CreditResponse originalAmount(Double originalAmount) {
    this.originalAmount = originalAmount;
    return this;
  }

  /**
   * Get originalAmount
   * @return originalAmount
  */
  
  @Schema(name = "originalAmount", example = "10000.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalAmount")
  public Double getOriginalAmount() {
    return originalAmount;
  }

  public void setOriginalAmount(Double originalAmount) {
    this.originalAmount = originalAmount;
  }

  public CreditResponse pendingDebt(Double pendingDebt) {
    this.pendingDebt = pendingDebt;
    return this;
  }

  /**
   * Get pendingDebt
   * @return pendingDebt
  */
  
  @Schema(name = "pendingDebt", example = "10000.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pendingDebt")
  public Double getPendingDebt() {
    return pendingDebt;
  }

  public void setPendingDebt(Double pendingDebt) {
    this.pendingDebt = pendingDebt;
  }

  public CreditResponse interestRate(Double interestRate) {
    this.interestRate = interestRate;
    return this;
  }

  /**
   * Get interestRate
   * @return interestRate
  */
  
  @Schema(name = "interestRate", example = "18.5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("interestRate")
  public Double getInterestRate() {
    return interestRate;
  }

  public void setInterestRate(Double interestRate) {
    this.interestRate = interestRate;
  }

  public CreditResponse termMonths(Integer termMonths) {
    this.termMonths = termMonths;
    return this;
  }

  /**
   * Get termMonths
   * @return termMonths
  */
  
  @Schema(name = "termMonths", example = "24", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("termMonths")
  public Integer getTermMonths() {
    return termMonths;
  }

  public void setTermMonths(Integer termMonths) {
    this.termMonths = termMonths;
  }

  public CreditResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @Schema(name = "status", example = "ACTIVE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public CreditResponse createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  */
  @Valid 
  @Schema(name = "createdAt", example = "2025-01-01T11:00Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreditResponse creditResponse = (CreditResponse) o;
    return Objects.equals(this.id, creditResponse.id) &&
        Objects.equals(this.customerId, creditResponse.customerId) &&
        Objects.equals(this.creditType, creditResponse.creditType) &&
        Objects.equals(this.originalAmount, creditResponse.originalAmount) &&
        Objects.equals(this.pendingDebt, creditResponse.pendingDebt) &&
        Objects.equals(this.interestRate, creditResponse.interestRate) &&
        Objects.equals(this.termMonths, creditResponse.termMonths) &&
        Objects.equals(this.status, creditResponse.status) &&
        Objects.equals(this.createdAt, creditResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, customerId, creditType, originalAmount, pendingDebt, interestRate, termMonths, status, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreditResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    creditType: ").append(toIndentedString(creditType)).append("\n");
    sb.append("    originalAmount: ").append(toIndentedString(originalAmount)).append("\n");
    sb.append("    pendingDebt: ").append(toIndentedString(pendingDebt)).append("\n");
    sb.append("    interestRate: ").append(toIndentedString(interestRate)).append("\n");
    sb.append("    termMonths: ").append(toIndentedString(termMonths)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

