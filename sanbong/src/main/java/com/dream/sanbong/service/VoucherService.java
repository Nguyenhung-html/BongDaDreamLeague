package com.dream.sanbong.service;
import com.dream.sanbong.dto.*; import java.util.*;
public interface VoucherService {
    List<VoucherPhanHoi> layTatCa();
    List<VoucherThongBaoPhanHoi> layVoucherDangHoatDong(); VoucherPhanHoi them(VoucherYeuCau y); VoucherPhanHoi sua(UUID id, VoucherYeuCau y); void xoa(UUID id);
    VoucherKiemTraPhanHoi kiemTra(String ma, java.math.BigDecimal donHang, String email);
    void apDungChoDatSan(String ma, java.math.BigDecimal tongTien, String email, com.dream.sanbong.entity.DatSan datSan);
    void hoanVoucher(UUID datSanId);
}
