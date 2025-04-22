<template>
  <div class="container py-5">
    <h2 class="mb-4">Giỏ hàng của bạn</h2>

    <div v-if="error" class="alert alert-danger">
      <p>{{ error }}</p>
    </div>

    <div class="toast-container" ref="toastContainer"></div>

    <div class="row">
      <div class="col-lg-12">
        <div class="cart-container">
          <div class="cart-header">
            <div class="row align-items-center">
              <div class="col-1 text-center">Chọn</div>
              <div class="col-4">Sản phẩm</div>
              <div class="col-2 text-center">Số lượng</div>
              <div class="col-3 text-center">Thành tiền</div>
              <div class="col-2 text-center">Hành động</div>
            </div>
          </div>

          <div v-if="cartItems && cartItems.length > 0">
            <div
              v-for="item in cartItems"
              :key="item.id"
              class="cart-item"
              :class="{ 'out-of-stock': item.availableQty === 0 }"
              :data-item-id="item.id"
              :data-discounted-price="item.discountedPrice"
              :data-original-price="item.price"
              :data-qty="item.qty"
              :data-stock="item.availableQty"
            >
              <div class="row align-items-center g-3">
                <div class="col-1 text-center">
                  <input
                    type="checkbox"
                    class="form-check-input item-checkbox"
                    :value="item.id"
                    :data-price="item.discountedPrice * item.qty"
                    @change="updateTotal"
                    :disabled="item.availableQty === 0"
                  />
                </div>
                <div class="col-4">
                  <div class="d-flex align-items-center">
                    <img
                      :src="getFirstImageUrl(item.image)"
                      class="product-image me-3"
                      :alt="item.productName"
                      @error="setDefaultImage"
                    />
                    <h5 class="product-title">{{ item.productName }}</h5>
                  </div>
                </div>
                <div class="col-2 text-center">
                  <div class="d-flex align-items-center justify-content-center">
                    <button
                      class="btn btn-quantity"
                      :disabled="item.availableQty === 0"
                      @click="updateQuantity(item.id, false)"
                    >
                      -
                    </button>
                    <input
                      type="number"
                      class="form-control quantity-input"
                      v-model.number="item.qty"
                      :max="item.availableQty"
                      min="1"
                      :disabled="item.availableQty === 0"
                      @change="updateCartItem(item.id, item.qty)"
                      :placeholder="item.availableQty === 0 ? 'Hết hàng' : ''"
                    />
                    <button
                      class="btn btn-quantity"
                      :disabled="item.availableQty === 0"
                      @click="updateQuantity(item.id, true)"
                    >
                      +
                    </button>
                  </div>
                  <small
                    v-if="item.qty > item.availableQty"
                    class="text-danger"
                  >
                    Chỉ còn {{ item.availableQty }} sản phẩm
                  </small>
                </div>
                <div class="col-3 text-end">
                  <p class="product-price mb-0">
                    <span v-if="item.discountedPrice < item.price">
                      <span class="discount-price">
                        {{ formatCurrency(item.discountedPrice * item.qty) }}
                      </span>
                      <span class="original-price">
                        {{ formatCurrency(item.price * item.qty) }}
                      </span>
                    </span>
                    <span v-else>
                      {{ formatCurrency(item.price * item.qty) }}
                    </span>
                  </p>
                </div>
                <div class="col-2 text-center">
                  <button class="btn-remove" @click="removeCartItem(item.id)">
                    <i class="fas fa-trash"></i> Xóa
                  </button>
                </div>
              </div>
            </div>

            <div class="cart-footer mt-4">
              <div class="row align-items-center">
                <div class="col">
                  <div class="form-check">
                    <input
                      type="checkbox"
                      class="form-check-input"
                      id="selectAll"
                      @change="toggleAll"
                    />
                    <label class="form-check-label" for="selectAll"
                      >Chọn tất cả</label
                    >
                  </div>
                </div>
                <div class="col text-end">
                  <span class="me-3"
                    >Tổng tiền:
                    <strong id="totalAmount"
                      >{{ formatCurrency(totalAmount) }}
                    </strong></span
                  >
                  <button
                    class="btn btn-primary"
                    @click="proceedToCheckout"
                    id="checkoutBtn"
                    :disabled="!hasSelectedItems"
                  >
                    <i class="fas fa-lock me-2"></i>Thanh toán
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="text-center py-5">
            <p>Giỏ hàng trống</p>
            <div class="justify-content-between align-items-center m-2">
              <router-link to="/" class="btn-continue-shopping"
                >Tiếp Tục Mua Sắm</router-link
              >
            </div>
          </div>
        </div>
      </div>
    </div>

    <ConfirmationModal
      :show="showConfirmDeleteModal"
      title="Xác nhận xóa sản phẩm"
      message="Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?"
      confirmText="Xóa"
      cancelText="Hủy"
      @confirm="handleConfirmDelete"
      @cancel="handleCancelDelete"
    />
  </div>
</template>

<script>
import { Toast } from "bootstrap";
import apiClient from "@/services/api";
import { useAuthStore } from "@/stores/auth";
import ConfirmationModal from "./ConfirmationModal.vue";
import { getFirstImageUrl } from "@/utils/imageUtils";

export default {
  name: "ShoppingCart",
  components: {
    ConfirmationModal,
  },
  data() {
    return {
      cartItems: [],
      error: null,
      totalAmount: 0,
      selectedItems: [],
      hasSelectedItems: false,
      showConfirmDeleteModal: false,
      itemToDeleteId: null,
    };
  },
  mounted() {
    this.fetchCartData();
  },
  methods: {
    getFirstImageUrl,
    async fetchCartData() {
      this.error = null;
      try {
        const response = await apiClient.get("/cart/view");
        this.cartItems = response.data.cartItems || [];
        this.updateTotal();
      } catch (error) {
        console.error("Error fetching cart data:", error);
        if (error.response && error.response.status === 401) {
          this.error = "Vui lòng đăng nhập để xem giỏ hàng.";
        } else {
          this.error = "Không thể tải giỏ hàng. Vui lòng thử lại sau.";
        }
        this.cartItems = [];
      }
    },
    showToast(message, type = "error") {
      const toastContainer = this.$refs.toastContainer;
      if (!toastContainer) return;
      const toastId = "toast-" + Date.now();
      const toastEl = document.createElement("div");
      toastEl.id = toastId;
      const bgColorClass = type === "error" ? "bg-danger" : "bg-success";
      toastEl.className = `toast align-items-center text-white border-0 fade ${bgColorClass}`;
      toastEl.setAttribute("role", "alert");
      toastEl.setAttribute("aria-live", "assertive");
      toastEl.setAttribute("aria-atomic", "true");

      const iconClass =
        type === "error" ? "fas fa-exclamation-circle" : "fas fa-check-circle";

      toastEl.innerHTML = `
          <div class="d-flex">
            <div class="toast-body">
              <i class="${iconClass} me-2"></i>
              ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
          </div>
        `;

      toastContainer.appendChild(toastEl);
      const toast = new Toast(toastEl, { delay: 3500, autohide: true });
      toast.show();

      toastEl.addEventListener("hidden.bs.toast", () => {
        toastEl.remove();
      });
    },
    updateQuantity(itemId, isIncrement) {
      const item = this.cartItems.find((item) => item.id === itemId);
      if (!item) return;

      let newQuantity = item.qty;
      if (isIncrement) {
        newQuantity++;
      } else if (item.qty > 1) {
        newQuantity--;
      } else {
        return;
      }
      item.qty = newQuantity;
      this.updateCartItem(itemId, newQuantity);
    },
    async updateCartItem(itemId, quantity) {
      const item = this.cartItems.find((item) => item.id === itemId);
      if (!item) return;

      const oldQuantity = item.qty;
      item.qty = quantity;
      this.updateTotal();

      try {
        const formData = new URLSearchParams();
        formData.append("itemId", itemId);
        formData.append("quantity", quantity);

        const response = await apiClient.post("/cart/update", formData, {
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
        });

        await this.fetchCartData();

        // Update the cart count in the auth store
        const authStore = useAuthStore();
        const newCount = this.cartItems.reduce(
          (sum, item) => sum + item.qty,
          0
        );
        authStore.updateCartCount(newCount);
        console.log("Cart count updated after quantity change:", newCount);

        this.showToast("Cập nhật số lượng thành công", "success");
      } catch (error) {
        console.error("Error updating cart item:", error);
        item.qty = oldQuantity;
        this.updateTotal();
        const errorMsg =
          error.response?.data?.error || "Có lỗi xảy ra khi cập nhật số lượng!";
        this.showToast(errorMsg.replace("error:", "").trim());
        await this.fetchCartData();
      }
    },
    async removeCartItem(itemId) {
      this.itemToDeleteId = itemId;
      this.showConfirmDeleteModal = true;
    },
    async handleConfirmDelete() {
      if (!this.itemToDeleteId) return;

      const itemId = this.itemToDeleteId;
      this.showConfirmDeleteModal = false;
      this.itemToDeleteId = null;

      try {
        await apiClient.post(`/cart/remove/${itemId}`);
        await this.fetchCartData();

        // Update the cart count in the auth store
        const authStore = useAuthStore();
        const newCount = this.cartItems.reduce(
          (sum, item) => sum + item.qty,
          0
        );
        authStore.updateCartCount(newCount);
        console.log("Cart count updated after item removal:", newCount);

        this.showToast("Đã xóa sản phẩm khỏi giỏ hàng", "success");
      } catch (error) {
        console.error("Error removing cart item:", error);
        this.showToast("Có lỗi xảy ra khi xóa sản phẩm!");
      }
    },
    handleCancelDelete() {
      this.showConfirmDeleteModal = false;
      this.itemToDeleteId = null;
    },
    updateTotal() {
      const checkboxes = document.querySelectorAll(".item-checkbox:checked");
      this.selectedItems = Array.from(checkboxes).map((checkbox) =>
        parseInt(checkbox.value)
      );
      this.hasSelectedItems = this.selectedItems.length > 0;

      let total = 0;
      checkboxes.forEach((checkbox) => {
        const itemId = parseInt(checkbox.value);
        const item = this.cartItems.find((item) => item.id === itemId);
        if (item && item.availableQty > 0 && item.qty > 0) {
          total += item.discountedPrice * item.qty;
        } else if (item) {
          checkbox.checked = false;
        }
      });

      this.totalAmount = total;
      this.selectedItems = Array.from(
        document.querySelectorAll(".item-checkbox:checked")
      ).map((cb) => parseInt(cb.value));
      this.hasSelectedItems = this.selectedItems.length > 0;

      const allCheckboxes = document.querySelectorAll(
        ".item-checkbox:not(:disabled)"
      );
      const selectAllCheckbox = document.getElementById("selectAll");
      if (selectAllCheckbox) {
        selectAllCheckbox.checked =
          allCheckboxes.length > 0 &&
          this.selectedItems.length === allCheckboxes.length;
        selectAllCheckbox.indeterminate =
          this.selectedItems.length > 0 &&
          this.selectedItems.length < allCheckboxes.length;
      }
    },
    toggleAll(event) {
      const isChecked = event.target.checked;
      const checkboxes = document.querySelectorAll(
        ".item-checkbox:not(:disabled)"
      );
      checkboxes.forEach((checkbox) => {
        checkbox.checked = isChecked;
      });
      this.updateTotal();
    },
    async proceedToCheckout() {
      if (this.selectedItems.length === 0) {
        this.showToast("Vui lòng chọn ít nhất một sản phẩm để thanh toán!");
        return;
      }

      try {
        const response = await apiClient.post(
          "/cart/checkout",
          this.selectedItems
        );

        if (response.data.message === "Checkout data prepared") {
          console.log("Proceeding to checkout page with data:", response.data);
          this.$router.push("/checkout");
        } else {
          this.showToast(
            response.data.error || "Không thể chuẩn bị thanh toán."
          );
        }
      } catch (error) {
        console.error("Error during checkout preparation:", error);
        const errorMsg =
          error.response?.data?.error ||
          "Có lỗi xảy ra khi chuẩn bị thanh toán!";
        this.showToast(errorMsg);
      }
    },
    formatCurrency(value) {
      if (typeof value !== "number") {
        return "0 VNĐ";
      }
      return value.toLocaleString("vi-VN") + " VNĐ";
    },
    setDefaultImage(event) {
      event.target.src = "/placeholder.png";
    },
  },
};
</script>

<style scoped>
:root {
  --primary-color: #0b0b0b;
  --accent-color: #e74c3c;
  --text-color: #1a1a1a;
  --secondary-color: #f5f5f5;
}

body {
  font-family: "Roboto", "Arial", sans-serif;
  color: var(--text-color);
  background-color: var(--secondary-color);
  min-height: 100vh;
  align-items: center;
}

.cart-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  padding: 2rem;
  margin-bottom: 20px;
}

.cart-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
  font-weight: 600;
}

.cart-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  align-items: center;
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

.cart-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.product-title {
  font-size: 1.1rem;
  margin-bottom: 5px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.product-price {
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--accent-color);
  display: block; /* Thay đổi từ flex thành block để xuống dòng */
  text-align: right;
}

.product-price .discount-price {
  color: var(--accent-color);
  font-weight: 600;
}

.product-price .original-price {
  display: block; /* Đảm bảo giá gốc xuống dòng */
  text-decoration: line-through;
  color: #999;
  font-size: 0.9rem;
  font-weight: normal;
  margin-top: 4px; /* Khoảng cách nhỏ giữa hai giá */
}

.quantity-input {
  width: 60px;
  text-align: center;
  border-radius: 4px;
  border: 1px solid #ddd;
  padding: 0;
  height: 30px;
}

.btn-quantity {
  width: 30px;
  height: 30px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  border: 1px solid #ddd;
  background-color: #fff;
  transition: all 0.3s ease;
}

.btn-quantity:hover {
  background-color: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

.btn-remove {
  color: #dc3545;
  background: none;
  border: none;
  padding: 5px 10px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.btn-remove:hover {
  color: #c82333;
}

.cart-footer {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

#checkoutBtn {
  background-color: #c0392b;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  color: white;
  transition: all 0.3s ease;
}

#checkoutBtn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  opacity: 0.7;
}

#checkoutBtn:not(:disabled):hover {
  background-color: #a83224;
}

.form-check-input {
  cursor: pointer;
  width: 18px;
  height: 18px;
}

.form-check-label {
  cursor: pointer;
  padding-left: 5px;
}

.out-of-stock {
  opacity: 0.6;
  background-color: #f8f9fa;
}

.out-of-stock .quantity-input:disabled {
  background-color: #e9ecef;
  color: #dc3545;
  border-color: #dc3545;
  text-align: center;
  cursor: not-allowed;
}

.out-of-stock .btn-quantity:disabled {
  background-color: #e9ecef;
  border-color: #ced4da;
  color: #6c757d;
  cursor: not-allowed;
}

.out-of-stock .item-checkbox {
  display: none;
}

.col-3.text-end {
  text-align: right;
}

.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1050;
  width: auto; /* Allow toast to size based on content */
  min-width: 250px; /* Minimum width */
}

/* General Toast Styling */
.toast {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
  border-radius: 0.375rem; /* Bootstrap default border radius */
  opacity: 0.95; /* Slightly transparent */
  color: #fff; /* Default text color to white for contrast */
}

.toast .toast-body {
  padding: 0.75rem 1rem; /* Adjust padding as needed */
  font-weight: 500;
}

.toast .btn-close {
  box-shadow: none;
}

.toast.show {
  opacity: 0.95; /* Ensure opacity is set when shown */
}

/* Remove old header style */
/* .toast-header {
  font-weight: bold;
} */

@media (max-width: 992px) {
  .product-title {
    max-width: 150px;
  }
  .product-image {
    width: 60px;
    height: 60px;
  }
}

@media (max-width: 768px) {
  .cart-container {
    padding: 1rem;
  }
  .cart-header,
  .cart-item {
    font-size: 0.9rem;
  }
  .product-title {
    max-width: 100px;
  }
  .product-price {
    font-size: 1rem;
    text-align: center; /* Căn giữa trên mobile */
  }
  .quantity-input {
    width: 50px;
  }
  .btn-quantity {
    width: 25px;
    height: 25px;
  }
}

@media (max-width: 576px) {
  .cart-item .row {
    flex-direction: column;
    text-align: center;
  }
  .cart-item .col-auto,
  .cart-item .col {
    margin-bottom: 10px;
  }
  .product-title {
    max-width: 100%;
  }
  .product-price {
    text-align: center;
  }
  .btn-remove {
    margin-top: 10px;
  }
}
</style>
