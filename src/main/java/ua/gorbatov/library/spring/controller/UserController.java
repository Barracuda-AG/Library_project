package ua.gorbatov.library.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.repository.BookRepository;
import ua.gorbatov.library.spring.repository.OrderRepository;
import ua.gorbatov.library.spring.repository.UserRepository;
import ua.gorbatov.library.spring.service.BookService;
import ua.gorbatov.library.spring.service.OrderService;
import ua.gorbatov.library.spring.service.UserService;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;


    @GetMapping(value = "/user/makeorder")
    public String makeOrder(Model model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("order", new Order());
        model.addAttribute("selectedBooks", books);
        return "/user/makeorder";
    }

    @PostMapping(value = "/user/makeorder")
    public String makeNewOrder(@ModelAttribute("order") Order order, @RequestParam(name = "books") Book book, BindingResult bindingResult, User user) {
        if (bindingResult.hasErrors()) {
            return "/user/makeorder";
        }
        Book bookFromDb = bookRepository.findByTitle(book.getTitle());
        userService.createUserOrder(user,order);
        Integer quantity = bookFromDb.getQuantity();
        if(quantity > 0) {
            bookFromDb.setQuantity(bookFromDb.getQuantity() - 1);
            bookRepository.save(bookFromDb);
            order.setBook(book);
            order.setIssueDate(LocalDate.now());
            order.setReturnDate(LocalDate.now().plus(Period.ofDays(10)));
            order.setReturned(false);
            order.setPenalty(0);
            orderRepository.save(order);
        } else return "/user/makeorder";
        return "/user/makeorder";
    }

    @GetMapping(value = "/user/showorder")
    public String showOrder(Model model) {
        List<Order> orderList = orderService.allOrders();
        model.addAttribute("orderList", orderList);

        return "/user/showorder";
    }
}
