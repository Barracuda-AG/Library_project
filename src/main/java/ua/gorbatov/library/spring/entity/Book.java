package ua.gorbatov.library.spring.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * The {@code Book} class used for representing books storage
 * @author Oleksandr Gorbatov
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String publisher;

    private LocalDate publishDate;

    private Integer quantity;

    @ManyToMany(mappedBy = "books")
    private List<Order> order;
}
