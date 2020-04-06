package com.example.androbank.ui;

public class RecyclerViewObject {
    private String cardText;
    private int imageResource;

    public RecyclerViewObject(int image, String text1) {
        cardText = text1;
        imageResource = image;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getCardText() {
        return cardText;
    }
}
