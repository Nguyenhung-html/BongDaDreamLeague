package com.dream.sanbong.service;

import com.dream.sanbong.dto.DanhGiaRequest;
import com.dream.sanbong.dto.DanhGiaResponse;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public interface DanhGiaService {
    void guiDanhGiaTheoEmail(String Email, DanhGiaRequest request);
    List<DanhGiaResponse> layDanhGiaCuaSan(UUID sanBongId);
    void adminAnBinhLuan(UUID danhGiaId);
    boolean checkQuyenDanhGia(UUID userId, UUID sanBongId);
    List<DanhGiaResponse> adminLayDanhGiaCuaSan(UUID sanBongId);
    void adminXoaBinhLuan(UUID danhGiaId);
}