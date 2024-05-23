package com.ezaz.ezbilling.model.mysql;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bills_amount")
public class JpaBillsAmount {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bno")
    private String bno;

    @Column(name = "cno")
    private Long cno;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount")
    private Double amount;

}
