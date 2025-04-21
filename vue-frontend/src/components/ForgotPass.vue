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

    <h2 class="page-title">Quên Mật Khẩu</h2>
    <div class="container">
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="username">Tài khoản</label>
          <input
            type="text"
            id="username"
            v-model="form.username"
            placeholder="Nhập tên tài khoản"
            required
          />
        </div>
        <div class="form-group">
          <label for="email">Email</label>
          <input
            type="email"
            id="email"
            v-model="form.email"
            placeholder="Nhập địa chỉ email"
            required
          />
        </div>
        <button type="submit" :disabled="isSubmitting">
          <i class="fas fa-key"></i>
          {{ isSubmitting ? "Đang xử lý..." : "Lấy lại mật khẩu" }}
        </button>
        <div class="message" :class="messageClass" v-if="message">
          {{ message }}
        </div>
        <div class="form-footer">
          <router-link to="/login">Quay lại đăng nhập</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import apiClient from "@/services/api";

const form = ref({
  username: "",
  email: "",
});

const message = ref("");
const isSubmitting = ref(false);
const messageClass = computed(() =>
  message.value.includes("thành công") || message.value.includes("đã được gửi")
    ? "success-message"
    : "error-message"
);

const submitForm = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;
  message.value = "";

  try {
    // Use form-urlencoded format as expected by the backend
    const formData = new URLSearchParams();
    formData.append("username", form.value.username);
    formData.append("email", form.value.email);

    const response = await apiClient.post(
      "/forgotpass/resetPassword",
      formData,
      {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      }
    );

    message.value =
      response.data.message || "Mật khẩu mới đã được gửi đến email của bạn.";
    form.value.username = "";
    form.value.email = "";
  } catch (error) {
    console.error("Forgot password error:", error);
    if (error.response && error.response.data && error.response.data.error) {
      message.value = error.response.data.error;
    } else {
      message.value = "Có lỗi xảy ra, vui lòng thử lại sau.";
    }
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.bg {
  background: linear-gradient(135deg, #1a1a1a, #2d2d2d);
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-family: "Montserrat", sans-serif;
  height: 100vh;
  color: #333333;
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

.page-title {
  color: #c9a55c;
  font-size: 2.5em;
  font-weight: 300;
  margin-bottom: 30px;
  text-align: center;
  letter-spacing: 2px;
  position: relative;
}
.page-title::after {
  content: "";
  display: block;
  width: 60px;
  height: 2px;
  background: #c9a55c;
  margin: 15px auto;
}
.container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 100%;
  max-width: 400px;
  margin: 2px auto;
}
form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.form-group {
  position: relative;
}
label {
  display: block;
  margin-bottom: 8px;
  color: #2d2d2d;
  font-weight: 500;
  font-size: 0.9em;
  letter-spacing: 0.5px;
}
input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid rgba(201, 165, 92, 0.3);
  border-radius: 8px;
  background-color: rgba(255, 255, 255, 0.9);
  font-size: 0.95em;
  transition: all 0.3s ease;
}
input:focus {
  outline: none;
  border-color: #c9a55c;
  box-shadow: 0 0 0 2px rgba(201, 165, 92, 0.2);
}
button {
  background: #c9a55c;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 15px;
  font-size: 1em;
  font-weight: 600;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
}
button:hover:not(:disabled) {
  background: #d4b36b;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(201, 165, 92, 0.3);
}
button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}
button i {
  margin-right: 8px;
}
.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 0.9em;
}
.form-footer a {
  color: #c9a55c;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}
.form-footer a:hover {
  color: #d4b36b;
}
.message {
  text-align: center;
  margin-top: 10px;
  font-size: 0.9em;
  font-weight: bold;
  padding: 10px;
  border-radius: 4px;
}
.error-message {
  color: #721c24;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}
.success-message {
  color: #155724;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
}
@media (max-width: 480px) {
  .container {
    margin: 20px;
    padding: 30px;
  }
  .page-title {
    font-size: 2em;
  }
}
</style>
