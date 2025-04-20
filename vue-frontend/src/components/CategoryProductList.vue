<template>
  <div>
    <!-- Toast Container -->
    <div class="toast-container">
      <div
        v-if="success"
        class="toast toast-success"
        role="alert"
        aria-live="assertive"
        aria-atomic="true"
      >
        <div class="toast-header">
          <i class="fas fa-check-circle"></i>
          <strong class="me-auto">Thành công</strong>
          <button
            type="button"
            class="btn-close"
            @click="success = null"
            aria-label="Close"
          ></button>
        </div>
        <div class="toast-body">{{ success }}</div>
      </div>
      <div
        v-if="error"
        class="toast toast-error"
        role="alert"
        aria-live="assertive"
        aria-atomic="true"
      >
        <div class="toast-header">
          <i class="fas fa-times-circle"></i>
          <strong class="me-auto">Lỗi</strong>
          <button
            type="button"
            class="btn-close"
            @click="error = null"
            aria-label="Close"
          ></button>
        </div>
        <div class="toast-body">{{ error }}</div>
      </div>
    </div>

    <section v-if="isFlashSaleActive" class="flash-sale-section">
      <div class="container">
        <h2>Flash Sale Đang Diễn Ra!</h2>
        <p>
          Thời gian còn lại: <span>{{ countdownText }}</span>
        </p>
      </div>
    </section>

    <div class="section">
      <div class="container py-4">
        <!-- Search & Filter Section -->
        <div class="search-filter-container">
          <form
            @submit.prevent="applyFilters"
            class="row g-3 align-items-center"
          >
            <!-- Search Input -->
            <div class="col-lg-5">
              <input
                type="text"
                class="form-control"
                id="searchInput"
                v-model="searchTerm"
                placeholder="Tìm kiếm trong danh mục..."
              />
            </div>
            <!-- Gender Filter -->
            <div class="col-lg-3">
              <select
                class="form-select"
                id="genderFilter"
                v-model="selectedGender"
              >
                <option value="">Chọn giới tính</option>
                <option value="male">Nam</option>
                <option value="female">Nữ</option>
              </select>
            </div>
            <!-- Price Filter -->
            <div class="col-lg-2">
              <select
                class="form-select"
                id="priceFilter"
                v-model="selectedPriceRange"
              >
                <option value="">Chọn mức giá</option>
                <option value="0-1000000">Dưới 1 triệu</option>
                <option value="1000000-3000000">1 - 3 triệu</option>
                <option value="3000000-5000000">3 - 5 triệu</option>
                <option value="5000000-10000000">5 - 10 triệu</option>
                <option value="10000000+">Trên 10 triệu</option>
              </select>
            </div>
            <!-- Filter Button -->
            <div class="col-lg-2">
              <button type="submit" class="btn btn-filter w-100">
                <i class="fas fa-filter me-2"></i>Lọc
              </button>
            </div>
          </form>
        </div>

        <!-- Loading/Error States -->
        <div v-if="loading" class="text-center my-5">
          <div class="spinner-border text-warning" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>
        <div v-if="error" class="alert alert-danger">{{ error }}</div>

        <!-- Product Display Area -->
        <div v-if="!loading && !error">
          <h2 class="section-title">{{ pageTitle }}</h2>
          <p class="result-count">Tìm thấy {{ totalItems }} sản phẩm</p>

          <div class="container" v-if="products.length > 0">
            <div class="product-grid">
              <div v-for="product in products" :key="product.id">
                <div class="product-card">
                  <div class="product-img-container">
                    <div
                      class="discount-badge"
                      v-if="product.discountPercentage"
                    >
                      <span>{{ Math.floor(product.discountPercentage) }}%</span>
                      <span>OFF</span>
                    </div>
                    <img
                      :src="'http://localhost:8080/photos/' + product.image"
                      class="product-img"
                      alt="Watch"
                    />
                    <router-link
                      class="quick-view-btn btn"
                      :to="{
                        name: 'ProductDetail',
                        params: { id: product.id },
                      }"
                      >Xem nhanh</router-link
                    >
                  </div>
                  <div class="product-info">
                    <h3 class="product-title">{{ product.name }}</h3>
                    <p class="product-price">
                      <span v-if="product.discountPercentage">
                        <span class="discount-price">
                          {{ formatPrice(product.discountedPrice) }} VNĐ
                        </span>
                        <br />
                        <span class="original-price">
                          {{ formatPrice(product.price) }} VNĐ
                        </span>
                      </span>
                      <span v-else>{{ formatPrice(product.price) }} VNĐ</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Pagination - Improved with Middle Ellipses -->
            <nav aria-label="Page navigation" v-if="totalPages > 1">
              <ul class="pagination">
                <!-- Previous Button -->
                <li class="page-item" :class="{ disabled: currentPage === 0 }">
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(currentPage - 1)"
                    aria-label="Previous"
                  >
                    <span aria-hidden="true">«</span>
                  </a>
                </li>

                <!-- Page Numbers and Ellipses -->
                <li
                  v-for="(pageNumber, index) in visiblePageNumbers"
                  :key="`page-${index}-${pageNumber}`"
                  class="page-item"
                  :class="{
                    active: currentPage === pageNumber,
                    disabled: pageNumber === '...',
                  }"
                >
                  <span v-if="pageNumber === '...'" class="page-link">...</span>
                  <a
                    v-else
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(pageNumber)"
                    >{{ pageNumber + 1 }}</a
                  >
                </li>

                <!-- Next Button -->
                <li
                  class="page-item"
                  :class="{ disabled: currentPage === totalPages - 1 }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(currentPage + 1)"
                    aria-label="Next"
                  >
                    <span aria-hidden="true">»</span>
                  </a>
                </li>
              </ul>
            </nav>
          </div>
          <div v-else class="text-center py-5">
            <p>Không tìm thấy sản phẩm nào phù hợp.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Toast } from "bootstrap";
import apiClient from "@/services/api";
import { watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";

export default {
  name: "CategoryProductList",
  props: {
    // Props can be defined here if using route params, but we use query for categoryId
  },
  data() {
    return {
      loading: true,
      products: [],
      pageTitle: "Danh mục sản phẩm",
      categoryId: null,
      // Remove brandName and subCategoryId if categoryId is the primary identifier

      // Filter data
      searchTerm: "",
      selectedPriceRange: "",
      selectedGender: "",

      // Pagination
      currentPage: 0,
      pageSize: 12,
      totalItems: 0,
      totalPages: 1,

      // Messages
      success: null,
      error: null,

      // Flash Sale
      isFlashSaleActive: false,
      flashSaleEndTime: null,
      countdownText: "",
      countdownInterval: null,
    };
  },
  computed: {
    visiblePageNumbers() {
      const total = this.totalPages; // Total number of pages
      const current = this.currentPage; // Current page (0-based index)
      const delta = 2; // How many pages to show around the current page (increased for smoother range)
      const range = [];
      const rangeWithDots = [];

      // If total pages are small (e.g., <= 5), show all pages without ellipses
      if (total <= 5) {
        for (let i = 0; i < total; i++) {
          range.push(i);
        }
        return range;
      }

      // Always include the first page
      range.push(0);

      // Calculate the range of pages around the current page
      const start = Math.max(1, current - delta);
      const end = Math.min(total - 2, current + delta);

      // Add pages around the current page
      for (let i = start; i <= end; i++) {
        range.push(i);
      }

      // Always include the last page
      if (total > 1) {
        range.push(total - 1);
      }

      // Ensure unique and sorted page numbers
      const uniqueSortedRange = [...new Set(range)].sort((a, b) => a - b);

      // Add ellipses where there are gaps
      let lastNum;
      for (const i of uniqueSortedRange) {
        if (lastNum !== undefined) {
          if (i - lastNum === 2) {
            // If the gap is exactly 1 page, just add that page
            rangeWithDots.push(lastNum + 1);
          } else if (i - lastNum > 2) {
            // If the gap is larger, add an ellipsis
            rangeWithDots.push("...");
          }
        }
        rangeWithDots.push(i);
        lastNum = i;
      }

      return rangeWithDots;
    },
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    return { route, router };
  },
  created() {
    this.updateStateFromRoute(this.route.query);
    this.loadProducts();
    // this.checkFlashSale(); // Keep or remove

    // Display success/error messages if passed in the URL
    this.success = this.route.query.success || null;
    this.error = this.route.query.error || null;
    if (this.success || this.error) {
      this.$nextTick(() => {
        this.showToasts();
      });
    }
  },
  mounted() {
    watch(
      () => this.route.query,
      (newQuery) => {
        console.log("Route query changed:", newQuery);
        this.updateStateFromRoute(newQuery);
        this.loadProducts();
      },
      { immediate: false, deep: true }
    );
  },
  methods: {
    updateStateFromRoute(query) {
      this.categoryId = query.categoryId || null;
      this.searchTerm = query.search || "";
      this.selectedPriceRange = query.priceRange || "";
      this.selectedGender = query.gender || "";
      this.currentPage = parseInt(query.page) || 0;
      this.pageSize = parseInt(query.size) || 12;
    },

    async loadProducts() {
      this.loading = true;
      this.error = null;
      this.products = [];

      const params = {
        categoryId: this.categoryId,
        search: this.searchTerm,
        priceRange: this.selectedPriceRange,
        gender: this.selectedGender,
        page: this.currentPage,
        size: this.pageSize,
      };

      Object.keys(params).forEach((key) => {
        if (params[key] === null || params[key] === "") {
          delete params[key];
        }
      });

      console.log("Loading products with params:", params);

      try {
        const response = await apiClient.get("/category/list-product", {
          params,
        });

        if (response.data) {
          this.products = response.data.products || [];
          this.totalItems = response.data.totalItems || 0;
          this.totalPages = response.data.totalPages || 1;
          this.currentPage = response.data.currentPage || 0;
          this.pageTitle =
            response.data.categoryName ||
            (this.categoryId
              ? `Danh mục ${this.categoryId}`
              : "Tất cả sản phẩm");

          if (this.products.length === 0 && this.totalItems === 0) {
            this.error =
              "Không tìm thấy sản phẩm nào phù hợp với tiêu chí lọc.";
          }
        } else {
          throw new Error("Invalid API response structure");
        }
      } catch (err) {
        console.error("Error loading products:", err);
        this.error = "Lỗi khi tải danh sách sản phẩm. Vui lòng thử lại.";
        if (err.response) {
          console.error("API Error Response:", err.response.data);
          this.error = err.response.data.message || this.error;
        }
        this.products = [];
        this.totalItems = 0;
        this.totalPages = 1;
        this.currentPage = 0;
        this.pageTitle = "Lỗi tải danh mục";
      } finally {
        this.loading = false;
      }
    },

    applyFilters() {
      this.currentPage = 0;
      this.updateRouterQuery();
    },

    goToPage(page) {
      if (page < 0 || page >= this.totalPages || page === this.currentPage)
        return;
      this.currentPage = page;
      this.updateRouterQuery();
      window.scrollTo(0, 0);
    },

    updateRouterQuery() {
      const query = { ...this.route.query };

      if (this.categoryId) query.categoryId = this.categoryId;
      else delete query.categoryId;
      if (this.searchTerm) query.search = this.searchTerm;
      else delete query.search;
      if (this.selectedPriceRange) query.priceRange = this.selectedPriceRange;
      else delete query.priceRange;
      if (this.selectedGender) query.gender = this.selectedGender;
      else delete query.gender;
      if (this.currentPage > 0) query.page = this.currentPage;
      else delete query.page;

      this.router.push({ query });
    },

    formatPrice(price) {
      if (typeof price !== "number") return "N/A";
      return new Intl.NumberFormat("vi-VN").format(price);
    },

    checkFlashSale() {
      // Implement actual flash sale check if needed
      // this.isFlashSaleActive = ...
      // this.flashSaleEndTime = ...
      // if (this.isFlashSaleActive) this.startCountdown();
    },
    startCountdown() {
      this.countdownInterval = setInterval(() => {
        const now = new Date().getTime();
        const distance = this.flashSaleEndTime - now;
        if (distance < 0) {
          clearInterval(this.countdownInterval);
          this.countdownText = "Flash Sale đã kết thúc!";
          this.isFlashSaleActive = false;
          return;
        }
        const days = Math.floor(distance / (1000 * 60 * 60 * 24));
        const hours = Math.floor(
          (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        );
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        this.countdownText = `${days}d ${hours}h ${minutes}m ${seconds}s`;
      }, 1000);
    },
    showToasts() {
      const toastElements = document.querySelectorAll(".toast");
      toastElements.forEach((toastEl) => {
        const toast = new Toast(toastEl, { autohide: true, delay: 5000 });
        toast.show();
      });
    },
  },
  beforeUnmount() {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  },
};
</script>

<style scoped>
/* Giữ nguyên CSS từ file gốc */
body {
  font-family: "Segoe UI", sans-serif;
  background-color: #ecf0f1;
  color: #34495e;
}

.search-filter-container {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.form-control,
.form-select {
  border: 1px solid #ddd;
  color: #34495e;
  background-color: #f9f9f9;
}

.form-control:focus,
.form-select:focus {
  border-color: #3498db;
  box-shadow: 0 0 5px rgba(52, 152, 219, 0.3);
  background-color: white;
}

.btn-filter {
  background-color: black;
  color: white;
  border: none;
  transition: all 0.3s ease;
}

.btn-filter:hover {
  background-color: black;
  color: white;
  transform: translateY(-2px);
}

.product-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
}

.product-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.15);
}

.product-img-container {
  position: relative;
  overflow: hidden;
  padding-top: 100%;
  background-color: #f5f6f7;
}

.product-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-img {
  transform: scale(1.1);
}

.discount-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #e74c3c;
  color: white;
  padding: 8px 10px;
  font-weight: bold;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
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

.product-info {
  padding: 15px;
  text-align: center;
}

.product-title {
  color: #34495e;
  font-size: 1.1rem;
  margin-bottom: 10px;
  height: 2.4em;
  overflow: hidden;
}

.product-price {
  color: #e74c3c;
  font-weight: bold;
  font-size: 1.2rem;
  margin-bottom: 0;
}

.product-price .original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 0.9rem;
  font-weight: normal;
  margin-left: 8px;
}

.product-price .discount-price {
  color: #e74c3c;
  font-weight: bold;
}

.quick-view-btn {
  position: absolute;
  bottom: -50px;
  left: 50%;
  transform: translateX(-50%);
  background-color: white;
  color: black;
  border: none;
  border-radius: 5px;
  padding: 8px 15px;
  transition: all 0.3s ease;
  opacity: 0;
}

.quick-view-btn:hover {
  background-color: #e74c3c !important;
  color: white !important;
}

.product-card:hover .quick-view-btn {
  bottom: 20px;
  opacity: 1;
}

.section-title {
  color: #2c3e50;
  margin-bottom: 30px;
  font-weight: 700;
  position: relative;
  padding-bottom: 10px;
  text-align: center;
}

.section-title::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background-color: #3498db;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
  padding: 0 15px;
}

@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}

.pagination {
  justify-content: center;
  margin-top: 40px;
}

.page-link {
  color: #e74c3c;
  border: none;
  padding: 10px 15px;
  margin: 0 5px;
  transition: all 0.3s ease;
}

.page-link:hover {
  background-color: #e74c3c;
  color: white;
}

.page-item.active .page-link {
  background-color: #e74c3c;
  color: white;
}

.page-item.disabled .page-link {
  color: #ccc;
}

.result-count {
  text-align: center;
  color: #34495e;
  margin-bottom: 20px;
}

.search-filter-container .row {
  align-items: center;
}

.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1055;
}

.toast {
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  min-width: 300px;
  background-color: #fff;
  border: none;
}

.toast-header {
  background-color: transparent;
  border-bottom: none;
  padding: 10px 15px;
}

.toast-header .me-auto {
  font-weight: 500;
  color: #34495e;
}

.toast-body {
  padding: 10px 15px;
  color: #34495e;
  font-size: 0.9rem;
}

.toast-success .toast-header {
  color: #28a745;
}

.toast-success .toast-header i {
  margin-right: 8px;
}

.toast-error .toast-header {
  color: #dc3545;
}

.toast-error .toast-header i {
  margin-right: 8px;
}

.toast .btn-close {
  filter: invert(0.5);
}

.flash-sale-section {
  background-color: #ff4444;
  color: white;
  padding: 20px 0;
  text-align: center;
  margin-bottom: 30px;
}

.flash-sale-section h2 {
  font-size: 2rem;
  font-weight: 500;
  margin-bottom: 10px;
}

.flash-sale-section p {
  font-size: 1.2rem;
}

@media (max-width: 576px) {
  .toast-container {
    top: 10px;
    right: 10px;
    left: 10px;
    width: auto;
  }

  .toast {
    min-width: 100%;
  }
}
</style>
