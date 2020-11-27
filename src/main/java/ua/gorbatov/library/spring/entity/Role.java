package ua.gorbatov.library.spring.entity;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("admin"), USER("user"), LIBRARIAN("librarian");

    private String name;

    Role(String name){
        this.name = name;
    }
}
