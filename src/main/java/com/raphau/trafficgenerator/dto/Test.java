package com.raphau.trafficgenerator.dto;

public class Test {
    private int id;
    private String text;

    public Test() {
    }

    public Test(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
