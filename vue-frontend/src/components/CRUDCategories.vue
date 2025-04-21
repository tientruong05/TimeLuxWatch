<template>
  <div class="container py-5">
    <div class="table-container">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản lý Loại sản phẩm theo Thương hiệu</h2>
        <button class="btn btn-success" @click="openAddModal">
          <i class="fas fa-plus me-2"></i>Thêm Loại
        </button>
      </div>

      <!-- Hiển thị thông báo lỗi hoặc thành công -->
      <div
        v-if="error"
        class="alert alert-danger alert-dismissible fade show"
        role="alert"
      >
        {{ error }}
        <button type="button" class="btn-close" @click="error = null"></button>
      </div>
      <div
        v-if="success"
        class="alert alert-success alert-dismissible fade show"
        role="alert"
      >
        {{ success }}
        <button
          type="button"
          class="btn-close"
          @click="success = null"
        ></button>
      </div>

      <!-- Form lọc theo danh mục (Brand) -->
      <form @submit.prevent="filterByCategory" class="mb-4">
        <div class="d-flex align-items-center">
          <label for="categorySelect" class="me-2">Chọn Thương Hiệu:</label>
          <select
            id="categorySelect"
            v-model="selectedCategoryName"
            class="form-select w-auto"
            @change="filterByCategory"
          >
            <option
              v-for="category in categories"
              :key="category.id"
              :value="category.name"
            >
              {{ category.name }}
            </option>
          </select>
        </div>
      </form>

      <!-- Bảng danh sách Loại sản phẩm (SubCategories) -->
      <table class="table table-bordered table-hover">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Thương Hiệu</th>
            <th>Loại sản phẩm</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="subCategories.length === 0">
            <td colspan="5" class="text-center">
              Không có loại sản phẩm nào cho thương hiệu này.
            </td>
          </tr>
          <tr v-else v-for="subCategory in subCategories" :key="subCategory.id">
            <td>{{ subCategory.id }}</td>
            <td>{{ subCategory.category?.name || "N/A" }}</td>
            <td>{{ subCategory.subCategoriesName }}</td>
            <td>
              {{ subCategory.status === 1 ? "Hoạt động" : "Không hoạt động" }}
            </td>
            <td>
              <button
                class="btn btn-warning btn-sm me-1"
                @click="openEditModal(subCategory)"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button
                class="btn btn-danger btn-sm"
                @click="openDeleteConfirmModal(subCategory.id)"
              >
                <i class="fas fa-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Phân trang -->
      <nav v-if="pagination.totalPages > 1" aria-label="Page navigation">
        <ul class="pagination justify-content-center">
          <li
            class="page-item"
            :class="{ disabled: pagination.currentPage === 0 }"
          >
            <a
              class="page-link"
              href="#"
              @click.prevent="changePage(pagination.currentPage - 1)"
              >Trước</a
            >
          </li>
          <li
            v-for="pageNumber in visiblePages"
            :key="pageNumber"
            class="page-item"
            :class="{ active: pageNumber - 1 === pagination.currentPage }"
          >
            <a
              class="page-link"
              href="#"
              @click.prevent="changePage(pageNumber - 1)"
              >{{ pageNumber }}</a
            >
          </li>
          <li
            class="page-item"
            :class="{
              disabled: pagination.currentPage === pagination.totalPages - 1,
            }"
          >
            <a
              class="page-link"
              href="#"
              @click.prevent="changePage(pagination.currentPage + 1)"
              >Sau</a
            >
          </li>
        </ul>
      </nav>
    </div>

    <!-- Use the CategoryModal component -->
    <category-modal
      v-if="showModal"
      :category-type="selectedCategoryType"
      :is-edit-mode="isEditMode"
      :categories="categories"
      @close="handleCloseModal"
      @save="handleSave"
    />

    <!-- Modal Xác nhận xóa -->
    <div
      class="modal fade"
      id="confirmDeleteModal"
      tabindex="-1"
      ref="deleteModal"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Xác nhận xóa</h5>
            <button
              type="button"
              class="btn-close"
              @click="closeDeleteModal"
            ></button>
          </div>
          <div class="modal-body">
            <p>Bạn có chắc chắn muốn xóa loại sản phẩm này không?</p>
          </div>
          <div class="modal-footer">
            <button
              type="button"
              class="btn btn-secondary"
              @click="closeDeleteModal"
            >
              Không
            </button>
            <button
              type="button"
              class="btn btn-primary"
              @click="deleteSubCategory"
            >
              Có
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { Modal } from "bootstrap";
import apiClient from "@/services/api"; // Import the configured apiClient
import CategoryModal from "./CategoryModal.vue"; // Import the new modal component

// --- State ---
const subCategories = ref([]); // Holds the list of SubCategories (Types) for the selected Category (Brand)
const categories = ref([]); // Holds the list of all Categories (Brands)
const selectedCategoryName = ref(""); // The name of the currently selected Category (Brand) for filtering

const pagination = reactive({
  currentPage: 0, // API uses 0-based indexing
  pageSize: 10,
  totalPages: 0,
  totalItems: 0,
});

const error = ref(null);
const success = ref(null);

// --- Modal State & Refs ---
const showModal = ref(false);
const isEditMode = ref(false);
const selectedCategoryType = ref(null); // Data to pass to the modal for editing

const deleteModalInstance = ref(null); // Keep for delete confirm

const deleteId = ref(null);

// --- Computed ---
const visiblePages = computed(() => {
  const pages = [];
  if (pagination.totalPages <= 1) return [];
  // Simple pagination for now, can enhance later
  for (let i = 0; i < pagination.totalPages; i++) {
    pages.push(i + 1);
  }
  return pages;
});

// --- Methods ---

// Fetch SubCategories (Types) for the selected Category (Brand) AND all Categories (Brands)
const fetchData = async () => {
  error.value = null;
  // Keep success message if it was just set by save/delete
  // success.value = null;
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      categoryName: selectedCategoryName.value || null, // Send null if no category selected initially
    };
    const response = await apiClient.get("/crud/categories", { params });

    if (response && response.data) {
      subCategories.value = response.data.subCategories || [];
      categories.value = response.data.categories || [];
      pagination.totalPages = response.data.totalPages || 0;
      pagination.totalItems = response.data.totalItems || 0;
      pagination.currentPage = response.data.currentPage || 0;
      // Only set selectedCategoryName if it wasn't set before OR if backend sends a different one
      if (
        !selectedCategoryName.value ||
        selectedCategoryName.value !== response.data.selectedCategory
      ) {
        selectedCategoryName.value =
          response.data.selectedCategory ||
          (categories.value.length > 0 ? categories.value[0].name : "");
      }
    } else {
      console.warn("Invalid response structure from /crud/categories");
      resetDataOnError();
    }
  } catch (err) {
    console.error("Error fetching data:", err);
    error.value =
      "Không thể tải dữ liệu: " + (err.response?.data?.error || err.message);
    resetDataOnError();
  }
};

const resetDataOnError = () => {
  subCategories.value = [];
  categories.value = [];
  pagination.totalPages = 0;
  pagination.currentPage = 0;
  pagination.totalItems = 0;
};

// Filter when category dropdown changes
const filterByCategory = () => {
  pagination.currentPage = 0; // Reset to first page
  fetchData();
};

// Change page handler
const changePage = (pageZeroIndexed) => {
  if (pageZeroIndexed >= 0 && pageZeroIndexed < pagination.totalPages) {
    pagination.currentPage = pageZeroIndexed;
    fetchData();
  }
};

// Automatically hide alerts after a delay
const setupAlertAutoHide = () => {
  if (error.value) {
    setTimeout(() => {
      error.value = null;
    }, 5000);
  }
  if (success.value) {
    setTimeout(() => {
      success.value = null;
    }, 5000);
  }
};

// --- Modal Handling ---
const openAddModal = () => {
  isEditMode.value = false;
  // Prepare data for add mode, maybe pre-select current brand
  selectedCategoryType.value = {
    categoryName:
      selectedCategoryName.value ||
      (categories.value.length > 0 ? categories.value[0].name : ""),
    subCategoriesName: "",
    status: 1,
  };
  showModal.value = true;
};

const openEditModal = (subCategory) => {
  isEditMode.value = true;
  selectedCategoryType.value = { ...subCategory }; // Pass a copy of the data
  showModal.value = true;
};

const handleCloseModal = () => {
  showModal.value = false;
  selectedCategoryType.value = null; // Clear selection
};

const handleSave = (message) => {
  showModal.value = false;
  selectedCategoryType.value = null;
  success.value = message;
  fetchData(); // Refresh data
  setupAlertAutoHide();
};

// --- Delete Modal ---
const openDeleteConfirmModal = (id) => {
  deleteId.value = id;
  error.value = null;
  deleteModalInstance.value?.show();
};

const closeDeleteModal = () => {
  deleteModalInstance.value?.hide();
};

const deleteSubCategory = async () => {
  error.value = null;
  success.value = null;
  if (!deleteId.value) return;
  try {
    const response = await apiClient.delete(
      `/crud/categories/delete/${deleteId.value}`
    );
    success.value = response.data.message || "Xóa thành công!";
    closeDeleteModal();
    // Adjust page if last item deleted (optional, depends on UX preference)
    if (subCategories.value.length === 1 && pagination.currentPage > 0) {
      pagination.currentPage--;
    }
    fetchData(); // Refresh data
    setupAlertAutoHide();
  } catch (err) {
    console.error("Error deleting subcategory:", err);
    error.value = "Lỗi khi xóa: " + (err.response?.data?.error || err.message);
    setupAlertAutoHide();
  }
};

// --- Lifecycle Hook ---
onMounted(() => {
  // Initialize ONLY Delete Modal instance here
  const deleteModalEl = document.getElementById("confirmDeleteModal");
  if (deleteModalEl) deleteModalInstance.value = new Modal(deleteModalEl);

  // Initial data load
  fetchData();
});
</script>

<style>
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
  text-align: center;
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
  color: #fff;
  padding: 15px;
  vertical-align: middle;
}

.btn-warning {
  background: #d4af37;
  color: #111111;
  border: none;
  /* margin-right: 5px; */ /* Removed margin for better spacing */
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
  filter: invert(1); /* Ensure visibility on dark background */
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

#categorySelect {
  background: #111111;
  border: 1px solid #d4af37;
  color: #fff;
  padding: 8px 16px;
  border-radius: 5px;
  /* margin-left: 10px; */ /* Removed margin */
}

#categorySelect:focus {
  background: #111111;
  border-color: #b4941e;
  box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25);
  color: #fff;
}

label[for="categorySelect"] {
  color: #d4af37;
}

.alert {
  margin-top: 20px;
}
</style>
