package ua.gorbatov.library.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    private BookService bookService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;


    @GetMapping(value = "/user/makeorder")
    public String makeOrder(Model model) {
        List<Book> books = bookService.getAll().stream()
                .filter(o -> o.getQuantity() > 0).collect(Collectors.toList());
        model.addAttribute("order", new Order());
        model.addAttribute("selectedBooks", books);
        return "/user/makeorder";
    }
    @Transactional
    @PostMapping(value = "/user/makeorder")
    public String makeNewOrder(@RequestParam(name = "books") List<Book> books,
                               BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "/user/showorder";
        }
        String userName = principal.getName();
        User user = userService.findByName(userName);

        if(user == null || user.getOrder() != null) return "/user/showorder";
        List<Book> booksFromDb = new ArrayList<>();
        for(Book book: books) {
            booksFromDb.add(bookService.findByTitle(book.getTitle()));
            bookService.updateQuantity(book,book.getQuantity()-1);
        }

            Order order = orderService.createOrder(booksFromDb);

            userService.createUserOrder(user,order);

        return "redirect: /user/showorder";
    }

    @GetMapping(value = "/user/showorder")
    public String showOrder(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userService.findByName(userName);

        Long orderId = user.getOrder().getId();
        Order order = orderService.getOrderById(orderId);

        List<Book> books = order.getBooks();

        model.addAttribute("order", order);
        model.addAttribute("user", user);
        model.addAttribute("books", books);

        return "/user/showorder";
    }
}
