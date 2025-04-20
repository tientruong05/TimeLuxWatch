<template>
  <div class="container py-5">
    <div class="table-container">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Lịch sử mua sắm</h2>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center my-5">
        <div class="spinner-border text-warning" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
      </div>

      <!-- Error State -->
      <div v-if="error" class="alert alert-danger">{{ error }}</div>

      <!-- Data Table -->
      <div v-if="!loading && !error">
        <table class="table">
          <thead>
            <tr>
              <th>Hình ảnh</th>
              <th>Tên sản phẩm</th>
              <th>Số lượng</th>
              <th>Ngày đặt hàng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="orderDetail in orderDetails" :key="orderDetail.id">
              <td>
                <img
                  :src="
                    'http://localhost:8080/photos/' + orderDetail.product.image
                  "
                  alt="Product Image"
                  class="product-image"
                  @error="handleImageError($event)"
                />
              </td>
              <td>{{ orderDetail.product.name }}</td>
              <td>{{ orderDetail.qty }}</td>
              <td>{{ formatDate(orderDetail.orderDate) }}</td>
            </tr>
          </tbody>
        </table>

        <nav aria-label="Page navigation" v-if="totalPages > 1">
          <ul class="pagination justify-content-center">
            <!-- Nút Previous -->
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage - 1)"
                :class="{ disabled: currentPage === 0 }"
              >
                Trước
              </a>
            </li>

            <!-- 3 trang đầu -->
            <li
              class="page-item"
              v-for="i in 3"
              :key="'first-' + i"
              v-if="i - 1 < totalPages"
              :class="{ active: currentPage === i - 1 }"
            >
              <a class="page-link" href="#" @click.prevent="changePage(i - 1)">
                {{ i }}
              </a>
            </li>

            <!-- Dấu "..." nếu cần (trước các trang xung quanh trang hiện tại) -->
            <li class="page-item" v-if="totalPages > 6 && currentPage > 3">
              <span class="page-link">...</span>
            </li>

            <!-- Các trang xung quanh trang hiện tại -->
            <li
              class="page-item"
              v-for="i in 3"
              :key="'middle-' + i"
              v-if="
                currentPage - 2 + i >= 3 && currentPage - 2 + i < totalPages - 3
              "
              :class="{ active: currentPage === currentPage - 2 + i }"
            >
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage - 2 + i)"
              >
                {{ currentPage - 1 + i }}
              </a>
            </li>

            <!-- Dấu "..." nếu cần (sau các trang xung quanh trang hiện tại) -->
            <li
              class="page-item"
              v-if="totalPages > 6 && currentPage < totalPages - 4"
            >
              <span class="page-link">...</span>
            </li>

            <!-- 3 trang cuối -->
            <li
              class="page-item"
              v-for="i in 3"
              :key="'last-' + i"
              v-if="totalPages > 3 && totalPages - 3 + i - 1 >= 3"
              :class="{ active: currentPage === totalPages - 3 + i - 1 }"
            >
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(totalPages - 3 + i - 1)"
              >
                {{ totalPages - 3 + i }}
              </a>
            </li>

            <!-- Nút Next -->
            <li
              class="page-item"
              :class="{ disabled: currentPage === totalPages - 1 }"
            >
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage + 1)"
                :class="{ disabled: currentPage === totalPages - 1 }"
              >
                Sau
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api"; // Import apiClient
import { useAuthStore } from "@/stores/auth"; // Import AuthStore
import { mapState } from "pinia"; // To map state easily

export default {
  name: "ShoppingHistory",
  data() {
    return {
      loading: true,
      error: null,
      orderDetails: [],
      currentPage: 0,
      totalPages: 0,
      pageSize: 5, // Giảm page size để test
      search: "", // Giữ lại nếu bạn muốn thêm tìm kiếm sau này
    };
  },
  computed: {
    // Map user state from AuthStore
    ...mapState(useAuthStore, ["user"]),
  },
  created() {
    this.fetchData();
  },
  methods: {
    async fetchData() {
      this.loading = true;
      this.error = null;

      const authStore = useAuthStore();
      const userId = authStore.user?.id;

      if (!userId) {
        this.error = "Không thể lấy ID người dùng. Vui lòng đăng nhập lại.";
        this.loading = false;
        return;
      }

      try {
        const response = await apiClient.get(`/shopping-history`, {
          params: {
            userId: userId,
            page: this.currentPage,
            size: this.pageSize,
            // search: this.search // Thêm nếu cần
          },
        });

        if (response.data && response.data.orderDetails) {
          this.orderDetails = response.data.orderDetails;
          this.totalPages = response.data.totalPages;
          this.currentPage = response.data.currentPage;
          if (this.orderDetails.length === 0 && this.totalPages === 0) {
            this.error = "Bạn chưa có lịch sử mua sắm nào."; // Thông báo nếu không có data
          }
        } else {
          this.orderDetails = [];
          this.totalPages = 0;
          this.error = "Không nhận được dữ liệu lịch sử mua sắm hợp lệ.";
        }
      } catch (error) {
        console.error("Error fetching order history:", error);
        if (error.response && error.response.status === 401) {
          this.error = "Vui lòng đăng nhập để xem lịch sử mua sắm.";
        } else if (
          error.response &&
          error.response.data &&
          error.response.data.error
        ) {
          this.error = error.response.data.error;
        } else {
          this.error = "Có lỗi xảy ra khi tải lịch sử mua sắm.";
        }
        this.orderDetails = [];
        this.totalPages = 0;
      } finally {
        this.loading = false;
      }
    },
    formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString);
      return `${date.getDate().toString().padStart(2, "0")}/${(
        date.getMonth() + 1
      )
        .toString()
        .padStart(2, "0")}/${date.getFullYear()}`;
    },
    changePage(pageNumber) {
      if (pageNumber < 0 || pageNumber >= this.totalPages) return;
      this.currentPage = pageNumber;
      this.fetchData();
    },
    handleImageError(event) {
      event.target.src = "http://localhost:8080/photos/default.png";
    },
  },
};
</script>

<style scoped>
body {
  background: #111111;
  color: #d4af37;
  font-family: "Helvetica Neue", Arial, sans-serif;
}

.table-container {
  background: #222222;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
  padding: 30px;
  margin-top: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

.table-container h2 {
  color: #d4af37;
  font-weight: 300;
  letter-spacing: 1px;
  margin-bottom: 30px;
}

table {
  border-collapse: separate;
  border-spacing: 0 8px;
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

.pagination {
  margin-top: 20px;
}

.page-link {
  background-color: #222222;
  color: #d4af37;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

.page-link:hover {
  background-color: rgba(212, 175, 55, 0.1);
  color: #d4af37;
}

.page-item.active .page-link {
  background-color: #d4af37;
  border-color: #d4af37;
  color: #111111;
}

.page-item.disabled .page-link {
  background-color: #222222;
  color: rgba(212, 175, 55, 0.5);
}

.product-image {
  display: block;
  margin: 0 auto;
  max-width: 80px;
  height: auto;
  border-radius: 5px;
  transition: all 0.3s ease;
}

.product-image:hover {
  transform: scale(1.1);
}
</style>
