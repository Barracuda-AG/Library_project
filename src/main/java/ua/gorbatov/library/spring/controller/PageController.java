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

/**
 * The {@code PageController} class is used for access control operations for main pages
 *
 * @author Oleksandr Gorbatov
 */
@Controller
public class PageController {

    private static Logger logger = LoggerFactory.getLogger(PageController.class);

    private UserService userService;

    @Autowired
    public PageController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Method provide get mapping to login
     * @return login page
     */
    @GetMapping("/login")
    public String login() {
        logger.info("Login page visited");
        return "login";
    }
    /**
     * Method provide pos mapping to login
     * @return welcome page
     */
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/login")
    public String getLogin() {
        logger.info("Login successul");
        return "/welcome";
    }
    /**
     * Method provide get mapping to registration
     * @return registration page
     */
    @GetMapping("/registration")
    public String registration(Model model)
    {
        model.addAttribute("userDTO", new UserDTO());
        logger.info("Registration page visited");
        return "/registration";
    }

    /**
     * Method provide post mapping to registration
     * @return login page
     */
    @PostMapping("/registration")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String setRegistration(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return "/registration";
        userService.createUser(userService.createUserFromDTO(userDTO));
        logger.warn("Registered user: " + userDTO.getEmail());
        return "/login";
    }

    /**
     * Method provide get mapping to welcome page
     * @return welcome page
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "/welcome";
    }
    /**
     * Method provide post mapping to welcome page
     * @return welcome page
     */
    @PostMapping("/welcome")
    public String welcomePost() {
        return "/welcome";
    }
    @GetMapping("/accessDenied")
    public String access(){
        return "/accessDenied";
    }
}
