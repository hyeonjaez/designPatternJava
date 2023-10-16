package org.example.singleton.gaeun.bank;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        BankAccount account = new BankAccount("Jason", new BigDecimal(100));
        account.printAccount();
        BankAccount account2 = new BankAccount("James", new BigDecimal(1000));
        account2.printAccount();
    }
}
