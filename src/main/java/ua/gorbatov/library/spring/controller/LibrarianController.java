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

@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    private static Logger logger = LoggerFactory.getLogger(LibrarianController.class);

    private BookService bookService;

    private UserService userService;

    private OrderService orderService;
    @Autowired
    public LibrarianController(BookService bookService, UserService userService, OrderService orderService) {
        this.bookService = bookService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/allbooks")
    public String allBooks(Model model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);

        logger.info("All books page visited by librarian");
        return "librarian/allbooks";
    }

    @GetMapping(value = "/allorders")
    public String cancel(Model model) {
        List<Order> orders = orderService.allOrders();
        List<User> usersWithOrder = userService.getAllUsers().stream()
                .filter(a -> a.getOrder()!= null)
                .collect(Collectors.toList());

        model.addAttribute("orders", orders);
        model.addAttribute("users", usersWithOrder);

        logger.info("All orders page visited by librarian");
        return "/librarian/allorders";
    }
    @Transactional
    @PostMapping(value = "/returnBook")
    public String returnBook(@RequestParam Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if(order == null) {
            logger.info("Unable to cancel order");
            return "/librarian/allorders";
        }

        model.addAttribute("orderToDelete", order);
        User user = userService.findById(order.getId());

        logger.info("Deleting order #: " + order.getId());
        userService.clearOrder(user);
        orderService.delete(order);
        logger.info("Deleting successful");
        return"/librarian/allbooks";
   }

}
