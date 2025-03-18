package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.config.JwtProvider;
import com.shop.ecommerce.domain.AccountStatus;
import com.shop.ecommerce.domain.User_Role;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Address;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.repository.AddressRepository;
import com.shop.ecommerce.repository.ProductRepository;
import com.shop.ecommerce.repository.SellerRepository;
import com.shop.ecommerce.service.SellerService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import com.shop.ecommerce.utils.UpdateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final MessageMultiUtils messageMultiUtils;
    private final ProductRepository productRepository;
    @Override
    public Seller getSellerProfile(String token) throws SellerException {
        String email = jwtProvider.getEmailFromToken(token);
        return getSellerByEmail(email);
    }

    @Override
    public Seller getSellerById(Long id) throws SellerException {
        return sellerRepository.findById(id).orElseThrow(() -> new SellerException(messageMultiUtils.getMessage("seller.not.found")));
    }

    @Override
    public Seller getSellerByEmail(String email) throws SellerException {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            throw new SellerException(messageMultiUtils.getMessage("seller.not.found"));
        }
        return seller;
    }

    @Override
    public List<Seller> getAllSellers(AccountStatus status) {
        return sellerRepository.findByAccountStatus(status);
    }

    @Override
    public Seller createSeller(Seller seller) throws SellerException {
        Seller sellerExists = sellerRepository.findByEmail(seller.getEmail());
        if (sellerExists != null) {
            throw new SellerException(messageMultiUtils.getMessage("seller.exists"));
        }

        Address address = addressRepository.save(seller.getPickupAddress());

        Seller newSeller = new Seller();
        UpdateUtils.updateNonNullFields(newSeller, seller);
        newSeller.setPassword(passwordEncoder.encode(seller.getPassword()));
        newSeller.setPickupAddress(address);
        newSeller.setRole(User_Role.ROLE_SELLER);

        return sellerRepository.save(newSeller);
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) throws SellerException {
        Seller sellerExists = this.getSellerById(id);
        UpdateUtils.updateNonNullFields(sellerExists, seller);
        if (!seller.getIsEmailVerified()) sellerExists.setIsEmailVerified(true);
        if (seller.getPassword() != null) sellerExists.setPassword(passwordEncoder.encode(seller.getPassword()));
        return sellerRepository.save(sellerExists);
    }

    @Override
    public void deleteSeller(Long id) throws SellerException {
        Seller seller = this.getSellerById(id);
        sellerRepository.delete(seller);
    }

    @Override
    public Seller verifyEmail(String email, String otp) throws SellerException {
        Seller seller = this.getSellerByEmail(email);
        seller.setIsEmailVerified(true);
        return sellerRepository.save(seller);
    }

    @Override
    public Seller updateAccountStatus(Long id, AccountStatus status) throws SellerException {
        Seller seller = this.getSellerById(id);
        seller.setAccountStatus(status);
        return sellerRepository.save(seller);
    }

    @Override
    public String updateBanner(String token, String banner) throws SellerException {
        Seller seller = getSellerProfile(token);
        if (banner == null) throw new SellerException("Banner cannot be null");
        if (seller == null) throw new SellerException("Seller not found");
        String img = banner.substring(11, banner.length() - 2);
        seller.getBusinessDetails().setBanner(img);
        sellerRepository.save(seller);
        return "Banner updated successfully";
    }

    @Override
    public Seller getSellerByProductId(Long productId) throws SellerException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new SellerException("Product not found"));
        if (product.getSeller() == null) throw new SellerException("Seller not found");
        return product.getSeller();
    }
}
