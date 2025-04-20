<template>
  <div class="product-card h-100 position-relative">
    <!-- Discount Badge -->
    <div class="discount-badge" v-if="product.discountPercentage > 0">
      <span>{{ Math.round(product.discountPercentage) }}%</span>
      <span>OFF</span>
    </div>

    <!-- Image -->
    <div class="text-center product-image-container">
      <img
        :src="getImageUrl(product.image)"
        class="product-image"
        :alt="product.name"
        @error="setDefaultImage"
      />
    </div>

    <!-- Body -->
    <div class="card-body text-center d-flex flex-column">
      <!-- Product Name -->
      <h3 class="product-title flex-grow-1">
        <router-link
          :to="{ name: 'ProductDetail', params: { id: product.id } }"
          class="product-link"
        >
          {{ product.name }}
        </router-link>
      </h3>

      <!-- Prices -->
      <div class="price-container">
        <p class="price mb-0">{{ formattedPrice(product.discountedPrice) }}</p>
        <p class="price-original" v-if="product.discountPercentage > 0">
          {{ formattedPrice(product.price) }}
        </p>
      </div>

      <!-- Add to Cart Button -->
      <button
        class="btn btn-shop mt-3 add-to-cart"
        @click="addToCartHandler(product.id)"
      >
        Thêm vào giỏ
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { formatPrice } from "@/utils/formatters";
import { useRouter, useRoute } from "vue-router"; // Import useRoute
import { useAuthStore } from "@/stores/auth"; // Import Pinia store
import apiClient from "@/services/api"; // Import apiClient

const props = defineProps({
  product: {
    type: Object,
    required: true,
    default: () => ({}),
  },
});

// Use a computed property for cleaner price formatting access in template
const formattedPrice = formatPrice;

// Function to construct image URL (adjust path as needed)
const getImageUrl = (imageName) => {
  if (!imageName) {
    return "/placeholder.png"; // Provide a default placeholder image path
  }
  // Use full URL including the server domain
  return `http://localhost:8080/photos/${imageName}`;
};

// Handle image loading errors
const setDefaultImage = (event) => {
  event.target.src = "http://localhost:8080/photos/placeholder.png"; // Set to placeholder if image fails
};

// Or use a store action
const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const addToCartHandler = async (productId) => {
  console.log(`Add to cart clicked for product ID: ${productId}`);

  if (!authStore.isAuthenticated) {
    console.log("User not authenticated, redirecting to login...");
    localStorage.setItem("redirectAfterLogin", route.fullPath); // Save current path
    router.push({ name: "LoginRegister", query: { promptLogin: true } });
    return;
  }

  try {
    const formData = new URLSearchParams();
    formData.append("quantity", 1); // Add 1 item from product card

    const response = await apiClient.post(`/cart/add/${productId}`, formData, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });

    if (response.data && response.data.message === "success") {
      authStore.updateCartCount(response.data.cartCount); // Update cart count in store
      alert("Đã thêm sản phẩm vào giỏ hàng!"); // Simple confirmation
      // TODO: Add a more sophisticated notification (e.g., toast)
    } else {
      throw new Error(response.data.error || "Không thể thêm vào giỏ hàng");
    }
  } catch (error) {
    console.error("Error adding to cart:", error);
    alert(`Lỗi: ${error.message || "Không thể thêm vào giỏ hàng"}`);
    // TODO: Add a more sophisticated notification (e.g., toast)
  }
};

// Optional: If using router-link programmatically
// const router = useRouter();
// const goToProductDetail = (productId) => {
//     router.push({ name: 'ProductDetail', params: { id: productId } });
// };
</script>

<style scoped>
.product-card {
  border: 1px solid #eee;
  border-radius: 8px; /* Slightly rounded corners */
  padding: 15px;
  transition: all 0.3s ease;
  background: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  display: flex; /* Ensure flex works correctly with h-100 */
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
}

.product-image-container {
  height: 200px; /* Fixed height for image container */
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  overflow: hidden; /* Prevent image overflow */
}

.product-image {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain; /* Scale image while preserving aspect ratio */
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-title {
  font-size: 1.1rem; /* Adjusted font size */
  color: #333;
  margin-bottom: 10px; /* Space below title */
  font-weight: 500;
  min-height: 44px; /* Ensure minimum height for 2 lines */
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-link {
  text-decoration: none;
  color: inherit; /* Inherit color from title */
}

.product-link:hover {
  color: #e74c3c; /* Accent color on hover */
}

.price-container {
  margin-top: auto; /* Push price to bottom before button */
  padding-top: 10px; /* Space above price */
  min-height: 50px; /* Ensure space even if no original price */
}

.price {
  color: #e74c3c; /* Accent color for discounted price */
  font-size: 1.25rem;
  font-weight: 600;
}

.price-original {
  text-decoration: line-through;
  color: #888; /* Lighter color for original price */
  font-size: 0.9rem;
  margin-left: 5px; /* Space from discounted price */
}

.discount-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #e74c3c;
  color: white;
  padding: 6px 8px;
  font-weight: bold;
  z-index: 2;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  font-size: 0.8rem;
}

.discount-badge span:first-child {
  font-size: 1rem;
  font-weight: 700;
  line-height: 1;
}

.discount-badge span:last-child {
  font-size: 0.7rem;
  font-weight: 600;
  margin-top: 2px;
}

.btn-shop {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 25px;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 0.85rem;
  font-weight: 500;
  box-shadow: 0 4px 10px rgba(231, 76, 60, 0.2);
  width: 100%; /* Make button full width */
}

.btn-shop:hover {
  background-color: #333; /* Darker background on hover */
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}
</style>
