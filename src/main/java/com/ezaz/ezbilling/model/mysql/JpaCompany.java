package com.ezaz.ezbilling.model.mysql;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comdetails")
public class JpaCompany {
    @Id
    private Long cid;
    private String cname;
    private Integer gst;
}
