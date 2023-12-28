package com.ezaz.ezbilling.Bo;

import com.ezaz.ezbilling.model.*;

import java.util.List;
import java.util.Optional;


public interface EzbillingBo {

    public void saveCustomerToDB(Customer customer);

    public List<GstCodeDetails> getGstCodeDetails();

    public void saveCompanyDetails(CompanyDetails companyDetails);

    public List<CompanyDetails> getCompanyDetails(String id);

    public void saveProductDetails(ProductDetails productDetails);

    public List<ProductDetails> getProductsDetails(String id);

    public List<ProductNames> getProductNames(String id);

    public Optional<ProductDetails> getProductDetailsByID(String id);

    public List<CompanyNames> getCompanyNames(String id);
    public Optional<CompanyDetails> getCompanyDetailsByID(String id);

    Optional<Customer> getCustomerDetailsByID(String id);

    public List<CustomerNames> getCustomerNames(String id);

    public List<ProductNames> getProductDetailsByCompany(String comapanyName);

    public List<Customer> getCustomerByDgst(String dgst);

    }


