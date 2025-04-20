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
  products.value = []; // Clear previous results

  try {
    const response = await apiClient.get("/home/search", {
      params: {
        q: searchQuery.value,
        page: currentPage.value,
        size: pageSize.value,
      },
    });
    // Correctly access the products array from the response
    products.value = response.data.products || []; // Use 'products' key
    totalPages.value = response.data.totalPages || 0;
    // Flash sale logic might need adjustment or removal if not part of this endpoint
    // isFlashSaleActive.value = response.data.isFlashSaleActive || false;
    // flashSaleEndTime.value = response.data.flashSaleEndTime || null;

    if (isFlashSaleActive.value && flashSaleEndTime.value) {
      startCountdown();
    }

    // Validate currentPage after fetching data
    if (currentPage.value >= totalPages.value && totalPages.value > 0) {
      currentPage.value = totalPages.value - 1;
      // Optionally refetch data for the last page or update URL
      // await fetchData(); // Be cautious of infinite loops
      updateRoute(); // Update URL to reflect valid page
    } else if (currentPage.value < 0 && totalPages.value > 0) {
      currentPage.value = 0;
      updateRoute(); // Update URL to reflect valid page
    }
  } catch (err) {
    console.error("Error fetching search results:", err);
    error.value = "Không thể tải kết quả tìm kiếm. Vui lòng thử lại sau.";
    // Handle specific error types if needed
    if (err.response) {
      // Server responded with a status code outside 2xx range
      error.value += ` (Lỗi: ${err.response.status})`;
    } else if (err.request) {
      // Request was made but no response received
      error.value = "Không nhận được phản hồi từ máy chủ.";
    } else {
      // Something else happened in setting up the request
      error.value = `Lỗi: ${err.message}`;
    }
  } finally {
    loading.value = false;
  }
};

const updateRoute = () => {
  router
    .push({
      name: "SearchResults", // Use the route name defined in main.js
      query: {
        q: searchQuery.value,
        page: currentPage.value,
        size: pageSize.value,
      },
    })
    .catch((err) => {
      // Optional: Catch navigation errors, e.g., NavigationDuplicated
      if (err.name !== "NavigationDuplicated") {
        console.error("Navigation Error:", err);
      }
    });
};

const changePage = (page) => {
  if (page >= 0 && page < totalPages.value && page !== currentPage.value) {
    currentPage.value = page;
    updateRoute(); // Update URL which triggers the watcher
  }
};

// Watch for changes in route query parameters (e.g., search term or page number)
watch(
  () => route.query,
  (newQuery) => {
    searchQuery.value = newQuery.q || "";
    const newPage = parseInt(newQuery.page || "0");
    const newSize = parseInt(newQuery.size || "12");

    // Only fetch data if relevant parameters change
    if (
      newPage !== currentPage.value ||
      newSize !== pageSize.value ||
      searchQuery.value !== (route.query.q || "")
    ) {
      currentPage.value = newPage;
      pageSize.value = newSize;
      fetchData();
    }
  },
  { deep: true } // Watch nested properties if needed, though query is usually flat
);

// Pagination logic for displaying page numbers
const visiblePages = computed(() => {
  const pages = [];
  const maxVisible = 5; // Max number of page links shown (excluding prev/next, including ellipsis)
  const halfVisible = Math.floor(maxVisible / 2);

  if (totalPages.value <= maxVisible) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    // Always show first page
    pages.push(1);

    let startPage = Math.max(2, currentPage.value - halfVisible + 2);
    let endPage = Math.min(
      totalPages.value - 1,
      currentPage.value + halfVisible + 1
    );

    // Adjust start/end if near beginning or end
    if (currentPage.value < halfVisible) {
      endPage = Math.min(totalPages.value - 1, maxVisible - 1);
    } else if (currentPage.value >= totalPages.value - halfVisible - 1) {
      startPage = Math.max(2, totalPages.value - maxVisible + 2);
    }

    // Add ellipsis before middle pages if needed
    if (startPage > 2) {
      pages.push("...");
    }

    // Add middle pages
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    // Add ellipsis after middle pages if needed
    if (endPage < totalPages.value - 1) {
      pages.push("...");
    }

    // Always show last page
    pages.push(totalPages.value);
  }

  return pages;
});

// Fetch data when the component is first mounted
onMounted(() => {
  // Update refs from initial route query just in case
  searchQuery.value = route.query.q || "";
  currentPage.value = parseInt(route.query.page || "0");
  pageSize.value = parseInt(route.query.size || "12");
  fetchData();
});

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
