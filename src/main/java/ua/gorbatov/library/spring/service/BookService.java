package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.dto.BookDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public boolean save(Book book){
        Book bookToSave = bookRepository.findByTitle(book.getTitle());
        if(bookToSave == null) {
            bookRepository.save(book);
            return true;
        }
        return false;
    }
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getOne(Long id) {
        return (bookRepository.findById(id).get());
    }

    public boolean delete(Long id){
        if(bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public Book saveBookFromDTO(BookDTO bookDTO){
        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .publisher(bookDTO.getPublisher())
                .publishDate(bookDTO.getPublishDate())
                .quantity(bookDTO.getQuantity()).build();

        return bookRepository.save(book);
    }
}
