<template>
  <footer class="footer-area">
    <div class="container">
      <div class="row justify-content-between">
        <div class="col-md-4 col-sm-6 mb-4">
          <div class="footer">
            <h2 class="title__line--2">VỀ CHÚNG TÔI</h2>
            <ul class="ft__list">
              <li>
                <router-link :to="{ name: 'About' }"
                  >Giới thiệu về TimeLuxStore</router-link
                >
              </li>
              <!--  <li><a href="/java5/asm/footer/about">Phản ánh - Khiếu nại</a></li> -->
              <li>
                <router-link :to="{ name: 'Warranty' }"
                  >Chính sách bảo hành</router-link
                >
              </li>
              <li>
                <router-link :to="{ name: 'PaymentTerms' }"
                  >Điều khoản thanh toán</router-link
                >
              </li>
            </ul>
            <div class="social-links">
              <a href="https://www.facebook.com/"
                ><i class="fab fa-facebook-f"></i
              ></a>
              <a href="https://x.com/"><i class="fab fa-x"></i></a>
              <a href="https://www.instagram.com/"
                ><i class="fab fa-instagram"></i
              ></a>
              <a href="https://www.youtube.com/"
                ><i class="fab fa-youtube"></i
              ></a>
            </div>
          </div>
        </div>
        <div class="col-md-4 col-sm-6 mb-4">
          <div class="footer">
            <h2 class="title__line--2">TÀI KHOẢN KHÁCH</h2>
            <ul class="ft__list">
              <li><a href="/java5/asm/profile">Tài Khoản</a></li>
              <li><a href="/java5/asm/users/orders">Đơn Hàng</a></li>
              <!-- 
              <li><a href="/java5/asm/cart">Thanh Toán</a></li> -->
            </ul>
          </div>
        </div>
        <div class="col-md-4 col-sm-6 mb-4">
          <div class="footer">
            <h2 class="title__line--2">LIÊN HỆ HỖ TRỢ</h2>
            <ul class="ft__list">
              <li>Hotline 1: 093 189 2222</li>
              <li>Hotline 2: 097 189 3333</li>
            </ul>
          </div>
        </div>
        <!-- 
        <div class="col-md-3 col-sm-6 mb-4">
          <div class="footer">
            <h2 class="title__line--2">ĐĂNG KÝ NHẬN TIN</h2>
            <div class="news__input">
              <input
                type="email"
                placeholder="Địa Chỉ Email*"
                class="form-control bg-transparent"
              />
              <button class="btn btn-primary">Đăng Ký</button>
            </div>
            <p class="text-muted mt-3">Nhận thông tin mới nhất về sản phẩm và khuyến mãi.</p>
          </div>
        </div>
      </div> -->
      </div>
      <div class="htc__copyright mt-4">
        <div class="container">
          <div class="row">
            <div class="col-12 text-center">
              <div class="copyright__inner">
                <p>
                  © 2025 TimeLux.vn - Đồng hồ chính hãng | All rights reserved
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>
<script></script>
<style scoped>
.footer-area {
  background-color: #0a0a0a;
  color: #ffffff;
  padding: 60px 0 0;
}

.footer .title__line--2 {
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 25px;
  position: relative;
  text-transform: uppercase;
}

.footer .title__line--2::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: -8px;
  width: 50px;
  height: 2px;
  background: #e74c3c; /* Màu vàng sang trọng */
}

.ft__list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.ft__list li {
  margin-bottom: 12px;
}

.ft__list li a {
  color: #a8a8a8;
  text-decoration: none;
  transition: all 0.3s ease;
}

.ft__list li a:hover {
  color: #e74c3c; /* Màu vàng sang trọng */
  padding-left: 5px;
}

.news__input {
  position: relative;
  margin-top: 20px;
}

.news__input input {
  background-color: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #ffffff;
  padding: 12px;
  width: 100%;
}

.news__input input::placeholder {
  color: #a8a8a8;
}

.news__input .btn {
  margin-top: 10px;
  padding: 10px 25px;
  background-color: #e74c3c; /* Màu vàng sang trọng */
  border: none;
  transition: all 0.3s ease;
}

.news__input .btn:hover {
  background-color: #e74c3c; /* Màu vàng đậm hơn */
  transform: translateY(-2px);
}

.htc__copyright {
  background-color: #000000;
  padding: 20px 0;
}

.copyright__inner p {
  color: #a8a8a8;
  margin: 0;
}

.copyright__inner a {
  color: #e74c3c; /* Màu vàng sang trọng */
  text-decoration: none;
  transition: all 0.3s ease;
}

.copyright__inner a:hover {
  color: #ffffff;
}

.social-links {
  margin-top: 20px;
}

.social-links a {
  display: inline-block;
  width: 35px;
  height: 35px;
  background: rgba(255, 255, 255, 0.1);
  margin-right: 10px;
  text-align: center;
  line-height: 35px;
  border-radius: 50%;
  color: #a8a8a8;
  transition: all 0.3s ease;
}

.social-links a:hover {
  background: #e74c3c;
  color: #ffffff;
  transform: translateY(-3px);
}

@media (max-width: 768px) {
  .footer {
    margin-bottom: 30px;
  }
}
</style>
