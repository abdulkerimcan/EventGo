package com.furkankerim.eventgo.Models;

public class Ticket {
    private String student,fullfare,drink,drinkCount,snack,snackCount,product;
    private CategoryItem item;

    public Ticket(String student, String fullfare, String drink, String drinkCount, String snack, String snackCount, String product,CategoryItem item) {

        this.student = student;
        this.fullfare = fullfare;
        this.drink = drink;
        this.drinkCount = drinkCount;
        this.snack = snack;
        this.snackCount = snackCount;
        this.product = product;
        this.item = item;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getFullfare() {
        return fullfare;
    }

    public void setFullfare(String fullfare) {
        this.fullfare = fullfare;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public String getDrinkCount() {
        return drinkCount;
    }

    public void setDrinkCount(String drinkCount) {
        this.drinkCount = drinkCount;
    }

    public String getSnack() {
        return snack;
    }

    public void setSnack(String snack) {
        this.snack = snack;
    }

    public String getSnackCount() {
        return snackCount;
    }

    public void setSnackCount(String snackCount) {
        this.snackCount = snackCount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public CategoryItem getItem() {
        return item;
    }

    public void setItem(CategoryItem item) {
        this.item = item;
    }
}
