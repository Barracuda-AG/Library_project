package ua.gorbatov.library.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.gorbatov.library.spring.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByName(String name);
}
