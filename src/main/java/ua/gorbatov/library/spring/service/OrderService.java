package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.User;
import ua.gorbatov.library.spring.exception.OrderNotFoundException;
import ua.gorbatov.library.spring.repository.OrderRepository;
import ua.gorbatov.library.spring.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {


    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private static final Integer PENALTY = 50;

    public void save(Order order) {
        orderRepository.save(order);
    }


    public void delete(Order order) {
        setOrderReturned(order);
    }

    public List<Order> allOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                OrderNotFoundException::new
        );
        return checkPenalty(order);
    }

    public Order getOrderByUser(User user) {
        return checkPenalty(user.getOrder());
    }

    private Order checkPenalty(Order order){
        if(order != null) {
            LocalDate now = LocalDate.now();
            LocalDate returnDate = order.getReturnDate();
            if (returnDate.isBefore(now) && !order.isReturned()) {
                order.setPenalty(PENALTY);
                orderRepository.save(order);
            }
        }
        return order;
    }
    private void setOrderReturned(Order order){
        order.setReturned(true);
        orderRepository.save(order);
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
