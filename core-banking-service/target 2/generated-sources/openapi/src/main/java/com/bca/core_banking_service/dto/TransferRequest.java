package com.bca.core_banking_service.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TransferRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public class TransferRequest {

  private String fromId;

  private String toId;

  private Float amount;

  public TransferRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TransferRequest(String fromId, String toId, Float amount) {
    this.fromId = fromId;
    this.toId = toId;
    this.amount = amount;
  }

  public TransferRequest fromId(String fromId) {
    this.fromId = fromId;
    return this;
  }

  /**
   * Get fromId
   * @return fromId
  */
  @NotNull 
  @Schema(name = "fromId", example = "acc-2001", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fromId")
  public String getFromId() {
    return fromId;
  }

  public void setFromId(String fromId) {
    this.fromId = fromId;
  }

  public TransferRequest toId(String toId) {
    this.toId = toId;
    return this;
  }

  /**
   * Get toId
   * @return toId
  */
  @NotNull 
  @Schema(name = "toId", example = "acc-2002", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("toId")
  public String getToId() {
    return toId;
  }

  public void setToId(String toId) {
    this.toId = toId;
  }

  public TransferRequest amount(Float amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
  */
  @NotNull 
  @Schema(name = "amount", example = "250.0", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransferRequest transferRequest = (TransferRequest) o;
    return Objects.equals(this.fromId, transferRequest.fromId) &&
        Objects.equals(this.toId, transferRequest.toId) &&
        Objects.equals(this.amount, transferRequest.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromId, toId, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransferRequest {\n");
    sb.append("    fromId: ").append(toIndentedString(fromId)).append("\n");
    sb.append("    toId: ").append(toIndentedString(toId)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

