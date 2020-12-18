package ua.gorbatov.library.spring.controller;

import ua.gorbatov.library.spring.exception.OrderNotFoundException;
import ua.gorbatov.library.spring.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.validation.Valid;
import java.util.List;

/**
 * The {@code AdminController} class is used for access control operations for admin
 *
 * @author Oleksandr Gorbatov
 */
@Controller
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);
    /**
     * The value is used to access user repository and operations
     */
    private UserService userService;
    /**
     * The value is used to access book repository and operations
     */
    private BookService bookService;

    @Autowired
    public AdminController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }
    /**
     * Method is used for mapping get request to show succes page
     *
     * @param model used for adding attribute to get all users
     * @return String address of page
     */
    @GetMapping(value = "/admin/success")
    public String success(Model model) {
        logger.info("admin page visited");
        List<User> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);
        return "/admin/success";
    }
    /**
     * Method is used for mapping get request to add book
     *
     * @param model used for adding attribute book
     * @return String address of page
     */
    @GetMapping(value = "/admin/addbook")
    public String addBook(Model model) {
        model.addAttribute("book", new BookDTO());

        logger.info("Adding book page visited");
        return "/admin/addbook";
    }
    /**
     * Method used to provide post mapping to add book
     *
     * @param bookDTO   used to add book information
     * @return String to redirect
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/admin/addbook")
    public String addNewBook(@Valid @ModelAttribute("book") BookDTO bookDTO) {

        bookService.saveBookFromDTO(bookDTO);

        logger.info("Created book " + bookDTO.getTitle());
        return "/admin/addbook";
    }
    /**
     * Method is used for mapping get request to show all books
     *
     * @param model used for adding attribute books
     * @return String address of page
     */
    @GetMapping(value = "/admin/allbooks")
    public String allBooks(Model model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);

        logger.info("All books page is visited");
        return "/admin/allbooks";
    }
    /**
     * Method is used for mapping get request to edit user
     *
     * @param model used for adding attribute users and librarians
     * @return String address of page
     */
    @GetMapping(value = "/admin/edituser")
    public String editUser(Model model) {

        List<User> users = userService.getOnlyUsers();

        model.addAttribute("users", users);
        logger.info("Edit user page is visited");
        return "/admin/edituser";
    }
    /**
     * Method is used for mapping get request to edit user
     *
     * @param model used for adding attribute users and librarians
     * @return String address of page
     */
    @GetMapping(value="/admin/editlibrarian")
    public String editLibrarian(Model model){
        List<User> librarians = userService.getOnlyLibrarians();

        model.addAttribute("librarians", librarians);
        logger.info("Edit librarian page is visited");
        return "/admin/editlibrarian";
    }
    /**
     * Method is used for post request to edit user
     * @param id to get user
     * @param model used for adding attribute book
     * @return String address of page
     */
    @PostMapping(value = "/admin/editlibrarian")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPost(@RequestParam Long id, Model model) {

        model.addAttribute("userToChange", userService.getUser(id));
        userService.update(userService.getUser(id), Role.ROLE_LIBRARIAN);

        logger.info("Changed role to librarian: " + userService.getUser(id).getEmail());
        return "/admin/success";
    }
    /**
     * Method is used for post request to edit librarian
     * @param id used to find user
     * @param model used for adding attribute userToChange
     * @return String address of page
     */
    @PostMapping(value = "/admin/edituser")
    @ResponseStatus(HttpStatus.OK)
    public String editUserPostUser(@RequestParam Long id,
                                   Model model) {
        model.addAttribute("userToChange", userService.getUser(id));
        userService.update(userService.getUser(id), Role.ROLE_USER);

        logger.info("Changed role to user: " + userService.getUser(id).getEmail());
        return "/admin/success";
    }

    /**
     * Method is used for get request to delete user
     *
     * @param model used for adding attribute book
     * @return String address of page
     */
    @GetMapping(value = "/admin/delete")
    public String delete(Model model) {
        List<User> users = userService.getUsersExceptAdmin();

        model.addAttribute("users", users);

        logger.info("Delete page is visited");
        return "/admin/delete";
    }

    /**
     * Method is used for post request to edit user
     *
     * @param model used for adding attribute book
     * @return String address of page
     */
    @PostMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam Long id, Model model) {
        User userToDelete = userService.getUser(id);
        String email = userToDelete.getEmail();
        model.addAttribute("userToDelete", userToDelete);
        userService.delete(userService.getUser(id).getEmail());

        logger.info("Deleted user: " + email);
        return "/admin/success";
    }
    /**
     * Method is used for get request to delete book
     *
     * @param model used for adding attribute selectedBooks
     * @return String address of page
     */
    @GetMapping(value = "/admin/deletebook")
    public String deleteBook(Model model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("selectedBooks", books);

        logger.info("Delete book page is visited");
        return "/admin/deletebook";
    }

    /**
     * Method is used for post request to delete book
     *
     * @param model used for adding attribute book
     * @return String address of page
     */
    @PostMapping(value = "/admin/deletebook")
    public String deleteBookPost(@RequestParam(name = "books") List<Book> books,
                                 Model model) {

        for (Book book : books) {
            bookService.delete(book.getId());
        }
        logger.info("Books are deleted");
        return "/admin/success";
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UserNotFoundException.class, OrderNotFoundException.class})
    public String handleException(){
        return "/admin/exception";
    }

}
