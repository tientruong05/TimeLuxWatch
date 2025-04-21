<template>
  <div class="wrapper">
    <div class="page-header">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-12">
            <h1 class="page-title">Đơn Hàng Của Bạn</h1>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="row mb-4">
        <div class="col-md-3" v-for="(box, index) in summary" :key="index">
          <div class="order-summary">
            <div class="summary-item">
              <div class="summary-icon">
                <i :class="box.icon"></i>
              </div>
              <div class="summary-text">
                <div class="summary-label">{{ box.label }}</div>
                <div class="summary-value">{{ box.value }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="table-wrapper">
        <table class="table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Khách Hàng</th>
              <th>Địa Chỉ</th>
              <th>Số Điện Thoại</th>
              <th>Thành Tiền</th>
              <th>Ngày Đặt Hàng</th>
              <th>Chi Tiết</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(order, index) in orders" :key="order.id">
              <td>{{ index + 1 }}</td>
              <td class="customer-name">{{ order.fullName }}</td>
              <td>{{ order.address }}</td>
              <td>{{ order.phone }}</td>
              <td class="total-amount">
                {{ formatCurrency(order.totalAmount + 30000) }}
              </td>
              <td class="order-date">{{ formatDate(order.orderDate) }}</td>
              <td>
                <button class="btn-details" @click="openModal(order.id)">
                  <i class="fas fa-eye"></i> Chi tiết
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="d-flex justify-content-between align-items-center m-2">
        <router-link to="/" class="btn-continue-shopping">
          <i class="fas fa-arrow-left"></i> Tiếp Tục Mua Sắm
        </router-link>
      </div>

      <!-- Modal -->
      <div
        class="modal fade show d-block"
        tabindex="-1"
        role="dialog"
        v-if="selectedOrder"
      >
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">
                Chi Tiết Đơn Hàng #{{ selectedOrder.id }}
              </h5>
              <button
                type="button"
                class="btn-close"
                @click="selectedOrder = null"
              ></button>
            </div>
            <div class="modal-body">
              <div class="row mb-4">
                <div class="col-md-6">
                  <h6>Thông Tin Khách Hàng</h6>
                  <p><strong>Tên:</strong> {{ selectedOrder.fullName }}</p>
                  <p><strong>Địa chỉ:</strong> {{ selectedOrder.address }}</p>
                  <p>
                    <strong>Số điện thoại:</strong> {{ selectedOrder.phone }}
                  </p>
                </div>
                <div class="col-md-6">
                  <h6>Thông Tin Đơn Hàng</h6>
                  <p>
                    <strong>Ngày đặt:</strong>
                    {{ formatDate(selectedOrder.orderDate) }}
                  </p>
                  <p>
                    <strong>Trạng thái:</strong>
                    <span
                      class="badge"
                      :class="getStatusBadge(selectedOrder.orderDate)"
                    >
                      {{ getStatusText(selectedOrder.orderDate) }}
                    </span>
                  </p>
                </div>
              </div>

              <h6 class="order-summary-title">Chi Tiết Sản Phẩm</h6>
              <div class="table-responsive">
                <table class="table table-striped">
                  <thead>
                    <tr>
                      <th>Ảnh</th>
                      <th>Tên sản phẩm</th>
                      <th>Giá</th>
                      <th>Số lượng</th>
                      <th>Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in orderDetails" :key="item.productName">
                      <td><img :src="item.image" class="product-img" /></td>
                      <td>{{ item.productName }}</td>
                      <td>{{ formatCurrency(item.price) }}</td>
                      <td>{{ item.qty }}</td>
                      <td>{{ formatCurrency(item.price * item.qty) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="mt-4">
                <h6 class="order-summary-title">Tổng Kết</h6>
                <div class="order-summary-row">
                  <span>Tổng tiền hàng:</span>
                  <span>{{ formatCurrency(selectedOrder.totalAmount) }}</span>
                </div>
                <div class="order-summary-row">
                  <span>Phí vận chuyển:</span>
                  <span>{{ formatCurrency(30000) }}</span>
                </div>
                <div class="order-summary-row total">
                  <span>Tổng thanh toán:</span>
                  <span>{{
                    formatCurrency(selectedOrder.totalAmount + 30000)
                  }}</span>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn-modal-close" @click="selectedOrder = null">
                Đóng
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
  data() {
    return {
      orders: [],
      selectedOrder: null,
      orderDetails: [],
      error: null,
      summary: [
        { label: "Tổng đơn hàng", value: "0 đơn", icon: "fas fa-shopping-bag" },
        { label: "Đang giao", value: "0 đơn", icon: "fas fa-box" },
        { label: "Đã hoàn thành", value: "0 đơn", icon: "fas fa-check-circle" },
        { label: "Tổng chi tiêu", value: "0 VNĐ", icon: "fas fa-wallet" },
      ],
    };
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    async fetchOrders() {
      this.error = null;
      try {
        const response = await apiClient.get("/cart/orders");
        this.orders = response.data.orders || [];
        this.calculateSummary();
      } catch (error) {
        console.error("Error fetching orders:", error);
        if (error.response && error.response.status === 401) {
          this.error = "Vui lòng đăng nhập để xem đơn hàng.";
        } else {
          this.error = "Không thể tải danh sách đơn hàng.";
        }
        this.orders = [];
      }
    },
    formatCurrency(val) {
      if (typeof val !== "number") {
        return "0 VNĐ";
      }
      return new Intl.NumberFormat("vi-VN").format(val) + " VNĐ";
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
    getStatusBadge(dateString) {
      if (!dateString) return "bg-secondary";
      try {
        const diff = new Date().getTime() - new Date(dateString).getTime();
        return diff < 3 * 86400000 ? "bg-warning" : "bg-success";
      } catch (e) {
        return "bg-secondary";
      }
    },
    getStatusText(dateString) {
      if (!dateString) return "Không xác định";
      try {
        const diff = new Date().getTime() - new Date(dateString).getTime();
        return diff < 3 * 86400000 ? "Đang giao" : "Đã giao";
      } catch (e) {
        return "Trạng thái lỗi";
      }
    },
    async openModal(orderId) {
      this.error = null;
      try {
        const response = await apiClient.get(`/cart/order/detail/${orderId}`);
        if (response.data && response.data.order) {
          this.selectedOrder = response.data.order;
          this.orderDetails = response.data.order.orderDetails || [];
        } else {
          this.error = "Không tìm thấy chi tiết đơn hàng.";
          this.selectedOrder = null;
        }
      } catch (error) {
        console.error("Error fetching order details:", error);
        if (error.response && error.response.status === 401) {
          this.error = "Vui lòng đăng nhập để xem chi tiết đơn hàng.";
        } else {
          this.error = "Không thể tải chi tiết đơn hàng.";
        }
        this.selectedOrder = null;
      }
    },
    calculateSummary() {
      const now = new Date().getTime();
      const threeDaysInMillis = 3 * 86400000;
      const delivering = this.orders.filter(
        (o) =>
          o.orderDate &&
          now - new Date(o.orderDate).getTime() < threeDaysInMillis
      );
      const completed = this.orders.length - delivering.length;
      const totalSpent = this.orders.reduce(
        (sum, o) => sum + (o.totalAmount || 0) + 30000,
        0
      );
      this.summary[0].value = `${this.orders.length} đơn`;
      this.summary[1].value = `${delivering.length} đơn`;
      this.summary[2].value = `${completed} đơn`;
      this.summary[3].value = this.formatCurrency(totalSpent);
    },
  },
};
</script>

<style scoped>
.page-header {
  position: relative;
  height: 20vh;
  background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)),
    url("https://images.unsplash.com/photo-1533139502658-0198f920d8e8?ixlib=rb-4.0.3");
  background-size: cover;
  background-position: center;
  overflow: hidden;
  margin-bottom: 60px;
}

.page-title {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 3rem;
  font-weight: 300;
  letter-spacing: 3px;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  width: 100%;
}

/* Order Summary Cards */
.order-summary {
  border: none;
  border-radius: 15px;
  padding: 25px;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  background: white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  height: 100%;
}

.order-summary:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 20px;
}

.summary-icon {
  width: 50px;
  height: 50px;
  background: #fff1f0;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.summary-icon i {
  color: #d4af37;
  font-size: 1.5rem;
}

.summary-text {
  flex: 1;
}

.summary-label {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 5px;
}

.summary-value {
  color: #d4af37;
  font-size: 1.4rem;
  font-weight: 600;
}

/* Table Styling */
.table-wrapper {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;
}

.table {
  margin-bottom: 0;
}

.table th {
  background: #1a1a1a;
  color: white;
  font-weight: 500;
  letter-spacing: 1px;
  border: none;
  padding: 20px;
  font-size: 0.9rem;
  text-transform: uppercase;
}

.table td {
  vertical-align: middle;
  border-color: #f1f1f1;
  padding: 20px;
  color: #444;
}

.table tbody tr {
  transition: all 0.3s ease;
}

.table tbody tr:hover {
  background-color: #fff8f7;
}

/* Button Styling */
.btn-details {
  background-color: #d4af37;
  color: white;
  border: none;
  padding: 10px 25px;
  border-radius: 30px;
  transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
  text-decoration: none;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.btn-details:hover {
  background-color: #1a1a1a;
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(231, 76, 60, 0.2);
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

/* Breadcrumb Styling */
.breadcrumb {
  background: transparent;
  margin: 0;
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
}

.breadcrumb-item a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: color 0.3s ease;
  font-size: 0.95rem;
}

.breadcrumb-item a:hover {
  color: #d4af37;
}

.breadcrumb-item.active {
  color: #d4af37;
}

.breadcrumb-item + .breadcrumb-item::before {
  color: rgba(255, 255, 255, 0.6);
}

/* Custom Classes */
.order-id {
  font-weight: 600;
  color: #d4af37;
}

.customer-name {
  font-weight: 500;
  color: #1a1a1a;
}

.total-amount {
  font-weight: 600;
  color: #28a745;
}

.order-date {
  color: #6c757d;
}

@media (max-width: 768px) {
  .page-header {
    height: 30vh;
  }

  .page-title {
    font-size: 2rem;
  }

  .summary-value {
    font-size: 1.2rem;
  }

  .table-wrapper {
    overflow-x: auto;
  }

  .table th,
  .table td {
    padding: 15px;
    font-size: 0.85rem;
  }
}

.order-modal .modal-content {
  border: none;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.order-modal .modal-header {
  background-color: #1a1a1a;
  color: white;
  border-bottom: none;
  border-radius: 15px 15px 0 0;
  padding: 20px 30px;
}

.order-modal .modal-title {
  font-weight: 300;
  letter-spacing: 1px;
  font-size: 1.5rem;
}

.order-modal .modal-body {
  padding: 30px;
}

.order-modal .modal-footer {
  border-top: none;
  padding: 20px 30px;
}

.order-modal .btn-close {
  color: white;
  opacity: 0.8;
  filter: invert(1) grayscale(100%) brightness(200%);
}

.order-modal .btn-close:hover {
  opacity: 1;
}

.order-detail-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.order-detail-item:last-child {
  border-bottom: none;
}

.product-img {
  width: 80px;
  height: 80px;
  object-fit: contain;
  margin-right: 20px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-weight: 500;
  margin-bottom: 5px;
  font-size: 1.1rem;
}

.product-price {
  color: #d4af37;
  font-weight: 600;
}

.product-quantity {
  color: #6c757d;
  font-size: 0.9rem;
}

.order-summary-title {
  font-size: 1.2rem;
  font-weight: 500;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.order-summary-row.total {
  font-weight: 600;
  font-size: 1.1rem;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.btn-modal-close {
  background-color: #1a1a1a;
  color: white;
  border: none;
  padding: 10px 25px;
  border-radius: 30px;
  transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
  font-size: 0.9rem;
  font-weight: 500;
}

.btn-modal-close:hover {
  background-color: #d4af37;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}
</style>
