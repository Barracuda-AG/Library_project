package ua.gorbatov.library.spring.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 100)
    private String name;
    @Size(min = 2, max = 100)
    private String author;

    @Size(min = 2, max = 100)
    private String publisher;

    @NotBlank
    private LocalDate publishDate;

    @NotBlank
    private Integer quantity;

}
