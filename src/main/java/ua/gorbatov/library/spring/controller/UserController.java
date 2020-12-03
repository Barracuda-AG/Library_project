package ua.gorbatov.library.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private BookService bookService;

    private OrderService orderService;

    private UserService userService;

    @Autowired
    public UserController(BookService bookService, OrderService orderService, UserService userService) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping(value = "/user/makeorder")
    public String makeOrder(Model model) {
        List<Book> books = bookService.getAll().stream()
                .filter(o -> o.getQuantity() > 0)
                .collect(Collectors.toList());
        model.addAttribute("order", new Order());
        model.addAttribute("selectedBooks", books);
        logger.info("Make order page is visited");
        return "/user/makeorder";
    }

    @GetMapping(value = "/user/cancelorder")
    public String cancel(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        Order order = user.getOrder();
        model.addAttribute("order", order);
        logger.info("Cancel order page is visited");
        return "/user/cancelorder";
    }

    @PostMapping(value = "/user/returnBook")
    public String returnBook(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        Order userOrder = user.getOrder();
        model.addAttribute("order", userOrder);

        if (userOrder != null) {
            List<Book> listBooks = userOrder.getBooks();
            logger.info("Books returned successfuly");
            for (Book book : listBooks) bookService.updateQuantity(book, book.getQuantity() + 1);
            userService.clearOrder(user);
            orderService.delete(userOrder);
        }

        logger.warn("Unable to return books");
        return "/user/makeorder";
    }

    @GetMapping(value = "/user/cabinet")
    public String cabinet(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        model.addAttribute("user", user);
        logger.info("Personal cabinet is visited by " + principal.getName());
        return "/user/cabinet";
    }

    @Transactional
    @PostMapping(value = "/user/makeorderPost")
    public String makeNewOrder(@RequestParam(name = "books") List<Book> books,
                               Model model, Principal principal) {

        String userName = principal.getName();
        User user = userService.findByName(userName);

        if (user == null || user.getOrder() != null){
            logger.warn("Unable to make order!");
            return "/user/totalbooks";
        }
        List<Book> booksFromDb = new ArrayList<>();
        for (Book book : books) {
            booksFromDb.add(bookService.findByTitle(book.getTitle()));
            bookService.updateQuantity(book, book.getQuantity() - 1);
        }

        Order order = orderService.createOrder(booksFromDb);

        userService.createUserOrder(user, order);
        logger.warn("Created order :"+ order.getId());
        return "/user/totalbooks";
    }

    @GetMapping(value = "/user/showorder")
    public String showOrder(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        if (user.getOrder() == null) return "/user/totalbooks";
        Long orderId = user.getOrder().getId();
        Order order = orderService.getOrderById(orderId);

        List<Book> books = order.getBooks();

        model.addAttribute("order", order);
        model.addAttribute("user", user);
        model.addAttribute("books", books);

        logger.info("Orders page visited by " + principal.getName());
        return "/user/showorder";
    }

    @GetMapping(value = "/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {
        int pageSize = 8;

        Page<Book> page = bookService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Book> bookList = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bookList", bookList);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "/user/totalbooks";
    }

    @GetMapping(value = "/user/totalbooks")
    public String viewBooks(Model model) {
        List<Book> books = bookService.getAll().stream()
                .filter(o -> o.getQuantity() > 0)
                .collect(Collectors.toList());
        model.addAttribute("order", new Order());
        model.addAttribute("selectedBooks", books);
        logger.info("Visited total books page");
        return findPaginated(1, "title", "asc", model);
    }
}
