package ua.gorbatov.library.spring.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;

    private boolean isReturned;

    private Integer penalty;

    @OneToMany
    private List<Book> books;

}
