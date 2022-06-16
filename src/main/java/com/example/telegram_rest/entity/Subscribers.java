package com.example.telegram_rest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "subscribers")
@Table()
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscribers {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "chat_id")
    private String chatId;
    private boolean enabled;
}
