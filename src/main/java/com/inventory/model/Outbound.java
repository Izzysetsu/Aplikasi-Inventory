package com.inventory.model;

public class Outbound {
    private int outboundId;
    private int assetId;
    private String assetName;
    private String barcodeCode;
    private int locationId;
    private String locationName;
    private int quantity;
    private String outboundDate;
    private String purpose;

    public int getOutboundId() { return outboundId; }
    public void setOutboundId(int outboundId) { this.outboundId = outboundId; }

    public int getAssetId() { return assetId; }
    public void setAssetId(int assetId) { this.assetId = assetId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getBarcodeCode() { return barcodeCode; }
    public void setBarcodeCode(String barcodeCode) { this.barcodeCode = barcodeCode; }

    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getOutboundDate() { return outboundDate; }
    public void setOutboundDate(String outboundDate) { this.outboundDate = outboundDate; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
