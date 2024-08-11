package com.ezaz.ezbilling.model;
import com.ezaz.ezbilling.configuration.LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "soldstock")
public class BillingDetails {
    @Id
    private String id;
    private String cno;
    private String bno;
    private String product_name;
    private Integer product_gst;
    private Integer qty;
    private Double amount;
    private String billing_date;
    private String free;
    private String hsn_code;
    private String unites_per;
    private Integer mrp;
    private String product_company;
    private Integer disc;
    private Double amount_after_disc;
    private String dgst;
    private Double gross_amount;
    private Double rate;
    private Double gstamount;
    private Integer cess;
    private Double cessAmount;
    private Double total_tax;
    private Double total_gross;
    private Double total_amount;
    private Double netAmount;


    public String getBilling_date() {
        if (billing_date != null) {
            return billing_date.substring(0,10);
        }
        return null;
    }
}

