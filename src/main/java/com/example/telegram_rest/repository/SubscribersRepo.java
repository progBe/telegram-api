package com.example.telegram_rest.repository;

import com.example.telegram_rest.entity.Subscribers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribersRepo extends JpaRepository<Subscribers, Long> {
    Optional<Subscribers> findByChatId(String chatId);

    List<Subscribers> findAllByEnabled(boolean enabled);

}
