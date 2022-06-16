package com.example.telegram_rest.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyDto {

    private String base;

    private String date;

    private HashMap<String, String> rates;

}
