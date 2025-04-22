# TimeLux Watch - Tài liệu Dự án

## Giới thiệu

TimeLux Watch là một nền tảng thương mại điện tử chuyên về đồng hồ xa xỉ, được xây dựng với kiến trúc ứng dụng web hiện đại bao gồm Spring Boot cho backend và Vue.js cho frontend. Hệ thống cung cấp đầy đủ các chức năng cho cả khách hàng và quản trị viên, từ duyệt sản phẩm, quản lý giỏ hàng, xử lý đơn hàng đến quản lý sản phẩm, người dùng và báo cáo thống kê.

## Cấu trúc Dự án

Dự án được chia thành hai phần chính:

1. **TimeLuxWatchBE**: Backend Java sử dụng Spring Boot
2. **vue-frontend**: Frontend sử dụng Vue.js 3

## 1. Công nghệ Sử dụng

### 1.1. Backend (TimeLuxWatchBE)

- **Ngôn ngữ**: Java
- **Framework**: Spring Boot
- **Cơ sở dữ liệu**: Microsoft SQL Server
- **ORM**: JPA/Hibernate
- **Bảo mật**: Spring Security
- **Dịch vụ Email**: Spring Mail (cấu hình cho Gmail)
- **Công cụ xây dựng**: Maven
- **Phiên bản Java**: 17+

### 1.2. Frontend (vue-frontend)

- **Framework**: Vue.js 3 (Composition API)
- **Quản lý trạng thái**: Pinia
- **Định tuyến**: Vue Router
- **HTTP Client**: Axios
- **UI Framework**: Bootstrap 5
- **Icons**: Bootstrap Icons
- **Công cụ xây dựng**: Vite
- **Phiên bản Node.js**: 18+

## 2. Kiến trúc Hệ thống

### 2.1. Kiến trúc Backend

Hệ thống backend được tổ chức theo mô hình kiến trúc phân lớp:

- **Controller Layer**: Định nghĩa các API endpoints và xử lý requests
- **Service Layer**: Chứa logic nghiệp vụ
- **Repository Layer**: Truy cập dữ liệu qua Spring Data JPA
- **Entity Layer**: Đại diện cho các bảng trong cơ sở dữ liệu
- **DTO Layer**: Các đối tượng truyền dữ liệu cho API responses
- **Exception Layer**: Xử lý ngoại lệ tập trung
- **Config Layer**: Cấu hình hệ thống

#### 2.1.1. Cấu trúc Thư mục Backend

```
TimeLuxWatchBE/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── TimeLuxWatchBE/
│   │   │           ├── config/            # Cấu hình hệ thống
│   │   │           ├── controller/        # API Controllers
│   │   │           ├── dto/               # Data Transfer Objects
│   │   │           ├── entity/            # JPA Entities
│   │   │           ├── exception/         # Exception Handlers
│   │   │           ├── payload/           # Request/Response Objects
│   │   │           ├── repository/        # JPA Repositories
│   │   │           ├── service/           # Business Logic
│   │   │           └── TimeLuxWatchBeApplication.java  # Main Application
│   │   └── resources/
│   │       ├── application.properties     # Cấu hình ứng dụng
│   │       ├── static/                    # Tài nguyên tĩnh
│   │       └── templates/                 # Templates
│   └── test/                              # Unit Tests
├── pom.xml                               # Maven Dependencies
└── ...
```

### 2.2. Kiến trúc Frontend

Frontend được xây dựng như một ứng dụng Single-Page Application (SPA) với cấu trúc:

- **Components**: Các thành phần UI có thể tái sử dụng
- **Views**: Các trang tương ứng với routes
- **Stores**: Quản lý trạng thái với Pinia
- **Services**: Giao tiếp API
- **Assets**: Tài nguyên tĩnh
- **Utils**: Các hàm tiện ích

#### 2.2.1. Cấu trúc Thư mục Frontend

```
vue-frontend/
├── node_modules/                  # Dependencies
├── public/                        # Public files
├── src/
│   ├── assets/                    # Hình ảnh, CSS, fonts
│   ├── components/                # Vue components
│   │   ├── HeaDer.vue             # Header component
│   │   ├── FooTer.vue             # Footer component
│   │   ├── LoginRegister.vue      # Đăng nhập/Đăng ký
│   │   ├── ForgotPass.vue         # Quên mật khẩu
│   │   ├── cart.vue               # Giỏ hàng
│   │   ├── checkout.vue           # Thanh toán
│   │   └── ...                    # Các components khác
│   ├── views/                     # Page components
│   │   ├── HomePage.vue           # Trang chủ
│   │   ├── ProductDetailPage.vue  # Chi tiết sản phẩm
│   │   └── ...                    # Các trang khác
│   ├── stores/                    # Pinia stores
│   │   ├── auth.js                # Quản lý xác thực
│   │   └── notificationStore.js   # Quản lý thông báo
│   ├── services/                  # API services
│   │   └── api.js                 # Axios configuration
│   ├── utils/                     # Utility functions
│   ├── App.vue                    # Root component
│   ├── main.js                    # Entry point
│   └── style.css                  # Global CSS
├── index.html                     # HTML template
├── vite.config.js                 # Vite configuration
├── package.json                   # Dependencies and scripts
└── ...
```

## 3. Mô hình Dữ liệu

### 3.1. Entities

#### 3.1.1. UserEntity

- **Chức năng**: Quản lý thông tin người dùng (khách hàng và admin)
- **Thuộc tính chính**:
  - `id`: Khóa chính
  - `username`: Tên đăng nhập
  - `password`: Mật khẩu (Base64 encoded)
  - `email`: Email (dùng để xác thực)
  - `fullName`: Họ tên đầy đủ
  - `phone`: Số điện thoại
  - `address`: Địa chỉ
  - `role`: Vai trò (true = admin, false = user)
  - `status`: Trạng thái tài khoản (đã kích hoạt hay chưa)

#### 3.1.2. ProductEntity

- **Chức năng**: Lưu trữ thông tin sản phẩm đồng hồ
- **Thuộc tính chính**:
  - `id`: Khóa chính
  - `name`: Tên sản phẩm
  - `description`: Mô tả sản phẩm
  - `price`: Giá sản phẩm
  - `image`: URL hình ảnh
  - `gender`: Giới tính (Nam/Nữ/Unisex)
  - `status`: Trạng thái sản phẩm
  - `createDate`: Ngày tạo
  - `quantity`: Số lượng tồn kho
  - `brand`: Thương hiệu
  - `categoryId`: Liên kết với danh mục
  - `subCategoryId`: Liên kết với danh mục con

#### 3.1.3. CategoryEntity và SubCategoryEntity

- **Chức năng**: Quản lý danh mục sản phẩm
- **Thuộc tính chính**:
  - `id`: Khóa chính
  - `name`: Tên danh mục
  - `description`: Mô tả danh mục

#### 3.1.4. OrderEntity và OrderDetailEntity

- **Chức năng**: Quản lý đơn hàng và chi tiết đơn hàng
- **Thuộc tính OrderEntity**:
  - `id`: Khóa chính
  - `userId`: Liên kết với người dùng
  - `orderDate`: Ngày đặt hàng
  - `status`: Trạng thái đơn hàng
  - `totalAmount`: Tổng số tiền
  - `shippingAddress`: Địa chỉ giao hàng
  - `paymentMethod`: Phương thức thanh toán
- **Thuộc tính OrderDetailEntity**:
  - `id`: Khóa chính
  - `orderId`: Liên kết với đơn hàng
  - `productId`: Liên kết với sản phẩm
  - `quantity`: Số lượng
  - `price`: Giá tại thời điểm mua

#### 3.1.5. CartEntity

- **Chức năng**: Lưu trữ giỏ hàng của người dùng
- **Thuộc tính chính**:
  - `id`: Khóa chính
  - `userId`: Liên kết với người dùng
  - `productId`: Liên kết với sản phẩm
  - `qty`: Số lượng
  - `dateAdded`: Ngày thêm vào giỏ

#### 3.1.6. DiscountEntity và DiscountDetailEntity

- **Chức năng**: Quản lý khuyến mãi và flash sale
- **Thuộc tính DiscountEntity**:
  - `id`: Khóa chính
  - `discountName`: Tên khuyến mãi
  - `startDate`: Ngày bắt đầu
  - `endDate`: Ngày kết thúc
  - `discountPercentage`: Phần trăm giảm giá
  - `isActive`: Trạng thái kích hoạt
- **Thuộc tính DiscountDetailEntity**:
  - `id`: Khóa chính
  - `discountId`: Liên kết với khuyến mãi
  - `productId`: Liên kết với sản phẩm
  - `specialPrice`: Giá đặc biệt (nếu có)

### 3.2. Relationships

- **UserEntity -> OrderEntity**: One-to-Many (Một người dùng có nhiều đơn hàng)
- **UserEntity -> CartEntity**: One-to-Many (Một người dùng có nhiều sản phẩm trong giỏ hàng)
- **CategoryEntity -> SubCategoryEntity**: One-to-Many (Một danh mục có nhiều danh mục con)
- **CategoryEntity/SubCategoryEntity -> ProductEntity**: One-to-Many (Một danh mục có nhiều sản phẩm)
- **OrderEntity -> OrderDetailEntity**: One-to-Many (Một đơn hàng có nhiều chi tiết đơn hàng)
- **ProductEntity -> OrderDetailEntity**: One-to-Many (Một sản phẩm có thể xuất hiện trong nhiều đơn hàng)
- **ProductEntity -> CartEntity**: One-to-Many (Một sản phẩm có thể xuất hiện trong nhiều giỏ hàng)
- **DiscountEntity -> DiscountDetailEntity**: One-to-Many (Một khuyến mãi áp dụng cho nhiều sản phẩm)

## 4. API Endpoints

### 4.1. API Xác thực (AuthApiController)

- **POST /api/auth/login**: Đăng nhập

  - **Parameters**: username (email), password
  - **Response**: Thông tin người dùng, số lượng sản phẩm trong giỏ hàng

- **POST /api/auth/register**: Đăng ký

  - **Parameters**: username, email, password, phone, address, fullname
  - **Response**: Thông báo đăng ký thành công

- **GET /api/auth/activate**: Kích hoạt tài khoản

  - **Parameters**: email
  - **Response**: Redirect sau khi kích hoạt

- **GET /api/auth/logout**: Đăng xuất

  - **Response**: Thông báo đăng xuất thành công

- **POST /api/auth/resetPassword**: Đặt lại mật khẩu
  - **Parameters**: email
  - **Response**: Thông báo gửi mật khẩu mới qua email

### 4.2. API Sản phẩm

#### 4.2.1. ProductDetailApiController

- **GET /api/products/detail/{id}**: Lấy chi tiết sản phẩm
  - **Response**: Chi tiết sản phẩm và các sản phẩm liên quan

#### 4.2.2. ProductCRUDApiController (Admin)

- **GET /api/products**: Lấy danh sách sản phẩm (phân trang)
- **GET /api/products/{id}**: Lấy thông tin sản phẩm theo ID
- **POST /api/products**: Thêm sản phẩm mới
- **PUT /api/products/{id}**: Cập nhật sản phẩm
- **DELETE /api/products/{id}**: Xóa sản phẩm

### 4.3. API Trang Chủ (HomeApiController)

- **GET /api/home/homepage**: Lấy dữ liệu trang chủ
  - **Response**:
    - Sản phẩm mới
    - Sản phẩm bán chạy
    - Sản phẩm giảm giá
    - Thông tin Flash Sale (nếu có)
- **GET /api/home/search**: Tìm kiếm sản phẩm
  - **Parameters**: q (từ khóa tìm kiếm), page, size
  - **Response**: Kết quả tìm kiếm phân trang

### 4.4. API Giỏ Hàng (CartApiController)

- **GET /api/cart**: Lấy giỏ hàng của người dùng
- **POST /api/cart/add**: Thêm sản phẩm vào giỏ hàng
- **PUT /api/cart/update**: Cập nhật số lượng sản phẩm
- **DELETE /api/cart/remove/{id}**: Xóa sản phẩm khỏi giỏ hàng
- **GET /api/cart/count**: Lấy số lượng sản phẩm trong giỏ hàng

### 4.5. API Đơn Hàng (ShoppingHistoryApiController)

- **GET /api/users/orders**: Lấy lịch sử đơn hàng
- **GET /api/users/orders/{id}**: Lấy chi tiết đơn hàng
- **POST /api/cart/checkout**: Thanh toán giỏ hàng
  - **Response**: Thông tin đơn hàng mới tạo

### 4.6. API Danh mục (CategoryApiController, CategoryCRUDApiController)

- **GET /api/categories**: Lấy tất cả danh mục
- **GET /api/categories/{id}**: Lấy danh mục theo ID
- **GET /api/categories/{id}/products**: Lấy sản phẩm theo danh mục
- **POST /api/categories**: Thêm danh mục mới (Admin)
- **PUT /api/categories/{id}**: Cập nhật danh mục (Admin)
- **DELETE /api/categories/{id}**: Xóa danh mục (Admin)

### 4.7. API Flash Sale (FlashSaleApiController)

- **GET /api/flash-sale/active**: Kiểm tra flash sale đang hoạt động
- **GET /api/flash-sale/products**: Lấy sản phẩm flash sale

### 4.8. API Tài khoản (AccountApiController)

- **GET /api/account/profile**: Lấy thông tin cá nhân
- **PUT /api/account/profile**: Cập nhật thông tin cá nhân
- **POST /api/account/change-password**: Đổi mật khẩu
- **GET /api/account/check-auth**: Kiểm tra trạng thái đăng nhập

### 4.9. API Thống kê (StatisticsApiController - Admin)

- **GET /api/statistics/revenue**: Thống kê doanh thu
- **GET /api/statistics/products**: Thống kê sản phẩm
- **GET /api/statistics/customers**: Thống kê khách hàng

## 5. Cấu hình Hệ thống

### 5.1. Cấu hình Backend (application.properties)

```properties
# Tên ứng dụng
spring.application.name=TimeLuxWatchBE

# Cấu hình SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=java6;encrypt=true;trustServerCertificate=true
spring.datasource.username=zander
spring.datasource.password=123456
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# Cấu hình Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=nhihupy00008@gmail.com
spring.mail.password=eabj fcff fyqa ozhk
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Cấu hình Server
server.port=8080
server.servlet.context-path=/

# Cấu hình CORS
spring.web.cors.allowed-origins=http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true

# Cấu hình Spring Framework
spring.main.allow-bean-definition-overriding=true

# Cấu hình Logging
logging.level.org.springframework=INFO
logging.level.poly.edu=DEBUG

# Cấu hình Upload File
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Cấu hình Session
spring.session.timeout=30m

# Cấu hình DevTools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Cấu hình Encoding
spring.web.character-encoding=utf-8
server.tomcat.uri-encoding=UTF-8
spring.http.encoding.charset=UTF-8
spring.http.encoding.enabled=true
spring.http.encoding.force=true
```

### 5.2. Cấu hình Frontend (api.js, main.js)

#### 5.2.1. Cấu hình API (api.js)

```javascript
import axios from "axios";

// URL API Backend
const API_BASE_URL = "http://localhost:8080/api";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Bật cookies cho tất cả requests
});

// Interceptors xử lý request
apiClient.interceptors.request.use(
  (config) => {
    // Xử lý đặc biệt cho FormData requests
    if (config.data instanceof FormData) {
      delete config.headers["Content-Type"];
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptors xử lý response
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Xử lý lỗi 401 Unauthorized
    if (error.response && error.response.status === 401) {
      // Xử lý chuyển hướng khi hết phiên đăng nhập
      // ...
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

#### 5.2.2. Cấu hình Routes và Auth Guard (main.js)

```javascript
// Định nghĩa routes
const routes = [
  { path: "/", component: HomePage, name: "Home" },
  {
    path: "/product/:id",
    name: "ProductDetail",
    component: ProductDetailPage,
    props: true,
  },
  {
    path: "/login",
    name: "LoginRegister",
    component: LoginRegister,
  },
  // ... các routes khác ...
  {
    path: "/profile",
    name: "Profile",
    component: ProfilePage,
    meta: { requiresAuth: true }, // Route yêu cầu đăng nhập
  },
  {
    path: "/admin/users",
    name: "AdminUsers",
    component: AdminUsers,
    meta: { requiresAuth: true, requiresAdmin: true }, // Yêu cầu quyền admin
  },
  // ... các routes admin khác ...
];

// Navigation guard kiểm tra xác thực
router.beforeEach(async (to, from, next) => {
  // Kiểm tra nếu route yêu cầu đăng nhập
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    // Kiểm tra đăng nhập từ localStorage trước
    let isAuthenticated = auth.isLoggedIn();

    if (isAuthenticated) {
      // Cho phép truy cập và kiểm tra server ở nền
      next();
      auth.checkAuthStatus().catch((error) => {
        // Xử lý khi phiên đăng nhập hết hạn
        // ...
      });
    } else {
      // Chuyển hướng đến trang đăng nhập
      localStorage.setItem("redirectAfterLogin", to.fullPath);
      next({ name: "LoginRegister" });
    }
  } else if (to.matched.some((record) => record.meta.requiresAdmin)) {
    // Kiểm tra quyền admin
    if (auth.isLoggedIn() && auth.isAdmin) {
      next();
    } else {
      next({ name: "Home" });
    }
  } else {
    // Route công khai, cho phép truy cập
    next();
  }
});
```

## 6. Quản lý Trạng thái

### 6.1. Auth Store (auth.js)

```javascript
import { defineStore } from "pinia";
import apiClient from "@/services/api";

export const useAuthStore = defineStore("auth", {
  // State
  state: () => ({
    user: localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user"))
      : null,
    cartCount: localStorage.getItem("cartCount")
      ? parseInt(localStorage.getItem("cartCount"))
      : 0,
    loading: false,
    error: null,
    justCheckedOut: false,
    isAuthCheckPending: false,
  }),

  // Getters
  getters: {
    isAuthenticated: (state) => !!state.user,
    userName: (state) => (state.user ? state.user.fullName : ""),
    isAdmin: (state) => state.user && state.user.role,
  },

  // Actions
  actions: {
    // Đăng nhập
    async login(username, password) {
      // Xử lý đăng nhập
      // ...
    },

    // Đăng xuất
    async logout() {
      // Xử lý đăng xuất
      // ...
    },

    // Cập nhật số lượng giỏ hàng
    updateCartCount(count) {
      this.cartCount = count;
      localStorage.setItem("cartCount", count.toString());
    },

    // Làm mới số lượng giỏ hàng từ server
    async refreshCartCount() {
      // ...
    },

    // Kiểm tra trạng thái đăng nhập từ server
    async checkAuthStatus() {
      // ...
    },

    // Kiểm tra đăng nhập từ localStorage
    isLoggedIn() {
      return !!this.user;
    },

    // Xóa dữ liệu người dùng
    clearUserData() {
      this.user = null;
      this.cartCount = 0;
      localStorage.removeItem("user");
      localStorage.removeItem("cartCount");
    },
  },
});
```

### 6.2. Notification Store (notificationStore.js)

```javascript
import { defineStore } from "pinia";
import { v4 as uuidv4 } from "uuid";

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [],
  }),

  actions: {
    addNotification({ message, type = "primary", duration = 5000 }) {
      const id = uuidv4();
      this.notifications.push({
        id,
        message,
        type,
        duration,
      });

      // Tự động xóa thông báo sau duration
      setTimeout(() => {
        this.removeNotification(id);
      }, duration);

      return id;
    },

    removeNotification(id) {
      const index = this.notifications.findIndex((n) => n.id === id);
      if (index !== -1) {
        this.notifications.splice(index, 1);
      }
    },
  },
});
```

### 6.3. Sự kết hợp giữa Axios và Pinia trong Frontend

#### 6.3.1. Sử dụng Axios để giao tiếp với API

Frontend TimeLux Watch sử dụng thư viện **Axios** như một HTTP client để giao tiếp với backend API. Việc cấu hình Axios được thực hiện thông qua file `api.js` trong thư mục `services` với các đặc điểm sau:

```javascript
// URL API Backend
const API_BASE_URL = "http://localhost:8080/api";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Bật cookies cho tất cả requests
});
```

**Mục đích chính của Axios:**

- Tạo một instance Axios với cấu hình đồng nhất cho toàn bộ ứng dụng
- Thiết lập URL cơ sở để tất cả các request đều gửi đến endpoint API của backend
- Bật chế độ `withCredentials` để đảm bảo cookies (phiên đăng nhập) được gửi kèm trong mỗi request
- Cung cấp một cách thức giao tiếp nhất quán với backend

#### 6.3.2. Interceptors - Xử lý Request và Response

Axios được cấu hình với các interceptors để xử lý request và response:

```javascript
// Interceptors xử lý request
apiClient.interceptors.request.use(
  (config) => {
    // Xử lý đặc biệt cho FormData requests
    if (config.data instanceof FormData) {
      delete config.headers["Content-Type"];
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptors xử lý response
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Xử lý lỗi 401 Unauthorized
    if (error.response && error.response.status === 401) {
      // Xử lý chuyển hướng khi hết phiên đăng nhập
      if (
        !isCurrentlyOnLoginPage &&
        !isPublicApiRequest &&
        authRetryCount < MAX_AUTH_RETRIES
      ) {
        localStorage.removeItem("user");
        localStorage.removeItem("cartCount");
        localStorage.setItem("redirectAfterLogin", currentPath);
        window.location.href = "/login?session=expired";
      }
    }
    return Promise.reject(error);
  }
);
```

**Mục đích của interceptors:**

- **Request Interceptor**: Xử lý các trường hợp đặc biệt trước khi gửi request, như khi gửi FormData (upload file)
- **Response Interceptor**: Xử lý các lỗi phản hồi, đặc biệt là lỗi 401 (Unauthorized) để chuyển hướng người dùng về trang đăng nhập khi phiên làm việc hết hạn

#### 6.3.3. Cách Axios và Pinia liên kết với nhau

Trong TimeLux Watch, Axios và Pinia được liên kết chặt chẽ với nhau thông qua các actions trong store:

```javascript
// Một ví dụ về action trong Auth Store sử dụng Axios
async login(username, password) {
  this.loading = true;
  try {
    const formData = new URLSearchParams();
    formData.append("username", username);
    formData.append("password", password);

    const response = await apiClient.post("/auth/login", formData, {
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      withCredentials: true,
    });

    // Lưu vào Pinia store
    this.user = response.data.user;
    this.cartCount = response.data.cartCount || 0;

    // Lưu vào localStorage cho persistence
    localStorage.setItem("user", JSON.stringify(this.user));
    localStorage.setItem("cartCount", this.cartCount.toString());

    return { success: true, message: response.data.message };
  } catch (error) {
    this.error = error.response?.data?.error || error.message;
    return { success: false, message: this.error };
  } finally {
    this.loading = false;
  }
}
```

**Workflow tổng thể:**

1. **Component gọi Store Action**:

   ```javascript
   const authStore = useAuthStore();
   await authStore.login(email, password);
   ```

2. **Store Action gọi Axios API**:

   ```javascript
   const response = await apiClient.post("/auth/login", formData);
   ```

3. **Kết quả API được lưu vào Store State**:

   ```javascript
   this.user = response.data.user;
   this.cartCount = response.data.cartCount || 0;
   ```

4. **Components theo dõi thay đổi State**:
   ```javascript
   // Trong component Vue
   const authStore = useAuthStore();
   const isLoggedIn = computed(() => authStore.isAuthenticated);
   ```

#### 6.3.4. Lợi ích của việc kết hợp Axios và Pinia

1. **Separation of Concerns (Phân tách trách nhiệm)**:

   - Components chỉ tập trung vào việc hiển thị UI
   - Store quản lý trạng thái và logic nghiệp vụ
   - Axios xử lý giao tiếp HTTP với backend

2. **Tái sử dụng Code**:

   - API calls có thể được tái sử dụng ở nhiều components
   - Không cần import Axios ở mỗi component cần gọi API

3. **State Management Hiệu quả**:

   - Trạng thái được quản lý tập trung
   - Dễ dàng debug và theo dõi thay đổi
   - Tránh được "prop drilling" (truyền props qua nhiều components)

4. **Bảo mật và Xử lý lỗi tốt hơn**:
   - Xử lý thống nhất cho các lỗi xác thực
   - Các interceptors giúp xử lý hết hạn phiên làm việc một cách nhất quán

#### 6.3.5. Kết quả của việc kết hợp Axios và Pinia

1. **Quản lý trạng thái tập trung**: Tất cả thông tin người dùng và giỏ hàng được lưu tại một nơi duy nhất (Auth Store)

2. **Tái sử dụng logic API**: Việc gọi API được đóng gói trong các actions của store, giúp tái sử dụng và bảo trì dễ dàng

3. **Reactive UI**: Components tự động cập nhật khi state thay đổi:

   - Header hiển thị số lượng giỏ hàng dựa trên `authStore.cartCount`
   - Menu navigation hiển thị tùy theo `authStore.isAdmin`
   - Các trang yêu cầu đăng nhập sẽ kiểm tra `authStore.isAuthenticated`

4. **Persistence**: Dữ liệu quan trọng (user, cartCount) được lưu vào localStorage để duy trì trạng thái giữa các phiên làm việc

5. **Xử lý lỗi thống nhất**: Các lỗi từ API được xử lý tập trung trong store actions

## 7. Luồng Dữ liệu và Xử lý

### 7.1. Quá trình Đăng nhập/Đăng ký

1. **Đăng ký**:

   - Người dùng nhập thông tin vào form đăng ký
   - Frontend gửi request đến `/api/auth/register`
   - Backend kiểm tra và lưu thông tin người dùng với trạng thái chưa kích hoạt
   - Gửi email kích hoạt tài khoản
   - Người dùng nhấp vào liên kết trong email để kích hoạt tài khoản

2. **Đăng nhập**:
   - Người dùng nhập email và mật khẩu
   - Frontend gửi request đến `/api/auth/login`
   - Backend xác thực và tạo session
   - Phản hồi với thông tin người dùng và giỏ hàng
   - Frontend lưu thông tin vào localStorage và Pinia store

### 7.2. Quá trình Duyệt và Mua hàng

1. **Trang chủ**:

   - Frontend gọi API `/api/home/homepage` để lấy dữ liệu trang chủ
   - Hiển thị sản phẩm mới, bán chạy, giảm giá và flash sale

2. **Xem chi tiết sản phẩm**:

   - Người dùng nhấp vào một sản phẩm
   - Frontend chuyển hướng đến `/product/:id`
   - Gọi API `/api/products/detail/:id`
   - Hiển thị thông tin chi tiết và sản phẩm liên quan

3. **Thêm vào giỏ hàng**:

   - Người dùng nhấp "Thêm vào giỏ hàng"
   - Kiểm tra đăng nhập, nếu chưa đăng nhập, chuyển đến trang đăng nhập
   - Gửi request đến `/api/cart/add`
   - Cập nhật số lượng giỏ hàng trong store

4. **Thanh toán**:
   - Người dùng xem giỏ hàng và nhấp "Thanh toán"
   - Chuyển đến trang thanh toán
   - Người dùng nhập thông tin giao hàng
   - Gửi request đến `/api/cart/checkout`
   - Hiển thị trang xác nhận đơn hàng

### 7.3. Quản lý Admin

1. **Quản lý Sản phẩm**:

   - Admin truy cập `/admin/products`
   - Xem danh sách sản phẩm từ API `/api/products`
   - Thêm/Sửa/Xóa sản phẩm qua các API tương ứng

2. **Quản lý Người dùng**:

   - Admin truy cập `/admin/users`
   - Thực hiện các thao tác quản lý người dùng

3. **Quản lý Danh mục**:

   - Admin truy cập `/admin/categories`
   - Thực hiện các thao tác quản lý danh mục

4. **Quản lý Khuyến mãi**:
   - Admin truy cập `/admin/discounts`
   - Tạo và quản lý các chương trình khuyến mãi

## 8. Bảo mật

### 8.1. Xác thực và Phân quyền

- **Session-based Authentication**: Sử dụng session để lưu trạng thái đăng nhập
- **Role-based Access Control**: Phân biệt quyền Admin và User
- **Navigation Guards**: Kiểm tra quyền truy cập route ở frontend
- **API Endpoint Protection**: Kiểm tra xác thực ở backend cho các API bảo mật

### 8.2. Mã hóa

- **Password Encoding**: Mã hóa mật khẩu bằng Base64 (nên cân nhắc nâng cấp lên thuật toán mạnh hơn)
- **HTTPS**: Nên cấu hình TLS/SSL cho môi trường sản xuất

### 8.3. CORS

- Cấu hình CORS để chỉ cho phép frontend từ domain được chỉ định truy cập API

## 9. Tính năng Quản lý Ảnh và Hiển thị Sản phẩm

### 9.1. Quản lý Đa Ảnh với Vue Draggable

TimeLux Watch sử dụng thư viện **vuedraggable** để triển khai chức năng tải lên và quản lý nhiều ảnh cho mỗi sản phẩm trong trang quản trị.

#### 9.1.1. Cấu trúc và Luồng Hoạt động

1. **Cài đặt và Import Thư viện**:

   ```javascript
   import draggable from "vuedraggable";
   ```

2. **Quản lý State Ảnh**:

   - `imagePreviews`: Mảng reactive chứa các đối tượng preview (hiển thị trong UI)
   - `formData.imageFiles`: Mảng chứa các đối tượng File cho việc upload
   - `formData.existingImage`: String lưu trữ thông tin ảnh hiện có khi chỉnh sửa

3. **Luồng Upload Ảnh**:

   - Người dùng chọn các file ảnh thông qua input file
   - Method `handleImageChange` xử lý các file được chọn:
     - Tạo URL.createObjectURL cho mỗi file để hiển thị preview
     - Lưu trữ các file trong formData.imageFiles
     - Cập nhật mảng imagePreviews với các URL preview

4. **Kéo-thả để Sắp xếp**:

   - Component `<draggable>` bao quanh các preview ảnh
   - Người dùng có thể kéo-thả để thay đổi thứ tự các ảnh
   - Method `onDragEnd` cập nhật thứ tự của `formData.imageFiles` để khớp với thứ tự mới sau khi kéo-thả
   - Ảnh đầu tiên trong danh sách được đánh dấu là "Ảnh chính" và được hiển thị nổi bật

5. **Lưu Nhiều Ảnh**:
   - Khi submit form, tất cả các ảnh được gửi đến server qua FormData API
   - Mỗi file được append vào FormData với cùng tên key "imageFiles"
   ```javascript
   formData.imageFiles.forEach((file) => {
     payload.append("imageFiles", file);
   });
   ```
   - Backend lưu trữ các ảnh và trả về chuỗi đường dẫn các ảnh được lưu, phân tách bằng dấu chấm phẩy

#### 9.1.2. Tính năng Chính

- **Tải lên nhiều ảnh**: Người dùng có thể chọn nhiều ảnh cùng lúc hoặc thêm ảnh mới vào danh sách hiện có
- **Kéo-thả sắp xếp**: Dễ dàng thay đổi thứ tự ảnh bằng cách kéo-thả
- **Xem trước ảnh**: Hiển thị thumbnail cho mỗi ảnh đã chọn
- **Xóa ảnh**: Xóa từng ảnh riêng lẻ từ danh sách
- **Ảnh chính**: Ảnh đầu tiên trong danh sách được đánh dấu là ảnh chính của sản phẩm
- **Xử lý ảnh đã tồn tại**: Khi chỉnh sửa sản phẩm, các ảnh hiện có được hiển thị và có thể thay thế bằng ảnh mới

### 9.2. Hiển thị Ảnh Sản phẩm với Swiper

TimeLux Watch sử dụng thư viện **Swiper** để tạo gallery ảnh tương tác trong trang chi tiết sản phẩm.

#### 9.2.1. Cấu trúc và Luồng Hoạt động

1. **Cài đặt và Import Thư viện**:

   ```javascript
   import { Swiper, SwiperSlide } from "swiper/vue";
   import { Navigation, Pagination } from "swiper/modules";
   import "swiper/css";
   import "swiper/css/navigation";
   import "swiper/css/pagination";
   ```

2. **Xử lý Dữ liệu Ảnh**:

   - `productImages`: Computed property xử lý chuỗi ảnh từ backend (phân tách bằng dấu chấm phẩy) thành mảng URL ảnh
   - `currentImageIndex`: Ref track index của ảnh chính hiện tại
   - `currentMainImage`: Computed property trả về URL của ảnh chính hiện tại

3. **Cấu trúc Giao diện Gallery**:

   - **Ảnh Chính**: Hiển thị ảnh hiện được chọn với kích thước lớn ở trên cùng
   - **Thumbnail Slider**: Hiển thị các ảnh thumbnail dưới ảnh chính, sử dụng Swiper

   ```html
   <swiper
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
       :class="{ active: currentImageIndex === index }"
       @click="setMainImage(index)"
     >
       <!-- Thumbnail image -->
     </swiper-slide>
   </swiper>
   ```

4. **Tương tác Người dùng**:
   - Người dùng nhấp vào thumbnail để thay đổi ảnh chính (thông qua method `setMainImage`)
   - Sử dụng navigation buttons của Swiper để cuộn qua các thumbnails khi có nhiều ảnh
   - Xử lý lỗi tải ảnh với method `setDefaultImage` để hiển thị ảnh placeholder thay thế

#### 9.2.2. Tính năng Chính của Gallery Ảnh

- **Hiển thị nhiều ảnh**: Hỗ trợ hiển thị nhiều ảnh cho một sản phẩm
- **Thumbnail navigation**: Dễ dàng chuyển đổi giữa các ảnh qua thumbnails
- **Responsive**: Gallery tự điều chỉnh để hiển thị tốt trên các thiết bị khác nhau
- **Điều hướng mượt mà**: Nút điều hướng và pagination giúp người dùng dễ dàng duyệt qua ảnh
- **Xử lý lỗi**: Hiển thị ảnh placeholder khi ảnh không tải được
- **Badge giảm giá**: Hiển thị badge phần trăm giảm giá trên ảnh chính khi sản phẩm có khuyến mãi

### 9.3. Tích hợp Giữa Upload và Hiển thị

1. **Lưu trữ nhất quán**:

   - Backend lưu ảnh dưới dạng chuỗi đường dẫn phân tách bằng dấu chấm phẩy
   - Ví dụ: "image1.jpg;image2.jpg;image3.jpg"

2. **Xử lý chuỗi ảnh**:

   - Trang quản lý phân tích chuỗi ảnh để hiển thị các ảnh hiện có khi chỉnh sửa
   - Trang chi tiết sản phẩm phân tích cùng định dạng chuỗi để hiển thị gallery
   - Cả hai trang đều xử lý một cách nhất quán các đường dẫn tương đối và URLs đầy đủ

   #### Cơ chế lưu trữ và xử lý chuỗi ảnh

   **a. Định dạng chuỗi ảnh:**

   - Hệ thống lưu trữ nhiều ảnh sản phẩm trong một trường `image` dạng chuỗi text
   - Các tên file được phân tách bằng dấu chấm phẩy (`;`). Ví dụ: `"watch1.jpg;watch2.jpg;watch3.png"`
   - Định dạng này cho phép lưu nhiều ảnh mà không cần tạo bảng quan hệ phức tạp
   - Thứ tự ảnh trong chuỗi được bảo toàn, với ảnh đầu tiên luôn được coi là ảnh chính

   **b. Xử lý trong Admin Panel (ProductAddEditModal.vue):**

   - Khi chỉnh sửa sản phẩm, chuỗi ảnh được tách thành các file riêng biệt
   - Hệ thống kiểm tra định dạng với regex cho các định dạng ảnh hỗ trợ (.jpg, .jpeg, .png, .gif, .webp)
   - Mỗi ảnh được chuyển thành URL đầy đủ để hiển thị preview
   - Người dùng có thể thêm, xóa và sắp xếp lại ảnh với vuedraggable
   - Khi lưu, thứ tự ảnh được bảo toàn và gửi đến server dưới dạng FormData multipart

   **c. Xử lý trong Frontend (ProductDetailPage.vue):**

   - Trang chi tiết sản phẩm nhận chuỗi ảnh từ API response
   - Chuỗi được phân tích với cùng logic, tách thành mảng URL đầy đủ
   - Swiper sử dụng các URL này để tạo gallery ảnh có thể điều hướng
   - Ảnh đầu tiên trong chuỗi tự động được hiển thị là ảnh chính

   **d. Xử lý lỗi và tương thích:**

   - Validation client-side và server-side cho định dạng file hợp lệ
   - Hệ thống dự phòng với ảnh placeholder khi không tìm thấy hoặc lỗi tải ảnh
   - Xử lý đặc biệt cho trường hợp ảnh đơn (không có dấu `;`)
   - Try/catch blocks để đảm bảo ứng dụng không bị crash khi xử lý chuỗi không hợp lệ

   **e. API Endpoints xử lý ảnh:**

   - `/crud/products/save-with-multiple-images`: Xử lý upload nhiều ảnh
   - Backend tự động tạo chuỗi từ danh sách file được gửi lên
   - Trong trường hợp chỉnh sửa, kết hợp với chuỗi `existingImage` nếu người dùng giữ lại ảnh cũ

Việc kết hợp vuedraggable cho upload và Swiper cho hiển thị tạo ra một trải nghiệm người dùng toàn diện - quản trị viên có thể dễ dàng quản lý nhiều ảnh và sắp xếp thứ tự hiển thị, trong khi khách hàng được trải nghiệm xem sản phẩm với gallery ảnh trực quan và tương tác.

## 10. Câu hỏi phỏng vấn Frontend cho vị trí Intern

Dưới đây là các câu hỏi phỏng vấn có thể xảy ra khi ứng tuyển vị trí Intern Frontend liên quan đến dự án TimeLux Watch. Các câu hỏi được chia theo chủ đề và đi kèm với gợi ý câu trả lời.

### 10.1. Vue.js cơ bản

**Q1: Vue.js 3 khác gì so với Vue.js 2?**

> **Trả lời:** Vue.js 3 có nhiều cải tiến so với Vue.js 2, bao gồm:
>
> - Composition API: Cung cấp cách tiếp cận mới để tổ chức logic, thay thế cho Options API
> - Hiệu suất tốt hơn: Kích thước nhỏ hơn, Virtual DOM được viết lại, tree-shaking tốt hơn
> - TypeScript support tốt hơn
> - Teleport: Component mới cho phép render content ở bất kỳ vị trí nào trong DOM
> - Fragment: Cho phép component có nhiều root element
> - Reactivity system mới dựa trên Proxy, không còn giới hạn với Object.defineProperty

**Q2: Giải thích về Composition API trong Vue.js 3 và tại sao nó lại tốt hơn Options API?**

> **Trả lời:** Composition API là một tập hợp các API chức năng cho phép tổ chức code theo logic thay vì theo tùy chọn (data, methods, computed...). Nó có nhiều ưu điểm:
>
> - Tổ chức code tốt hơn theo tính năng thay vì theo tùy chọn
> - Tái sử dụng logic tốt hơn thông qua composables
> - TypeScript type inference tốt hơn
> - Bundle size nhỏ hơn nhờ tree-shaking
>
> Trong TimeLux Watch, chúng ta sử dụng Composition API với cú pháp `<script setup>` giúp code ngắn gọn và dễ đọc hơn.

**Q3: Trong project này, các component được tổ chức như thế nào?**

> **Trả lời:** Project TimeLux Watch tổ chức components thành:
>
> - Components (src/components): Chứa các UI components tái sử dụng như HeaDer, FooTer, LoginRegister
> - Views (src/views): Chứa các page components tương ứng với routes như HomePage, ProductDetailPage
>
> Cách tổ chức này giúp phân tách rõ ràng giữa các components có thể tái sử dụng và các trang cụ thể.

### 10.2. Quản lý trạng thái với Pinia

**Q4: Pinia là gì và tại sao dự án sử dụng Pinia thay vì Vuex?**

> **Trả lời:** Pinia là thư viện quản lý trạng thái mới cho Vue, được coi là phiên bản tiếp theo của Vuex. Lợi ích của Pinia bao gồm:
>
> - API đơn giản hơn Vuex, không cần mutations, chỉ có state, getters, actions
> - TypeScript support tốt hơn với type inference
> - Devtools support để debug
> - Tối ưu hóa kích thước bundle với automatic code-splitting
> - Hỗ trợ nhiều stores, không cần modules lồng nhau như Vuex
>
> Trong dự án TimeLux Watch, Pinia được sử dụng để quản lý trạng thái xác thực người dùng (AuthStore) và thông báo (NotificationStore).

**Q5: Giải thích cấu trúc của một Pinia store trong dự án này.**

> **Trả lời:** Trong TimeLux Watch, một Pinia store (ví dụ như AuthStore) có cấu trúc:
>
> - **state**: Định nghĩa trạng thái, ví dụ: `user`, `cartCount`, `loading`, `error`
> - **getters**: Các computed properties từ state, ví dụ: `isAuthenticated`, `userName`, `isAdmin`
> - **actions**: Các methods để thay đổi state và thực hiện các tác vụ bất đồng bộ, ví dụ: `login()`, `logout()`, `updateCartCount()`
>
> Ví dụ: `useAuthStore` quản lý trạng thái người dùng và giỏ hàng, cung cấp các actions để đăng nhập/đăng xuất và getters để kiểm tra người dùng đã đăng nhập hay chưa.

**Q6: Làm thế nào để một component Vue sử dụng Pinia store?**

> **Trả lời:** Để sử dụng Pinia store trong component Vue:
>
> ```javascript
> <script setup>
> import { useAuthStore } from '@/stores/auth';
> import { storeToRefs } from 'pinia';
>
> // Lấy store instance
> const authStore = useAuthStore();
>
> // Sử dụng storeToRefs để giữ tính reactive khi destructuring
> const { user, cartCount } = storeToRefs(authStore);
>
> // Gọi actions trực tiếp
> function handleLogin() {
>   authStore.login(username.value, password.value);
> }
> </script>
> ```
>
> Lưu ý việc sử dụng `storeToRefs` khi destructuring các properties từ store để giữ tính reactive.

### 10.3. Routing với Vue Router

**Q7: Giải thích cách Vue Router được cấu hình trong dự án này.**

> **Trả lời:** Trong TimeLux Watch, Vue Router được cấu hình trong `main.js`:
>
> - Định nghĩa routes với path, component và metadata (meta)
> - Thiết lập navigation guards để kiểm tra quyền truy cập route
> - Sử dụng `createWebHistory()` để tạo history mode thay vì hash mode
>
> Metadata `meta: { requiresAuth: true }` được sử dụng để đánh dấu các routes yêu cầu đăng nhập, và `meta: { requiresAdmin: true }` cho các routes yêu cầu quyền admin.

**Q8: Navigation Guards là gì và chúng được sử dụng cho mục đích gì trong dự án này?**

> **Trả lời:** Navigation Guards là các hooks của Vue Router cho phép kiểm soát điều hướng. Trong TimeLux Watch, `router.beforeEach` được sử dụng để:
>
> - Kiểm tra nếu route đang truy cập yêu cầu đăng nhập
> - Kiểm tra đăng nhập từ localStorage trước
> - Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
> - Kiểm tra quyền admin cho các routes yêu cầu quyền admin
>
> Ví dụ, khi người dùng cố gắng truy cập `/profile` mà chưa đăng nhập, họ sẽ được chuyển hướng đến trang đăng nhập.

**Q9: Làm thế nào để truyền tham số qua routes trong Vue Router?**

> **Trả lời:** Vue Router cung cấp nhiều cách để truyền tham số:
>
> 1. **Dynamic Segments (Path Parameters)**:
>
>    ```javascript
>    { path: '/product/:id', name: 'ProductDetail', component: ProductDetailPage }
>    ```
>
>    Truy cập bằng: `this.$route.params.id` hoặc `useRoute().params.id`
>
> 2. **Query Parameters**:
>
>    ```javascript
>    router.push({ path: "/search", query: { q: "đồng hồ" } });
>    ```
>
>    Truy cập bằng: `this.$route.query.q` hoặc `useRoute().query.q`
>
> 3. **Props Mode**:
>    ```javascript
>    { path: '/product/:id', component: ProductDetailPage, props: true }
>    ```
>    Truy cập bằng prop `id` trong component
>
> TimeLux Watch sử dụng cả ba cách, tùy theo trường hợp cụ thể.

### 10.4. HTTP Requests với Axios

**Q10: Tại sao dự án sử dụng Axios thay vì Fetch API?**

> **Trả lời:** Axios được chọn thay vì Fetch API vì những lợi ích:
>
> - API nhất quán trên các trình duyệt
> - Tự động parse JSON response
> - Hỗ trợ interceptors để xử lý request/response
> - Hủy requests dễ dàng hơn với cancel tokens
> - Tự động xử lý XSRF protection
> - Hỗ trợ timeout requests
> - Xử lý lỗi tốt hơn
>
> Đặc biệt trong TimeLux Watch, interceptors của Axios đóng vai trò quan trọng trong việc xử lý các lỗi 401 (Unauthorized) và các request đặc biệt như FormData.

**Q11: Giải thích cách Axios được cấu hình và sử dụng trong dự án.**

> **Trả lời:** Axios được cấu hình trong file `services/api.js`:
>
> - Tạo instance với baseURL, headers mặc định và withCredentials
> - Thiết lập request interceptors để xử lý FormData
> - Thiết lập response interceptors để xử lý lỗi 401
>
> Instance này được export và sử dụng trong các Pinia stores, đặc biệt là AuthStore, để thực hiện các HTTP requests đến backend API.

**Q12: Interceptors trong Axios là gì và chúng được sử dụng để làm gì trong dự án?**

> **Trả lời:** Interceptors là các hàm được thực thi trước khi request được gửi đi hoặc sau khi response được nhận về:
>
> - **Request Interceptors**: Trong TimeLux Watch, chúng được sử dụng để xử lý đặc biệt cho FormData requests (xóa header Content-Type để Axios tự thiết lập boundary)
> - **Response Interceptors**: Xử lý lỗi 401 (Unauthorized) - khi token hết hạn, người dùng sẽ được đăng xuất và chuyển hướng về trang đăng nhập
>
> Interceptors giúp xử lý logic chung cho tất cả các requests/responses mà không cần lặp lại code ở mỗi nơi gọi API.

### 10.5. Quản lý dữ liệu và reactive

**Q13: Trong Vue 3, có những cách nào để tạo reactive data?**

> **Trả lời:** Vue 3 cung cấp nhiều API để tạo reactive data:
>
> - **ref()**: Dùng cho primitive values (string, number, boolean)
> - **reactive()**: Dùng cho objects
> - **computed()**: Tạo giá trị reactive được tính toán từ các reactive data khác
> - **readonly()**: Tạo proxy chỉ đọc cho reactive object
>
> Trong TimeLux Watch, chúng ta sử dụng `ref` và `computed` trong các components, trong khi Pinia stores sử dụng state objects (tương tự như `reactive`).

**Q14: Giải thích cách dữ liệu được đồng bộ giữa Pinia store và localStorage?**

> **Trả lời:** Trong TimeLux Watch, dữ liệu giữa Pinia store và localStorage được đồng bộ theo cách:
>
> 1. **Khởi tạo từ localStorage**: Khi ứng dụng khởi động, AuthStore khởi tạo state từ localStorage nếu có
>
>    ```javascript
>    state: () => ({
>      user: localStorage.getItem("user")
>        ? JSON.parse(localStorage.getItem("user"))
>        : null,
>      cartCount: localStorage.getItem("cartCount")
>        ? parseInt(localStorage.getItem("cartCount"))
>        : 0,
>    });
>    ```
>
> 2. **Lưu vào localStorage khi state thay đổi**: Trong actions như login, logout, updateCartCount
>    ```javascript
>    localStorage.setItem("user", JSON.stringify(this.user));
>    localStorage.setItem("cartCount", this.cartCount.toString());
>    ```
>
> Cơ chế này giúp duy trì trạng thái người dùng giữa các phiên làm việc, ngay cả khi refresh trang.

**Q15: Computed properties trong Vue.js là gì và khi nào nên sử dụng chúng?**

> **Trả lời:** Computed properties là giá trị reactive được tính toán dựa trên reactive data khác:
>
> ```javascript
> const fullName = computed(() => firstName.value + " " + lastName.value);
> ```
>
> Nên sử dụng computed properties khi:
>
> - Cần tính toán giá trị dựa trên dữ liệu khác
> - Giá trị được tính toán được sử dụng ở nhiều nơi
> - Có logic phức tạp cần cache (computed properties được cache dựa trên dependencies)
>
> Trong TimeLux Watch, computed properties được sử dụng trong components và cũng trong Pinia stores (getters).

### 10.6. Components và Component Communication

**Q16: Các cách để components giao tiếp với nhau trong Vue.js?**

> **Trả lời:** Có nhiều cách để components giao tiếp trong Vue.js:
>
> 1. **Props**: Truyền dữ liệu từ cha xuống con
> 2. **Events**: Con emit events cho cha
> 3. **v-model**: Two-way binding giữa cha và con
> 4. **Provide/Inject**: Truyền dữ liệu xuống các component con sâu trong cây
> 5. **Pinia Store**: Quản lý shared state giữa các components không liên quan
> 6. **Refs**: Truy cập trực tiếp đến component con
>
> TimeLux Watch sử dụng kết hợp các cách này, nhưng chủ yếu dựa vào Pinia stores (AuthStore, NotificationStore) cho global state và props/events cho local state.

**Q17: Giải thích cách sử dụng props trong Vue components.**

> **Trả lời:** Props là cách truyền dữ liệu từ component cha xuống component con:
>
> **Trong component cha:**
>
> ```html
> <ProductCard :product="product" :showDiscount="true" />
> ```
>
> **Trong component con (Options API):**
>
> ```javascript
> export default {
>   props: {
>     product: { type: Object, required: true },
>     showDiscount: { type: Boolean, default: false },
>   },
> };
> ```
>
> **Trong component con (Composition API với script setup):**
>
> ```javascript
> const props = defineProps({
>   product: { type: Object, required: true },
>   showDiscount: { type: Boolean, default: false },
> });
> ```
>
> Trong TimeLux Watch, props được sử dụng để truyền dữ liệu cụ thể cho mỗi component, ví dụ như truyền product data cho ProductCard component.

**Q18: Cách nào để handle form inputs trong Vue.js?**

> **Trả lời:** Vue.js cung cấp nhiều cách để xử lý form inputs:
>
> 1. **v-model**: Two-way binding
>
>    ```html
>    <input v-model="username" />
>    ```
>
> 2. **v-model với modifiers**: Xử lý input theo cách đặc biệt
>
>    ```html
>    <input v-model.trim="username" />
>    <input v-model.number="age" />
>    <input v-model.lazy="bio" />
>    ```
>
> 3. **Custom component với v-model**:
>
>    ```html
>    <custom-input v-model="searchText" />
>    ```
>
> 4. **Form validation**: Thường kết hợp với thư viện như vee-validate
>
> Trong TimeLux Watch, forms được xử lý chủ yếu với v-model kết hợp với các hàm validation tùy chỉnh.

### 10.7. Performance Optimization

**Q19: Làm thế nào để tối ưu hiệu suất trong ứng dụng Vue.js?**

> **Trả lời:** Một số cách tối ưu hiệu suất trong ứng dụng Vue:
>
> 1. **Lazy loading components và routes**:
>
>    ```javascript
>    const AdminDashboard = () => import("@/views/AdminDashboard.vue");
>    ```
>
> 2. **Tối ưu hình ảnh**:
>
>    - Sử dụng WebP format
>    - Implement lazy loading cho images
>    - Sử dụng srcset cho responsive images
>
> 3. **Code splitting**:
>
>    - Tách vendor chunks
>    - Dynamic imports
>
> 4. **Caching**:
>
>    - Implement API response caching
>    - Use Service Workers
>
> 5. **Prefetching**:
>
>    ```html
>    <link rel="prefetch" href="/js/ProductDetail.js" />
>    ```
>
> 6. **Optimize rendering**:
>
>    - Sử dụng `v-show` thay vì `v-if` khi thích hợp
>    - Tránh computed properties phức tạp
>    - Sử dụng `v-once` cho static content
>
> 7. **Bundle analysis**:
>
>    - Sử dụng bundle analyzer để tìm và giảm kích thước bundle
>
> Những cải tiến này có thể giúp TimeLux Watch tải nhanh hơn và cung cấp trải nghiệm người dùng mượt mà hơn.

**Q20: Giải thích cách Vue.js sử dụng Virtual DOM để cải thiện hiệu suất.**

> **Trả lời:** Vue.js sử dụng Virtual DOM để tối ưu hóa việc cập nhật DOM thực:
>
> 1. Vue tạo bản sao nhẹ của DOM thực trong bộ nhớ (Virtual DOM)
> 2. Khi state thay đổi, Vue tạo Virtual DOM mới
> 3. Vue so sánh (diff) Virtual DOM mới với cũ
> 4. Chỉ cập nhật những phần thực sự thay đổi trong DOM thực
>
> Quá trình này giảm thiểu DOM manipulations, vốn tốn kém về mặt hiệu suất. Vue.js 3 có thuật toán diff được cải tiến so với Vue 2, dẫn đến hiệu suất tốt hơn.

### 10.8. Authentication và Security

**Q21: TimeLux Watch xử lý authentication như thế nào?**

> **Trả lời:** TimeLux Watch sử dụng session-based authentication:
>
> 1. **Đăng nhập**: User gửi credentials đến backend, backend tạo session và trả về session cookie
> 2. **Lưu trữ**: Frontend lưu user data trong Pinia store và localStorage
> 3. **Kiểm tra**: AuthStore có methods để kiểm tra đăng nhập (isLoggedIn, checkAuthStatus)
> 4. **Routes bảo vệ**: Navigation guards kiểm tra requiresAuth và requiresAdmin
> 5. **Xử lý hết hạn**: Axios interceptor xử lý lỗi 401, logout và redirect về login page
>
> Cơ chế này đảm bảo chỉ users đã xác thực mới có thể truy cập các routes được bảo vệ như profile, cart, checkout.

**Q22: Làm thế nào để bảo vệ một route chỉ cho phép người dùng đã đăng nhập truy cập?**

> **Trả lời:** Để bảo vệ routes chỉ cho authenticated users:
>
> 1. **Đánh dấu route với meta**:
>
>    ```javascript
>    {
>      path: '/profile',
>      name: 'Profile',
>      component: ProfilePage,
>      meta: { requiresAuth: true }
>    }
>    ```
>
> 2. **Sử dụng navigation guard**:
>
>    ```javascript
>    router.beforeEach(async (to, from, next) => {
>      const authStore = useAuthStore();
>
>      if (to.matched.some((record) => record.meta.requiresAuth)) {
>        if (authStore.isLoggedIn()) {
>          next(); // Cho phép truy cập
>        } else {
>          next({ name: "Login", query: { redirect: to.fullPath } });
>        }
>      } else {
>        next(); // Không yêu cầu auth, cho phép truy cập
>      }
>    });
>    ```
>
> TimeLux Watch kết hợp cả hai cách này để bảo vệ các routes như profile, cart, checkout.

### 10.9. Error Handling

**Q23: Làm thế nào để xử lý lỗi trong các API calls?**

> **Trả lời:** Trong TimeLux Watch, lỗi API được xử lý theo nhiều lớp:
>
> 1. **Trong Pinia Store Actions**: Sử dụng try/catch
>
>    ```javascript
>    async login(username, password) {
>      try {
>        const response = await apiClient.post('/auth/login', formData);
>        // Handle success
>        return { success: true, message: response.data.message };
>      } catch (error) {
>        this.error = error.response?.data?.error || error.message;
>        return { success: false, message: this.error };
>      }
>    }
>    ```
>
> 2. **Axios Interceptors**: Xử lý lỗi 401 và các lỗi chung
>
>    ```javascript
>    apiClient.interceptors.response.use(
>      (response) => response,
>      (error) => {
>        if (error.response && error.response.status === 401) {
>          // Handle unauthorized error
>        }
>        return Promise.reject(error);
>      }
>    );
>    ```
>
> 3. **NotificationStore**: Hiển thị thông báo lỗi cho người dùng
>    ```javascript
>    notificationStore.addNotification({
>      message: error.message,
>      type: "danger",
>    });
>    ```
>
> Cách tiếp cận nhiều lớp này đảm bảo lỗi được xử lý và hiển thị phù hợp cho người dùng.

**Q24: Làm thế nào để debug một ứng dụng Vue.js?**

> **Trả lời:** Có nhiều cách để debug ứng dụng Vue.js:
>
> 1. **Vue DevTools**: Extension cho Chrome/Firefox cho phép inspect components, routes, store
>
> 2. **Browser DevTools**:
>
>    - Console.log
>    - Breakpoints trong Source panel
>    - Network tab để xem API requests
>
> 3. **Khi làm việc với Pinia**:
>
>    - DevTools tích hợp với Pinia
>    - State inspector
>    - Time-travel debugging
>
> 4. **Performance monitoring**:
>    - Vue DevTools Performance tab
>    - Lighthouse audits
>
> Trong TimeLux Watch, sử dụng kết hợp Vue DevTools và Browser DevTools là cách hiệu quả nhất để debug issues.

### 10.10. Responsive Design

**Q25: Làm thế nào mà TimeLux Watch đảm bảo giao diện responsive?**

> **Trả lời:** TimeLux Watch sử dụng nhiều kỹ thuật để đảm bảo responsive:
>
> 1. **Bootstrap Grid System**: Sử dụng classes như col-md-6, col-lg-4
>
>    ```html
>    <div class="row">
>      <div class="col-12 col-md-6 col-lg-4">
>        <!-- Product card -->
>      </div>
>    </div>
>    ```
>
> 2. **Media Queries**: CSS dành riêng cho các breakpoints khác nhau
>
> 3. **Responsive Images**: Đảm bảo hình ảnh sản phẩm hiển thị tốt trên mọi thiết bị
>
>    ```html
>    <img class="img-fluid" :src="product.image" alt="product.name" />
>    ```
>
> 4. **Responsive Navigation**: Sử dụng Bootstrap navbar-collapse cho menu di động
>
> 5. **Flexbox**: Căn chỉnh elements linh hoạt
>
> Cách tiếp cận này đảm bảo TimeLux Watch hoạt động tốt trên desktop, tablet và mobile.

**Q26: Khi nào nên sử dụng CSS Flexbox và khi nào nên sử dụng CSS Grid?**

> **Trả lời:**
>
> **Sử dụng Flexbox khi**:
>
> - Layout một chiều (row hoặc column)
> - Cần alignment linh hoạt (center, space-between, etc.)
> - Cần order khác nhau trên các màn hình khác nhau
> - Làm việc với elements có kích thước không xác định
>
> **Sử dụng Grid khi**:
>
> - Layout hai chiều (rows và columns cùng lúc)
> - Cần gaps đồng nhất giữa các elements
> - Cần overlapping elements
> - Cần định vị chính xác các elements
>
> TimeLux Watch sử dụng kết hợp cả hai: Flexbox cho component layouts và Grid (thông qua Bootstrap grid) cho page layouts.

### 10.11. Build & Deploy

**Q27: Vite là gì và tại sao dự án sử dụng Vite thay vì Webpack?**

> **Trả lời:** Vite là build tool hiện đại cho frontend với nhiều ưu điểm so với Webpack:
>
> 1. **Dev server nhanh hơn**: Vite sử dụng native ES modules, không cần bundle trong development
> 2. **Hot Module Replacement (HMR) nhanh**: Cập nhật tức thì các modules đã thay đổi
> 3. **Build production tối ưu**: Sử dụng Rollup để tạo bundles tối ưu
> 4. **Cấu hình đơn giản**: API dễ sử dụng, ít boilerplate
> 5. **Tích hợp TypeScript**: Hỗ trợ tốt với TypeScript mà không cần cấu hình phức tạp
>
> TimeLux Watch sử dụng Vite vì những lợi ích này, đặc biệt là tốc độ dev server nhanh giúp tăng năng suất phát triển.

**Q28: Giải thích quá trình build và deploy một ứng dụng Vue.js.**

> **Trả lời:** Quá trình build và deploy ứng dụng Vue.js bao gồm:
>
> 1. **Build for Production**:
>
>    ```bash
>    npm run build
>    ```
>
>    Lệnh này tạo ra phiên bản tối ưu hóa của ứng dụng trong thư mục `dist`
>
> 2. **Optimize Assets**:
>
>    - Minify JavaScript, CSS
>    - Tối ưu hóa hình ảnh
>    - Code splitting
>    - Tree shaking
>
> 3. **Deploy Options**:
>
>    - Static hosting (Netlify, Vercel, GitHub Pages)
>    - Server hosting (VPS, shared hosting)
>    - Containerization (Docker)
>
> 4. **Configuration**:
>
>    - Environment variables
>    - API endpoints
>    - CORS settings
>
> 5. **Testing after deploy**:
>    - Smoke tests
>    - End-to-end tests
>
> Đối với TimeLux Watch, quá trình build tạo ra các static files có thể được host trên bất kỳ web server nào.

### 10.12. Câu hỏi thực hành

**Q29: Làm thế nào để thêm một trang chi tiết sản phẩm mới vào dự án?**

> **Trả lời:** Để thêm trang chi tiết sản phẩm mới:
>
> 1. **Tạo component Vue mới**:
>
>    ```javascript
>    // src/views/EnhancedProductDetailPage.vue
>    <script setup>
>    import { ref, onMounted } from 'vue';
>    import { useRoute } from 'vue-router';
>    import apiClient from '@/services/api';
>
>    const route = useRoute();
>    const product = ref(null);
>    const loading = ref(true);
>    const error = ref(null);
>
>    onMounted(async () => {
>      try {
>        const response = await apiClient.get(`/products/detail/${route.params.id}`);
>        product.value = response.data.product;
>      } catch (err) {
>        error.value = err.message;
>      } finally {
>        loading.value = false;
>      }
>    });
>    </script>
>
>    <template>
>      <div v-if="loading">Loading...</div>
>      <div v-else-if="error">Error: {{ error }}</div>
>      <div v-else-if="product" class="product-detail">
>        <!-- Product detail UI -->
>      </div>
>    </template>
>    ```
>
> 2. **Thêm route mới**:
>
>    ```javascript
>    // In main.js or router.js
>    const routes = [
>      // Existing routes
>      {
>        path: "/enhanced-product/:id",
>        name: "EnhancedProductDetail",
>        component: EnhancedProductDetailPage,
>        props: true,
>      },
>    ];
>    ```
>
> 3. **Thêm link đến trang mới**:
>
>    ```html
>    <router-link
>      :to="{ name: 'EnhancedProductDetail', params: { id: product.id }}"
>    >
>      View Enhanced Details
>    </router-link>
>    ```
>
> 4. **Thêm functionality đặc biệt** như image gallery, reviews, related products, etc.

**Q30: Bạn sẽ làm thế nào để cải thiện performance của dự án TimeLux Watch?**

> **Trả lời:** Để cải thiện performance của TimeLux Watch:
>
> 1. **Lazy loading components và routes**:
>
>    ```javascript
>    const AdminDashboard = () => import("@/views/AdminDashboard.vue");
>    ```
>
> 2. **Tối ưu hình ảnh**:
>
>    - Sử dụng WebP format
>    - Implement lazy loading cho images
>    - Sử dụng srcset cho responsive images
>
> 3. **Code splitting**:
>
>    - Tách vendor chunks
>    - Dynamic imports
>
> 4. **Caching**:
>
>    - Implement API response caching
>    - Use Service Workers
>
> 5. **Prefetching**:
>
>    ```html
>    <link rel="prefetch" href="/js/ProductDetail.js" />
>    ```
>
> 6. **Optimize rendering**:
>
>    - Sử dụng `v-show` thay vì `v-if` khi thích hợp
>    - Tránh computed properties phức tạp
>    - Sử dụng `v-once` cho static content
>
> 7. **Bundle analysis**:
>    - Sử dụng bundle analyzer để tìm và giảm kích thước bundle
>
> Những cải tiến này có thể giúp TimeLux Watch tải nhanh hơn và cung cấp trải nghiệm người dùng mượt mà hơn.
