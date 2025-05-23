<template>
    <div>
      <!-- Banner lớn ở đầu trang -->
      <div class="banner">
        <div class="banner-overlay">
          <div class="banner-text">
            <h1>Điều Khoản Thanh Toán TimeLux</h1>
            <p>Đa dạng phương thức thanh toán và giải pháp tài chính linh hoạt cho tình yêu đồng hồ của bạn</p>
          </div>
        </div>
      </div>
  
      <section class="page-title">
        <div class="container">
          <h1>Điều Khoản Thanh Toán</h1>
        </div>
      </section>
  
      <div class="container">
        <section class="content-section">
          <h2 class="term">Phương Thức Thanh Toán</h2>
          <p>Tại TimeLux, chúng tôi cung cấp đa dạng phương thức thanh toán để đáp ứng nhu cầu của quý khách hàng. Mọi giao dịch đều được bảo mật và an toàn theo tiêu chuẩn quốc tế.</p>
          
          <div class="payment-icons">
            <img src="/photos/P9.jpg" alt="Visa">
            <img src="/photos/P15.jpg" alt="Mastercard">
            <img src="/photos/P11.webp" alt="American Express">
            <img src="/photos/P18.png" alt="JCB">
            <img src="/photos/P14.webp" alt="Bank Transfer">
          </div>
          
          <div class="highlight">
            <p>TimeLux hiện chấp nhận thanh toán tiền mặt, thẻ tín dụng/ghi nợ quốc tế, chuyển khoản ngân hàng và các phương thức trả góp linh hoạt lên đến 24 tháng.</p>
          </div>
          
          <h3>Thanh Toán Trực Tiếp</h3>
          <p>Quý khách có thể thanh toán trực tiếp bằng tiền mặt hoặc thẻ tại tất cả các cửa hàng của TimeLux trên toàn quốc.</p>
          <ul>
            <li>Tiền mặt (VND)</li>
            <li>Thẻ tín dụng/ghi nợ quốc tế (Visa, Mastercard, JCB, American Express)</li>
            <li>Thẻ ATM nội địa có đăng ký dịch vụ Internet Banking</li>
          </ul>
          
          <div class="image-gallery">
            <div class="image-item" v-for="(image, index) in paymentImages" :key="index">
              <img :src="image.src" :alt="image.alt">
              <div class="image-caption">{{ image.caption }}</div>
            </div>
          </div>
          
          <h3>Chuyển Khoản Ngân Hàng</h3>
          <p>Đối với đơn hàng online hoặc đặt trước, quý khách có thể thực hiện chuyển khoản đến tài khoản ngân hàng của TimeLux:</p>
          
          <table class="payment-table">
            <tr>
              <th>Ngân hàng</th>
              <th>Số tài khoản</th>
              <th>Chủ tài khoản</th>
            </tr>
            <tr v-for="(bank, index) in bankAccounts" :key="index">
              <td>{{ bank.name }}</td>
              <td>{{ bank.accountNumber }}</td>
              <td>{{ bank.accountHolder }}</td>
            </tr>
          </table>
          
          <p>Khi thực hiện chuyển khoản, quý khách vui lòng ghi rõ nội dung chuyển khoản theo cú pháp: <strong>"TL + Tên + Số điện thoại"</strong> (Ví dụ: TL Nguyen Van A 0901234567).</p>
        </section>
  
        <section class="content-section">
          <h2 class="term">Chính Sách Trả Góp</h2>
          <p>Để đáp ứng nhu cầu sở hữu đồng hồ cao cấp, TimeLux hợp tác với các ngân hàng và tổ chức tài chính uy tín cung cấp giải pháp trả góp linh hoạt, giúp quý khách dễ dàng sở hữu chiếc đồng hồ mơ ước mà không gặp áp lực tài chính.</p>
          
          <h3>Trả góp qua thẻ tín dụng</h3>
          <p>Áp dụng cho chủ thẻ tín dụng của các ngân hàng liên kết với TimeLux. Kỳ hạn trả góp từ 3 đến 24 tháng với lãi suất ưu đãi.</p>
          
          <div class="payment-table">
            <table>
              <tr>
                <th>Ngân hàng</th>
                <th>Kỳ hạn</th>
                <th>Lãi suất</th>
                <th>Giá trị đơn hàng tối thiểu</th>
              </tr>
              <tr v-for="(item, index) in installmentPlans" :key="index">
                <td>{{ item.bank }}</td>
                <td>{{ item.term }}</td>
                <td>{{ item.interest }}</td>
                <td>{{ item.minAmount }}</td>
              </tr>
            </table>
          </div>
          
          <div class="highlight">
            <p>Chương trình ưu đãi: Trả góp 0% lãi suất trong 12 tháng đầu cho các sản phẩm Rolex, Omega, Patek Philippe khi thanh toán bằng thẻ tín dụng của các ngân hàng đối tác!</p>
          </div>
          
          <h3>Trả góp qua công ty tài chính</h3>
          <p>TimeLux hợp tác với các công ty tài chính uy tín như FE Credit, Home Credit và HD Saison để cung cấp giải pháp trả góp đa dạng.</p>
          
          <ul>
            <li v-for="(item, index) in financialCompanyFeatures" :key="index">{{ item }}</li>
          </ul>
          
          <div class="image-gallery">
            <div class="image-item" v-for="(image, index) in installmentImages" :key="index">
              <img :src="image.src" :alt="image.alt">
              <div class="image-caption">{{ image.caption }}</div>
            </div>
          </div>
          
          <p>Để biết thêm thông tin chi tiết về các chương trình trả góp, quý khách vui lòng liên hệ trực tiếp với nhân viên tư vấn tại cửa hàng hoặc gọi đến hotline của TimeLux.</p>
        </section>
        
        <section class="content-section">
          <h2 class="term">Quy Định Thanh Toán</h2>
          <p>Để đảm bảo giao dịch diễn ra thuận lợi và an toàn, quý khách vui lòng lưu ý các quy định thanh toán sau:</p>
          
          <h3>Đặt cọc và thanh toán đơn hàng đặt trước</h3>
          <ol>
            <li v-for="(rule, index) in depositRules" :key="index">{{ rule }}</li>
          </ol>
          
          <h3>Đối với đơn hàng online</h3>
          <p>Quý khách vui lòng thanh toán 100% giá trị đơn hàng trước khi TimeLux tiến hành giao hàng. Các phương thức thanh toán được chấp nhận:</p>
          <ul>
            <li v-for="(method, index) in onlinePaymentMethods" :key="index">{{ method }}</li>
          </ul>
          
          <div class="highlight">
            <p>Lưu ý: Đối với đơn hàng có giá trị từ 100 triệu đồng trở lên, TimeLux sẽ liên hệ trực tiếp với quý khách để xác nhận thông tin thanh toán và giao hàng.</p>
          </div>
          
          <h3>Hóa đơn và chứng từ thanh toán</h3>
          <p>TimeLux cam kết cung cấp đầy đủ hóa đơn VAT và chứng từ thanh toán cho mọi giao dịch. Quý khách có thể yêu cầu cung cấp hóa đơn điện tử hoặc hóa đơn giấy tùy theo nhu cầu.</p>
          
          <p>Quý khách lưu ý cung cấp chính xác thông tin xuất hóa đơn tại thời điểm thanh toán để đảm bảo quyền lợi về bảo hành và hậu mãi.</p>
        </section>
        
        <section class="content-section">
          <h2 class="term">Chính Sách Bảo Mật Thông Tin Thanh Toán</h2>
          <p>TimeLux cam kết bảo mật thông tin thanh toán của khách hàng theo quy định của pháp luật và tuân thủ các tiêu chuẩn bảo mật quốc tế.</p>
          
          <ul>
            <li v-for="(policy, index) in securityPolicies" :key="index">{{ policy }}</li>
          </ul>
          
          <div class="highlight">
            <p>TimeLux khuyến nghị khách hàng không chia sẻ thông tin thanh toán qua email, tin nhắn hoặc điện thoại. Mọi giao dịch chỉ được thực hiện thông qua kênh chính thức của TimeLux.</p>
          </div>
        </section>
        
        <section class="content-section">
          <h2 class="term">Câu Hỏi Thường Gặp</h2>
          
          <div v-for="(faq, index) in faqs" :key="index">
            <h3>{{ faq.question }}</h3>
            <p>{{ faq.answer }}</p>
          </div>
        </section>
        
        <section class="contact-info">
          <h3>Cần Hỗ Trợ Thêm?</h3>
          <p>Nếu quý khách có bất kỳ thắc mắc nào về các phương thức thanh toán hoặc cần tư vấn về giải pháp tài chính phù hợp, vui lòng liên hệ với chúng tôi:</p>
          <p>Hotline: 093 189 2222 (8:00 - 22:00, tất cả các ngày trong tuần)</p>
          <p>Email: TimeLux.Store@gmail.com</p>
          <p>Hoặc ghé thăm cửa hàng TimeLux gần nhất để được tư vấn trực tiếp.</p>
        </section>
        <br>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'PaymentTerms',
    data() {
      return {
        paymentImages: [
          { src: '/photos/P1.jpg', alt: 'Thanh toán tiền mặt', caption: 'Thanh toán tiền mặt tại cửa hàng' },
          { src: '/photos/P2.jpg', alt: 'Thanh toán thẻ', caption: 'Thanh toán bằng thẻ tín dụng/ghi nợ' },
          { src: '/photos/P14.webp', alt: 'Thanh toán QR', caption: 'Thanh toán qua QR Code' }
        ],
        bankAccounts: [
          { name: 'Vietcombank', accountNumber: '1234567890', accountHolder: 'CÔNG TY CỔ PHẦN TIMELUX' },
          { name: 'Techcombank', accountNumber: '0987654321', accountHolder: 'CÔNG TY CỔ PHẦN TIMELUX' },
          { name: 'BIDV', accountNumber: '1357924680', accountHolder: 'CÔNG TY CỔ PHẦN TIMELUX' }
        ],
        installmentPlans: [
          { bank: 'Sacombank', term: '3, 6, 9, 12 tháng', interest: '0% (đối với sản phẩm khuyến mãi)', minAmount: '5.000.000 VNĐ' },
          { bank: 'VPBank', term: '6, 12, 18, 24 tháng', interest: '0.99% - 1.49%/tháng', minAmount: '10.000.000 VNĐ' },
          { bank: 'Shinhan Bank', term: '3, 6, 12 tháng', interest: '0% (chương trình đặc biệt)', minAmount: '20.000.000 VNĐ' }
        ],
        financialCompanyFeatures: [
          'Kỳ hạn trả góp: 6, 12, 18, 24 tháng',
          'Trả trước tối thiểu: 20% - 40% giá trị sản phẩm',
          'Lãi suất: Từ 1.5% - 2.5%/tháng (tùy theo kỳ hạn và điều kiện)',
          'Hồ sơ đơn giản: CMND/CCCD, Hộ khẩu/KT3',
          'Thời gian phê duyệt nhanh chóng: 15-30 phút'
        ],
        installmentImages: [
          { src: '/photos/P4.png', alt: 'Trả góp qua thẻ tín dụng', caption: 'Trả góp qua thẻ tín dụng' },
          { src: '/photos/P5.png', alt: 'Trả góp qua công ty tài chính', caption: 'Trả góp qua công ty tài chính' },
          { src: '/photos/P6.png', alt: 'Tư vấn trả góp', caption: 'Tư vấn trả góp tại cửa hàng' }
        ],
        depositRules: [
          'Đối với sản phẩm đặt trước, quý khách cần đặt cọc tối thiểu 30% giá trị sản phẩm',
          'Thời hạn thanh toán số tiền còn lại: 7 ngày kể từ khi sản phẩm về cửa hàng',
          'Trường hợp hủy đặt hàng: phí hủy đơn 10% giá trị đặt cọc'
        ],
        onlinePaymentMethods: [
          'Chuyển khoản ngân hàng',
          'Thanh toán qua thẻ tín dụng/ghi nợ trực tuyến',
          'Thanh toán qua các ví điện tử: MoMo, ZaloPay, VNPay'
        ],
        securityPolicies: [
          'Mọi thông tin thẻ tín dụng/ghi nợ được bảo mật theo tiêu chuẩn PCI DSS',
          'Thông tin khách hàng chỉ được sử dụng cho mục đích giao dịch và không chia sẻ cho bên thứ ba',
          'Hệ thống thanh toán của TimeLux được bảo vệ bằng công nghệ mã hóa SSL 256-bit',
          'Quý khách có quyền yêu cầu xóa thông tin thanh toán lưu trữ sau khi hoàn tất giao dịch'
        ],
        faqs: [
          {
            question: 'TimeLux có chấp nhận thanh toán bằng ngoại tệ không?',
            answer: 'Hiện tại, TimeLux chỉ chấp nhận thanh toán bằng VND. Tuy nhiên, quý khách có thể sử dụng thẻ tín dụng quốc tế để thanh toán, hệ thống sẽ tự động quy đổi sang VND theo tỷ giá của ngân hàng phát hành thẻ.'
          },
          {
            question: 'Làm thế nào để kiểm tra tình trạng thanh toán của đơn hàng?',
            answer: 'Quý khách có thể kiểm tra tình trạng thanh toán bằng cách đăng nhập vào tài khoản TimeLux, liên hệ với bộ phận CSKH qua số hotline 1900.6789 hoặc email support@timelux.vn.'
          },
          {
            question: 'Có thể thay đổi phương thức thanh toán sau khi đã đặt hàng không?',
            answer: 'Có, quý khách có thể thay đổi phương thức thanh toán trước khi hoàn tất thanh toán. Vui lòng liên hệ với nhân viên CSKH để được hỗ trợ.'
          },
          {
            question: 'TimeLux có chấp nhận thanh toán bằng Bitcoin hoặc tiền điện tử khác không?',
            answer: 'Hiện tại, TimeLux chưa chấp nhận thanh toán bằng Bitcoin hoặc các loại tiền điện tử khác. Chúng tôi sẽ cập nhật nếu có thay đổi về phương thức thanh toán trong tương lai.'
          }
        ]
      }
    }
  }
  </script>
  
  <style scoped>
  /* Giữ nguyên tất cả các style từ file gốc */
  body {
    font-family: 'Helvetica Neue', Arial, sans-serif;
    line-height: 1.6;
    color: #333;
    margin: 0;
    padding: 0;
    background-color: #f8f8f8;
  }
  
  .container {
    width: 90%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 15px;
  }
  
  .page-title {
    background-color: #f2f2f2;
    padding: 40px 0;
    margin-bottom: 40px;
    border-bottom: 1px solid #e5e5e5;
  }
  
  .page-title h1 {
    font-size: 36px;
    margin: 0;
    color: #1a1a1a;
    font-weight: 300;
  }
  
  .content-section {
    background-color: #fff;
    padding: 40px;
    margin-bottom: 40px;
    border-radius: 5px;
    box-shadow: 0 2px 15px rgba(0,0,0,0.05);
  }
  
  .terms {
    color: #1a1a1a;
    font-size: 28px;
    margin-top: 0;
    margin-bottom: 25px;
    font-weight: 500;
    border-bottom: 2px solid #f2f2f2;
    padding-bottom: 10px;
  }
  
  h3 {
    font-size: 22px;
    color: #333;
    margin-top: 30px;
    font-weight: 500;
  }
  
  ul {
    padding-left: 20px;
  }
  
  ol {
    padding-left: 20px;
  }
  
  .highlight {
    background-color: #f9f8f3;
    border-left: 4px solid #d4af37;
    padding: 15px 20px;
    margin: 25px 0;
  }
  
  .highlight p {
    margin: 0;
    font-style: italic;
  }
  
  .image-gallery {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    margin: 30px 0;
  }
  
  .image-item {
    width: 32%;
    height: 300px;
    margin-bottom: 25px;
    box-shadow: 0 3px 10px rgba(0,0,0,0.1);
    border-radius: 5px;
    overflow: hidden;
    transition: transform 0.3s;
  }
  
  .image-item:hover {
    transform: translateY(-5px);
  }
  
  .image-item img {
    width: 100%;
    height: 220px;
    display: block;
    object-fit: cover;
  }
  
  .image-caption {
    padding: 12px 15px;
    background-color: #fff;
    font-size: 14px;
    color: #666;
    text-align: center;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .contact-info {
    background-color: #1a1a1a;
    color: #fff;
    padding: 25px;
    border-radius: 5px;
    margin-top: 40px;
  }
  
  .contact-info h3 {
    color: #d4af37;
    margin-top: 0;
  }
  
  .contact-info p {
    color: #ccc;
  }
  
  .cta-button {
    display: inline-block;
    background-color: #d4af37;
    color: #fff;
    padding: 12px 25px;
    border-radius: 4px;
    text-decoration: none;
    margin-top: 15px;
    transition: background-color 0.3s;
    font-weight: 500;
  }
  
  .cta-button:hover {
    background-color: #c4a030;
  }
  
  /* Breadcrumb */
  .breadcrumb {
    display: flex;
    list-style: none;
    padding: 0;
    margin-bottom: 20px;
  }
  
  .breadcrumb li {
    margin-right: 5px;
  }
  
  .breadcrumb li:after {
    content: "›";
    margin-left: 5px;
    color: #999;
  }
  
  .breadcrumb li:last-child:after {
    content: "";
  }
  
  .breadcrumb a {
    color: #666;
    text-decoration: none;
  }
  
  .breadcrumb a:hover {
    color: #d4af37;
  }
  
  /* Mobile Responsiveness */
  @media (max-width: 768px) {
    .image-item {
      width: 48%;
      height: 250px;
    }
    
    .image-item img {
      height: 170px;
    }
    
    .content-section {
      padding: 30px 20px;
    }
    
    .banner {
      height: 250px;
    }
  }
  
  @media (max-width: 480px) {
    .image-item {
      width: 100%;
      height: 280px;
    }
    
    .image-item img {
      height: 200px;
    }
    
    .content-section {
      padding: 25px 15px;
    }
    
    .page-title h1 {
      font-size: 28px;
    }
    
    h2 {
      font-size: 24px;
    }
    
    .contact-info {
      padding: 20px 15px;
    }
    
    .banner {
      height: 200px;
    }
  }
  
  /* Banner lớn ở đầu trang */
  .banner {
    width: 100%;
    height: 400px;
    background-image: url("/photos/P8.jpg");
    background-size: cover;
    background-position: center;
    position: relative;
    margin-bottom: 40px;
  }
  
  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .banner-text p{
    color: #fff !important;
  }
  .banner-text {
    color: #fff;
    text-align: center;
  }
  
  .banner-text h1 {
    font-size: 48px;
    margin-bottom: 20px;
    font-weight: 300;
    text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.6);
  }
  
  .banner-text p {
    font-size: 20px;
    max-width: 800px;
    margin: 0 auto;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.6);
  }
  
  .payment-icons {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 15px;
    margin: 20px 0;
  }
  
  .payment-icons img {
    height: 100px;
    width: auto;
    margin: 0 10px;
  }
  
  .payment-table {
    width: 100%;
    border-collapse: collapse;
    margin: 20px 0;
  }
  
  .payment-table th, .payment-table td {
    border: 1px solid #e5e5e5;
    padding: 12px 15px;
    text-align: left;
  }
  
  .payment-table th {
    background-color: #f2f2f2;
    font-weight: 500;
  }
  
  .payment-table tr:nth-child(even) {
    background-color: #f9f9f9;
  }
  </style>