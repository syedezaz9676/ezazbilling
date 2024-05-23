package com.ezaz.ezbilling.repository.mysql;

import com.ezaz.ezbilling.model.mysql.JpaBillsAmount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBillsAmountRepo extends JpaRepository<JpaBillsAmount,Integer> {
}
