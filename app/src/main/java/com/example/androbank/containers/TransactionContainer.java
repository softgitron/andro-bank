package com.example.androbank.containers;

import java.util.Date;

public class TransactionContainer {

  public enum TransactionType {
    Transfer,
    Deposit,
    Withdraw,
    Payment
  }

  public Integer transferId;
  public Integer fromAccountId;
  public String fromAccountIban;
  public String fromAccountBic;
  public Integer toAccountId;
  public String toAccountIban;
  public String toAccountBic;
  public Integer cardId;
  public String cardNumber;
  public Integer amount;
  public String time;
  public TransactionType type;
}
