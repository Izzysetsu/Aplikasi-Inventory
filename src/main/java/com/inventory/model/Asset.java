package com.inventory.model;

public class Asset {
    
    private int id;
    private String barcodeCode;
    private String name;
    private int categoryId;
    private String categoryName; 
    private int supplierId;
    private String supplierName; 
    private String specification;
    private String status;
    private int quantity;
    private String unit;
    private String categoryType;
    private String createdAt;

    public Asset() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBarcodeCode() { return barcodeCode; }
    public void setBarcodeCode(String barcodeCode) { this.barcodeCode = barcodeCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCategoryType() { return categoryType; }
    public void setCategoryType(String categoryType) { this.categoryType = categoryType; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}