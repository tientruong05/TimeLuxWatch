<template>
  <div class="container-fluid">
    <!-- Loading/Error States -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div v-else-if="error" class="alert alert-danger my-5">
      {{ error }}
    </div>

    <!-- Flash Sale Active -->
    <div v-else-if="isFlashSaleActive" class="flash-sale-header">
      <div class="container">
        <h1 class="flash-sale-title"><i class="fas fa-bolt"></i> FLASH SALE</h1>
        <p class="lead">Giảm giá sốc chỉ trong thời gian giới hạn!</p>
        <div class="countdown-container">
          <div
            class="countdown-box"
            v-for="unit in countdownUnits"
            :key="unit.label"
          >
            <div class="countdown-number">{{ unit.value }}</div>
            <div class="countdown-label">{{ unit.label }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="container mb-5">
      <div v-if="isFlashSaleActive">
        <p class="result-count">Tìm thấy {{ totalItems }} sản phẩm</p>
        <div class="product-grid">
          <div
            v-for="product in flashSaleProducts"
            :key="product.id"
            class="product-card"
          >
            <div class="product-img-container">
              <div class="discount-badge" v-if="product.discountPercentage">
                <span>{{ Math.round(product.discountPercentage) }}%</span>
                <span>OFF</span>
              </div>
              <img
                :src="`http://localhost:8080/photos/${product.image}`"
                class="product-img"
                :alt="product.name"
              />
              <router-link
                class="quick-view-btn btn"
                :to="{ name: 'ProductDetail', params: { id: product.id } }"
              >
                Xem nhanh
              </router-link>
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ product.name }}</h3>
              <p class="product-price">
                <span v-if="product.discountPercentage">
                  <span class="discount-price">{{
                    formatPrice(product.discountedPrice)
                  }}</span>
                  <br />
                  <span class="original-price">{{
                    formatPrice(product.price)
                  }}</span>
                </span>
              </p>
            </div>
          </div>
        </div>

        <nav aria-label="Page navigation" v-if="totalPages > 1">
          <ul class="pagination">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage - 1)"
                >«</a
              >
            </li>
            <li
              v-for="page in visiblePages"
              :key="page"
              class="page-item"
              :class="{ active: page === currentPage }"
            >
              <a class="page-link" href="#" @click.prevent="changePage(page)">{{
                page + 1
              }}</a>
            </li>
            <li
              class="page-item"
              :class="{ disabled: currentPage === totalPages - 1 }"
            >
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage + 1)"
                >»</a
              >
            </li>
          </ul>
        </nav>
      </div>

      <div v-else class="no-flash-sale">
        <i class="fas fa-clock fa-4x mb-3 text-muted"></i>
        <h2>Không có Flash Sale nào đang diễn ra</h2>
        <p class="lead">
          Vui lòng quay lại sau để không bỏ lỡ các ưu đãi đặc biệt!
        </p>
        <router-link
          to="/"
          class="btn btn-primary mt-3"
          style="
            background-color: #e74c3c;
            border-color: #e74c3c;
            border-radius: 20px;
          "
        >
          Quay lại trang chủ
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from "vue";
import apiClient from "@/services/api";
import { formatPrice } from "@/utils/formatters";

export default {
  setup() {
    // State
    const flashSaleProducts = ref([]);
    const flashSaleEndTime = ref(null);
    const isFlashSaleActive = ref(false);
    const totalItems = ref(0);
    const totalPages = ref(1);
    const currentPage = ref(0);
    const pageSize = ref(8);
    const countdownUnits = ref([
      { label: "ngày", value: "00" },
      { label: "giờ", value: "00" },
      { label: "phút", value: "00" },
      { label: "giây", value: "00" },
    ]);
    const loading = ref(true);
    const error = ref(null);
    const countdownInterval = ref(null);

    // Computed
    const visiblePages = computed(() => {
      const pages = [];
      for (let i = 0; i < totalPages.value; i++) {
        pages.push(i);
      }
      return pages;
    });

    // Methods
    const fetchData = async () => {
      loading.value = true;
      error.value = null;

      try {
        const response = await apiClient.get(`/flash-sale`, {
          params: {
            page: currentPage.value,
            size: pageSize.value,
          },
        });

        const data = response.data;
        isFlashSaleActive.value = data.isFlashSaleActive;

        if (isFlashSaleActive.value) {
          flashSaleProducts.value = data.flashSaleProducts;
          flashSaleEndTime.value = new Date(data.flashSaleEndTime);
          totalItems.value = data.totalItems;
          totalPages.value = data.totalPages;

          // Bắt đầu đếm ngược nếu có thời gian kết thúc
          if (flashSaleEndTime.value) {
            updateCountdown();
          }
        }
      } catch (err) {
        console.error("Error fetching flash sale data:", err);
        error.value = "Không thể tải dữ liệu Flash Sale. Vui lòng thử lại sau.";
      } finally {
        loading.value = false;
      }
    };

    const changePage = (page) => {
      if (page >= 0 && page < totalPages.value && page !== currentPage.value) {
        currentPage.value = page;
        fetchData();
        // Cuộn lên đầu trang khi chuyển trang
        window.scrollTo({ top: 0, behavior: "smooth" });
      }
    };

    const updateCountdown = () => {
      if (!flashSaleEndTime.value) return;

      const now = new Date();
      const end = flashSaleEndTime.value;
      const diff = end - now;

      if (diff <= 0) {
        // Flash sale đã kết thúc
        countdownUnits.value.forEach((u) => (u.value = "00"));
        isFlashSaleActive.value = false;
        return;
      }

      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      const hours = Math.floor(
        (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
      );
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);

      countdownUnits.value[0].value = String(days).padStart(2, "0");
      countdownUnits.value[1].value = String(hours).padStart(2, "0");
      countdownUnits.value[2].value = String(minutes).padStart(2, "0");
      countdownUnits.value[3].value = String(seconds).padStart(2, "0");
    };

    onMounted(() => {
      fetchData();
      // Thiết lập interval để cập nhật đếm ngược
      countdownInterval.value = setInterval(updateCountdown, 1000);
    });

    onUnmounted(() => {
      // Xóa interval khi component bị hủy
      if (countdownInterval.value) {
        clearInterval(countdownInterval.value);
      }
    });

    return {
      flashSaleProducts,
      isFlashSaleActive,
      totalItems,
      totalPages,
      currentPage,
      countdownUnits,
      loading,
      error,
      visiblePages,
      changePage,
      formatPrice,
    };
  },
};
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css");
@import url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css");
@import url("https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.5/font/bootstrap-icons.min.css");
/* Dán phần CSS từ file HTML cũ tại đây hoặc dùng SCSS nếu thích */
body {
  font-family: "Segoe UI", sans-serif;
  color: #34495e;
}
.flash-sale-header {
  background-color: #b22210;
  color: white;
  padding: 30px 0;
  text-align: center;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(255, 68, 68, 0.2);
}
.flash-sale-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
}
.countdown-container {
  display: flex;
  justify-content: center;
  margin-top: 25px;
  perspective: 1000px;
}
.countdown-box {
  background: linear-gradient(145deg, #333333, #222222);
  color: white;
  padding: 15px;
  border-radius: 10px;
  margin: 0 8px;
  min-width: 80px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}
.countdown-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
}
.countdown-number {
  font-size: 2.2rem;
  font-weight: bold;
  margin-bottom: 5px;
  display: block;
  color: #ffffff;
}
.countdown-label {
  font-size: 0.9rem;
  text-transform: uppercase;
  color: #f8f8f8;
  letter-spacing: 1px;
  opacity: 0.9;
}

/* Media queries for responsiveness */
@media (max-width: 576px) {
  .countdown-box {
    padding: 10px;
    min-width: 60px;
    margin: 0 4px;
  }
  .countdown-number {
    font-size: 1.6rem;
  }
  .countdown-label {
    font-size: 0.7rem;
  }
}
/* Sử dụng CSS product-grid từ category */
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

/* Sử dụng CSS product-card từ category */
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

.no-flash-sale {
  text-align: center;
  padding: 50px;
  background: white;
  border-radius: 10px;
  margin: 30px 0;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
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

/* Phân trang */
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
</style>
