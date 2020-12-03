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
/**
 * The {@code UserController} class is used for access control operations for users
 *
 * @author Oleksandr Gorbatov
 */
@Controller
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    /**
     * The value is used to access book repository and operations
     */
    private BookService bookService;
    /**
     * The value is used to access order repository and operations
     */
    private OrderService orderService;
    /**
     * The value is used to access user repository and operations
     */
    private UserService userService;

    @Autowired
    public UserController(BookService bookService, OrderService orderService, UserService userService) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.userService = userService;
    }
    /**
     * Method is used for mapping get request to make order
     *
     * @param model used for adding attribute to get all books
     * @return String address of page
     */
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
    /**
     * Method is used for mapping get request to cancel order
     * for current user
     *
     * @param model used for adding attribute to get all books
     * @param principal used for get current user name
     * @return String address of page
     */
    @GetMapping(value = "/user/cancelorder")
    public String cancel(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);
        Order order = user.getOrder();
        model.addAttribute("order", order);
        logger.info("Cancel order page is visited");
        return "/user/cancelorder";
    }

    /**
     * Method used to provide post mapping to return order
     * @param model used to add attribute order which get current order
     * @param principal to get current user name
     * @return String redirect to page makeorder
     */
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

    /**
     * Method is used for mapping get request to personal cabinet
     * @param model used to add attribute user
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
     * @param books used to get list fo books
     * @param model to add attribute
     * @param principal to get current name of user
     * @return redirect to page totalbooks
     */
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

    /**
     * Method provide get request to show order
     * @param model to add attribute
     * @param principal to get current user name
     * @return String path to showorder page
     */
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

    /**
     * Method is used for pagination and sorting of list of books
     * @param pageNo current page number
     * @param sortField present field sort
     * @param sortDir preset sort direction
     * @param model to add attributes
     * @return String address of pages
     */
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

    /**
     * Method provide get request to show all books
     * @param model to add attributes order and selectedBooks
     * @return String paginated pages
     */
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
