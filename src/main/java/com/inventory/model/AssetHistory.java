package com.inventory.model;

public class AssetHistory {
    private String transDate;
    private String barcodeCode;
    private String assetName;
    private String transType;
    private int quantity;
    private String location;
    private String purpose;

    public String getTransDate() { return transDate; }
    public void setTransDate(String transDate) { this.transDate = transDate; }

    public String getBarcodeCode() { return barcodeCode; }
    public void setBarcodeCode(String barcodeCode) { this.barcodeCode = barcodeCode; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getTransType() { return transType; }
    public void setTransType(String transType) { this.transType = transType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
