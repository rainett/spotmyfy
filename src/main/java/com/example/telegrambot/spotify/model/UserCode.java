package com.example.telegrambot.spotify.model;

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
@Table(name="user_codes")
public class UserCode {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    @Column(unique = true)
    private Long userId;

    private String accessToken;

    private String refreshToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserCode userCode = (UserCode) o;
        return id != null && Objects.equals(id, userCode.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
