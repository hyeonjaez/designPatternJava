package org.example.singleton.gaeun.bank;
//bank 응용 프로그램 리패토링

import java.math.BigDecimal;

public class CreateAccount {
    /*
    * 계좌번호를 생성하는 싱클턴으로 리팩토링
     */
    // Write private, static CreateAccount here
    private static CreateAccount createAccount;

    private static String nextAccountNumber = "0";

    private CreateAccount() {}
    public static CreateAccount getCreateAccount() {
        if(createAccount == null) {
            createAccount = new CreateAccount();
        }
        return createAccount;
    }

    //
    // Write public getCreateAccount method here
    //

    public static String createAccountNumber() {
        int accountNumber = Integer.parseInt(nextAccountNumber);
        nextAccountNumber = Integer.toString(++accountNumber);
        return "0000-" + nextAccountNumber;
    }


}
