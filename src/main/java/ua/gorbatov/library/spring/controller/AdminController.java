package ua.gorbatov.library.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.dto.BookDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Role;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private UserService userService;

    private BookService bookService;

    @Autowired
    public AdminController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    @GetMapping(value = "/admin/success")
    public String success(Model model) {
        logger.info("admin page visited");
        List<User> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);
        return "/admin/success";
    }

    @GetMapping(value = "/admin/addbook")
    public String addBook(Model model) {
        model.addAttribute("book", new BookDTO());

        logger.info("Adding book page visited");
        return "/admin/addbook";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/admin/addbook")
    public String addNewBook(@Valid @ModelAttribute("book") BookDTO bookDTO,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/admin/addbook";
        }
        bookService.saveBookFromDTO(bookDTO);

        logger.info("Created book " + bookDTO.getTitle());
        return "/admin/addbook";
    }

    @GetMapping(value = "/admin/allbooks")
    public String allBooks(Model model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);

        logger.info("All books page is visited");
        return "/admin/allbooks";
    }

    @GetMapping(value = "/admin/edituser")
    public String editUser(Model model) {
        List<User> librarians = userService.getAllUsers()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_LIBRARIAN))
                .collect(Collectors.toList());
        model.addAttribute("librarians", librarians);
        List<User> users = userService.getAllUsers()
                .stream().filter(o -> o.getRole().equals(Role.ROLE_USER))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        logger.info("Edit user page is visited");
        return "/admin/edituser";
    }

    @PostMapping(value = "/admin/editLibrarian")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPost(@RequestParam Long id, Model model) {

        model.addAttribute("userToChange", userService.getUser(id));
        userService.update(userService.getUser(id), Role.ROLE_LIBRARIAN);

        logger.info("Changed role to librarian: " + userService.getUser(id).getEmail());
        return "/admin/success";
    }

    @PostMapping(value = "/admin/edituser")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPostUser(@RequestParam Long id,
                                   Model model) {
        model.addAttribute("userToChange", userService.getUser(id));
        userService.update(userService.getUser(id), Role.ROLE_USER);

        logger.info("Changed role to user: " + userService.getUser(id).getEmail());
        return "/admin/success";
    }

    @GetMapping(value = "/admin/delete")
    public String delete(Model model) {
        List<User> users = userService.getAllUsers().stream()
                .filter(o -> !o.getRole().equals(Role.ROLE_ADMIN))
                .collect(Collectors.toList());

        model.addAttribute("users", users);

        logger.info("Delete page is visited");
        return "/admin/delete";
    }

    @PostMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam Long id, Model model) {
        User userToDelete = userService.getUser(id);
        String email = userToDelete.getEmail();
        model.addAttribute("userToDelete", userToDelete);
        userService.delete(userService.getUser(id).getEmail());

        logger.info("Deleted user: " + email);
        return "/admin/success";
    }

    @GetMapping(value = "/admin/deletebook")
    public String deleteBook(Model model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("selectedBooks", books);

        logger.info("Delete book page is visited");
        return "/admin/deletebook";
    }

    @PostMapping(value = "/admin/deletebook")
    public String deleteBookPost(@RequestParam(name = "books") List<Book> books,
                                 Model model) {

        for (Book book : books) {
            bookService.delete(book.getId());
        }
        logger.info("Books are deleted");
        return "/user/totalbooks";
    }
}
