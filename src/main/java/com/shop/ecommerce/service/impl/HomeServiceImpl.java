package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.domain.HomeCategorySection;
import com.shop.ecommerce.modal.Deal;
import com.shop.ecommerce.modal.Home;
import com.shop.ecommerce.modal.HomeCategory;
import com.shop.ecommerce.repository.DealRepository;
import com.shop.ecommerce.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final DealRepository dealRepository;
    @Override
    public Home createHomePageData(List<HomeCategory> categories) {
        List<HomeCategory> grid = getCategoriesHome(categories, HomeCategorySection.GRID);
        List<HomeCategory> shopByCategory = getCategoriesHome(categories, HomeCategorySection.SHOP_BY_CATEGORY);
        List<HomeCategory> electricCategories = getCategoriesHome(categories, HomeCategorySection.ELECTRIC_CATEGORIES);
        List<HomeCategory> dealCategories = getCategoriesHome(categories, HomeCategorySection.DEALS);

        List<Deal> createdDeal;
        if (dealRepository.findAll().isEmpty()) {
            List<Deal> deals = categories.stream().filter(category -> category.getSection().equals(HomeCategorySection.DEALS))
                    .map(category -> new Deal(null, 10, category)).toList();
            createdDeal = dealRepository.saveAll(deals);
        } else createdDeal = dealRepository.findAll();

        Home home = new Home();
        home.setGrid(grid);
        home.setShopByCategory(shopByCategory);
        home.setElectricCategories(electricCategories);
        home.setDeals(createdDeal);
        home.setDealCategories(dealCategories);

        return home;
    }

    public List<HomeCategory> getCategoriesHome(List<HomeCategory> categories, HomeCategorySection homeCategorySection) {
        return categories.stream().filter(category -> category.getSection().equals(homeCategorySection)).toList();
    }
}
