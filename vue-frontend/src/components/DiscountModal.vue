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

            <!-- Selection Summary -->
            <div
              v-if="
                selectedCategories.length ||
                selectedSubCategories.length ||
                selectedProducts.length
              "
              class="selection-summary mb-3 p-3 border rounded bg-dark"
            >
              <h6 class="text-warning mb-2">Các mục đã chọn áp dụng:</h6>
              <div v-if="selectedCategories.length">
                <strong>Loại hàng:</strong>
                <span
                  v-for="cat in selectedCategories"
                  :key="cat.id"
                  class="badge bg-secondary me-1 mb-1"
                  >{{ cat.name }}</span
                >
              </div>
              <div v-if="selectedSubCategories.length" class="mt-2">
                <strong>Hãng:</strong>
                <span
                  v-for="sub in selectedSubCategories"
                  :key="sub.id"
                  class="badge bg-info text-dark me-1 mb-1"
                  >{{ subCategoryLabel(sub) }}</span
                >
              </div>
              <div v-if="selectedProducts.length" class="mt-2">
                <strong>Sản phẩm:</strong>
                <span
                  v-for="prod in selectedProducts"
                  :key="prod.id"
                  class="badge bg-light text-dark me-1 mb-1"
                  >{{ prod.name }}</span
                >
              </div>
            </div>
            <!-- End Selection Summary -->

            <h6 class="text-warning">Chọn để áp dụng giảm giá:</h6>
            <div class="mb-3">
              <label class="form-label">Loại hàng (Category):</label>
              <VueMultiselect
                v-model="selectedCategories"
                :options="allCategories"
                :multiple="true"
                :close-on-select="false"
                :clear-on-select="false"
                :preserve-search="true"
                placeholder="Tìm hoặc chọn loại hàng"
                label="name"
                track-by="id"
                :preselect-first="false"
              >
                <template #noResult>Không tìm thấy kết quả.</template>
              </VueMultiselect>
            </div>

            <div class="mb-3">
              <label class="form-label">Hãng (SubCategory):</label>
              <VueMultiselect
                v-model="selectedSubCategories"
                :options="filteredSubCategories"
                :multiple="true"
                :close-on-select="false"
                :clear-on-select="false"
                :preserve-search="true"
                placeholder="Tìm hoặc chọn hãng"
                track-by="id"
                :custom-label="subCategoryLabel"
                :preselect-first="false"
              >
                <template #option="{ option }">
                  {{ option.subCategoriesName }} ({{ option.category?.name }})
                </template>
                <template #tag="{ option, remove }">
                  <span class="multiselect__tag">
                    <span
                      >{{ option.subCategoriesName }} ({{
                        option.category?.name
                      }})</span
                    >
                    <i
                      aria-hidden="true"
                      tabindex="1"
                      class="multiselect__tag-icon"
                      @click="remove(option)"
                    ></i>
                  </span>
                </template>
                <template #noResult>Không tìm thấy kết quả.</template>
              </VueMultiselect>
            </div>

            <div class="mb-3">
              <label class="form-label">Sản phẩm (Product):</label>
              <VueMultiselect
                v-model="selectedProducts"
                :options="filteredProducts"
                :multiple="true"
                :close-on-select="false"
                :clear-on-select="false"
                :preserve-search="true"
                placeholder="Tìm hoặc chọn sản phẩm"
                label="name"
                track-by="id"
                :preselect-first="false"
              >
                <template #noResult>Không tìm thấy kết quả.</template>
              </VueMultiselect>
            </div>
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
import VueMultiselect from "vue-multiselect";

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
  startDate: "",
  endDate: "",
  status: 1,
});

const allCategories = ref([]);
const allSubCategories = ref([]);
const allProducts = ref([]); // Use ProductDTO structure {id, name, image, price, discountedPrice, discountPercentage, discounted}

const selectedCategories = ref([]);
const selectedSubCategories = ref([]);
const selectedProducts = ref([]);

const isLoading = ref(false);
const isSubmitting = ref(false);
const error = ref(null); // For initial loading
const submitError = ref(null); // For submission errors

const isEditMode = computed(
  () => props.discountId !== null && props.discountId > 0
);

// --- Computed Properties for Filtering ---
const filteredSubCategories = computed(() => {
  if (selectedCategories.value.length === 0) {
    return allSubCategories.value; // Show all if no category selected
  }
  const selectedCategoryIds = selectedCategories.value.map((cat) => cat.id);
  return allSubCategories.value.filter(
    (sub) => sub.category && selectedCategoryIds.includes(sub.category.id)
  );
});

const filteredProducts = computed(() => {
  const selectedCategoryIds = selectedCategories.value.map((cat) => cat.id);
  const selectedSubCategoryIds = selectedSubCategories.value.map(
    (sub) => sub.id
  );

  if (selectedCategoryIds.length === 0 && selectedSubCategoryIds.length === 0) {
    return allProducts.value; // Show all if nothing selected
  }

  return allProducts.value.filter((prod) => {
    // Check if product's subcategory's category is selected
    const categoryMatch =
      selectedCategoryIds.length > 0 &&
      prod.subCategory?.category &&
      selectedCategoryIds.includes(prod.subCategory.category.id);

    // Check if product's subcategory is selected
    const subCategoryMatch =
      selectedSubCategoryIds.length > 0 &&
      prod.subCategory &&
      selectedSubCategoryIds.includes(prod.subCategory.id);

    // Include product if it matches either selected category or selected subcategory
    return categoryMatch || subCategoryMatch;
  });
});

// --- Fetch Data ---
const fetchModalData = async () => {
  if (!props.show) return;

  isLoading.value = true;
  error.value = null;
  submitError.value = null;
  resetForm(); // Reset form and selections first

  try {
    const fetchId = isEditMode.value ? props.discountId : 0;
    // Assuming GET /api/discounts/0 returns all lists needed for add mode
    // and GET /api/discounts/{id} returns the discount + details + all lists for edit mode
    const response = await apiClient.get(`/discounts/edit-data/${fetchId}`); // Using a potentially combined endpoint

    allCategories.value = response.data.allCategories || [];
    allSubCategories.value = response.data.allSubCategories || [];
    allProducts.value = response.data.allProducts || [];

    if (isEditMode.value && response.data.discount) {
      const discount = response.data.discount;
      formData.id = discount.id;
      formData.discountName = discount.discountName;
      formData.discountValue = discount.discountValue;
      formData.startDate = formatDateForInput(discount.startDate);
      formData.endDate = formatDateForInput(discount.endDate);
      formData.status = discount.status;

      // Map existing selections
      selectedCategories.value = response.data.selectedCategories || [];
      selectedSubCategories.value = response.data.selectedSubCategories || [];
      selectedProducts.value = response.data.selectedProducts || [];
    } else if (!isEditMode.value) {
      // Reset selections for add mode (already done in resetForm)
    }
  } catch (err) {
    console.error("Error fetching modal data:", err);
    error.value =
      "Lỗi khi tải dữ liệu cho modal: " +
      (err.response?.data?.error || err.message);
    // Clear lists on error to prevent issues
    allCategories.value = [];
    allSubCategories.value = [];
    allProducts.value = [];
  } finally {
    isLoading.value = false;
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

// Watch for changes in selectedCategories to potentially clear selectedSubCategories/Products
// This prevents keeping selections that are no longer relevant after changing the category
watch(
  selectedCategories,
  (newVal, oldVal) => {
    if (newVal.length < oldVal.length) {
      // If a category was removed
      const newCategoryIds = newVal.map((c) => c.id);
      // Remove subcategories whose category is no longer selected
      selectedSubCategories.value = selectedSubCategories.value.filter(
        (sub) => sub.category && newCategoryIds.includes(sub.category.id)
      );
      // Products are handled by the computed property filter, but we might also
      // want to explicitly clear product selections if their parent category/subcategory is removed.
      // Let's filter selected products as well:
      selectedProducts.value = selectedProducts.value.filter((prod) => {
        const categoryMatch =
          prod.subCategory?.category &&
          newCategoryIds.includes(prod.subCategory.category.id);
        const subCategoryMatch =
          prod.subCategory &&
          selectedSubCategories.value.some((s) => s.id === prod.subCategory.id);
        return categoryMatch || subCategoryMatch;
      });
    }
  },
  { deep: true }
);

// Watch selectedSubCategories for similar cleanup
watch(
  selectedSubCategories,
  (newVal, oldVal) => {
    if (newVal.length < oldVal.length) {
      // If a subcategory was removed
      const newSubCategoryIds = newVal.map((s) => s.id);
      // Filter selected products
      selectedProducts.value = selectedProducts.value.filter(
        (prod) =>
          prod.subCategory && newSubCategoryIds.includes(prod.subCategory.id)
      );
    }
  },
  { deep: true }
);

// --- Methods ---
const resetForm = () => {
  formData.id = null;
  formData.discountName = "";
  formData.discountValue = 0;
  formData.startDate = "";
  formData.endDate = "";
  formData.status = 1;
  // Also reset selections
  selectedCategories.value = [];
  selectedSubCategories.value = [];
  selectedProducts.value = [];
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

  const discountData = {
    id: isEditMode.value ? formData.id : 0,
    discountName: formData.discountName,
    discountValue: formData.discountValue,
    startDate: formData.startDate,
    endDate: formData.endDate,
    status: formData.status,
  };

  // Prepare query parameters
  const params = new URLSearchParams();
  selectedCategories.value.forEach((cat) =>
    params.append("categoryIds", cat.id)
  );
  selectedSubCategories.value.forEach((sub) =>
    params.append("subCategoryIds", sub.id)
  );
  selectedProducts.value.forEach((prod) =>
    params.append("productIds", prod.id)
  );

  const url = isEditMode.value
    ? `/discounts/edit/${formData.id}?${params.toString()}`
    : `/discounts/create?${params.toString()}`;
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

const subCategoryLabel = (option) => {
  if (option.category) {
    return `${option.subCategoriesName} (Category: ${option.category.name})`;
  }
  return option.subCategoriesName;
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

.selection-summary {
  background-color: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(212, 175, 55, 0.3);
  font-size: 0.9em;
}
.selection-summary strong {
  color: #d4af37;
}
</style>

<style src="vue-multiselect/dist/vue-multiselect.css"></style>
