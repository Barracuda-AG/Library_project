package ua.gorbatov.library.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.gorbatov.library.spring.entity.Order;
import ua.gorbatov.library.spring.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
