<template>
    <div class="container py-5">
      <div class="table-container">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>Quản lý sản phẩm</h2>
          <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i>
            {{ successMessage }}
            <button type="button" class="btn-close" @click="successMessage = ''"></button>
          </div>
          <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-times-circle"></i>
            {{ errorMessage }}
            <button type="button" class="btn-close" @click="errorMessage = ''"></button>
          </div>
          <div class="d-flex gap-3">
            <button class="btn btn-success" @click="showAddModal">
              <i class="fas fa-plus me-2"></i>Thêm sản phẩm
            </button>
          </div>
        </div>
  
        <div class="search-filter-container mb-4">
          <form @submit.prevent="applyFilters" class="row g-3">
            <input type="hidden" name="page" :value="pagination.currentPage">
            <input type="hidden" name="size" :value="pagination.pageSize">
            <div class="col-lg-3">
              <input type="text" class="form-control" v-model="filters.search" placeholder="Tìm kiếm theo tên, loại hàng, hãng..." />
            </div>
            <div class="col-lg-2">
              <select class="form-select" v-model="filters.categoryId" @change="filterSubCategoriesForFilter">
                <option value="">Tất cả loại hàng</option>
                <option v-for="cat in categories" :value="cat.id" :key="cat.id">{{ cat.name }}</option>
              </select>
            </div>
            <div class="col-lg-2">
              <select class="form-select" v-model="filters.subCategoryId" id="filterSubCategory">
                <option value="">Tất cả hãng</option>
                <option v-for="subcat in filteredSubCategories" :value="subcat.id" :key="subcat.id" :data-category="subcat.category.id">
                  {{ subcat.subCategoriesName }}
                </option>
              </select>
            </div>
            <div class="col-lg-2">
              <select class="form-select" v-model="filters.status">
                <option value="">Tất cả trạng thái</option>
                <option value="1">Hoạt động</option>
                <option value="0">Khóa</option>
              </select>
            </div>
            <div class="col-lg-2">
              <button type="submit" class="btn btn-primary w-100">
                <i class="fas fa-filter me-2"></i>Lọc
              </button>
            </div>
          </form>
        </div>
  
        <table class="table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Tên sản phẩm</th>
              <th>Ảnh</th>
              <th>Loại hàng</th>
              <th>Hãng</th>
              <th>Giá</th>
              <th>Số lượng</th>
              <th>Mô tả</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(product, index) in products" :key="product.id">
              <td>{{ index + 1 + (pagination.currentPage * pagination.pageSize) }}</td>
              <td>{{ product.name }}</td>
              <td>
                <img :src="'/photos/' + product.image" alt="Product Image" class="product-image" @error="setDefaultImage">
              </td>
              <td>{{ product.subCategory?.category?.name || 'N/A' }}</td>
              <td>{{ product.subCategory?.subCategoriesName || 'N/A' }}</td>
              <td>
                <span v-if="product.discountPercentage">
                  {{ formatPrice(product.discountedPrice) }} VND
                  <br/>
                  <span style="text-decoration: line-through; color: #999;">{{ formatPrice(product.price) }} VND</span>
                  <span style="color: #D4AF37;"> ({{ product.discountPercentage }}% OFF)</span>
                </span>
                <span v-else>{{ formatPrice(product.price) }} VND</span>
              </td>
              <td>{{ product.qty }}</td>
              <td>{{ product.description || 'N/A' }}</td>
              <td>{{ product.status == 1 ? 'Hoạt động' : 'Khóa' }}</td>
              <td style="min-width: 80px;">
                <div class="d-flex gap-1">
                  <button class="btn btn-warning btn-sm" @click="showEditModal(product)">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button class="btn btn-danger btn-sm" @click="showDeleteModal(product)">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="products.length === 0">
              <td colspan="10" style="text-align: center;">Không tìm thấy sản phẩm nào</td>
            </tr>
          </tbody>
        </table>
  
        <nav aria-label="Page navigation" v-if="pagination.totalPages > 1">
          <ul class="pagination">
            <li class="page-item" :class="{ disabled: pagination.currentPage === 0 }">
              <a class="page-link" href="#" @click.prevent="changePage(pagination.currentPage - 1)">Trước</a>
            </li>
            <li class="page-item" v-for="i in visiblePages" :key="i" :class="{ active: pagination.currentPage === i }">
              <a class="page-link" href="#" @click.prevent="changePage(i)">{{ i + 1 }}</a>
            </li>
            <li class="page-item" :class="{ disabled: pagination.currentPage === pagination.totalPages - 1 }">
              <a class="page-link" href="#" @click.prevent="changePage(pagination.currentPage + 1)">Sau</a>
            </li>
          </ul>
        </nav>
  
        <!-- Modal thêm sản phẩm -->
        <div class="modal fade" id="addProductModal" tabindex="-1">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Thêm sản phẩm mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" @click="closeAddModal"></button>
              </div>
              <div class="modal-body">
                <form @submit.prevent="saveProduct" enctype="multipart/form-data" id="addForm">
                  <div class="mb-3">
                    <label class="form-label">Tên sản phẩm</label>
                    <input type="text" v-model="newProduct.name" class="form-control" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Loại hàng</label>
                    <select v-model="newProduct.categoryId" class="form-select" required @change="filterSubCategories">
                      <option value="" disabled selected>Chọn loại hàng</option>
                      <option v-for="cat in categories" :value="cat.id" :key="cat.id">{{ cat.name }}</option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Hãng</label>
                    <select v-model="newProduct.subCategoryId" class="form-select" required id="addSubCategory">
                      <option value="" disabled selected>Chọn hãng</option>
                      <option v-for="subcat in filteredSubCategoriesForAdd" :value="subcat.id" :key="subcat.id" :data-category="subcat.category.id">
                        {{ subcat.subCategoriesName }}
                      </option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Giá</label>
                    <input type="number" v-model="newProduct.price" class="form-control" min="0" step="1000" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Số lượng</label>
                    <input type="number" v-model="newProduct.qty" class="form-control" min="0" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Mô tả</label>
                    <textarea v-model="newProduct.description" class="form-control" rows="3"></textarea>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Trạng thái</label>
                    <select v-model="newProduct.status" class="form-select">
                      <option value="1">Hoạt động</option>
                      <option value="0">Khóa</option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Ảnh</label>
                    <input type="file" ref="addImageFile" class="form-control" accept="image/*">
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="closeAddModal">Đóng</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Modal sửa sản phẩm -->
        <div class="modal fade" id="editProductModal" tabindex="-1">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Sửa thông tin sản phẩm</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" @click="closeEditModal"></button>
              </div>
              <div class="modal-body">
                <form @submit.prevent="updateProduct" enctype="multipart/form-data" id="editForm">
                  <input type="hidden" v-model="editingProduct.id">
                  <div class="mb-3">
                    <label class="form-label">Tên sản phẩm</label>
                    <input type="text" v-model="editingProduct.name" class="form-control" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Loại hàng</label>
                    <select v-model="editingProduct.categoryId" class="form-select" required @change="filterSubCategoriesEdit">
                      <option value="" disabled>Chọn loại hàng</option>
                      <option v-for="cat in categories" :value="cat.id" :key="cat.id">{{ cat.name }}</option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Hãng</label>
                    <select v-model="editingProduct.subCategoryId" class="form-select" required id="editSubCategory">
                      <option value="" disabled>Chọn hãng</option>
                      <option v-for="subcat in filteredSubCategoriesForEdit" :value="subcat.id" :key="subcat.id" :data-category="subcat.category.id">
                        {{ subcat.subCategoriesName }}
                      </option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Giá</label>
                    <input type="number" v-model="editingProduct.price" class="form-control" min="0" step="1000" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Số lượng</label>
                    <input type="number" v-model="editingProduct.qty" class="form-control" min="0" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Mô tả</label>
                    <textarea v-model="editingProduct.description" class="form-control" rows="3"></textarea>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Trạng thái</label>
                    <select v-model="editingProduct.status" class="form-select">
                      <option value="1">Hoạt động</option>
                      <option value="0">Khóa</option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Ảnh hiện tại</label>
                    <div>
                      <img :src="editingProduct.image ? '/photos/' + editingProduct.image : '/photos/default.png'" alt="Current Image" class="product-image">
                      <input type="hidden" v-model="editingProduct.image">
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Thay đổi ảnh</label>
                    <input type="file" ref="editImageFile" class="form-control" accept="image/*">
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="closeEditModal">Đóng</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Modal xác nhận xóa -->
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1">
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Xác nhận xóa sản phẩm</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" @click="closeDeleteModal"></button>
              </div>
              <div class="modal-body">
                <div class="delete-warning">
                  <i class="fas fa-exclamation-triangle"></i>
                  <p>Bạn có chắc muốn xóa sản phẩm <strong>{{ deletingProduct.name }}</strong> không? Hành động này không thể hoàn tác.</p>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="closeDeleteModal">Hủy</button>
                <button type="button" class="btn btn-danger" @click="confirmDelete">Xác nhận</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { Modal } from 'bootstrap';
  import axios from 'axios';
  
  export default {
    name: 'ProductManagement',
    data() {
      return {
        products: [],
        categories: [],
        subcategories: [],
        filteredSubCategories: [],
        filteredSubCategoriesForAdd: [],
        filteredSubCategoriesForEdit: [],
        filters: {
          search: '',
          categoryId: '',
          subCategoryId: '',
          status: ''
        },
        pagination: {
          currentPage: 0,
          pageSize: 10,
          totalPages: 1
        },
        newProduct: {
          name: '',
          categoryId: '',
          subCategoryId: '',
          price: 0,
          qty: 0,
          description: '',
          status: '1',
          image: ''
        },
        editingProduct: {
          id: '',
          name: '',
          categoryId: '',
          subCategoryId: '',
          price: 0,
          qty: 0,
          description: '',
          status: '1',
          image: ''
        },
        deletingProduct: {
          id: '',
          name: ''
        },
        successMessage: '',
        errorMessage: '',
        addModal: null,
        editModal: null,
        deleteModal: null
      };
    },
    computed: {
      visiblePages() {
        const start = Math.max(0, this.pagination.currentPage - 2);
        const end = Math.min(this.pagination.totalPages - 1, this.pagination.currentPage + 2);
        return Array.from({ length: end - start + 1 }, (_, i) => start + i);
      }
    },
    mounted() {
      this.addModal = new Modal(document.getElementById('addProductModal'));
      this.editModal = new Modal(document.getElementById('editProductModal'));
      this.deleteModal = new Modal(document.getElementById('deleteConfirmModal'));
      
      this.loadCategories();
      this.loadSubCategories();
      this.loadProducts();
      
      // Check URL for success/error messages
      const urlParams = new URLSearchParams(window.location.search);
      if (urlParams.has('success')) {
        this.successMessage = 'Thao tác thành công!';
        setTimeout(() => this.successMessage = '', 5000);
      }
      if (urlParams.has('error')) {
        this.errorMessage = urlParams.get('error');
        setTimeout(() => this.errorMessage = '', 5000);
      }
    },
    methods: {
      async loadProducts() {
        try {
          const params = {
            page: this.pagination.currentPage,
            size: this.pagination.pageSize,
            search: this.filters.search,
            categoryId: this.filters.categoryId,
            subCategoryId: this.filters.subCategoryId,
            status: this.filters.status
          };
          
          const response = await axios.get('/java5/asm/crud/products', { params });
          this.products = response.data.content;
          this.pagination.totalPages = response.data.totalPages;
        } catch (error) {
          console.error('Error loading products:', error);
          this.errorMessage = 'Lỗi khi tải danh sách sản phẩm';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      },
      async loadCategories() {
        try {
          const response = await axios.get('/java5/asm/crud/categories');
          this.categories = response.data;
        } catch (error) {
          console.error('Error loading categories:', error);
        }
      },
      async loadSubCategories() {
        try {
          const response = await axios.get('/java5/asm/crud/subcategories');
          this.subcategories = response.data;
          this.filteredSubCategories = [...this.subcategories];
          this.filteredSubCategoriesForAdd = [...this.subcategories];
          this.filteredSubCategoriesForEdit = [...this.subcategories];
        } catch (error) {
          console.error('Error loading subcategories:', error);
        }
      },
      applyFilters() {
        this.pagination.currentPage = 0;
        this.loadProducts();
      },
      changePage(page) {
        if (page >= 0 && page < this.pagination.totalPages) {
          this.pagination.currentPage = page;
          this.loadProducts();
        }
      },
      showAddModal() {
        this.newProduct = {
          name: '',
          categoryId: '',
          subCategoryId: '',
          price: 0,
          qty: 0,
          description: '',
          status: '1',
          image: ''
        };
        this.addModal.show();
      },
      closeAddModal() {
        this.addModal.hide();
      },
      showEditModal(product) {
        this.editingProduct = {
          id: product.id,
          name: product.name,
          categoryId: product.subCategory?.category?.id || '',
          subCategoryId: product.subCategory?.id || '',
          price: product.price,
          qty: product.qty,
          description: product.description || '',
          status: product.status.toString(),
          image: product.image
        };
        this.filterSubCategoriesEdit();
        this.editModal.show();
      },
      closeEditModal() {
        this.editModal.hide();
      },
      showDeleteModal(product) {
        this.deletingProduct = {
          id: product.id,
          name: product.name
        };
        this.deleteModal.show();
      },
      closeDeleteModal() {
        this.deleteModal.hide();
      },
      filterSubCategories() {
        if (!this.newProduct.categoryId) {
          this.filteredSubCategoriesForAdd = [...this.subcategories];
          return;
        }
        this.filteredSubCategoriesForAdd = this.subcategories.filter(
          subcat => subcat.category.id == this.newProduct.categoryId
        );
      },
      filterSubCategoriesEdit() {
        if (!this.editingProduct.categoryId) {
          this.filteredSubCategoriesForEdit = [...this.subcategories];
          return;
        }
        this.filteredSubCategoriesForEdit = this.subcategories.filter(
          subcat => subcat.category.id == this.editingProduct.categoryId
        );
      },
      filterSubCategoriesForFilter() {
        if (!this.filters.categoryId) {
          this.filteredSubCategories = [...this.subcategories];
          return;
        }
        this.filteredSubCategories = this.subcategories.filter(
          subcat => subcat.category.id == this.filters.categoryId
        );
        this.filters.subCategoryId = '';
      },
      async saveProduct() {
        try {
          const formData = new FormData();
          formData.append('name', this.newProduct.name);
          formData.append('subCategory.id', this.newProduct.subCategoryId);
          formData.append('price', this.newProduct.price);
          formData.append('qty', this.newProduct.qty);
          formData.append('description', this.newProduct.description);
          formData.append('status', this.newProduct.status);
          
          if (this.$refs.addImageFile.files[0]) {
            formData.append('imageFile', this.$refs.addImageFile.files[0]);
          }
          
          await axios.post('/java5/asm/crud/products/save', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          
          this.successMessage = 'Thêm sản phẩm thành công!';
          setTimeout(() => this.successMessage = '', 5000);
          this.addModal.hide();
          this.loadProducts();
        } catch (error) {
          console.error('Error saving product:', error);
          this.errorMessage = 'Lỗi khi thêm sản phẩm';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      },
      async updateProduct() {
        try {
          const formData = new FormData();
          formData.append('id', this.editingProduct.id);
          formData.append('name', this.editingProduct.name);
          formData.append('subCategory.id', this.editingProduct.subCategoryId);
          formData.append('price', this.editingProduct.price);
          formData.append('qty', this.editingProduct.qty);
          formData.append('description', this.editingProduct.description);
          formData.append('status', this.editingProduct.status);
          formData.append('image', this.editingProduct.image);
          
          if (this.$refs.editImageFile.files[0]) {
            formData.append('imageFile', this.$refs.editImageFile.files[0]);
          }
          
          await axios.post('/java5/asm/crud/products/save', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          
          this.successMessage = 'Cập nhật sản phẩm thành công!';
          setTimeout(() => this.successMessage = '', 5000);
          this.editModal.hide();
          this.loadProducts();
        } catch (error) {
          console.error('Error updating product:', error);
          this.errorMessage = 'Lỗi khi cập nhật sản phẩm';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      },
      async confirmDelete() {
        try {
          await axios.delete(`/java5/asm/crud/products/delete/${this.deletingProduct.id}`);
          this.successMessage = 'Xóa sản phẩm thành công!';
          setTimeout(() => this.successMessage = '', 5000);
          this.deleteModal.hide();
          this.loadProducts();
        } catch (error) {
          console.error('Error deleting product:', error);
          this.errorMessage = 'Lỗi khi xóa sản phẩm';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      },
      formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price);
      },
      setDefaultImage(event) {
        event.target.src = '/photos/default.png';
      }
    }
  };
  </script>
  
  <style scoped>
  /* Giữ nguyên tất cả các style từ file gốc */
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
    border-spacing: 0 10px;
    width: 100%;
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
  
  .product-image {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 5px;
    border: 1px solid rgba(212, 175, 55, 0.3);
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
    border-radius: 10px;
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
    filter: invert(1);
  }
  
  .form-label {
    color: #D4AF37;
  }
  
  table thead tr th:nth-child(1) { /* STT */
    min-width: 50px;
  }
  table thead tr th:nth-child(2) { /* Tên sản phẩm */
    min-width: 200px;
  }
  table thead tr th:nth-child(3) { /* Ảnh */
    min-width: 80px;
  }
  table thead tr th:nth-child(4) { /* Loại hàng */
    min-width: 120px;
  }
  table thead tr th:nth-child(5) { /* Hãng */
    min-width: 120px;
  }
  table thead tr th:nth-child(6) { /* Giá */
    min-width: 150px;
  }
  table thead tr th:nth-child(7) { /* Số lượng */
    min-width: 100px;
  }
  table thead tr th:nth-child(8) { /* Mô tả */
    min-width: 200px;
  }
  table thead tr th:nth-child(9) { /* Trạng thái */
    min-width: 100px;
  }
  table thead tr th:nth-child(10) { /* Thao tác */
    min-width: 100px;
  }
  
  .form-control, .form-select {
    background: #111111;
    border: 1px solid #D4AF37;
    color: #fff;
    height: 40px;
    padding: 8px;
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
    transition: all 0.3s ease;
  }
  
  .modal .btn-secondary:hover {
    background: #3a3a3a;
    transform: translateY(-1px);
  }
  
  .modal .btn-primary {
    background: #D4AF37;
    color: #111111;
    border: none;
    transition: all 0.3s ease;
  }
  
  .modal .btn-primary:hover {
    background: #B4941E;
    transform: translateY(-1px);
  }
  
  .modal .btn-danger {
    background: #dc3545;
    color: #fff;
    border: none;
    transition: all 0.3s ease;
  }
  
  .modal .btn-danger:hover {
    background: #c82333;
    transform: translateY(-1px);
  }
  
  .pagination {
    margin-top: 20px;
    justify-content: center;
    display: flex;
  }
  
  .page-link {
    background-color: #222222;
    color: #D4AF37;
    border: 1px solid rgba(212, 175, 55, 0.2);
    margin: 0 2px;
    padding: 6px 12px;
    border-radius: 4px;
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
    pointer-events: none;
  }
  
  .search-filter-container {
    background: #222222;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 4px 15px rgba(212, 175, 55, 0.15);
    border: 1px solid rgba(212, 175, 55, 0.2);
  }
  
  .search-filter-container .row {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .search-filter-container .col-lg-3,
  .search-filter-container .col-lg-2 {
    flex: 0 0 auto;
    width: 200px;
  }
  
  .search-filter-container .btn-primary {
    background: #D4AF37;
    color: #111111;
    border: none;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    padding: 0;
  }
  
  .search-filter-container .btn-primary:hover {
    background: #B4941E;
  }
  
  .alert {
    border-radius: 8px;
    padding: 12px 15px;
    font-size: 0.9rem;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    margin-bottom: 15px;
    width: 100%;
    max-width: 500px;
    position: relative;
    color: #fff;
  }
  
  .alert-success {
    background-color: #28a745;
    border-color: #28a745;
  }
  
  .alert-danger {
    background-color: #dc3545;
    border-color: #dc3545;
  }
  
  .alert i {
    vertical-align: middle;
    margin-right: 8px;
  }
  
  .alert-dismissible .btn-close {
    padding: 12px;
    filter: invert(1);
  }
  
  .modal-body .delete-warning {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #fff;
  }
  
  .modal-body .delete-warning i {
    color: #dc3545;
    font-size: 24px;
  }
  
  .modal-body .delete-warning strong {
    color: #D4AF37;
  }
  
  .modal.fade .modal-dialog {
    transition: transform 0.3s ease-out;
    transform: translate(0, -50px);
  }
  
  .modal.show .modal-dialog {
    transform: translate(0, 0);
  }
  
  @media (max-width: 768px) {
    .alert {
      max-width: 100%;
      font-size: 0.85rem;
    }
    .table-container {
      padding: 15px;
    }
    .btn-success {
      padding: 8px 15px;
    }
    .search-filter-container .col-lg-3,
    .search-filter-container .col-lg-2 {
      width: 100%;
      margin-bottom: 10px;
    }
    .search-filter-container .row {
      flex-direction: column;
      gap: 0;
    }
  }
  </style>