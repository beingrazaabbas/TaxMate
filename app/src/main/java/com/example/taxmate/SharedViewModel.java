package com.example.taxmate;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private String date;
    private String tax;

    public String getTax() { return tax; }

    public void setTax(String tax){this.tax = tax;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
