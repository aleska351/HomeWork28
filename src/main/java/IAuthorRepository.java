import java.util.Collection;
import java.util.Optional;

public interface IAuthorRepository {

    public void initialize();

    Collection<Author> getAll();

    Optional<Author> getById(int n);

    void save(Author author);

    void deleteById(int id);

    void deleteAuthorsTable();

    void printAuthors(Collection<Author> authors);

    void printAuthor(Author author);
}
