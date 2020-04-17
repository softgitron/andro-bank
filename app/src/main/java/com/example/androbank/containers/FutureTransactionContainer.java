package com.example.androbank.containers;

import java.util.Date;

public class FutureTransactionContainer extends TransactionContainer {
  public Integer futureTransferId;
  public Integer atInterval; // How often transfer is processed in minutes
  public Integer times;
  public Date atTime;
}
