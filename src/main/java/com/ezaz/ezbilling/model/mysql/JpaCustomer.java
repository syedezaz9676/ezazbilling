package com.ezaz.ezbilling.model.mysql;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "custable")
public class JpaCustomer {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cno", nullable = false)
    private int cno;

    @Column(name = "cname", nullable = false)
    private String cname;

    @Column(name = "ctno")
    private String ctno;

    @Column(name = "cpno")
    private String cpno;

    @Column(name = "cadd")
    private String cadd;

    @Column(name = "isigst")
    private String isigst;

    @Column(name = "supplyplace")
    private String supplyplace;

    @Column(name = "legal_name")
    private String legal_name;
}
