package com.example.telegrambot.bot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "authorization_codes")
public class AuthorizationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuthorizationCode user = (AuthorizationCode) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
