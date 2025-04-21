<template>
  <div
    class="modal fade show d-block"
    tabindex="-1"
    style="background-color: rgba(0, 0, 0, 0.5)"
    @click.self="closeModal"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ isEditMode ? "Chỉnh sửa Loại sản phẩm" : "Thêm Loại sản phẩm" }}
          </h5>
          <button type="button" class="btn-close" @click="closeModal"></button>
        </div>
        <div class="modal-body">
          <!-- Loading/Error for submit -->
          <div v-if="isSubmitting" class="text-center my-2">
            <div
              class="spinner-border spinner-border-sm text-warning"
              role="status"
            >
              <span class="visually-hidden">Đang lưu...</span>
            </div>
          </div>
          <div v-if="submitError" class="alert alert-danger py-2">
            {{ submitError }}
          </div>

          <form @submit.prevent="saveCategoryType">
            <!-- Brand Input -->
            <div class="mb-3">
              <label class="form-label">Thương Hiệu:</label>
              <input
                type="text"
                v-model.trim="formData.categoryName"
                class="form-control"
                placeholder="Nhập tên thương hiệu"
                required
                :class="{ 'is-invalid': errors.categoryName }"
              />
              <div class="invalid-feedback" v-if="errors.categoryName">
                {{ errors.categoryName }}
              </div>
            </div>
            <!-- Type Selection -->
            <div class="mb-3">
              <label class="form-label">Loại sản phẩm:</label>
              <select
                v-model="formData.subCategoriesName"
                class="form-select"
                required
                :class="{ 'is-invalid': errors.subCategoriesName }"
              >
                <option
                  v-for="option in subCategoryOptions"
                  :key="option.value"
                  :value="option.value"
                  :disabled="option.disabled"
                >
                  {{ option.text }}
                </option>
              </select>
              <div class="invalid-feedback" v-if="errors.subCategoriesName">
                {{ errors.subCategoriesName }}
              </div>
            </div>
            <!-- Status Selection -->
            <div class="mb-3">
              <label class="form-label">Trạng thái:</label>
              <select
                v-model.number="formData.status"
                class="form-select"
                :class="{ 'is-invalid': errors.status }"
              >
                <option :value="1">Hoạt động</option>
                <option :value="0">Không hoạt động</option>
              </select>
              <div class="invalid-feedback" v-if="errors.status">
                {{ errors.status }}
              </div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="closeModal">
            Đóng
          </button>
          <button
            type="button"
            class="btn btn-primary"
            @click="saveCategoryType"
            :disabled="isSubmitting"
          >
            {{ isSubmitting ? "Đang lưu..." : "Lưu" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, defineProps, defineEmits, computed } from "vue";
import apiClient from "@/services/api";

// --- Props ---
const props = defineProps({
  categoryType: {
    // Renamed from 'user' or similar to be specific
    type: Object,
    default: null, // Null for add mode
  },
  isEditMode: {
    type: Boolean,
    default: false,
  },
  categories: {
    // Pass the list of available brands (categories) - No longer directly used for brand input in modal
    type: Array,
    required: true, // Keep required if parent still needs to pass it for other reasons, or make false
    default: () => [],
  },
});

// --- Emits ---
const emit = defineEmits(["close", "save"]);

// --- Reactive State for Form ---
const formData = reactive({
  id: null,
  categoryName: "",
  subCategoriesName: "",
  status: 1,
});

const errors = reactive({
  categoryName: "",
  subCategoriesName: "",
  status: "",
});

const isSubmitting = ref(false);
const submitError = ref(null);

// --- Computed property to disable "Cả hai giới tính" in edit mode ---
const subCategoryOptions = computed(() => {
  const options = [
    { value: "", text: "Chọn loại sản phẩm", disabled: true },
    { value: "Đồng hồ nam", text: "Đồng hồ nam", disabled: false },
    { value: "Đồng hồ nữ", text: "Đồng hồ nữ", disabled: false },
  ];
  if (!props.isEditMode) {
    options.push({
      value: "Cả hai giới tính",
      text: "Cả hai giới tính",
      disabled: false,
    });
  }
  return options;
});

// --- Watcher to update form data when props change (for edit mode) ---
watch(
  () => props.categoryType,
  (newVal) => {
    if (newVal && props.isEditMode) {
      formData.id = newVal.id;
      formData.categoryName = newVal.category?.name || "";
      formData.subCategoriesName = newVal.subCategoriesName || "";
      formData.status = newVal.status ?? 1;
    } else {
      formData.id = null;
      formData.categoryName = props.categoryType?.categoryName || ""; // Keep potential prefill from parent
      formData.subCategoriesName = ""; // Start empty for add mode
      formData.status = 1;
    }
    Object.keys(errors).forEach((key) => (errors[key] = ""));
    submitError.value = null;
  },
  { immediate: true, deep: true }
);

// --- Methods ---
const validateForm = () => {
  Object.keys(errors).forEach((key) => (errors[key] = ""));
  submitError.value = null;
  let isValid = true;

  if (!formData.categoryName?.trim()) {
    errors.categoryName = "Vui lòng nhập tên thương hiệu.";
    isValid = false;
  }
  if (!formData.subCategoriesName?.trim()) {
    errors.subCategoriesName = "Vui lòng chọn loại sản phẩm.";
    isValid = false;
  }
  // Add validation if user selected the disabled option in edit mode somehow
  if (props.isEditMode && formData.subCategoriesName === "Cả hai giới tính") {
    errors.subCategoriesName =
      "Không thể chọn 'Cả hai giới tính' khi chỉnh sửa.";
    isValid = false;
  }
  if (
    typeof formData.status !== "number" ||
    (formData.status !== 0 && formData.status !== 1)
  ) {
    errors.status = "Trạng thái không hợp lệ.";
    isValid = false;
  }

  return isValid;
};

const saveCategoryType = async () => {
  if (!validateForm()) {
    return;
  }

  isSubmitting.value = true;
  submitError.value = null;

  // --- Simplified Save Logic (Always one call) ---
  try {
    const payload = {
      categoryName: formData.categoryName,
      // Send the actual selected value (e.g., "Cả hai giới tính")
      subCategoriesName: formData.subCategoriesName,
      status: formData.status,
    };

    let url = "/crud/categories/save";
    if (props.isEditMode && formData.id) {
      url += `?id=${formData.id}`;
    }

    // Backend now handles the "Cả hai giới tính" logic
    const response = await apiClient.post(url, payload);
    emit(
      "save",
      response.data.message || // Use message directly from backend
        (props.isEditMode ? "Cập nhật thành công!" : "Thêm thành công!")
    );
  } catch (error) {
    console.error("Error saving category type:", error);
    if (error.response?.status === 409) {
      // Duplicate error
      submitError.value =
        error.response.data.error ||
        "Loại sản phẩm này đã tồn tại cho thương hiệu này.";
    } else {
      // Other errors (including backend validation for empty fields)
      submitError.value =
        error.response?.data?.error || "Lỗi khi lưu loại sản phẩm.";
    }
  } finally {
    isSubmitting.value = false;
  }
  // --- End Simplified Save Logic ---
};

const closeModal = () => {
  emit("close");
};
</script>

<style scoped>
/* Copied styles from UserModal, adjust if needed */
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
  font-weight: 500;
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

.is-invalid {
  border-color: #dc3545;
}
.invalid-feedback {
  display: block; /* Make sure feedback is visible */
  color: #dc3545;
  font-size: 0.875em;
}

.alert {
  border-radius: 5px;
  padding: 0.75rem 1rem;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.form-select option:disabled {
  color: #6c757d; /* Style disabled option */
  background-color: #343a40; /* Darker background for disabled */
}

/* Ensure dropdown shows correctly */
.modal-body .form-select {
  /* Add styles if needed, but Bootstrap defaults usually work */
}
</style>
