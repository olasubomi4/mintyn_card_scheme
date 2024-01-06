package com.mintyn.assessment.repository;

import com.mintyn.assessment.entity.CardVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CardVerificationRepository extends CrudRepository<CardVerification, Long> {
    CardVerification findByCardNumber(String cardNumber);
    Page<CardVerification> findAll( Pageable pageable);
}
