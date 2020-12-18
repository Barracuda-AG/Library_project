package ua.gorbatov.library.spring.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
/**
 * The {@code Role} implements {@code GrantedAuthority} interface is an enum
 * representing the roles available to user - ADMIN, USER, LIBRARIAN
 * @author Oleksandr Gorbatov
 */
@Getter
public enum Role implements GrantedAuthority {
    /**
     * The instances for Roles
     */
    ROLE_ADMIN, ROLE_USER, ROLE_LIBRARIAN;

    @Override
    public String getAuthority() {
        return name();
    }
}
