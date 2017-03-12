package com.creation.tiagocv.cashregister;

public class DatabaseClasses {
}

class Shop {
    private Object Statistics;
    private String creator;
    private String title;
    private Object items;
    private Object registers;
    private Object transactions;
    private String currency;

    public Shop() {
    }

    public Shop(Object Statistics, String creator, String title, Object items, Object registers, Object transactions, String currency) {
        this.Statistics = Statistics;
        this.creator = creator;
        this.title = title;
        this.items = items;
        this.registers = registers;
        this.transactions = transactions;
        this.currency = currency;
    }

    public Object getStatistics() {
        return Statistics;
    }

    public String getCreator() {
        return creator;
    }

    public String getTitle() {
        return title;
    }

    public Object getItems() {
        return items;
    }

    public Object getRegisters() {
        return registers;
    }

    public Object getTransactions() {
        return transactions;
    }

    public String getCurrency() {
        return currency;
    }
}

class Register {
    private int ranking;
    private String registerName;
    private int salesNumber;
    private int salesProfit;
    private int salesValue;

    public Register() {
    }

    public Register(int ranking, String registerName, int salesNumber, int salesProfit, int salesValue) {
        this.ranking = ranking;
        this.registerName = registerName;
        this.salesNumber = salesNumber;
        this.salesProfit = salesProfit;
        this.salesValue = salesValue;
    }

    public int getRanking() {
        return ranking;
    }

    public String getRegisterName() {
        return registerName;
    }

    public int getSalesNumber() {
        return salesNumber;
    }

    public int getSalesProfit() {
        return salesProfit;
    }

    public int getSalesValue() {
        return salesValue;
    }
}
