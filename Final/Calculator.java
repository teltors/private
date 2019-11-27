package com.example.var.Final;

public class Calculator implements Calculaotr_Interface{

    public Calculator() {
    }

    @Override
    public int sum(int first, int second) {
        return first + second;
    }

    @Override
    public int minus(int first, int second) {
        return first - second;
    }
}
