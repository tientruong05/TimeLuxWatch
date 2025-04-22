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

      <!-- Table for desktop and tablet -->
      <div class="table-responsive d-none d-md-block">
        <table class="table">
          <thead>
            <tr>
              <th class="col-stt">STT</th>
              <th class="col-name">Tên sản phẩm</th>
              <th class="col-image">Ảnh</th>
              <th class="col-category">Loại hàng</th>
              <th class="col-brand">Hãng</th>
              <th class="col-price">Giá</th>
              <th class="col-qty">Số lượng</th>
              <th class="col-desc d-lg-table-cell">Mô tả</th>
              <th class="col-status">Trạng thái</th>
              <th class="col-action">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(product, index) in products" :key="product.id">
              <td class="col-stt">
                {{ index + 1 + pagination.currentPage * pagination.pageSize }}
              </td>
              <td class="col-name text-ellipsis">{{ product.name }}</td>
              <td class="col-image">
                <img
                  :src="getImageUrl(product.image)"
                  alt="Product Image"
                  class="product-image"
                  @error="setDefaultImage"
                />
              </td>
              <td class="col-category">
                {{ product.subCategory?.category?.name || "N/A" }}
              </td>
              <td class="col-brand">
                {{ product.subCategory?.subCategoriesName || "N/A" }}
              </td>
              <td class="col-price">
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
              <td class="col-qty">{{ product.qty }}</td>
              <td class="col-desc d-lg-table-cell">
                {{ product.description || "N/A" }}
              </td>
              <td class="col-status">
                {{ product.status == 1 ? "Hoạt động" : "Khóa" }}
              </td>
              <td class="col-action">
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
      </div>

      <!-- Card layout for mobile -->
      <div class="product-card-container d-md-none">
        <div
          v-for="(product, index) in products"
          :key="product.id"
          class="product-card"
        >
          <div class="product-card-header">
            <span class="product-index">
              {{ index + 1 + pagination.currentPage * pagination.pageSize }}
            </span>
            <h3>{{ product.name }}</h3>
          </div>
          <div class="product-card-body">
            <img
              :src="getImageUrl(product.image)"
              alt="Product Image"
              class="product-image"
              @error="setDefaultImage"
            />
            <div class="product-details">
              <p>
                <strong>Loại hàng:</strong>
                {{ product.subCategory?.category?.name || "N/A" }}
              </p>
              <p>
                <strong>Hãng:</strong>
                {{ product.subCategory?.subCategoriesName || "N/A" }}
              </p>
              <p>
                <strong>Giá:</strong>
                <span v-if="product.discountPercentage">
                  {{ formatPrice(product.discountedPrice) }}
                  <br />
                  <span style="text-decoration: line-through; color: #999">{{
                    formatPrice(product.price)
                  }}</span>
                  <span style="color: #d4af37">
                    ({{ product.discountPercentage }}% OFF)
                  </span>
                </span>
                <span v-else>{{ formatPrice(product.price) }}</span>
              </p>
              <p><strong>Số lượng:</strong> {{ product.qty }}</p>
              <p>
                <strong>Trạng thái:</strong>
                {{ product.status == 1 ? "Hoạt động" : "Khóa" }}
              </p>
            </div>
          </div>
          <div class="product-card-footer">
            <button
              class="btn btn-warning btn-sm"
              @click="openEditModal(product)"
            >
              <i class="fas fa-edit"></i> Sửa
            </button>
            <button
              class="btn btn-danger btn-sm"
              @click="openDeleteModal(product)"
            >
              <i class="fas fa-trash"></i> Xóa
            </button>
          </div>
        </div>
        <div v-if="products.length === 0" class="no-products">
          Không tìm thấy sản phẩm nào
        </div>
      </div>

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
// Giữ nguyên toàn bộ phần <script> từ file gốc
import { ref, reactive, computed, onMounted } from "vue";
import { Modal } from "bootstrap";
import apiClient from "@/services/api";
import { formatPrice as formatPriceUtil } from "@/utils/formatters";

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
  imageFile: null,
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
  image: "",
  imageFile: null,
});

const deletingProduct = reactive({
  id: null,
  name: "",
});

const successMessage = ref(null);
const errorMessage = ref(null);

const addModalInstance = ref(null);
const editModalInstance = ref(null);
const deleteModalInstance = ref(null);
const addImageFileRef = ref(null);
const editImageFileRef = ref(null);

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

      const newTotalPages =
        typeof response.data.totalPages === "number" &&
        response.data.totalPages >= 0
          ? response.data.totalPages
          : 0;
      pagination.totalPages = newTotalPages;

      const responseCurrentPage = response.data.currentPage ?? 0;

      if (newTotalPages > 0 && responseCurrentPage >= newTotalPages) {
        console.warn(
          `Current page ${responseCurrentPage} is out of bounds (${newTotalPages}). Adjusting to last page.`
        );
        pagination.currentPage = Math.max(0, newTotalPages - 1);
        if (!isRetry) {
          loadProducts(true);
          return;
        }
      } else {
        pagination.currentPage = responseCurrentPage;
      }

      categories.value = Array.isArray(response.data.categories)
        ? response.data.categories
        : [];
      subcategories.value = Array.isArray(response.data.subcategories)
        ? response.data.subcategories
        : [];
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
    pagination.currentPage = 0;
  }
};

const applyFilters = () => {
  pagination.currentPage = 0;
  loadProducts();
};

const changePage = (page) => {
  if (
    page >= 0 &&
    page < pagination.totalPages &&
    page !== pagination.currentPage
  ) {
    pagination.currentPage = page;
    loadProducts();
  }
};

const openAddModal = () => {
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
    addImageFileRef.value.value = null;
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
  if (!validatePrice(newProduct.price)) return;
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

const openEditModal = (product) => {
  Object.assign(editingProduct, {
    id: product.id,
    name: product.name,
    categoryId: product.subCategory?.category?.id || "",
    subCategoryId: product.subCategory?.id || "",
    price: product.price,
    qty: product.qty,
    description: product.description || "",
    status: product.status.toString(),
    image: product.image,
    imageFile: null,
  });

  if (editImageFileRef.value) {
    editImageFileRef.value.value = null;
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
  } else {
    editingProduct.imageFile = null;
  }
};

const updateProduct = async () => {
  if (!validatePrice(editingProduct.price)) return;
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
    loadProducts();
  } catch (error) {
    console.error("Error deleting product:", error);
    errorMessage.value = error.response?.data?.error || "Lỗi khi xóa sản phẩm";
    setTimeout(() => (errorMessage.value = null), 5000);
  }
};

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

const formatPrice = (price) => {
  return formatPriceUtil(price);
};

const setDefaultImage = (event) => {
  event.target.src = "/placeholder.png";
};

const getImageUrl = (imageName) => {
  const baseUrl = "http://localhost:8080";
  const placeholder = "/placeholder.png";
  if (!imageName) {
    return placeholder;
  }
  return `${baseUrl}/photos/${imageName}`;
};

onMounted(() => {
  const addModalEl = document.getElementById("addProductModal");
  if (addModalEl) addModalInstance.value = new Modal(addModalEl);

  const editModalEl = document.getElementById("editProductModal");
  if (editModalEl) editModalInstance.value = new Modal(editModalEl);

  const deleteModalEl = document.getElementById("deleteConfirmModal");
  if (deleteModalEl) deleteModalInstance.value = new Modal(deleteModalEl);

  loadProducts();

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
body {
  background: #111111;
  color: #d4af37;
  font-family: "Helvetica Neue", Arial, sans-serif;
}

.table-container {
  background: #222222;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
  padding: 20px;
  margin-top: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

.table-container h2 {
  color: #d4af37;
  font-weight: 300;
  letter-spacing: 1px;
  margin-bottom: 20px;
}

.btn-success {
  background: #d4af37;
  color: #111111;
  border: none;
  padding: 8px 20px;
  border-radius: 5px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-success:hover {
  background: #b4941e;
  color: #111111;
  transform: translateY(-2px);
}

.table-responsive {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  table-layout: auto;
}

table thead tr th {
  background-color: #111111 !important;
  color: #d4af37 !important;
  text-align: center;
  border: none !important;
  padding: 10px 8px;
  font-weight: 400;
  letter-spacing: 1px;
  white-space: nowrap;
}

tbody tr {
  background: rgba(212, 175, 55, 0.05);
  transition: all 0.3s ease;
}

tbody td {
  border: none !important;
  padding: 10px 8px;
  vertical-align: middle;
  font-size: 0.9rem;
}

/* Tối ưu chiều rộng cột */
.col-stt {
  width: 60px;
  text-align: center;
}

.col-name {
  max-width: 200px;
}

.col-image {
  width: 80px;
  text-align: center;
}

.col-category,
.col-brand {
  max-width: 120px;
}

.col-price {
  max-width: 150px;
  white-space: nowrap;
}

.col-qty {
  width: 100px;
  text-align: center;
}

.col-desc {
  max-width: 200px;
}

.col-status {
  width: 100px;
  text-align: center;
}

.col-action {
  width: 100px;
  text-align: center;
}

/* Xử lý văn bản dài */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  padding: 5px 10px;
}

.btn-danger {
  background: #2c2c2c;
  color: #d4af37;
  border: 1px solid #d4af37;
  padding: 5px 10px;
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

.form-control,
.form-select {
  background: #111111;
  border: 1px solid #d4af37;
  color: #fff;
  height: 38px;
  padding: 6px;
  font-size: 0.9rem;
}

.form-control:focus,
.form-select:focus {
  background: #111111;
  border-color: #b4941e;
  box-shadow: 0 0 0 0.2rem rgba(212, 175, 55, 0.25);
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

.modal .btn-secondary:hover {
  background: #3a3a3a;
  transform: translateY(-1px);
}

.modal .btn-primary {
  background: #d4af37;
  color: #111111;
  border: none;
}

.modal .btn-primary:hover {
  background: #b4941e;
  transform: translateY(-1px);
}

.modal .btn-danger {
  background: #dc3545;
  color: #fff;
  border: none;
}

.modal .btn-danger:hover {
  background: #c82333;
  transform: translateY(-1px);
}

.pagination {
  margin-top: 15px;
  justify-content: center;
  display: flex;
  flex-wrap: wrap;
}

.page-link {
  background-color: #222222;
  color: #d4af37;
  border: 1px solid rgba(212, 175, 55, 0.2);
  margin: 0 2px;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 0.9rem;
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
  padding: 15px;
  box-shadow: 0 4px 15px rgba(212, 175, 55, 0.1);
  border: 1px solid rgba(212, 175, 55, 0.2);
  margin-bottom: 20px;
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
}

.search-filter-container .btn-primary:hover {
  background: #c09d2e;
  transform: translateY(-1px);
}

.search-filter-container .btn-secondary {
  background: #444;
  color: #ccc;
  border: 1px solid #555;
}

.search-filter-container .btn-secondary:hover {
  background: #505050;
  color: #fff;
  border-color: #666;
  transform: translateY(-1px);
}

.alert {
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 0.85rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  margin-bottom: 10px;
  width: 100%;
  max-width: 500px;
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
  padding: 10px;
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
  font-size: 20px;
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

.product-card-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-card {
  background: rgba(212, 175, 55, 0.05);
  border-radius: 8px;
  padding: 12px;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

.product-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.product-card-header .product-index {
  background: #d4af37;
  color: #111111;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
}

.product-card-header h3 {
  font-size: 1rem;
  color: #d4af37;
  margin: 0;
  flex-grow: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-card-body {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.product-card-body .product-image {
  width: 50px;
  height: 50px;
  flex-shrink: 0;
}

.product-card-body .product-details {
  flex-grow: 1;
}

.product-card-body .product-details p {
  margin: 0 0 6px;
  font-size: 0.85rem;
  color: #fff;
}

.product-card-body .product-details p strong {
  color: #d4af37;
}

.product-card-footer {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 8px;
}

.no-products {
  text-align: center;
  padding: 15px;
  color: #fff;
  font-size: 0.9rem;
}

@media (max-width: 991px) {
  .col-desc {
    display: none !important; /* Ẩn cột Mô tả trên tablet */
  }
}

@media (max-width: 767px) {
  .table-container {
    padding: 12px;
  }

  .alert {
    max-width: 100%;
    font-size: 0.8rem;
  }

  .btn-success {
    padding: 6px 12px;
    font-size: 0.85rem;
  }

  .search-filter-container {
    padding: 12px;
  }

  .search-filter-container .col-lg-3,
  .search-filter-container .col-lg-2 {
    width: 100%;
    margin-bottom: 8px;
  }

  .search-filter-container .row {
    flex-direction: column;
    gap: 0;
  }

  .search-filter-container .form-control,
  .search-filter-container .form-select {
    height: 36px;
    font-size: 0.85rem;
  }

  .pagination {
    margin-top: 10px;
  }

  .page-link {
    padding: 4px 8px;
    font-size: 0.85rem;
  }
}

@media (max-width: 576px) {
  .table-container h2 {
    font-size: 1.2rem;
  }

  .product-card-header h3 {
    font-size: 0.95rem;
  }

  .product-card-body .product-details p {
    font-size: 0.8rem;
  }

  .product-card-footer .btn-sm {
    padding: 4px 8px;
    font-size: 0.8rem;
  }
}

.search-input::placeholder {
  color: #a0a0a0;
  font-weight: 500;
  opacity: 0.8;
}

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
</style>
