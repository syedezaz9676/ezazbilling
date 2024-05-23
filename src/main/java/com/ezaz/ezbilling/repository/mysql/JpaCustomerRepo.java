package com.ezaz.ezbilling.repository.mysql;

import com.ezaz.ezbilling.model.mysql.JpaCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCustomerRepo extends JpaRepository<JpaCustomer,Integer> {
}
