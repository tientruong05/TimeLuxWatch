<template>
  <div class="logout-container">
    <div class="card">
      <div class="card-body text-center">
        <div
          class="spinner-border text-primary mb-4"
          role="status"
          v-if="isLoggingOut"
        >
          <span class="visually-hidden">Loading...</span>
        </div>
        <h3 class="mb-4">{{ message }}</h3>
        <p>
          Bạn sẽ được chuyển hướng về trang chủ trong {{ countdown }} giây...
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth"; // Import Pinia store

export default {
  setup() {
    const router = useRouter();
    const authStore = useAuthStore(); // Khởi tạo store
    const isLoggingOut = ref(true);
    const message = ref("Đang đăng xuất...");
    const countdown = ref(3);

    const performLogout = async () => {
      try {
        // Sử dụng authStore để đăng xuất
        const result = await authStore.logout();
        message.value = result.message;
      } catch (error) {
        console.error("Error during logout:", error);
        message.value = "Đăng xuất thành công!"; // Vẫn hiển thị thành công kể cả khi API bị lỗi
      } finally {
        isLoggingOut.value = false;

        // Bắt đầu đếm ngược để chuyển hướng
        const timer = setInterval(() => {
          countdown.value -= 1;
          if (countdown.value <= 0) {
            clearInterval(timer);
            // Sử dụng window.location thay vì router.push để buộc tải lại ứng dụng
            window.location.href = "/";
          }
        }, 1000);
      }
    };

    onMounted(() => {
      performLogout();
    });

    return {
      isLoggingOut,
      message,
      countdown,
    };
  },
};
</script>

<style scoped>
.logout-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
  padding: 2rem;
}

.card {
  width: 100%;
  max-width: 500px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 3rem;
}

h3 {
  color: #d4af37;
  font-weight: 300;
}
</style>
