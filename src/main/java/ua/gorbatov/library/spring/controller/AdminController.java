package ua.gorbatov.library.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/success")
    public String success(Model model){
        List<User> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);
        return "admin/success";
    }
}
