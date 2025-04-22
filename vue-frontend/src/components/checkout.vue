<template>
  <div class="container py-5">
    <h2 class="mb-4">Thanh toán</h2>

    <!-- Loading/Error States -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-warning" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div v-if="error" class="alert alert-danger" role="alert">
      {{ error }}
    </div>

    <div class="row" v-if="!loading && !error">
      <!-- Thông tin đặt hàng -->
      <div class="col-md-7">
        <div class="checkout-container">
          <h4>Thông tin giao hàng</h4>
          <form @submit.prevent="submitOrder" novalidate>
            <div class="mb-3">
              <label class="form-label">Họ tên</label>
              <input
                type="text"
                class="form-control"
                v-model="formData.fullName"
                :class="{ 'is-invalid': errors.fullName }"
                required
              />
              <div class="invalid-feedback" v-if="errors.fullName">
                {{ errors.fullName }}
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">Địa chỉ giao hàng</label>
              <textarea
                class="form-control"
                rows="3"
                v-model="formData.address"
                :class="{ 'is-invalid': errors.address }"
                required
              ></textarea>
              <div class="invalid-feedback" v-if="errors.address">
                {{ errors.address }}
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">Số điện thoại</label>
              <input
                type="tel"
                class="form-control"
                v-model="formData.phone"
                :class="{ 'is-invalid': errors.phone }"
                required
              />
              <div class="invalid-feedback" v-if="errors.phone">
                {{ errors.phone }}
              </div>
            </div>
            <!-- Thêm trường Ghi chú (tùy chọn) -->
            <div class="mb-3">
              <label class="form-label">Ghi chú</label>
              <textarea
                class="form-control"
                rows="2"
                v-model="formData.note"
                placeholder="Ghi chú thêm cho đơn hàng (tùy chọn)"
              ></textarea>
            </div>
          </form>
        </div>
      </div>

      <!-- Tổng quan đơn hàng -->
      <div class="col-md-5">
        <div class="order-summary">
          <h4>Đơn hàng của bạn</h4>
          <table class="table">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Số lượng</th>
                <th>Giá</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in checkoutItems" :key="item.id">
                <td>{{ item.productName }}</td>
                <td>{{ item.qty }}</td>
                <td>{{ formatCurrency(item.discountedPrice * item.qty) }}</td>
              </tr>
            </tbody>
          </table>
          <div class="summary-row">
            <span>Tổng tiền hàng:</span>
            <span>{{ formatCurrency(total) }}</span>
          </div>
          <div class="summary-row">
            <span>Phí vận chuyển:</span>
            <span>{{ formatCurrency(shippingFee) }}</span>
          </div>
          <div class="summary-row total">
            <span>Tổng thanh toán:</span>
            <span>{{ formatCurrency(total + shippingFee) }}</span>
          </div>
          <button
            @click="submitOrder"
            class="btn btn-place-order"
            :disabled="isSubmitting"
          >
            {{ isSubmitting ? "Đang xử lý..." : "Xác nhận đặt hàng" }}
          </button>
          <router-link to="/cart" class="btn btn-secondary mt-2 w-100"
            >Quay lại giỏ hàng</router-link
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

export default {
  name: "CheckOut",
  data() {
    return {
      loading: true,
      error: null,
      isSubmitting: false,
      checkoutItems: [],
      total: 0,
      shippingFee: 30000,
      formData: {
        fullName: "",
        address: "",
        phone: "",
        note: "",
      },
      errors: {
        fullName: "",
        address: "",
        phone: "",
      },
    };
  },
  setup() {
    const router = useRouter();
    return { router };
  },
  created() {
    this.fetchCheckoutData();
  },
  methods: {
    async fetchCheckoutData() {
      this.loading = true;
      this.error = null;
      try {
        const response = await apiClient.get("/cart/checkout");
        if (response.data) {
          this.checkoutItems = response.data.checkoutItems || [];
          this.total = response.data.total || 0;
          this.formData.fullName = response.data.fullName || "";
          this.formData.address = response.data.address || "";
          this.formData.phone = response.data.phone || "";

          if (this.checkoutItems.length === 0) {
            this.error =
              "Không có sản phẩm nào để thanh toán. Vui lòng quay lại giỏ hàng.";
          }
        } else {
          this.error = "Không thể tải dữ liệu thanh toán.";
          this.checkoutItems = [];
          this.total = 0;
        }
      } catch (error) {
        console.error("Error fetching checkout data:", error);
        if (error.response && error.response.status === 401) {
          this.error = "Vui lòng đăng nhập để tiếp tục thanh toán.";
          this.router.push({
            name: "LoginRegister",
            query: { redirect: "/checkout" },
          });
        } else if (
          error.response &&
          error.response.data &&
          error.response.data.error === "No checkout data found"
        ) {
          this.error =
            "Không tìm thấy dữ liệu thanh toán. Vui lòng quay lại giỏ hàng và chọn sản phẩm.";
        } else {
          this.error = "Có lỗi xảy ra khi tải dữ liệu thanh toán.";
        }
        this.checkoutItems = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },
    calculateTotal() {
      const itemsTotal = this.checkoutItems.reduce(
        (sum, item) => sum + item.discountedPrice * item.qty,
        0
      );
      return itemsTotal + this.shippingFee;
    },
    formatCurrency(value) {
      if (typeof value !== "number") {
        return "0 VNĐ";
      }
      return new Intl.NumberFormat("vi-VN").format(value) + " VNĐ";
    },
    validate() {
      let isValid = true;
      this.errors = { fullName: "", address: "", phone: "" };

      if (!this.formData.fullName.trim()) {
        this.errors.fullName = "Vui lòng nhập họ tên.";
        isValid = false;
      } else if (/\d/.test(this.formData.fullName)) {
        this.errors.fullName = "Họ tên không được chứa số.";
        isValid = false;
      }

      if (
        !this.formData.address.trim() ||
        this.formData.address.trim().length < 5
      ) {
        this.errors.address = "Vui lòng nhập địa chỉ hợp lệ (ít nhất 5 ký tự).";
        isValid = false;
      }

      const phoneRegex = /^0\d{9}$/;
      if (!phoneRegex.test(this.formData.phone.trim())) {
        this.errors.phone =
          "Vui lòng nhập số điện thoại hợp lệ (10 số, bắt đầu bằng 0).";
        isValid = false;
      }

      return isValid;
    },
    async submitOrder() {
      this.error = null;
      if (!this.validate() || this.isSubmitting) return;

      this.isSubmitting = true;

      try {
        const formData = new URLSearchParams();
        formData.append("fullName", this.formData.fullName);
        formData.append("address", this.formData.address);
        formData.append("phone", this.formData.phone);
        if (this.formData.note) {
          formData.append("note", this.formData.note);
        }

        const response = await apiClient.post("/cart/complete", formData, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        });

        if (response.data && response.data.orderId) {
          const authStore = useAuthStore();
          authStore.justCheckedOut = true;
          console.log("Checkout.vue: Setting justCheckedOut flag to true.");
          authStore.isAuthCheckPending = true;
          console.log("Checkout.vue: Setting isAuthCheckPending flag to true.");

          // Update cart count directly from the response
          if (
            response.data.cartCount !== undefined &&
            typeof response.data.cartCount === "number"
          ) {
            console.log(
              `Checkout.vue: Updating cart count directly from response: ${response.data.cartCount}`
            );
            authStore.updateCartCount(response.data.cartCount);
          } else {
            // Fallback if cartCount is not in response (should not happen now, but good practice)
            console.warn(
              "Checkout.vue: cartCount not found in response, setting to 0 as fallback."
            );
            authStore.updateCartCount(0);
            // Optionally, you could still call refreshCartCount here as a deeper fallback
            // await authStore.refreshCartCount();
          }

          setTimeout(() => {
            authStore.isAuthCheckPending = false;
            this.router.push({
              name: "OrderConfirmation",
              params: { id: response.data.orderId },
            });
          }, 500);
        } else {
          this.error = response.data.error || "Đặt hàng không thành công.";
        }
      } catch (error) {
        console.error("Error completing order:", error);
        if (error.response && error.response.status === 401) {
          this.error = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
          this.router.push({
            name: "LoginRegister",
            query: { redirect: "/checkout" },
          });
        } else {
          this.error =
            error.response?.data?.error || "Có lỗi xảy ra khi đặt hàng.";
        }
      } finally {
        this.isSubmitting = false;
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
.checkout-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  padding: 2rem;
  margin-bottom: 20px;
  transition: all 0.4s ease;
}
.form-label {
  font-weight: 500;
  color: #0b0b0b;
}
.order-summary {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}
.btn-place-order {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 0.9rem;
  width: 100%;
}
.btn-place-order:hover {
  background-color: #c0392b;
  transform: translateY(-3px);
}
.is-invalid {
  border-color: #dc3545;
}
.invalid-feedback {
  display: block;
  color: #dc3545;
  font-size: 0.875rem;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 1rem;
}
.summary-row.total {
  font-weight: bold;
  font-size: 1.1rem;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  color: #e74c3c;
}
.summary-row span:last-child {
  font-weight: 500;
}

.btn-place-order:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
</style>
