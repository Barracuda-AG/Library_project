package ua.gorbatov.library.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.constants.Constants;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.OrderService;
import ua.gorbatov.library.spring.service.UserService;

import java.security.Principal;
import java.util.List;

/**
 * The {@code LibrarianController} class is used for access control operations for librarian
 *
 * @author Oleksandr Gorbatov
 */
@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    private static final Logger logger = LoggerFactory.getLogger(LibrarianController.class);
    /**
     * The value is used to access book repository and operations
     */
    private final BookService bookService;
    /**
     * The value is used to access user repository and operations
     */
    private final UserService userService;
    /**
     * The value is used to access order repository and operations
     */
    private final OrderService orderService;

    @Autowired
    public LibrarianController(BookService bookService, UserService userService, OrderService orderService) {
        this.bookService = bookService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping(value = "/cabinet")
    public String cabinet(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        model.addAttribute("user", user);
        logger.info("Personal cabinet is visited by " + principal.getName());
        return "/librarian/cabinet";
    }

    /**
     * Method is used for get mapping to access to all orders
     *
     * @param model used for adding attributes orders and users to get all orders
     * @return String address of the page
     */
    @GetMapping(value = "/allorders")
    public String cancel(Model model) {

        logger.info("All orders page visited by librarian");
        return findPaginatedOrders(Constants.FIRST_PAGE, Constants.ID, Constants.DESC, model);
    }

    /**
     * Method is used for access
     * readers with subscriptions
     *
     * @param model used for adding attributes users to get all orders
     * @return String address of the page
     */
    @GetMapping(value = "/readers")
    public String showReaders(Model model) {
        List<User> usersWithOrders = userService.findUserWithOrders();
        model.addAttribute("users", usersWithOrders);
        return "/librarian/readers";
    }

    /**
     * Method used to cancel subscription
     *
     * @param id used to get order id
     * @return String to redirect
     */
    @PostMapping(value = "/returnBook")
    public String returnBook(@RequestParam Long id) {
        Order order = orderService.getOrderById(id);

        User user = userService.findByOrderID(order);

        userService.clearOrder(user);

        logger.info("Order " + order.getId() + " is canceled");
        return "redirect:/librarian/readers";
    }

    /**
     * Method is used for mapping get request to show all books
     *
     * @param model used for adding attribute to get all books
     * @return String address of page
     */
    @GetMapping("/allbooks")
    public String allBooks(Model model) {

        logger.info("All books page visited by librarian");
        return findPaginatedBooks(Constants.FIRST_PAGE, Constants.ID, Constants.ASC, model);
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
    public String findPaginatedBooks(@PathVariable(value = "pageNo") int pageNo,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDir") String sortDir,
                                     Model model) {

        Page<Book> page = bookService.findPaginated(pageNo, Constants.NUMBERS_ON_PAGE, sortField, sortDir);
        List<Book> bookList = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bookList", bookList);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals(Constants.ASC) ? Constants.DESC : Constants.ASC);

        return "/librarian/allbooks";
    }

    @GetMapping(value = "/page_order/{pageNo}")
    public String findPaginatedOrders(@PathVariable(value = "pageNo") int pageNo,
                                      @RequestParam("sortField") String sortField,
                                      @RequestParam("sortDir") String sortDir,
                                      Model model) {
        Page<Order> page = orderService.findPaginated(pageNo, Constants.NUMBERS_ON_PAGE, sortField, sortDir);
        List<Order> orderList = page.getContent();

        model.addAttribute("orderList", orderList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals(Constants.ASC) ? Constants.DESC : Constants.ASC);

        return "/librarian/allorders";
    }

}
