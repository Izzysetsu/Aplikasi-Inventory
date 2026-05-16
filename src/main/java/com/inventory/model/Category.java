/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;


public class Category {
    // 1. Variabel 
    private int id;
    private String name;
    private String status;

    // 2. Constructor Kosong 
    public Category() {}

    // 3. Constructor Lengkap
    public Category(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // 4. Getter dan Setter 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
