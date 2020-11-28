package ua.gorbatov.library.spring.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    ROLE_ADMIN, ROLE_USER, ROLE_LIBRARIAN;

    @Override
    public String getAuthority() {
        return name();
    }
}
