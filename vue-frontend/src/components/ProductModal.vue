<template>
  <div
    class="modal fade show d-block"
    tabindex="-1"
    style="background-color: rgba(0, 0, 0, 0.5)"
    @click.self="closeModal"
  >
    <div
      class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable"
    >
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ isEditMode ? "Sửa thông tin sản phẩm" : "Thêm sản phẩm mới" }}
          </h5>
          <button
            type="button"
            class="btn-close"
            @click="closeModal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <!-- Loading/Error for submit -->
          <div v-if="isSubmitting" class="text-center my-2">
            <div
              class="spinner-border spinner-border-sm text-primary"
              role="status"
            >
              <span class="visually-hidden">Đang lưu...</span>
            </div>
          </div>
          <div v-if="submitError" class="alert alert-danger py-2">
            {{ submitError }}
          </div>

          <form @submit.prevent="saveProduct" novalidate>
            <div class="row">
              <!-- Left Column: Name, Categories, Price, Qty -->
              <div class="col-md-6">
                <!-- Name -->
                <div class="mb-3">
                  <label for="productName" class="form-label"
                    >Tên sản phẩm</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    id="productName"
                    v-model.trim="formData.name"
                    required
                    :class="{ 'is-invalid': errors.name }"
                  />
                  <div class="invalid-feedback" v-if="errors.name">
                    {{ errors.name }}
                  </div>
                </div>

                <!-- Category -->
                <div class="mb-3">
                  <label for="productCategory" class="form-label"
                    >Thương hiệu</label
                  >
                  <select
                    class="form-select"
                    id="productCategory"
                    v-model="formData.categoryId"
                    @change="filterSubCategoriesLocal"
                    required
                    :class="{ 'is-invalid': errors.categoryId }"
                  >
                    <option value="" disabled>Chọn thương hiệu</option>
                    <option
                      v-for="cat in categories"
                      :value="cat.id"
                      :key="cat.id"
                    >
                      {{ cat.name }}
                    </option>
                  </select>
                  <div class="invalid-feedback" v-if="errors.categoryId">
                    {{ errors.categoryId }}
                  </div>
                </div>

                <!-- SubCategory -->
                <div class="mb-3">
                  <label for="productSubCategory" class="form-label"
                    >Giới tính</label
                  >
                  <select
                    class="form-select"
                    id="productSubCategory"
                    v-model="formData.subCategoryId"
                    required
                    :disabled="!formData.categoryId"
                    :class="{ 'is-invalid': errors.subCategoryId }"
                  >
                    <option value="" disabled>Chọn giới tính</option>
                    <option
                      v-for="subcat in localFilteredSubcategories"
                      :value="subcat.id"
                      :key="subcat.id"
                    >
                      {{ subcat.subCategoriesName }}
                    </option>
                  </select>
                  <div class="invalid-feedback" v-if="errors.subCategoryId">
                    {{ errors.subCategoryId }}
                  </div>
                </div>

                <!-- Price -->
                <div class="mb-3">
                  <label for="productPrice" class="form-label">Giá (VNĐ)</label>
                  <input
                    type="text"
                    class="form-control"
                    id="productPrice"
                    v-model="formattedPrice"
                    @input="updateRawPrice($event.target.value)"
                    required
                    :class="{ 'is-invalid': errors.price }"
                  />
                  <div class="invalid-feedback" v-if="errors.price">
                    {{ errors.price }}
                  </div>
                </div>

                <!-- Quantity -->
                <div class="mb-3">
                  <label for="productQty" class="form-label">Số lượng</label>
                  <input
                    type="number"
                    class="form-control"
                    id="productQty"
                    v-model.number="formData.qty"
                    min="0"
                    required
                    :class="{ 'is-invalid': errors.qty }"
                  />
                  <div class="invalid-feedback" v-if="errors.qty">
                    {{ errors.qty }}
                  </div>
                </div>
              </div>

              <!-- Right Column: Description, Status, Image -->
              <div class="col-md-6">
                <!-- Description -->
                <div class="mb-3">
                  <label for="productDescription" class="form-label"
                    >Mô tả</label
                  >
                  <textarea
                    class="form-control"
                    id="productDescription"
                    rows="4"
                    v-model="formData.description"
                  ></textarea>
                </div>

                <!-- Status -->
                <div class="mb-3">
                  <label for="productStatus" class="form-label"
                    >Trạng thái</label
                  >
                  <select
                    class="form-select"
                    id="productStatus"
                    v-model="formData.status"
                    required
                    :class="{ 'is-invalid': errors.status }"
                  >
                    <option value="1">Hoạt động</option>
                    <option value="0">Khóa</option>
                  </select>
                  <div class="invalid-feedback" v-if="errors.status">
                    {{ errors.status }}
                  </div>
                </div>

                <!-- Image Upload -->
                <div class="mb-3">
                  <label for="productImageFile" class="form-label"
                    >Ảnh sản phẩm</label
                  >
                  <input
                    type="file"
                    class="form-control"
                    id="productImageFile"
                    ref="imageFileRef"
                    @change="handleImageChange"
                    accept="image/png, image/jpeg, image/gif, image/webp"
                    :class="{ 'is-invalid': errors.imageFile }"
                  />
                  <div class="invalid-feedback" v-if="errors.imageFile">
                    {{ errors.imageFile }}
                  </div>
                </div>

                <!-- Image Preview -->
                <div class="mb-3 image-preview-container">
                  <label class="form-label">Xem trước ảnh:</label>
                  <img
                    v-if="imagePreviewUrl || formData.existingImage"
                    :src="
                      imagePreviewUrl || getImageUrl(formData.existingImage)
                    "
                    alt="Xem trước ảnh sản phẩm"
                    class="img-thumbnail product-image-preview"
                    @error="setDefaultPreview"
                  />
                  <p v-else class="text-muted">Chưa chọn ảnh.</p>
                </div>
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
            @click="saveProduct"
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
import {
  ref,
  reactive,
  watch,
  computed,
  defineProps,
  defineEmits,
  onMounted,
} from "vue";
import apiClient from "@/services/api";
import {
  formatPrice as formatPriceUtil,
  parseFormattedPrice,
} from "@/utils/formatters";

// --- Props and Emits ---
const props = defineProps({
  product: {
    type: Object,
    default: null, // Null for add mode, product object for edit mode
  },
  isEditMode: {
    type: Boolean,
    default: false,
  },
  categories: {
    type: Array,
    required: true,
    default: () => [],
  },
  subcategories: {
    type: Array,
    required: true,
    default: () => [],
  },
});

const emit = defineEmits(["close", "save"]);

// --- Reactive State ---
const formData = reactive({
  id: null,
  name: "",
  categoryId: "",
  subCategoryId: "",
  price: 0, // Store the raw numeric price
  qty: 0,
  description: "",
  status: "1", // Default to active
  imageFile: null, // Store the File object
  existingImage: null, // Store the existing image filename for edit mode
});

const errors = reactive({
  name: "",
  categoryId: "",
  subCategoryId: "",
  price: "",
  qty: "",
  status: "",
  imageFile: "",
});

const isSubmitting = ref(false);
const submitError = ref(null);
const localFilteredSubcategories = ref([]);
const imageFileRef = ref(null); // Ref for the file input element
const imagePreviewUrl = ref(null); // URL for image preview

// --- Computed Properties ---

// Computed property for displaying formatted price
const formattedPrice = computed({
  get: () => formatPriceUtil(formData.price),
  set: (newValue) => {
    // Update the raw price when the formatted price changes
    formData.price = parseFormattedPrice(newValue);
  },
});

// --- Utility Functions ---
const getImageUrl = (imageName) => {
  return imageName
    ? `http://localhost:8080/photos/${imageName}`
    : "/placeholder.png";
};

// --- Watchers ---

// Watch for prop changes to update form data when editing
watch(
  () => props.product,
  (newProductData) => {
    resetForm(); // Reset form and errors first
    if (newProductData && props.isEditMode) {
      formData.id = newProductData.id;
      formData.name = newProductData.name || "";
      formData.categoryId = newProductData.subCategory?.category?.id || "";
      formData.subCategoryId = newProductData.subCategory?.id || "";
      formData.price = newProductData.price || 0;
      formData.qty = newProductData.qty || 0;
      formData.description = newProductData.description || "";
      formData.status = newProductData.status?.toString() || "1"; // Ensure string
      formData.existingImage = newProductData.image || null;
      imagePreviewUrl.value = null; // Clear preview when loading existing
      filterSubCategoriesLocal(); // Filter subcategories based on the initial category
    } else {
      // Set defaults for add mode (or if product is null)
      formData.id = null;
      formData.name = "";
      formData.categoryId = "";
      formData.subCategoryId = "";
      formData.price = 0;
      formData.qty = 0;
      formData.description = "";
      formData.status = "1";
      formData.existingImage = null;
      imagePreviewUrl.value = null;
      localFilteredSubcategories.value = []; // Clear subcategories initially
    }
  },
  { immediate: true, deep: true } // immediate: true to run on initial load
);

// --- Form Handling ---

const resetForm = () => {
  Object.keys(formData).forEach((key) => {
    if (key === "status") formData[key] = "1";
    else if (key === "price" || key === "qty") formData[key] = 0;
    else formData[key] = null;
  });
  formData.categoryId = "";
  formData.subCategoryId = "";
  imagePreviewUrl.value = null;
  if (imageFileRef.value) {
    imageFileRef.value.value = ""; // Reset file input visually
  }
  resetErrors();
};

const resetErrors = () => {
  Object.keys(errors).forEach((key) => (errors[key] = ""));
  submitError.value = null;
};

const filterSubCategoriesLocal = () => {
  if (!formData.categoryId) {
    localFilteredSubcategories.value = [];
  } else {
    localFilteredSubcategories.value = props.subcategories.filter(
      (subcat) => subcat.category?.id == formData.categoryId
    );
  }
  // Reset subcategory selection if it's no longer valid
  if (
    !localFilteredSubcategories.value.some(
      (sc) => sc.id == formData.subCategoryId
    )
  ) {
    formData.subCategoryId = "";
  }
};

// Update raw price from formatted input
const updateRawPrice = (formattedValue) => {
  formData.price = parseFormattedPrice(formattedValue);
};

const handleImageChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    // Basic validation (type, size)
    const allowedTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"];
    if (!allowedTypes.includes(file.type)) {
      errors.imageFile =
        "Loại file không hợp lệ. Chỉ chấp nhận JPG, PNG, GIF, WEBP.";
      imagePreviewUrl.value = null;
      formData.imageFile = null;
      event.target.value = ""; // Clear the input
      return;
    }
    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
      errors.imageFile = "Kích thước file quá lớn (Tối đa 5MB).";
      imagePreviewUrl.value = null;
      formData.imageFile = null;
      event.target.value = ""; // Clear the input
      return;
    }

    errors.imageFile = ""; // Clear error
    formData.imageFile = file;

    // Create preview URL
    const reader = new FileReader();
    reader.onload = (e) => {
      imagePreviewUrl.value = e.target.result;
    };
    reader.readAsDataURL(file);
  } else {
    formData.imageFile = null;
    imagePreviewUrl.value = null;
    errors.imageFile = ""; // Clear error if deselected
  }
};

const setDefaultPreview = (event) => {
  event.target.src = "/placeholder.png"; // Fallback if preview fails
};

const validateForm = () => {
  resetErrors();
  let isValid = true;

  if (!formData.name) {
    errors.name = "Tên sản phẩm không được để trống.";
    isValid = false;
  }
  if (!formData.categoryId) {
    errors.categoryId = "Vui lòng chọn loại hàng.";
    isValid = false;
  }
  if (!formData.subCategoryId) {
    errors.subCategoryId = "Vui lòng chọn hãng.";
    isValid = false;
  }
  if (formData.price < 0 || typeof formData.price !== "number") {
    errors.price = "Giá không hợp lệ.";
    isValid = false;
  }
  if (formData.qty < 0 || !Number.isInteger(formData.qty)) {
    errors.qty = "Số lượng không hợp lệ.";
    isValid = false;
  }
  if (formData.status !== "0" && formData.status !== "1") {
    errors.status = "Trạng thái không hợp lệ.";
    isValid = false;
  }
  // Image is optional for edit if existing image exists
  if (!props.isEditMode && !formData.imageFile) {
    // errors.imageFile = "Vui lòng chọn ảnh sản phẩm.";
    // isValid = false;
    // Make image optional for add mode as well, backend uses default
  }

  return isValid;
};

const saveProduct = async () => {
  if (!validateForm()) {
    return;
  }

  isSubmitting.value = true;
  submitError.value = null;

  const productData = new FormData();

  // Append common fields
  productData.append("name", formData.name);
  productData.append("categoryId", formData.categoryId); // Send category ID
  productData.append("subCategoryId", formData.subCategoryId); // Send subcategory ID
  productData.append("price", formData.price.toString()); // Send price as string
  productData.append("qty", formData.qty.toString());
  productData.append("description", formData.description || "");
  productData.append("status", formData.status);

  // Append ID and image details based on mode
  if (props.isEditMode) {
    productData.append("id", formData.id.toString());
    if (formData.imageFile) {
      productData.append("imageFile", formData.imageFile);
      productData.append("existingImage", formData.existingImage || ""); // Send existing even if new is uploaded, backend handles replacement
    } else {
      // If no new file, send the existing image name
      productData.append("existingImage", formData.existingImage || "");
      // Do not append imageFile if not changed
    }
  } else {
    // Add mode - append image file if selected
    if (formData.imageFile) {
      productData.append("imageFile", formData.imageFile);
    }
    // No ID or existingImage needed for add mode
  }

  try {
    // Use the single save endpoint
    const response = await apiClient.post("/crud/products/save", productData, {
      headers: {
        // Axios automatically sets Content-Type for FormData
      },
    });

    // Emit save event on success
    emit(
      "save",
      response.data.message ||
        (props.isEditMode ? "Cập nhật thành công!" : "Thêm thành công!")
    );
    closeModal(); // Close modal after successful save
  } catch (error) {
    console.error("Error saving product:", error);
    submitError.value =
      error.response?.data?.error || "Lỗi khi lưu sản phẩm. Vui lòng thử lại.";
  } finally {
    isSubmitting.value = false;
  }
};

const closeModal = () => {
  emit("close");
  resetForm(); // Reset form state when closing
};

// --- Lifecycle Hooks (Optional) ---
onMounted(() => {
  // Initial setup if needed, though watcher handles most
  filterSubCategoriesLocal();
});
</script>

<style scoped>
/* Scoped styles from UserModal, adjusted for products */
.modal-content {
  background: #ffffff; /* Light theme */
  border: 1px solid #dee2e6;
  color: #212529;
}

.modal-header {
  border-bottom: 1px solid #dee2e6;
  background-color: #f8f9fa;
}

.modal-footer {
  border-top: 1px solid #dee2e6;
  background-color: #f8f9fa;
}

.modal-title {
  color: #212529;
}

.btn-close {
  /* Default Bootstrap styling */
}

.form-label {
  color: #495057;
  font-weight: 500;
  font-size: 0.9rem;
}

.form-control,
.form-select {
  background: #fff;
  border: 1px solid #ced4da;
  color: #212529;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.form-control:focus,
.form-select:focus {
  background: #fff;
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
  color: #212529;
}

.form-select option {
  background: #fff;
  color: #212529;
}

.modal .btn-secondary {
  background: #6c757d;
  color: #fff;
  border: 1px solid #6c757d;
}

.modal .btn-primary {
  background: #0d6efd;
  color: #fff;
  border: 1px solid #0d6efd;
}

.modal .btn-primary:hover,
.modal .btn-secondary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.alert {
  border-radius: 5px;
  padding: 0.75rem 1rem;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.product-image-preview {
  max-width: 100%;
  max-height: 150px;
  margin-top: 10px;
  border: 1px solid #dee2e6;
}

.image-preview-container {
  min-height: 50px; /* Ensure space even if no image */
}

.is-invalid {
  border-color: #dc3545;
}
.invalid-feedback {
  display: block; /* Make sure feedback is visible */
  color: #dc3545;
  font-size: 0.875em;
}
</style>
