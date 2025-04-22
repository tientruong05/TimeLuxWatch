<template>
  <header class="header border-bottom py-3">
    <div class="container">
      <div class="row align-items-center">
        <!-- Logo -->
        <div class="col-lg-1 col-md-1 col-4 text-center logo-container">
          <a href="/" class="logo-link">
            <svg
              class="logo"
              width="80"
              height="80"
              viewBox="0 0 80 80"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <circle
                cx="40"
                cy="40"
                r="30"
                stroke="#d4af37"
                stroke-width="2"
                fill="none"
              />
              <circle cx="40" cy="40" r="3" fill="#d4af37" />
              <line
                x1="40"
                y1="40"
                x2="40"
                y2="20"
                stroke="#d4af37"
                stroke-width="2"
              />
              <line
                x1="40"
                y1="40"
                x2="55"
                y2="45"
                stroke="#d4af37"
                stroke-width="2"
              />
            </svg>
            <div class="logo-text">TimeLux Watch</div>
          </a>
        </div>

        <!-- Menu Chính -->
        <div class="col-lg-9 col-md-9 d-none d-md-block">
          <nav class="text-center">
            <ul class="nav justify-content-center">
              <li class="nav-item">
                <router-link to="/" class="nav-link text-uppercase"
                  >Trang Chủ</router-link
                >
              </li>

              <!-- Thương hiệu (Dropdown) -->
              <li class="nav-item dropdown watch-menu">
                <a class="nav-link text-uppercase" href="#">Thương Hiệu</a>
                <ul class="dropdown-menu">
                  <li v-if="loadingBrands">
                    <span class="dropdown-item">Loading...</span>
                  </li>
                  <li v-if="errorBrands">
                    <span class="dropdown-item text-danger">{{
                      errorBrands
                    }}</span>
                  </li>
                  <li
                    v-if="!loadingBrands && !errorBrands && brands.length === 0"
                  >
                    <span class="dropdown-item">No brands found</span>
                  </li>
                  <li v-for="brand in brands" :key="brand.id">
                    <router-link
                      class="dropdown-item"
                      :to="{
                        name: 'CategoryProductList',
                        query: { categoryId: brand.id },
                      }"
                    >
                      {{ brand.name }}
                    </router-link>
                  </li>
                </ul>
              </li>

              <!-- Flash Sale -->
              <li class="nav-item">
                <router-link to="/flash-sale" class="nav-link text-uppercase"
                  >Flash Sale</router-link
                >
              </li>

              <!-- Quản lý (Admin) -->
              <li class="nav-item dropdown" v-if="user && user.role">
                <a class="nav-link text-uppercase" href="#">Quản lý</a>
                <ul class="dropdown-menu">
                  <li>
                    <router-link
                      :to="{ name: 'AdminCategories' }"
                      class="dropdown-item"
                    >
                      QL thương hiệu
                    </router-link>
                  </li>
                  <li>
                    <router-link
                      :to="{ name: 'AdminDiscounts' }"
                      class="dropdown-item"
                    >
                      QL giảm giá
                    </router-link>
                  </li>
                  <li>
                    <router-link
                      :to="{ name: 'AdminProducts' }"
                      class="dropdown-item"
                    >
                      QL sản phẩm
                    </router-link>
                  </li>
                  <li>
                    <router-link
                      :to="{ name: 'AdminUsers' }"
                      class="dropdown-item"
                    >
                      QL người dùng
                    </router-link>
                  </li>
                </ul>
              </li>

              <!-- Người dùng (Đã đăng nhập) -->
              <li class="nav-item dropdown" v-if="user && !user.role">
                <a class="nav-link text-uppercase" href="#">Người dùng</a>
                <ul
                  class="dropdown-menu dropdown-menu-end"
                  aria-labelledby="userDropdown"
                >
                  <li>
                    <router-link to="/profile" class="dropdown-item">
                      <i class="bi bi-person-circle me-2"></i>Thông tin cá nhân
                    </router-link>
                  </li>
                  <li>
                    <router-link to="/users/orders" class="dropdown-item">
                      <i class="bi bi-box-seam me-2"></i>Đơn hàng của tôi
                    </router-link>
                  </li>
                  <li>
                    <router-link
                      to="/users/shopping-history"
                      class="dropdown-item"
                    >
                      <i class="bi bi-clock-history me-2"></i>Lịch sử mua hàng
                    </router-link>
                  </li>
                  <!-- Add Change Password Link -->
                  <li>
                    <router-link to="/change-password" class="dropdown-item">
                      <i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
                    </router-link>
                  </li>
                </ul>
              </li>

              <!-- Thống kê (Admin) -->
              <li class="nav-item dropdown" v-if="user && user.role">
                <a class="nav-link text-uppercase" href="#">Thống kê</a>
                <ul class="dropdown-menu">
                  <li>
                    <router-link
                      :to="{ name: 'BusinessStatistics' }"
                      class="dropdown-item"
                    >
                      Doanh thu
                    </router-link>
                  </li>
                  <li>
                    <router-link
                      :to="{ name: 'VipCustomers' }"
                      class="dropdown-item"
                    >
                      Khách hàng
                    </router-link>
                  </li>
                </ul>
              </li>
            </ul>
          </nav>
        </div>

        <!-- Thanh tìm kiếm và biểu tượng -->
        <div
          class="col-lg-2 col-md-2 col-8 text-end d-flex align-items-center justify-content-end"
        >
          <!-- Thanh tìm kiếm -->
          <div
            class="search-container"
            @mouseenter="handleMouseEnter"
            @mouseleave="handleMouseLeave"
          >
            <button class="search-btn header-icon" @click="focusSearchInput">
              <i class="bi bi-search"></i>
            </button>
            <input
              id="searchInput"
              v-model="searchQuery"
              type="text"
              class="search-input"
              placeholder="Tìm sản phẩm..."
              :style="inputStyle"
              @input="handleInput"
              @keypress.enter="submitSearch"
              @focus="handleFocus"
              @blur="handleBlur"
            />
            <div class="search-suggestions" v-show="showSuggestions">
              <div
                class="suggestion-item"
                v-for="suggestion in suggestions"
                :key="suggestion.id"
                @click="goToSuggestion(suggestion.id)"
              >
                <img
                  :src="getFirstImageUrl(suggestion.image)"
                  alt="Product Suggestion"
                  class="suggestion-image"
                  @error="handleImageError($event)"
                />
                <div class="suggestion-info">
                  <p class="suggestion-name">
                    {{ suggestion.name }}
                  </p>
                  <p class="suggestion-price">
                    {{
                      formatPrice(
                        suggestion.discountedPrice ?? suggestion.price
                      )
                    }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Biểu tượng đăng nhập/đăng xuất và giỏ hàng -->
          <router-link to="/login" class="header-icon" v-if="!user">
            <i class="bi bi-person"></i>
          </router-link>
          <router-link to="/logout" class="header-icon" v-if="user">
            <i class="bi bi-box-arrow-right"></i>
          </router-link>
          <router-link
            to="/cart"
            class="header-icon position-relative"
            v-if="user && !user.role"
          >
            <i class="bi bi-bag"></i>
            <span
              class="position-absolute translate-middle badge rounded-pill"
              >{{ cartCount }}</span
            >
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import axios from "axios";
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth"; // Import Pinia store
import apiClient from "@/services/api"; // Import apiClient
import { getFirstImageUrl } from "@/utils/imageUtils"; // <-- Import the new utility

export default {
  setup() {
    const router = useRouter();
    const authStore = useAuthStore(); // Khởi tạo store

    const searchQuery = ref("");
    const showSuggestions = ref(false);
    const isInputActive = ref(false);
    const isHovering = ref(false);
    const suggestions = ref([]); // Gợi ý tìm kiếm từ API
    const watchBrands = ref([]); // Danh sách thương hiệu
    const isNavOpen = ref(false);
    const brands = ref([]); // Add state for brands
    const loadingBrands = ref(true); // Add loading state
    const errorBrands = ref(null); // Add error state

    // Thay thế biến user và cartCount với computed properties từ store
    const user = computed(() => authStore.user);
    const cartCount = computed(() => {
      const count = authStore.cartCount;
      console.log(`HeaDer.vue: Computed cartCount evaluated. Value: ${count}`);
      return count;
    });

    const inputStyle = computed(() => {
      return {
        width: isInputActive.value ? (isMobile.value ? "150px" : "200px") : "0",
        padding: isInputActive.value ? "0 15px" : "0",
        opacity: isInputActive.value ? "1" : "0",
        border: isInputActive.value ? "1px solid #ddd" : "none",
        transition: "all 0.3s ease",
      };
    });

    const isMobile = computed(() => {
      return window.innerWidth <= 576;
    });

    const fetchInitialData = async () => {
      try {
        // Lấy thông tin người dùng từ store và API
        await authStore.checkAuthStatus();

        // Lấy danh sách thương hiệu
        const brandsResponse = await axios.get("/api/brands");
        watchBrands.value = brandsResponse.data;
      } catch (error) {
        console.error("Error fetching initial data:", error);
      }
    };

    const fetchSuggestions = async () => {
      if (!searchQuery.value.trim()) {
        suggestions.value = [];
        showSuggestions.value = false;
        return;
      }
      try {
        // Use the main search endpoint with a limit for suggestions
        const response = await apiClient.get("/home/search", {
          params: {
            q: encodeURIComponent(searchQuery.value),
            page: 0,
            size: 5, // Limit to 5 suggestions
          },
        });
        // Assuming the response structure is { products: [...] }
        suggestions.value = response.data.products || [];
        showSuggestions.value = suggestions.value.length > 0;
      } catch (error) {
        console.error("Error fetching suggestions:", error);
        suggestions.value = [];
        showSuggestions.value = false;
      }
    };

    const focusSearchInput = () => {
      isInputActive.value = true;
      // Need to use nextTick in the composition API context
      setTimeout(() => {
        document.querySelector(".search-input")?.focus();
      }, 0);
    };

    const handleInput = () => {
      showSuggestions.value = searchQuery.value.length > 0;
      fetchSuggestions();
    };

    const submitSearch = () => {
      if (searchQuery.value.trim()) {
        router.push(`/search?q=${encodeURIComponent(searchQuery.value)}`);
        showSuggestions.value = false;
        isInputActive.value = false;
      }
    };

    const handleFocus = () => {
      isInputActive.value = true;
    };

    const handleBlur = () => {
      if (!isHovering.value) {
        isInputActive.value = false;
        showSuggestions.value = false;
      }
    };

    const handleMouseEnter = () => {
      isHovering.value = true;
    };

    const handleMouseLeave = () => {
      isHovering.value = false;
      if (!document.querySelector(".search-input")?.matches(":focus")) {
        isInputActive.value = false;
        showSuggestions.value = false;
      }
    };

    const handleDocumentClick = (event) => {
      if (!event.target.closest(".search-container")) {
        showSuggestions.value = false;
        if (!document.querySelector(".search-input")?.matches(":focus")) {
          isInputActive.value = false;
        }
      }
    };

    const handleResize = () => {
      // Force a recomputation of reactive properties
    };

    const formatPrice = (price) => {
      // Add check for valid number
      if (typeof price !== "number" || isNaN(price)) {
        return "N/A"; // Or 'Liên hệ', or an empty string ''
      }
      return price.toLocaleString("vi-VN", {
        style: "currency",
        currency: "VND",
      });
    };

    const toggleNav = () => {
      isNavOpen.value = !isNavOpen.value;
    };

    // Fetch brands from API
    const fetchBrands = async () => {
      loadingBrands.value = true;
      errorBrands.value = null;
      try {
        const response = await apiClient.get("/brands");
        brands.value = response.data || [];
      } catch (error) {
        console.error("Error fetching brands:", error);
        errorBrands.value = "Could not load brands.";
        brands.value = []; // Ensure brands is empty on error
      } finally {
        loadingBrands.value = false;
      }
    };

    // Add handler for suggestion image errors
    const handleImageError = (event) => {
      event.target.src = "/placeholder.png"; // Placeholder image path
    };

    // Optional: Add method to navigate when suggestion is clicked
    const goToSuggestion = (productId) => {
      console.log(`Navigating to product detail for ID: ${productId}`); // Add logging
      router.push({ name: "ProductDetail", params: { id: productId } });
      showSuggestions.value = false; // Hide suggestions after click
    };

    onMounted(() => {
      window.addEventListener("resize", handleResize);
      document.addEventListener("click", handleDocumentClick);
      fetchInitialData();
      fetchBrands();
    });

    onBeforeUnmount(() => {
      window.removeEventListener("resize", handleResize);
      document.removeEventListener("click", handleDocumentClick);
    });

    return {
      searchQuery,
      showSuggestions,
      isInputActive,
      isHovering,
      suggestions,
      watchBrands,
      user,
      cartCount,
      inputStyle,
      isMobile,
      fetchInitialData,
      fetchSuggestions,
      focusSearchInput,
      handleInput,
      submitSearch,
      handleFocus,
      handleBlur,
      handleMouseEnter,
      handleMouseLeave,
      handleDocumentClick,
      handleResize,
      formatPrice,
      isNavOpen,
      toggleNav,
      brands,
      loadingBrands,
      errorBrands,
      fetchBrands,
      getFirstImageUrl,
      handleImageError,
      goToSuggestion,
    };
  },
};
</script>
<style scoped>
/* Container cho search */
.search-container {
  position: relative;
  display: inline-block;
}

/* Ẩn ô tìm kiếm mặc định */
.search-input {
  width: 0;
  height: 35px;
  padding: 0;
  border: none;
  outline: none;
  border-radius: 20px;
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0;
  transition: all 0.3s ease;
}

/* Khi hover vào container thì hiện ô input */
.search-container:hover .search-input {
  width: 200px;
  padding: 0 15px;
  opacity: 1;
  border: 1px solid #ddd;
}

/* Giữ hiển thị khi focus vào input */
.search-input:focus {
  width: 200px;
  padding: 0 15px;
  opacity: 1;
  border: 1px solid #ddd;
}

/* Điều chỉnh chiều rộng input trên màn hình nhỏ */
@media (max-width: 576px) {
  .search-container:hover .search-input,
  .search-input:focus {
    width: 150px; /* Giảm chiều rộng trên màn hình nhỏ */
  }
}

/* Icon styling cho nút tìm kiếm */
.search-btn.header-icon {
  margin-right: 8px; /* Giảm từ 15px xuống 8px */
}

.header-icon {
  color: #000;
  font-size: 1.2rem;
  padding: 8px;
  margin: 0 8px;
  transition: all 0.3s ease;
  position: relative;
  background: transparent;
  border: none;
  cursor: pointer;
}

.header-icon:hover {
  color: #e74c3c;
  transform: translateY(-2px);
}

/* Responsive adjustments cho phần icon */
@media (max-width: 767.98px) {
  .header-icon {
    margin: 0 5px; /* Giảm margin trên mobile */
    padding: 6px; /* Giảm padding trên mobile */
  }

  .search-btn.header-icon {
    margin-right: 5px;
  }
}

/* Styling for search suggestions */
.search-suggestions {
  position: absolute;
  top: 40px;
  right: 0;
  width: 250px;
  background: white;
  border-radius: 5px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: none;
  max-height: 300px; /* Limit height */
  overflow-y: auto; /* Enable scroll if needed */
}

/* Show search suggestions when input is focused */
.search-input:focus ~ .search-suggestions {
  display: block;
}

/* Điều chỉnh độ rộng suggestions trên màn hình nhỏ */
@media (max-width: 576px) {
  .search-suggestions {
    width: 200px;
    right: -20px; /* Điều chỉnh vị trí */
  }
}

/* Styling for suggestion items */
.suggestion-item {
  display: flex;
  align-items: center;
  padding: 8px 10px; /* Adjusted padding */
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  position: relative; /* Ensure z-index works */
  z-index: 1001; /* Higher than search box */
}

.suggestion-item:last-child {
  border-bottom: none; /* Remove border for last item */
}

.suggestion-item:hover {
  background-color: #f9f9f9;
}

.suggestion-item img.suggestion-image {
  /* Target the specific class */
  width: 40px; /* Fixed width */
  height: 40px; /* Fixed height */
  object-fit: cover; /* Crop image to fit */
  margin-right: 10px;
  border-radius: 3px;
  flex-shrink: 0; /* Prevent shrinking */
}

.suggestion-info {
  flex-grow: 1;
  overflow: hidden; /* Prevent content overflow */
}

.suggestion-name {
  font-size: 14px;
  margin: 0;
  font-weight: 500;
  white-space: nowrap; /* Prevent wrapping */
  overflow: hidden;
  text-overflow: ellipsis; /* Add ... for long text */
}

.suggestion-price {
  font-size: 12px;
  color: #e74c3c;
  margin: 2px 0 0 0; /* Adjusted margin */
  font-weight: 600;
}

/* Giữ nguyên phần CSS còn lại của header */
.header {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  background: white;
}

.nav-link {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 1.2px;
  color: #000 !important;
  padding: 8px 8px !important;
  margin: 0 4px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link::after {
  content: "";
  position: absolute;
  width: 0;
  height: 2px;
  background: #e74c3c;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  transition: width 0.3s ease;
}

.nav-link:hover {
  color: #e74c3c !important;
}

.nav-link:hover::after {
  width: 70%;
}

.nav-link.active {
  color: #e74c3c !important;
}

.nav-link.active::after {
  width: 70%;
}

.badge {
  font-size: 10px;
  padding: 4px 6px;
  background-color: #e74c3c !important;
  border: 2px solid white;
}

.logo-container img {
  max-height: 50px;
  transition: transform 0.3s ease;
}

.logo-container:hover img {
  transform: scale(1.05);
}

@media (max-width: 768px) {
  .nav-link {
    padding: 6px 12px !important;
    margin: 0 2px;
  }

  .header-icon {
    margin: 0 4px;
  }
}

/* Hover dropdown menu */
.nav-item.dropdown:hover .dropdown-menu {
  display: block;
  margin-top: 0;
}

/* General dropdown menu styles */
.dropdown-menu {
  display: none;
  background-color: white; /* Ensure white background */
  color: black; /* Ensure black text */
  border: 1px solid #ddd; /* Optional: Add a subtle border */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* Optional: Add shadow for depth */
}

/* Target specifically the dropdown under watch-menu to override any conflicting styles */
.watch-menu .dropdown-menu {
  background-color: white !important; /* Force white background */
  color: black !important; /* Force black text */
}

/* Ensure all list items and links within the dropdown have black text */
.watch-menu .dropdown-menu li,
.watch-menu .dropdown-menu a,
.watch-menu .dropdown-menu span {
  color: black !important; /* Force black text for all elements */
  background-color: transparent !important; /* Ensure no background color interference */
}

/* Override any active or hover states that might apply blue background */
.watch-menu .dropdown-menu li:hover,
.watch-menu .dropdown-menu a:hover,
.watch-menu .dropdown-menu span:hover {
  background-color: #e74c3c !important; /* Red background on hover */
  color: white !important; /* White text on hover */
}

/* General dropdown item styles */
.dropdown-item {
  color: black !important; /* Force black text for dropdown items */
  background-color: transparent !important; /* Ensure no background color by default */
}

.dropdown-item:hover {
  background-color: #e74c3c !important; /* Keep the hover background as red */
  color: white !important; /* White text on hover */
}

/* Watch Menu Dropdown Styles */
.watch-menu {
  position: relative;
}

.watch-submenu {
  display: none;
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  padding: 20px;
  width: 500px;
  z-index: 1000;
}

.watch-menu:hover .watch-submenu {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.category-section {
  position: relative;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.category-section:hover {
  background: #f8f9fa;
}

.dropdown-header {
  color: #e74c3c;
  font-weight: 600;
  font-size: 0.9rem;
  padding: 0 0 10px 0;
  margin-bottom: 10px;
  border-bottom: 2px solid #e74c3c;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.sub-items {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.watch-submenu .dropdown-item {
  padding: 8px 15px;
  color: #333;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  border-radius: 6px;
  text-decoration: none;
  display: block;
}

.watch-submenu .dropdown-item:hover {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
  padding-left: 20px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .watch-submenu {
    position: static;
    width: 100%;
    transform: none;
    box-shadow: none;
    padding: 10px;
  }

  .watch-menu:hover .watch-submenu {
    display: flex;
    flex-direction: column;
  }

  .category-section {
    margin-bottom: 15px;
  }
}

.logo {
  height: 60px;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.3));
}
.logo-text {
  font-size: 12px;
  font-weight: 700;
  color: #d4af37;
  text-transform: uppercase;
  letter-spacing: 2px;
  margin-top: 8px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);

  white-space: nowrap;
}

.logo-link {
  display: inline-block;
  transition: transform 0.3s ease;
}

.logo-link:hover {
  transform: scale(1.05);
}

a {
  text-decoration: none;
}
</style>
