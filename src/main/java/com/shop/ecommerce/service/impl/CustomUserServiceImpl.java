package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.domain.User_Role;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.SellerRepository;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.service.AuthService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final MessageMultiUtils messageMultiUtils;
    private final String SELLER_PREFIX = "SELLER_";
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.startsWith(SELLER_PREFIX)) {
            User user = userRepository.findByEmail(username);
            if (user != null) {
                return customUserDetails(user.getEmail(), user.getPassword(), user.getRole());
            }
        } else {
            String usernameActual = username.substring(SELLER_PREFIX.length());
            Seller seller = sellerRepository.findByEmail(usernameActual);
            if (seller != null) {
                return customUserDetails(seller.getEmail(), seller.getPassword(), seller.getRole());
            }
        }

        throw new UsernameNotFoundException(messageMultiUtils.getMessage("user.not.found"));
    }

    private UserDetails customUserDetails(String email, String password, User_Role role) {
        if (role == null) role = User_Role.ROLE_CUSTOMER;

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        return new org.springframework.security.core.userdetails.User(email, password, authorities);
    }
}
