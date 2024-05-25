package com.ezaz.ezbilling.model;


import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SavedBillandWayBillDetails {
    private List<BillingDetails> billingDetails;
    private List<WayBillDetails> wayBillDetails;
}
