package com.example.telegram_rest.service;

import com.example.telegram_rest.dto.CurrencyType;
import com.example.telegram_rest.entity.CurrencyDto;
import com.example.telegram_rest.entity.CurrencyEntity;

import java.io.IOException;
import java.util.List;

public interface ICurrencyService {
    List<CurrencyEntity> getActualCurrencies(CurrencyType base);

    List<CurrencyEntity> getUpdateForNowFromDb();

    List<CurrencyDto> getUpdateForNowFromExternalService() throws IOException;
}
