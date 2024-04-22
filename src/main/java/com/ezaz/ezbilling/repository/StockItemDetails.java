package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.ProductDetails;
import com.ezaz.ezbilling.model.StockDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockItemDetails extends MongoRepository<StockDetails, String > {

    StockDetails findByPid(String productId);
    List<StockDetails> findByDgst(String dgst);



}
