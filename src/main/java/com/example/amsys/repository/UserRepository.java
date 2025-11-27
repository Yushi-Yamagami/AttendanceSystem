package com.example.amsys.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.amsys.model.User;
import com.example.amsys.model.User.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    
    /**
     * ロールでユーザーを検索
     * @param role ユーザーロール
     * @return ユーザーリスト
     */
    List<User> findByRoleOrderByUserIdAsc(UserRole role);

}