<template>
  <div class="section">
    <div class="container1">
      <div class="profile-card">
        <h2><i class="fas fa-user-circle me-2"></i> Thông tin cá nhân</h2>

        <!-- Loading/Error states -->
        <div v-if="loading" class="text-center my-4">
          <div class="spinner-border text-warning" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>

        <div v-else-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <!-- Profile Form -->
        <form @submit.prevent="updateProfile" v-if="!loading && !error">
          <div class="mb-3">
            <label for="username" class="form-label">Tên đăng nhập</label>
            <input
              type="text"
              class="form-control"
              id="username"
              v-model="user.username"
              readonly
            />
          </div>
          <div class="mb-3">
            <label for="fullname" class="form-label">Họ và tên</label>
            <input
              type="text"
              class="form-control"
              id="fullname"
              v-model="user.fullName"
              required
            />
          </div>
          <div class="mb-3">
            <label for="phonenumber" class="form-label">Số điện thoại</label>
            <input
              type="text"
              class="form-control"
              id="phonenumber"
              v-model="user.phone"
              required
            />
          </div>
          <div class="mb-3">
            <label for="address" class="form-label">Địa chỉ</label>
            <input
              type="text"
              class="form-control"
              id="address"
              v-model="user.address"
              required
            />
          </div>
          <button type="submit" class="btn btn-update" :disabled="updating">
            <i class="fas fa-save me-2"></i>
            {{ updating ? "Đang cập nhật..." : "Cập nhật" }}
          </button>
        </form>
        <p class="message-box" :style="{ color: messageColor }">
          {{ message }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api"; // Import apiClient service

export default {
  data() {
    return {
      user: {
        username: "",
        fullName: "",
        phone: "",
        address: "",
      },
      message: "",
      messageColor: "",
      loading: true,
      error: null,
      updating: false,
    };
  },
  mounted() {
    this.fetchUserData();
  },
  methods: {
    async fetchUserData() {
      this.loading = true;
      try {
        // Trước tiên thử debug session để xem thông tin
        try {
          const debugResponse = await apiClient.get("/account/debug-session");
          console.log("Session Debug:", debugResponse.data);
        } catch (e) {
          console.log("Could not debug session:", e);
        }

        // Gọi API profile chính
        const response = await apiClient.get("/account/profile");
        console.log("Profile response:", response.data);

        if (response.data && response.data.user) {
          this.user = response.data.user;
        } else {
          this.error =
            "Không thể tải thông tin người dùng. Vui lòng đăng nhập lại.";
        }
      } catch (error) {
        console.error("Error fetching user data:", error);

        // Chi tiết lỗi để debug
        if (error.response) {
          console.error("Error status:", error.response.status);
          console.error("Error data:", error.response.data);
        }

        // Xử lý lỗi 401 - Unauthorized
        if (error.response && error.response.status === 401) {
          this.error =
            "Phiên đăng nhập của bạn đã hết hạn hoặc chưa đăng nhập. Đang chuyển hướng tới trang đăng nhập...";
          this.messageColor = "#ff4444";

          // Redirect sau 2 giây để người dùng có thể đọc thông báo
          setTimeout(() => {
            this.$router.push({
              name: "LoginRegister",
              query: { redirect: "/profile" },
            });
          }, 2000);
        } else {
          this.error =
            "Có lỗi xảy ra khi tải thông tin người dùng. Vui lòng thử lại sau.";
          this.messageColor = "#ff4444";
        }
      } finally {
        this.loading = false;
      }
    },

    async updateProfile() {
      // Validation checks
      const fullname = this.user.fullName.trim();
      const phoneNumber = this.user.phone.trim();
      const address = this.user.address.trim();

      if (!/^[^0-9]+$/.test(fullname) || fullname === "") {
        this.message = "Họ và tên không hợp lệ!";
        this.messageColor = "#ff4444";
        return;
      }

      if (!/^\d{10}$/.test(phoneNumber)) {
        this.message = "Số điện thoại phải có đúng 10 chữ số!";
        this.messageColor = "#ff4444";
        return;
      }

      if (address === "") {
        this.message = "Địa chỉ không được để trống!";
        this.messageColor = "#ff4444";
        return;
      }

      this.updating = true;
      this.message = "";

      try {
        // Create form data
        const formData = new FormData();
        formData.append("username", this.user.username);
        formData.append("fullname", this.user.fullName);
        formData.append("phonenumber", this.user.phone);
        formData.append("address", this.user.address);

        // Send update request
        const response = await apiClient.post("/account/profile", formData);

        if (response.data && response.data.message) {
          this.message = response.data.message;
          this.messageColor = "#28a745";

          // Update the user data if the response contains updated user info
          if (response.data.user) {
            this.user = response.data.user;
          }

          // Clear message after a delay
          setTimeout(() => {
            this.message = "";
          }, 3000);
        }
      } catch (error) {
        console.error("Error updating profile:", error);

        if (
          error.response &&
          error.response.data &&
          error.response.data.error
        ) {
          this.message = error.response.data.error;
        } else {
          this.message = "Có lỗi xảy ra, vui lòng thử lại!";
        }
        this.messageColor = "#ff4444";
      } finally {
        this.updating = false;
      }
    },
  },
};
</script>

<style scoped>
body {
  background-color: #f5f5f5;
  color: #1c1c1c;
  font-family: "Poppins", sans-serif;
}
.container1 {
  max-width: 600px;
  padding: 40px;
  margin: 1px auto;
}
.profile-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
  padding: 30px;
}
.profile-card h2 {
  color: #c9a14c;
  font-weight: 500;
  text-transform: uppercase;
  text-align: center;
  margin-bottom: 20px;
}
.form-label {
  font-weight: 600;
}
.form-control {
  border-radius: 6px;
  padding: 12px;
  border: 1px solid #ddd;
}
.form-control:focus {
  border-color: #c9a14c;
  box-shadow: 0 0 5px rgba(201, 161, 76, 0.5);
}
.btn-update {
  background-color: #c9a14c;
  color: white;
  border: none;
  padding: 12px;
  width: 100%;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  text-transform: uppercase;
  transition: all 0.3s ease;
}
.btn-update:hover {
  background-color: #a8803b;
}
.btn-update:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
.message-box {
  text-align: center;
  font-weight: bold;
  margin-top: 10px;
  font-size: 16px;
  min-height: 24px;
}
.alert {
  margin-top: 20px;
  border-radius: 6px;
}
</style>
