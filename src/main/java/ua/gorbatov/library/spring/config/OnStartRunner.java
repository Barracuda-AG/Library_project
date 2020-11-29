package ua.gorbatov.library.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.UserService;

@Component
public class OnStartRunner implements CommandLineRunner {
    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
//        User admin = User.builder()
//                .password(bCryptPasswordEncoder().encode("admin"))
//                .email("admin@mail.com")
//                .firstName("admin")
//                .lastName("admin")
//                .build();
//        userService.createAdmin(admin);
//
//        User librarian = User.builder()
//                .password(bCryptPasswordEncoder().encode("gorbatov"))
//                .email("gorbatov2010@yahoo.com")
//                .firstName("alex")
//                .lastName("gorbatov")
//                .build();
//        userService.createLibrarian(librarian);
//        User user = User.builder()
//                .password(bCryptPasswordEncoder().encode("user"))
//                .email("user@mail.com")
//                .firstName("user")
//                .lastName("user")
//                .build();
//        userService.createUser(user);
    }
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
