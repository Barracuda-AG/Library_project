package ua.gorbatov.library.spring.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    @NotBlank
    private LocalDate issueDate;
    @NotBlank
    private LocalDate returnDate;
    @NotBlank
    private boolean isReturned;

    private Integer penalty;

    @OneToMany
    private List<Book> bookList;

}
