package com.example.telegrambot.bot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_languages")
public class UserLanguage {

    @Id
    private Long userId;

    private String localeCode = Locale.US.toLanguageTag();

}
