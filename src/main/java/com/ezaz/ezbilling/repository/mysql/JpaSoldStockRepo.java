package com.ezaz.ezbilling.repository.mysql;


import com.ezaz.ezbilling.model.mysql.JpaSoldStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSoldStockRepo extends JpaRepository<JpaSoldStock,String> {
    @Query("SELECT s FROM JpaSoldStock s")
    List<JpaSoldStock> findAllDistinct();
}
