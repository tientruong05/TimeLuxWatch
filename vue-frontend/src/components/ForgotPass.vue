<template>
  <div class="bg">
    <div class="logo-header">
      <router-link to="/" class="logo-link">
        <svg class="logo" width="60" height="60" viewBox="0 0 80 80">
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
          <i v-if="isSubmitting" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-key"></i>
          {{ isSubmitting ? "Đang xử lý..." : "Lấy lại mật khẩu" }}
        </button>
        <div class="message" :class="messageClass" v-if="message">
          {{ message }}
          <button
            v-if="message"
            class="message-close"
            @click="message = ''"
            aria-label="Đóng thông báo"
          >
            <i class="fas fa-times"></i>
          </button>
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
import { useRouter } from "vue-router";
import apiClient from "@/services/api";

const router = useRouter();
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

    // Chuyển hướng về trang đăng nhập sau 3 giây với thông báo
    setTimeout(() => {
      router.push({
        path: "/login",
        query: {
          reset: "success",
          message: encodeURIComponent(message.value),
        },
      });
    }, 3000);
  } catch (error) {
    console.error("Forgot password error:", error);
    message.value =
      error.response?.data?.error || "Có lỗi xảy ra, vui lòng thử lại sau.";
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
  min-height: 100vh;
  padding: 20px;
}

.logo-header {
  margin-bottom: 25px;
  text-align: center;
}

.logo-link {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.logo-link:hover {
  transform: scale(1.05);
  opacity: 0.9;
}

.logo {
  width: 60px;
  height: 60px;
  filter: drop-shadow(0 3px 5px rgba(0, 0, 0, 0.4));
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: #d4af37;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
}

.page-title {
  color: #d4af37;
  font-size: 2.2rem;
  font-weight: 400;
  margin-bottom: 30px;
  text-align: center;
  letter-spacing: 1.5px;
  position: relative;
}

.page-title::after {
  content: "";
  display: block;
  width: 50px;
  height: 2px;
  background: #d4af37;
  margin: 12px auto;
  border-radius: 1px;
}

.container {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
  padding: 30px;
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
  border: 1px solid rgba(212, 175, 55, 0.1);
}

form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-group {
  position: relative;
}

label {
  display: block;
  margin-bottom: 6px;
  color: #2d2d2d;
  font-weight: 600;
  font-size: 0.85rem;
  letter-spacing: 0.3px;
  text-transform: uppercase;
}

input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid rgba(212, 175, 55, 0.3);
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.85);
  font-size: 0.95rem;
  color: #333;
  transition: border-color 0.3s ease, box-shadow 0.3s ease,
    background-color 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #d4af37;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(212, 175, 55, 0.15);
}

input::placeholder {
  color: #999;
  font-style: italic;
}

button {
  background: linear-gradient(135deg, #d4af37, #b4941e);
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 12px;
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: 0.5px;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease, background 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
}

button:hover:not(:disabled) {
  background: linear-gradient(135deg, #e0bc4f, #c0a32f);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.3);
}

button:disabled {
  background: #999;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

button i {
  margin-right: 6px;
}

.form-footer {
  text-align: center;
  margin-top: 15px;
  font-size: 0.85rem;
}

.form-footer a {
  color: #d4af37;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease, text-decoration 0.3s ease;
}

.form-footer a:hover {
  color: #e0bc4f;
  text-decoration: underline;
}

.message {
  position: relative;
  text-align: left;
  margin-top: 12px;
  font-size: 0.85rem;
  font-weight: 500;
  padding: 10px 35px 10px 12px;
  border-radius: 6px;
  opacity: 0;
  animation: fadeIn 0.3s ease forwards;
}

.message-close {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: inherit;
  font-size: 0.8rem;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.message-close:hover {
  opacity: 1;
}

@keyframes fadeIn {
  to {
    opacity: 1;
  }
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

@media (max-width: 576px) {
  .bg {
    padding: 15px;
  }

  .logo {
    width: 50px;
    height: 50px;
  }

  .logo-text {
    font-size: 1.2rem;
  }

  .page-title {
    font-size: 1.8rem;
    margin-bottom: 20px;
  }

  .page-title::after {
    width: 40px;
    margin: 10px auto;
  }

  .container {
    padding: 20px;
    max-width: 100%;
    margin: 10px;
  }

  input {
    padding: 10px 12px;
    font-size: 0.9rem;
  }

  button {
    padding: 10px;
    font-size: 0.9rem;
  }

  .form-footer {
    font-size: 0.8rem;
  }

  .message {
    font-size: 0.8rem;
    padding: 8px 30px 8px 10px;
  }

  .message-close {
    font-size: 0.75rem;
  }
}

@media (max-width: 400px) {
  .logo-text {
    font-size: 1rem;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .container {
    padding: 15px;
  }

  input {
    font-size: 0.85rem;
  }

  button {
    font-size: 0.85rem;
  }
}
</style>
