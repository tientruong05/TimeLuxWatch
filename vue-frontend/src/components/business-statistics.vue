<template>
    <div class="container py-5">
      <h1>Thống kê doanh thu theo loại hàng</h1>
      <div id="product-stats">
        <table class="table">
          <thead>
            <tr>
              <th>Tên loại hàng</th>
              <th>Tên thương hiệu</th>
              <th>Tổng doanh thu</th>
              <th>Tổng số lượng</th>
              <th>Giá cao nhất</th>
              <th>Giá thấp nhất</th>
              <th>Giá trung bình</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="stat in revenueStats" :key="stat.categoryName + stat.subName">
              <td>{{ stat.categoryName }}</td>
              <td>{{ stat.subName }}</td>
              <td>{{ formatCurrency(stat.totalRevenue) }} VNĐ</td>
              <td>{{ stat.totalQty }}</td>
              <td>{{ formatCurrency(stat.maxPrice) }} VNĐ</td>
              <td>{{ formatCurrency(stat.minPrice) }} VNĐ</td>
              <td>{{ formatCurrency(stat.avgPrice) }} VNĐ</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'BusinessStatistics',
    data() {
      return {
        revenueStats: []
      }
    },
    mounted() {
      // Fetch data from API when component is mounted
      this.fetchRevenueData()
    },
    methods: {
      async fetchRevenueData() {
        try {
          const response = await fetch('/api/statistics/revenue')
          this.revenueStats = await response.json()
        } catch (error) {
          console.error('Error fetching revenue statistics:', error)
        }
      },
      formatCurrency(value) {
        return value.toLocaleString('vi-VN')
      }
    }
  }
  </script>
  
  <style scoped>
  body {
    background: #111111;
    color: #D4AF37;
    font-family: 'Helvetica Neue', Arial, sans-serif;
  }
  
  h1 {
    text-align: center;
    color: #D4AF37;
    font-weight: 300;
    letter-spacing: 1px;
    margin-bottom: 30px;
  }
  
  #product-stats {
    background: #222222;
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(212, 175, 55, 0.15);
    padding: 30px;
    margin-top: 20px;
    border: 1px solid rgba(212, 175, 55, 0.2);
  }
  
  table {
    border-collapse: separate;
    border-spacing: 0 8px;
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
    text-align: center;
  }
  </style>