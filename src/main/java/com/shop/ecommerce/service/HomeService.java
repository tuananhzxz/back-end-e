package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.Home;
import com.shop.ecommerce.modal.HomeCategory;

import java.util.List;

public interface HomeService {

    Home createHomePageData(List<HomeCategory> categories);
}
