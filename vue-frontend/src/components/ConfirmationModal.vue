<template>
  <div v-if="show" class="modal-backdrop fade show"></div>
  <div
    class="modal fade"
    :class="{ show: show }"
    :style="{ display: show ? 'block' : 'none' }"
    tabindex="-1"
    @click.self="handleBackdropClick"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ title }}</h5>
          <button type="button" class="btn-close" @click="cancel"></button>
        </div>
        <div class="modal-body">
          <p>{{ message }}</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="cancel">
            {{ cancelText }}
          </button>
          <button type="button" class="btn btn-danger" @click="confirm">
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  show: {
    type: Boolean,
    required: true,
  },
  title: {
    type: String,
    default: "Xác nhận",
  },
  message: {
    type: String,
    required: true,
  },
  confirmText: {
    type: String,
    default: "Xác nhận",
  },
  cancelText: {
    type: String,
    default: "Hủy",
  },
  allowBackdropClick: {
    // Prop to control closing on backdrop click
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(["confirm", "cancel"]);

const confirm = () => {
  emit("confirm");
};

const cancel = () => {
  emit("cancel");
};

const handleBackdropClick = () => {
  if (props.allowBackdropClick) {
    cancel();
  }
};
</script>

<style scoped>
.modal-backdrop {
  background-color: rgba(0, 0, 0, 0.6); /* Darker backdrop */
}

.modal-content {
  background-color: #2d2d2d; /* Dark background */
  color: #e0e0e0; /* Light text */
  border: 1px solid #d4af37; /* Gold border */
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
}

.modal-header {
  border-bottom: 1px solid #444; /* Darker border */
  padding: 1rem 1.5rem;
}

.modal-title {
  color: #d4af37; /* Gold title */
  font-weight: 500;
}

.btn-close {
  filter: invert(1) grayscale(100%) brightness(200%); /* Make close button white */
  opacity: 0.8;
}
.btn-close:hover {
  opacity: 1;
}

.modal-body {
  padding: 1.5rem;
  line-height: 1.6;
}

.modal-footer {
  border-top: 1px solid #444; /* Darker border */
  padding: 1rem 1.5rem;
  justify-content: flex-end; /* Align buttons to the right */
}

.modal-footer .btn {
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.modal-footer .btn-secondary {
  background-color: #5a5a5a;
  border-color: #5a5a5a;
  color: #fff;
}
.modal-footer .btn-secondary:hover {
  background-color: #6c757d;
  border-color: #6c757d;
}

.modal-footer .btn-danger {
  background-color: #c82333; /* Slightly different red */
  border-color: #c82333;
  color: #fff;
}
.modal-footer .btn-danger:hover {
  background-color: #dc3545;
  border-color: #dc3545;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.4);
}

/* Ensure modal is displayed correctly */
.modal.show {
  display: block;
}
</style>
