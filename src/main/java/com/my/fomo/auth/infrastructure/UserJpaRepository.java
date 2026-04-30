package com.my.fomo.auth.infrastructure;

import com.my.fomo.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface UserJpaRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
}
