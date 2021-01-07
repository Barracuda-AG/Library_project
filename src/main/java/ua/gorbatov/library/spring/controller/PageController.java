package ua.gorbatov.library.spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.dto.UserDTO;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.exception.OrderNotFoundException;
import ua.gorbatov.library.spring.exception.UserNotFoundException;
import ua.gorbatov.library.spring.service.UserService;

import javax.validation.Valid;

/**
 * The {@code PageController} class is used for access control operations for main pages
 *
 * @author Oleksandr Gorbatov
 */
@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    private final UserService userService;

    @Autowired
    public PageController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Method provide get mapping to login
     *
     * @return login page
     */
    @GetMapping("/login")
    public String login() {
        logger.info("Login page visited");
        return "/login";
    }

    /**
     * Method provide pos mapping to login
     *
     * @return welcome page
     */
    @PostMapping("/login")
    public String getLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.findByName(email);

        if (user.getRole().equals(Role.ROLE_USER)) {
            logger.info("Login successful");
            return "redirect:/user/cabinet";
        } else if (user.getRole().equals(Role.ROLE_LIBRARIAN)) {
            logger.info("Login successful");
            return "redirect:/librarian/cabinet";
        } else if (user.getRole().equals(Role.ROLE_ADMIN)) {
            logger.info("Login successful");
            return "redirect:/admin/cabinet";
        }

        return "/login";
    }

    /**
     * Method provide get mapping to registration
     *
     * @return registration page
     */
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        logger.info("Registration page visited");
        return "/registration";
    }

    /**
     * Method provide post mapping to registration
     *
     * @return login page
     */
    @PostMapping("/registration")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String setRegistration(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "/registration";
        userService.createUser(userService.createUserFromDTO(userDTO));
        logger.warn("Registered user: " + userDTO.getEmail());
        return "/login";
    }

    @GetMapping("/accessDenied")
    public String access() {
        return "/accessDenied";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UserNotFoundException.class})
    public String handleException() {
        return "/user/exception";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({Exception.class})
    public String handleNotFound(){
        return "/404";
    }
}
