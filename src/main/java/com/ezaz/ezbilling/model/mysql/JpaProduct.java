package com.ezaz.ezbilling.model.mysql;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class JpaProduct {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pid")
    private Long pid;

    @Column(name = "pname")
    private String pname;

    @Column(name = "mrp")
    private Float mrp;

    @Column(name = "rate")
    private Float rate;

    @Column(name = "pcom")
    private String pcom;

    @Column(name = "vatp")
    private Integer vatp;

    @Column(name = "Hsn_code")
    private String hsnCode;

    @Column(name = "unites_per")
    private String unitesPer;

    @Column(name = "no_of_unites")
    private Integer noOfUnites;

    @Column(name = "is_sp")
    private String isSp;

    @Column(name = "rel_prod")
    private String relProd;

    @Column(name = "description")
    private String description;

    @Column(name = "uqc")
    private String uqc;

}
