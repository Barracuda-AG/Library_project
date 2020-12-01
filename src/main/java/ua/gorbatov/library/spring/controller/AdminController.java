package ua.gorbatov.library.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.dto.BookDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;

    @GetMapping(value ="/admin/success")
    public String success(Model model){
        List<User> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);
        return "/admin/success";
    }
    @GetMapping(value ="/admin/addbook")
    public String addBook(){
        return "/admin/addbook";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/admin/addbook")
    public String addNewBook(@ModelAttribute("book") BookDTO bookDTO){
        bookService.saveBookFromDTO(bookDTO);
        return "/admin/addbook";
    }
    @GetMapping(value ="/admin/allbooks")
    public String allBooks(Model model){
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);
        return "/admin/allbooks";
    }
    @GetMapping(value = "/admin/edituser")
    public String editUser(Model model){
        List<User> librarians = userService.getAllUsers()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_LIBRARIAN))
                .collect(Collectors.toList());
        model.addAttribute("librarians", librarians);
        List<User> users = userService.getAllUsers()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_USER))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "/admin/edituser";
    }

    @PostMapping(value = "/admin/editLibrarian")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPost(@RequestParam Long id, Model model){
        model.addAttribute("userToChange", userService.getUser(id));
        userService.update(userService.getUser(id), Role.ROLE_LIBRARIAN);
        return "redirect: /admin/success";
    }

    @PostMapping(value = "/admin/edituser")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPostUser(@RequestParam Long id,
                                   Model model){
        model.addAttribute("userToChange", userService.getUser(id));
        userService.update(userService.getUser(id), Role.ROLE_USER);
        return "redirect: /admin/success";
    }
    @GetMapping(value = "/admin/delete")
    public String delete(Model model){
        List<User> users = userService.getAllUsers().stream()
                .filter(o -> !o.getRole().equals(Role.ROLE_ADMIN))
                .collect(Collectors.toList());

        model.addAttribute("users", users);
        return "/admin/delete";
    }
    @PostMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam Long id, Model model){
        model.addAttribute("userToDelete", userService.getUser(id));
        userService.delete(userService.getUser(id).getEmail());
        return "redirect: /admin/success";
    }
}
