package com.shop.ecommerce.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private Double discountPercentage;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private Double minimumOrderValue;

    private Boolean isActive = true;

    @ManyToMany(mappedBy = "usedCoupons")
    private Set<User> usedByUsers = new HashSet<>();
}
