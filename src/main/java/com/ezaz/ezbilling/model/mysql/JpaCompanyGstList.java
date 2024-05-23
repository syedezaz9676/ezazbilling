package com.ezaz.ezbilling.model.mysql;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;


@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JpaCompanyGstList {
    @Id
    private Integer cid;
    private String cname;
    private List<Integer> gstList;
}
