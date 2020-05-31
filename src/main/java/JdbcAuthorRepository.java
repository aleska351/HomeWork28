import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcAuthorRepository implements IAuthorRepository {
    private final Connection connection;

    /**
     * Creates an instance of the class.
     *
     * @param connection The opened and prepared connection.
     */
    public JdbcAuthorRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Initializes required tables if needed.
     */
    public void initialize() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS authors(" +
                    " id_author INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " name VARCHAR(200), " +
                    " birth_year INTEGER" +
                    ")");
        } catch (SQLException e) {
            System.out.println("Не удалось создать таблицу для авторов");
            ;
        }
    }

    @Override
    public Collection<Author> getAll() {
        try (Statement statement = connection.createStatement();
             ResultSet cursor = statement.executeQuery("SELECT * FROM authors")) {
            Collection<Author> authors = new ArrayList<>();
            while (cursor.next()) {
                Author author = createAuthorFromCursorIfPossible(cursor);
                authors.add(author);
            }
            return authors;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @return found author.
     * @throws RuntimeException If no author is found for given ID.
     */
    @Override
    public Optional<Author> getById(int id) {
        final Author author;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM authors WHERE id_author = ?")) {
            statement.setInt(1, id);
            try (ResultSet cursor = statement.executeQuery()) {
                if (!cursor.next()) {
                    return Optional.empty();
                }
                author = this.createAuthorFromCursorIfPossible(cursor);
            }
            return Optional.of(author);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Saves the author by updating existing one or inserting new one.
     */
    @Override
    public void save(Author author) {
        if (author.id != Author.INVALID_ID) {
            updateAuthor(author);
        } else {
            insertAuthor(author);
        }
    }

    /**
     * Inserts new record of author.
     */
    private void insertAuthor(Author author) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO" +
                        " authors(name, birth_year)" +
                        " VALUES(?, ?)", RETURN_GENERATED_KEYS)) {

            statement.setString(1, author.name);
            statement.setInt(2, author.birthYear);

            if (statement.executeUpdate() > 0) {
                try (ResultSet cursor = statement.getGeneratedKeys()) {
                    if (cursor.next()) {
                        author.id = cursor.getInt(1);
                    } else {
                        throw new RuntimeException("Failed to get generated key for a author");
                    }
                }
            } else {
                throw new RuntimeException("Failed to insert a author record");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing record by using its primary key.
     */
    private void updateAuthor(Author author) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE authors" +
                        " SET name = ?, birth_year = ?" +
                        " WHERE id_author = ?")) {

            statement.setString(1, author.name);
            statement.setInt(2, author.birthYear);
            statement.setInt(3, author.id);

            if (statement.executeUpdate() == 0) {
                throw new RuntimeException("Failed to update a author record");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteById(int id) {
        try (PreparedStatement statement1 = connection.prepareStatement("SELECT (1) from authors  WHERE id_author = ?")) {
            statement1.setInt(1, id);
            if (statement1.executeQuery().next()) {
                try (PreparedStatement statement =
                             connection.prepareStatement("DELETE FROM authors WHERE id_author = ? ")) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }
            } else System.out.println("Автора с таким id нет");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    private Author createAuthorFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final Author author = new Author();

        author.id = cursor.getInt("id_author");
        author.name = cursor.getString("name");
        author.birthYear = cursor.getInt("birth_year");

        return author;
    }

    @Override
    public void printAuthors(Collection<Author> authors) {
        if (!authors.isEmpty()) {
            System.out.println("------------------------------------------");
            System.out.printf("%-5s | %-20s | %-20s %n",
                    "ID", "NAME", "BIRTH YEAR");
            System.out.println("------------------------------------------");
            authors.stream().forEach(author -> {
                System.out.printf("%-5d | %-20s | %-20d %n",
                        author.id, author.name, author.birthYear);
                System.out.println("------------------------------------------");
            });
        }
    }

    @Override
    public void printAuthor(Author author) {
        System.out.println("------------------------------------------");
        System.out.printf("%-5s | %-20s | %-20s %n",
                "ID", "NAME", "BIRTH YEAR");
        System.out.println("------------------------------------------");
        System.out.printf("%-5d | %-20s | %-20d %n",
                author.id, author.name, author.birthYear);
        System.out.println("------------------------------------------");
    }

    @Override
    public void deleteAuthorsTable() {
        try {
            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate("DROP TABLE IF EXISTS authors");
            }
        } catch (SQLException e) {
            System.out.println("Не удалось удалить таблицу авторов");
        }

    }

}
