package com.TimeLuxWatchBE.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.TimeLuxWatchBE.dto.CartDetailDTO;
import com.TimeLuxWatchBE.dto.OrderDTO;
import com.TimeLuxWatchBE.dto.OrderDetailDTO;
import com.TimeLuxWatchBE.entity.CartEntity;
import com.TimeLuxWatchBE.entity.OrderEntity;
import com.TimeLuxWatchBE.entity.OrderDetailEntity;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.service.CartService;
import com.TimeLuxWatchBE.service.OrderService;
import com.TimeLuxWatchBE.service.OrderDetailService;
import com.TimeLuxWatchBE.service.ProductService;
import com.TimeLuxWatchBE.service.DiscountService;
import com.TimeLuxWatchBE.repository.DiscountRepository;
import com.TimeLuxWatchBE.entity.DiscountEntity;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.repository.ProductRepository;
import java.util.Optional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private DiscountRepository discountRepository;

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        return Optional.ofNullable((UserEntity) session.getAttribute("user"))
                .map(user -> {
                    List<CartDetailDTO> cartItemDTOs = cartService.getCartDetailsByUserId(user.getId());

                    for (CartDetailDTO dto : cartItemDTOs) {
                        Optional<ProductEntity> productOpt = productRepository.findById(dto.getProductId());
                        if (productOpt.isEmpty()) {
                            dto.setPrice(0f);
                            dto.setDiscountedPrice(0f);
                            dto.setProductName(dto.getProductName() + " (Không còn tồn tại)");
                            continue;
                        }
                        ProductEntity product = productOpt.get();

                        float originalPrice = product.getPrice();
                        dto.setPrice(originalPrice);

                        Integer categoryId = (product.getSubCategory() != null && product.getSubCategory().getCategory() != null)
                                ? product.getSubCategory().getCategory().getId() : null;
                        Integer subCategoryId = (product.getSubCategory() != null)
                                ? product.getSubCategory().getId() : null;

                        List<DiscountEntity> activeDiscounts = discountRepository.findAllActiveDiscountsForProduct(
                                product.getId(), categoryId, subCategoryId);

                        Optional<DiscountEntity> bestDiscount = activeDiscounts.stream()
                                .max((d1, d2) -> Float.compare(d1.getDiscountValue(), d2.getDiscountValue()));

                        if (bestDiscount.isPresent()) {
                            float discountValue = bestDiscount.get().getDiscountValue();
                            float currentDiscountedPrice = originalPrice * (1 - discountValue / 100);
                            dto.setDiscountedPrice(currentDiscountedPrice);
                        } else {
                            dto.setDiscountedPrice(originalPrice);
                        }
                        dto.setAvailableQty(product.getQty());
                    }

                    int cartCount = cartService.getCartCount(user.getId());
                    double total = cartItemDTOs.stream()
                            .mapToDouble(CartDetailDTO::getDiscountedPrice)
                            .sum();

                    response.put("cartItems", cartItemDTOs);
                    response.put("total", total);
                    response.put("shippingFee", 30000.0);
                    session.setAttribute("cartCount", cartCount);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @GetMapping("/check-stock")
    public ResponseEntity<List<CartDetailDTO>> checkStock(HttpSession session) {
        return Optional.ofNullable((UserEntity) session.getAttribute("user"))
                .map(user -> ResponseEntity.ok(cartService.getCartDetailsByUserId(user.getId())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @PostMapping("/add/{productId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> addToCart(@PathVariable("productId") int productId,
                                                         @RequestParam("quantity") int quantity,
                                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String result = cartService.addItemToCart(user, productId, quantity);
        if (result.equals("success")) {
            int newCartCount = cartService.getCartCount(user.getId());
            session.setAttribute("cartCount", newCartCount);
            response.put("message", "success");
            response.put("cartCount", newCartCount);
            return ResponseEntity.ok(response);
        } else if (result.startsWith("error:")) {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        } else {
            response.put("error", "Unexpected server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateCart(@RequestParam("itemId") int itemId,
                                                          @RequestParam("quantity") int quantity) {
        Map<String, Object> response = new HashMap<>();
        String result = cartService.updateCartItemQuantity(itemId, quantity);
        if (result.equals("success")) {
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/remove/{itemId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable("itemId") int itemId) {
        Map<String, Object> response = new HashMap<>();
        cartService.deleteCartItem(itemId);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartCount(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Ensure we get the most up-to-date count directly from the repository
        int cartCount = cartService.getCartCount(user.getId());
        response.put("count", cartCount);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody List<Integer> selectedItems, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (selectedItems.isEmpty()) {
            response.put("error", "Vui lòng chọn sản phẩm để thanh toán");
            return ResponseEntity.badRequest().body(response);
        }

        List<CartEntity> cartItems = cartService.getCartItemsByUserId(user.getId())
                .stream().filter(item -> selectedItems.contains(item.getId()))
                .collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            response.put("error", "Không tìm thấy sản phẩm đã chọn");
            return ResponseEntity.badRequest().body(response);
        }

        String stockError = cartItems.stream()
                .filter(item -> item.getQty() > item.getProduct().getQty())
                .findFirst()
                .map(item -> "Sản phẩm " + item.getProduct().getName() + " vượt quá tồn kho (" + item.getProduct().getQty() + " sản phẩm)")
                .orElse(null);
        if (stockError != null) {
            response.put("error", stockError);
            return ResponseEntity.badRequest().body(response);
        }

        List<CartDetailDTO> checkoutDTOs = cartService.mapToCartDetailDTOs(cartItems);
        double total = cartService.calculateCartTotal(checkoutDTOs);

        session.setAttribute("checkoutItems", checkoutDTOs);
        session.setAttribute("total", total);
        response.put("message", "Checkout data prepared");
        response.put("checkoutItems", checkoutDTOs);
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkout")
    public ResponseEntity<Map<String, Object>> showCheckout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<CartDetailDTO> checkoutItems = (List<CartDetailDTO>) session.getAttribute("checkoutItems");
        Double total = (Double) session.getAttribute("total");

        if (checkoutItems == null || total == null) {
            response.put("error", "No checkout data found");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("checkoutItems", checkoutItems);
        response.put("total", total);
        response.put("fullName", user.getFullName() != null ? user.getFullName() : "");
        response.put("address", user.getAddress() != null ? user.getAddress() : "");
        response.put("phone", user.getPhone() != null ? user.getPhone() : "");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    @Transactional
    public ResponseEntity<Map<String, Object>> completeOrder(HttpSession session,
                                                             @RequestParam("fullName") String fullName,
                                                             @RequestParam("address") String address,
                                                             @RequestParam("phone") String phone,
                                                             @RequestParam(value = "note", required = false) String note) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<CartDetailDTO> checkoutItems = (List<CartDetailDTO>) session.getAttribute("checkoutItems");
        Double total = (Double) session.getAttribute("total");
        if (checkoutItems == null || total == null) {
            response.put("error", "No checkout data found");
            return ResponseEntity.badRequest().body(response);
        }

        String stockError = cartService.validateCheckoutItems(checkoutItems);
        if (stockError != null) {
            response.put("error", stockError);
            return ResponseEntity.badRequest().body(response);
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setFullname(fullName);
        order.setAddress(address);
        order.setPhone(phone);
        OrderEntity savedOrder = orderService.save(order);

        cartService.processOrderItems(savedOrder, checkoutItems);

        try {
            cartService.sendOrderConfirmationEmail(user.getEmail(), savedOrder, checkoutItems, total);
            response.put("message", "Đơn hàng đã được đặt thành công! Vui lòng kiểm tra email.");
        } catch (MessagingException e) {
            response.put("message", "Đơn hàng đã được đặt, nhưng không thể gửi email xác nhận.");
        }

        session.removeAttribute("checkoutItems");
        session.removeAttribute("total");
        response.put("orderId", savedOrder.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> viewOrders(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        return Optional.ofNullable((UserEntity) session.getAttribute("user"))
                .map(user -> {
                    List<OrderDTO> orderDTOs = orderService.findByUser(user).stream()
                            .map(order -> {
                                List<OrderDetailEntity> details = orderDetailService.findByOrder(order);
                                double totalAmount = details.stream()
                                        .mapToDouble(detail -> detail.getPrice() * detail.getQty())
                                        .sum();
                                Date orderDate = details.isEmpty() ? new Date() : details.get(0).getOrderDate();
                                return new OrderDTO(
                                        order.getId(),
                                        order.getFullname(),
                                        order.getAddress(),
                                        order.getPhone(),
                                        totalAmount,
                                        orderDate);
                            })
                            .collect(Collectors.toList());

                    response.put("orders", orderDTOs);
                    response.put("totalOrders", orderDTOs.size());
                    response.put("totalSpent", orderDTOs.stream().mapToDouble(OrderDTO::getTotalAmount).sum());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @GetMapping("/order/detail/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable("orderId") int orderId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = Optional.ofNullable((UserEntity) session.getAttribute("user"))
                .orElse(null);
        if (user == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<OrderEntity> orderOptional = orderService.getOrderById(orderId)
                .filter(o -> o.getUser().getId() == user.getId());
        if (!orderOptional.isPresent()) {
            response.put("error", "Không tìm thấy đơn hàng hoặc không có quyền truy cập");
            return ResponseEntity.badRequest().body(response);
        }

        OrderEntity order = orderOptional.get();
        List<OrderDetailEntity> details = orderDetailService.findByOrder(order);
        List<OrderDetailDTO> detailDTOs = details.stream()
                .map(detail -> new OrderDetailDTO(
                        detail.getId(),
                        detail.getProduct().getName(),
                        detail.getProduct().getImage(),
                        detail.getQty(),
                        detail.getPrice()))
                .collect(Collectors.toList());
        double totalAmount = details.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQty())
                .sum();
        Date orderDate = details.isEmpty() ? new Date() : details.get(0).getOrderDate();

        OrderDTO orderDTO = new OrderDTO(
                order.getId(),
                order.getFullname(),
                order.getAddress(),
                order.getPhone(),
                totalAmount,
                orderDate);
        orderDTO.setOrderDetails(detailDTOs);
        response.put("order", orderDTO);
        return ResponseEntity.ok(response);
    }
} 