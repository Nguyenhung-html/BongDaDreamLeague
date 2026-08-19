package com.dream.sanbong.service;

import com.dream.sanbong.dto.DanhGiaHeThongRequest;
import com.dream.sanbong.dto.DanhGiaHeThongResponse;
import com.dream.sanbong.dto.PhanHoiRequest;
import com.dream.sanbong.entity.DanhGiaHeThong;

import java.util.List;
import java.util.UUID;

public interface DanhGiaHeThongService {
    List<DanhGiaHeThongResponse> getAllDanhGiaHienThi();
    List<DanhGiaHeThongResponse> getAllDanhGiaChoQuanLy();
    
    // Thêm phương thức này vào Interface
    DanhGiaHeThong createDanhGia(DanhGiaHeThongRequest request, String email);
    
    DanhGiaHeThong createDanhGia(DanhGiaHeThongRequest request);
    DanhGiaHeThongResponse phanHoiDanhGia(UUID id, PhanHoiRequest request);
    DanhGiaHeThong thayDoiTrangThaiHienThi(UUID id, boolean hienThi);
    void xoaDanhGia(UUID id);
}