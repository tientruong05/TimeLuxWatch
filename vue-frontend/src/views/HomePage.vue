<template>
  <div>
    <!-- Admin Mode Indicator - Removed -->

    <!-- Hero Section with Carousel from InDex.vue -->
    <section class="hero-section mb-5">
      <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
        <div class="carousel-inner">
          <div class="carousel-item active">
            <img
              src="https://www.luxurybazaar.com/cdn/uploads/2023/12/bannerbg.jpg"
              alt="Hero 1"
              class="hero-image"
            />
          </div>
          <div class="carousel-item">
            <img
              src="https://ramawatch.co/blog/wp-content/uploads/2024/08/brands.jpeg"
              alt="Hero 2"
              class="hero-image"
            />
          </div>
          <div class="carousel-item">
            <img
              src="https://media.gq-magazine.co.uk/photos/6660cf60aa9580e0fa0b0b91/16:9/w_1280,c_limit/Best-watch-brands-hp.jpeg"
              alt="Hero 3"
              class="hero-image"
            />
          </div>
        </div>
        <button
          class="carousel-control-prev"
          type="button"
          data-bs-target="#heroCarousel"
          data-bs-slide="prev"
        >
          <span class="carousel-control-prev-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Previous</span>
        </button>
        <button
          class="carousel-control-next"
          type="button"
          data-bs-target="#heroCarousel"
          data-bs-slide="next"
        >
          <span class="carousel-control-next-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Next</span>
        </button>
        <div class="hero-content">
          <h1 class="display-4 fw-bold">Khám phá đồng hồ cao cấp</h1>
          <p class="lead">Khám phá bộ sưu tập đồng hồ cao cấp của chúng tôi</p>
          <a href="#collection" class="btn btn-shop">Mua ngay</a>
        </div>
      </div>
    </section>

    <!-- Featured Categories -->
    <section class="container my-5">
      <div class="row">
        <div class="col-md-6">
          <div class="category-card">
            <img
              src="https://stayhard.com/cdn/shop/files/Which_wrist_should_you_wear_your_watch_on__A_men_s_style_guide.webp?v=1717752622&width=3000"
              alt="Men's Watches"
            />
            <div class="category-overlay">
              <h3 class="fw-bold">Đồng hồ nam</h3>
              <a class="explore-link"
                ><router-link
                  to="/products/gender/male"
                  class="nav-link text-uppercase"
                  >Khám phá ngay</router-link
                ></a
              >
            </div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="category-card">
            <img
              src="https://cdn.tatlerasia.com/asiatatler/i/my/2021/07/01162934-untitled-design-8_cover_1900x1200.jpg"
              alt="Women's Watches"
            />
            <div class="category-overlay">
              <h3 class="fw-bold">Đồng hồ nữ</h3>
              <a class="explore-link"
                ><router-link
                  to="/products/gender/female"
                  class="nav-link text-uppercase"
                  >Khám phá ngay</router-link
                ></a
              >
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Loading/Error State -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div v-if="error" class="alert alert-danger my-5">
      {{ error }}
    </div>

    <!-- Data Display (once loaded) -->
    <div v-if="!loading && !error" id="collection">
      <!-- New Products -->
      <section class="container mb-5" v-if="homepageData.newProducts?.length">
        <h2 class="section-title">Hàng mới</h2>
        <div class="row g-4">
          <div
            class="col-md-3"
            v-for="product in homepageData.newProducts"
            :key="'new-' + product.id"
          >
            <ProductCard :product="product" />
          </div>
        </div>
      </section>

      <!-- Best Selling Products -->
      <section class="container mb-5" v-if="homepageData.bestProducts?.length">
        <h2 class="section-title">Bán chạy</h2>
        <div class="row g-4">
          <div
            class="col-md-3"
            v-for="product in homepageData.bestProducts"
            :key="'best-' + product.id"
          >
            <ProductCard :product="product" />
          </div>
        </div>
      </section>

      <!-- Flash Sale Section (Enhanced from InDex.vue) -->
      <section
        v-if="homepageData.flashSaleEndTime"
        class="flash-sale-section py-4 mb-5"
      >
        <div class="container">
          <div
            class="d-flex justify-content-between align-items-center mb-3 flex-wrap"
          >
            <div class="d-flex align-items-center flex-wrap">
              <h2
                class="mb-0 me-3 text-uppercase fw-bold"
                style="font-size: 1.5rem"
              >
                <i class="bi bi-lightning-charge-fill me-2"></i>FLASH SALE
              </h2>
              <div
                v-if="homepageData.flashSaleEndTime"
                class="countdown-wrapper mt-2 mt-md-0"
              >
                <span class="me-2">Kết thúc trong:</span>
                <CountdownTimer :endTime="homepageData.flashSaleEndTime" />
              </div>
            </div>
            <router-link
              to="/flash-sale"
              class="text-white text-decoration-none d-none d-md-block flash-sale-view-all"
            >
              <span class="me-1">Xem tất cả</span
              ><i class="bi bi-chevron-right"></i>
            </router-link>
          </div>
          <div class="flash-sale-products">
            <div class="row g-4">
              <div
                class="col-md-3"
                v-for="product in homepageData.flashSaleProducts"
                :key="'flash-' + product.id"
              >
                <ProductCard :product="product" />
              </div>
            </div>
          </div>
          <div class="text-center mt-3 d-md-none">
            <router-link to="/flash-sale" class="btn btn-dark btn-sm">
              Xem tất cả Flash Sale <i class="bi bi-chevron-right ms-1"></i>
            </router-link>
          </div>
        </div>
      </section>

      <!-- Who We Are Section -->
      <section class="who-we-are py-5" style="background-color: #f5f5f5">
        <div class="container">
          <h2 class="section-title">Về Chúng Tôi</h2>
          <div class="row align-items-center">
            <div class="col-md-6">
              <img
                src="https://images.unsplash.com/photo-1533139502658-0198f920d8e8?ixlib=rb-4.0.3"
                alt="Watch Store Interior"
                class="img-fluid rounded shadow"
                style="width: 100%; height: 400px; object-fit: cover"
              />
            </div>
            <div class="col-md-6">
              <div class="ps-md-4"></div>
              <h3 class="about-heading">Đam Mê & Chất Lượng</h3>
              <p class="about-text">
                Chúng tôi tự hào là đơn vị chuyên cung cấp các sản phẩm đồng hồ
                cao cấp với hơn 10 năm kinh nghiệm trong ngành. Với cam kết mang
                đến những sản phẩm chất lượng nhất cùng dịch vụ khách hàng xuất
                sắc, chúng tôi luôn được khách hàng tin tưởng lựa chọn.
              </p>
              <div class="row mt-4">
                <div class="col-6">
                  <div class="text-center mb-3">
                    <i class="bi bi-check-circle-fill feature-icon"></i>
                    <h4 class="mt-2 feature-title">Chính Hãng</h4>
                  </div>
                </div>
                <div class="col-6">
                  <div class="text-center mb-3">
                    <i class="bi bi-award-fill feature-icon"></i>
                    <h4 class="mt-2 feature-title">Bảo Hành</h4>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import apiClient from "@/services/api"; // Use alias for cleaner imports
import ProductCard from "@/components/ProductCard.vue"; // Assuming ProductCard component exists
import CountdownTimer from "@/components/CountdownTimer.vue"; // Assuming CountdownTimer component exists
import { formatPrice } from "@/utils/formatters"; // Assuming a utility for formatting
import "bootstrap/dist/js/bootstrap.bundle.min.js"; // Import Bootstrap JS for carousel
import { useAuthStore } from "@/stores/auth"; // Import auth store

// Get auth store instance
const authStore = useAuthStore();

// Check if current user is admin
const isAdmin = computed(() => authStore.user && authStore.user.role);

const homepageData = ref({});
const loading = ref(true);
const error = ref(null);

const fetchHomepageData = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await apiClient.get("/home/homepage");
    homepageData.value = response.data;
    console.log("Homepage Data:", homepageData.value); // Log fetched data

    // Add debug logging for flash sale data
    if (homepageData.value.flashSaleEndTime) {
      const endTime = new Date(homepageData.value.flashSaleEndTime);
      const now = new Date();
      console.log("Flash Sale End Time:", endTime);
      console.log("Current Time:", now);
      console.log("Is End Time in Future:", endTime > now);
      console.log("Flash Sale Products:", homepageData.value.flashSaleProducts);

      // Force flash sale to be active if end time is in the future
      if (endTime > now && homepageData.value.flashSaleProducts?.length > 0) {
        homepageData.value.isFlashSaleActive = true;
      }
    }
  } catch (err) {
    console.error("Error fetching homepage data:", err);
    error.value = "Không thể tải dữ liệu trang chủ. Vui lòng thử lại sau.";
    if (err.response) {
      // Log more detailed error if available from backend
      console.error("Backend response:", err.response.data);
      error.value += ` (Status: ${err.response.status})`;
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchHomepageData();
});

// Make formatPrice available in the template if needed directly
// If ProductCard handles formatting, this might not be necessary here
const formattedPrice = formatPrice;
</script>

<style scoped>
.category-card {
  position: relative;
  overflow: hidden;
  height: 400px;
  margin-bottom: 30px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}
.category-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
  filter: brightness(0.9);
}
.category-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
}
.category-overlay h3 {
  font-size: 2rem;
  font-weight: 300;
  letter-spacing: 1px;
}
.explore-link {
  color: white;
  text-decoration: none;
  padding: 8px 20px;
  border: 2px solid white;
  border-radius: 25px;
  transition: all 0.3s ease;
  display: inline-block;
  margin-top: 10px;
}
.explore-link:hover {
  background-color: white;
  color: #e74c3c;
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
}
/* Hero Section Styles from InDex.vue */
.hero-section {
  position: relative;
  overflow: hidden;
  margin-bottom: 50px;
}

.hero-image {
  width: 100%;
  height: 70vh;
  object-fit: cover;
  object-position: center;
}

.hero-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
  z-index: 2;
  width: 80%;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.hero-content h1 {
  font-size: 3.5rem;
  margin-bottom: 20px;
  animation: fadeInDown 1s ease;
}

.hero-content p {
  font-size: 1.5rem;
  margin-bottom: 30px;
  animation: fadeInUp 1s ease;
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
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  animation: fadeIn 1.2s ease;
}

.btn-shop:hover {
  background-color: #0b0b0b;
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

/* Section Title Styles */
.section-title {
  text-align: center;
  margin-bottom: 40px;
  font-weight: 300;
  font-size: 2.2rem;
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
  background-color: #e74c3c; /* Accent color */
}

/* Flash Sale Specific Styles (from InDex.vue) */
.flash-sale-section {
  background-color: #b22210; /* Dark red background */
  color: white;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(255, 68, 68, 0.2);
}

.countdown-wrapper {
  background-color: rgba(0, 0, 0, 0.2);
  padding: 8px 15px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
}

.flash-sale-view-all {
  position: relative;
  padding: 6px 10px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.flash-sale-view-all:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.flash-sale-view-all i {
  transition: transform 0.3s ease;
}

.flash-sale-view-all:hover i {
  transform: translateX(3px);
}

/* Responsive adjustments */
@media (max-width: 767.98px) {
  .hero-image {
    height: 50vh;
  }

  .hero-content h1 {
    font-size: 2rem;
  }

  .hero-content p {
    font-size: 1rem;
  }

  .section-title {
    font-size: 1.8rem;
  }

  .flash-sale-section .d-flex.justify-content-between {
    flex-direction: column;
    align-items: flex-start !important; /* Override default */
  }

  .flash-sale-section h2 {
    margin-bottom: 10px;
  }

  .countdown-wrapper {
    margin-top: 10px;
  }
}

/* Animations for hero content */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Who We Are Section Styles */
.who-we-are {
  background-color: #f5f5f5;
  padding: 80px 0;
  margin-top: 30px;
}

.about-heading {
  color: #e74c3c;
  font-weight: 300;
  margin-bottom: 20px;
  font-size: 2rem;
}

.about-text {
  line-height: 1.8;
  color: #666;
  font-size: 1.1rem;
}

.feature-icon {
  color: #e74c3c;
  font-size: 2rem;
}

.feature-title {
  font-size: 1.2rem;
  color: #333;
  font-weight: 500;
}

/* Add responsive styles for Who We Are section */
@media (max-width: 767.98px) {
  .who-we-are {
    padding: 50px 0;
  }

  .about-heading {
    font-size: 1.8rem;
    margin-top: 20px;
  }

  .about-text {
    font-size: 1rem;
  }
}

/* Admin Mode Indicator - Removed */
</style>
