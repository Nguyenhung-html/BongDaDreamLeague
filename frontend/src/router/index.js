import { createRouter, createWebHistory } from 'vue-router'

// =====================================================
// AUTH
// =====================================================
import DangNhap from '../views/Auth/DangNhap.vue'
import DangKy from '../views/Auth/DangKy.vue'
import QuenMatKhau from '../views/Auth/QuenMatKhau.vue'

// =====================================================
// USER
// =====================================================
import TrangChu from '../views/User/TrangChu.vue'
import DanhSachSan from '../views/User/DanhSachSan.vue'
import ChiTietSan from '../views/User/ChiTietSan.vue'
import DatSan from '../views/User/DatSan.vue'
import LichSuDatSan from '../views/User/LichSuDatSan.vue'
import ThongTinCaNhan from '../views/User/ThongTinCaNhan.vue'
import ThongBao from '../views/User/ThongBao.vue'
import HoTro from '../views/User/HoTro.vue'

// =====================================================
// STAFF
// =====================================================
import DashboardStaff from '../views/Staff/DashboardStaff.vue'
import QuanLyDatSanStaff from '../views/Staff/QuanLyDatSan.vue'
import QuanLyThanhToanStaff from '../views/Staff/QuanLyThanhToan.vue'
import QuanLyLichSan from '../views/Staff/QuanLyLichSan.vue'
import XemHoaDonStaff from '../views/Staff/XemHoaDon.vue'
import QuanLyBaiVietStaff from '../views/Staff/QuanLyBaiViet.vue'
import PhanHoiTrangChu from '../views/Staff/PhanHoiTrangChu.vue'
import ThongTinCaNhanStaff from '../views/Staff/ThongTinCaNhan.vue'
import QuanLySupportStaff from '../views/Staff/QuanLySupport.vue'

// =====================================================
// ADMIN
// =====================================================
import DashboardAdmin from '../views/Admin/DashboardAdmin.vue'
import QuanLySanPham from '../views/Admin/QuanLySanPham.vue'
import QuanLyNhanVien from '../views/Admin/QuanLyNhanVien.vue'
import QuanLySanBong from '../views/Admin/QuanLySanBong.vue'
import QuanLyKhachHang from '../views/Admin/QuanLyKhachHang.vue'
import QuanLyThongBao from '../views/Admin/QuanLyThongBao.vue'
import QuanLyDanhGia from '../views/Admin/QuanLyDanhGia.vue'
import ThongKeDoanhThu from '../views/Admin/ThongKeDoanhThu.vue'
import ThongTinCaNhanAdmin from '../views/Admin/ThongTinCaNhan.vue'
import QuanLyDanhGiaHeThong from '../views/Admin/QuanLyDanhGiaHeThong.vue'
import QuanLyBaiVietAdmin from '../views/Admin/QuanLyBaiViet.vue'
import QuanLyVoucher from '../views/Admin/QuanLyVoucher.vue'

// =====================================================
// ROUTES
// =====================================================
const routes = [

  // ===================================================
  // AUTH
  // ===================================================
  {
    path: '/dang-nhap',
    name: 'DangNhap',
    component: DangNhap
  },
  {
    path: '/dang-ky',
    name: 'DangKy',
    component: DangKy
  },
  {
    path: '/quen-mat-khau',
    name: 'QuenMatKhau',
    component: QuenMatKhau
  },

  // ===================================================
  // USER - CÔNG KHAI
  // ===================================================
  {
    path: '/',
    name: 'TrangChu',
    component: TrangChu,
    meta: {
      layout: 'user'
    }
  },
  {
    path: '/san',
    name: 'DanhSachSan',
    component: DanhSachSan,
    meta: {
      layout: 'user'
    }
  },
  {
    path: '/san/:id',
    name: 'ChiTietSan',
    component: ChiTietSan,
    meta: {
      layout: 'user'
    }
  },
  {
    path: '/ho-tro',
    name: 'HoTro',
    component: HoTro,
    meta: {
      layout: 'user'
    }
  },

  // ===================================================
  // USER - YÊU CẦU ĐĂNG NHẬP
  // User / Staff / Admin đều được phép sử dụng
  // ===================================================
  {
    path: '/dat-san/:id?',
    name: 'DatSan',
    component: DatSan,
    meta: {
      layout: 'user',
      requiresAuth: true,
      roles: ['User', 'Staff', 'Admin']
    }
  },
  {
    path: '/lich-su-dat-san',
    name: 'LichSuDatSan',
    component: LichSuDatSan,
    meta: {
      layout: 'user',
      requiresAuth: true,
      roles: ['User', 'Staff', 'Admin']
    }
  },
  {
    path: '/thong-tin-ca-nhan',
    name: 'ThongTinCaNhanUser',
    component: ThongTinCaNhan,
    meta: {
      layout: 'user',
      requiresAuth: true,
      roles: ['User', 'Staff', 'Admin']
    }
  },
  {
    path: '/thong-bao',
    name: 'ThongBao',
    component: ThongBao,
    meta: {
      layout: 'user'
    }
  },

  // ===================================================
  // STAFF
  // ===================================================

  // Dashboard Staff
  {
    path: '/staff',
    name: 'DashboardStaff',
    component: DashboardStaff,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },

  // Quản lý đặt sân
  {
    path: '/staff/dat-san',
    name: 'QuanLyDatSanStaff',
    component: QuanLyDatSanStaff,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },

  // Quản lý thanh toán
  {
    path: '/staff/thanh-toan',
    name: 'QuanLyThanhToanStaff',
    component: QuanLyThanhToanStaff,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },

  // Quản lý lịch sân
  {
    path: '/staff/lich-san',
    name: 'QuanLyLichSan',
    component: QuanLyLichSan,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },

  // ===================================================
  // XEM HÓA ĐƠN STAFF
  // ===================================================
  {
    path: '/staff/hoa-don',
    name: 'XemHoaDonStaff',
    component: XemHoaDonStaff,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },

  // Quản lý bài viết Staff
  {
    path: '/staff/bai-viet',
    name: 'StaffBaiViet',
    component: QuanLyBaiVietStaff,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },


  // Phản hồi trang chủ
  {
    path: '/staff/phan-hoi-trang-chu',
    name: 'StaffPhanHoiTrangChu',
    component: PhanHoiTrangChu,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },

  // Thông tin cá nhân Staff
  {
    path: '/staff/ca-nhan',
    name: 'ThongTinCaNhanStaff',
    component: ThongTinCaNhanStaff,
    meta: {
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff']
    }
  },

  {
    path: '/staff/quan-ly-support',
    name: 'QuanLySupportStaff',
    component: QuanLySupportStaff,
    meta: { 
      layout: 'staff',
      requiresAuth: true,
      roles: ['Staff'] // Chỉ cho phép Nhân viên và Admin truy cập
    }
  },

  // ===================================================
  // ADMIN
  // ===================================================

  // Dashboard Admin
  {
    path: '/admin',
    name: 'DashboardAdmin',
    component: DashboardAdmin,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý khách hàng
  {
    path: '/admin/khach-hang',
    name: 'QuanLyKhachHang',
    component: QuanLyKhachHang,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý nhân viên
  {
    path: '/admin/nhan-vien',
    name: 'QuanLyNhanVien',
    component: QuanLyNhanVien,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý sân bóng
  {
    path: '/admin/san-bong',
    name: 'QuanLySanBong',
    component: QuanLySanBong,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },
  // Thống kê doanh thu
  {
    path: '/admin/doanh-thu',
    name: 'ThongKeDoanhThu',
    component: ThongKeDoanhThu,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý thông báo
  {
    path: '/admin/thong-bao',
    name: 'QuanLyThongBao',
    component: QuanLyThongBao,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý đánh giá
  {
    path: '/admin/danh-gia',
    name: 'QuanLyDanhGia',
    component: QuanLyDanhGia,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý đánh giá hệ thống
  {
    path: '/admin/danh-gia-he-thong',
    name: 'QuanLyDanhGiaHeThong',
    component: QuanLyDanhGiaHeThong,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý sản phẩm
  {
    path: '/admin/san-pham',
    name: 'QuanLySanPham',
    component: QuanLySanPham,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Thông tin cá nhân Admin
  {
    path: '/admin/ca-nhan',
    name: 'ThongTinCaNhanAdmin',
    component: ThongTinCaNhanAdmin,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  },

  // Quản lý Voucher
  {
    path: '/admin/voucher',
    name: 'QuanLyVoucher',
    component: QuanLyVoucher,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin'],
      title: 'Quản lý Voucher'
    }
  },

  // Quản lý bài viết Admin
  {
    path: '/admin/bai-viet',
    name: 'QuanLyBaiVietAdmin',
    component: QuanLyBaiVietAdmin,
    meta: {
      layout: 'admin',
      requiresAuth: true,
      roles: ['Admin']
    }
  }
]

// =====================================================
// CREATE ROUTER
// =====================================================
const router = createRouter({
  history: createWebHistory(),
  routes,

  scrollBehavior(to) {
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth',
        top: 90
      }
    }

    return {
      top: 0
    }
  }
})

// =====================================================
// NAVIGATION GUARD
// =====================================================
router.beforeEach((to, from, next) => {

  // Kiểm tra đăng nhập
  const isAuthenticated =
    localStorage.getItem('isLoggedIn') === 'true'

  // Lấy quyền tài khoản
  const userRole =
    localStorage.getItem('userRole')

  // Kiểm tra route có yêu cầu đăng nhập không
  const requiresAuth =
    to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth) {

    // ===============================================
    // CHƯA ĐĂNG NHẬP
    // ===============================================
    if (!isAuthenticated) {
      alert(
        'Vui lòng đăng nhập tài khoản để tiếp tục sử dụng tính năng này!'
      )

      return next({
        name: 'DangNhap'
      })
    }

    // ===============================================
    // KIỂM TRA QUYỀN
    // ===============================================
    const allowedRoles = to.meta.roles

    if (
      allowedRoles &&
      !allowedRoles.includes(userRole)
    ) {
      alert(
        'Tài khoản của bạn không có quyền truy cập vào khu vực này!'
      )

      // Admin
      if (userRole === 'Admin') {
        return next({
          name: 'DashboardAdmin'
        })
      }

      // Staff
      if (userRole === 'Staff') {
        return next({
          name: 'DashboardStaff'
        })
      }

      // User
      return next({
        name: 'TrangChu'
      })
    }
  }

  // Cho phép truy cập
  next()
})

export default router