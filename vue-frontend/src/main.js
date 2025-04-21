import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import { createPinia } from "pinia";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./style.css";
import App from "./App.vue";
import { useAuthStore } from "@/stores/auth";

// Import page components
import HomePage from "@/views/HomePage.vue";
import ProductDetailPage from "@/views/ProductDetailPage.vue";
import LoginRegister from "@/components/LoginRegister.vue";
import ForgotPass from "@/components/ForgotPass.vue";
import Logout from "@/components/Logout.vue";
import FlashSale from "@/components/FlashSale.vue";
import ProfilePage from "@/components/profilePage.vue";
import PasswordChange from "@/components/change_pw.vue";
import ShoppingCart from "@/components/cart.vue";
import OrderPage from "@/components/OrderPage.vue";
import OrderConfirmation from "@/components/OrderConfirmation.vue";
import Checkout from "@/components/checkout.vue";
import ShoppingHistory from "@/components/shoppingHistory.vue";
import CategoryProductList from "@/components/CategoryProductList.vue";
import GenderProductList from "@/components/GenderProductList.vue";
import SearchResultsPage from "@/components/searchPage.vue";
import AdminUsers from "@/components/crud_users.vue";
import AdminProducts from "@/components/CRUDProducts.vue";
import AdminCategories from "@/components/CRUDCategories.vue";
import AdminDiscounts from "@/components/CRUDDiscount.vue";
import AboutPage from "@/components/aboutPage.vue";
import WarrantyPage from "@/components/warrantyPage.vue";
import TermsPaymentPage from "@/components/TermsPayment.vue";

// Define routes
const routes = [
  { path: "/", component: HomePage, name: "Home" },
  // Define route for Product Detail Page
  {
    path: "/product/:id", // Use dynamic segment :id
    name: "ProductDetail", // Match the name used in ProductCard
    component: ProductDetailPage,
    props: true, // Automatically pass route params as props (optional but convenient)
  },
  {
    path: "/login", // Path for the login/register page
    name: "LoginRegister",
    component: LoginRegister,
  },
  {
    path: "/forgot-pass", // Path for the forgot password page
    name: "ForgotPassword",
    component: ForgotPass,
  },
  {
    path: "/logout", // Path for the logout page
    name: "Logout",
    component: Logout,
  },
  {
    path: "/flash-sale", // Path for the flash sale page
    name: "FlashSale",
    component: FlashSale,
  },
  {
    path: "/profile", // Path for the user profile page
    name: "Profile",
    component: ProfilePage,
    meta: { requiresAuth: true }, // This route requires authentication
  },
  {
    path: "/change-password", // Path for the change password page
    name: "PasswordChange",
    component: PasswordChange,
    meta: { requiresAuth: true }, // <-- Thêm yêu cầu đăng nhập
  },
  {
    path: "/cart", // Path for the shopping cart page
    name: "ShoppingCart",
    component: ShoppingCart,
    meta: { requiresAuth: true }, // <-- Thêm yêu cầu đăng nhập
  },
  {
    path: "/users/orders", // Path for the user orders page
    name: "OrderPage",
    component: OrderPage,
    meta: { requiresAuth: true }, // <-- THÊM ROUTE NÀY
  },
  {
    path: "/order-confirmation/:id", // Path for order confirmation with ID parameter
    name: "OrderConfirmation",
    component: OrderConfirmation,
    props: true, // Pass route params as props
    meta: { requiresAuth: true }, // <-- THÊM ROUTE NÀY
  },
  {
    path: "/checkout", // Path for the checkout page
    name: "Checkout",
    component: Checkout,
    meta: { requiresAuth: true }, // <-- Thêm yêu cầu đăng nhập
  },
  {
    path: "/users/shopping-history", // Path for the shopping history page
    name: "ShoppingHistory",
    component: ShoppingHistory,
    meta: { requiresAuth: true }, // <-- ADD THIS ROUTE
  },
  {
    path: "/products/category", // Use a base path
    name: "CategoryProductList",
    component: CategoryProductList,
    // Props are not directly used here as categoryId comes from query
  },
  {
    path: "/products/gender/:gender", // Use gender as a route parameter
    name: "GenderProductList",
    component: GenderProductList,
    props: true, // Pass route params (like gender) as props to the component
  },
  {
    path: "/search", // Path for the search results page
    name: "SearchResults",
    component: SearchResultsPage,
    // Props are not typically needed here as search query comes from query params
  },
  {
    path: "/admin/users", // Define the admin route for user management
    name: "AdminUsers",
    component: AdminUsers,
    meta: { requiresAuth: true, requiresAdmin: true }, // Require auth and admin role
  },
  {
    path: "/admin/products", // Define the admin route for product management
    name: "AdminProducts",
    component: AdminProducts,
    meta: { requiresAuth: true, requiresAdmin: true }, // Require auth and admin role
  },
  {
    path: "/admin/categories", // Define the admin route for category management
    name: "AdminCategories",
    component: AdminCategories,
    meta: { requiresAuth: true, requiresAdmin: true }, // Require auth and admin role
  },
  {
    path: "/admin/discounts", // Define the admin route for discount management
    name: "AdminDiscounts",
    component: AdminDiscounts,
    meta: { requiresAuth: true, requiresAdmin: true }, // Require auth and admin role
  },
  // Add routes for statistics pages
  {
    path: "/statistics/business", // Add route for business statistics
    name: "BusinessStatistics",
    component: () => import("@/components/business-statistics.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/statistics/customers", // Add route for VIP customers statistics
    name: "VipCustomers",
    component: () => import("@/components/VipPage.vue"),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/about",
    name: "About",
    component: AboutPage,
  },
  {
    path: "/warranty",
    name: "Warranty",
    component: WarrantyPage,
  },
  {
    path: "/payment-terms",
    name: "PaymentTerms",
    component: TermsPaymentPage,
  },
  // --- End of new static page routes ---
];

// Create router instance
const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: "active",
});

// Khởi tạo Pinia
const pinia = createPinia();

// Create Vue app instance
const app = createApp(App);

// Sử dụng Pinia
app.use(pinia);

// Use the router
app.use(router);

// Lấy instance AuthStore sau khi Pinia đã được cài đặt
const auth = useAuthStore(pinia);

// Đồng bộ hóa trạng thái ban đầu từ localStorage vào store
// Làm điều này trước khi router guard chạy
if (localStorage.getItem("user")) {
  try {
    auth.user = JSON.parse(localStorage.getItem("user"));
    auth.cartCount = parseInt(localStorage.getItem("cartCount") || "0");
    console.log(
      "Initial auth state synced from localStorage:",
      auth.user?.username
    );
  } catch (e) {
    console.error("Error parsing localStorage data:", e);
    localStorage.removeItem("user");
    localStorage.removeItem("cartCount");
  }
}

// Navigation guard for routes requiring authentication
router.beforeEach(async (to, from, next) => {
  console.log(`Navigation: ${from.path} -> ${to.path}`);

  // Check if the route requires authentication
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    console.log("Route requires authentication");

    // Bây giờ AuthStore đã có trạng thái ban đầu từ localStorage (nếu có)
    let isAuthenticated = auth.isLoggedIn(); // Dùng hàm kiểm tra local của store
    console.log("Initial local check (isLoggedIn):", isAuthenticated);

    // Nếu trạng thái local là đã đăng nhập, tin tưởng nó và kiểm tra API background
    if (isAuthenticated) {
      console.log("User is locally authenticated, proceeding to", to.path);
      next(); // Cho đi trước

      // Kiểm tra lại với server ở background
      auth.checkAuthStatus().catch((error) => {
        console.error("Background auth check failed:", error);
        // Nếu check thất bại, cập nhật store và có thể redirect nếu cần
        if (
          router.currentRoute.value.meta.requiresAuth &&
          router.currentRoute.value.name !== "LoginRegister"
        ) {
          console.log("Background check failed, redirecting to login");
          localStorage.setItem(
            "redirectAfterLogin",
            router.currentRoute.value.fullPath
          );
          router.replace({
            name: "LoginRegister",
            query: { session: "expired" },
          });
        }
      });
    } else {
      // Nếu local state là chưa đăng nhập, kiểm tra với API
      console.log("User is NOT locally authenticated, checking with API...");
      try {
        isAuthenticated = await auth.checkAuthStatus(); // Gọi API check
        console.log("API auth check result:", isAuthenticated);

        if (isAuthenticated) {
          console.log("API confirms authentication, proceeding to", to.path);
          next();
        } else {
          console.log("API confirms NOT authenticated, redirecting to login");
          localStorage.setItem("redirectAfterLogin", to.fullPath);
          next({
            name: "LoginRegister",
            query: { redirect: to.fullPath },
          });
        }
      } catch (error) {
        console.error("API Auth check failed during navigation:", error);
        localStorage.setItem("redirectAfterLogin", to.fullPath);
        next({
          name: "LoginRegister",
          query: { error: "auth_check_failed" },
        });
      }
    }
  } else {
    // Route doesn't require auth, proceed
    next();
  }
});

// Mount the app
app.mount("#app");
