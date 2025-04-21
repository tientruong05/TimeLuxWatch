<template>
  <div class="container py-5">
    <h1>Thống kê doanh thu theo thương hiệu</h1>

    <!-- Loading Indicator -->
    <div v-if="isLoading" class="text-center my-5">
      <div class="spinner-border text-warning" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
    </div>

    <!-- Error Message -->
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>

    <!-- Data Table -->
    <div
      id="product-stats"
      v-if="!isLoading && !error && revenueStats.length > 0"
    >
      <table class="table">
        <thead>
          <tr>
            <th>Tên thương hiệu</th>
            <th>Tổng doanh thu</th>
            <th>Tổng số lượng</th>
            <th>Giá cao nhất</th>
            <th>Giá thấp nhất</th>
            <th>Giá trung bình</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="stat in revenueStats" :key="stat.categoryName">
            <td>{{ stat.categoryName }}</td>
            <td>{{ formatCurrency(stat.totalRevenue) }} VNĐ</td>
            <td>{{ stat.totalQty }}</td>
            <td>{{ formatCurrency(stat.maxPrice) }} VNĐ</td>
            <td>{{ formatCurrency(stat.minPrice) }} VNĐ</td>
            <td>{{ formatCurrency(stat.avgPrice) }} VNĐ</td>
          </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="d-flex justify-content-center mt-4">
        <nav>
          <ul class="pagination">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage - 1)"
              >
                <i class="bi bi-chevron-left"></i>
              </a>
            </li>
            <li
              v-for="page in paginationRange"
              :key="page"
              class="page-item"
              :class="{ active: page === currentPage + 1 }"
            >
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(page - 1)"
                >{{ page }}</a
              >
            </li>
            <li
              class="page-item"
              :class="{ disabled: currentPage >= totalPages - 1 }"
            >
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage + 1)"
              >
                <i class="bi bi-chevron-right"></i>
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <!-- No Data Message -->
    <div
      v-if="!isLoading && !error && revenueStats.length === 0"
      class="alert alert-info"
    >
      Không có dữ liệu thống kê doanh thu.
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import apiClient from "@/services/api";

// Data
const revenueStats = ref([]);
const isLoading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const pageSize = ref(10);
const totalPages = ref(0);
const totalItems = ref(0);

// Computed
const paginationRange = computed(() => {
  const range = [];
  const startPage = Math.max(
    0,
    Math.min(currentPage.value - 2, totalPages.value - 5)
  );
  const endPage = Math.min(totalPages.value, startPage + 5);

  for (let i = startPage + 1; i <= endPage; i++) {
    range.push(i);
  }

  return range;
});

// Methods
const fetchRevenueData = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    const response = await apiClient.get("/statistics/business", {
      params: {
        page: currentPage.value,
        size: pageSize.value,
      },
    });

    revenueStats.value = response.data.revenueStats || [];
    currentPage.value = response.data.currentPage;
    totalPages.value = response.data.totalPages;
    totalItems.value = response.data.totalItems;
  } catch (err) {
    console.error("Error fetching revenue statistics:", err);
    error.value = "Không thể tải dữ liệu thống kê. Vui lòng thử lại sau.";
    revenueStats.value = [];
  } finally {
    isLoading.value = false;
  }
};

const changePage = (page) => {
  if (page < 0 || page >= totalPages.value) return;
  currentPage.value = page;
  fetchRevenueData();
};

const formatCurrency = (value) => {
  if (value === undefined || value === null) return "N/A";
  return value.toLocaleString("vi-VN");
};

// Lifecycle
onMounted(() => {
  fetchRevenueData();
});
</script>

<style scoped>
body {
  background: #111111;
  color: #d4af37;
  font-family: "Helvetica Neue", Arial, sans-serif;
}

h1 {
  text-align: center;
  color: #d4af37;
  font-weight: 300;
  letter-spacing: 1px;
  margin-bottom: 30px;
}

#product-stats {
  background: #222222;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
  padding: 30px;
  margin-top: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

table {
  border-collapse: separate;
  border-spacing: 0 8px;
  width: 100%;
}

table thead tr th {
  background-color: #111111 !important;
  color: #d4af37 !important;
  text-align: center;
  border: none !important;
  padding: 15px;
  font-weight: 400;
  letter-spacing: 1px;
}

tbody tr {
  background: rgba(212, 175, 55, 0.05);
  transition: all 0.3s ease;
}

tbody tr:hover {
  background: rgba(212, 175, 55, 0.1);
  transform: scale(1.01);
}

tbody td {
  border: none !important;
  padding: 15px;
  vertical-align: middle;
  text-align: center;
}

/* Pagination styling */
.pagination {
  justify-content: center;
}

.page-item .page-link {
  background-color: #222222;
  border-color: #333333;
  color: #d4af37;
}

.page-item.active .page-link {
  background-color: #d4af37;
  border-color: #d4af37;
  color: #111111;
}

.page-item.disabled .page-link {
  background-color: #222222;
  border-color: #333333;
  color: #777777;
}

.page-item:not(.disabled):not(.active) .page-link:hover {
  background-color: #333333;
  color: #ffffff;
}
</style>
