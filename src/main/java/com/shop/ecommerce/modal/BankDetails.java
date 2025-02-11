package com.shop.ecommerce.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
public class BankDetails extends BaseEntity {

    private String accountNumber;
    private String accountHolderName;
    //    private String bankName;
    private String code;

}
