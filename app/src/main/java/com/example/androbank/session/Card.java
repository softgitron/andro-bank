package com.example.androbank.session;

public class Card {
    private String cardNumber = null;
    private Integer cardId = null;
    private Integer accountId = null;
    private String areaLimit = null;
    private Integer paymentLimit = 0;
    private Integer withdrawLimit = 0;


    public Card(String cardNumber, Integer cardId, Integer accountId, String areaLimit, Integer paymentLimit, Integer withdrawLimit) {
        this.cardNumber = cardNumber;
        this.cardId = cardId;
        this.accountId = accountId;
        this.areaLimit = areaLimit;
        this.paymentLimit = paymentLimit;
        this.withdrawLimit = withdrawLimit;
    }

    public String getCardNumber () { return cardNumber;}

    public Integer getAccountId() { return accountId;}

    public String getAreaLimit() { return areaLimit;}

    public Integer getPaymentLimit() { return paymentLimit;}

    public Integer getWithdrawLimit() { return withdrawLimit;}

    public Integer getCardId() {return cardId;}

    public void setAreaLimit(String areaLimit) {
        this.areaLimit = areaLimit;
    }

    public void setPaymentLimit(int paymentLimit) {
        this.paymentLimit = paymentLimit;
    }

    public void setWithdrawLimit(int withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }
}
