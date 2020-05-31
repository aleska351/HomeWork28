import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        try (Connection connection = getConnection()) {
            JdbcAuthorRepository authorRepository = new JdbcAuthorRepository(connection);
            JDBCBookRepository bookRepository = new JDBCBookRepository(connection, authorRepository);
            Menu.start(scan, bookRepository, authorRepository);
        } catch (SQLException | IOException e) {
            System.err.println("Failed: " + e.getMessage());
        }
    }

    public final static Connection getConnection() throws SQLException, IOException {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
            properties.load(in);
        }
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        System.out.println("Подключение успешно");
        return DriverManager.getConnection(url, username, password);
    }
}

