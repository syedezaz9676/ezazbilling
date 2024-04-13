package com.ezaz.ezbilling.repository;

import com.ezaz.ezbilling.model.BillAggregationResult;
import com.ezaz.ezbilling.model.BillNo;
import com.ezaz.ezbilling.model.BillingDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface BillItemsRepository extends MongoRepository<BillingDetails, String> {
    List<BillingDetails> findByBno(String invoiceNo);

    void deleteByBno(String bno);
    @Query("{ 'cno' : ?0, 'billing_date' : { $gte: ?1, $lte: ?2 } }, { 'bno': 1 }")
    List<BillNo> findBnoByCnoAndBillingDateBetween(String cno, String startDate, String endDate);

    List<BillingDetails> findByCno(int cno);

    @Query(value = "{'bno': ?0}")
    List<BillAggregationResult> sumAmountByBnoGroupByProductGstAndBillingDate(String bno);

}
