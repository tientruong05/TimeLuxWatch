<template>
    <div>
      <div class="section">
        <div class="container1">
          <h1>10 KHÁCH HÀNG CỦA NĂM</h1>
  
          <form @submit.prevent="filterByYear">
            <label for="year">CHỌN NĂM:</label>
            <input type="number" id="year" v-model="selectedYear" />
            <button type="submit">LỌC</button>
          </form>
  
          <div v-if="Object.keys(vipCustomersByYear).length > 0">
            <div v-for="(customers, year) in vipCustomersByYear" :key="year">
              <h2>Năm: {{ year }}</h2>
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
                    <td>{{ customer.formattedTotalAmount }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div v-else>
            <p class="no-data">Không có dữ liệu cho năm đã chọn.</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'TopVipCustomers',
    data() {
      return {
        selectedYear: new Date().getFullYear(),
        vipCustomersByYear: {}
      }
    },
    mounted() {
      this.fetchCustomers();
    },
    methods: {
      fetchCustomers() {
        // Trong Vue.js thực tế, bạn sẽ gọi API để lấy dữ liệu
        // Ví dụ:
        // this.axios.get(`/api/statistics/customers?year=${this.selectedYear}`)
        //   .then(response => {
        //     this.vipCustomersByYear = response.data;
        //   })
        //   .catch(error => {
        //     console.error("Lỗi khi lấy dữ liệu khách hàng:", error);
        //   });
        
        // Đoạn code mẫu để giữ logic:
        this.vipCustomersByYear = {
          [this.selectedYear]: [
            // Dữ liệu mẫu
            {
              fullName: "Nguyễn Văn A",
              email: "nguyenvana@example.com",
              phone: "0912345678",
              formattedTotalAmount: "10,000,000 VNĐ"
            },
            // Thêm dữ liệu mẫu khác nếu cần
          ]
        };
      },
      filterByYear() {
        this.fetchCustomers();
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
  
  h1, h2 {
    text-align: center;
    color: #D4AF37;
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
    color: #D4AF37;
    font-weight: 400;
  }
  
  input[type="number"] {
    padding: 10px;
    background: #111111;
    border: 1px solid #D4AF37;
    border-radius: 5px;
    width: 120px;
    color: #fff;
    margin-right: 10px;
  }
  
  input[type="number"]:focus {
    border-color: #B4941E;
    box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.25);
    outline: none;
  }
  
  button {
    padding: 10px 25px;
    background: #D4AF37;
    color: #111111;
    border: none;
    border-radius: 5px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  button:hover {
    background: #B4941E;
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
  }
  
  .pagination a {
    padding: 8px 15px;
    margin: 0 5px;
    text-decoration: none;
    background: #D4AF37;
    color: #111111;
    border-radius: 5px;
    transition: all 0.3s ease;
  }
  
  .pagination a:hover {
    background: #B4941E;
    transform: translateY(-2px);
  }
  
  .pagination span {
    padding: 8px 15px;
    background: #2c2c2c;
    color: #D4AF37;
    border: 1px solid #D4AF37;
    border-radius: 5px;
    margin: 0 5px;
  }
  
  .no-data {
    text-align: center;
    margin-top: 20px;
    font-style: italic;
    color: #D4AF37;
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
  </style>