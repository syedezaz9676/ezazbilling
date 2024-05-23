package com.ezaz.ezbilling.repository.mysql;

import com.ezaz.ezbilling.model.mysql.JpaCompany;
import com.ezaz.ezbilling.model.mysql.JpaCompanyGstList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCompanyRepo  extends JpaRepository<JpaCompany,Integer> {
    @Query(value = "SELECT c.cid AS cid, c.cname AS cname, GROUP_CONCAT(c.gst) AS gstList " +
            "FROM comdetails c GROUP BY c.cname", nativeQuery = true)
    List<Object[]> findCompanyGstGroupedByCname();
}
