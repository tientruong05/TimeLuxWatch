package com.TimeLuxWatchBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TimeLuxWatchBE.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUsername(@Param("username") String username);

    // Truy vấn này sẽ trả về dữ liệu thô để xử lý trong service layer
    @Query("SELECT u.fullName, u.email, u.phone, SUM(od.qty * od.price) " +
            "FROM UserEntity u " +
            "JOIN OrderEntity o ON u.id = o.user.id " +
            "JOIN OrderDetailEntity od ON o.id = od.order.id " +
            "GROUP BY u.id, u.fullName, u.email, u.phone " +
            "ORDER BY SUM(od.qty * od.price) DESC")
    List<Object[]> findTop10VipCustomers();

    List<UserEntity> findByFullNameContaining(@Param("fullName") String fullName);

    @Query("SELECT u FROM UserEntity u WHERE u.role = :role")
    List<UserEntity> findByRole(@Param("role") int role);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    UserEntity findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.password = :password")
    UserEntity findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByUsername(@Param("username") String username);

    boolean existsByPhone(@Param("phone") String phone);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.email = :email")
    UserEntity findByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    @Query("SELECT COUNT(u) FROM UserEntity u")
    long countUsers();

    @Override
    <S extends UserEntity> S saveAndFlush(S user);

    void deleteByEmail(@Param("email") String email);
}
