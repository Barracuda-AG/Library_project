package ua.gorbatov.library.spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.dto.UserDTO;
import ua.gorbatov.library.spring.service.UserService;

@Slf4j
@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/login")
    public String getLogin() {
        return "/welcome";
    }

    @GetMapping("/registration")
    public String registration() {
        return "/registration";
    }

    @PostMapping("/registration")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String setRegistration(UserDTO userDTO) {
        userService.createUser(userService.createUserFromDTO(userDTO));
        return "/login";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "/welcome";
    }

    @PostMapping("/welcome")
    public String welcomePost() {
        return "/welcome";
    }
}
