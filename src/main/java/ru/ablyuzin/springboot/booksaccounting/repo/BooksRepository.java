package ru.ablyuzin.springboot.booksaccounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ablyuzin.springboot.booksaccounting.models.Book;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    List<Book> findAllByNameContainingIgnoreCase(String name);

    @Query("SELECT b FROM Book b WHERE LOWER(b.reader.fullName) LIKE CONCAT('%', LOWER(:rdr), '%')")
    List<Book> findAllByReader(@Param("rdr") String reader);
}