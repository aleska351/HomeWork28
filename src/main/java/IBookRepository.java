import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface IBookRepository {


    Collection<Book> getAll();

    void initialize();

    Optional<Book> getById(int id);

    void save(Book book);

    void deleteById(int id);

    Optional<Collection<Book>> searchBookByAuthorName(String text);

    void printBooks(Collection<Book> books);

    void printBook(Book book);

    void deleteBooksTable();

    Optional<Collection<Book>> getBookBetweenYear(int year1, int year2);

}
