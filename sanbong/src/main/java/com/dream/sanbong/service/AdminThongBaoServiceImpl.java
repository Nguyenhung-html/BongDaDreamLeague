package com.dream.sanbong.service;

import com.dream.sanbong.dto.CapNhatThongBaoQuanTriYeuCau;
import com.dream.sanbong.dto.TaoThongBaoQuanTriYeuCau;
import com.dream.sanbong.dto.ThongBaoQuanTriPhanHoi;
import com.dream.sanbong.entity.*;
import com.dream.sanbong.repository.NguoiDungRepository;
import com.dream.sanbong.repository.ThongBaoQuanTriRepository;
import com.dream.sanbong.repository.ThongBaoRepository;
import com.dream.sanbong.service.AdminThongBaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
public class AdminThongBaoServiceImpl implements AdminThongBaoService {

    private final ThongBaoQuanTriRepository repo;
    private final NguoiDungRepository nguoiDungRepo;
    private final ThongBaoRepository thongBaoRepo;

    public AdminThongBaoServiceImpl(ThongBaoQuanTriRepository repo,
                                     NguoiDungRepository nguoiDungRepo,
                                     ThongBaoRepository thongBaoRepo) {
        this.repo = repo;
        this.nguoiDungRepo = nguoiDungRepo;
        this.thongBaoRepo = thongBaoRepo;
    }

    @Override
    public List<ThongBaoQuanTriPhanHoi> layDanhSach(DoiTuongNhan loc) {
        List<ThongBaoQuanTri> danhSach = repo.findAllByOrderByNgayTaoDesc();

        if (loc != null) {
            danhSach = danhSach.stream()
                    .filter(tb -> tb.getDoiTuongNhan() == loc || tb.getDoiTuongNhan() == DoiTuongNhan.TAT_CA)
                    .toList();
        }

        return danhSach.stream().map(this::sangDto).toList();
    }

    @Override
    public ThongBaoQuanTriPhanHoi layChiTiet(UUID id) {
        return sangDto(timHoacLoi(id));
    }

    @Override
    @Transactional
    public ThongBaoQuanTriPhanHoi them(TaoThongBaoQuanTriYeuCau yeuCau) {
        if (yeuCau.getTieuDe() == null || yeuCau.getTieuDe().isBlank()) {
            throw new RuntimeException("Lỗi: Tiêu đề không được để trống!");
        }
        if (yeuCau.getDoiTuongNhan() == null) {
            throw new RuntimeException("Lỗi: Vui lòng chọn đối tượng nhận!");
        }
        kiemTraKhoangNgay(yeuCau.getNgayBatDau(), yeuCau.getNgayKetThuc());

        ThongBaoQuanTri tb = ThongBaoQuanTri.builder()
                .tieuDe(yeuCau.getTieuDe())
                .noiDung(yeuCau.getNoiDung())
                .doiTuongNhan(yeuCau.getDoiTuongNhan())
                .ngayBatDau(yeuCau.getNgayBatDau())
                .ngayKetThuc(yeuCau.getNgayKetThuc())
                .hienThi(yeuCau.isHienThi())
                .build();

        tb = repo.save(tb);

        // Tạo mới và bật hiển thị ngay -> gửi thẳng tới chuông thông báo của người nhận
        if (tb.isHienThi()) {
            guiThongBaoChoDoiTuong(tb);
        }

        return sangDto(tb);
    }

    @Override
    @Transactional
    public ThongBaoQuanTriPhanHoi capNhat(UUID id, CapNhatThongBaoQuanTriYeuCau yeuCau) {
        ThongBaoQuanTri tb = timHoacLoi(id);

        if (yeuCau.getTieuDe() != null && !yeuCau.getTieuDe().isBlank()) {
            tb.setTieuDe(yeuCau.getTieuDe());
        }
        if (yeuCau.getNoiDung() != null) {
            tb.setNoiDung(yeuCau.getNoiDung());
        }
        if (yeuCau.getDoiTuongNhan() != null) {
            tb.setDoiTuongNhan(yeuCau.getDoiTuongNhan());
        }
        if (yeuCau.getNgayBatDau() != null) {
            tb.setNgayBatDau(yeuCau.getNgayBatDau());
        }
        if (yeuCau.getNgayKetThuc() != null) {
            tb.setNgayKetThuc(yeuCau.getNgayKetThuc());
        }

        kiemTraKhoangNgay(tb.getNgayBatDau(), tb.getNgayKetThuc());

        // Lưu ý: sửa nội dung sau khi đã gửi KHÔNG gửi lại / không sửa các thông báo
        // đã nằm sẵn trong chuông của người dùng (giữ nguyên lịch sử họ đã thấy).
        return sangDto(repo.save(tb));
    }

    @Override
    @Transactional
    public void xoa(UUID id) {
        repo.delete(timHoacLoi(id));
    }

    @Override
    @Transactional
    public ThongBaoQuanTriPhanHoi doiTrangThaiHienThi(UUID id, boolean hienThi) {
        ThongBaoQuanTri tb = timHoacLoi(id);

        if (tb.isHienThi() == hienThi) {
            throw new RuntimeException(hienThi
                    ? "Lỗi: Thông báo này đang hiển thị rồi!"
                    : "Lỗi: Thông báo này đã bị ẩn trước đó rồi!");
        }

        tb.setHienThi(hienThi);
        tb = repo.save(tb);

        // Chuyển từ Ẩn -> Hiện: coi như "xuất bản", gửi tới chuông người nhận lúc này
        if (hienThi) {
            guiThongBaoChoDoiTuong(tb);
        }

        return sangDto(tb);
    }

    // ── Helpers ───────────────────────────────────────────────────

    /**
     * Fan-out: tạo 1 dòng ThongBao (bảng THONG_BAO, có daDoc riêng) cho mỗi
     * NguoiDung thuộc nhóm đối tượng của thông báo quảng bá này, để nó hiện
     * lên chuông "Thông báo của tôi" phía client. Chỉ gửi cho tài khoản đang
     * hoạt động (trangThai = true) — tài khoản bị khoá thì bỏ qua.
     */
    private void guiThongBaoChoDoiTuong(ThongBaoQuanTri tb) {
        List<VaiTro> nhomVaiTro = tb.getDoiTuongNhan() == DoiTuongNhan.TAT_CA
                ? List.of(VaiTro.USER, VaiTro.STAFF)
                : List.of(VaiTro.valueOf(tb.getDoiTuongNhan().name()));

        List<ThongBao> danhSachGui = new ArrayList<>();

        for (VaiTro vaiTro : nhomVaiTro) {
            List<NguoiDung> nguoiNhan = nguoiDungRepo.findByVaiTroOrderByNgayTaoDesc(vaiTro);
            for (NguoiDung nd : nguoiNhan) {
                if (!nd.isTrangThai()) continue; // bỏ qua tài khoản đã khoá

                danhSachGui.add(ThongBao.builder()
                        .nguoiDung(nd)
                        .tieuDe(tb.getTieuDe())
                        .noiDung(tb.getNoiDung())
                        .loai("promotion")
                        .build());
            }
        }

        if (!danhSachGui.isEmpty()) {
            thongBaoRepo.saveAll(danhSachGui);
        }
    }

    private void kiemTraKhoangNgay(LocalDate batDau, LocalDate ketThuc) {
        if (batDau != null && ketThuc != null && ketThuc.isBefore(batDau)) {
            throw new RuntimeException("Lỗi: Ngày kết thúc phải sau ngày bắt đầu!");
        }
    }

    private ThongBaoQuanTri timHoacLoi(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy thông báo!"));
    }

    private ThongBaoQuanTriPhanHoi sangDto(ThongBaoQuanTri tb) {
        return ThongBaoQuanTriPhanHoi.builder()
                .id(tb.getId())
                .tieuDe(tb.getTieuDe())
                .noiDung(tb.getNoiDung())
                .doiTuongNhan(tb.getDoiTuongNhan())
                .ngayBatDau(tb.getNgayBatDau())
                .ngayKetThuc(tb.getNgayKetThuc())
                .hienThi(tb.isHienThi())
                .ngayTao(tb.getNgayTao())
                .build();
    }
}