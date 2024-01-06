package com.mintyn.assessment.repository;

import com.mintyn.assessment.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {
	 Optional<User> findByUsername(String username);
}