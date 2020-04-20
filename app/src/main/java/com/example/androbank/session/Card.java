package com.example.androbank.session;

public class Card {
    private String cardNumber = null;
    private Integer accountId = null;
    private String areaLimit = null;
    private float paymentLimit = 0;
    private float withdrawLimit = 0;

    public Card(String cardNumber, Integer accountId, String areaLimit, float paymentLimit, float withdrawLimit) {
        this.cardNumber = cardNumber;
        this.accountId = accountId;
        this.areaLimit = areaLimit;
        this.paymentLimit = paymentLimit;
        this.withdrawLimit = withdrawLimit;
    }

    public String getCardNumber () { return cardNumber;}

    public Integer getAccountId() { return accountId;}

    public String getAreaLimit() { return areaLimit;}

    public float getPaymentLimit() { return paymentLimit;}

    public float getWithdrawLimit() { return withdrawLimit;}

    public void makePayment(float amount, String toAccount) {

    }

    public void withdrawMoney(float amount) {

    }
}
