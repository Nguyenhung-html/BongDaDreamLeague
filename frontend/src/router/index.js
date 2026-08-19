import { createRouter, createWebHistory } from 'vue-router'

// Auth Views
import DangNhap from '../views/Auth/DangNhap.vue'
import DangKy from '../views/Auth/DangKy.vue'
import QuenMatKhau from '../views/Auth/QuenMatKhau.vue'

// Views - User
import TrangChu from '../views/User/TrangChu.vue'
import DanhSachSan from '../views/User/DanhSachSan.vue'
import ChiTietSan from '../views/User/ChiTietSan.vue'
import DatSan from '../views/User/DatSan.vue'
import LichSuDatSan from '../views/User/LichSuDatSan.vue'
import ThongTinCaNhan from '../views/User/ThongTinCaNhan.vue'
import ThongBao from '../views/User/ThongBao.vue'
import HoTro from '../views/user/HoTro.vue'

// Views - Staff
import DashboardStaff from '../views/Staff/DashboardStaff.vue'
import QuanLyDatSanStaff from '../views/Staff/QuanLyDatSan.vue'
import QuanLyThanhToanStaff from '../views/Staff/QuanLyThanhToan.vue'
import QuanLyLichSan from '../views/Staff/QuanLyLichSan.vue'
import XemHoaDonStaff from '../views/Staff/XemHoaDon.vue'
import QuanLyBaiViet from '../views/staff/QuanLyBaiViet.vue'
import PhanHoiTrangChu from '../views/Staff/PhanHoiTrangChu.vue'
import HoTroKhachHang from '../views/Staff/HoTroKhachHang.vue'
import ThongTinCaNhanStaff from '../views/Staff/ThongTinCaNhan.vue'

// Views - Admin
import DashboardAdmin from '../views/Admin/DashboardAdmin.vue'
import QuanLySanPham from '../views/Admin/QuanLySanPham.vue'
import QuanLyNhanVien from '../views/Admin/QuanLyNhanVien.vue'
import QuanLySanBong from '../views/Admin/QuanLySanBong.vue'
import QuanLyKhachHang from '../views/Admin/QuanLyKhachHang.vue'
import QuanLyThongBao from '../views/Admin/QuanLyThongBao.vue'
import QuanLyDanhGia from '../views/Admin/QuanLyDanhGia.vue'
import ThongKeDoanhThu from '../views/Admin/ThongKeDoanhThu.vue'
import QuanLyDatSanAdmin from '../views/Admin/QuanLyDatSan.vue'
import QuanLyThanhToanAdmin from '../views/Admin/QuanLyThanhToan.vue'
import XemHoaDonAdmin from '../views/Admin/XemHoaDon.vue'
import ThongTinCaNhanAdmin from '../views/Admin/ThongTinCaNhan.vue'
import QuanLyDanhGiaHeThong from '../views/Admin/QuanLyDanhGiaHeThong.vue'
import QuanLyBaiVietAdmin from '../views/Admin/QuanLyBaiViet.vue'
import QuanLyVoucher from '../views/Admin/QuanLyVoucher.vue'

const routes = [
  // ---- Auth (Công khai công cộng, ai cũng có thể vào) ----
  { path: '/dang-nhap', name: 'DangNhap', component: DangNhap },
  { path: '/dang-ky', name: 'DangKy', component: DangKy },
  { path: '/quen-mat-khau', name: 'QuenMatKhau', component: QuenMatKhau },

  // ---- User ----
// Các trang công khai (Chưa đăng nhập vẫn xem được)
{ path: '/', name: 'TrangChu', component: TrangChu, meta: { layout: 'user' } },
{ path: '/san', name: 'DanhSachSan', component: DanhSachSan, meta: { layout: 'user' } },
{ path: '/san/:id', name: 'ChiTietSan', component: ChiTietSan, meta: { layout: 'user' } },
{ path: '/ho-tro', name: 'HoTro', component: HoTro, meta: { layout: 'user' } },

// Cho phép cả User, Staff, Admin cùng vào các trang này
{ path: '/dat-san/:id?', name: 'DatSan', component: DatSan, meta: { layout: 'user', requiresAuth: true, roles: ['User', 'Staff', 'Admin'] } },
{ path: '/lich-su-dat-san', name: 'LichSuDatSan', component: LichSuDatSan, meta: { layout: 'user', requiresAuth: true, roles: ['User', 'Staff', 'Admin'] } },
{ path: '/thong-tin-ca-nhan', name: 'ThongTinCaNhanUser', component: ThongTinCaNhan, meta: { layout: 'user', requiresAuth: true, roles: ['User', 'Staff', 'Admin'] } },
{ path: '/thong-bao', name: 'ThongBao', component: ThongBao, meta: { layout: 'user' } },

  // ---- Staff (Yêu cầu đăng nhập và có quyền 'Staff') ----
  { path: '/staff', name: 'DashboardStaff', component: DashboardStaff, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/dat-san', name: 'QuanLyDatSanStaff', component: QuanLyDatSanStaff, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/thanh-toan', name: 'QuanLyThanhToanStaff', component: QuanLyThanhToanStaff, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/lich-san', name: 'QuanLyLichSan', component: QuanLyLichSan, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/hoa-don', name: 'XemHoaDonStaff', component: XemHoaDonStaff, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/bai-viet', name: 'StaffBaiViet', component: QuanLyBaiViet, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/ho-tro', name: 'HoTroKhachHang', component: HoTroKhachHang, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/ca-nhan', name: 'ThongTinCaNhanStaff', component: ThongTinCaNhanStaff, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff'] } },
  { path: '/staff/phan-hoi-trang-chu', name: 'StaffPhanHoiTrangChu', component: PhanHoiTrangChu, meta: { layout: 'staff', requiresAuth: true, roles: ['Staff']}},

  // ---- Admin (Yêu cầu đăng nhập và có quyền 'Admin') ----
  { path: '/admin', name: 'DashboardAdmin', component: DashboardAdmin, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/khach-hang', name: 'QuanLyKhachHang', component: QuanLyKhachHang, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/nhan-vien', name: 'QuanLyNhanVien', component: QuanLyNhanVien, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/san-bong', name: 'QuanLySanBong', component: QuanLySanBong, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/dat-san', name: 'QuanLyDatSanAdmin', component: QuanLyDatSanAdmin, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/thanh-toan', name: 'QuanLyThanhToanAdmin', component: QuanLyThanhToanAdmin, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/hoa-don', name: 'XemHoaDonAdmin', component: XemHoaDonAdmin, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/doanh-thu', name: 'ThongKeDoanhThu', component: ThongKeDoanhThu, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/thong-bao', name: 'QuanLyThongBao', component: QuanLyThongBao, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/danh-gia', name: 'QuanLyDanhGia', component: QuanLyDanhGia, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/danh-gia-he-thong', name: 'QuanLyDanhGiaHeThong', component: QuanLyDanhGiaHeThong, meta: {layout: 'admin', requiresAuth: true, roles: ['Admin']}},
  { path: '/admin/san-pham', name: 'QuanLySanPham', component: QuanLySanPham, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/ca-nhan', name: 'ThongTinCaNhanAdmin', component: ThongTinCaNhanAdmin, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },
  { path: '/admin/voucher', name: 'QuanLyVoucher', component: QuanLyVoucher, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'], title: 'Quản lý Voucher' } },
  {path: '/admin/bai-viet', name: 'QuanLyBaiVietAdmin', component: QuanLyBaiVietAdmin, meta: { layout: 'admin', requiresAuth: true, roles: ['Admin'] } },


  
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to) {
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth', top: 90 }
    }
    return { top: 0 }
  }
})

// ==== BỘ LỌC CHẶN LINK THÔNG MINH (NAVIGATION GUARD) ====
router.beforeEach((to, from, next) => {
  // 1. Kiểm tra trạng thái đăng nhập từ localStorage
  const isAuthenticated = localStorage.getItem('isLoggedIn') === 'true'
  const userRole = localStorage.getItem('userRole') // Nhận các giá trị: 'Admin', 'Staff', hoặc 'User'

  // 2. Nếu tuyến đường (Route) chuẩn bị vào yêu cầu phải có quyền tài khoản
  if (to.matched.some(record => record.meta.requiresAuth)) {
    
    // TRƯỜNG HỢP A: Người dùng chưa đăng nhập gì cả
    if (!isAuthenticated) {
      alert('Vui lòng đăng nhập tài khoản để tiếp tục sử dụng tính năng này!')
      return next({ name: 'DangNhap' }) // Tên này phải khớp chính xác với name: 'DangNhap' ở trên route
    }

    // TRƯỜNG HỢP B: Đã đăng nhập nhưng cố tình vào sai quyền hạn (Ví dụ: User gõ link /admin)
    const allowedRoles = to.meta.roles
    if (allowedRoles && !allowedRoles.includes(userRole)) {
      alert('Tài khoản của bạn không có quyền truy cập vào khu vực này!')
      
      // Định hướng về trang phù hợp với quyền đang giữ để giảm lỗi sập trang
      if (userRole === 'Admin') return next({ name: 'DashboardAdmin' })
      if (userRole === 'Staff') return next({ name: 'DashboardStaff' })
      return next({ name: 'TrangChu' })
    }

    // Nếu thỏa mãn toàn bộ điều kiện hợp lệ -> Cho phép đi vào trang
    next()
  } else {
    // Nếu trang đó là trang công khai (Trang chủ, Xem danh sách sân, Xem chi tiết sân, Đăng nhập, Đăng ký) -> Cho qua luôn
    next()
  }
})

export default router