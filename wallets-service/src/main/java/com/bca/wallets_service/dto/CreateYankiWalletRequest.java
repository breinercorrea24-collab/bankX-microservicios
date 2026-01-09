package com.bca.wallets_service.dto;

public class CreateYankiWalletRequest {
    private String customerId;
    private String phone;

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}