package me.huynhducphu.ping_me.repository.jpa.auth;

import me.huynhducphu.ping_me.model.User;
import me.huynhducphu.ping_me.model.constant.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Admin 8/3/2025
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> getUserByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = 'ONLINE' WHERE u.id = :userId")
    void connect(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = 'OFFLINE' WHERE u.id = :userId")
    void disconnect(@Param("userId") Long userId);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.accountStatus = :accountStatus")
    Page<User> findByAccountStatus(@Param("accountStatus") AccountStatus accountStatus, Pageable pageable);
}
