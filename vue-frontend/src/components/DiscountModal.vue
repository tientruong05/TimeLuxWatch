<template>
  <div
    class="modal fade show d-block"
    tabindex="-1"
    style="background-color: rgba(0, 0, 0, 0.5)"
    @click.self="closeModal"
    v-if="show"
  >
    <div class="modal-dialog modal-lg modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ isEditMode ? "Chỉnh sửa giảm giá" : "Thêm giảm giá mới" }}
          </h5>
          <button type="button" class="btn-close" @click="closeModal"></button>
        </div>
        <div class="modal-body">
          <div v-if="isLoading" class="text-center my-3">
            <div class="spinner-border text-warning" role="status">
              <span class="visually-hidden">Đang tải...</span>
            </div>
          </div>
          <div v-if="error" class="alert alert-danger">{{ error }}</div>

          <form v-if="!isLoading && !error" @submit.prevent="saveDiscount">
            <div class="row">
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label">Tên giảm giá:</label>
                  <input
                    type="text"
                    v-model="formData.discountName"
                    class="form-control"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Giá trị giảm giá (%):</label>
                  <input
                    type="number"
                    v-model.number="formData.discountValue"
                    class="form-control"
                    min="0"
                    max="99"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Mã Giảm Giá (Code):</label>
                  <input
                    type="text"
                    v-model="formData.discountCode"
                    class="form-control"
                    :disabled="isEditMode"
                    required
                  />
                  <small v-if="!isEditMode" class="form-text text-muted"
                    >Mã này không thể thay đổi sau khi tạo.</small
                  >
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label">Ngày bắt đầu:</label>
                  <input
                    type="date"
                    v-model="formData.startDate"
                    class="form-control"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Ngày kết thúc:</label>
                  <input
                    type="date"
                    v-model="formData.endDate"
                    class="form-control"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Trạng thái:</label>
                  <select v-model.number="formData.status" class="form-select">
                    <option :value="1">Hoạt động</option>
                    <option :value="0">Không hoạt động</option>
                  </select>
                </div>
              </div>
            </div>

            <hr />
            <h6 class="text-warning">Áp dụng giảm giá cho:</h6>
            <div class="row">
              <div class="col-md-4 mb-3">
                <label class="form-label">Loại hàng (Category):</label>
                <select
                  multiple
                  class="form-select multi-select"
                  v-model="selectedCategoryIds"
                >
                  <option
                    v-for="cat in allCategories"
                    :key="cat.id"
                    :value="cat.id"
                  >
                    {{ cat.name }}
                  </option>
                </select>
              </div>
              <div class="col-md-4 mb-3">
                <label class="form-label">Hãng (SubCategory):</label>
                <select
                  multiple
                  class="form-select multi-select"
                  v-model="selectedSubCategoryIds"
                >
                  <option
                    v-for="subcat in allSubCategories"
                    :key="subcat.id"
                    :value="subcat.id"
                  >
                    {{ subcat.subCategoriesName }} ({{ subcat.category?.name }})
                  </option>
                </select>
              </div>
              <div class="col-md-4 mb-3">
                <label class="form-label">Sản phẩm (Product):</label>
                <select
                  multiple
                  class="form-select multi-select"
                  v-model="selectedProductIds"
                >
                  <option
                    v-for="prod in allProducts"
                    :key="prod.id"
                    :value="prod.id"
                  >
                    {{ prod.name }}
                  </option>
                </select>
              </div>
            </div>
            <small class="form-text text-muted"
              >Giữ Ctrl (hoặc Cmd trên Mac) để chọn nhiều mục.</small
            >
          </form>
        </div>
        <div class="modal-footer">
          <div v-if="submitError" class="alert alert-danger me-auto py-1 px-2">
            {{ submitError }}
          </div>
          <button type="button" class="btn btn-secondary" @click="closeModal">
            Đóng
          </button>
          <button
            type="button"
            class="btn btn-primary"
            @click="saveDiscount"
            :disabled="isSubmitting || isLoading"
          >
            {{ isSubmitting ? "Đang lưu..." : "Lưu" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed } from "vue";
import apiClient from "@/services/api";

// --- Props ---
const props = defineProps({
  discountId: {
    type: Number,
    default: null,
  },
  show: {
    type: Boolean,
    default: false,
  },
});

// --- Emits ---
const emit = defineEmits(["close", "save"]);

// --- State ---
const formData = reactive({
  id: null,
  discountName: "",
  discountValue: 0,
  discountCode: "", // Added discount code
  startDate: "",
  endDate: "",
  status: 1,
});

const allCategories = ref([]);
const allSubCategories = ref([]);
const allProducts = ref([]); // Use ProductDTO structure {id, name, image, price, discountedPrice, discountPercentage, discounted}

const selectedCategoryIds = ref([]);
const selectedSubCategoryIds = ref([]);
const selectedProductIds = ref([]);

const isLoading = ref(false);
const isSubmitting = ref(false);
const error = ref(null); // For initial loading
const submitError = ref(null); // For submission errors

const isEditMode = computed(
  () => props.discountId !== null && props.discountId > 0
);

// --- Fetch Data ---
const fetchModalData = async () => {
  if (!props.show) return;

  isLoading.value = true;
  error.value = null;
  submitError.value = null;
  resetForm(); // Reset form before fetching

  try {
    // Fetch data required for dropdowns AND potentially existing data if editing
    // Use the /api/discounts/{id} endpoint. Use a placeholder ID like 0 or 1 for add mode if backend supports it,
    // otherwise, we need a dedicated endpoint or separate calls.
    // Let's try using discountId = 0 for add mode, assuming backend might handle this.
    // If not, we might need GET /api/categories, GET /api/subcategories, GET /api/products separately.
    // The GET /api/discounts/{id} endpoint returns everything we need.
    const fetchId = isEditMode.value ? props.discountId : 0; // Use 0 for Add mode data fetch

    // Make the API call
    // Note: If ID 0 doesn't work for fetching initial data, adjust this logic.
    // Maybe fetch ID 1 if it likely exists, or call separate endpoints.
    const response = await apiClient.get(`/api/discounts/${fetchId}`);

    allCategories.value = response.data.categories || [];
    allSubCategories.value = response.data.subCategories || [];
    // Assuming products are returned as ProductDTOs
    allProducts.value = response.data.products || [];

    if (isEditMode.value && response.data.discount) {
      const discount = response.data.discount;
      formData.id = discount.id;
      formData.discountName = discount.discountName;
      formData.discountValue = discount.discountValue;
      formData.discountCode = discount.discountCode; // Populate code
      formData.startDate = formatDateForInput(discount.startDate);
      formData.endDate = formatDateForInput(discount.endDate);
      formData.status = discount.status;

      // Populate selected IDs from discountDetails
      const details = response.data.discountDetails || [];
      selectedCategoryIds.value = details
        .filter((d) => d.category)
        .map((d) => d.category.id);
      selectedSubCategoryIds.value = details
        .filter((d) => d.subCategory)
        .map((d) => d.subCategory.id);
      selectedProductIds.value = details
        .filter((d) => d.product)
        .map((d) => d.product.id);
    } else if (!isEditMode.value) {
      // Reset selections for add mode, form is already reset by resetForm()
    }
  } catch (err) {
    console.error("Error fetching modal data:", err);
    if (err.response?.status === 404 && fetchId === 0) {
      // Handle case where ID 0 doesn't work - try fetching categories/subs/products separately
      error.value =
        "Lỗi khi tải dữ liệu ban đầu. Không thể lấy danh sách Hãng/Loại/Sản phẩm.";
      // Implement fallback fetching here if needed
      // e.g., await fetchDropdownDataSeparately();
      await fetchDropdownDataSeparately(); // Try fallback
    } else if (isEditMode.value && err.response?.status === 404) {
      error.value = `Không tìm thấy giảm giá với ID: ${props.discountId}.`;
    } else {
      error.value =
        "Lỗi khi tải dữ liệu: " + (err.response?.data?.error || err.message);
    }
  } finally {
    isLoading.value = false;
  }
};

// Fallback function if GET /api/discounts/0 doesn't work
const fetchDropdownDataSeparately = async () => {
  try {
    // Example: Fetch categories, subcategories, products using other endpoints if available
    // This assumes endpoints like /api/categories, /api/subcategories exist
    // and /api/crud/products returns products
    const [catRes, subCatRes, prodRes] = await Promise.all([
      apiClient.get("/api/categories"), // Adjust endpoint if needed
      apiClient.get("/api/subcategories"), // Adjust endpoint if needed
      apiClient.get("/api/crud/products?size=1000"), // Fetch products (adjust size)
    ]);
    allCategories.value = catRes.data || [];
    allSubCategories.value = subCatRes.data || [];
    allProducts.value = prodRes.data?.products || [];
    error.value = null; // Clear previous error if fallback succeeds
  } catch (fallbackError) {
    console.error("Error fetching dropdown data separately:", fallbackError);
    // Keep the original error message or set a new one
    error.value = "Lỗi nghiêm trọng khi tải dữ liệu Hãng/Loại/Sản phẩm.";
  }
};

// --- Watchers ---
watch(
  () => props.show,
  (newVal) => {
    if (newVal) {
      fetchModalData();
    } else {
      resetForm(); // Reset form when modal is hidden
    }
  }
);

// --- Methods ---
const resetForm = () => {
  formData.id = null;
  formData.discountName = "";
  formData.discountValue = 0;
  formData.discountCode = ""; // Reset code
  formData.startDate = "";
  formData.endDate = "";
  formData.status = 1;
  selectedCategoryIds.value = [];
  selectedSubCategoryIds.value = [];
  selectedProductIds.value = [];
  error.value = null;
  submitError.value = null;
  isSubmitting.value = false;
};

const validateForm = () => {
  submitError.value = null;
  if (!formData.discountName?.trim()) {
    submitError.value = "Vui lòng nhập tên giảm giá.";
    return false;
  }
  if (!formData.discountCode?.trim()) {
    submitError.value = "Vui lòng nhập mã giảm giá.";
    return false;
  }
  if (!formData.startDate) {
    submitError.value = "Vui lòng chọn ngày bắt đầu.";
    return false;
  }
  if (!formData.endDate) {
    submitError.value = "Vui lòng chọn ngày kết thúc.";
    return false;
  }
  if (new Date(formData.endDate) < new Date(formData.startDate)) {
    submitError.value = "Ngày kết thúc không được trước ngày bắt đầu.";
    return false;
  }
  // Add more validation as needed
  return true;
};

const saveDiscount = async () => {
  if (!validateForm()) {
    return;
  }

  isSubmitting.value = true;
  submitError.value = null;

  // Prepare the discount object for the request body
  const discountData = {
    id: isEditMode.value ? formData.id : 0, // Send 0 or null for new
    discountName: formData.discountName,
    discountValue: formData.discountValue,
    discountCode: formData.discountCode, // Include code
    startDate: formData.startDate,
    endDate: formData.endDate,
    status: formData.status,
  };

  // Prepare query parameters
  const params = new URLSearchParams();
  selectedCategoryIds.value.forEach((id) => params.append("categoryIds", id));
  selectedSubCategoryIds.value.forEach((id) =>
    params.append("subCategoryIds", id)
  );
  selectedProductIds.value.forEach((id) => params.append("productIds", id));

  const url = isEditMode.value
    ? `/api/discounts/edit/${formData.id}?${params.toString()}`
    : `/api/discounts/create?${params.toString()}`;
  const method = isEditMode.value ? "put" : "post";

  try {
    const response = await apiClient[method](url, discountData); // Send discountData in body
    emit("save", response.data.message || "Lưu thành công!");
  } catch (err) {
    console.error("Error saving discount:", err);
    submitError.value =
      "Lỗi khi lưu: " +
      (err.response?.data?.error || err.response?.data?.message || err.message);
  } finally {
    isSubmitting.value = false;
  }
};

const closeModal = () => {
  emit("close");
};

// Helper to format date string (YYYY-MM-DD) for input type="date"
const formatDateForInput = (dateString) => {
  if (!dateString) return "";
  try {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const day = date.getDate().toString().padStart(2, "0");
    return `${year}-${month}-${day}`;
  } catch (e) {
    console.error("Error formatting date:", dateString, e);
    return ""; // Return empty if formatting fails
  }
};
</script>

<style scoped>
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

.alert-danger {
  font-size: 0.9em;
}

/* Style for multi-select boxes */
.multi-select {
  height: 150px; /* Adjust height as needed */
}
.multi-select option {
  padding: 5px;
}
</style>
