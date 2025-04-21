<template>
  <div
    class="modal fade show d-block"
    tabindex="-1"
    style="background-color: rgba(0, 0, 0, 0.5)"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{
              isEditMode ? "Sửa thông tin người dùng" : "Thêm người dùng mới"
            }}
          </h5>
          <button type="button" class="btn-close" @click="closeModal"></button>
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

          <form @submit.prevent="saveUser">
            <!-- Username (Readonly for edit, required for add) -->
            <div class="mb-3">
              <label for="username" class="form-label">Tên đăng nhập</label>
              <input
                type="text"
                class="form-control"
                id="username"
                v-model="formData.username"
                :readonly="isEditMode"
                required
                :class="{ 'is-invalid': errors.username }"
              />
              <div class="invalid-feedback" v-if="errors.username">
                {{ errors.username }}
              </div>
            </div>

            <!-- Email -->
            <div class="mb-3">
              <label for="email" class="form-label">Email</label>
              <input
                type="email"
                class="form-control"
                id="email"
                v-model="formData.email"
                required
                :class="{ 'is-invalid': errors.email }"
              />
              <div class="invalid-feedback" v-if="errors.email">
                {{ errors.email }}
              </div>
            </div>

            <!-- Password (Only for Add mode) -->
            <div class="mb-3" v-if="!isEditMode">
              <label for="password" class="form-label">Mật khẩu</label>
              <input
                type="password"
                class="form-control"
                id="password"
                v-model="formData.password"
                required
                :class="{ 'is-invalid': errors.password }"
              />
              <div class="invalid-feedback" v-if="errors.password">
                {{ errors.password }}
              </div>
            </div>

            <!-- Full Name -->
            <div class="mb-3">
              <label for="fullName" class="form-label">Họ tên</label>
              <input
                type="text"
                class="form-control"
                id="fullName"
                v-model="formData.fullName"
                required
                :class="{ 'is-invalid': errors.fullName }"
              />
              <div class="invalid-feedback" v-if="errors.fullName">
                {{ errors.fullName }}
              </div>
            </div>

            <!-- Phone -->
            <div class="mb-3">
              <label for="phone" class="form-label">Số điện thoại</label>
              <input
                type="tel"
                class="form-control"
                id="phone"
                v-model="formData.phone"
                required
                :class="{ 'is-invalid': errors.phone }"
              />
              <div class="invalid-feedback" v-if="errors.phone">
                {{ errors.phone }}
              </div>
            </div>

            <!-- Address (Optional, only needed for edit?) -->
            <div class="mb-3" v-if="isEditMode">
              <label for="address" class="form-label">Địa chỉ</label>
              <input
                type="text"
                class="form-control"
                id="address"
                v-model="formData.address"
              />
              <!-- No validation shown for address -->
            </div>

            <!-- Role -->
            <div class="mb-3">
              <label for="role" class="form-label">Vai trò</label>
              <select
                class="form-select"
                id="role"
                v-model="formData.role"
                :class="{ 'is-invalid': errors.role }"
              >
                <option :value="false">User</option>
                <option :value="true">Admin</option>
              </select>
              <div class="invalid-feedback" v-if="errors.role">
                {{ errors.role }}
              </div>
            </div>

            <!-- Status -->
            <div class="mb-3">
              <label for="status" class="form-label">Trạng thái</label>
              <select
                class="form-select"
                id="status"
                v-model="formData.status"
                :class="{ 'is-invalid': errors.status }"
              >
                <option :value="true">Hoạt động</option>
                <option :value="false">Khóa</option>
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
            @click="saveUser"
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

const props = defineProps({
  user: {
    type: Object,
    default: null, // Null for add mode, user object for edit mode
  },
  isEditMode: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["close", "save"]);

const formData = reactive({
  id: null,
  username: "",
  email: "",
  password: "", // Only relevant for add mode
  fullName: "",
  phone: "",
  address: "", // Might not be needed for add?
  role: false,
  status: true,
});

const errors = reactive({
  username: "",
  email: "",
  password: "",
  fullName: "",
  phone: "",
  role: "",
  status: "",
});

const isSubmitting = ref(false);
const submitError = ref(null);

// Watch for prop changes to update form data when editing
watch(
  () => props.user,
  (newUser) => {
    if (newUser && props.isEditMode) {
      formData.id = newUser.id;
      formData.username = newUser.username || "";
      formData.email = newUser.email || "";
      formData.password = "********"; // Don't show real password
      formData.fullName = newUser.fullName || "";
      formData.phone = newUser.phone || "";
      formData.address = newUser.address || "";
      formData.role = newUser.role === true; // Ensure boolean
      formData.status = newUser.status === true; // Ensure boolean
    } else {
      // Reset for add mode
      formData.id = null;
      formData.username = "";
      formData.email = "";
      formData.password = "";
      formData.fullName = "";
      formData.phone = "";
      formData.address = ""; // Clear address for add mode
      formData.role = false;
      formData.status = true;
    }
    // Clear errors when user changes
    Object.keys(errors).forEach((key) => (errors[key] = ""));
    submitError.value = null;
  },
  { immediate: true }
); // immediate: true to run on initial load

const validateForm = () => {
  // Clear previous errors
  Object.keys(errors).forEach((key) => (errors[key] = ""));
  submitError.value = null;
  let isValid = true;

  if (!formData.username?.trim()) {
    errors.username = "Tên đăng nhập không được để trống.";
    isValid = false;
  }
  if (
    !formData.email?.trim() ||
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)
  ) {
    errors.email = "Email không hợp lệ.";
    isValid = false;
  }
  // Only validate password in add mode
  if (!props.isEditMode && !formData.password) {
    errors.password = "Mật khẩu không được để trống.";
    isValid = false;
  }
  if (!formData.fullName?.trim()) {
    errors.fullName = "Họ tên không được để trống.";
    isValid = false;
  }
  if (!formData.phone?.trim() || !/^0\d{9}$/.test(formData.phone)) {
    errors.phone = "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0).";
    isValid = false;
  }
  // Basic checks for role and status (should always have a value)
  if (typeof formData.role !== "boolean") {
    errors.role = "Vai trò không hợp lệ.";
    isValid = false;
  }
  if (typeof formData.status !== "boolean") {
    errors.status = "Trạng thái không hợp lệ.";
    isValid = false;
  }

  return isValid;
};

const saveUser = async () => {
  if (!validateForm()) {
    return;
  }

  isSubmitting.value = true;
  submitError.value = null;

  try {
    let response;
    // Prepare data, removing password for edit mode if it wasn't changed (or handling it appropriately)
    const payload = { ...formData };
    if (props.isEditMode) {
      // Don't send password if it wasn't intended to be changed
      // The backend expects the existing user object structure
      delete payload.password; // Or handle password change logic separately
    }

    if (props.isEditMode) {
      // API call for editing
      response = await apiClient.put(`/users/edit/${formData.id}`, payload);
    } else {
      // API call for creating
      response = await apiClient.post("/users/create", payload);
    }

    // Emit save event on success
    emit(
      "save",
      response.data.message ||
        (props.isEditMode ? "Cập nhật thành công" : "Thêm thành công")
    );
  } catch (error) {
    console.error("Error saving user:", error);
    submitError.value =
      error.response?.data?.error || "Lỗi khi lưu người dùng.";
  } finally {
    isSubmitting.value = false;
  }
};

const closeModal = () => {
  emit("close");
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
/* CSS cho thông báo */
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

.alert-warning {
  background-color: #ffc107;
  border-color: #ffc107;
  color: #1a1a1a;
}

.alert i {
  vertical-align: middle;
  margin-right: 8px;
}

.alert-dismissible .btn-close {
  padding: 12px;
  filter: invert(1);
}

@media (max-width: 768px) {
  .alert {
    max-width: 100%;
    font-size: 0.85rem;
  }
}
</style>
