package com.ezaz.ezbilling.model.mysql;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "soldstock")
//@IdClass(SoldStockId.class)
public class JpaSoldStock implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "cno")
    private Long cno;


    @Column(name = "bno")
    private String bno;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_gst")
    private Integer productGst;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "billing_date")
    private LocalDate billingDate;

    @Column(name = "free")
    private Integer free;

    @Column(name = "hsn_code")
    private String hsnCode;

    @Column(name = "unites_per")
    private String unitesPer;

    @Column(name = "mrp")
    private Integer mrp;

    @Column(name = "product_company")
    private String productCompany;

    @Column(name = "disc")
    private Integer disc;

    @Column(name = "amount_after_disc")
    private Double amountAfterDisc;

}
