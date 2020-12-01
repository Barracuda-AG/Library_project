package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.repository.OrderRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private static final Integer PENALTY = 20;

    public void save(Order order) {
        orderRepository.save(order);
    }

    public List<Order> allOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        if (orderRepository.findById(id).isPresent()) {
            Order order = orderRepository.findById(id).get();
            return checkPenalty(order);
        }
        return null;
    }

    private Order checkPenalty(Order order) {
//        LocalDate now = LocalDate.now();
//        LocalDate returnDate = order.getReturnDate();
//        if (returnDate.isBefore(now) && !order.isReturned()) {
//            order.setPenalty(PENALTY);
//        }
        return order;
    }

    public Order createOrder(List<Book> books) {
        Order order = new Order();
        order.setBooks(books);
        order.setIssueDate(LocalDate.now());
        order.setReturnDate(LocalDate.now().plus(Period.ofDays(10)));
        order.setReturned(false);
        order.setPenalty(0);
        return orderRepository.save(order);
    }
}
