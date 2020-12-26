package ua.gorbatov.library.spring.controller;

import org.springframework.validation.BindingResult;
import ua.gorbatov.library.spring.exception.OrderNotFoundException;
import ua.gorbatov.library.spring.exception.UnableDeleteBookException;
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
import java.security.Principal;
import java.util.List;

/**
 * The {@code AdminController} class is used for access control operations for admin
 *
 * @author Oleksandr Gorbatov
 */
@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    /**
     * The value is used to access user repository and operations
     */
    private final UserService userService;
    /**
     * The value is used to access book repository and operations
     */
    private final BookService bookService;

    @Autowired
    public AdminController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    @GetMapping(value = "/admin/cabinet")
    public String cabinet(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        model.addAttribute("user", user);
        logger.info("Personal cabinet is visited by " + principal.getName());
        return "/admin/cabinet";
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
     * @param bookDTO used to add book information
     * @return String to redirect
     */
    @PostMapping(value = "/admin/addbook")
    public String addNewBook(@Valid @ModelAttribute("book") BookDTO bookDTO,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "/admin/addbook";
        bookService.saveBookFromDTO(bookDTO);

        logger.info("Created book " + bookDTO.getTitle());
        return "redirect:/admin/allbooks";
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
    @PostMapping(value = "/admin/changeRole")
    public String editUser(@RequestParam Long id, Model model) {

        User user = userService.getUser(id);
        model.addAttribute("user", user);

        logger.info("Edit user page is visited");
        return "/admin/edituser";
    }

    /**
     * Method is used for post request to edit user
     *
     * @param id    used to find user
     * @param model used for adding attribute userToChange
     * @return String address of page
     */
    @PostMapping(value = "/admin/edituser")
    public String editUserPostUser(@RequestParam Long id,
                                   Model model) {
        User user = userService.getUser(id);
        model.addAttribute("userToChange", user);
        if (user.getRole().equals(Role.ROLE_USER)) {
            userService.update(userService.getUser(id), Role.ROLE_LIBRARIAN);
        } else if (user.getRole().equals(Role.ROLE_LIBRARIAN)) {
            userService.update(userService.getUser(id), Role.ROLE_USER);
        }
        logger.info("Changed role to user: " + user.getEmail());
        return "redirect:/admin/success";
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
        return "redirect:/admin/success";
    }

    /**
     * Method is used for post request to delete book
     *
     * @param id    used for delete book
     * @param title used for book title
     * @return String address of page
     */
    @PostMapping(value = "/admin/deletebook")
    public String deleteBookPost(@RequestParam(name = "id") Long id,
                                 @RequestParam(name = "title") String title) {

        bookService.delete(id);

        logger.info(title + " book are deleted");
        return "redirect:/admin/allbooks";
    }

    @PostMapping(value = "/admin/editpage")
    public String editBook(@RequestParam(name = "id") Long id,
                           Model model) {
        Book bookToEdit = bookService.findById(id);
        model.addAttribute("book", bookToEdit);
        return "/admin/editbook";
    }

    @PostMapping(value = "/admin/editbook")
    public String editBookPost(@RequestParam(name = "id")Long id,
                               @Valid @ModelAttribute(name = "book")BookDTO bookDTO,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "redirect:/admin/editpage";
        bookService.updateBook(id, bookDTO.getTitle(), bookDTO.getAuthor(),
                bookDTO.getPublisher(), bookDTO.getPublishDate(), bookDTO.getQuantity());

        return "redirect:/admin/allbooks";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UserNotFoundException.class, OrderNotFoundException.class,
            UnableDeleteBookException.class})
    public String handleException() {
        return "/admin/exception";
    }

}
