package com.dream.sanbong.service;

import com.dream.sanbong.dto.DashboardTongQuanDTO;
import com.dream.sanbong.dto.DoanhThuChiTietDTO;

public interface ThongKeService {

    /** Dashboard: cards tổng quan + 2 line charts (12 tháng) */
    DashboardTongQuanDTO layTongQuanDashboard(int nam);

    /** Doanh thu chi tiết: cards + line chart ngày + bảng sân */
    DoanhThuChiTietDTO layDoanhThuChiTiet(int nam, Integer thang);
}
