package com.ezaz.ezbilling.repository;


import com.ezaz.ezbilling.model.ProductDetails;
import com.ezaz.ezbilling.model.ProductNames;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailsRepository {

    List<ProductNames> getProductNames(String id);
    public List<ProductDetails> getProductDetailsByComapany(String companyName);
    public void addDgst(String dgst);
}
