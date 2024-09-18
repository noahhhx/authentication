package com.noah.db.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Document
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;
    @NonNull
    @Indexed(unique = true)
    private String username;
    @NonNull
    private String password;
    @NonNull
    private LocalDateTime createdAt;
    private Collection<GrantedAuthority> authorities;
    @Getter
    private Integer failedAttempts;
    private Boolean accountLocked;
    private Boolean credentialsExpired;
    private Boolean enabled;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            return Collections.emptyList();
        }
        return authorities;
    }

    @Override
    public @NonNull String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (accountLocked == null) {
            return true;
        }
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (credentialsExpired == null) {
            return true;
        }
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        if (enabled == null) {
            return true;
        }
        return enabled;
    }

}
