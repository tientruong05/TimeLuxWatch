<template>
  <div>
    <!-- Header component sẽ được import riêng -->
    <!-- <header-component></header-component> -->

    <!-- Flash Sale Section -->
    <section v-if="isFlashSaleActive" class="flash-sale-section">
      <div class="container">
        <h2>Flash Sale Đang Diễn Ra!</h2>
        <p>
          Thời gian còn lại: <span id="countdown">{{ countdownText }}</span>
        </p>
      </div>
    </section>

    <!-- Search Results -->
    <section class="collection-section">
      <div class="container">
        <h2 v-if="searchQuery" class="section-title">
          Kết quả tìm kiếm cho: <span>{{ searchQuery }}</span>
        </h2>
        <h2 v-else class="section-title">Kết quả tìm kiếm</h2>

        <div v-if="loading" class="text-center">
          <p>Đang tải kết quả...</p>
          <!-- Optional: Add a spinner or loading animation -->
        </div>

        <div v-else-if="error" class="alert alert-danger" role="alert">
          {{ error }}
        </div>

        <div v-else-if="products.length > 0" class="row g-4">
          <div class="col-md-3" v-for="product in products" :key="product.id">
            <product-card :product="product"></product-card>
          </div>
        </div>

        <div v-else class="text-center">
          <p>Không tìm thấy sản phẩm nào.</p>
        </div>

        <!-- Pagination -->
        <nav
          aria-label="Page navigation"
          v-if="totalPages > 1 && !loading && !error && products.length > 0"
        >
          <ul class="pagination justify-content-center">
            <!-- Previous Button -->
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage - 1)"
                >Trước</a
              >
            </li>

            <!-- Page Numbers (Simplified logic for demonstration) -->
            <li
              class="page-item"
              v-for="pageNumber in visiblePages"
              :key="pageNumber"
              :class="{ active: currentPage === pageNumber - 1 }"
            >
              <span v-if="pageNumber === '...'" class="page-link">...</span>
              <a
                v-else
                class="page-link"
                href="#"
                @click.prevent="changePage(pageNumber - 1)"
                >{{ pageNumber }}</a
              >
            </li>

            <!-- Next Button -->
            <li
              class="page-item"
              :class="{ disabled: currentPage === totalPages - 1 }"
            >
              <a
                class="page-link"
                href="#"
                @click.prevent="changePage(currentPage + 1)"
                >Sau</a
              >
            </li>
          </ul>
        </nav>
      </div>
    </section>

    <!-- Footer component sẽ được import riêng -->
    <!-- <footer-component></footer-component> -->
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import apiClient from "@/services/api"; // Corrected path
import ProductCard from "./ProductCard.vue"; // Import ProductCard
// import HeaderComponent from './HeaDer.vue'; // Assuming HeaderComponent is correctly set up
// import FooterComponent from './FooterComponent.vue'; // Assuming FooterComponent is correctly set up

const route = useRoute();
const router = useRouter();

const products = ref([]);
const searchQuery = ref(route.query.q || "");
const currentPage = ref(parseInt(route.query.page || "0"));
const totalPages = ref(0);
const pageSize = ref(parseInt(route.query.size || "12")); // Or your default page size
const loading = ref(false);
const error = ref(null);
const isFlashSaleActive = ref(false);
const flashSaleEndTime = ref(null);
const countdownText = ref("");
const countdownInterval = ref(null);

const fetchData = async () => {
  loading.value = true;
  error.value = null;
  // Do NOT clear products.value here, let it display old results while loading

  // Ensure currentPage is a valid number before API call
  const pageToFetch = Number.isInteger(currentPage.value)
    ? currentPage.value
    : 0;

  try {
    const response = await apiClient.get("/home/search", {
      params: {
        q: searchQuery.value,
        page: pageToFetch, // Use the validated page number
        size: pageSize.value,
      },
    });
    // Correctly access the products array from the response
    products.value = response.data.products || []; // Update products
    totalPages.value = response.data.totalPages || 0;

    // Validate currentPage against the *new* totalPages
    if (pageToFetch >= totalPages.value && totalPages.value > 0) {
      console.warn(
        `Requested page ${pageToFetch} is out of bounds (total: ${totalPages.value}). Correcting...`
      );
      currentPage.value = totalPages.value - 1;
      // Need to update URL and potentially refetch if correction happened
      // To avoid refetch loop, only update route if needed
      if (currentPage.value !== pageToFetch) {
        updateRoute(); // Update URL, watcher will trigger refetch if needed
      }
    } else if (pageToFetch < 0 && totalPages.value > 0) {
      currentPage.value = 0;
      if (currentPage.value !== pageToFetch) {
        updateRoute();
      }
    }
  } catch (err) {
    console.error("Error fetching search results:", err);
    error.value = "Không thể tải kết quả tìm kiếm. Vui lòng thử lại sau.";
    products.value = []; // Clear products on error
    totalPages.value = 0;
    // Handle specific error types if needed
    if (err.response) {
      error.value += ` (Lỗi: ${err.response.status})`;
    } else if (err.request) {
      error.value = "Không nhận được phản hồi từ máy chủ.";
    } else {
      error.value = `Lỗi: ${err.message}`;
    }
  } finally {
    loading.value = false;
  }
};

const updateRoute = () => {
  // Only push if query params actually change to avoid duplicate navigation errors
  const currentQuery = route.query;
  const newQuery = {
    q: searchQuery.value,
    page: currentPage.value,
    size: pageSize.value,
  };

  // Simple comparison (might need deeper comparison for complex objects)
  if (JSON.stringify(currentQuery) !== JSON.stringify(newQuery)) {
    router
      .push({
        name: "SearchResults", // Use the route name defined in main.js
        query: newQuery,
      })
      .catch((err) => {
        if (err.name !== "NavigationDuplicated") {
          console.error("Navigation Error:", err);
        }
      });
  }
};

const changePage = (page) => {
  // page is 0-indexed
  if (page >= 0 && page < totalPages.value && page !== currentPage.value) {
    currentPage.value = page; // Update state FIRST
    updateRoute(); // THEN update the URL (watcher will handle fetch)
  }
};

// Watch for changes in route query parameters (e.g., search term or page number)
watch(
  () => route.query,
  (newQuery, oldQuery) => {
    const newSearch = newQuery.q || "";
    const newPage = parseInt(newQuery.page || "0");
    const newSize = parseInt(newQuery.size || String(pageSize.value)); // Use current pageSize as default

    const oldSearch = oldQuery?.q || "";
    const oldPage = parseInt(oldQuery?.page || "0");
    const oldSize = parseInt(oldQuery?.size || String(pageSize.value));

    // Update state based on new query
    searchQuery.value = newSearch;
    currentPage.value = newPage;
    pageSize.value = newSize;

    // Fetch data only if relevant parameters changed
    if (newSearch !== oldSearch || newPage !== oldPage || newSize !== oldSize) {
      fetchData();
    }
  },
  { deep: true, immediate: true } // immediate: true to fetch on initial load
);

// Simplified Pagination logic for displaying page numbers
const visiblePages = computed(() => {
  const pages = [];
  const total = totalPages.value;
  const current = currentPage.value;
  const maxVisible = 5;

  if (total <= 1) return [];

  if (total <= maxVisible) {
    for (let i = 1; i <= total; i++) pages.push(i);
  } else {
    pages.push(1);
    let start = Math.max(2, current - 1);
    let end = Math.min(total - 1, current + 3);

    if (current < 3) {
      end = Math.min(total - 1, maxVisible - 1); // Adjust end if near start
    }
    if (current > total - 4) {
      start = Math.max(2, total - maxVisible + 2); // Adjust start if near end
    }

    if (start > 2) pages.push("...");
    for (let i = start; i <= end; i++) pages.push(i);
    if (end < total - 1) pages.push("...");
    pages.push(total);
  }
  return pages;
});

// Remove the onMounted hook as the watcher with immediate: true handles initial load
// onMounted(() => {
//   fetchData();
// });

const startCountdown = () => {
  if (!flashSaleEndTime.value) return;

  const endTime = new Date(flashSaleEndTime.value.replace("T", " ")).getTime();

  countdownInterval.value = setInterval(() => {
    const now = new Date().getTime();
    const distance = endTime - now;

    if (distance < 0) {
      clearInterval(countdownInterval.value);
      countdownText.value = "Flash Sale đã kết thúc!";
      return;
    }

    const days = Math.floor(distance / (1000 * 60 * 60 * 24));
    const hours = Math.floor(
      (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
    );
    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((distance % (1000 * 60)) / 1000);

    countdownText.value =
      days + "d " + hours + "h " + minutes + "m " + seconds + "s";
  }, 1000);
};
</script>

<style scoped>
body {
  font-family: "Roboto", "Arial", sans-serif;
  color: #1a1a1a;
  background-color: #f5f5f5;
}
.row {
  display: flex;
  flex-wrap: wrap;
}
.product-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.product-card .card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.product-title {
  height: 60px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-top: 15px;
}
.product-card .btn-shop {
  margin-top: auto;
}
.product-card .text-center {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.product-card img {
  max-height: 180px;
  width: auto;
}
.price,
.price-custom {
  margin: 10px 0;
  height: 30px;
}
.product-card {
  border: none;
  border-radius: 0;
  padding: 20px;
  transition: all 0.4s ease;
  background: white;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}
.product-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}
.product-card img {
  width: 100%;
  height: auto;
  object-fit: contain;
  margin-bottom: 15px;
  transition: transform 0.3s ease;
}
.product-card:hover img {
  transform: scale(1.05);
}
.product-title {
  font-size: 1.3rem;
  color: #0b0b0b;
  margin: 15px 0;
  font-weight: 400;
  letter-spacing: 0.5px;
}
.price {
  color: #e74c3c;
  font-size: 1.4rem;
  font-weight: 500;
  margin: 15px 0;
}
.btn-shop {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 0.9rem;
  box-shadow: 0 4px 15px rgba(212, 175, 55, 0.2);
}
.btn-shop:hover {
  background-color: #0b0b0b;
  color: #e74c3c;
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(212, 175, 55, 0.3);
}
.collection-section {
  background-color: white;
  padding: 80px 0;
}
.section-title {
  text-align: center;
  margin-bottom: 50px;
  color: #0b0b0b;
  font-size: 2.5rem;
  font-weight: 300;
  letter-spacing: 2px;
  position: relative;
  padding-bottom: 15px;
}
.section-title::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background-color: #e74c3c;
}
.price-custom {
  text-decoration: line-through;
}
.product {
  text-decoration: none;
  color: black;
}
.product:hover {
  color: #e74c3c;
  text-decoration: underline;
}
.discount-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #e74c3c;
  color: white;
  padding: 8px 10px;
  font-weight: bold;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}
.discount-badge span:first-child {
  font-size: 1.2rem;
  font-weight: 700;
  line-height: 1;
}
.discount-badge span:last-child {
  font-size: 1rem;
  font-weight: 600;
}
.product-card:hover .discount-badge {
  transform: scale(1.05);
  transition: transform 0.3s ease;
}
.flash-sale-section {
  background-color: #ff4444;
  color: white;
  padding: 20px 0;
  text-align: center;
  margin-bottom: 30px;
}
.flash-sale-section h2 {
  font-size: 2rem;
  font-weight: 500;
  margin-bottom: 10px;
}
.flash-sale-section p {
  font-size: 1.2rem;
}
.pagination {
  margin-top: 20px;
}
.page-link {
  background-color: #fff;
  color: #e74c3c;
  border: 1px solid #e74c3c;
}
.page-link:hover {
  background-color: #e74c3c;
  color: #fff;
}
.page-item.active .page-link {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}
.page-item.disabled .page-link {
  background-color: #fff;
  color: #ccc;
}
</style>
