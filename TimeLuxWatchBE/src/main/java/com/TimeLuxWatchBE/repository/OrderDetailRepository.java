package com.TimeLuxWatchBE.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TimeLuxWatchBE.entity.OrderDetailEntity;
import com.TimeLuxWatchBE.entity.OrderEntity;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Integer> {
	List<OrderDetailEntity> findByOrder(OrderEntity order);
	List<OrderDetailEntity> findByOrderUserId(int userId);
	Page<OrderDetailEntity> findByOrderUserId(int userId, Pageable pageable);
	Page<OrderDetailEntity> findByOrderIn(List<OrderEntity> orders, Pageable pageable);
}
