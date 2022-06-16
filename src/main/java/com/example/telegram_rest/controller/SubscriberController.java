package com.example.telegram_rest.controller;

import com.example.telegram_rest.dto.CurrencyType;
import com.example.telegram_rest.entity.CurrencyEntity;
import com.example.telegram_rest.service.ISubscribersService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/subscribers")
@AllArgsConstructor
public class SubscriberController {

    private final ISubscribersService service;

    @GetMapping("/{chatId}/{base}")
    public ResponseEntity<List<CurrencyEntity>> subscribeIfNewAndGetActual(
            @PathVariable("chatId") String chatId,
            @PathVariable("base") CurrencyType base) {
        return ResponseEntity.ok(service.subscribeIfNewAndReturnActual(chatId, base));
    }

    // every 15 minutes
    @Scheduled(cron = "0 */15 * * * ?")
    public void sendMsgToUserIfUpdatesExist() throws IOException {
        service.sendMsgToUsersIfUpdateExist();
    }
}
