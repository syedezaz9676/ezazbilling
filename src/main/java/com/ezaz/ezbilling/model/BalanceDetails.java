package com.ezaz.ezbilling.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "balanceAmount")
public class BalanceDetails {
    @Id
    private String id;
    private String cno;
    private String cname;
    private Double creditBalance;
    private Double debitBalance;
    private Double totalBalance;
    private String lastUpdatedDate;
    private Double lastUpdateAmount;
    private String dgst;
    private String type;
    private String reason;
}
