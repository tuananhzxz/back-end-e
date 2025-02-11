package com.shop.ecommerce.modal;

import com.shop.ecommerce.domain.User_Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
public class BusinessDetails extends BaseEntity {

    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private String businessAddress;
    private String logo;
    private String banner;

}
