package ua.gorbatov.library.spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.gorbatov.library.spring.dto.UserDTO;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.UserService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("login")
    public String login(){
        return "login";
    }
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("login")
    public String getLogin(){
       return "welcome";
    }
    @GetMapping("registration")
    public String registration(){
        return "registration";
    }
    @PostMapping("registration")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String setRegistration (UserDTO userDTO){
            userService.createUser(userService.createUserFromDTO(userDTO));
        return "login";
    }
    @GetMapping("welcome")
    public String welcome(){
        return "welcome";
    }
}
