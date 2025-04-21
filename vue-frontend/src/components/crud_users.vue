<template>
  <div class="container py-5">
    <div class="table-container">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản lý người dùng</h2>

        <div class="d-flex gap-3">
          <button class="btn btn-add btn-success" @click="openAddModal">
            <i class="bi bi-plus-circle-fill me-2"></i>Thêm người dùng
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center my-4">
        <div class="spinner-border text-warning" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
      </div>

      <!-- Messages -->
      <div
        v-if="successMessage"
        class="alert alert-success alert-dismissible fade show"
      >
        <i class="bi bi-check-circle-fill"></i> {{ successMessage }}
        <button
          type="button"
          class="btn-close"
          @click="successMessage = null"
        ></button>
      </div>
      <div
        v-if="errorMessage"
        class="alert alert-danger alert-dismissible fade show"
      >
        <i class="bi bi-exclamation-triangle-fill"></i> {{ errorMessage }}
        <button
          type="button"
          class="btn-close"
          @click="errorMessage = null"
        ></button>
      </div>

      <!-- Table -->
      <div v-if="!loading && users.length > 0" class="table-responsive">
        <table class="table align-middle">
          <thead>
            <tr>
              <th>STT</th>
              <th>Tên đăng nhập</th>
              <th>Email</th>
              <th>Họ tên</th>
              <th>SĐT</th>
              <th>Vai trò</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(user, index) in users" :key="user.id">
              <td>{{ index + 1 + currentPage * pageSize }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.fullName || "-" }}</td>
              <td>{{ user.phone || "-" }}</td>
              <td>
                <span
                  class="badge"
                  :class="user.role ? 'bg-success' : 'bg-secondary'"
                >
                  {{ user.role ? "Admin" : "User" }}
                </span>
              </td>
              <td>
                <span
                  class="badge"
                  :class="user.status ? 'bg-success' : 'bg-danger'"
                >
                  {{ user.status ? "Hoạt động" : "Khóa" }}
                </span>
              </td>
              <td>
                <button
                  class="btn btn-warning btn-sm me-2"
                  @click="openEditModal(user)"
                >
                  <i class="bi bi-pencil-fill"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- No Users -->
      <div
        v-if="!loading && users.length === 0 && !errorMessage"
        class="text-center py-4"
      >
        <p>Không tìm thấy người dùng nào.</p>
      </div>

      <!-- Pagination -->
      <nav class="pagination-nav mt-4" v-if="totalPages > 1 && !loading">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <a
              class="page-link"
              href="#"
              @click.prevent="changePage(currentPage - 1)"
              >Trước</a
            >
          </li>
          <li
            v-for="pageNumber in visiblePages"
            :key="pageNumber"
            class="page-item"
            :class="{
              active: currentPage === pageNumber - 1,
              disabled: pageNumber === '...',
            }"
          >
            <span v-if="pageNumber === '...'" class="page-link">...</span>
            <a
              v-else
              class="page-link"
              href="#"
              @click.prevent="changePage(pageNumber - 1)"
              >{{ pageNumber }}</a
            >
          </li>
          <li
            class="page-item"
            :class="{ disabled: currentPage === totalPages - 1 }"
          >
            <a
              class="page-link"
              href="#"
              @click.prevent="changePage(currentPage + 1)"
              >Sau</a
            >
          </li>
        </ul>
      </nav>
    </div>

    <!-- Add/Edit Modal -->
    <user-modal
      v-if="showModal"
      :user="selectedUser"
      :is-edit-mode="isEditMode"
      @close="handleCloseModal"
      @save="handleSaveUser"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import apiClient from "@/services/api"; // Import apiClient
import UserModal from "./UserModal.vue"; // Import the modal component

const users = ref([]);
const currentPage = ref(0);
const pageSize = ref(5); // Match backend default or specify
const totalPages = ref(0);
const totalItems = ref(0);
const loading = ref(true);
const successMessage = ref(null);
const errorMessage = ref(null);

// Modal state
const showModal = ref(false);
const isEditMode = ref(false);
const selectedUser = ref(null);

// Fetch users function
const loadUsers = async () => {
  loading.value = true;
  errorMessage.value = null;
  try {
    const response = await apiClient.get("/users", {
      params: {
        page: currentPage.value,
        size: pageSize.value,
      },
    });
    users.value = response.data.users || [];
    totalPages.value = response.data.totalPages || 0;
    totalItems.value = response.data.totalItems || 0;
    currentPage.value = response.data.currentPage || 0; // Sync page from response
  } catch (error) {
    console.error("Error loading users:", error);
    errorMessage.value =
      "Không thể tải danh sách người dùng. Vui lòng thử lại.";
    if (error.response && error.response.status === 401) {
      errorMessage.value =
        "Phiên đăng nhập hết hạn hoặc bạn không có quyền truy cập.";
      // Optionally redirect to login
    }
    users.value = [];
    totalPages.value = 0;
  } finally {
    loading.value = false;
  }
};

// Change page handler
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
    loadUsers();
  }
};

// --- Modal Handlers ---
const openAddModal = () => {
  isEditMode.value = false;
  selectedUser.value = null; // Reset selected user for add mode
  showModal.value = true;
};

const openEditModal = (user) => {
  isEditMode.value = true;
  selectedUser.value = { ...user }; // Pass a copy to avoid direct mutation
  showModal.value = true;
};

const handleCloseModal = () => {
  showModal.value = false;
  selectedUser.value = null;
};

const handleSaveUser = (message) => {
  showModal.value = false;
  selectedUser.value = null;
  successMessage.value = message;
  loadUsers(); // Reload the user list
  setTimeout(() => (successMessage.value = null), 4000); // Clear message after a delay
};
// --- End Modal Handlers ---

// Pagination logic (same as searchPage)
const visiblePages = computed(() => {
  const pages = [];
  const maxVisible = 5;
  const halfVisible = Math.floor((maxVisible - 2) / 2);

  if (totalPages.value <= 1) return [];

  if (totalPages.value <= maxVisible) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    pages.push(1);
    let startPage = Math.max(2, currentPage.value - halfVisible + 1);
    let endPage = Math.min(
      totalPages.value - 1,
      currentPage.value + halfVisible + 1
    );

    if (currentPage.value <= halfVisible + 1) {
      startPage = 2;
      endPage = Math.min(totalPages.value - 1, maxVisible - 1);
    } else if (currentPage.value >= totalPages.value - halfVisible - 2) {
      startPage = Math.max(2, totalPages.value - maxVisible + 2);
      endPage = totalPages.value - 1;
    }

    if (startPage > 2) {
      pages.push("...");
    }
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    if (endPage < totalPages.value - 1) {
      pages.push("...");
    }
    pages.push(totalPages.value);
  }
  return pages;
});

// Load users when component is mounted
onMounted(() => {
  loadUsers();
});
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

.btn-success {
  background: #d4af37;
  color: #111111;
  border: none;
  padding: 10px 25px;
  border-radius: 5px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-success:hover {
  background: #b4941e;
  color: #111111;
  transform: translateY(-2px);
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

tbody td {
  border: none !important;
  padding: 15px;
  vertical-align: middle;
}

.btn-warning {
  background: #d4af37;
  color: #111111;
  border: none;
  margin-right: 5px;
}

.btn-danger {
  background: #2c2c2c;
  color: #d4af37;
  border: 1px solid #d4af37;
}

.btn-warning:hover,
.btn-danger:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

.modal-content {
  background: #222222;
  border: 1px solid #d4af37;
}

.modal-header {
  border-bottom: 1px solid rgba(212, 175, 55, 0.2);
}

.modal-footer {
  border-top: 1px solid rgba(212, 175, 55, 0.2);
}

.modal-title {
  color: #d4af37;
}

.btn-close {
  color: #d4af37;
  opacity: 1;
}

.form-label {
  color: #d4af37;
}

.form-control,
.form-select {
  background: #111111;
  border: 1px solid #d4af37;
  color: #fff;
}

.form-control:focus,
.form-select:focus {
  background: #111111;
  border-color: #b4941e;
  box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25);
  color: #fff;
}

.form-select option {
  background: #111111;
  color: #fff;
}

.modal .btn-secondary {
  background: #2c2c2c;
  color: #d4af37;
  border: 1px solid #d4af37;
}

.modal .btn-primary {
  background: #d4af37;
  color: #111111;
  border: none;
}

.modal .btn-primary:hover,
.modal .btn-secondary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
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

.btn-success {
  background: #d4af37;
  color: #111111;
  border: none;
  padding: 10px 25px;
  border-radius: 5px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-success:hover {
  background: #b4941e;
  color: #111111;
  transform: translateY(-2px);
}

table {
  border-collapse: separate;
  border-spacing: 0 10px;
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

tbody td {
  border: none !important;
  padding: 15px;
  vertical-align: middle;
}

.product-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 5px;
  border: 1px solid rgba(212, 175, 55, 0.3);
}

.btn-warning {
  background: #d4af37;
  color: #111111;
  border: none;
  margin-right: 5px;
}

.btn-danger {
  background: #2c2c2c;
  color: #d4af37;
  border: 1px solid #d4af37;
}

.btn-warning:hover,
.btn-danger:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

.modal-content {
  background: #222222;
  border: 1px solid #d4af37;
  border-radius: 10px;
}

.modal-header {
  border-bottom: 1px solid rgba(212, 175, 55, 0.2);
}

.modal-footer {
  border-top: 1px solid rgba(212, 175, 55, 0.2);
}

.modal-title {
  color: #d4af37;
}

.btn-close {
  color: #d4af37;
  opacity: 1;
  filter: invert(1);
}

.form-label {
  color: #d4af37;
}

table thead tr th:nth-child(1) {
  /* STT */
  min-width: 50px;
}
table thead tr th:nth-child(2) {
  /* Tên sản phẩm */
  min-width: 200px;
}
table thead tr th:nth-child(3) {
  /* Ảnh */
  min-width: 80px;
}
table thead tr th:nth-child(4) {
  /* Loại hàng */
  min-width: 120px;
}
table thead tr th:nth-child(5) {
  /* Hãng */
  min-width: 120px;
}
table thead tr th:nth-child(6) {
  /* Giá */
  min-width: 150px;
}
table thead tr th:nth-child(7) {
  /* Số lượng */
  min-width: 100px;
}
table thead tr th:nth-child(8) {
  /* Mô tả */
  min-width: 200px;
}
table thead tr th:nth-child(9) {
  /* Trạng thái */
  min-width: 100px;
}
table thead tr th:nth-child(10) {
  /* Thao tác */
  min-width: 100px;
}

.form-control,
.form-select {
  background: #111111;
  border: 1px solid #d4af37;
  color: #fff;
  height: 40px;
  padding: 8px;
}

.form-control:focus,
.form-select:focus {
  background: #111111;
  border-color: #b4941e;
  box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25);
  color: #fff;
}

.form-select option {
  background: #111111;
  color: #fff;
}

.modal .btn-secondary {
  background: #2c2c2c;
  color: #d4af37;
  border: 1px solid #d4af37;
  transition: all 0.3s ease;
}

.modal .btn-secondary:hover {
  background: #3a3a3a;
  transform: translateY(-1px);
}

.modal .btn-primary {
  background: #d4af37;
  color: #111111;
  border: none;
  transition: all 0.3s ease;
}

.modal .btn-primary:hover {
  background: #b4941e;
  transform: translateY(-1px);
}

.modal .btn-danger {
  background: #dc3545;
  color: #fff;
  border: none;
  transition: all 0.3s ease;
}

.modal .btn-danger:hover {
  background: #c82333;
  transform: translateY(-1px);
}

.pagination {
  margin-top: 20px;
  justify-content: center;
  display: flex;
}

.page-link {
  background-color: #222222;
  color: #d4af37;
  border: 1px solid rgba(212, 175, 55, 0.2);
  margin: 0 2px;
  padding: 6px 12px;
  border-radius: 4px;
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
  pointer-events: none;
}

.search-filter-container {
  background: #222222;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(212, 175, 55, 0.15);
  border: 1px solid rgba(212, 175, 55, 0.2);
}

.search-filter-container .row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-filter-container .col-lg-3,
.search-filter-container .col-lg-2 {
  flex: 0 0 auto;
  width: 200px;
}

.search-filter-container .btn-primary {
  background: #d4af37;
  color: #111111;
  border: none;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0;
}

.search-filter-container .btn-primary:hover {
  background: #b4941e;
}

.alert {
  border-radius: 8px;
  padding: 12px 15px;
  font-size: 0.9rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  margin-bottom: 15px;
  width: 100%;
  max-width: 500px;
  position: relative;
  color: #fff;
}

.alert-success {
  background-color: #28a745;
  border-color: #28a745;
}

.alert-danger {
  background-color: #dc3545;
  border-color: #dc3545;
}

.alert i {
  vertical-align: middle;
  margin-right: 8px;
}

.alert-dismissible .btn-close {
  padding: 12px;
  filter: invert(1);
}

.modal-body .delete-warning {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
}

.modal-body .delete-warning i {
  color: #dc3545;
  font-size: 24px;
}

.modal-body .delete-warning strong {
  color: #d4af37;
}

.modal.fade .modal-dialog {
  transition: transform 0.3s ease-out;
  transform: translate(0, -50px);
}

.modal.show .modal-dialog {
  transform: translate(0, 0);
}

@media (max-width: 768px) {
  .alert {
    max-width: 100%;
    font-size: 0.85rem;
  }
  .table-container {
    padding: 15px;
  }
  .btn-success {
    padding: 8px 15px;
  }
  .search-filter-container .col-lg-3,
  .search-filter-container .col-lg-2 {
    width: 100%;
    margin-bottom: 10px;
  }
  .search-filter-container .row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
