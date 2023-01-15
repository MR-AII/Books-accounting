package ru.ablyuzin.springboot.booksaccounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ablyuzin.springboot.booksaccounting.models.Book;
import ru.ablyuzin.springboot.booksaccounting.models.User;
import ru.ablyuzin.springboot.booksaccounting.repo.BooksRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BooksRepository booksRepository;

    @Autowired
    public BookService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAllByTitle(String name) {
        if (name == null) return Collections.emptyList();
        return booksRepository.findAllByNameContainingIgnoreCase(name);
    }

    public List<Book> findAllByReader(String reader) {
        if (reader == null) return Collections.emptyList();
        return booksRepository.findAllByReader(reader);
    }

    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear) return booksRepository.findAll(Sort.by("yearOfPublication"));
        return booksRepository.findAll();
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("yearOfPublication"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public Book findById(int id) {
        return booksRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Book book) {
        book.setCreated_at(new Date());
        book.setUpdated_at(new Date());
        booksRepository.save(book);
    }

    @Transactional
    public void update(Book updatedBook, int id) {
        Book book = findById(id);
        updatedBook.setId(id);
        updatedBook.setCreated_at(getCreatedAt(id));
        updatedBook.setUpdated_at(new Date());
        updatedBook.setReader(book.getReader());
        updatedBook.setBookTaken(book.getBookTaken());
        booksRepository.save(book);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void addReader(User user, int id) {
        Book book = findById(id);
        book.setId(id);
        book.setReader(user);
        book.setBookTaken(new Date());
        booksRepository.save(book);
    }

    @Transactional
    public void removeReader(int id) {
        Book book = findById(id);
        book.setId(id);
        book.setReader(null);
        book.setBookTaken(null);
        booksRepository.save(book);
    }

    private Date getCreatedAt(int id) {
        Book book = booksRepository.findById(id).orElse(null);
        assert book != null;
        return book.getCreated_at();
    }
}