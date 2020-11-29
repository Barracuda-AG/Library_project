package ua.gorbatov.library.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.dto.BookDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.UserService;

import java.util.List;

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
}
