package com.example.telegram_rest.service;

import com.example.telegram_rest.dto.CurrencyType;
import com.example.telegram_rest.entity.CurrencyEntity;

import java.io.IOException;
import java.util.List;

public interface ISubscribersService {
    List<CurrencyEntity> subscribeIfNewAndReturnActual(String chatId, CurrencyType base);

    void sendMsgToUsersIfUpdateExist() throws IOException;
}
