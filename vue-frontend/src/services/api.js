import axios from "axios";

// --- IMPORTANT ---
// Replace with your actual backend API base URL
const API_BASE_URL = "http://localhost:8080/api"; // Example placeholder

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Enable cookies for all requests
});

// Biến theo dõi số lần thử lại
let authRetryCount = 0;
const MAX_AUTH_RETRIES = 2;

// Biến đánh dấu đang ở trang đăng nhập
let isOnLoginPage = false;

// Thiết lập trạng thái trang đăng nhập
export function setLoginPageStatus(status) {
  isOnLoginPage = status;
  console.log("Login page status set to:", status);
}

// Optional: Add request interceptors
apiClient.interceptors.request.use(
  (config) => {
    // Xử lý đặc biệt cho FormData requests
    if (config.data instanceof FormData) {
      // Bỏ Content-Type để Axios tự đặt với boundary đúng
      delete config.headers["Content-Type"];
    }

    // Xử lý các request liên quan đến xác thực - thêm delay để tránh race condition
    if (
      config.url.includes("check-auth") ||
      config.url.includes("login") ||
      config.url.includes("profile")
    ) {
      console.log("Auth-related request:", config.url);
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Optional: Add response interceptors
apiClient.interceptors.response.use(
  (response) => {
    // Reset retry counter khi có response thành công
    if (
      response.config.url.includes("login") ||
      response.config.url.includes("check-auth")
    ) {
      authRetryCount = 0;
    }
    return response; // Return successful responses directly
  },
  (error) => {
    // Xử lý lỗi 401 Unauthorized
    if (error.response && error.response.status === 401) {
      // Lấy URL gốc của request đã gây lỗi
      const originalRequestUrl = error.config.url;
      // Lấy URL hiện tại của trang
      const currentPath = window.location.pathname;
      const isCurrentlyOnLoginPage = currentPath.includes("login");

      // Các đường dẫn API công khai không nên gây redirect khi bị 401
      const publicApiPaths = ["/api/home/", "/api/products/detail/"]; // Thêm các path public khác nếu cần
      const isPublicApiRequest = publicApiPaths.some((path) =>
        originalRequestUrl.startsWith(path)
      );

      // Kiểm tra xem request có phải là check-auth không
      const isCheckAuth = originalRequestUrl.includes("/check-auth");

      console.log(`Interceptor caught 401 for: ${originalRequestUrl}`);
      console.log(`Is checkAuth request: ${isCheckAuth}`);
      console.log(`Is public API request: ${isPublicApiRequest}`);
      console.log(`Is on login page: ${isCurrentlyOnLoginPage}`);

      // CHỈ redirect nếu:
      // 1. Lỗi 401 KHÔNG phải từ check-auth HOẶC từ API public
      // 2. Người dùng KHÔNG đang ở trang login
      // 3. Số lần thử lại chưa đạt tối đa
      if (
        !isCheckAuth &&
        !isPublicApiRequest &&
        !isCurrentlyOnLoginPage &&
        authRetryCount < MAX_AUTH_RETRIES
      ) {
        console.log(
          "Protected API failed with 401, cleaning up session and redirecting to login..."
        );
        authRetryCount++;

        // Clear user data
        localStorage.removeItem("user");
        localStorage.removeItem("cartCount");

        // Lưu current path để sau khi đăng nhập redirect lại
        if (!currentPath.includes("login")) {
          localStorage.setItem("redirectAfterLogin", currentPath);
          console.log("Saved redirect path:", currentPath);

          // Redirect với tham số session=expired để hiện thông báo
          window.location.href = "/login?session=expired";
          return new Promise(() => {}); // Không resolve/reject promise để tránh lỗi tiếp theo
        }
      } else {
        console.info(
          "Auth request failed but not redirecting (already on login page or too many retries)"
        );
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
