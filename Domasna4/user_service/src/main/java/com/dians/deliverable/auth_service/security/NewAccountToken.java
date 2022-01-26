package com.dians.deliverable.auth_service.security;
import com.dians.deliverable.auth_service.models.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table(name = "new_account_token")
public class NewAccountToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(
            nullable = false,
            name = "app_user_id"
    )
    private AppUser user;

    public NewAccountToken() {
    }

    public NewAccountToken(String token, AppUser user) {
        this.token = token;
        this.user = user;
    }
}