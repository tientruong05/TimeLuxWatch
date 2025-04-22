<template>
  <div
    class="modal fade"
    :class="{ show: show, 'd-block': show }"
    tabindex="-1"
    role="dialog"
    style="background-color: rgba(0, 0, 0, 0.5)"
    @click.self="close"
  >
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ modalTitle }}</h5>
          <button
            type="button"
            class="btn-close"
            aria-label="Close"
            @click="close"
          ></button>
        </div>
        <div class="modal-body">
          <div v-if="isLoading" class="text-center my-3">
            <div class="spinner-border text-warning" role="status">
              <span class="visually-hidden">Đang tải...</span>
            </div>
          </div>
          <div v-if="error" class="alert alert-danger">{{ error }}</div>

          <form v-if="!isLoading && !error" @submit.prevent="submitForm">
            <!-- Form fields moved from CRUDProducts -->
            <div class="row">
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label">Tên sản phẩm</label>
                  <input
                    type="text"
                    v-model="formData.name"
                    class="form-control"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label class="form-label">Thương hiệu</label>
                  <select
                    v-model="formData.categoryId"
                    class="form-select"
                    required
                    @change="filterSubCategories"
                  >
                    <option value="" disabled selected>Chọn thương hiệu</option>
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
                  <label class="form-label">Giới tính</label>
                  <select
                    v-model="formData.subCategoryId"
                    class="form-select"
                    required
                    id="productSubCategory"
                    :disabled="
                      !formData.categoryId || filteredSubCategories.length === 0
                    "
                  >
                    <option value="" disabled selected>Chọn giới tính</option>
                    <option
                      v-for="subcat in filteredSubCategories"
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
                    v-model.number="formData.price"
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
                    v-model.number="formData.qty"
                    class="form-control"
                    min="0"
                    required
                  />
                </div>
              </div>
              <div class="col-md-6">
                <div class="mb-3">
                  <label class="form-label">Mô tả</label>
                  <textarea
                    v-model="formData.description"
                    class="form-control"
                    rows="3"
                  ></textarea>
                </div>
                <div class="mb-3">
                  <label class="form-label">Trạng thái</label>
                  <select v-model="formData.status" class="form-select">
                    <option value="1">Hoạt động</option>
                    <option value="0">Khóa</option>
                  </select>
                </div>

                <!-- Image Section -->
                <div v-if="isEditMode" class="mb-3">
                  <label class="form-label">Ảnh hiện tại</label>
                  <div
                    v-if="currentImages.length"
                    class="current-images-container"
                  >
                    <div
                      v-for="(img, index) in currentImages"
                      :key="'current-' + index"
                      class="current-image-item"
                      :class="{ 'is-main': index === 0 }"
                    >
                      <img
                        :src="img.url"
                        :alt="`Current Image ${index + 1}`"
                        class="product-image"
                        @error="setDefaultImage"
                      />
                      <div class="image-overlay">
                        <span v-if="index === 0" class="main-badge"
                          >Ảnh chính</span
                        >
                      </div>
                    </div>
                  </div>
                  <div v-else class="no-images-placeholder">
                    <i class="fas fa-image"></i>
                    <span>Không có ảnh</span>
                  </div>
                </div>

                <div class="mb-3">
                  <label class="form-label"
                    >{{ isEditMode ? "Thay đổi ảnh" : "Ảnh sản phẩm" }} (Chọn
                    nhiều ảnh)</label
                  >
                  <div class="image-upload-container">
                    <div class="image-upload-area">
                      <input
                        type="file"
                        ref="imageFileRef"
                        class="form-control visually-hidden"
                        accept="image/*"
                        multiple
                        @change="handleImageChange"
                        id="productImageInput"
                      />
                      <label for="productImageInput" class="image-upload-btn">
                        <i class="fas fa-cloud-upload-alt"></i>
                        <span>Tải ảnh lên</span>
                      </label>
                    </div>

                    <div v-if="imagePreviews.length" class="image-preview-list">
                      <div class="preview-header">
                        <small class="text-muted"
                          >Kéo thả để sắp xếp. Ảnh đầu tiên là ảnh chính.</small
                        >
                      </div>
                      <draggable
                        v-model="imagePreviews"
                        group="productImages"
                        @end="onDragEnd"
                        item-key="id"
                        class="preview-container"
                      >
                        <template #item="{ element, index }">
                          <div
                            class="image-preview-item"
                            :class="{ 'is-main': index === 0 }"
                          >
                            <img
                              :src="element.url"
                              :alt="`Preview ${index}`"
                              class="preview-image"
                            />
                            <div class="preview-overlay">
                              <span v-if="index === 0" class="main-badge"
                                >Ảnh chính</span
                              >
                              <button
                                type="button"
                                class="btn-remove"
                                @click="removePreviewImage(index)"
                              >
                                <i class="fas fa-times"></i>
                              </button>
                            </div>
                          </div>
                        </template>
                      </draggable>
                    </div>
                  </div>
                </div>
                <!-- End Image Section -->
              </div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <div v-if="submitError" class="alert alert-danger me-auto py-1 px-2">
            {{ submitError }}
          </div>
          <button type="button" class="btn btn-secondary" @click="close">
            Đóng
          </button>
          <button
            type="button"
            class="btn btn-primary"
            @click="submitForm"
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
import { ref, reactive, watch, computed, defineProps, defineEmits } from "vue";
import apiClient from "@/services/api";
import draggable from "vuedraggable"; // Import vuedraggable
import { Modal } from "bootstrap"; // Import Modal if needed for direct manipulation (optional)

// --- Props ---
const props = defineProps({
  show: {
    type: Boolean,
    required: true,
  },
  productToEdit: {
    type: Object,
    default: null, // Null for add mode
  },
  categories: {
    type: Array,
    required: true,
  },
  subcategories: {
    type: Array,
    required: true,
  },
});

// --- Emits ---
const emit = defineEmits(["close", "save-success"]);

// --- State ---
const formData = reactive({
  id: null,
  name: "",
  categoryId: "",
  subCategoryId: "",
  price: 1000000, // Default price
  qty: 0,
  description: "",
  status: "1",
  imageFiles: [], // Holds File objects for new uploads
  existingImage: "", // Holds the existing image string for edits
});

const imagePreviews = ref([]);
const imageFileRef = ref(null); // Ref for the file input

const filteredSubCategories = ref([]);
const isLoading = ref(false); // For loading state within modal if needed
const isSubmitting = ref(false);
const error = ref(null); // For fetch errors
const submitError = ref(null); // For form validation/submit errors

// --- Computed Properties ---
const isEditMode = computed(
  () => !!props.productToEdit && props.productToEdit.id > 0
);
const modalTitle = computed(() =>
  isEditMode.value ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới"
);

const currentImages = computed(() => {
  console.log(
    "[Modal Computed] currentImages START. Edit Mode:",
    isEditMode.value,
    "Existing Image String:",
    formData.existingImage
  );
  if (!isEditMode.value || !formData.existingImage) {
    console.log(
      "[Modal Computed] currentImages: Not in edit mode or no existing image string. Returning []."
    );
    return [];
  }
  const images = getProductImages(formData.existingImage);
  console.log(
    "[Modal Computed] currentImages: Processed existing images:",
    images
  );
  return images.map((imgData, index) => ({
    // Ensure it maps to the object structure expected
    id: `existing-${index}`,
    url: imgData.url, // Access the url property from getProductImages result
  }));
});

// --- Watchers ---
// Watch for the modal becoming visible or the productToEdit changing
watch(
  [() => props.show, () => props.productToEdit],
  ([newShow, newProduct]) => {
    if (newShow) {
      console.log("Modal shown or product changed, resetting form");
      resetFormAndPopulate(newProduct);
    }
  },
  { immediate: true } // Run immediately when component is created
);

// --- Methods ---
const resetFormAndPopulate = (product) => {
  // Reset reactive form data
  formData.id = null;
  formData.name = "";
  formData.categoryId = "";
  formData.subCategoryId = "";
  formData.price = 1000000;
  formData.qty = 0;
  formData.description = "";
  formData.status = "1";
  formData.imageFiles = [];
  formData.existingImage = "";

  // Reset refs
  imagePreviews.value = [];
  if (imageFileRef.value) {
    imageFileRef.value.value = null; // Clear file input
  }
  filteredSubCategories.value = [];
  isLoading.value = false; // Reset loading/error states
  isSubmitting.value = false;
  error.value = null;
  submitError.value = null;

  // Populate form if in edit mode
  if (product) {
    console.log("Populating form for edit:", product);
    formData.id = product.id;
    formData.name = product.name;
    formData.categoryId = product.subCategory?.category?.id || "";
    formData.subCategoryId = product.subCategory?.id || "";
    formData.price = product.price;
    formData.qty = product.qty;
    formData.description = product.description || "";
    formData.status = product.status?.toString() || "1";
    formData.existingImage = product.image || ""; // Store existing image string
    // Trigger subcategory filtering after category is set
    filterSubCategories();
  } else {
    // Ensure subcategories are filtered even in add mode if categories exist
    filterSubCategories();
  }
};

const filterSubCategories = () => {
  if (!formData.categoryId) {
    // If no category selected, show all relevant subcategories (or none if strict)
    // Depending on requirements, you might want to show all or clear the list.
    // Showing all might be confusing if they belong to different categories.
    // Let's filter based on the selected category, or show none if no category.
    filteredSubCategories.value = [];
    // If a subcategory was selected but doesn't match the new empty category, clear it
    if (formData.subCategoryId) {
      formData.subCategoryId = "";
    }
  } else {
    filteredSubCategories.value = props.subcategories.filter(
      (subcat) => subcat.category?.id == formData.categoryId
    );
    // If the currently selected subcategory is not in the new filtered list, reset it
    if (
      !filteredSubCategories.value.some((sc) => sc.id == formData.subCategoryId)
    ) {
      formData.subCategoryId = "";
    }
  }
  console.log("Filtered subcategories:", filteredSubCategories.value);
};

const validatePrice = (price) => {
  if (typeof price !== "number" || price < 1000000) {
    submitError.value = "Giá sản phẩm phải từ 1.000.000 VND trở lên.";
    return false;
  }
  return true;
};

const handleImageChange = (event) => {
  const newFiles = Array.from(event.target.files || []);
  console.log(
    "[Modal Method] handleImageChange: New files selected:",
    newFiles.map((f) => f.name)
  );

  if (newFiles.length > 0) {
    // Combine existing files with new files
    // Create a Set of names from existing files to avoid duplicates if user re-selects
    const existingFileNames = new Set(formData.imageFiles.map((f) => f.name));
    const uniqueNewFiles = newFiles.filter(
      (file) => !existingFileNames.has(file.name)
    );

    const combinedFiles = [...formData.imageFiles, ...uniqueNewFiles];
    console.log(
      "[Modal Method] handleImageChange: Combined files:",
      combinedFiles.map((f) => f.name)
    );

    formData.imageFiles = combinedFiles; // Update the File list

    // Regenerate previews from the combined list
    imagePreviews.value = formData.imageFiles.map((file, index) => ({
      id: `new-${index}-${Date.now()}`,
      url: URL.createObjectURL(file),
      file: file,
    }));
    console.log(
      "[Modal Method] handleImageChange: Updated previews:",
      imagePreviews.value.length
    );
  }
  // If no new files selected, we don't clear existing ones anymore
  // else {
  //   formData.imageFiles = [];
  //   imagePreviews.value = [];
  // }

  // Important: Clear the input value so the change event fires again
  // if the user selects the exact same file(s)
  if (event.target) {
    event.target.value = null;
  }
};

const removePreviewImage = (index) => {
  // Remove from File list
  const updatedFiles = [...formData.imageFiles];
  updatedFiles.splice(index, 1);
  formData.imageFiles = updatedFiles;

  // Remove from preview list
  const previewToRemove = imagePreviews.value[index];
  URL.revokeObjectURL(previewToRemove.url); // Clean up object URL
  imagePreviews.value.splice(index, 1);

  // Clear file input if all previews are removed
  if (imagePreviews.value.length === 0 && imageFileRef.value) {
    imageFileRef.value.value = null;
  }
};

const onDragEnd = () => {
  // Reorder the actual File objects in formData.imageFiles to match imagePreviews order
  const newFileOrder = imagePreviews.value.map((preview) => {
    // Find the original file object associated with this preview
    return preview.file;
  });
  formData.imageFiles = newFileOrder;
  console.log(
    "Reordered image files based on drag:",
    formData.imageFiles.map((f) => f.name)
  );
};

const submitForm = async () => {
  submitError.value = null; // Clear previous errors
  if (!validatePrice(formData.price)) return;
  // Add other validations if needed (e.g., name required)
  if (!formData.name?.trim()) {
    submitError.value = "Vui lòng nhập tên sản phẩm.";
    return;
  }
  if (!formData.categoryId) {
    submitError.value = "Vui lòng chọn thương hiệu.";
    return;
  }
  if (!formData.subCategoryId) {
    submitError.value = "Vui lòng chọn giới tính.";
    return;
  }

  isSubmitting.value = true;

  const payload = new FormData();
  payload.append("name", formData.name);
  payload.append("categoryId", formData.categoryId);
  payload.append("subCategoryId", formData.subCategoryId);
  payload.append("price", formData.price.toString());
  payload.append("qty", formData.qty.toString());
  payload.append("description", formData.description || "");
  payload.append("status", formData.status);

  if (isEditMode.value) {
    payload.append("id", formData.id.toString());
    payload.append("existingImage", formData.existingImage || ""); // Send existing image string
  }

  // Append multiple image files if they exist
  if (formData.imageFiles && formData.imageFiles.length > 0) {
    formData.imageFiles.forEach((file) => {
      payload.append("imageFiles", file); // Use "imageFiles" as key
    });
  }

  // Determine the correct endpoint and method
  const endpoint =
    isEditMode.value || (formData.imageFiles && formData.imageFiles.length > 0)
      ? "/crud/products/save-with-multiple-images" // Use multi-image endpoint for edit or if new images exist
      : "/crud/products/save"; // Use single-image endpoint only for NEW products with NO images

  console.log("Submitting form to endpoint:", endpoint);
  console.log("Payload (excluding files):");
  for (let pair of payload.entries()) {
    if (pair[0] !== "imageFiles") {
      console.log(pair[0] + ": " + pair[1]);
    }
  }
  console.log("Number of files in payload:", formData.imageFiles.length);

  try {
    // Use POST for both create and update with FormData
    const response = await apiClient.post(endpoint, payload, {
      headers: {
        // Content-Type is automatically set by browser for FormData
      },
    });
    emit("save-success", response.data.message || "Lưu thành công!");
    close(); // Close modal on success
  } catch (err) {
    console.error("Error saving product:", err);
    submitError.value =
      err.response?.data?.error ||
      err.response?.data?.message ||
      err.message ||
      "Lỗi không xác định khi lưu.";
  } finally {
    isSubmitting.value = false;
  }
};

const close = () => {
  resetFormAndPopulate(null); // Reset form fully on close
  emit("close");
};

// Helper for processing image string
const getProductImages = (imageString) => {
  console.log(
    "[Modal Helper] getProductImages processing string:",
    imageString
  );
  if (!imageString) return [];
  const baseUrl = "http://localhost:8080";
  const validImageExtensions = [
    /\.jpg$/i,
    /\.jpeg$/i,
    /\.png$/i,
    /\.gif$/i,
    /\.webp$/i,
  ];

  const results = imageString
    .split(";")
    .map((img) => img.trim())
    .filter((trimmed) => {
      const isValid =
        trimmed && validImageExtensions.some((regex) => regex.test(trimmed));
      if (!isValid && trimmed)
        console.warn(
          `[Modal Helper] getProductImages: Filtering invalid segment: '${trimmed}'`
        );
      return isValid;
    })
    .map((validTrimmed) => ({
      url: validTrimmed.startsWith("http")
        ? validTrimmed
        : `${baseUrl}/photos/${validTrimmed}`,
    }));
  console.log("[Modal Helper] getProductImages results:", results);
  return results;
};

// Helper for image errors
const setDefaultImage = (event) => {
  console.warn(
    "[Modal Helper] setDefaultImage triggered for:",
    event.target.src
  );
  event.target.src = "/placeholder.png"; // Use local placeholder
};
</script>

<style scoped>
/* Import Font Awesome if needed for icons */
@import url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css");

/* Modal styles copied/adapted from CRUDProducts */
.modal.show {
  display: block;
}
.modal-content {
  background: #222222;
  border: 1px solid #d4af37;
  color: #fff; /* Ensure text is visible */
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
  filter: invert(1) grayscale(100%) brightness(200%);
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

/* Image Upload/Preview Styles */
.image-upload-container {
  width: 100%;
  margin-bottom: 15px;
}
.image-upload-area {
  border: 2px dashed rgba(212, 175, 55, 0.5);
  border-radius: 5px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 15px;
}
.image-upload-area:hover {
  background: rgba(212, 175, 55, 0.1);
  border-color: rgba(212, 175, 55, 0.8);
}
.image-upload-btn {
  display: inline-block;
  color: #d4af37;
  font-size: 1rem;
  cursor: pointer;
  width: 100%;
  padding: 10px;
}
.image-upload-btn i {
  font-size: 1.5rem;
  margin-right: 10px;
  display: block;
  margin-bottom: 5px;
}
.image-preview-list {
  margin-top: 15px;
}
.preview-header {
  margin-bottom: 10px;
}
.preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.image-preview-item,
.current-image-item {
  width: 100px;
  height: 100px;
  position: relative;
  border-radius: 5px;
  overflow: hidden;
  border: 1px solid rgba(212, 175, 55, 0.3);
  transition: all 0.3s ease;
  cursor: grab;
}
.image-preview-item.is-main,
.current-image-item.is-main {
  border: 2px solid #d4af37;
  box-shadow: 0 0 10px rgba(212, 175, 55, 0.5);
}
.preview-image,
.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.preview-overlay,
.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}
.image-preview-item:hover .preview-overlay,
.current-image-item:hover .image-overlay {
  opacity: 1;
}
.main-badge {
  background: #d4af37;
  color: #111111;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: bold;
  position: absolute;
  top: 5px;
  left: 5px;
}
.btn-remove {
  position: absolute;
  top: 5px;
  right: 5px;
  background: rgba(220, 53, 69, 0.8);
  border: none;
  color: #fff;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s ease;
}
.btn-remove:hover {
  background: rgba(220, 53, 69, 1);
  transform: scale(1.1);
}
.no-images-placeholder {
  text-align: center;
  padding: 20px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 5px;
  color: rgba(255, 255, 255, 0.6);
}
.no-images-placeholder i {
  font-size: 2rem;
  margin-bottom: 10px;
  display: block;
}
.current-images-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}
@media (max-width: 576px) {
  .preview-container,
  .current-images-container {
    justify-content: center;
  }
  .image-preview-item,
  .current-image-item {
    width: 80px;
    height: 80px;
  }
}
</style>
