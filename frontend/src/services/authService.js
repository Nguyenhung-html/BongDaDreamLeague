// src/services/authService.js
// Gọi API xác thực thật xuống Spring Boot.
// TRƯỚC ĐÂY file này trả về dữ liệu giả (Promise.resolve({ user: null })) nên
// bất kỳ chỗ nào gọi vào đều tưởng đăng nhập thành công dù chưa hề gọi server.

const BASE_URL = '/api/xac-thuc'

/** Chuẩn hoá vai trò từ backend (USER/STAFF/ADMIN) sang dạng router dùng (User/Staff/Admin) */
export function chuanHoaVaiTro(vaiTro) {
  if (vaiTro === 'ADMIN') return 'Admin'
  if (vaiTro === 'STAFF') return 'Staff'
  return 'User'
}

/** Đọc phản hồi an toàn: backend có chỗ trả JSON, có chỗ trả plain text */
async function docPhanHoi(res) {
  const text = await res.text()
  let data = null
  try {
    data = text ? JSON.parse(text) : null
  } catch {
    data = text ? { message: text } : null
  }
  if (!res.ok) {
    let errorMsg = 'Yêu cầu thất bại!'
    if (data) {
      if (typeof data === 'string') {
        errorMsg = data
      } else if (typeof data === 'object') {
        errorMsg = data.message || data.error || text || errorMsg
      }
    } else if (text) {
      errorMsg = text
    }
    throw new Error(errorMsg)
  }
  return data
}

export default {
  chuanHoaVaiTro,

  /**
   * Đăng nhập. `identifier` có thể là email HOẶC số điện thoại —
   * backend tự dò cả hai (findByEmailOrSoDienThoai).
   * Trả về object tài khoản và tự lưu phiên vào localStorage.
   */
  async login({ identifier, password }) {
    const res = await fetch(`${BASE_URL}/dang-nhap`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: identifier, matKhau: password })
    })

    const data = await docPhanHoi(res)
    if (!data) throw new Error('Máy chủ phản hồi rỗng, không tìm thấy dữ liệu tài khoản!')

    const vaiTro = chuanHoaVaiTro(data.vaiTro)

    localStorage.setItem('token', data.token || '')
    localStorage.setItem('hoTen', data.hoTen || '')
    localStorage.setItem('soDienThoai', data.soDienThoai || '')
    localStorage.setItem('userId', data.idNguoiDung || '')
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('userRole', vaiTro)

    if (data.avatar || data.hinhAnh) {
      if (data.idNguoiDung) {
        localStorage.setItem(`avatar_${data.idNguoiDung}`, data.avatar || data.hinhAnh)
      }
    }
    localStorage.removeItem('user_avatar')
    localStorage.removeItem('admin_avatar')
    localStorage.removeItem('staff_avatar')

    return { ...data, vaiTro }
  },

  /**
   * Đăng nhập / Đăng ký tự động bằng Google ID Token.
   * Lưu phiên đăng nhập vào localStorage và trả về thông tin người dùng.
   */
  async loginWithGoogle(idToken) {
    const res = await fetch(`${BASE_URL}/google`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken })
    })

    const data = await docPhanHoi(res)
    if (!data) throw new Error('Máy chủ phản hồi rỗng!')

    const vaiTro = chuanHoaVaiTro(data.vaiTro)

    localStorage.setItem('token', data.token || '')
    localStorage.setItem('hoTen', data.hoTen || '')
    localStorage.setItem('soDienThoai', data.soDienThoai || '')
    localStorage.setItem('userId', data.idNguoiDung || '')
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('userRole', vaiTro)
    if (data.avatar || data.hinhAnh) {
      if (data.idNguoiDung) {
        localStorage.setItem(`avatar_${data.idNguoiDung}`, data.avatar || data.hinhAnh)
      }
    }
    localStorage.removeItem('user_avatar')
    localStorage.removeItem('admin_avatar')
    localStorage.removeItem('staff_avatar')

    return { ...data, vaiTro }
  },

  /** Đăng ký tài khoản mới (backend luôn gán vai trò USER) */
  async register(payload) {
    const res = await fetch(`${BASE_URL}/dang-ky`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    return docPhanHoi(res)
  },

  /** Gửi mã OTP xác thực qua Email để quên mật khẩu */
  async guiMaOtp(email) {
    const res = await fetch(`${BASE_URL}/quen-mat-khau/gui-ma`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    })
    return docPhanHoi(res)
  },

  /** Xác thực mã OTP qua Email */
  async xacThucMaOtp(email, maXacThuc) {
    const res = await fetch(`${BASE_URL}/quen-mat-khau/xac-thuc-ma`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, maXacThuc })
    })
    return docPhanHoi(res)
  },

  /** Cài đặt mật khẩu mới sau khi xác thực OTP qua Email */
  async datLaiMatKhau(email, maXacThuc, matKhauMoi) {
    const res = await fetch(`${BASE_URL}/quen-mat-khau/dat-lai-mat-khau`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, maXacThuc, matKhauMoi })
    })
    return docPhanHoi(res)
  },

  /** Gửi mã OTP xác thực qua SMS để quên mật khẩu */
  async guiMaOtpSms(soDienThoai) {
    const res = await fetch(`${BASE_URL}/quen-mat-khau/gui-ma-sms`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ soDienThoai })
    })
    return docPhanHoi(res)
  },

  /** Xác thực mã OTP qua SMS */
  async xacThucMaOtpSms(soDienThoai, maXacThuc) {
    const res = await fetch(`${BASE_URL}/quen-mat-khau/xac-thuc-ma-sms`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ soDienThoai, maXacThuc })
    })
    return docPhanHoi(res)
  },

  /** Cài đặt mật khẩu mới sau khi xác thực OTP qua SMS */
  async datLaiMatKhauSms(soDienThoai, maXacThuc, matKhauMoi) {
    const res = await fetch(`${BASE_URL}/quen-mat-khau/dat-lai-mat-khau-sms`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ soDienThoai, maXacThuc, matKhauMoi })
    })
    return docPhanHoi(res)
  },

  /** Xoá toàn bộ phiên đăng nhập */
  logout() {
     ;['token', 'hoTen', 'soDienThoai', 'userId', 'isLoggedIn', 'userRole', 'vaiTro', 'user_avatar', 'admin_avatar', 'staff_avatar'].forEach(k =>
      localStorage.removeItem(k)
    )
  },

  /** Đường dẫn điều hướng sau khi đăng nhập, theo vai trò */
  duongDanTheoVaiTro(vaiTro) {
    if (vaiTro === 'Admin') return '/admin'
    if (vaiTro === 'Staff') return '/staff'
    return '/'
  }
}
