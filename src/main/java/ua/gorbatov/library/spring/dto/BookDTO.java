package ua.gorbatov.library.spring.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    @Size(min = 2, max = 100)
    private String title;
    @Size(min = 2, max = 100)
    private String author;
    @Size(min = 2, max = 100)
    private String publisher;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;
    @NotBlank
    private Integer quantity;
}
