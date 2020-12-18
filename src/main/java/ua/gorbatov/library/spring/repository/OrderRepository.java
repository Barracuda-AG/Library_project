package ua.gorbatov.library.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.gorbatov.library.spring.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
