package com.ezaz.ezbilling.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class ConversionConfig {

    @Bean
    public Converter<java.util.Date, java.sql.Date> dateConverter() {
        return new DateConverter();
    }
}
