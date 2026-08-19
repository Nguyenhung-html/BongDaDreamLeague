// src/services/googleAuth.js
// Quản lý tích hợp Google Identity Services (GIS) cho Frontend Vue 3

export const GOOGLE_CLIENT_ID = '215856834155-pe4904net5soi6ecgqrurud4qroi045m.apps.googleusercontent.com'

let scriptLoadingPromise = null

/**
 * Tải mã kịch bản Google Identity Services nếu chưa có
 */
export function loadGoogleGsiScript() {
  if (window.google?.accounts?.id) {
    return Promise.resolve(window.google)
  }

  if (scriptLoadingPromise) {
    return scriptLoadingPromise
  }

  scriptLoadingPromise = new Promise((resolve, reject) => {
    // Kiểm tra xem thẻ script đã có trên trang chưa
    const existingScript = document.querySelector('script[src="https://accounts.google.com/gsi/client"]')
    if (existingScript) {
      existingScript.addEventListener('load', () => resolve(window.google))
      existingScript.addEventListener('error', (err) => reject(err))
      return
    }

    const script = document.createElement('script')
    script.src = 'https://accounts.google.com/gsi/client'
    script.async = true
    script.defer = true
    script.onload = () => {
      resolve(window.google)
    }
    script.onerror = (err) => {
      reject(new Error('Không thể tải Google Identity Services SDK: ' + err))
    }
    document.head.appendChild(script)
  })

  return scriptLoadingPromise
}

/**
 * Khởi tạo và gắn nút Đăng nhập Google vào phần tử DOM chỉ định
 * @param {HTMLElement|string} elementOrId
 * @param {Function} onCredentialResponse Callback nhận (response.credential)
 */
export async function initGoogleSignIn(elementOrId, onCredentialResponse) {
  try {
    const google = await loadGoogleGsiScript()

    google.accounts.id.initialize({
      client_id: GOOGLE_CLIENT_ID,
      callback: (response) => {
        if (response && response.credential) {
          onCredentialResponse(response.credential)
        }
      },
      auto_select: false,
      cancel_on_tap_outside: true
    })

    const el = typeof elementOrId === 'string' ? document.getElementById(elementOrId) : elementOrId
    if (el) {
      google.accounts.id.renderButton(el, {
        type: 'standard',
        shape: 'rectangular',
        theme: 'outline',
        text: 'signin_with',
        size: 'large',
        logo_alignment: 'left',
        width: el.offsetWidth || 280,
        locale: 'vi'
      })
    }
  } catch (error) {
    console.error('Lỗi khởi tạo Google Sign-In:', error)
  }
}