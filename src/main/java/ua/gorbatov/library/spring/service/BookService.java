package ua.gorbatov.library.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.gorbatov.library.spring.dto.BookDTO;
import ua.gorbatov.library.spring.entity.Book;
import ua.gorbatov.library.spring.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book findByTitle(String title){
        return bookRepository.findByTitle(title);
    }
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

    public void updateQuantity(Book book,Integer quantity){
        book.setQuantity(quantity);

    }
    public Page<Book> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField) :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo -1, pageSize, sort);
        return this.bookRepository.findAll(pageable);
    }
}
