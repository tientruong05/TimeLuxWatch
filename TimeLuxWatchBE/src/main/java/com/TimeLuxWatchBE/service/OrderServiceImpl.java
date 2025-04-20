package com.TimeLuxWatchBE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.TimeLuxWatchBE.entity.OrderEntity;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.repository.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<OrderEntity> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> order != null)
                .collect(Collectors.toList());
    }

    @Override
    public void addOrder(OrderEntity order) {
        Optional.ofNullable(order).ifPresent(orderRepository::save);
    }

    @Override
    public void updateOrder(OrderEntity order) {
        Optional.ofNullable(order).ifPresent(orderRepository::save);
    }

    @Override
    public void removeOrder(int id) {
        Optional.of(id).filter(i -> i > 0).ifPresent(orderRepository::deleteById);
    }

    @Override
    public List<Object[]> getTop10VipCustomers(Integer year, Integer month) {
        return orderRepository.findTop10VipCustomers(year, month);
    }

    @Override
    public List<Integer> getAvailableYears() {
        return orderRepository.findDistinctYears().stream()
                .filter(year -> year != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderEntity> findByUser(UserEntity user) {
        return orderRepository.findByUser(user).stream()
                .filter(order -> order != null)
                .collect(Collectors.toList());
    }

    @Override
    public OrderEntity save(OrderEntity order) {
        return Optional.ofNullable(order)
                .map(orderRepository::save)
                .orElse(null);
    }

    @Override
    public OrderEntity findById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<OrderEntity> findAll() {
        return orderRepository.findAll().stream()
                .filter(order -> order != null)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(int id) {
        Optional.of(id).filter(i -> i > 0).ifPresent(orderRepository::deleteById);
    }
}
