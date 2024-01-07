package com.mintyn.assessment.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "card_verification")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;
    private String scheme;
    private String type;
    private String bank;
    private int hitCount;
}