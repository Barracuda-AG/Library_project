package ua.gorbatov.library.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.constants.Constants;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.exception.OrderNotFoundException;
import ua.gorbatov.library.spring.exception.UserNotFoundException;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.OrderService;
import ua.gorbatov.library.spring.service.UserService;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code UserController} class is used for access control operations for users
 *
 * @author Oleksandr Gorbatov
 */
@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    /**
     * The value is used to access book repository and operations
     */
    private final BookService bookService;
    /**
     * The value is used to access order repository and operations
     */
    private final OrderService orderService;
    /**
     * The value is used to access user repository and operations
     */
    private final UserService userService;

    @Autowired
    public UserController(BookService bookService, OrderService orderService, UserService userService) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Method used to return order
     *
     * @param principal to get current user name
     * @return String redirect to page cabinet
     */
    @PostMapping(value = "/user/returnBook")
    public String returnBook(Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);

        userService.clearOrder(user);

        return "redirect:/user/cabinet";
    }

    /**
     * Method is used for mapping get request to personal cabinet
     *
     * @param model     used to add attribute user
     * @param principal used to get current user's name
     * @return String address of page
     */
    @GetMapping(value = "/user/cabinet")
    public String cabinet(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        model.addAttribute("user", user);
        logger.info("Personal cabinet is visited by " + principal.getName());
        return "/user/cabinet";
    }

    /**
     * Method used to provide post mapping to make order
     *
     * @param books     used to get list fo books
     * @param principal to get current name of user
     * @return redirect to page showorder
     */
    @Transactional
    @PostMapping(value = "/user/makeorderPost")
    public String makeNewOrder(@RequestParam(name = "books", required = false) List<Book> books,
                               Principal principal) {

        String userName = principal.getName();
        User user = userService.findByName(userName);
        if (user.getOrder() != null) return "redirect:/user/cabinet";
        List<Book> booksFromDb = new ArrayList<>();
        for (Book book : books) {
            booksFromDb.add(bookService.findByTitle(book.getTitle()));
            bookService.updateQuantity(book, book.getQuantity() - Constants.ONE);
        }
        Order order = orderService.createOrder(booksFromDb);

        userService.createUserOrder(user, order);
        logger.warn("Created order :" + order.getId());

        return "redirect:/user/showorder";
    }

    /**
     * Method provide get request to show order
     *
     * @param model     to add attribute
     * @param principal to get current user name
     * @return String path to showorder page
     */
    @GetMapping(value = "/user/showorder")
    public String showOrder(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        Order order = orderService.getOrderByUser(user);
        List<Book> books = new ArrayList<>();
        if (order != null) {
            books = order.getBooks();
        }
        model.addAttribute("order", order);
        model.addAttribute("user", user);
        model.addAttribute("books", books);

        logger.info("Orders page visited by " + principal.getName());
        return "/user/showorder";
    }

    /**
     * Method is used for pagination and sorting of list of books
     *
     * @param pageNo    current page number
     * @param sortField present field sort
     * @param sortDir   preset sort direction
     * @param model     to add attributes
     * @return String address of pages
     */
    @GetMapping(value = "/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {

        Page<Book> page = bookService.findPaginated(pageNo, Constants.NUMBERS_ON_PAGE, sortField, sortDir);
        List<Book> bookList = page.getContent().stream()
                .filter(o -> o.getQuantity() > 0)
                .collect(Collectors.toList());

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bookList", bookList);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals(Constants.ASC) ? Constants.DESC : Constants.ASC);

        return "/user/totalbooks";
    }

    /**
     * Method provide get request to show all books
     *
     * @param model to add attributes order and selectedBooks
     * @return String paginated pages
     */
    @GetMapping(value = "/user/totalbooks")
    public String viewBooks(Model model) {

        model.addAttribute("order", new Order());

        logger.info("Visited total books page");
        return findPaginated(Constants.FIRST_PAGE, Constants.TITLE, Constants.ASC, model);
    }

    /**
     * Method searches books according text parameter
     * @param text used for search condition
     * @param model to add attributes finded books
     * @return page with results
     */
    @PostMapping(value = "/user/search")
    public String search(@RequestParam(name = "text") String text, Model model) {
        List<Book> bookList = bookService.findByAuthorOrTitle(text);
        model.addAttribute("bookList", bookList);
        return "/user/result";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UserNotFoundException.class, OrderNotFoundException.class})
    public String handleException() {
        return "/user/exception";
    }
}
