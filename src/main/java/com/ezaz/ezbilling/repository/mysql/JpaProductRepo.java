package com.ezaz.ezbilling.repository.mysql;


import com.ezaz.ezbilling.model.mysql.JpaProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepo extends JpaRepository<JpaProduct,String> {
}
