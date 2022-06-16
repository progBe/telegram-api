package com.example.telegram_rest.service.impl;

import com.example.telegram_rest.dto.CurrencyType;
import com.example.telegram_rest.entity.CurrencyDto;
import com.example.telegram_rest.entity.CurrencyEntity;
import com.example.telegram_rest.repository.CurrencyRepo;
import com.example.telegram_rest.service.ICurrencyService;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements ICurrencyService {

    private final String apiKey = "ysP7IFXEww9nOwDVLrrweua9Oi6opPr6";
    private final String baseUrl = "https://api.apilayer.com/fixer/";
    private final Integer actualDays = 10;
    private final List<String> rate = Collections.singletonList(CurrencyType.KZT.toString());
    private final List<CurrencyType> availableCurrencies = List.of(CurrencyType.USD, CurrencyType.RUB, CurrencyType.EUR);

    private final CurrencyRepo currencyRepo;

    @Override
    public List<CurrencyEntity> getActualCurrencies(CurrencyType base) {
        LocalDateTime start = LocalDateTime.of(LocalDate.from(LocalDateTime.now().minusDays(10)), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
        if (isExistActualByBase(base, start, end)) {
            return currencyRepo.getAllBetweenDates(start, end, base);
        }
        return actualizeCurrenciesByBase(base);
    }

    @Override
    public List<CurrencyEntity> getUpdateForNowFromDb() {
        return currencyRepo.getForDate(LocalDateTime.now());
    }

    @Override
    public List<CurrencyDto> getUpdateForNowFromExternalService() throws IOException {
        List<CurrencyDto> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDate = formatter.format(LocalDateTime.now());
        for (CurrencyType availableCurrency : availableCurrencies) {
            CurrencyDto currency = getCurrency(rate, availableCurrency, todayDate);
            list.add(currency);
        }
        return list;
    }

    private List<CurrencyEntity> actualizeCurrenciesByBase(CurrencyType base) {
        removeCurrenciesByBase(base);

        List<String> dates = generateFormattedDateStringForDays(actualDays);
        List<CurrencyEntity> newCurrencies = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String date : dates) {
            try {
                CurrencyDto currency = getCurrency(rate, base, date);
                LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
                newCurrencies.add(
                        CurrencyEntity.builder()
                                .base(base.toString())
                                .date(dateTime)
                                .kztRate(Double.parseDouble(currency.getRates().get(CurrencyType.KZT.toString())))
                                .build()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        currencyRepo.saveAll(newCurrencies);
        return newCurrencies;
    }

    private void removeCurrenciesByBase(CurrencyType base) {
        currencyRepo.deleteAllByBase(base.toString());
    }

    private boolean isExistActualByBase(CurrencyType base, LocalDateTime start, LocalDateTime end) {
        Long count = currencyRepo.countAllBetweenDates(start, end, base);
        return count == actualDays.longValue();
    }

    private CurrencyDto getCurrency(List<String> symbols, CurrencyType base, String date) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl + date + "?symbols=" + String.join(",", symbols) + "&base=" + base.toString())
                .addHeader("apikey", apiKey)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String res = (response.body().string());
        Gson gson = new Gson();
        return gson.fromJson(res, CurrencyDto.class);
    }

    private List<String> generateFormattedDateStringForDays(int count) {
        List<String> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -count);

        for (int i = 0; i < count; i++) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            data.add(sdf.format(cal.getTime()));
        }
        return data;
    }

}
