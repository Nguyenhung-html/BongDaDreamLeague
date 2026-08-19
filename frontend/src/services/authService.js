// src/services/authService.js
// Gọi API xác thực thật xuống Spring Boot.
// TRƯỚC ĐÂY file này trả về dữ liệu giả (Promise.resolve({ user: null })) nên
// bất kỳ chỗ nào gọi vào đều tưởng đăng nhập thành công dù chưa hề gọi server.

const BASE_URL = 'http://localhost:8080/api/xac-thuc'

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
    data = { message: text }
  }
  if (!res.ok) {
    throw new Error((data && (data.message || data)) || 'Yêu cầu thất bại!')
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

  /** Xoá toàn bộ phiên đăng nhập */
  logout() {
    ;['token', 'hoTen', 'soDienThoai', 'userId', 'isLoggedIn', 'userRole', 'vaiTro'].forEach(k =>
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
