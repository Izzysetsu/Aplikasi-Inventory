package com.inventory.model;

public class Loan {
    private int id;
    private String picName;
    private String division;
    private int assetId;           
    private String assetDisplay;
    private String location;
    private String loanDate;
    private String returnDate;
    private String purpose;        
    private String status;
    private int quantity;
    private String categoryType;

    // ----- GETTER & SETTER -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getPicName() { return picName; }
    public void setPicName(String picName) { this.picName = picName; }
    
    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }
    
    public int getAssetId() { return assetId; }
    public void setAssetId(int assetId) { this.assetId = assetId; }
    
    public String getAssetDisplay() { return assetDisplay; }
    public void setAssetDisplay(String assetDisplay) { this.assetDisplay = assetDisplay; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getLoanDate() { return loanDate; }
    public void setLoanDate(String loanDate) { this.loanDate = loanDate; }
    
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getCategoryType() { return categoryType; }
    public void setCategoryType(String categoryType) { this.categoryType = categoryType; }
}