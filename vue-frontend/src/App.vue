<script setup>
// Keep the script setup if you need it for App-level logic in the future
import { computed, watch, ref, onMounted, nextTick } from "vue"; // Import necessary functions
import { useRoute } from "vue-router"; // Import useRoute
import HeaDer from "@/components/HeaDer.vue"; // Import Header
import FooTer from "@/components/FooTer.vue"; // Import Footer
import { useNotificationStore } from "@/stores/notificationStore"; // Import the notification store
import { useAuthStore } from "@/stores/auth"; // <-- Import Auth Store
import { Toast } from "bootstrap"; // Import Bootstrap Toast JS

// Get current route information
const route = useRoute();

// Notification store instance
const notificationStore = useNotificationStore();

// Auth store instance <-- Add this
const authStore = useAuthStore();

// Ref for the toast container element
const toastContainerRef = ref(null);

// Override the native alert function to use our notification system
onMounted(() => {
  const originalAlert = window.alert;
  window.alert = function (message) {
    console.log("Alert intercepted:", message);
    // Use our notification store
    notificationStore.addNotification(message, "success");
  };
  console.log("Alert function overridden to use notifications");
});

// Computed property to determine if Header/Footer should be shown
const showLayout = computed(() => {
  // List of route names where the layout should be hidden
  const hiddenLayoutRoutes = ["LoginRegister", "ForgotPassword"];
  // Hide if the current route name is in the list
  return !hiddenLayoutRoutes.includes(route.name);
});

// Computed property for Header key <-- Add this
const headerKey = computed(() => authStore.cartCount);

// Keep track of active Bootstrap Toast instances
const activeToasts = new Map();

// Watch for changes in the notifications array
watch(
  notificationStore.notifications,
  (newNotifications, oldNotifications) => {
    console.log(
      "App.vue: Notification store watcher triggered!",
      newNotifications
    );
    nextTick(() => {
      console.log(
        "App.vue: Inside nextTick. toastContainerRef exists?",
        !!toastContainerRef.value
      );
      if (!toastContainerRef.value) {
        console.error("App.vue: toastContainerRef is NULL inside nextTick!");
        return;
      }

      // Create toasts for new notifications
      newNotifications.forEach((notification) => {
        console.log(
          `App.vue: Processing notification ID: ${notification.id}, Already active?`,
          activeToasts.has(notification.id)
        );
        // Check if this notification's toast is already active
        if (!activeToasts.has(notification.id)) {
          console.log(
            `App.vue: Creating toast element for notification ID: ${notification.id}`
          );
          const toastId = `toast-${notification.id}`;
          const toastElement = document.createElement("div");
          toastElement.id = toastId;
          toastElement.className = `toast align-items-center text-white bg-${notification.type} border-0`;
          toastElement.setAttribute("role", "alert");
          toastElement.setAttribute("aria-live", "assertive");
          toastElement.setAttribute("aria-atomic", "true");

          toastElement.innerHTML = `
            <div class="d-flex">
              <div class="toast-body">${notification.message}</div>
              <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
          `;

          toastContainerRef.value.appendChild(toastElement);

          const bsToast = new Toast(toastElement, {
            delay: notification.duration,
          });
          bsToast.show();

          // Add the instance to our map
          activeToasts.set(notification.id, {
            element: toastElement,
            instance: bsToast,
          });

          // Listen for the hidden event to clean up
          toastElement.addEventListener("hidden.bs.toast", () => {
            // Remove from DOM
            toastElement.remove();
            // Remove from store (already done by timeout, but good practice)
            notificationStore.removeNotification(notification.id);
            // Remove from active toasts map
            activeToasts.delete(notification.id);
            console.log(`Toast ${notification.id} hidden and removed.`);
          });
        }
      });

      // Cleanup toasts that are no longer in the store (should be rare with timeout)
      const currentNotificationIds = new Set(newNotifications.map((n) => n.id));
      activeToasts.forEach((toastData, id) => {
        if (!currentNotificationIds.has(id)) {
          toastData.instance.hide(); // Hide might trigger the 'hidden.bs.toast' event
          // Or forcefully remove if needed
          // toastData.element.remove();
          // activeToasts.delete(id);
          console.log(`Cleaning up orphaned toast ${id}`);
        }
      });
    });
  },
  { deep: true } // Watch deeply for changes within the array
);
</script>

<template>
  <!-- Global Toast Container -->
  <div
    class="toast-container position-fixed top-0 end-0 p-3"
    style="z-index: 1100"
    ref="toastContainerRef"
  ></div>

  <HeaDer v-if="showLayout" :key="headerKey" />
  <!-- Add Header component, conditionally rendered -->
  <main>
    <router-view />
    <!-- Router view renders the current route component -->
  </main>
  <FooTer v-if="showLayout" />
  <!-- Add Footer component, conditionally rendered -->
</template>

<style>
/* Import global styles */
@import "./style.css";

/* Optional: Add some global styles if needed */
.toast-container {
  /* Adjust z-index if necessary to be above all other elements */
  z-index: 1100 !important;
}

/* Additional global styling can be added here */
</style>
