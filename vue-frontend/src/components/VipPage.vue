<template>
  <div>
    <div class="section">
      <div class="container1">
        <h1>10 KHÁCH HÀNG CỦA NĂM</h1>

        <form @submit.prevent="filterCustomers">
          <div class="row">
            <div class="col-md-4">
              <label for="year">CHỌN NĂM:</label>
              <input
                type="number"
                id="year"
                v-model.number="selectedYear"
                class="form-control mb-2"
              />
            </div>
            <div class="col-md-4">
              <label for="quarter">CHỌN QUÝ:</label>
              <select
                id="quarter"
                v-model.number="selectedQuarter"
                class="form-select mb-2"
              >
                <option :value="null">Tất cả</option>
                <option value="1">Quý 1</option>
                <option value="2">Quý 2</option>
                <option value="3">Quý 3</option>
                <option value="4">Quý 4</option>
              </select>
            </div>
            <div class="col-md-4 d-flex align-items-end">
              <button type="submit" class="btn btn-filter mb-2">LỌC</button>
            </div>
          </div>
        </form>

        <!-- Loading indicator -->
        <div v-if="isLoading" class="text-center my-5">
          <div class="spinner-border text-warning" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>

        <!-- Error message -->
        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <!-- Results display -->
        <div
          v-if="
            !isLoading && !error && Object.keys(vipCustomersByYear).length > 0
          "
        >
          <div v-for="(customers, year) in vipCustomersByYear" :key="year">
            <h2>
              Năm: {{ year
              }}{{ selectedQuarter ? `, Quý ${selectedQuarter}` : "" }}
            </h2>
            <table>
              <thead>
                <tr>
                  <th>Họ và tên</th>
                  <th>Email</th>
                  <th>Điện thoại</th>
                  <th>Tổng số tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(customer, index) in customers" :key="index">
                  <td>{{ customer.fullName }}</td>
                  <td>{{ customer.email }}</td>
                  <td>{{ customer.phone }}</td>
                  <td>{{ formatCurrency(customer.totalAmount) }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination mt-4">
            <span
              v-if="currentPage > 0"
              @click="changePage(currentPage - 1)"
              class="pagination-link"
            >
              &laquo; Trước
            </span>
            <span class="pagination-current">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
            </span>
            <span
              v-if="currentPage < totalPages - 1"
              @click="changePage(currentPage + 1)"
              class="pagination-link"
            >
              Sau &raquo;
            </span>
          </div>
        </div>

        <div
          v-if="
            !isLoading && !error && Object.keys(vipCustomersByYear).length === 0
          "
          class="no-data"
        >
          Không có dữ liệu cho năm đã chọn.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import apiClient from "@/services/api";

// State
const selectedYear = ref(new Date().getFullYear());
const selectedQuarter = ref(null);
const vipCustomersByYear = ref({});
const isLoading = ref(false);
const error = ref(null);
const currentPage = ref(0);
const pageSize = ref(10);
const totalPages = ref(0);

// Methods
const filterCustomers = () => {
  fetchCustomers();
};

const fetchCustomers = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
    };

    if (selectedYear.value) {
      params.year = selectedYear.value;
    }

    if (selectedQuarter.value) {
      params.quarter = selectedQuarter.value;
    }

    const response = await apiClient.get("/statistics/customers", {
      params,
    });

    vipCustomersByYear.value = response.data.vipCustomersByYear || {};
    currentPage.value = response.data.currentPage;
    totalPages.value = response.data.totalPages || 1;

    // If no selectedYear but data is returned, update the selectedYear
    if (!selectedYear.value && response.data.selectedYear) {
      selectedYear.value = response.data.selectedYear;
    }

    // If we have a selectedQuarter in the response, update the local one
    if (response.data.selectedQuarter !== undefined) {
      selectedQuarter.value = response.data.selectedQuarter;
    }
  } catch (err) {
    console.error("Error fetching VIP customers:", err);
    error.value = "Không thể tải dữ liệu khách hàng. Vui lòng thử lại sau.";
    vipCustomersByYear.value = {};
  } finally {
    isLoading.value = false;
  }
};

const changePage = (newPage) => {
  currentPage.value = newPage;
  fetchCustomers();
};

const formatCurrency = (value) => {
  if (value === undefined || value === null) return "";
  return value.toLocaleString("vi-VN", {
    style: "currency",
    currency: "VND",
  });
};

// Lifecycle hooks
onMounted(() => {
  fetchCustomers();
});
</script>

<style scoped>
body {
  background: #111111;
  color: #d4af37;
  font-family: "Helvetica Neue", Arial, sans-serif;
}

h1,
h2 {
  text-align: center;
  color: #d4af37;
  font-weight: 300;
  letter-spacing: 1px;
  margin-bottom: 30px;
}

form {
  text-align: center;
  margin-bottom: 30px;
  background: #222222;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
  padding: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

label {
  margin-right: 15px;
  color: #d4af37;
  font-weight: 400;
  display: block;
  text-align: left;
}

input[type="number"] {
  background: #111111;
  border: 1px solid #d4af37;
  border-radius: 5px;
  color: #fff;
  width: 100%;
  padding: 10px;
}

input[type="number"]:focus,
.form-select:focus {
  border-color: #b4941e;
  box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25);
  outline: none;
}

.form-select {
  background-color: #111111;
  border: 1px solid #d4af37;
  border-radius: 5px;
  color: #fff;
  width: 100%;
  padding: 10px;
}

.form-select option {
  background-color: #111111;
  color: #fff;
}

.btn-filter {
  padding: 10px 25px;
  background: #d4af37;
  color: #111111;
  border: none;
  border-radius: 5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.btn-filter:hover {
  background: #b4941e;
  transform: translateY(-2px);
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  margin-bottom: 20px;
  background: #222222;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
  padding: 30px;
  margin-top: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

th {
  background-color: #111111 !important;
  color: #d4af37 !important;
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

td {
  border: none !important;
  color: #fff;
  padding: 15px;
  vertical-align: middle;
  text-align: center;
}

.pagination {
  text-align: center;
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.pagination-link {
  padding: 8px 15px;
  background: #d4af37;
  color: #111111;
  border-radius: 5px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.pagination-link:hover {
  background: #b4941e;
  transform: translateY(-2px);
}

.pagination-current {
  padding: 8px 15px;
  background: #2c2c2c;
  color: #d4af37;
  border: 1px solid #d4af37;
  border-radius: 5px;
}

.no-data {
  text-align: center;
  margin-top: 20px;
  font-style: italic;
  color: #d4af37;
  background: #222222;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid rgba(212, 175, 55, 0.2);
}

.container1 {
  padding: 30px;
}

.section {
  width: 100%;
  padding: 40px 0;
}

.alert-danger {
  background-color: rgba(220, 53, 69, 0.1);
  color: #dc3545;
  border: 1px solid rgba(220, 53, 69, 0.2);
  border-radius: 5px;
  padding: 10px 15px;
  margin-bottom: 20px;
}
</style>
