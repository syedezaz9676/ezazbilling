package com.ezaz.ezbilling.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GstDetailsRequest {
    String dgst;
    Date fromDate;
    Date toDate;
}
