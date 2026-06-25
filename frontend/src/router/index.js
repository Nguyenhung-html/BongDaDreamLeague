import { createRouter, createWebHistory } from 'vue-router'

// Auth Views
import DangNhap from '../views/Auth/DangNhap.vue'
import DangKy from '../views/Auth/DangKy.vue'

// Views - User
import TrangChu from '../views/user/TrangChu.vue'
import DanhSachSan from '../views/user/DanhSachSan.vue'
import ChiTietSan from '../views/user/ChiTietSan.vue'
import DatSan from '../views/user/DatSan.vue'
import LichSuDatSan from '../views/user/LichSuDatSan.vue'
import ThongTinCaNhan from '../views/user/ThongTinCaNhan.vue'
import ThongBao from '../views/user/ThongBao.vue'

// Views - Staff
import DashboardStaff from '../views/Staff/DashboardStaff.vue'
import QuanLyDatSanStaff from '../views/staff/QuanLyDatSan.vue'
import QuanLyThanhToanStaff from '../views/staff/QuanLyThanhToan.vue'
import QuanLyLichSan from '../views/staff/QuanLyLichSan.vue'
import XemHoaDonStaff from '../views/staff/XemHoaDon.vue'
import HoTroKhachHang from '../views/staff/HoTroKhachHang.vue'
import ThongTinCaNhanStaff from '../views/staff/ThongTinCaNhan.vue'

// Views - Admin
import DashboardAdmin from '../views/Admin/DashboardAdmin.vue'
import QuanLyNhanVien from '../views/admin/QuanLyNhanVien.vue'
import QuanLySanBong from '../views/admin/QuanLySanBong.vue'
import QuanLyKhachHang from '../views/admin/QuanLyKhachHang.vue'
import QuanLyThongBao from '../views/admin/QuanLyThongBao.vue'
import QuanLyDanhGia from '../views/admin/QuanLyDanhGia.vue'
import ThongKeDoanhThu from '../views/admin/ThongKeDoanhThu.vue'
import QuanLyDatSanAdmin from '../views/Admin/QuanLyDatSan.vue'
import QuanLyThanhToanAdmin from '../views/admin/QuanLyThanhToan.vue'
import XemHoaDonAdmin from '../views/admin/XemHoaDon.vue'
import ThongTinCaNhanAdmin from '../views/admin/ThongTinCaNhan.vue'
import QuanLyBinhLuan from '../views/admin/QuanLyBinhLuan.vue'

const routes = [
  // ---- Auth ----
  { path: '/dang-nhap', name: 'DangNhap', component: DangNhap },
  { path: '/dang-ky', name: 'DangKy', component: DangKy },

  // ---- User ----
  { path: '/', name: 'TrangChu', component: TrangChu, meta: { layout: 'user' } },
  { path: '/san', name: 'DanhSachSan', component: DanhSachSan, meta: { layout: 'user' } },
  { path: '/san/:id', name: 'ChiTietSan', component: ChiTietSan, meta: { layout: 'user' } },
  { path: '/dat-san/:id?', name: 'DatSan', component: DatSan, meta: { layout: 'user' } },
  { path: '/lich-su-dat-san', name: 'LichSuDatSan', component: LichSuDatSan, meta: { layout: 'user' } },
  { path: '/thong-tin-ca-nhan', name: 'ThongTinCaNhanUser', component: ThongTinCaNhan, meta: { layout: 'user' } },
  { path: '/thong-bao', name: 'ThongBao', component: ThongBao, meta: { layout: 'user' } },

  // ---- Staff ----
  { path: '/staff', name: 'DashboardStaff', component: DashboardStaff, meta: { layout: 'staff' } },
  { path: '/staff/dat-san', name: 'QuanLyDatSanStaff', component: QuanLyDatSanStaff, meta: { layout: 'staff' } },
  { path: '/staff/thanh-toan', name: 'QuanLyThanhToanStaff', component: QuanLyThanhToanStaff, meta: { layout: 'staff' } },
  { path: '/staff/lich-san', name: 'QuanLyLichSan', component: QuanLyLichSan, meta: { layout: 'staff' } },
  { path: '/staff/hoa-don', name: 'XemHoaDonStaff', component: XemHoaDonStaff, meta: { layout: 'staff' } },
  { path: '/staff/ho-tro', name: 'HoTroKhachHang', component: HoTroKhachHang, meta: { layout: 'staff' } },
  { path: '/staff/ca-nhan', name: 'ThongTinCaNhanStaff', component: ThongTinCaNhanStaff, meta: { layout: 'staff' } },

  // ---- Admin ----
  { path: '/admin', name: 'DashboardAdmin', component: DashboardAdmin, meta: { layout: 'admin' } },
  { path: '/admin/khach-hang', name: 'QuanLyKhachHang', component: QuanLyKhachHang, meta: { layout: 'admin' } },
  { path: '/admin/nhan-vien', name: 'QuanLyNhanVien', component: QuanLyNhanVien, meta: { layout: 'admin' } },
  { path: '/admin/san-bong', name: 'QuanLySanBong', component: QuanLySanBong, meta: { layout: 'admin' } },
  { path: '/admin/dat-san', name: 'QuanLyDatSanAdmin', component: QuanLyDatSanAdmin, meta: { layout: 'admin' } },
  { path: '/admin/thanh-toan', name: 'QuanLyThanhToanAdmin', component: QuanLyThanhToanAdmin, meta: { layout: 'admin' } },
  { path: '/admin/hoa-don', name: 'XemHoaDonAdmin', component: XemHoaDonAdmin, meta: { layout: 'admin' } },
  { path: '/admin/doanh-thu', name: 'ThongKeDoanhThu', component: ThongKeDoanhThu, meta: { layout: 'admin' } },
  { path: '/admin/thong-bao', name: 'QuanLyThongBao', component: QuanLyThongBao, meta: { layout: 'admin' } },
  { path: '/admin/danh-gia', name: 'QuanLyDanhGia', component: QuanLyDanhGia, meta: { layout: 'admin' } },
  { path: '/admin/binh-luan', name: 'QuanLyBinhLuan', component: QuanLyBinhLuan, meta: { layout: 'admin' } },
  { path: '/admin/ca-nhan', name: 'ThongTinCaNhanAdmin', component: ThongTinCaNhanAdmin, meta: { layout: 'admin' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router