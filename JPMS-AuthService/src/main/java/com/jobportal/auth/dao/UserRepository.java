package com.jobportal.auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import com.jobportal.auth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {
	
	Optional<User> findByEmail(String email);
	 
    boolean existsByEmail(String email);
 
    Optional<User> findByRefreshToken(String refreshToken);
}
