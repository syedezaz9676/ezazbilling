package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.CompanyNames;

import java.util.List;


public interface CompanyDetailsRepository {


    public List<CompanyNames> getCompanyNames(String id);


}
