package com.TimeLuxWatchBE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TimeLuxWatchBE.entity.OrderEntity;
import com.TimeLuxWatchBE.entity.UserEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
	List<OrderEntity> findByUser(UserEntity user);

	/**
     * Tìm top 10 khách hàng chi tiêu nhiều nhất
     * @param year Năm (có thể null)
     * @param month Tháng (có thể null)
     * @return Danh sách các object chứa tên khách hàng, tổng tiền, ngày đầu tiên mua hàng và ngày cuối cùng mua hàng
     */
    @Query("SELECT u.fullName, SUM(od.qty * od.price), MIN(od.orderDate), MAX(od.orderDate) " +
           "FROM OrderEntity o " +
           "JOIN o.user u " +
           "JOIN OrderDetailEntity od ON o.id = od.order.id " +
           "WHERE (:year IS NULL OR YEAR(od.orderDate) = :year) " +
           "AND (:month IS NULL OR MONTH(od.orderDate) = :month) " +
           "GROUP BY u.id, u.fullName " +
           "ORDER BY SUM(od.qty * od.price) DESC")
    List<Object[]> findTop10VipCustomers(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * Tìm danh sách các năm riêng biệt có trong đơn hàng
     * @return Danh sách các năm
     */
    @Query("SELECT DISTINCT YEAR(od.orderDate) FROM OrderDetailEntity od ORDER BY YEAR(od.orderDate) DESC")
    List<Integer> findDistinctYears();
}
