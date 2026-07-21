// src/services/thongKeService.js
// API calls thống kê cho Admin Dashboard & Thống kê doanh thu

const BASE_URL = 'http://localhost:8080/api/staff/thong-ke'

function getAuthHeader() {
  const token = localStorage.getItem('token')
  return token
    ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
    : { 'Content-Type': 'application/json' }
}

async function handleResponse(res) {
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || 'Lỗi máy chủ!')
  return data
}

export default {

  /** Dashboard: cards tổng quan + 2 line charts (12 tháng) */
  async layDashboard(nam) {
    const res = await fetch(`${BASE_URL}/dashboard?nam=${nam}`, {
      headers: getAuthHeader()
    })
    return handleResponse(res)
  },

  /** Doanh thu chi tiết: cards + line chart ngày + bảng sân */
  async layDoanhThu(nam, thang) {
    let url = `${BASE_URL}/doanh-thu?nam=${nam}`
    if (thang != null) url += `&thang=${thang}`
    const res = await fetch(url, {
      headers: getAuthHeader()
    })
    return handleResponse(res)
  }
}
