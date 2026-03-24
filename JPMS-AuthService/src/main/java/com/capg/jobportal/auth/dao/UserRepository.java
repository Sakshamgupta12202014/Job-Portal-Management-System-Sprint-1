package com.capg.jobportal.auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capg.jobportal.auth.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {
	
	Optional<User> findByEmail(String email);
	 
    boolean existsByEmail(String email);
 
    Optional<User> findByRefreshToken(String refreshToken);
}
