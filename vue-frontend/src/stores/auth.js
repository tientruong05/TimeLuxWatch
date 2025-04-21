import { defineStore } from "pinia";
import apiClient from "@/services/api";

export const useAuthStore = defineStore("auth", {
  // State: Trạng thái người dùng và giỏ hàng
  state: () => ({
    user: localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user"))
      : null,
    cartCount: localStorage.getItem("cartCount")
      ? parseInt(localStorage.getItem("cartCount"))
      : 0,
    loading: false,
    error: null,
    justCheckedOut: false,
    isAuthCheckPending: false,
  }),

  // Getters: Các trạng thái được tính toán
  getters: {
    isAuthenticated: (state) => !!state.user,
    userName: (state) => (state.user ? state.user.fullName : ""),
    isAdmin: (state) => state.user && state.user.role,
  },

  // Actions: Các hàm thay đổi trạng thái
  actions: {
    // Đăng nhập
    async login(username, password) {
      this.loading = true;
      this.error = null;

      try {
        console.log("Attempting login for user:", username);
        // Chuẩn bị dữ liệu form
        const formData = new URLSearchParams();
        formData.append("username", username);
        formData.append("password", password);

        const response = await apiClient.post("/auth/login", formData, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          withCredentials: true, // Đảm bảo cookies được lưu
        });

        console.log("Login response received:", response.status);
        console.log("Response headers:", response.headers);
        console.log("Cookies after login:", document.cookie);

        if (response.data.user) {
          // Lưu vào store
          this.user = response.data.user;
          this.cartCount = response.data.cartCount || 0;

          // Lưu vào localStorage
          localStorage.setItem("user", JSON.stringify(this.user));
          localStorage.setItem("cartCount", this.cartCount.toString());

          console.log("Login successful, user saved to store and localStorage");

          return {
            success: true,
            message: response.data.message,
          };
        } else {
          console.error(
            "Login response did not contain user data:",
            response.data
          );
          throw new Error(response.data.error || "Unknown login error");
        }
      } catch (error) {
        console.error("Login error details:", error);
        this.error =
          error.response?.data?.error || error.message || "Login failed";
        console.error("Login error:", this.error);
        return {
          success: false,
          message: this.error,
        };
      } finally {
        this.loading = false;
      }
    },

    // Đăng xuất
    async logout() {
      this.loading = true;

      try {
        // Xóa dữ liệu người dùng từ localStorage trước khi gọi API
        localStorage.removeItem("user");
        localStorage.removeItem("cartCount");
        sessionStorage.clear();

        // Gọi API đăng xuất
        await apiClient.get("/auth/logout");

        // Xóa dữ liệu trong store
        this.user = null;
        this.cartCount = 0;

        return {
          success: true,
          message: "Đăng xuất thành công!",
        };
      } catch (error) {
        console.error("Error during logout:", error);

        // Ngay cả khi có lỗi, vẫn đảm bảo đăng xuất thành công ở phía client
        this.user = null;
        this.cartCount = 0;

        return {
          success: true,
          message: "Đăng xuất thành công!",
        };
      } finally {
        this.loading = false;
      }
    },

    // Cập nhật số lượng giỏ hàng
    updateCartCount(count) {
      this.cartCount = count;
      localStorage.setItem("cartCount", count.toString());
    },

    // New method to refresh cart count from server
    async refreshCartCount() {
      try {
        const response = await apiClient.get("/cart/count");
        if (response.data && response.data.count !== undefined) {
          console.log(
            `auth.js: Refreshing cartCount from server: ${response.data.count}`
          );
          this.cartCount = response.data.count;
          localStorage.setItem("cartCount", this.cartCount.toString());
        }
        return this.cartCount;
      } catch (error) {
        console.error("Error refreshing cart count:", error);
        // Don't change the existing count on error
        return this.cartCount;
      }
    },

    // Kiểm tra trạng thái đăng nhập từ server
    async checkAuthStatus() {
      if (this.isAuthCheckPending) {
        console.log(
          "auth.js: Auth check skipped because isAuthCheckPending is true."
        );
        return this.isLoggedIn();
      }

      try {
        console.log("Checking auth status with backend...");
        if (this.justCheckedOut) {
          console.log(
            "auth.js: Skipping cartCount update check immediately after checkout."
          );
          this.justCheckedOut = false;
        } else {
          console.log(
            "auth.js: Not just checked out, proceeding with normal check."
          );
        }

        console.log("Cookie available:", document.cookie);

        // Thử gọi API check-auth
        const response = await apiClient.get("/account/check-auth", {
          withCredentials: true,
          headers: {
            "Cache-Control": "no-cache",
          },
        });

        console.log("Auth check response:", response.data);

        if (response.data && response.data.authenticated) {
          // Cập nhật thông tin người dùng nếu có từ server
          if (response.data.user) {
            this.user = response.data.user;
            console.log("Updated user from API response:", this.user);
          } else {
            // Nếu không có thông tin chi tiết, ít nhất cập nhật username
            if (!this.user) {
              this.user = {
                username: response.data.username,
                role: response.data.role || false,
              };
              console.log("Created minimal user object:", this.user);
            } else {
              // Cập nhật role từ server nếu có user trong store
              this.user.role = response.data.role;
              console.log("Updated user role:", this.user.role);
            }
          }

          // Nếu có thông tin giỏ hàng, cập nhật
          if (response.data.cartCount !== undefined) {
            if (
              !this.justCheckedOut &&
              this.cartCount !== response.data.cartCount
            ) {
              console.log(
                `auth.js: Updating cartCount from ${this.cartCount} to ${response.data.cartCount} based on checkAuthStatus`
              ); // Log change
              this.cartCount = response.data.cartCount;
              localStorage.setItem("cartCount", this.cartCount.toString());
            } else {
              console.log(
                `auth.js: Cart count from checkAuthStatus (${response.data.cartCount}) matches store. No update needed.`
              ); // Log no change
            }
          }

          // Lưu thông tin người dùng đã cập nhật
          localStorage.setItem("user", JSON.stringify(this.user));
          console.log("Auth check successful - user authenticated");

          return true;
        } else {
          console.log("Auth check returned no authentication");
          return false;
        }
      } catch (error) {
        // Add detailed logging for CORS or network errors
        if (!error.response) {
          console.error(
            "Network Error or CORS issue preventing request:",
            error.message
          );
        } else {
          console.error("Error checking auth status:", error);
        }

        // Kiểm tra lỗi 401 - Không xác thực
        if (error.response && error.response.status === 401) {
          console.log("Auth check returned 401 - User not authenticated");
          if (this.user) {
            console.log("Clearing user data from store due to expired session");
            this.clearUserData();
          }
          return false;
        }

        // Nếu không có phản hồi từ server (lỗi mạng hoặc CORS block)
        console.log(
          "Using local authentication status due to network/CORS error"
        );
        return this.isLoggedIn();
      }
    },

    // Kiểm tra nhanh trạng thái đăng nhập (không gọi API)
    isLoggedIn() {
      const loggedIn = !!this.user;
      console.log("Local auth check (isLoggedIn):", loggedIn);
      return loggedIn;
    },

    // Xóa dữ liệu người dùng
    clearUserData() {
      this.user = null;
      this.cartCount = 0;
      this.justCheckedOut = false;
      this.isAuthCheckPending = false;
      localStorage.removeItem("user");
      localStorage.removeItem("cartCount");
      console.log("User data cleared from store and localStorage");
    },
  },
});
