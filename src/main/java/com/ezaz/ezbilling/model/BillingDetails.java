package com.ezaz.ezbilling.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import java.sql.Date;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "soldstock")
public class BillingDetails {
    private String cno;
    private String bno;
    private String product_name;
    private Integer product_gst;
    private Integer qty;
    private Float amount;
    private Date billing_date;
    private String free;
    private String hsn_code;
    private String unites_per;
    private Integer mrp;
    private String product_company;
    private Integer disc;
    private Integer Amount_after_disc;
    private String dgst;
}

