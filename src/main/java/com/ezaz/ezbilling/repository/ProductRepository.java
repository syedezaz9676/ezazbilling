package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.ProductDetails;
import com.ezaz.ezbilling.model.ProductNames;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<ProductDetails, String > {

    List<ProductDetails> findAllByDgst(String dgst);

    List<ProductNames> findAllByPcom(String pcom);
    ProductDetails findByPname(String pname);

}
