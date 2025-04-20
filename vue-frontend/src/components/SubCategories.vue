<template>
    <div class="container py-5">
      <div class="table-container">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>Quản lý Thương Hiệu</h2>
          <button class="btn btn-success" @click="openAddModal">
            <i class="fas fa-plus me-2"></i>Thêm Thương Hiệu
          </button>
        </div>
  
        <!-- Hiển thị thông báo lỗi hoặc thành công -->
        <div v-if="error" class="alert alert-danger" id="errorAlert">{{ error }}</div>
        <div v-if="success" class="alert alert-success" id="successAlert">{{ success }}</div>
  
        <!-- Form lọc theo danh mục -->
        <form @submit.prevent="filterByCategory" class="mb-4">
          <div class="d-flex align-items-center">
            <label for="categorySelect" class="me-2">Chọn Danh Mục:</label>
            <select 
              id="categorySelect" 
              v-model="selectedCategory" 
              class="form-select w-auto"
              @change="filterByCategory"
            >
              <option 
                v-for="category in categories" 
                :key="category.name" 
                :value="category.name"
              >{{ category.name }}</option>
            </select>
          </div>
        </form>
  
        <!-- Bảng danh sách thương hiệu -->
        <table class="table table-bordered table-hover">
          <thead class="table-dark">
            <tr>
              <th>STT</th>
              <th>Tên Danh Mục</th>
              <th>Tên Thương Hiệu</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="subCategory in subCategories" :key="subCategory.id">
              <td>{{ subCategory.id }}</td>
              <td>{{ subCategory.subCategoriesName }}</td>
              <td>{{ subCategory.category.name }}</td>
              <td>{{ subCategory.status === 1 ? 'Hoạt động' : 'Không hoạt động' }}</td>
              <td>
                <button 
                  class="btn btn-warning btn-sm" 
                  @click="openEditModal(subCategory)"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  class="btn btn-danger btn-sm" 
                  @click="openDeleteConfirmModal(subCategory.id)"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
  
        <!-- Phân trang -->
        <nav v-if="totalPages > 1" aria-label="Page navigation">
          <ul class="pagination justify-content-center">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">Trước</a>
            </li>
            <li 
              v-for="page in totalPages" 
              :key="page" 
              class="page-item"
              :class="{ active: page === currentPage }"
            >
              <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">Sau</a>
            </li>
          </ul>
        </nav>
      </div>
  
      <!-- Modal Thêm mới -->
      <div class="modal fade" id="addSubCategoryModal" tabindex="-1" ref="addModal">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Thêm Thương Hiệu</h5>
              <button type="button" class="btn-close" @click="closeAddModal"></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="saveSubCategory">
                <div class="mb-3">
                  <label class="form-label">Tên Danh Mục (Đồng hồ nam, Đồng hồ nữ, ...):</label>
                  <select v-model="newSubCategory.subCategoriesName" class="form-select" required>
                    <option value="Đồng hồ nam">Đồng hồ nam</option>
                    <option value="Đồng hồ nữ">Đồng hồ nữ</option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Thương Hiệu (Rolex, Casio, ...):</label>
                  <input type="text" v-model="newSubCategory.categoryName" class="form-control" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Trạng thái:</label>
                  <select v-model="newSubCategory.status" class="form-select">
                    <option :value="1">Hoạt động</option>
                    <option :value="0">Không hoạt động</option>
                  </select>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" @click="closeAddModal">Đóng</button>
              <button type="button" @click="saveSubCategory" class="btn btn-primary">Lưu</button>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Modal Chỉnh sửa -->
      <div class="modal fade" id="editSubCategoryModal" tabindex="-1" ref="editModal">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Chỉnh sửa Thương Hiệu</h5>
              <button type="button" class="btn-close" @click="closeEditModal"></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="updateSubCategory">
                <input type="hidden" v-model="editingSubCategory.id">
                <div class="mb-3">
                  <label class="form-label">Tên Danh Mục (Đồng hồ nam, Đồng hồ nữ, ...):</label>
                  <select v-model="editingSubCategory.subCategoriesName" class="form-select" required>
                    <option value="Đồng hồ nam">Đồng hồ nam</option>
                    <option value="Đồng hồ nữ">Đồng hồ nữ</option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Thương Hiệu (Rolex, Casio, ...):</label>
                  <input type="text" v-model="editingSubCategory.categoryName" class="form-control" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Trạng thái:</label>
                  <select v-model="editingSubCategory.status" class="form-select">
                    <option :value="1">Hoạt động</option>
                    <option :value="0">Không hoạt động</option>
                  </select>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" @click="closeEditModal">Đóng</button>
              <button type="button" @click="updateSubCategory" class="btn btn-primary">Cập nhật</button>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Modal Xác nhận xóa -->
      <div class="modal fade" id="confirmDeleteModal" tabindex="-1" ref="deleteModal">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Xác nhận xóa</h5>
              <button type="button" class="btn-close" @click="closeDeleteModal"></button>
            </div>
            <div class="modal-body">
              <p>Bạn có chắc chắn muốn xóa thương hiệu này không?</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" @click="closeDeleteModal">Không</button>
              <button type="button" class="btn btn-primary" @click="deleteSubCategory">Có</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { Modal } from 'bootstrap';
  
  export default {
    name: 'BrandManagement',
    data() {
      return {
        // Các biến quản lý dữ liệu
        subCategories: [],
        categories: [],
        selectedCategory: '',
        currentPage: 1,
        totalPages: 1,
        
        // Quản lý thông báo
        error: '',
        success: '',
        
        // Quản lý modal
        addModal: null,
        editModal: null,
        deleteModal: null,
        
        // Dữ liệu cho thêm mới
        newSubCategory: {
          subCategoriesName: 'Đồng hồ nam',
          categoryName: '',
          status: 1
        },
        
        // Dữ liệu cho chỉnh sửa
        editingSubCategory: {
          id: '',
          subCategoriesName: '',
          categoryName: '',
          status: 1
        },
        
        // ID cho xóa
        deleteId: null
      }
    },
    mounted() {
      // Khởi tạo các đối tượng modal sử dụng Bootstrap
      this.initModals();
      
      // Khởi tạo dữ liệu
      this.fetchCategories();
      this.fetchSubCategories();
      
      // Thiết lập auto-hide cho alert
      this.setupAlertAutoHide();
    },
    methods: {
      // Khởi tạo các modal Bootstrap
      initModals() {
        this.$nextTick(() => {
          this.addModal = new Modal(document.getElementById('addSubCategoryModal'));
          this.editModal = new Modal(document.getElementById('editSubCategoryModal'));
          this.deleteModal = new Modal(document.getElementById('confirmDeleteModal'));
        });
      },
      
      // Thiết lập tự động ẩn thông báo
      setupAlertAutoHide() {
        if (this.error) {
          setTimeout(() => {
            this.error = '';
          }, 5000);
        }
        
        if (this.success) {
          setTimeout(() => {
            this.success = '';
          }, 5000);
        }
      },
      
      // Lấy danh sách danh mục
      fetchCategories() {
        // Trong thực tế, bạn sẽ gọi API
        // axios.get('/api/categories')
        //   .then(response => {
        //     this.categories = response.data;
        //     if (this.categories.length > 0) {
        //       this.selectedCategory = this.categories[0].name;
        //     }
        //   })
        //   .catch(error => {
        //     this.error = 'Không thể lấy danh sách danh mục: ' + error.message;
        //   });
        
        // Dữ liệu mẫu
        this.categories = [
          { name: 'Rolex' },
          { name: 'Casio' },
          { name: 'Seiko' },
          { name: 'Citizen' }
        ];
        this.selectedCategory = 'Rolex';
      },
      
      // Lấy danh sách thương hiệu con theo danh mục
      fetchSubCategories() {
        // Trong thực tế, bạn sẽ gọi API
        // axios.get(`/api/subcategories?categoryName=${this.selectedCategory}&page=${this.currentPage}`)
        //   .then(response => {
        //     this.subCategories = response.data.content;
        //     this.totalPages = response.data.totalPages;
        //     this.currentPage = response.data.number + 1;
        //   })
        //   .catch(error => {
        //     this.error = 'Không thể lấy danh sách thương hiệu: ' + error.message;
        //   });
        
        // Dữ liệu mẫu
        this.subCategories = [
          {
            id: 1,
            subCategoriesName: 'Đồng hồ nam',
            category: { name: 'Rolex' },
            status: 1
          },
          {
            id: 2,
            subCategoriesName: 'Đồng hồ nữ',
            category: { name: 'Rolex' },
            status: 1
          }
        ];
        this.totalPages = 1;
        this.currentPage = 1;
      },
      
      // Lọc theo danh mục
      filterByCategory() {
        this.currentPage = 1;
        this.fetchSubCategories();
      },
      
      // Chuyển trang
      changePage(page) {
        if (page < 1 || page > this.totalPages) return;
        this.currentPage = page;
        this.fetchSubCategories();
      },
      
      // Mở modal thêm mới
      openAddModal() {
        this.newSubCategory = {
          subCategoriesName: 'Đồng hồ nam',
          categoryName: '',
          status: 1
        };
        this.addModal.show();
      },
      
      // Đóng modal thêm mới
      closeAddModal() {
        this.addModal.hide();
      },
      
      // Lưu thương hiệu mới
      saveSubCategory() {
        // Trong thực tế, bạn sẽ gọi API
        // axios.post('/api/subcategories/save', this.newSubCategory)
        //   .then(response => {
        //     this.success = 'Thêm thương hiệu thành công!';
        //     this.closeAddModal();
        //     this.fetchSubCategories();
        //   })
        //   .catch(error => {
        //     this.error = 'Không thể thêm thương hiệu: ' + error.message;
        //   });
        
        // Giả lập thành công
        this.success = 'Thêm thương hiệu thành công!';
        this.closeAddModal();
        this.fetchSubCategories();
        this.setupAlertAutoHide();
      },
      
      // Mở modal chỉnh sửa
      openEditModal(subCategory) {
        this.editingSubCategory = {
          id: subCategory.id,
          subCategoriesName: subCategory.subCategoriesName,
          categoryName: subCategory.category.name,
          status: subCategory.status
        };
        this.editModal.show();
      },
      
      // Đóng modal chỉnh sửa
      closeEditModal() {
        this.editModal.hide();
      },
      
      // Cập nhật thương hiệu
      updateSubCategory() {
        // Trong thực tế, bạn sẽ gọi API
        // axios.post('/api/subcategories/save', this.editingSubCategory)
        //   .then(response => {
        //     this.success = 'Cập nhật thương hiệu thành công!';
        //     this.closeEditModal();
        //     this.fetchSubCategories();
        //   })
        //   .catch(error => {
        //     this.error = 'Không thể cập nhật thương hiệu: ' + error.message;
        //   });
        
        // Giả lập thành công
        this.success = 'Cập nhật thương hiệu thành công!';
        this.closeEditModal();
        this.fetchSubCategories();
        this.setupAlertAutoHide();
      },
      
      // Mở modal xác nhận xóa
      openDeleteConfirmModal(id) {
        this.deleteId = id;
        this.deleteModal.show();
      },
      
      // Đóng modal xác nhận xóa
      closeDeleteModal() {
        this.deleteModal.hide();
      },
      
      // Xóa thương hiệu
      deleteSubCategory() {
        // Trong thực tế, bạn sẽ gọi API
        // axios.get(`/api/subcategories/delete/${this.deleteId}?categoryName=${this.selectedCategory}`)
        //   .then(response => {
        //     this.success = 'Xóa thương hiệu thành công!';
        //     this.closeDeleteModal();
        //     this.fetchSubCategories();
        //   })
        //   .catch(error => {
        //     this.error = 'Không thể xóa thương hiệu: ' + error.message;
        //   });
        
        // Giả lập thành công
        this.success = 'Xóa thương hiệu thành công!';
        this.closeDeleteModal();
        this.fetchSubCategories();
        this.setupAlertAutoHide();
      }
    }
  }
  </script>
  
  <style>
  body {
    background: #111111;
    color: #D4AF37;
    font-family: 'Helvetica Neue', Arial, sans-serif;
  }
  
  .table-container {
    background: #222222;
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
    padding: 30px;
    margin-top: 20px;
    border: 1px solid rgba(212, 175, 55, 0.2);
  }
  
  .table-container h2 {
    color: #D4AF37;
    font-weight: 300;
    letter-spacing: 1px;
    margin-bottom: 30px;
  }
  
  .btn-success {
    background: #D4AF37;
    color: #111111;
    border: none;
    padding: 10px 25px;
    border-radius: 5px;
    font-weight: 500;
    transition: all 0.3s ease;
  }
  
  .btn-success:hover {
    background: #B4941E;
    color: #111111;
    transform: translateY(-2px);
  }
  
  table {
    border-collapse: separate;
    border-spacing: 0 8px;
    text-align: center;
  }
  
  table thead tr th {
    background-color: #111111 !important;
    color: #D4AF37 !important;
    text-align: center;
    border: none !important;
    padding: 15px;
    font-weight: 400;
    letter-spacing: 1px;
  }
  
  tbody tr {
    background: rgba(212, 175, 55, 0.05);
    transition: all 0.3s ease;
  }
  
  tbody tr:hover {
    background: rgba(212, 175, 55, 0.1);
    transform: scale(1.01);
  }
  
  tbody td {
    border: none !important;
    color: #fff;
    padding: 15px;
    vertical-align: middle;
  }
  
  .btn-warning {
    background: #D4AF37;
    color: #111111;
    border: none;
    margin-right: 5px;
  }
  
  .btn-danger {
    background: #2c2c2c;
    color: #D4AF37;
    border: 1px solid #D4AF37;
  }
  
  .btn-warning:hover, .btn-danger:hover {
    opacity: 0.8;
    transform: translateY(-1px);
  }
  
  .modal-content {
    background: #222222;
    border: 1px solid #D4AF37;
  }
  
  .modal-header {
    border-bottom: 1px solid rgba(212, 175, 55, 0.2);
  }
  
  .modal-footer {
    border-top: 1px solid rgba(212, 175, 55, 0.2);
  }
  
  .modal-title {
    color: #D4AF37;
  }
  
  .btn-close {
    color: #D4AF37;
    opacity: 1;
  }
  
  .form-label {
    color: #D4AF37;
  }
  
  .form-control, .form-select {
    background: #111111;
    border: 1px solid #D4AF37;
    color: #fff;
  }
  
  .form-control:focus, .form-select:focus {
    background: #111111;
    border-color: #B4941E;
    box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25);
    color: #fff;
  }
  
  .form-select option {
    background: #111111;
    color: #fff;
  }
  
  .modal .btn-secondary {
    background: #2c2c2c;
    color: #D4AF37;
    border: 1px solid #D4AF37;
  }
  
  .modal .btn-primary {
    background: #D4AF37;
    color: #111111;
    border: none;
  }
  
  .modal .btn-primary:hover, .modal .btn-secondary:hover {
    opacity: 0.9;
    transform: translateY(-1px);
  }
  
  .pagination {
    margin-top: 20px;
  }
  
  .page-link {
    background-color: #222222;
    color: #D4AF37;
    border: 1px solid rgba(212, 175, 55, 0.2);
  }
  
  .page-link:hover {
    background-color: rgba(212, 175, 55, 0.1);
    color: #D4AF37;
  }
  
  .page-item.active .page-link {
    background-color: #D4AF37;
    border-color: #D4AF37;
    color: #111111;
  }
  
  .page-item.disabled .page-link {
    background-color: #222222;
    color: rgba(212, 175, 55, 0.5);
  }
  
  #categorySelect {
    background: #111111;
    border: 1px solid #D4AF37;
    color: #fff;
    padding: 8px 16px;
    border-radius: 5px;
    margin-left: 10px;
  }
  
  #categorySelect:focus {
    background: #111111;
    border-color: #B4941E;
    box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25);
    color: #fff;
  }
  
  label[for="categorySelect"] {
    color: #D4AF37;
  }
  
  .alert {
    margin-top: 20px;
  }
  </style>