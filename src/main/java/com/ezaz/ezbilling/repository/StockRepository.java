package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.StockDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository {
    void updateStock(StockDetails stockDetails);
    void updateBillItemInStock(StockDetails stockDetails);
}
