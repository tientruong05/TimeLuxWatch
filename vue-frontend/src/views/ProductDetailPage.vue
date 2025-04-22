<template>
  <div class="product-detail-page section" v-if="!loading && productData">
    <div class="container">
      <div class="row">
        <!-- Product Image Gallery -->
        <div class="col-md-6">
          <div class="product-gallery-container">
            <!-- Main Image -->
            <div class="main-image-wrapper mb-3">
              <div
                v-if="product && product.discountPercentage > 0"
                class="discount-badge"
              >
                <span>{{ Math.round(product.discountPercentage) }}%</span>
                <span>OFF</span>
              </div>
              <img
                :src="currentMainImage"
                :alt="product ? product.name : 'Product Image'"
                class="main-product-image img-fluid"
                @error="setDefaultImage"
              />
            </div>

            <!-- Swiper Thumbnail Slider -->
            <swiper
              v-if="productImages.length > 1"
              :modules="swiperModules"
              :slides-per-view="4"
              :space-between="10"
              :navigation="true"
              :pagination="{ clickable: true }"
              class="thumbnail-swiper"
            >
              <swiper-slide
                v-for="(image, index) in productImages"
                :key="`thumb-${index}`"
                class="thumbnail-slide"
                :class="{ active: currentImageIndex === index }"
                @click="setMainImage(index)"
              >
                <img
                  :src="image"
                  :alt="`${product ? product.name : 'Thumbnail'} ${index + 1}`"
                  class="img-fluid"
                />
              </swiper-slide>
            </swiper>
          </div>
        </div>

        <!-- Product Info -->
        <div class="col-md-6">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-code">Mã SP: {{ product.id }}</p>

          <!-- Pricing -->
          <p class="product-price">
            <span v-if="product.discountPercentage > 0">
              <span class="discount-price">{{
                formatPrice(product.discountedPrice)
              }}</span>
              <span class="original-price">{{
                formatPrice(product.price)
              }}</span>
            </span>
            <span v-else>
              {{ formatPrice(product.price) }}
            </span>
          </p>

          <!-- Details -->
          <div class="product-info">
            <p>
              <strong>Số lượng còn:</strong> <span>{{ product.qty }}</span>
            </p>
            <p>
              <strong>Mô tả:</strong> <span>{{ product.description }}</span>
            </p>
            <p>
              <strong>Trạng thái:</strong>
              <span
                :class="
                  product.qty > 0 ? 'stock-status-in' : 'stock-status-out'
                "
              >
                {{ product.qty > 0 ? "Còn hàng" : "Hết hàng" }}
              </span>
            </p>
          </div>

          <!-- Quantity and Add to Cart -->
          <div class="d-flex align-items-start gap-3 mt-4">
            <div
              class="input-group quantity-selector mb-3"
              style="max-width: 150px"
            >
              <button
                class="btn btn-outline-secondary"
                type="button"
                @click="decrementQuantity"
                :disabled="product.qty <= 0 || quantity <= 1"
              >
                -
              </button>
              <input
                type="number"
                class="form-control text-center"
                v-model.number="quantity"
                min="1"
                :max="product.qty"
                :disabled="product.qty <= 0"
                @change="validateQuantity"
              />
              <button
                class="btn btn-outline-secondary"
                type="button"
                @click="incrementQuantity"
                :disabled="product.qty <= 0 || quantity >= product.qty"
              >
                +
              </button>
            </div>
            <div class="d-flex flex-column flex-grow-1">
              <div class="d-flex gap-2">
                <button
                  class="btn btn-buy flex-grow-1"
                  @click="buyNow"
                  :disabled="product.qty <= 0"
                >
                  Mua ngay
                </button>
                <button
                  class="btn btn-cart flex-grow-1"
                  @click="addToCartHandler"
                  :disabled="product.qty <= 0"
                >
                  Thêm vào giỏ
                </button>
              </div>
              <div class="quantity-warning mt-2" v-if="showQuantityWarning">
                Số lượng không hợp lệ hoặc vượt quá số lượng tồn kho!
              </div>
            </div>
          </div>

          <!-- Delivery & Social -->
          <div class="delivery-info">
            <p class="mb-2"><strong>Giao hàng:</strong> Freeship toàn quốc</p>
            <p class="mb-0"><strong>Hotline:</strong> 093 189 2222</p>
          </div>
          <div class="social-share">
            <span class="text-muted me-2">Chia sẻ:</span>
            <a href="#" class="text-decoration-none me-2"
              ><i class="bi bi-facebook"></i
            ></a>
            <a href="#" class="text-decoration-none me-2"
              ><i class="bi bi-twitter"></i
            ></a>
            <a href="#" class="text-decoration-none"
              ><i class="bi bi-pinterest"></i
            ></a>
          </div>
        </div>
      </div>

      <!-- Related Products -->
      <div
        class="related-products mt-5 pt-4 border-top"
        v-if="relatedProducts && relatedProducts.length"
      >
        <h4 class="text-center mb-4">Sản phẩm liên quan</h4>
        <div class="row g-3">
          <div
            class="col-md-2 col-6"
            v-for="related in relatedProducts"
            :key="related.id"
          >
            <!-- Use ProductCard component for consistency -->
            <ProductCard :product="related" />
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Loading State -->
  <div v-else-if="loading" class="text-center my-5 py-5">
    <div
      class="spinner-border text-primary"
      role="status"
      style="width: 3rem; height: 3rem"
    >
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>

  <!-- Error State -->
  <div v-else-if="error" class="container my-5">
    <div class="alert alert-danger">
      <p><strong>Lỗi:</strong> {{ error }}</p>
      <p>Không thể tải thông tin chi tiết sản phẩm. Vui lòng thử lại sau.</p>
    </div>
    <router-link to="/" class="btn btn-secondary"
      >Quay lại Trang chủ</router-link
    >
  </div>

  <!-- Not Found State -->
  <div v-else class="container my-5 text-center">
    <h2>Sản phẩm không tồn tại</h2>
    <p>Không tìm thấy sản phẩm bạn yêu cầu.</p>
    <router-link to="/" class="btn btn-primary">Về Trang chủ</router-link>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import apiClient from "@/services/api";
import { formatPrice } from "@/utils/formatters";
import ProductCard from "@/components/ProductCard.vue";
import { useAuthStore } from "@/stores/auth"; // Import Pinia store

// Swiper Imports
import { Swiper, SwiperSlide } from "swiper/vue";
import { Navigation, Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore(); // Initialize auth store

const productId = ref(route.params.id);
const productData = ref(null); // Will hold the entire { product: {}, relatedProducts: [] }
const quantity = ref(1);
const showQuantityWarning = ref(false);
const loading = ref(true); // Start in loading state
const error = ref(null);
const currentImageIndex = ref(0); // For tracking the selected image

// Computed property to easily access the main product detail
const product = computed(() => productData.value?.product);
// Computed property for related products
const relatedProducts = computed(
  () => productData.value?.relatedProducts || []
);

// --- NEW: Computed property for processing images ---
const productImages = computed(() => {
  console.log("--- ProductDetailPage: [Computed] productImages START ---");
  const imageString = product.value?.image;

  if (!imageString) {
    console.log(
      "ProductDetailPage: [Computed] productImages: No image string found. Returning placeholder."
    );
    return ["http://localhost:8080/photos/placeholder.png"];
  }
  console.log(
    `ProductDetailPage: [Computed] productImages: Input image string: '${imageString}'`
  );

  let processedImages = [];
  const validImageExtensions = [
    /\.jpg$/i,
    /\.jpeg$/i,
    /\.png$/i,
    /\.gif$/i,
    /\.webp$/i,
  ];

  try {
    if (imageString.includes(";")) {
      console.log(
        "ProductDetailPage: [Computed] productImages: Detected MULTIPLE images."
      );
      processedImages = imageString
        .split(";")
        .map((img) => img.trim())
        .filter((trimmed) => {
          const isValid =
            trimmed &&
            validImageExtensions.some((regex) => regex.test(trimmed));
          if (!isValid)
            console.warn(
              `ProductDetailPage: Filtering out invalid segment: '${trimmed}'`
            );
          return isValid;
        })
        .map((validTrimmed) => `http://localhost:8080/photos/${validTrimmed}`);
    } else {
      console.log(
        "ProductDetailPage: [Computed] productImages: Detected SINGLE image."
      );
      const trimmed = imageString.trim();
      const isValidSingle =
        trimmed && validImageExtensions.some((regex) => regex.test(trimmed));
      if (isValidSingle) {
        processedImages = [`http://localhost:8080/photos/${trimmed}`];
      } else {
        console.warn(
          `ProductDetailPage: Single image '${trimmed}' invalid. Filtering out.`
        );
      }
    }
  } catch (e) {
    console.error("ProductDetailPage: Error processing image string:", e);
    processedImages = [];
  }

  const finalResult =
    processedImages.length > 0
      ? processedImages
      : ["http://localhost:8080/photos/placeholder.png"];
  console.log(
    "ProductDetailPage: [Computed] productImages: Final result:",
    finalResult
  );
  console.log("--- ProductDetailPage: [Computed] productImages END ---");
  return finalResult;
});

// --- NEW: Computed property for the main image URL ---
const currentMainImage = computed(() => {
  if (productImages.value && productImages.value.length > 0) {
    return (
      productImages.value[currentImageIndex.value] ||
      "http://localhost:8080/photos/placeholder.png"
    );
  }
  return "http://localhost:8080/photos/placeholder.png";
});

// --- NEW: Swiper setup ---
const swiperModules = [Navigation, Pagination];

const fetchProductDetails = async (id) => {
  if (!id) return;
  loading.value = true;
  error.value = null;
  productData.value = null; // Reset data
  quantity.value = 1; // Reset quantity
  showQuantityWarning.value = false;
  currentImageIndex.value = 0; // Reset image index on new product fetch
  console.log(`Fetching details for product ID: ${id}`);
  try {
    const response = await apiClient.get(`/products/detail/${id}`);
    productData.value = response.data;
    console.log("Fetched product details:", productData.value);
    if (product.value?.qty === 0) {
      quantity.value = 0;
    }
  } catch (err) {
    console.error(`Error fetching product details for ID ${id}:`, err);
    if (err.response && err.response.status === 404) {
      error.value = "Sản phẩm không được tìm thấy.";
    } else {
      error.value = "Không thể tải chi tiết sản phẩm. Vui lòng thử lại.";
    }
    productData.value = null;
  } finally {
    loading.value = false;
  }
};

const setDefaultImage = (event) => {
  event.target.src = "http://localhost:8080/photos/placeholder.png";
};

// --- NEW: Method to change main image ---
const setMainImage = (index) => {
  currentImageIndex.value = index;
};

// Quantity controls
const validateQuantity = () => {
  if (!product.value || product.value.qty <= 0) {
    quantity.value = 0;
    return;
  }
  if (quantity.value < 1) {
    quantity.value = 1;
  }
  if (quantity.value > product.value.qty) {
    quantity.value = product.value.qty;
    showQuantityWarning.value = true;
    setTimeout(() => (showQuantityWarning.value = false), 3000);
  } else {
    showQuantityWarning.value = false;
  }
};

const incrementQuantity = () => {
  if (product.value && quantity.value < product.value.qty) {
    quantity.value++;
    showQuantityWarning.value = false;
  } else {
    showQuantityWarning.value = true;
    setTimeout(() => (showQuantityWarning.value = false), 3000);
  }
};

const decrementQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
    showQuantityWarning.value = false;
  }
};

// --- Add to Cart / Buy Now Logic ---
const isProcessingCart = ref(false); // Prevent multiple clicks

const handleAddToCart = async (productId, quantityToAdd, redirect = false) => {
  if (isProcessingCart.value) return;
  isProcessingCart.value = true;

  if (!authStore.isAuthenticated) {
    console.log("User not authenticated, redirecting to login...");
    localStorage.setItem("redirectAfterLogin", route.fullPath); // Save current path
    router.push({ name: "LoginRegister", query: { promptLogin: true } });
    isProcessingCart.value = false;
    return;
  }

  try {
    const formData = new URLSearchParams();
    formData.append("quantity", quantityToAdd);

    const response = await apiClient.post(`/cart/add/${productId}`, formData, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });

    if (response.data && response.data.message === "success") {
      authStore.updateCartCount(response.data.cartCount); // Update cart count in store
      alert("Đã thêm sản phẩm vào giỏ hàng!"); // Simple confirmation
      // TODO: Add a more sophisticated notification (e.g., toast)

      if (redirect) {
        router.push("/cart"); // Redirect to cart for "Buy Now"
      }
    } else {
      throw new Error(response.data.error || "Không thể thêm vào giỏ hàng");
    }
  } catch (error) {
    console.error("Error adding/buying:", error);
    alert(`Lỗi: ${error.message || "Không thể xử lý yêu cầu"}`);
    // TODO: Add a more sophisticated notification (e.g., toast)
  } finally {
    isProcessingCart.value = false;
  }
};

const addToCartHandler = () => {
  if (!product.value || quantity.value <= 0) return;
  validateQuantity(); // Ensure quantity is valid
  handleAddToCart(product.value.id, quantity.value);
};

const buyNow = () => {
  if (!product.value || quantity.value <= 0) return;
  validateQuantity(); // Ensure quantity is valid
  handleAddToCart(product.value.id, quantity.value, true); // Pass redirect=true
};

// Fetch data when the component mounts
onMounted(() => {
  fetchProductDetails(productId.value);
});

// Watch for changes in route params (e.g., navigating from one product detail to another)
watch(
  () => route.params.id,
  (newId) => {
    if (newId && newId !== productId.value) {
      // Check if newId is valid
      productId.value = newId;
      fetchProductDetails(newId);
    }
  }
);
</script>

<style scoped>
/* Reuse styles from detail_product.vue or define new ones */
.section {
  padding: 40px 0;
}
.product-image {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  position: relative; /* For discount badge positioning */
  background-color: #fff; /* Background for image container */
  padding: 20px;
}
.product-image img {
  width: 100%;
  height: auto;
  max-height: 500px; /* Limit image height */
  object-fit: contain;
  display: block;
}

.product-title {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}
.product-code {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}
.product-price {
  font-size: 1.5rem; /* Adjusted size */
  font-weight: 600;
  margin: 1.5rem 0;
  display: flex;
  align-items: baseline;
}
.discount-price {
  color: #e74c3c;
  font-size: 1.8rem; /* Emphasize discount price */
}
.original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 1.1rem;
  margin-left: 10px;
}

.product-info {
  line-height: 1.8;
  margin-bottom: 1.5rem;
  color: #4a4a4a;
}
.product-info strong {
  color: #1a1a1a;
  min-width: 110px; /* Align labels */
  display: inline-block;
}

.stock-status-out {
  color: #e74c3c;
  font-weight: bold;
}
.stock-status-in {
  color: #2ecc71;
  font-weight: bold;
}

.quantity-selector input {
  border-left: none;
  border-right: none;
}
.quantity-selector .btn {
  z-index: 1; /* Prevent buttons overlapping input focus */
}

.quantity-warning {
  color: #e74c3c;
  font-size: 0.8rem;
  /* display: none; Initially hidden */
}

.btn-buy,
.btn-cart {
  padding: 10px 25px;
  border-radius: 25px; /* Pill shape buttons */
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
  font-size: 0.9rem;
}

.btn-buy {
  background-color: #e74c3c;
  color: white;
}
.btn-buy:hover {
  background-color: #c0392b;
  transform: translateY(-2px);
}
.btn-buy:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  transform: none;
}

.btn-cart {
  background-color: #f0f0f0;
  color: #333;
}
.btn-cart:hover {
  background-color: #e0e0e0;
  transform: translateY(-2px);
}
.btn-cart:disabled {
  background-color: #eee;
  color: #aaa;
  cursor: not-allowed;
  transform: none;
}

.delivery-info {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-top: 2rem;
  font-size: 0.9rem;
}

.social-share {
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
  font-size: 0.9rem;
}
.social-share a {
  color: #555;
  margin-right: 10px;
  font-size: 1.2rem;
}
.social-share a:hover {
  color: #e74c3c;
}

.related-products h4 {
  font-size: 1.5rem;
  font-weight: 600;
}

/* Reusing ProductCard styles for related items */
.related-products .col-md-2 {
  margin-bottom: 1rem; /* Add spacing between related items */
}

.discount-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: #e74c3c;
  color: white;
  padding: 6px 10px;
  font-weight: bold;
  border-radius: 4px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  text-align: center;
  z-index: 10;
  font-size: 0.8rem;
}
.discount-badge span:first-child {
  font-size: 1rem;
  font-weight: 700;
  line-height: 1;
  display: block;
}
.discount-badge span:last-child {
  font-size: 0.7rem;
  font-weight: 600;
}

/* --- Swiper Styles --- */
.product-gallery-container {
  /* Container for main image and slider */
}

.main-image-wrapper {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  position: relative;
  background-color: #fff; /* Background for image container */
  padding: 10px; /* Optional padding around main image */
  min-height: 400px; /* Ensure space while loading */
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-product-image {
  max-width: 100%;
  max-height: 500px; /* Limit image height */
  object-fit: contain;
  display: block;
}

.thumbnail-swiper {
  width: 100%;
  height: auto; /* Let height be determined by content */
  padding-top: 10px;
  padding-bottom: 30px;
}

.thumbnail-slide {
  /* width is controlled by slides-per-view, don't set fixed width here */
  aspect-ratio: 1 / 1; /* Make the slide square */
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.3s ease;
  background-color: #f8f9fa;
}

.thumbnail-slide.active {
  border-color: #e74c3c;
}

.thumbnail-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* Style Swiper navigation buttons */
:deep(.swiper-button-next),
:deep(.swiper-button-prev) {
  color: #e74c3c; /* Customize arrow color */
  transform: scale(0.6); /* Make arrows slightly smaller */
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  width: 35px;
  height: 35px;
  top: 40%; /* Adjust vertical position */
}
:deep(.swiper-button-next:after),
:deep(.swiper-button-prev:after) {
  font-size: 16px; /* Adjust arrow icon size */
  font-weight: bold;
}

:deep(.swiper-pagination-bullet-active) {
  background-color: #e74c3c; /* Customize active pagination dot color */
}
/* --- End Swiper Styles --- */
</style>
