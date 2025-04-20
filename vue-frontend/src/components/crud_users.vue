<template>
    <div class="container py-5">
      <div class="table-container">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>Quản lý người dùng</h2>
  
          <div class="d-flex gap-3">
            <button class="btn btn-success" @click="showAddModal = true">
              <i class="fas fa-user-plus me-2"></i>Thêm người dùng
            </button>
          </div>
        </div>
  
        <div v-if="successMessage" class="alert alert-success alert-dismissible fade show">
          <i class="fas fa-check-circle"></i> {{ successMessage }}
          <button type="button" class="btn-close" @click="successMessage = null"></button>
        </div>
        <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show">
          <i class="fas fa-times-circle"></i> {{ errorMessage }}
          <button type="button" class="btn-close" @click="errorMessage = null"></button>
        </div>
  
        <table class="table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Tên đăng nhập</th>
              <th>Email</th>
              <th>Họ tên</th>
              <th>SĐT</th>
              <th>Vai trò</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(user, index) in users" :key="user.id">
              <td>{{ index + 1 + currentPage * pageSize }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.fullName }}</td>
              <td>{{ user.phone }}</td>
              <td>{{ user.role ? 'Admin' : 'User' }}</td>
              <td>{{ user.status ? 'Hoạt động' : 'Khóa' }}</td>
              <td>
                <button class="btn btn-warning btn-sm" @click="openEditModal(user)">
                  <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-danger btn-sm" @click="deleteUser(user.id)">
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
  
        <nav class="pagination justify-content-center" v-if="totalPages > 1">
          <button class="btn btn-sm btn-outline-warning me-2" :disabled="currentPage === 0" @click="changePage(currentPage - 1)">
            Trước
          </button>
          <button
            v-for="i in totalPages"
            :key="i"
            class="btn btn-sm me-1"
            :class="{ 'btn-warning': currentPage === i - 1, 'btn-outline-warning': currentPage !== i - 1 }"
            @click="changePage(i - 1)"
          >
            {{ i }}
          </button>
          <button class="btn btn-sm btn-outline-warning ms-2" :disabled="currentPage === totalPages - 1" @click="changePage(currentPage + 1)">
            Sau
          </button>
        </nav>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        users: [],
        currentPage: 0,
        pageSize: 5,
        totalPages: 0,
        successMessage: null,
        errorMessage: null,
        showAddModal: false,
        showEditModal: false,
        selectedUser: null,
      };
    },
    mounted() {
      this.loadUsers();
    },
    methods: {
      loadUsers() {
        fetch(`/java5/asm/crud/users/api?page=${this.currentPage}&size=${this.pageSize}`)
          .then((res) => res.json())
          .then((data) => {
            this.users = data.content;
            this.totalPages = data.totalPages;
          })
          .catch(() => {
            this.errorMessage = 'Không thể tải danh sách người dùng.';
          });
      },
      changePage(page) {
        this.currentPage = page;
        this.loadUsers();
      },
      openEditModal(user) {
        this.selectedUser = { ...user };
        this.showEditModal = true;
      },
      deleteUser(id) {
        if (confirm('Bạn có chắc muốn xóa người dùng này?')) {
          fetch(`/java5/asm/crud/users/delete/${id}`, {
            method: 'DELETE',
          })
            .then(() => {
              this.successMessage = 'Xóa người dùng thành công';
              this.loadUsers();
            })
            .catch(() => {
              this.errorMessage = 'Lỗi khi xóa người dùng';
            });
        }
      },
    },
  };
  </script>
  
  <style scoped>
  @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css');
  @import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');
  
  .table-container {
    background: #222222;
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
    padding: 30px;
    margin-top: 20px;
    border: 1px solid rgba(212, 175, 55, 0.2);
    color: #d4af37;
  }
  
  .table-container h2 {
    color: #d4af37;
    font-weight: 300;
    letter-spacing: 1px;
    margin-bottom: 30px;
  }
  
  .btn-success {
    background: #d4af37;
    color: #111111;
    border: none;
    padding: 10px 25px;
    border-radius: 5px;
    font-weight: 500;
    transition: all 0.3s ease;
  }
  
  .btn-success:hover {
    background: #b4941e;
    transform: translateY(-2px);
  }
  </style>
  