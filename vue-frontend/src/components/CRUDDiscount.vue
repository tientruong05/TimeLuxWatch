<template>
  <div class="container py-5">
    <div class="table-container mt-4">
      <h2 class="mb-4 text-warning">Quản lý Giảm Giá</h2>

      <!-- Success/Error Messages -->
      <div
        v-if="message"
        :class="[
          'alert',
          messageType === 'success' ? 'alert-success' : 'alert-danger',
        ]"
      >
        {{ message }}
      </div>

      <!-- Add Button -->
      <button class="btn btn-warning mb-3" @click="openAddModal">
        <i class="bi bi-plus-circle me-2"></i>Thêm Giảm Giá Mới
      </button>

      <!-- Loading Indicator -->
      <div v-if="isLoading" class="text-center my-5">
        <div class="spinner-border text-warning" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
      </div>

      <!-- Discount Table -->
      <div v-if="!isLoading && discounts.length > 0" class="table-responsive">
        <table
          class="table table-dark table-striped table-bordered align-middle"
        >
          <thead>
            <tr>
              <th>STT</th>
              <th>Tên</th>
              <th>Giá trị (%)</th>
              <th>Ngày bắt đầu</th>
              <th>Ngày kết thúc</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(discount, index) in paginatedDiscounts"
              :key="discount.id"
            >
              <td>{{ index + 1 }}</td>
              <td>{{ discount.discountName }}</td>
              <td>{{ discount.discountValue }}%</td>
              <td>{{ formatDate(discount.startDate) }}</td>
              <td>{{ formatDate(discount.endDate) }}</td>
              <td>
                <span
                  :class="
                    discount.status === 1 ? 'text-success' : 'text-danger'
                  "
                >
                  {{ discount.status === 1 ? "Hoạt động" : "Không hoạt động" }}
                </span>
              </td>
              <td>
                <button
                  class="btn btn-sm btn-primary me-2"
                  @click="openEditModal(discount.id)"
                >
                  <i class="bi bi-pencil-square"></i> Sửa
                </button>
                <button
                  class="btn btn-sm btn-danger"
                  @click="confirmDelete(discount.id)"
                >
                  <i class="bi bi-trash"></i> Xóa
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- No Discounts Message -->
      <div v-if="!isLoading && discounts.length === 0" class="alert alert-info">
        Không có dữ liệu giảm giá nào.
      </div>

      <!-- Discount Modal -->
      <DiscountModal
        :show="showModal"
        :discount-id="currentDiscountId"
        @close="closeModal"
        @save="handleSave"
      />

      <!-- Confirmation Modal -->
      <div
        class="modal fade show d-block"
        tabindex="-1"
        v-if="showConfirmModal"
        style="background-color: rgba(0, 0, 0, 0.5)"
      >
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content bg-dark text-white">
            <div class="modal-header border-secondary">
              <h5 class="modal-title text-warning">Xác nhận Xóa</h5>
              <button
                type="button"
                class="btn-close btn-close-white"
                @click="cancelDelete"
              ></button>
            </div>
            <div class="modal-body">
              <p>Bạn có chắc chắn muốn xóa giảm giá này không?</p>
            </div>
            <div class="modal-footer border-secondary">
              <button
                type="button"
                class="btn btn-secondary"
                @click="cancelDelete"
              >
                Hủy
              </button>
              <button
                type="button"
                class="btn btn-danger"
                @click="deleteDiscount"
              >
                Xóa
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination Controls -->
      <div
        v-if="!isLoading && totalPages > 1"
        class="mt-4 d-flex justify-content-center"
      >
        <nav>
          <ul class="pagination">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <a
                class="page-link"
                @click="changePage(currentPage - 1)"
                aria-label="Previous"
              >
                <span aria-hidden="true">&laquo;</span>
              </a>
            </li>
            <li v-for="page in visiblePages" :key="page" class="page-item">
              <a class="page-link" @click="changePage(page - 1)">{{ page }}</a>
            </li>
            <li
              class="page-item"
              :class="{ disabled: currentPage === totalPages - 1 }"
            >
              <a
                class="page-link"
                @click="changePage(currentPage + 1)"
                aria-label="Next"
              >
                <span aria-hidden="true">&raquo;</span>
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from "vue";
import apiClient from "@/services/api";
import DiscountModal from "./DiscountModal.vue"; // Import the new modal component

const discounts = ref([]);
const isLoading = ref(false);
const message = ref("");
const messageType = ref("success"); // 'success' or 'error'

const showModal = ref(false);
const currentDiscountId = ref(null); // null for add, ID for edit

const showConfirmModal = ref(false);
const discountToDeleteId = ref(null);

// --- Pagination State ---
const pagination = reactive({
  currentPage: 0,
  pageSize: 10,
  // totalPages will be calculated
});

// --- Computed Properties for Pagination ---
const totalPages = computed(() => {
  if (!discounts.value || discounts.value.length === 0) return 0;
  return Math.ceil(discounts.value.length / pagination.pageSize);
});

const paginatedDiscounts = computed(() => {
  if (!discounts.value || discounts.value.length === 0) return [];
  const start = pagination.currentPage * pagination.pageSize;
  const end = start + pagination.pageSize;
  return discounts.value.slice(start, end);
});

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
    let startPage = Math.max(2, pagination.currentPage - halfVisible + 1);
    let endPage = Math.min(
      totalPages.value - 1,
      pagination.currentPage + halfVisible + 1
    );

    // Adjust if near the beginning
    if (pagination.currentPage <= halfVisible + 1) {
      startPage = 2;
      endPage = Math.min(totalPages.value - 1, maxVisible - 1);
    }
    // Adjust if near the end
    else if (pagination.currentPage >= totalPages.value - halfVisible - 2) {
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

// --- Fetch Data ---
const fetchDiscounts = async () => {
  isLoading.value = true;
  message.value = "";
  try {
    // Use GET /api/discounts to fetch all discounts
    const response = await apiClient.get("/discounts");
    discounts.value = response.data || []; // Assuming the API returns an array directly
  } catch (err) {
    console.error("Error fetching discounts:", err);
    message.value =
      "Lỗi khi tải danh sách giảm giá: " +
      (err.response?.data?.error || err.message);
    messageType.value = "error";
    discounts.value = []; // Clear discounts on error
  } finally {
    isLoading.value = false;
  }
};

// --- Pagination Method ---
const changePage = (page) => {
  // page here is 0-indexed from the click event
  if (page >= 0 && page < totalPages.value) {
    pagination.currentPage = page;
    // No need to call fetchDiscounts with client-side pagination
  }
};

// --- Modal Handling ---
const openAddModal = () => {
  currentDiscountId.value = null;
  showModal.value = true;
};

const openEditModal = (id) => {
  currentDiscountId.value = id;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  currentDiscountId.value = null; // Reset ID when closing
};

const handleSave = (successMessage) => {
  closeModal();
  message.value = successMessage || "Lưu giảm giá thành công!";
  messageType.value = "success";
  fetchDiscounts(); // Refresh the list
  // Auto-hide message after a few seconds
  setTimeout(() => {
    message.value = "";
  }, 5000);
};

// --- Delete Handling ---
const confirmDelete = (id) => {
  discountToDeleteId.value = id;
  showConfirmModal.value = true;
};

const cancelDelete = () => {
  showConfirmModal.value = false;
  discountToDeleteId.value = null;
};

const deleteDiscount = async () => {
  if (!discountToDeleteId.value) return;

  isLoading.value = true; // Indicate loading during delete
  message.value = "";
  const id = discountToDeleteId.value;

  try {
    // Use DELETE /api/discounts/delete/{id}
    await apiClient.delete(`/discounts/delete/${id}`);
    message.value = "Xóa giảm giá thành công!";
    messageType.value = "success";
    fetchDiscounts(); // Refresh the list after delete
  } catch (err) {
    console.error(`Error deleting discount ${id}:`, err);
    message.value = `Lỗi khi xóa giảm giá: ${
      err.response?.data?.error || err.response?.data?.message || err.message
    }`;
    messageType.value = "error";
  } finally {
    cancelDelete(); // Close confirmation modal
    isLoading.value = false;
    // Auto-hide message
    setTimeout(() => {
      message.value = "";
    }, 5000);

    // --- Adjust current page after delete if necessary ---
    if (pagination.currentPage >= totalPages.value && totalPages.value > 0) {
      pagination.currentPage = totalPages.value - 1;
    }
    // --- End Adjust page ---
  }
};

// --- Utility ---
const formatDate = (dateString) => {
  if (!dateString) return "";
  try {
    const options = { year: "numeric", month: "2-digit", day: "2-digit" };
    return new Date(dateString).toLocaleDateString("vi-VN", options);
  } catch (e) {
    console.error("Error formatting date:", dateString, e);
    return dateString; // Return original string if formatting fails
  }
};

// --- Lifecycle Hooks ---
onMounted(() => {
  fetchDiscounts();
});
</script>

<style scoped>
body {
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

.table-container h1,
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
  color: #fff;
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
  /* ID */
  min-width: 50px;
}
table thead tr th:nth-child(2) {
  /* Tên giảm giá */
  min-width: 200px;
}
table thead tr th:nth-child(3) {
  /* Giá trị giảm giá */
  min-width: 150px;
}
table thead tr th:nth-child(4) {
  /* Ngày bắt đầu */
  min-width: 120px;
}
table thead tr th:nth-child(5) {
  /* Ngày kết thúc */
  min-width: 120px;
}
table thead tr th:nth-child(6) {
  /* Hành động */
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

@media (max-width: 768px) {
  .table-container {
    padding: 15px;
  }
  .btn-success {
    padding: 8px 15px;
  }
}
</style>
