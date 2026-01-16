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
 * CreditPaymentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public class CreditPaymentResponse {

  private String creditId;

  private String paymentId;

  private Double paidAmount;

  private Double remainingDebt;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    FULL_PAYMENT("FULL_PAYMENT"),
    
    PARTIAL_PAYMENT("PARTIAL_PAYMENT");

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

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime paidAt;

  public CreditPaymentResponse creditId(String creditId) {
    this.creditId = creditId;
    return this;
  }

  /**
   * Get creditId
   * @return creditId
  */
  
  @Schema(name = "creditId", example = "cred-9001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creditId")
  public String getCreditId() {
    return creditId;
  }

  public void setCreditId(String creditId) {
    this.creditId = creditId;
  }

  public CreditPaymentResponse paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
  */
  
  @Schema(name = "paymentId", example = "pay-001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public CreditPaymentResponse paidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
    return this;
  }

  /**
   * Get paidAmount
   * @return paidAmount
  */
  
  @Schema(name = "paidAmount", example = "500.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paidAmount")
  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  public CreditPaymentResponse remainingDebt(Double remainingDebt) {
    this.remainingDebt = remainingDebt;
    return this;
  }

  /**
   * Get remainingDebt
   * @return remainingDebt
  */
  
  @Schema(name = "remainingDebt", example = "9500.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("remainingDebt")
  public Double getRemainingDebt() {
    return remainingDebt;
  }

  public void setRemainingDebt(Double remainingDebt) {
    this.remainingDebt = remainingDebt;
  }

  public CreditPaymentResponse status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @Schema(name = "status", example = "PARTIAL_PAYMENT", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public CreditPaymentResponse paidAt(OffsetDateTime paidAt) {
    this.paidAt = paidAt;
    return this;
  }

  /**
   * Get paidAt
   * @return paidAt
  */
  @Valid 
  @Schema(name = "paidAt", example = "2025-01-15T10:30Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paidAt")
  public OffsetDateTime getPaidAt() {
    return paidAt;
  }

  public void setPaidAt(OffsetDateTime paidAt) {
    this.paidAt = paidAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreditPaymentResponse creditPaymentResponse = (CreditPaymentResponse) o;
    return Objects.equals(this.creditId, creditPaymentResponse.creditId) &&
        Objects.equals(this.paymentId, creditPaymentResponse.paymentId) &&
        Objects.equals(this.paidAmount, creditPaymentResponse.paidAmount) &&
        Objects.equals(this.remainingDebt, creditPaymentResponse.remainingDebt) &&
        Objects.equals(this.status, creditPaymentResponse.status) &&
        Objects.equals(this.paidAt, creditPaymentResponse.paidAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(creditId, paymentId, paidAmount, remainingDebt, status, paidAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreditPaymentResponse {\n");
    sb.append("    creditId: ").append(toIndentedString(creditId)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    paidAmount: ").append(toIndentedString(paidAmount)).append("\n");
    sb.append("    remainingDebt: ").append(toIndentedString(remainingDebt)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    paidAt: ").append(toIndentedString(paidAt)).append("\n");
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

