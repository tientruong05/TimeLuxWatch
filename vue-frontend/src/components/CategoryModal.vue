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
            <!-- Brand Selection -->
            <div class="mb-3">
              <label class="form-label">Thương Hiệu:</label>
              <select
                v-model="formData.categoryName"
                class="form-select"
                required
                :class="{ 'is-invalid': errors.categoryName }"
              >
                <option value="" disabled>Chọn thương hiệu</option>
                <option
                  v-for="cat in categories"
                  :key="cat.id"
                  :value="cat.name"
                >
                  {{ cat.name }}
                </option>
              </select>
              <div class="invalid-feedback" v-if="errors.categoryName">
                {{ errors.categoryName }}
              </div>
            </div>
            <!-- Type Input -->
            <div class="mb-3">
              <label class="form-label"
                >Loại sản phẩm (ví dụ: Đồng hồ nam):</label
              >
              <input
                type="text"
                v-model.trim="formData.subCategoriesName"
                class="form-control"
                required
                :class="{ 'is-invalid': errors.subCategoriesName }"
              />
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
import { ref, reactive, watch, defineProps, defineEmits } from "vue";
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
    // Pass the list of available brands (categories)
    type: Array,
    required: true,
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
      // Reset for add mode (or if prop becomes null)
      formData.id = null;
      // Keep default or allow parent to set default categoryName for add mode
      formData.categoryName =
        props.categoryType?.categoryName ||
        (props.categories.length > 0 ? props.categories[0].name : "");
      formData.subCategoriesName = "";
      formData.status = 1;
    }
    // Clear errors when data changes
    Object.keys(errors).forEach((key) => (errors[key] = ""));
    submitError.value = null;
  },
  { immediate: true, deep: true } // Run immediately and watch nested changes
);

// --- Methods ---
const validateForm = () => {
  Object.keys(errors).forEach((key) => (errors[key] = ""));
  submitError.value = null;
  let isValid = true;

  if (!formData.categoryName?.trim()) {
    errors.categoryName = "Vui lòng chọn hoặc nhập tên thương hiệu.";
    isValid = false;
  }
  if (!formData.subCategoriesName?.trim()) {
    errors.subCategoriesName = "Vui lòng nhập tên loại sản phẩm.";
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

  try {
    const params = new URLSearchParams();
    if (props.isEditMode && formData.id) {
      params.append("id", formData.id.toString());
    }
    params.append("categoryName", formData.categoryName);
    params.append("subCategoriesName", formData.subCategoriesName);
    params.append("status", formData.status.toString());

    const response = await apiClient.post("/crud/categories/save", params);

    // Emit save event on success
    emit(
      "save",
      response.data.message ||
        (props.isEditMode ? "Cập nhật thành công!" : "Thêm thành công!")
    );
  } catch (error) {
    console.error("Error saving category type:", error);
    submitError.value =
      error.response?.data?.error || "Lỗi khi lưu loại sản phẩm.";
  } finally {
    isSubmitting.value = false;
  }
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
</style>
