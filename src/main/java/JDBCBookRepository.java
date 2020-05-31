import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * The repository for {@link Book},
 * which uses {@link java.sql.Connection} and SQL to persist data.
 */
public class JDBCBookRepository implements IBookRepository {
    private final Connection connection;
    private final IAuthorRepository authorRepository;

    /**
     * Creates an instance of the class.
     *
     * @param connection The opened and prepared connection.
     */
    public JDBCBookRepository(Connection connection,
                              IAuthorRepository authorRepository) {
        this.connection = connection;
        this.authorRepository = authorRepository;
    }

    /**
     * Initializes required tables if needed.
     */
    public void initialize() {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS books(" +
                    " id_book INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " title VARCHAR(200), " +
                    " pages_count INTEGER, " +
                    " publish_year INTEGER, " +
                    " author_id INTEGER" +
                    ")");
        } catch (SQLException e) {
            System.out.println("Не удалось создать таблицу для книги");
            ;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Collection<Book> getAll() {
        try (Statement statement = connection.createStatement();
             ResultSet cursor = statement.executeQuery("SELECT * FROM books")) {
            Collection<Book> books = new ArrayList<>();
            while (cursor.next()) {
                Book book = createBookFromCursorIfPossible(cursor);
                book.author = createAuthorFromBookCursor(cursor);
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param id
     * @return
     * @throws SQLException
     */
    @Override
    public Optional<Book> getById(int id) {
        Book book = null;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM books WHERE id_book = ?")) {
            statement.setInt(1, id);

            try (ResultSet cursor = statement.executeQuery()) {
                if (!cursor.next()) {
                    return Optional.empty();
                }
                book = this.createBookFromCursorIfPossible(cursor);
                book.author = createAuthorFromBookCursor(cursor);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(book);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Book book) {
        authorRepository.save(book.author);
        if (book.id != Book.INVALID_ID) {
            updateBook(book);
        } else {
            insertBook(book);
        }
    }

    /**
     * Inserts new record for the book.
     */
    public void insertBook(Book book) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO" +
                        " books(title, pages_count, publish_year, author_id)" +
                        " VALUES(?, ?, ?, ?)", RETURN_GENERATED_KEYS)) {

            statement.setString(1, book.title);
            statement.setInt(2, book.pagesCount);
            statement.setInt(3, book.publishYear);
            statement.setInt(4, book.author.id);

            if (statement.executeUpdate() > 0) {
                try (ResultSet cursor = statement.getGeneratedKeys()) {
                    if (cursor.next()) {
                        book.id = cursor.getInt(1);
                    } else {
                        throw new RuntimeException("Failed to get generated key for a book");
                    }
                }
            } else {
                throw new RuntimeException("Failed to insert a book record");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates existing record of a book, identified by the primary key.
     */

    private void updateBook(Book book) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE books" +
                        " SET title = ?, pages_count = ?, publish_year = ?, author_id = ? WHERE id_book = ?")) {

            statement.setString(1, book.title);
            statement.setInt(2, book.pagesCount);
            statement.setInt(3, book.publishYear);
            statement.setInt(4, book.author.id);
            statement.setInt(5, book.id);

            if (statement.executeUpdate() == 0) {
                throw new RuntimeException("Failed to update a book record");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates a book instance from the cursor position without loading its author.
     */
    private Book createBookFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final Book book = new Book();

        book.id = cursor.getInt("id_book");
        book.title = cursor.getString("title");
        book.pagesCount = cursor.getInt("pages_count");
        book.publishYear = cursor.getInt("publish_year");

        return book;
    }

    private Author createAuthorFromBookCursor(ResultSet bookCursor) throws SQLException {
        int authorId = bookCursor.getInt("author_id");

        return authorRepository.getById(authorId).get();
    }


    @Override
    public void deleteById(int id) {
        try (PreparedStatement statement1 = connection.prepareStatement("SELECT (1) from books  WHERE id_book = ?")) {
            statement1.setInt(1, id);
            if (statement1.executeQuery().next()) {
                try (PreparedStatement statement =
                             connection.prepareStatement("DELETE FROM books WHERE id_book = ?")) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }
            } else System.out.println("Книги с таким id нет");
        } catch (SQLException e) {
            e.getMessage();
            throw new RuntimeException(e);
        }
    }

    public Optional<Collection<Book>> getBookBetweenYear(int year1, int year2) {

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM books" +
                " WHERE  publish_year BETWEEN ? AND  ? ")) {
            final Collection books = new ArrayList();
            statement.setInt(1, year1);
            statement.setInt(2, year2);
            try (ResultSet cursor = statement.executeQuery()) {
                if (!cursor.next()) {
                    return Optional.empty();
                }
               // while (cursor.next()) {
                    Book book = this.createBookFromCursorIfPossible(cursor);
                    book.author = createAuthorFromBookCursor(cursor);
                    books.add(book);
                //}
            }
            return Optional.of(books);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Collection<Book>> searchBookByAuthorName(String text) {

        final Collection books = new ArrayList();
        try (Statement statement = connection.createStatement();
             ResultSet cursor = statement.executeQuery(
                     "SELECT * FROM books " +
                             " INNER JOIN authors ON   authors.id_author =  books.author_id" +
                             " WHERE  authors.name LIKE '%" + text + "%'")) {

            if (!cursor.next()) {
                return Optional.empty();
            }
            while (cursor.next()) {
                Book book = createBookFromCursorIfPossible(cursor);
                book.author = createAuthorFromBookCursor(cursor);
                books.add(book);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());

        }
        return Optional.of(books);
    }

    @Override
    public void printBooks(Collection<Book> books) {
        if (!books.isEmpty()) {
            System.out.printf("%-5s | %-20s | %-20s | %-20s | %-20s %n",
                    "ID", "TITLE", "PUBLISH YEAR", "PAGES COUNT", "AUTHOR");
            books.stream().forEach(book -> {
                System.out.printf("%-5d | %-20s | %-20d | %-20d | %-20s",
                        book.id, book.title, book.publishYear, book.pagesCount, book.author.name);
                System.out.println();
            });
        }
    }

    @Override
    public void printBook(Book book) {
        System.out.printf("%-5s | %-20s | %-15s | %-15s | %-15s | %n",
                "ID", "TITLE", "PUBLISH YEAR", "PAGES COUNT", "AUTHOR");

        System.out.printf("%-5d | %-20s | %-15d | %-15d | %-15s | %n",
                book.id, book.title, book.publishYear, book.pagesCount, book.author.name);
    }

    @Override
    public void deleteBooksTable() {
        try {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP TABLE IF EXISTS books");
            }
        } catch (SQLException e) {
            System.out.println("Не удалось удалить таблицу книг");
        }
    }
}