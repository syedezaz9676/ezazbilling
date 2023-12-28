package com.ezaz.ezbilling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "companysdetails")
public class CompanyDetails {
    @Id
    private String id;
    private String name;
    private List<Integer> gstPercentage;
    private String dgst;

}
