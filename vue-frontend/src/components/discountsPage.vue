<template>
    <div class="container py-5">
      <div class="table-container">
        <h1>Quản lý giảm giá</h1>
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2>Tạo giảm giá mới</h2>
          <button class="btn btn-success" @click="showAddModal = true">
            <i class="fas fa-plus me-2"></i>Thêm giảm giá
          </button>
        </div>
        <h2>Danh sách giảm giá</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên giảm giá</th>
              <th>Giá trị giảm giá (%)</th>
              <th>Ngày bắt đầu</th>
              <th>Ngày kết thúc</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="discount in discounts" :key="discount.id">
              <td>{{ discount.id }}</td>
              <td>{{ discount.discountName }}</td>
              <td>{{ discount.discountValue }}</td>
              <td>{{ formatDate(discount.startDate) }}</td>
              <td>{{ formatDate(discount.endDate) }}</td>
              <td>
                <button class="btn btn-warning btn-sm" @click="openEditModal(discount.id)">
                  <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-danger btn-sm" @click="deleteDiscount(discount.id)">
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
  
        <EditDiscountModal v-if="showEditModal" :discountId="selectedDiscountId" @close="showEditModal = false; loadDiscounts()" />
        <AddDiscountModal v-if="showAddModal" @close="showAddModal = false; loadDiscounts()" />
      </div>
    </div>
  </template>
  
  <script>
  import EditDiscountModal from './EditDiscountModal.vue';
  import AddDiscountModal from './AddDiscountModal.vue';
  
  export default {
    components: { EditDiscountModal, AddDiscountModal },
    data() {
      return {
        discounts: [],
        showEditModal: false,
        showAddModal: false,
        selectedDiscountId: null,
      };
    },
    mounted() {
      this.loadDiscounts();
    },
    methods: {
      loadDiscounts() {
        fetch('/java5/asm/admin/discounts/api')
          .then(res => res.json())
          .then(data => {
            this.discounts = data;
          });
      },
      openEditModal(id) {
        this.selectedDiscountId = id;
        this.showEditModal = true;
      },
      deleteDiscount(id) {
        if (confirm('Bạn có chắc chắn muốn xóa?')) {
          fetch(`/java5/asm/admin/discounts/delete/${id}`, { method: 'DELETE' })
            .then(() => this.loadDiscounts())
            .catch(err => alert('Xóa thất bại'));
        }
      },
      formatDate(dateStr) {
        return new Date(dateStr).toLocaleDateString('vi-VN');
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
  
  .table-container h1,
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
  
  .table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0 10px;
  }
  
  .table thead tr th {
    background-color: #111111 !important;
    color: #d4af37 !important;
    text-align: center;
    border: none !important;
    padding: 15px;
    font-weight: 400;
    letter-spacing: 1px;
  }
  
  .table tbody tr {
    background: rgba(212, 175, 55, 0.05);
    transition: all 0.3s ease;
  }
  
  .table tbody tr:hover {
    background: rgba(212, 175, 55, 0.1);
    transform: scale(1.01);
  }
  
  .table tbody td {
    border: none !important;
    color: #fff;
    padding: 15px;
    vertical-align: middle;
  }
  
  .btn-warning {
    background: #d4af37;
    color: #111111;
    border: none;
    margin-right: 5px;
  }
  
  .btn-danger {
    background: #2c2c2c;
    color: #d4af37;
    border: 1px solid #d4af37;
  }
  
  .btn-warning:hover,
  .btn-danger:hover {
    opacity: 0.8;
    transform: translateY(-1px);
  }
  </style>
  