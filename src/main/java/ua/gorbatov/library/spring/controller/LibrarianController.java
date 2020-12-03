package ua.gorbatov.library.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.OrderService;
import ua.gorbatov.library.spring.service.UserService;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code LibrarianController} class is used for access control operations for librarian
 *
 * @author Oleksandr Gorbatov
 */
@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    private static Logger logger = LoggerFactory.getLogger(LibrarianController.class);
    /**
     * The value is used to access book repository and operations
     */
    private BookService bookService;
    /**
     * The value is used to access user repository and operations
     */
    private UserService userService;
    /**
     * The value is used to access order repository and operations
     */
    private OrderService orderService;

    @Autowired
    public LibrarianController(BookService bookService, UserService userService, OrderService orderService) {
        this.bookService = bookService;
        this.userService = userService;
        this.orderService = orderService;
    }

    /**
     * Method is used for mapping get request to show all books
     *
     * @param model used for adding attribute to get all books
     * @return String address of page
     */
    @GetMapping("/allbooks")
    public String allBooks(Model model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);

        logger.info("All books page visited by librarian");
        return "librarian/allbooks";
    }

    /**
     * Method is used for get mapping to access to all orders
     *
     * @param model used for adding attributes orders and users to get all orders
     * @return String address of the page
     */
    @GetMapping(value = "/allorders")
    public String cancel(Model model) {
        List<Order> orders = orderService.allOrders();
        List<User> usersWithOrder = userService.getAllUsers().stream()
                .filter(a -> a.getOrder() != null)
                .collect(Collectors.toList());

        model.addAttribute("orders", orders);
        model.addAttribute("users", usersWithOrder);

        logger.info("All orders page visited by librarian");
        return "/librarian/allorders";
    }

    /**
     * Method used to provide post mapping to cancel order
     *
     * @param id    used to get order id
     * @param model used to add attribute orderToDelete
     * @return String to redirect
     */
    @Transactional
    @PostMapping(value = "/returnBook")
    public String returnBook(@RequestParam Long id, Model model) {
        Order order = orderService.getOrderById(id);

        model.addAttribute("orderToDelete", order);
        User user = userService.findByOrderID(order);
        if (order != null) {

            userService.clearOrder(user);
            orderService.delete(order);

            logger.info("Deleting successful");
            return "/librarian/allbooks";
        }
        logger.info("Unable to cancel order");
        return "/librarian/allorders";
    }

}
