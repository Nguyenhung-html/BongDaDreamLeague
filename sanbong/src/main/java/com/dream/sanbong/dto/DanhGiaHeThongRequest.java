package com.dream.sanbong.dto;

import java.util.UUID;

public class DanhGiaHeThongRequest {
    private UUID nguoiDungId;
    private String name;
    private Integer rating;
    private String quote;

    // Getters and Setters
    public UUID getNguoiDungId() { return nguoiDungId; }
    public void setNguoiDungId(UUID nguoiDungId) { this.nguoiDungId = nguoiDungId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }
}