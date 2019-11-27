package com.example.var.Final;

import com.example.var.Bank;

public class Banker {

    public Banker(){

    }

    public void save(Account account, int savingMoney){
        account.money = account.money + savingMoney;
    }

    public void withdraw(Account account, int money){
        account.money = account.money - money;
    }
}
