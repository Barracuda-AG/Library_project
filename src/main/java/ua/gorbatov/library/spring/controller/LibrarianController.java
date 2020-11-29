package ua.gorbatov.library.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.service.BookService;

import java.util.List;

@Controller
@RequestMapping("/librarian")
public class LibrarianController {
    @Autowired
    private BookService bookService;

    @GetMapping("/allbooks")
    public String allBooks(Model model){
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);
        return "librarian/allbooks";
    }
}
