// src/services/profileService.js
// Service quản lý API Thông tin cá nhân (Profile) & Logic Validation dùng chung

const API_BASE = 'http://localhost:8080/api'

/**
 * Lấy endpoint API tương ứng theo vai trò
 */
function getEndpointByRole(role) {
  const r = (role || '').toUpperCase()
  if (r === 'ADMIN') return `${API_BASE}/admin/profile`
  if (r === 'STAFF') return `${API_BASE}/staff/profile`
  return `${API_BASE}/user/profile`
}

/**
 * Đọc phản hồi an toàn từ Spring Boot
 */
async function parseResponse(res) {
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
      if (typeof data === 'string') errorMsg = data
      else if (typeof data === 'object') errorMsg = data.message || data.error || text || errorMsg
    } else if (text) {
      errorMsg = text
    }
    throw new Error(errorMsg)
  }
  return data
}

export default {
  /**
   * Lấy thông tin cá nhân từ Backend
   */
  async getProfile(userId, role) {
    const token = localStorage.getItem('token')
    const endpoint = getEndpointByRole(role)
    const res = await fetch(`${endpoint}/${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    return parseResponse(res)
  },

  /**
   * Cập nhật thông tin cá nhân
   */
  async updateProfile(userId, payload, role) {
    const token = localStorage.getItem('token')
    const endpoint = getEndpointByRole(role)
    const res = await fetch(`${endpoint}/${userId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        hoTen: payload.hoTen ? payload.hoTen.trim() : '',
        soDienThoai: payload.soDienThoai ? payload.soDienThoai.trim() : null,
        email: payload.email ? payload.email.trim() : '',
        diaChi: payload.diaChi ? payload.diaChi.trim() : ''
      })
    })

    const data = await parseResponse(res)

    // Cập nhật lại localStorage để Layouts/Header đồng bộ tên mới
    if (data.hoTen) localStorage.setItem('hoTen', data.hoTen)
    if (data.soDienThoai) localStorage.setItem('soDienThoai', data.soDienThoai)
    
    // Phát sự kiện toàn cục để Header/Sidebar cập nhật ngay lập tức
    window.dispatchEvent(new CustomEvent('user-profile-updated', { detail: data }))

    return data
  },

  /**
   * Đổi mật khẩu
   */
  async changePassword(userId, payload, role) {
    const token = localStorage.getItem('token')
    const endpoint = getEndpointByRole(role)
    const res = await fetch(`${endpoint}/${userId}/doi-mat-khau`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        matKhauHienTai: payload.matKhauHienTai,
        matKhauMoi: payload.matKhauMoi
      })
    })
    return parseResponse(res)
  },

  // ===== BỘ HÀM KIỂM TRA ĐIỀU KIỆN (VALIDATION & NHẮC NHỞ) =====

  validateHoTen(hoTen) {
    if (!hoTen || !hoTen.trim()) {
      return 'Vui lòng nhập họ và tên đầy đủ'
    }
    if (hoTen.trim().length < 2) {
      return 'Họ và tên phải có ít nhất 2 ký tự'
    }
    return ''
  },

  validateEmail(email) {
    if (!email || !email.trim()) {
      return 'Vui lòng nhập địa chỉ email'
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(email.trim())) {
      return 'Email không hợp lệ (ví dụ: ban@email.com)'
    }
    return ''
  },

  validateSoDienThoai(soDienThoai) {
    if (!soDienThoai || !soDienThoai.trim()) {
      return 'Vui lòng nhập số điện thoại'
    }
    const phoneRegex = /^0\d{9}$/
    if (!phoneRegex.test(soDienThoai.trim())) {
      return 'Số điện thoại không hợp lệ (gồm 10 số bắt đầu bằng 0, vd: 0901234567)'
    }
    return ''
  },

  validatePassword(password) {
    if (!password) {
      return 'Vui lòng nhập mật khẩu mới'
    }
    if (password.length < 6) {
      return 'Mật khẩu mới phải có ít nhất 6 ký tự'
    }
    return ''
  },

  validateConfirmPassword(password, confirmPassword) {
    if (!confirmPassword) {
      return 'Vui lòng xác nhận mật khẩu mới'
    }
    if (password !== confirmPassword) {
      return 'Mật khẩu xác nhận không trùng khớp'
    }
    return ''
  },

  /**
   * Tính toán độ mạnh mật khẩu (Score 0 -> 4) theo chuẩn Form Đăng ký
   */
  calculatePasswordScore(password) {
    if (!password) return 0
    let score = 0
    if (password.length >= 6) score++
    if (password.length >= 10) score++
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++
    if (/\d/.test(password) && /[^A-Za-z0-9]/.test(password)) score++
    return score
  },

  getPasswordLabel(score) {
    const labels = ['Rất yếu', 'Yếu', 'Trung bình', 'Khá mạnh', 'Mạnh']
    return labels[score] || 'Rất yếu'
  }
}