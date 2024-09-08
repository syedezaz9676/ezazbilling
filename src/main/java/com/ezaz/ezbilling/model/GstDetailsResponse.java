package com.ezaz.ezbilling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;



@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GstDetailsResponse {
    private boolean flag;
    private String message;
    private Data data;


    @Setter
    @Getter
    @AllArgsConstructor
    public static class Data {
        private String lgnm;
        private String stj;
        private String dty;
        private String gstin;
        private List<String> nba;
        private String rgdt;
        private String ctb;
        private Pradr pradr;
        private String sts;
        private String tradeNam;
        @Setter
        @Getter
        @AllArgsConstructor
        public static class Pradr {
            private String adr;
            private Addr addr;


            @Setter
            @Getter
            @AllArgsConstructor
            public static class Addr {
                private String flno;
                private String loc;
                private String pncd;
                private String bnm;
                private String stcd;
                private String dst;
                private String st;
            }
        }

        // Getters and setters...
    }
}
