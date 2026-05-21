package com.inventory.model;

public class RoomBooking {
    private int bookingId;
    private int roomId;
    private String roomName;
    private String startTime;
    private String endTime;
    private String picName;
    private String division;
    private String position;
    private int participants;
    private String purpose;
    private String status;
    private String createdAt;

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getPicName() { return picName; }
    public void setPicName(String picName) { this.picName = picName; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getParticipants() { return participants; }
    public void setParticipants(int participants) { this.participants = participants; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
