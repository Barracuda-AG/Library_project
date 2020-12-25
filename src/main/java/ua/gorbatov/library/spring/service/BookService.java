package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.dto.BookDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.exception.UnableDeleteBookException;
import ua.gorbatov.library.spring.repository.BookRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public boolean save(Book book) {
        Book bookToSave = bookRepository.findByTitle(book.getTitle());
        if (bookToSave == null) {
            bookRepository.save(book);
            return true;
        }
        return false;
    }

    public List<Book> getAll() {
        return bookRepository.findAll().stream()
                .filter(o -> o.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
            try {
                bookRepository.deleteById(id);
            }catch (Exception e){
                throw new UnableDeleteBookException(e.getMessage());
            }
    }

    public Book saveBookFromDTO(BookDTO bookDTO) {
        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .publisher(bookDTO.getPublisher())
                .publishDate(bookDTO.getPublishDate())
                .quantity(bookDTO.getQuantity()).build();

        return bookRepository.save(book);
    }

    public void updateQuantity(Book book, Integer quantity) {
        book.setQuantity(quantity);

    }

    public Page<Book> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField) :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.bookRepository.findAll(pageable);
    }
}
