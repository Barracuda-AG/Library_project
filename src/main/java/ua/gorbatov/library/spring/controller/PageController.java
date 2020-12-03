package ua.gorbatov.library.spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.dto.UserDTO;
import ua.gorbatov.library.spring.service.UserService;

import javax.validation.Valid;

@Controller
public class PageController {

    private static Logger logger = LoggerFactory.getLogger(PageController.class);

    private UserService userService;

    @Autowired
    public PageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        logger.info("Login page visited");
        return "login";
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/login")
    public String getLogin() {
        logger.info("Login successul");
        return "/welcome";
    }

    @GetMapping("/registration")
    public String registration(Model model)
    {
        model.addAttribute("userDTO", new UserDTO());
        logger.info("Registration page visited");
        return "/registration";
    }

    @PostMapping("/registration")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String setRegistration(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return "/registration";
        userService.createUser(userService.createUserFromDTO(userDTO));
        logger.warn("Registered user: " + userDTO.getEmail());
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
