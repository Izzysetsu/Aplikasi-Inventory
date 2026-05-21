package com.inventory.model;

public class Category {
    
    // 1. Variabel 
    private int categoryId;
    private String name;
    private String type; // Diisi: "ASET" atau "ATK"

    // 2. Constructor Kosong 
    public Category() {}

    // 3. Constructor Lengkap
    public Category(int categoryId, String name, String type) {
        this.categoryId = categoryId;
        this.name = name;
        this.type = type;
    }

    // 4. Getter dan Setter 
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    // 5. Override toString
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}