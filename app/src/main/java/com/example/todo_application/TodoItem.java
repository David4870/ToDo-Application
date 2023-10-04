package com.example.todo_application;

public class TodoItem {
    private String itemName;
    private String imagePath;

    public TodoItem(String itemName, String imagePath) {
        this.itemName = itemName;
        this.imagePath = imagePath;
    }

    public String getItemName() {
        return itemName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
