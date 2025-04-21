<template>
  <div class="container py-5">
    <div class="table-container">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản lý sản phẩm</h2>
        <div
          v-if="successMessage"
          class="alert alert-success alert-dismissible fade show"
          role="alert"
        >
          <i class="fas fa-check-circle"></i>
          {{ successMessage }}
          <button
            type="button"
            class="btn-close"
            @click="successMessage = null"
          ></button>
        </div>
        <div
          v-if="errorMessage"
          class="alert alert-danger alert-dismissible fade show"
          role="alert"
        >
          <i class="fas fa-times-circle"></i>
          {{ errorMessage }}
          <button
            type="button"
            class="btn-close"
            @click="errorMessage = null"
          ></button>
        </div>
        <div class="d-flex gap-3">
          <button class="btn btn-success" @click="openAddModal">
            <i class="fas fa-plus me-2"></i>Thêm sản phẩm
          </button>
        </div>
      </div>

      <div class="search-filter-container mb-4">
        <form @submit.prevent="applyFilters" class="row g-3 align-items-center">
          <input type="hidden" name="page" :value="pagination.currentPage" />
          <input type="hidden" name="size" :value="pagination.pageSize" />
          <div class="col-lg-3">
            <input
              type="text"
              class="form-control search-input"
              v-model="filters.search"
              placeholder="Tìm kiếm theo tên, loại hàng, hãng..."
            />
          </div>
          <div class="col-lg-2">
            <select class="form-select" v-model="filters.categoryId">
              <option value="">Thương hiệu</option>
              <option v-for="cat in categories" :value="cat.id" :key="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>
          <div class="col-lg-2">
            <select
              class="form-select"
              v-model="filters.gender"
              id="filterGender"
            >
              <option value="">Giới tính</option>
              <option value="Đồng hồ nam">Đồng hồ nam</option>
              <option value="Đồng hồ nữ">Đồng hồ nữ</option>
            </select>
          </div>
          <div class="col-lg-2">
            <select class="form-select" v-model="filters.status">
              <option value="">Trạng thái</option>
              <option value="1">Hoạt động</option>
              <option value="0">Khóa</option>
            </select>
          </div>
          <div class="col-lg-3 d-flex gap-2">
            <button type="submit" class="btn btn-primary flex-grow-1">
              <i class="fas fa-filter me-1"></i>Lọc
            </button>
            <button
              type="button"
              class="btn btn-secondary flex-grow-1"
              @click="resetFiltersAndLoad"
            >
              <i class="fas fa-times me-1"></i>Xóa lọc
            </button>
          </div>
        </form>
      </div>

      <table class="table">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên sản phẩm</th>
            <th>Ảnh</th>
            <th>Loại hàng</th>
            <th>Hãng</th>
            <th>Giá</th>
            <th>Số lượng</th>
            <th>Mô tả</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(product, index) in products" :key="product.id">
            <td>
              {{ index + 1 + pagination.currentPage * pagination.pageSize }}
            </td>
            <td>{{ product.name }}</td>
            <td>
              <img
                :src="getImageUrl(product.image)"
                alt="Product Image"
                class="product-image"
                @error="setDefaultImage"
              />
            </td>
            <td>{{ product.subCategory?.category?.name || "N/A" }}</td>
            <td>{{ product.subCategory?.subCategoriesName || "N/A" }}</td>
            <td>
              <span v-if="product.discountPercentage">
                {{ formatPrice(product.discountedPrice) }}
                <br />
                <span style="text-decoration: line-through; color: #999">{{
                  formatPrice(product.price)
                }}</span>
                <span style="color: #d4af37">
                  ({{ product.discountPercentage }}% OFF)</span
                >
              </span>
              <span v-else>{{ formatPrice(product.price) }}</span>
            </td>
            <td>{{ product.qty }}</td>
            <td>{{ product.description || "N/A" }}</td>
            <td>{{ product.status == 1 ? "Hoạt động" : "Khóa" }}</td>
            <td style="min-width: 80px">
              <div class="d-flex gap-1">
                <button
                  class="btn btn-warning btn-sm"
                  @click="openEditModal(product)"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button
                  class="btn btn-danger btn-sm"
                  @click="openDeleteModal(product)"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="products.length === 0">
            <td colspan="10" style="text-align: center">
              Không tìm thấy sản phẩm nào
            </td>
          </tr>
        </tbody>
      </table>

      <nav aria-label="Page navigation" v-if="pagination.totalPages > 0">
        <ul class="pagination">
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
            class="page-item"
            v-for="i in visiblePages"
            :key="i"
            :class="{ active: pagination.currentPage === i }"
          >
            <a class="page-link" href="#" @click.prevent="changePage(i)">{{
              i + 1
            }}</a>
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

      <!-- Modal thêm sản phẩm -->
      <div class="modal fade" id="addProductModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Thêm sản phẩm mới</h5>
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                @click="closeAddModal"
              ></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="saveProduct">
                <div class="mb-3">
                  <label class="form-label">Tên sản phẩm</label>
                  <input
                    type="text"
                    v-model="newProduct.name"
                    class="form-control"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Loại hàng</label>
                  <select
                    v-model="newProduct.categoryId"
                    class="form-select"
                    required
                    @change="filterSubCategories"
                  >
                    <option value="" disabled selected>Chọn loại hàng</option>
                    <option
                      v-for="cat in categories"
                      :value="cat.id"
                      :key="cat.id"
                    >
                      {{ cat.name }}
                    </option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Hãng</label>
                  <select
                    v-model="newProduct.subCategoryId"
                    class="form-select"
                    required
                    id="addSubCategory"
                    :disabled="
                      !newProduct.categoryId ||
                      filteredSubCategoriesForAdd.length === 0
                    "
                  >
                    <option value="" disabled selected>Chọn hãng</option>
                    <option
                      v-for="subcat in filteredSubCategoriesForAdd"
                      :value="subcat.id"
                      :key="subcat.id"
                    >
                      {{ subcat.subCategoriesName }}
                    </option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Giá</label>
                  <input
                    type="number"
                    v-model.number="newProduct.price"
                    class="form-control"
                    min="1000000"
                    step="1000"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Số lượng</label>
                  <input
                    type="number"
                    v-model.number="newProduct.qty"
                    class="form-control"
                    min="0"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Mô tả</label>
                  <textarea
                    v-model="newProduct.description"
                    class="form-control"
                    rows="3"
                  ></textarea>
                </div>
                <div class="mb-3">
                  <label class="form-label">Trạng thái</label>
                  <select v-model="newProduct.status" class="form-select">
                    <option value="1">Hoạt động</option>
                    <option value="0">Khóa</option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Ảnh</label>
                  <input
                    type="file"
                    ref="addImageFileRef"
                    class="form-control"
                    accept="image/*"
                    @change="handleAddImageChange"
                  />
                </div>
                <div class="modal-footer">
                  <button
                    type="button"
                    class="btn btn-secondary"
                    data-bs-dismiss="modal"
                    @click="closeAddModal"
                  >
                    Đóng
                  </button>
                  <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal sửa sản phẩm -->
      <div class="modal fade" id="editProductModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Sửa thông tin sản phẩm</h5>
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                @click="closeEditModal"
              ></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="updateProduct">
                <div class="mb-3">
                  <label class="form-label">Tên sản phẩm</label>
                  <input
                    type="text"
                    v-model="editingProduct.name"
                    class="form-control"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Loại hàng</label>
                  <select
                    v-model="editingProduct.categoryId"
                    class="form-select"
                    required
                    @change="filterSubCategoriesEdit"
                  >
                    <option value="" disabled>Chọn loại hàng</option>
                    <option
                      v-for="cat in categories"
                      :value="cat.id"
                      :key="cat.id"
                    >
                      {{ cat.name }}
                    </option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Hãng</label>
                  <select
                    v-model="editingProduct.subCategoryId"
                    class="form-select"
                    required
                    id="editSubCategory"
                    :disabled="
                      !editingProduct.categoryId ||
                      filteredSubCategoriesForEdit.length === 0
                    "
                  >
                    <option value="" disabled>Chọn hãng</option>
                    <option
                      v-for="subcat in filteredSubCategoriesForEdit"
                      :value="subcat.id"
                      :key="subcat.id"
                    >
                      {{ subcat.subCategoriesName }}
                    </option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Giá</label>
                  <input
                    type="number"
                    v-model.number="editingProduct.price"
                    class="form-control"
                    min="1000000"
                    step="1000"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Số lượng</label>
                  <input
                    type="number"
                    v-model.number="editingProduct.qty"
                    class="form-control"
                    min="0"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Mô tả</label>
                  <textarea
                    v-model="editingProduct.description"
                    class="form-control"
                    rows="3"
                  ></textarea>
                </div>
                <div class="mb-3">
                  <label class="form-label">Trạng thái</label>
                  <select v-model="editingProduct.status" class="form-select">
                    <option value="1">Hoạt động</option>
                    <option value="0">Khóa</option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Ảnh hiện tại</label>
                  <div>
                    <img
                      :src="getImageUrl(editingProduct.image)"
                      alt="Current Image"
                      class="product-image"
                      @error="setDefaultImage"
                    />
                  </div>
                </div>
                <div class="mb-3">
                  <label class="form-label">Thay đổi ảnh</label>
                  <input
                    type="file"
                    ref="editImageFileRef"
                    class="form-control"
                    accept="image/*"
                    @change="handleEditImageChange"
                  />
                </div>
                <div class="modal-footer">
                  <button
                    type="button"
                    class="btn btn-secondary"
                    data-bs-dismiss="modal"
                    @click="closeEditModal"
                  >
                    Đóng
                  </button>
                  <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal xác nhận xóa -->
      <div class="modal fade" id="deleteConfirmModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Xác nhận xóa sản phẩm</h5>
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                @click="closeDeleteModal"
              ></button>
            </div>
            <div class="modal-body">
              <div class="delete-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <p>
                  Bạn có chắc muốn xóa sản phẩm
                  <strong>{{ deletingProduct.name }}</strong> không? Hành động
                  này không thể hoàn tác.
                </p>
              </div>
            </div>
            <div class="modal-footer">
              <button
                type="button"
                class="btn btn-secondary"
                data-bs-dismiss="modal"
                @click="closeDeleteModal"
              >
                Hủy
              </button>
              <button
                type="button"
                class="btn btn-danger"
                @click="confirmDelete"
              >
                Xác nhận
              </button>
            </div>
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
import { formatPrice as formatPriceUtil } from "@/utils/formatters"; // Assuming you have a formatter utility

// --- State --- Refs for simple values, Reactive for objects
const products = ref([]);
const categories = ref([]);
const subcategories = ref([]);
const filteredSubCategoriesForAdd = ref([]);
const filteredSubCategoriesForEdit = ref([]);

const filters = reactive({
  search: "",
  categoryId: "",
  gender: "",
  status: "",
});

const pagination = reactive({
  currentPage: 0,
  pageSize: 10,
  totalPages: 0,
});

const newProduct = reactive({
  name: "",
  categoryId: "",
  subCategoryId: "",
  price: 0,
  qty: 0,
  description: "",
  status: "1",
  imageFile: null, // Use null for file objects initially
});

const editingProduct = reactive({
  id: null,
  name: "",
  categoryId: "",
  subCategoryId: "",
  price: 0,
  qty: 0,
  description: "",
  status: "1",
  image: "", // Existing image name
  imageFile: null, // New file object
});

const deletingProduct = reactive({
  id: null,
  name: "",
});

const successMessage = ref(null);
const errorMessage = ref(null);

// Refs for modals and file inputs
const addModalInstance = ref(null);
const editModalInstance = ref(null);
const deleteModalInstance = ref(null);
const addImageFileRef = ref(null); // Ref for the add form file input
const editImageFileRef = ref(null); // Ref for the edit form file input

// --- Computed ---
const visiblePages = computed(() => {
  if (typeof pagination.totalPages !== "number" || pagination.totalPages <= 0) {
    return [];
  }
  const start = Math.max(0, pagination.currentPage - 2);
  const end = Math.min(pagination.totalPages - 1, pagination.currentPage + 2);
  if (end < start) {
    return [];
  }
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

// --- Methods ---
const resetFilters = () => {
  filters.search = "";
  filters.categoryId = "";
  filters.gender = "";
  filters.status = "";
};

const resetFiltersAndLoad = () => {
  resetFilters();
  applyFilters();
};

const loadProducts = async (isRetry = false) => {
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      search: filters.search || null,
      categoryId: filters.categoryId || null,
      gender: filters.gender || null,
      status: filters.status || null,
    };
    const response = await apiClient.get("/crud/products", { params });

    if (response && response.data) {
      products.value = Array.isArray(response.data.products)
        ? response.data.products
        : [];

      // Store the *new* total pages count
      const newTotalPages =
        typeof response.data.totalPages === "number" &&
        response.data.totalPages >= 0
          ? response.data.totalPages
          : 0;
      pagination.totalPages = newTotalPages;

      // Use the current page from the response IF VALID, otherwise adjust
      const responseCurrentPage = response.data.currentPage ?? 0;

      // ---- Pagination Stability Check ----
      if (newTotalPages > 0 && responseCurrentPage >= newTotalPages) {
        // If the current page from response is invalid (e.g., after deletion/filtering)
        // Go to the new last page
        console.warn(
          `Current page ${responseCurrentPage} is out of bounds (${newTotalPages}). Adjusting to last page.`
        );
        pagination.currentPage = Math.max(0, newTotalPages - 1);
        // Avoid infinite loops if retrying already
        if (!isRetry) {
          loadProducts(true); // Reload data for the adjusted page
          return; // Exit current execution to avoid further processing with old data
        }
      } else {
        // If the current page is valid, update it from the response
        pagination.currentPage = responseCurrentPage;
      }
      // ---- End Pagination Stability Check ----

      // Update categories and subcategories (for modals)
      categories.value = Array.isArray(response.data.categories)
        ? response.data.categories
        : [];
      subcategories.value = Array.isArray(response.data.subcategories)
        ? response.data.subcategories
        : [];
      // Initialize modal subcategory filters if needed
      if (
        filteredSubCategoriesForAdd.value.length === 0 &&
        subcategories.value.length > 0 &&
        !newProduct.categoryId
      ) {
        filterSubCategories();
      }
      if (
        filteredSubCategoriesForEdit.value.length === 0 &&
        subcategories.value.length > 0 &&
        !editingProduct.categoryId
      ) {
        filterSubCategoriesEdit();
      }
    } else {
      console.warn("Invalid response structure from /crud/products");
      products.value = [];
      categories.value = [];
      subcategories.value = [];
      pagination.totalPages = 0;
    }
  } catch (error) {
    console.error("Error loading products:", error);
    errorMessage.value = "Lỗi khi tải dữ liệu sản phẩm";
    setTimeout(() => (errorMessage.value = null), 5000);
    products.value = [];
    categories.value = [];
    subcategories.value = [];
    pagination.totalPages = 0;
    pagination.currentPage = 0; // Reset page on error too
  }
};

const applyFilters = () => {
  pagination.currentPage = 0; // Reset page to 0 when filtering
  loadProducts();
};

const changePage = (page) => {
  if (
    page >= 0 &&
    page < pagination.totalPages &&
    page !== pagination.currentPage
  ) {
    // Added check to prevent redundant loads
    pagination.currentPage = page;
    loadProducts();
  }
};

// --- Add Modal Logic ---
const openAddModal = () => {
  // Reset newProduct reactive object
  Object.assign(newProduct, {
    name: "",
    categoryId: "",
    subCategoryId: "",
    price: 0,
    qty: 0,
    description: "",
    status: "1",
    imageFile: null,
  });

  if (addImageFileRef.value) {
    addImageFileRef.value.value = null; // Clear file input
  }
  filterSubCategories();
  addModalInstance.value?.show();
};

const closeAddModal = () => {
  addModalInstance.value?.hide();
};

const handleAddImageChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    newProduct.imageFile = file;
    // Basic validation (optional)
  } else {
    newProduct.imageFile = null;
  }
};

const validatePrice = (price) => {
  if (typeof price !== "number" || price < 1000000) {
    errorMessage.value = "Giá sản phẩm phải từ 1.000.000 VND trở lên.";
    setTimeout(() => (errorMessage.value = null), 4000);
    return false;
  }
  return true;
};

const saveProduct = async () => {
  if (!validatePrice(newProduct.price)) return; // Add price validation check
  try {
    const formData = new FormData();
    formData.append("name", newProduct.name);
    formData.append("categoryId", newProduct.categoryId);
    formData.append("subCategoryId", newProduct.subCategoryId);
    formData.append("price", newProduct.price.toString());
    formData.append("qty", newProduct.qty.toString());
    formData.append("description", newProduct.description || "");
    formData.append("status", newProduct.status);
    if (newProduct.imageFile) {
      formData.append("imageFile", newProduct.imageFile);
    }
    await apiClient.post("/crud/products/save", formData);
    successMessage.value = "Thêm sản phẩm thành công!";
    setTimeout(() => (successMessage.value = null), 5000);
    addModalInstance.value?.hide();
    loadProducts();
  } catch (error) {
    console.error("Error saving product:", error);
    errorMessage.value = error.response?.data?.error || "Lỗi khi thêm sản phẩm";
    setTimeout(() => (errorMessage.value = null), 5000);
  }
};

// --- Edit Modal Logic ---
const openEditModal = (product) => {
  // Reset editingProduct first
  Object.assign(editingProduct, {
    id: product.id,
    name: product.name,
    categoryId: product.subCategory?.category?.id || "",
    subCategoryId: product.subCategory?.id || "",
    price: product.price,
    qty: product.qty,
    description: product.description || "",
    status: product.status.toString(),
    image: product.image, // Keep existing image name
    imageFile: null, // Reset new file input
  });

  if (editImageFileRef.value) {
    editImageFileRef.value.value = null; // Clear file input
  }
  filterSubCategoriesEdit();
  editModalInstance.value?.show();
};

const closeEditModal = () => {
  editModalInstance.value?.hide();
};

const handleEditImageChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    editingProduct.imageFile = file;
    // Basic validation (optional)
  } else {
    editingProduct.imageFile = null;
  }
};

const updateProduct = async () => {
  if (!validatePrice(editingProduct.price)) return; // Add price validation check
  try {
    const formData = new FormData();
    formData.append("id", editingProduct.id.toString());
    formData.append("name", editingProduct.name);
    formData.append("categoryId", editingProduct.categoryId);
    formData.append("subCategoryId", editingProduct.subCategoryId);
    formData.append("price", editingProduct.price.toString());
    formData.append("qty", editingProduct.qty.toString());
    formData.append("description", editingProduct.description || "");
    formData.append("status", editingProduct.status);
    formData.append("existingImage", editingProduct.image || "");
    if (editingProduct.imageFile) {
      formData.append("imageFile", editingProduct.imageFile);
    }
    await apiClient.post("/crud/products/save", formData);
    successMessage.value = "Cập nhật sản phẩm thành công!";
    setTimeout(() => (successMessage.value = null), 5000);
    editModalInstance.value?.hide();
    loadProducts();
  } catch (error) {
    console.error("Error updating product:", error);
    errorMessage.value =
      error.response?.data?.error || "Lỗi khi cập nhật sản phẩm";
    setTimeout(() => (errorMessage.value = null), 5000);
  }
};

// --- Delete Modal Logic ---
const openDeleteModal = (product) => {
  deletingProduct.id = product.id;
  deletingProduct.name = product.name;
  deleteModalInstance.value?.show();
};

const closeDeleteModal = () => {
  deleteModalInstance.value?.hide();
};

const confirmDelete = async () => {
  if (!deletingProduct.id) return;
  try {
    await apiClient.delete(`/crud/products/delete/${deletingProduct.id}`);
    successMessage.value = "Xóa sản phẩm thành công!";
    setTimeout(() => (successMessage.value = null), 5000);
    deleteModalInstance.value?.hide();
    // No need to manually decrement page here,
    // loadProducts will handle the adjustment if the page becomes invalid
    loadProducts(); // Refresh list
  } catch (error) {
    console.error("Error deleting product:", error);
    errorMessage.value = error.response?.data?.error || "Lỗi khi xóa sản phẩm";
    setTimeout(() => (errorMessage.value = null), 5000);
  }
};

// --- Filtering Logic ---
const filterSubCategories = () => {
  if (!newProduct.categoryId) {
    filteredSubCategoriesForAdd.value = [...subcategories.value];
  } else {
    filteredSubCategoriesForAdd.value = subcategories.value.filter(
      (subcat) => subcat.category?.id == newProduct.categoryId
    );
  }
  if (
    !filteredSubCategoriesForAdd.value.some(
      (sc) => sc.id == newProduct.subCategoryId
    )
  ) {
    newProduct.subCategoryId = "";
  }
};

const filterSubCategoriesEdit = () => {
  if (!editingProduct.categoryId) {
    filteredSubCategoriesForEdit.value = [...subcategories.value];
  } else {
    filteredSubCategoriesForEdit.value = subcategories.value.filter(
      (subcat) => subcat.category?.id == editingProduct.categoryId
    );
  }
  if (
    !filteredSubCategoriesForEdit.value.some(
      (sc) => sc.id == editingProduct.subCategoryId
    )
  ) {
    editingProduct.subCategoryId = "";
  }
};

// --- Formatting & Utilities ---
const formatPrice = (price) => {
  // Use the imported utility or keep existing
  return formatPriceUtil(price);
  // return new Intl.NumberFormat("vi-VN").format(price);
};

const setDefaultImage = (event) => {
  // Consider using a local placeholder if /photos/ path fails
  event.target.src = "/placeholder.png";
};

const getImageUrl = (imageName) => {
  const baseUrl = "http://localhost:8080"; // Define the base URL for images
  const placeholder = "/placeholder.png"; // Or a locally hosted placeholder

  if (!imageName) {
    return placeholder;
  }
  // Construct the full URL
  return `${baseUrl}/photos/${imageName}`;
};

// --- Lifecycle Hook ---
onMounted(() => {
  // Initialize Modals
  const addModalEl = document.getElementById("addProductModal");
  if (addModalEl) addModalInstance.value = new Modal(addModalEl);

  const editModalEl = document.getElementById("editProductModal");
  if (editModalEl) editModalInstance.value = new Modal(editModalEl);

  const deleteModalEl = document.getElementById("deleteConfirmModal");
  if (deleteModalEl) deleteModalInstance.value = new Modal(deleteModalEl);

  // Initial data load
  loadProducts();

  // You can keep the URL param check if needed
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.has("success")) {
    successMessage.value = "Thao tác thành công!";
    setTimeout(() => (successMessage.value = null), 5000);
  }
  if (urlParams.has("error")) {
    errorMessage.value = urlParams.get("error");
    setTimeout(() => (errorMessage.value = null), 5000);
  }
});
</script>

<style scoped>
/* Giữ nguyên tất cả các style từ file gốc */
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
  background: #2a2a2a;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(212, 175, 55, 0.1);
  border: 1px solid rgba(212, 175, 55, 0.2);
  margin-bottom: 25px;
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

.search-filter-container .form-control,
.search-filter-container .form-select {
  background-color: #1e1e1e;
  border-color: #444;
  color: #eee;
  transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.search-filter-container .form-control:focus,
.search-filter-container .form-select:focus {
  background-color: #1e1e1e;
  border-color: #d4af37;
  box-shadow: 0 0 0 0.2rem rgba(212, 175, 55, 0.2);
  color: #fff;
}

.search-filter-container .btn-primary {
  background: #d4af37;
  color: #111;
  border: none;
  transition: background-color 0.2s ease, transform 0.1s ease;
}
.search-filter-container .btn-primary:hover {
  background: #c09d2e;
  transform: translateY(-1px);
}
.search-filter-container .btn-secondary {
  background: #444;
  color: #ccc;
  border: 1px solid #555;
  transition: background-color 0.2s ease, transform 0.1s ease;
}
.search-filter-container .btn-secondary:hover {
  background: #505050;
  color: #fff;
  border-color: #666;
  transform: translateY(-1px);
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

/* Style for the search input placeholder */
.search-input::placeholder {
  color: #a0a0a0;
  font-weight: 500;
  opacity: 0.8;
}

/* You might need vendor prefixes for older browsers */
.search-input::-webkit-input-placeholder {
  color: #a0a0a0;
  font-weight: 500;
  opacity: 0.8;
}
.search-input::-moz-placeholder {
  color: #a0a0a0;
  font-weight: 500;
  opacity: 0.8;
}
.search-input:-ms-input-placeholder {
  color: #a0a0a0;
  font-weight: 500;
  opacity: 0.8;
}
.search-input:-moz-placeholder {
  color: #a0a0a0;
  font-weight: 500;
  opacity: 0.8;
}

/* Optional: Adjust button widths if needed */
.search-filter-container .col-lg-3.d-flex.gap-2 {
  /* Override default width if needed */
  /* width: auto; */
  /* flex-basis: auto; */
}
</style>
