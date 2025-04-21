import { defineStore } from "pinia";
import { ref } from "vue";

export const useNotificationStore = defineStore("notification", () => {
  // State: Array to hold active notifications
  const notifications = ref([]);

  let nextId = 0;

  // Action: Add a new notification
  const addNotification = (message, type = "success", duration = 3000) => {
    const id = nextId++;
    notifications.value.push({
      id,
      message,
      type,
      duration,
    });

    // Automatically remove the notification after its duration
    // Note: The ToastDisplay component will handle the visual hiding.
    // This is mainly for cleaning up the store state.
    setTimeout(() => {
      removeNotification(id);
    }, duration + 500); // Add a small buffer after the toast hides
  };

  // Action: Remove a notification by its ID
  const removeNotification = (id) => {
    notifications.value = notifications.value.filter((n) => n.id !== id);
  };

  return {
    notifications,
    addNotification,
    removeNotification,
  };
});
