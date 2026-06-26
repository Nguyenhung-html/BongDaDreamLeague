// src/services/staffService.js
// Tất cả API calls dành cho nhân viên Staff

const BASE_URL = 'http://localhost:8080/api/staff'

function getAuthHeader() {
  const token = localStorage.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' }
}

async function handleResponse(res) {
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || 'Lỗi máy chủ!')
  return data
}

export default {

  // ── Quản lý Đặt sân ──────────────────────────────────────────

  /** Lấy danh sách tất cả đơn đặt sân, filter tuỳ chọn */
  async layDanhSachDatSan(params = {}) {
    const query = new URLSearchParams()
    if (params.trangThai) query.append('trangThai', params.trangThai)
    if (params.ngay) query.append('ngay', params.ngay)
    const res = await fetch(`${BASE_URL}/dat-san?${query}`, { headers: getAuthHeader() })
    return handleResponse(res)
  },

  /** Đặt sân walk-in tại quầy */
  async datSanWalkIn(payload) {
    const res = await fetch(`${BASE_URL}/dat-san`, {
      method: 'POST',
      headers: getAuthHeader(),
      body: JSON.stringify(payload)
    })
    return handleResponse(res)
  },

  /** Xác nhận đơn đặt sân → CHO_XAC_NHAN thành DA_COC */
  async xacNhanDon(id) {
    const res = await fetch(`${BASE_URL}/dat-san/${id}/xac-nhan`, {
      method: 'PUT',
      headers: getAuthHeader()
    })
    return handleResponse(res)
  },

  /** Staff hủy đơn (kèm lý do) */
  async staffHuyDon(id, lyDo = '') {
    const res = await fetch(`${BASE_URL}/dat-san/${id}/huy`, {
      method: 'PUT',
      headers: getAuthHeader(),
      body: JSON.stringify({ lyDo })
    })
    return handleResponse(res)
  },

  /** Đánh dấu đơn hoàn thành */
  async hoanThanhDon(id) {
    const res = await fetch(`${BASE_URL}/dat-san/${id}/hoan-thanh`, {
      method: 'PUT',
      headers: getAuthHeader()
    })
    return handleResponse(res)
  },

  // ── Quản lý Lịch sân ─────────────────────────────────────────

  /** Lấy lịch sân theo ngày (YYYY-MM-DD) → calendar view */
  async layLichSanTheoNgay(ngay) {
    const res = await fetch(`${BASE_URL}/lich-san?ngay=${ngay}`, { headers: getAuthHeader() })
    return handleResponse(res)
  },

  /** Khóa hoặc mở sân */
  async capNhatTrangThaiSan(sanBongId, trangThai, lyDo = '') {
    const res = await fetch(`${BASE_URL}/san-bong/${sanBongId}/trang-thai`, {
      method: 'PUT',
      headers: getAuthHeader(),
      body: JSON.stringify({ trangThai, lyDo })
    })
    return handleResponse(res)
  },

  // ── Quản lý Thanh toán ───────────────────────────────────────

  /** Danh sách thanh toán (filter theo khoảng ngày tuỳ chọn) */
  async layDanhSachThanhToan(params = {}) {
    const query = new URLSearchParams()
    if (params.tuNgay) query.append('tuNgay', params.tuNgay)
    if (params.denNgay) query.append('denNgay', params.denNgay)
    const res = await fetch(`${BASE_URL}/thanh-toan?${query}`, { headers: getAuthHeader() })
    return handleResponse(res)
  },

  /** Xác nhận đã nhận tiền cọc */
  async xacNhanCoc(thanhToanId) {
    const res = await fetch(`${BASE_URL}/thanh-toan/${thanhToanId}/xac-nhan-coc`, {
      method: 'PUT',
      headers: getAuthHeader()
    })
    return handleResponse(res)
  },

  /** Thu phần tiền còn lại tại quầy */
  async thanhToanConLai(datSanId, phuongThuc) {
    const res = await fetch(`${BASE_URL}/thanh-toan/thanh-toan-cuoi`, {
      method: 'POST',
      headers: getAuthHeader(),
      body: JSON.stringify({ datSanId, phuongThuc })
    })
    return handleResponse(res)
  }
}
