<template>
  <div class="bg">
    <div class="logo-header">
      <router-link to="/" class="logo-link">
        <svg class="logo" width="80" height="80" viewBox="0 0 80 80">
          <circle
            cx="40"
            cy="40"
            r="35"
            stroke="#d4af37"
            stroke-width="2"
            fill="none"
          />
          <circle cx="40" cy="40" r="3" fill="#d4af37" />
          <line
            x1="40"
            y1="40"
            x2="40"
            y2="20"
            stroke="#d4af37"
            stroke-width="2"
          />
          <line
            x1="40"
            y1="40"
            x2="55"
            y2="45"
            stroke="#d4af37"
            stroke-width="2"
          />
        </svg>
        <div class="logo-text">TimeLux Watch</div>
      </router-link>
    </div>

    <div :class="['container', { 'right-panel-active': isRegister }]">
      <!-- Đăng ký -->
      <div class="form-container sign-up-container">
        <form @submit.prevent="handleRegister">
          <h1>Tạo tài khoản</h1>
          <div v-if="registerMessage" class="alert" :class="registerAlertClass">
            {{ registerMessage }}
          </div>
          <input
            v-model="register.fullname"
            type="text"
            placeholder="Họ và tên"
            required
          />
          <input
            v-model="register.username"
            type="text"
            placeholder="Tài khoản"
            required
          />
          <input
            v-model="register.email"
            type="email"
            placeholder="Email"
            required
          />
          <input
            v-model="register.password"
            type="password"
            placeholder="Mật khẩu"
            required
          />
          <input
            v-model="register.address"
            type="text"
            placeholder="Địa chỉ"
            required
          />
          <input
            v-model="register.phone"
            type="text"
            placeholder="Số điện thoại"
            required
          />
          <button type="submit">Đăng ký</button>
        </form>
      </div>

      <!-- Đăng nhập -->
      <div class="form-container sign-in-container">
        <form @submit.prevent="handleLogin">
          <h1>Đăng nhập</h1>
          <div v-if="loginMessage" class="alert" :class="loginAlertClass">
            {{ loginMessage }}
          </div>
          <input
            v-model="login.username"
            type="text"
            placeholder="Email"
            required
          />
          <input
            v-model="login.password"
            type="password"
            placeholder="Mật khẩu"
            required
          />
          <router-link to="/forgot-pass">Quên mật khẩu?</router-link>
          <button type="submit" :disabled="isLoggingIn">
            {{ isLoggingIn ? "Đang xử lý..." : "Đăng nhập" }}
          </button>
        </form>
      </div>

      <!-- Overlay -->
      <div class="overlay-container">
        <div class="overlay">
          <div class="overlay-panel overlay-left">
            <h1>Chào mừng trở lại!</h1>
            <p>Bạn đã có tài khoản? Nhấp vào nút bên dưới để đăng nhập</p>
            <button class="ghost" @click="isRegister = false">Đăng nhập</button>
          </div>
          <div class="overlay-panel overlay-right">
            <h1>Xin chào!</h1>
            <p>Bạn chưa có tài khoản? Nhấp vào nút bên dưới để đăng ký</p>
            <button class="ghost" @click="isRegister = true">Đăng ký</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth"; // Import Pinia store
import apiClient from "@/services/api"; // Sử dụng apiClient
import { setLoginPageStatus } from "@/services/api"; // Import hàm cập nhật trạng thái

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore(); // Khởi tạo store
const isRegister = ref(false);
const isLoggingIn = ref(false);
const isRegistering = ref(false);
const isCheckingAuth = ref(false);

const login = ref({
  username: "",
  password: "",
});

const register = ref({
  fullname: "",
  username: "",
  email: "",
  password: "",
  address: "",
  phone: "",
});

const loginMessage = ref("");
const loginAlertClass = computed(() =>
  loginMessage.value.includes("thành công") ? "alert-success" : "alert-danger"
);

const registerMessage = ref("");
const registerAlertClass = computed(() =>
  registerMessage.value.includes("thành công")
    ? "alert-success"
    : "alert-danger"
);

// Đánh dấu đang ở trang login
onMounted(() => {
  setLoginPageStatus(true);
  checkAuthenticationStatus();

  // Check for activation message from URL
  const activationStatus = route.query.activation;
  const activationMessage = route.query.message;
  if (activationStatus && activationMessage) {
    try {
      const decodedMessage = decodeURIComponent(activationMessage);
      if (activationStatus === "success") {
        // Use loginMessage to display success on the login form side
        loginMessage.value = decodedMessage;
      } else if (activationStatus === "error") {
        loginMessage.value = decodedMessage;
      } else {
        // Handle 'info' or other statuses if needed
        loginMessage.value = decodedMessage;
      }
      // Optionally clear the query params from URL after reading
      // router.replace({ query: {} }); // Be careful with timing if checkAuthenticationStatus also redirects
    } catch (e) {
      console.error("Error decoding activation message:", e);
      loginMessage.value = "Lỗi xử lý thông báo kích hoạt.";
    }
  }
});

// Xóa đánh dấu khi rời trang
onBeforeUnmount(() => {
  setLoginPageStatus(false);
});

// Kiểm tra trạng thái đăng nhập khi component được mount
const checkAuthenticationStatus = async () => {
  isCheckingAuth.value = true;

  // Kiểm tra nếu URL có query param session=expired
  if (route.query.session === "expired") {
    loginMessage.value = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
  }

  try {
    // Kiểm tra localStorage trước - nếu có user thì thử kiểm tra API session
    const userFromStorage = localStorage.getItem("user");
    if (userFromStorage) {
      // Kiểm tra xem người dùng đã đăng nhập chưa thông qua API
      const isLoggedIn = await authStore.checkAuthStatus();

      console.log("Auth status check result:", isLoggedIn);

      // Nếu đã đăng nhập, chuyển hướng đến trang chủ hoặc trang đã được lưu
      if (isLoggedIn) {
        console.log("User already logged in, redirecting...");

        // Sử dụng router.replace thay vì window.location để tránh refresh
        redirectAfterLogin();
      }
    }
  } catch (error) {
    console.error("Error checking authentication status:", error);
    // Không làm gì, cho phép người dùng đăng nhập
  } finally {
    isCheckingAuth.value = false;
  }
};

// Hàm xử lý chuyển hướng sau khi đăng nhập
const redirectAfterLogin = () => {
  // Kiểm tra xem có redirect URL được lưu trữ không
  const redirectUrl = localStorage.getItem("redirectAfterLogin");

  if (redirectUrl) {
    localStorage.removeItem("redirectAfterLogin");
    console.log("Redirecting to saved URL:", redirectUrl);
    router.replace(redirectUrl);
  } else {
    console.log("No saved redirect URL, going to homepage");
    router.replace("/");
  }
};

// --- Login Handler ---
const handleLogin = async () => {
  if (isLoggingIn.value) return;
  isLoggingIn.value = true;
  loginMessage.value = "";

  try {
    // Sử dụng authStore để đăng nhập
    const result = await authStore.login(
      login.value.username,
      login.value.password
    );

    // Xử lý kết quả
    loginMessage.value = result.message;

    if (result.success) {
      // Đợi 1 chút để đảm bảo state đã được cập nhật
      await new Promise((resolve) => setTimeout(resolve, 100));

      console.log("Login successful, store user:", authStore.user);
      console.log(
        "Login successful, localStorage user:",
        localStorage.getItem("user")
      );
      console.log("Login successful, current cookies:", document.cookie);

      // Không cần checkAuthStatus lại ở đây nữa vì login đã cập nhật state
      // Chỉ cần đợi và chuyển hướng
      setTimeout(() => {
        redirectAfterLogin();
      }, 500); // Giữ lại delay nhỏ để UI kịp cập nhật
    }
  } catch (error) {
    console.error("Login error:", error);
    loginMessage.value = "Lỗi đăng nhập. Vui lòng thử lại.";
  } finally {
    isLoggingIn.value = false;
  }
};

// --- Register Handler ---
const handleRegister = async () => {
  if (isRegistering.value) return;
  isRegistering.value = true;
  registerMessage.value = "";

  if (!register.value.password) {
    registerMessage.value = "Vui lòng nhập mật khẩu.";
    isRegistering.value = false;
    return;
  }
  // Password length validation (e.g., minimum 6 characters)
  if (register.value.password.length < 6) {
    registerMessage.value = "Mật khẩu phải có ít nhất 6 ký tự.";
    isRegistering.value = false;
    return;
  }

  // Address validation (basic check)
  if (!register.value.address?.trim()) {
    registerMessage.value = "Vui lòng nhập địa chỉ.";
    isRegistering.value = false;
    return;
  }

  // Phone number validation (Vietnamese format: starts with 0, 10 digits total)
  const phoneRegex = /^0\d{9}$/;
  if (!register.value.phone || !phoneRegex.test(register.value.phone)) {
    registerMessage.value =
      "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và có 10 chữ số).";
    isRegistering.value = false;
    return;
  }
  // --- End Validation ---

  try {
    const formData = new URLSearchParams();
    formData.append("fullname", register.value.fullname);
    formData.append("username", register.value.username);
    formData.append("email", register.value.email);
    formData.append("password", register.value.password);
    formData.append("address", register.value.address);
    formData.append("phone", register.value.phone);

    console.log("Attempting registration with:", register.value.email);

    // Sử dụng apiClient để giữ nhất quán với phần login
    const response = await apiClient.post("/auth/register", formData, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });

    console.log("Register response:", response.data);
    registerMessage.value = response.data.message || "Đăng ký thành công!";

    // Switch to login form after successful registration
    if (response.status >= 200 && response.status < 300) {
      setTimeout(() => {
        isRegister.value = false; // Switch to login view
      }, 2000);
    }
  } catch (error) {
    console.error("Registration error:", error);
    registerMessage.value =
      error.response?.data?.error || "Lỗi đăng ký. Vui lòng thử lại.";
  } finally {
    isRegistering.value = false;
  }
};
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.bg {
  background: #1a1a1a;
  background-image: linear-gradient(to bottom right, #1a1a1a, #2c2c2c);
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-family: "Montserrat", sans-serif;
  min-height: 100vh;
  color: #ffffff;
}

.logo-header {
  margin-bottom: 30px;
  text-align: center;
}

.logo-link {
  display: inline-block;
  transition: transform 0.3s ease;
}

.logo-link:hover {
  transform: scale(1.05);
}

.logo {
  height: 80px;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.3));
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #d4af37;
  text-transform: uppercase;
  letter-spacing: 2px;
  margin-top: 8px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.container {
  background-color: #2d2d2d;
  border-radius: 15px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5);
  position: relative;
  overflow: hidden;
  width: 850px;
  max-width: 100%;
  min-height: 550px;
}

.form-container {
  position: absolute;
  top: 0;
  height: 100%;
  transition: all 0.6s ease-in-out;
}

.sign-in-container {
  left: 0;
  width: 50%;
  z-index: 2;
}

.sign-up-container {
  left: 0;
  width: 50%;
  opacity: 0;
  z-index: 1;
}

.container.right-panel-active .sign-in-container {
  transform: translateX(100%);
}

.container.right-panel-active .sign-up-container {
  transform: translateX(100%);
  opacity: 1;
  z-index: 5;
}

form {
  background-color: #2d2d2d;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 50px;
  height: 100%;
  text-align: center;
}

h1 {
  color: #d4af37;
  font-weight: bold;
  margin: 0 0 20px;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 24px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

input {
  background-color: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 8px;
  color: #ffffff;
  padding: 12px 15px;
  margin: 8px 0;
  width: 100%;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #d4af37;
  box-shadow: 0 0 0 3px rgba(212, 175, 55, 0.2);
}

button {
  border-radius: 20px;
  border: 2px solid #d4af37;
  background: transparent;
  color: #d4af37;
  font-size: 14px;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: all 0.3s ease;
  margin-top: 20px;
  cursor: pointer;
}

button:hover {
  background: linear-gradient(135deg, #d4af37, #f4e08d);
  color: #1a1a1a;
  transform: scale(1.05);
  box-shadow: 0 5px 15px rgba(212, 175, 55, 0.4);
}

.overlay-container {
  position: absolute;
  top: 0;
  left: 50%;
  width: 50%;
  height: 100%;
  overflow: hidden;
  transition: transform 0.6s ease-in-out;
  z-index: 100;
}

.container.right-panel-active .overlay-container {
  transform: translateX(-100%);
}

.overlay {
  background: linear-gradient(135deg, #d4af37, #f4e08d);
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 0 0;
  color: #1a1a1a;
  position: relative;
  left: -100%;
  height: 100%;
  width: 200%;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.container.right-panel-active .overlay {
  transform: translateX(50%);
}

.overlay-panel {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 40px;
  text-align: center;
  top: 0;
  height: 100%;
  width: 50%;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.overlay-left {
  transform: translateX(-20%);
}

.overlay-right {
  right: 0;
  transform: translateX(0);
}

.container.right-panel-active .overlay-left {
  transform: translateX(0);
}

.container.right-panel-active .overlay-right {
  transform: translateX(20%);
}

.overlay-panel h1 {
  color: #1a1a1a;
  font-size: 28px;
  margin-bottom: 30px;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.2);
}

.overlay-panel p {
  font-size: 16px;
  font-weight: 500;
  line-height: 1.5;
  margin-bottom: 30px;
  color: #333333;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.2);
}

.overlay-panel button {
  background-color: #1a1a1a;
  border-color: #1a1a1a;
  color: #d4af37;
}

.overlay-panel button:hover {
  background-color: #333333;
  color: #f4e08d;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

a {
  color: #d4af37;
  font-size: 14px;
  text-decoration: none;
  margin: 15px 0;
  transition: all 0.3s ease;
  font-weight: 500;
}

a:hover {
  color: #f4e08d;
  text-shadow: 0 0 5px rgba(212, 175, 55, 0.5);
}

footer {
  margin-top: 20px;
  text-align: center;
  color: #a0a0a0;
  font-size: 0.8rem;
}

/* CSS cho thông báo */
.alert {
  border-radius: 8px;
  padding: 12px 15px;
  font-size: 0.9rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  margin-bottom: 15px;
  width: 100%;
  max-width: 300px;
  position: relative;
  color: #fff;
}

.alert-success {
  background-color: #28a745;
  border-color: #28a745;
}

.alert-danger {
  background-color: #dc3545;
  border-color: #dc3545;
}

.alert-warning {
  background-color: #ffc107;
  border-color: #ffc107;
  color: #1a1a1a;
}

.alert i {
  vertical-align: middle;
  margin-right: 8px;
}

.alert-dismissible .btn-close {
  padding: 12px;
  filter: invert(1);
}

@media (max-width: 768px) {
  .container {
    min-height: 650px;
  }

  .sign-in-container,
  .sign-up-container,
  .overlay-container {
    width: 100%;
  }

  .overlay-container {
    display: none;
  }

  .sign-up-container {
    opacity: 1;
    z-index: 5;
  }

  .container.right-panel-active .sign-in-container {
    transform: translateY(-110%);
  }

  .container.right-panel-active .sign-up-container {
    transform: translateY(0);
  }

  .alert {
    max-width: 100%;
    font-size: 0.85rem;
  }
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
