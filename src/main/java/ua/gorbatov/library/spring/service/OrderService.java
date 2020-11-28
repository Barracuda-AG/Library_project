package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.repository.OrderRepository;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void save(Order order){
        orderRepository.save(order);
    }

    public List<Order> allOrders(){
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id){
        if(orderRepository.findById(id).isPresent()){
            return orderRepository.findById(id).get();
        }
        return null;
    }
}
