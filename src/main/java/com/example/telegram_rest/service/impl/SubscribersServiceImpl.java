package com.example.telegram_rest.service.impl;

import com.example.telegram_rest.dto.CurrencyType;
import com.example.telegram_rest.entity.CurrencyDto;
import com.example.telegram_rest.entity.CurrencyEntity;
import com.example.telegram_rest.entity.Subscribers;
import com.example.telegram_rest.repository.SubscribersRepo;
import com.example.telegram_rest.service.ICurrencyService;
import com.example.telegram_rest.service.ISubscribersService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubscribersServiceImpl implements ISubscribersService {

    private final ICurrencyService currencyService;
    private final SubscribersRepo subscribersRepo;
    private final String botToken = "5458291387:AAErZIZKBRShUyAcAgxVGYW_UrAJhJ6QMjE";

    @Override
    public List<CurrencyEntity> subscribeIfNewAndReturnActual(String chatId, CurrencyType base) {
        subscribeIfNotExist(chatId);

        return currencyService.getActualCurrencies(base);
    }

    @Override
    public void sendMsgToUsersIfUpdateExist() throws IOException {
        List<Subscribers> subscribers = subscribersRepo.findAllByEnabled(Boolean.TRUE);
        List<CurrencyEntity> listFromDb = currencyService.getUpdateForNowFromDb();
        List<CurrencyDto> listFromExternalService = currencyService.getUpdateForNowFromExternalService();
        StringBuilder updateMsg = new StringBuilder();
        for (CurrencyDto externalCurrency : listFromExternalService) {
            CurrencyEntity fromDb = listFromDb.stream().filter(currencyEntity -> currencyEntity.getBase().equals(externalCurrency.getBase())).findFirst().get();
            double kztRateDb = fromDb.getKztRate();
            double kztRateExternal = Double.parseDouble(externalCurrency.getRates().get(CurrencyType.KZT.toString()));
            double diffTenPercent = kztRateDb / 10;
            if (kztRateDb - kztRateExternal >= diffTenPercent || kztRateExternal - kztRateDb <= diffTenPercent) {
                updateMsg.append("BASE:").append(externalCurrency.getBase()).append("\n")
                        .append("RATE: ").append(kztRateExternal);
            }
        }
        sendMessageToSubscribers(subscribers,updateMsg.toString());
    }

    private void sendMessageToSubscribers(List<Subscribers> subscribers, String msg) throws IOException {
        OkHttpClient client = new OkHttpClient();
        for (Subscribers subscriber : subscribers) {
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + subscriber.getChatId() + "&text=" + msg)
                    .method("GET", null)
                    .build();
            client.newCall(request).execute();
        }
    }

    private void subscribeIfNotExist(String chatId) {
        Optional<Subscribers> subscribersOptional = subscribersRepo.findByChatId(chatId);
        if (subscribersOptional.isPresent()) {
            subscribersOptional.get().setEnabled(true);
            subscribersRepo.save(subscribersOptional.get());
        } else {
            Subscribers newSubscriber = Subscribers.builder()
                    .chatId(chatId)
                    .enabled(true)
                    .build();
            subscribersRepo.save(newSubscriber);
        }
    }
}
