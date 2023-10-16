package org.example.singleton.jaehyeon;

import java.math.BigDecimal;

public class Bank {
    protected String accountNumber;
    protected String ownerName;
    protected BigDecimal balance;

    private static String nextAccountNumber = "0";

    public Bank(String accountNumber, String ownerName, BigDecimal balance) {
        this.accountNumber = CreateAccount.getCreateAccount().createAccountNumber();
        this.ownerName = ownerName;
        this.balance = balance;
    }
}
