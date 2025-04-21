<template>
  <div class="section">
    <!-- Add a container for toast messages -->
    <div
      class="toast-container position-fixed top-0 end-0 p-3"
      ref="toastContainer"
    ></div>

    <div class="container" v-if="product">
      <div class="row">
        <div class="col-md-6">
          <div class="product-image">
            <div v-if="product.discountPercentage" class="discount-badge">
              <span>{{ product.discountPercentage.toFixed(0) }}%</span>
              <span>OFF</span>
            </div>
            <img :src="`/photos/${product.image}`" :alt="product.name" />
          </div>
        </div>

        <div class="col-md-6">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-code">Mã SP: {{ product.id }}</p>

          <p class="product-price">
            <span v-if="product.discountPercentage">
              <span class="discount-price">{{
                formatCurrency(product.discountedPrice)
              }}</span>
              <span class="original-price">{{
                formatCurrency(product.price)
              }}</span>
            </span>
            <span v-else>
              {{ formatCurrency(product.price) }}
            </span>
          </p>

          <div class="product-info">
            <p>
              <strong>Số lượng còn:</strong> <span>{{ product.qty }}</span>
            </p>
            <p>
              <strong>Mô tả:</strong> <span>{{ product.description }}</span>
            </p>
            <p>
              <strong>Trạng thái:</strong>
              <span
                :class="
                  product.qty > 0 ? 'stock-status-in' : 'stock-status-out'
                "
              >
                {{ product.qty > 0 ? "Còn hàng" : "Hết hàng" }}
              </span>
            </p>
          </div>

          <div class="d-flex gap-3 mt-4">
            <div class="input-group mb-3" style="max-width: 200px">
              <button
                class="btn btn-outline-secondary"
                type="button"
                @click="decrementQuantity"
                :disabled="product.qty <= 0"
              >
                -
              </button>
              <input
                type="number"
                class="form-control text-center"
                v-model.number="quantity"
                min="1"
                :max="product.qty"
                :disabled="product.qty <= 0"
                @change="validateQuantity"
              />
              <button
                class="btn btn-outline-secondary"
                type="button"
                @click="incrementQuantity"
                :disabled="product.qty <= 0"
              >
                +
              </button>
            </div>
            <div class="quantity-warning" v-if="showQuantityWarning">
              Số lượng không được vượt quá số lượng sản phẩm hiện có!
            </div>
          </div>

          <div class="d-flex gap-3">
            <button
              class="btn btn-buy"
              @click="buyNow"
              :disabled="product.qty <= 0"
            >
              Mua ngay
            </button>
            <button
              class="btn btn-cart"
              @click="addToCart"
              :disabled="product.qty <= 0"
            >
              Thêm vào giỏ
            </button>
          </div>

          <div class="delivery-info">
            <p class="mb-2">
              <strong>Giao hàng:</strong> Freeship Hà Nội & HCM
            </p>
            <p class="mb-0"><strong>Hotline:</strong> 093.934.8888</p>
          </div>

          <div class="social-share">
            <span class="text-muted">Chia sẻ:</span>
            <a href="#"><i class="bi bi-facebook"></i></a>
            <a href="#"><i class="bi bi-twitter"></i></a>
            <a href="#"><i class="bi bi-pinterest"></i></a>
          </div>
        </div>
      </div>

      <div class="related-products" v-if="relatedProducts.length">
        <h4>Đề xuất sản phẩm lựa chọn khác</h4>
        <div class="row">
          <div
            class="col-md-2"
            v-for="related in relatedProducts"
            :key="related.id"
          >
            <div class="related-product-item">
              <a :href="`/java5/asm/products/detail/${related.id}`">
                <img
                  :src="`/photos/${related.image}`"
                  :alt="related.name"
                  class="img-fluid"
                />
                <p class="related-product-title">{{ related.name }}</p>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Toast } from "bootstrap"; // Import Bootstrap Toast

export default {
  data() {
    return {
      product: null,
      relatedProducts: [],
      quantity: 1,
      showQuantityWarning: false,
    };
  },
  mounted() {
    const id = this.$route.params.id;
    fetch(`/java5/asm/products/api/${id}`)
      .then((res) => res.json())
      .then((data) => {
        this.product = data.product;
        this.relatedProducts = data.relatedProducts;
      });
  },
  methods: {
    formatCurrency(val) {
      return new Intl.NumberFormat("vi-VN").format(val) + " VNĐ";
    },
    validateQuantity() {
      if (this.quantity < 1) this.quantity = 1;
      if (this.quantity > this.product.qty) {
        this.quantity = this.product.qty;
        this.showQuantityWarning = true;
        setTimeout(() => (this.showQuantityWarning = false), 3000);
      }
    },
    incrementQuantity() {
      if (this.quantity < this.product.qty) {
        this.quantity++;
      } else {
        this.showQuantityWarning = true;
        setTimeout(() => (this.showQuantityWarning = false), 3000);
      }
    },
    decrementQuantity() {
      if (this.quantity > 1) {
        this.quantity--;
      }
      this.showQuantityWarning = false;
    },
    showToast(message, type = "success") {
      // Default to success for add to cart
      const toastContainer = this.$refs.toastContainer;
      if (!toastContainer) return;
      const toastId = "toast-" + Date.now();
      const toastEl = document.createElement("div");
      toastEl.id = toastId;
      toastEl.className = `toast align-items-center text-white bg-${
        type === "success" ? "success" : "danger"
      } border-0`;
      toastEl.setAttribute("role", "alert");
      toastEl.setAttribute("aria-live", "assertive");
      toastEl.setAttribute("aria-atomic", "true");

      toastEl.innerHTML = `
          <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
          </div>
        `;

      toastContainer.appendChild(toastEl);
      const toast = new Toast(toastEl, { delay: 3000 });
      toast.show();

      toastEl.addEventListener("hidden.bs.toast", () => {
        toastEl.remove();
      });
    },
    addToCart() {
      fetch(`/java5/asm/cart/add/${this.product.id}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `quantity=${this.quantity}`,
      })
        .then((response) => {
          if (response.status === 401) {
            window.location.href = `/java5/asm/login?redirectUrl=${encodeURIComponent(
              window.location.pathname
            )}`;
            return Promise.reject("Unauthorized");
          }
          return response.json();
        })
        .then((data) => {
          if (data.message === "success") {
            this.showToast("Thêm vào giỏ hàng thành công!", "success");
          } else {
            this.showToast(
              data.error || "Có lỗi xảy ra khi thêm vào giỏ!",
              "danger"
            );
          }
        })
        .catch((error) => {
          if (error !== "Unauthorized") {
            console.error("Add to cart error:", error);
            this.showToast("Lỗi kết nối hoặc lỗi không xác định!", "danger");
          }
        });
    },
    buyNow() {
      this.validateQuantity();
      this.addToCart();
    },
  },
};
</script>

<style scoped>
/* Import lại CSS từ file gốc */
@import url("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css");
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css");

/* Giữ nguyên phần style trong file HTML cũ */
/* (Bạn có thể dán lại toàn bộ CSS vào đây nếu cần) */
body {
  background-color: #f8f9fa;
  color: #212529;
  font-family: "Arial", sans-serif;
}
a {
  text-decoration: none !important;
}
.product-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 20px;
  line-height: 1.3;
}
.product-code {
  color: #6c757d;
  font-size: 14px;
  margin-bottom: 15px;
}
.product-price {
  font-size: 32px;
  color: #1a1a1a;
  font-weight: 600;
  margin: 20px 0;
  display: flex;
  align-items: center;
}
.product-price .discount-price {
  color: #e74c3c;
  font-size: 32px;
  font-weight: 600;
}
.product-price .original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 20px;
  font-weight: normal;
  margin-left: 12px;
}
.product-price .currency {
  font-size: 24px;
  margin-left: 4px;
}
.product-info {
  line-height: 2;
  margin-bottom: 12px;
  color: #4a4a4a;
}
.product-info strong {
  color: #1a1a1a;
  width: 120px;
  display: inline-block;
}
.btn-buy {
  background-color: #1a1a1a;
  color: white;
  padding: 12px 35px;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}
.btn-buy:hover {
  background-color: #333;
  color: white;
  transform: translateY(-2px);
}
.btn-buy:disabled {
  background-color: #999;
  transform: none;
  cursor: not-allowed;
}
.btn-cart {
  background-color: white;
  color: #1a1a1a;
  border: 2px solid #1a1a1a;
  padding: 12px 35px;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}
.btn-cart:hover {
  background-color: #f8f9fa;
  color: #1a1a1a;
}
.btn-cart:disabled {
  background-color: #f8f9fa;
  color: #999;
  border-color: #999;
  cursor: not-allowed;
}
.rating-stars {
  color: #1a1a1a;
  font-size: 18px;
  margin-bottom: 20px;
}
.product-image {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  display: block;
  width: 100%;
  height: 500px;
}
.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}
.related-products {
  margin-top: 60px;
  padding-top: 40px;
  border-top: 1px solid #eee;
}
.related-products h4 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 30px;
  text-align: center;
}
.related-product-item {
  padding: 15px;
  transition: all 0.3s ease;
  border-radius: 8px;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
}
.related-product-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}
.related-product-item img {
  border-radius: 6px;
  margin-bottom: 12px;
}
.related-product-title {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 16px;
  text-align: center;
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.related-product-price {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 16px;
}
.delivery-info {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-top: 30px;
}
.social-share {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
.social-share a {
  color: #1a1a1a;
  margin-right: 15px;
  font-size: 18px;
  text-decoration: none;
}
.social-share a:hover {
  color: #666;
}
.section {
  width: 100%;
  padding: 40px 0;
}
.discount-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #e74c3c;
  color: white;
  padding: 8px 12px;
  font-weight: bold;
  border-radius: 4px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  text-align: center;
  z-index: 10;
}
.discount-badge span:first-child {
  font-size: 1.2rem;
  font-weight: 700;
  line-height: 1;
}
.discount-badge span:last-child {
  font-size: 1rem;
  font-weight: 600;
}
.product-card:hover .discount-badge {
  transform: scale(1.05);
  transition: transform 0.3s ease;
}
.stock-status-out {
  color: #e74c3c;
  font-weight: bold;
}
.stock-status-in {
  color: #2ecc71;
  font-weight: bold;
}
.quantity-warning {
  color: #e74c3c;
  font-size: 13px;
  margin-top: 5px;
  display: none;
}
/* Add styles for the toast container */
.toast-container {
  z-index: 1055; /* Ensure toast appears above other elements */
}

/* Optional: Adjust toast appearance if needed */
.toast {
  /* Add custom toast styles here if default Bootstrap isn't sufficient */
}
</style>
