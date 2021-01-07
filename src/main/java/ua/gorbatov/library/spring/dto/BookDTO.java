package ua.gorbatov.library.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
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

    @NotNull
    private Integer quantity;
}
