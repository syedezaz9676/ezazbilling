package com.ezaz.ezbilling.repository.impl;

import com.ezaz.ezbilling.model.BillDetails;
import com.ezaz.ezbilling.repository.BillingRepositry;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BillingRepositryImpl  implements BillingRepositry {


    @Override
    public String getMaxBillNo(String dgst) {
        return null;
    }
}

