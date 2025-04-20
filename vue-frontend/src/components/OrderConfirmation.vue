<template>
  <div class="confirmation-container">
    <h1>Đơn Hàng Đã Được Xác Nhận</h1>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    <div class="order-details" v-if="order">
      <p>
        Mã đơn hàng: <span>#{{ order.id }}</span>
      </p>
      <p>
        Tổng thanh toán:
        <span>{{ formatCurrency(order.totalAmount + 30000) }}</span>
      </p>
      <p>
        Ngày đặt hàng: <span>{{ formatDate(order.orderDate) }}</span>
      </p>
      <p>Cảm ơn bạn đã mua sắm!</p>
    </div>
    <div v-else-if="!error">
      <p>Đang tải thông tin đơn hàng...</p>
    </div>
    <div class="navigation-buttons mt-4">
      <router-link to="/users/orders" class="btn-continue-shopping">
        <i class="fas fa-list-alt me-2"></i> Xem đơn hàng
      </router-link>
      <router-link to="/" class="btn-continue ms-3">
        <i class="fas fa-home me-2"></i> Về trang chủ
      </router-link>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  props: ["id"],
  data() {
    return {
      order: null,
      error: null,
    };
  },
  mounted() {
    if (this.id) {
      this.fetchOrderDetails();
    } else {
      this.error = "Không tìm thấy ID đơn hàng.";
    }
  },
  methods: {
    async fetchOrderDetails() {
      this.error = null;
      try {
        const response = await apiClient.get(`/cart/order/detail/${this.id}`);
        if (response.data && response.data.order) {
          this.order = response.data.order;
        } else {
          this.error =
            "Không thể tải thông tin đơn hàng hoặc đơn hàng không tồn tại.";
        }
      } catch (err) {
        console.error("Error fetching order confirmation details:", err);
        if (err.response && err.response.status === 401) {
          this.error = "Vui lòng đăng nhập để xem xác nhận đơn hàng.";
        } else {
          this.error = "Lỗi khi tải thông tin đơn hàng.";
        }
        this.order = null;
      }
    },
    formatCurrency(value) {
      if (typeof value !== "number") {
        return "0 VNĐ";
      }
      return new Intl.NumberFormat("vi-VN").format(value) + " VNĐ";
    },
    formatDate(dateString) {
      if (!dateString) return "";
      try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
          return "Ngày không hợp lệ";
        }
        return date.toLocaleDateString("vi-VN");
      } catch (e) {
        console.error("Error formatting date:", e);
        return "Ngày lỗi";
      }
    },
  },
};
</script>

<style scoped>
body {
  font-family: "Roboto", "Arial", sans-serif;
  color: #1a1a1a;
  background-color: #f5f5f5;
}

.confirmation-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  padding: 2rem;
  margin: 50px auto;
  max-width: 800px;
  text-align: center;
}

h1 {
  color: #0b0b0b;
}

.order-details {
  font-size: 1.1rem;
  margin: 20px 0;
}

.order-details span {
  font-weight: bold;
  color: #e74c3c;
}

.btn-continue {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  display: inline-block;
  margin-top: 15px;
  text-decoration: none;
}

.btn-continue:hover {
  background: #c0392b;
  transform: translateY(-3px);
}

.btn-continue-shopping {
  background-color: #1a1a1a;
  color: white;
  border: none;
  padding: 15px 30px;
  border-radius: 30px;
  transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 0.9rem;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}

.btn-continue-shopping:hover {
  background-color: #d4af37;
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.navigation-buttons a {
  margin-right: 10px;
}

.navigation-buttons a:last-child {
  margin-right: 0;
}
</style>
