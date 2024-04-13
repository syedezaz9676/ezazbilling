package com.ezaz.ezbilling.configuration;

import org.springframework.core.convert.converter.Converter;

import java.sql.Date;
import java.util.Objects;

public class DateConverter implements Converter<java.util.Date, java.sql.Date> {

    @Override
    public Date convert(java.util.Date source) {
        return Objects.isNull(source) ? null : new Date(source.getTime());
    }
}

