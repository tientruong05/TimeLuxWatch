<template>
  <div class="section">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card">
            <div class="card-body">
              <h3 class="title text-center">Đổi mật khẩu</h3>
              <div v-if="successMessage" class="success-message">
                {{ successMessage }}
              </div>
              <div v-if="errorMessage" class="error-message text-center mt-3">
                {{ errorMessage }}
              </div>
              <div class="form-group">
                <input
                  class="form-control"
                  type="password"
                  v-model="currentPassword"
                  placeholder="Mật khẩu hiện tại"
                  required
                />
                <i class="fas fa-lock"></i>
                <div v-if="errors.currentPassword" class="error-message">
                  {{ errors.currentPassword }}
                </div>
              </div>
              <div class="form-group">
                <input
                  class="form-control"
                  type="password"
                  v-model="newPassword"
                  minlength="6"
                  maxlength="30"
                  placeholder="Mật khẩu mới"
                  required
                />
                <i class="fas fa-key"></i>
                <div v-if="errors.newPassword" class="error-message">
                  {{ errors.newPassword }}
                </div>
              </div>
              <div class="form-group">
                <input
                  class="form-control"
                  type="password"
                  v-model="confirmPassword"
                  placeholder="Xác nhận mật khẩu mới"
                  required
                />
                <i class="fas fa-check-circle"></i>
                <div v-if="errors.confirmPassword" class="error-message">
                  {{ errors.confirmPassword }}
                </div>
              </div>
              <button @click="changePassword" class="primary-btn w-100">
                Đổi mật khẩu
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  name: "PasswordChange",
  data() {
    return {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
      successMessage: "",
      errorMessage: "",
      errors: {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
    };
  },
  methods: {
    validateForm() {
      let isValid = true;
      this.errors = {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      };
      this.errorMessage = "";
      this.successMessage = "";

      if (!this.currentPassword) {
        this.errors.currentPassword = "Vui lòng nhập mật khẩu hiện tại";
        isValid = false;
      }

      if (!this.newPassword) {
        this.errors.newPassword = "Vui lòng nhập mật khẩu mới";
        isValid = false;
      } else if (this.newPassword.length < 6) {
        this.errors.newPassword = "Mật khẩu phải có ít nhất 6 ký tự";
        isValid = false;
      } else if (/\s{6,}/.test(this.newPassword)) {
        this.errors.newPassword =
          "Mật khẩu không được chứa 6 dấu cách liên tiếp";
        isValid = false;
      }

      if (!this.confirmPassword) {
        this.errors.confirmPassword = "Vui lòng xác nhận mật khẩu mới";
        isValid = false;
      } else if (this.confirmPassword !== this.newPassword) {
        this.errors.confirmPassword = "Xác nhận mật khẩu không khớp";
        isValid = false;
      }

      return isValid;
    },
    async changePassword() {
      if (!this.validateForm()) return;

      try {
        const formData = new URLSearchParams();
        formData.append("currentPassword", this.currentPassword);
        formData.append("newPassword", this.newPassword);
        formData.append("confirmPassword", this.confirmPassword);

        const response = await apiClient.post(
          "/changepass/changePassword",
          formData,
          {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
          }
        );

        this.successMessage =
          response.data.message || "Đổi mật khẩu thành công!";
        this.errorMessage = "";
        this.currentPassword = "";
        this.newPassword = "";
        this.confirmPassword = "";
      } catch (error) {
        console.error("Change password error:", error.response || error);
        this.successMessage = "";
        if (
          error.response &&
          error.response.data &&
          error.response.data.error
        ) {
          this.errorMessage = error.response.data.error;
        } else if (error.response && error.response.status === 401) {
          this.errorMessage =
            "Phiên đăng nhập hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.";
        } else {
          this.errorMessage =
            "Có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại.";
        }
      }
    },
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  background: white;
  color: #ffffff;
  min-height: 100vh;
  align-items: center;
}

.section {
  width: 100%;
  padding: 40px 0;
}

.card {
  background: linear-gradient(135deg, #000000, #1a1a1a);
  border: 1px solid rgba(201, 165, 92, 0.1) !important;
  backdrop-filter: blur(10px) !important;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2) !important;
  border-radius: 15px !important;
  transition: transform 0.3s ease, box-shadow 0.3s ease !important;
}

.card:hover {
  transform: translateY(-5px) !important;
  box-shadow: 0 20px 40px rgba(201, 165, 92, 0.15) !important;
}

.card-body {
  padding: 40px;
}

.title {
  color: rgba(201, 165, 92);
  font-weight: 300 !important;
  letter-spacing: 2px !important;
  margin-bottom: 30px !important;
  text-transform: uppercase !important;
}

.form-group {
  position: relative;
  margin-bottom: 25px;
}

.form-control {
  background: rgba(255, 255, 255, 0.05);
  border: none;
  border-bottom: 2px solid rgba(201, 165, 92, 0.3);
  border-radius: 0;
  color: white;
  padding: 15px 15px 15px 45px;
  transition: all 0.3s ease;
}

.form-control:focus {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border-bottom-color: #c9a55c;
  color: white;
}

.form-group i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #e3c388;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.form-control:focus + i {
  opacity: 1;
  transform: translateY(-50%) scale(1.1);
}

.primary-btn {
  background: linear-gradient(45deg, #e3c388, #e3c388);
  color: #1a1a1a;
  border: none;
  padding: 15px 30px;
  border-radius: 30px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.primary-btn:before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: 0.5s;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px #e74c3c;
  color: #1a1a1a;
}

.primary-btn:hover:before {
  left: 100%;
}

.error-message {
  color: #ff4444;
  font-size: 0.8em;
  margin-top: 5px;
  text-align: left;
}

.success-message {
  color: #c9a55c;
  font-size: 0.9em;
  margin-top: 20px;
  text-align: center;
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.5s forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

::placeholder {
  color: #bfbfbf !important;
  opacity: 0.8;
}
</style>
