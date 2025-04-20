package com.TimeLuxWatchBE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.TimeLuxWatchBE.entity.OrderDetailEntity;
import com.TimeLuxWatchBE.entity.OrderEntity;
import com.TimeLuxWatchBE.repository.OrderDetailRepository;
import com.TimeLuxWatchBE.dto.ProductSummaryDTO;
import com.TimeLuxWatchBE.dto.ShoppingHistoryItemDTO;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public Optional<OrderDetailEntity> getOrderDetailById(int id) {
        return orderDetailRepository.findById(id);
    }

    @Override
    public List<OrderDetailEntity> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Override
    public void addOrderDetail(OrderDetailEntity orderDetail) {
        Optional.ofNullable(orderDetail).ifPresent(orderDetailRepository::save);
    }

    @Override
    public void updateOrderDetail(OrderDetailEntity orderDetail) {
        Optional.ofNullable(orderDetail).ifPresent(orderDetailRepository::save);
    }

    @Override
    public void removeOrderDetail(int id) {
        orderDetailRepository.findById(id).ifPresent(order -> orderDetailRepository.deleteById(id));
    }

    @Override
    public List<OrderDetailEntity> findByOrder(OrderEntity order) {
        return orderDetailRepository.findByOrder(order);
    }

    @Override
    public OrderDetailEntity save(OrderDetailEntity orderDetail) {
        return Optional.ofNullable(orderDetail)
                .map(orderDetailRepository::save)
                .orElse(null);
    }

    @Override
    public OrderDetailEntity findById(int id) {
        return orderDetailRepository.findById(id).orElse(null);
    }

    @Override
    public List<OrderDetailEntity> findAll() {
        return orderDetailRepository.findAll();
    }

    @Override
    public void deleteById(int id) {
        Optional.of(id).filter(i -> i > 0).ifPresent(orderDetailRepository::deleteById);
    }

    @Override
    public List<OrderDetailEntity> getOrderDetailsByUserId(int userId) {
        return orderDetailRepository.findByOrderUserId(userId);
    }

    @Override
    public Page<OrderDetailEntity> findByOrders(List<OrderEntity> orders, Pageable pageable) {
        return orderDetailRepository.findByOrderIn(orders, pageable);
    }

    @Override
    public Page<OrderDetailEntity> findByUserId(int userId, Pageable pageable) {
        return orderDetailRepository.findByOrderUserId(userId, pageable);
    }

    @Override
    public Page<ShoppingHistoryItemDTO> findHistoryByUserId(int userId, Pageable pageable) {
        Page<OrderDetailEntity> orderDetailsPage = orderDetailRepository.findByOrderUserId(userId, pageable);

        // Map the Page<Entity> to Page<DTO>
        return orderDetailsPage.map(this::mapToShoppingHistoryItemDTO);
    }

    // Helper method to map Entity to DTO
    private ShoppingHistoryItemDTO mapToShoppingHistoryItemDTO(OrderDetailEntity entity) {
        ProductSummaryDTO productDTO = new ProductSummaryDTO(
            entity.getProduct().getId(),
            entity.getProduct().getName(),
            entity.getProduct().getImage()
        );
        return new ShoppingHistoryItemDTO(
            entity.getId(),
            productDTO,
            entity.getQty(),
            entity.getOrderDate()
        );
    }
}
