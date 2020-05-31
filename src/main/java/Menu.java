import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;

public class Menu {
    private final IAuthorRepository authorRepository;
    private final IBookRepository bookRepository;

    public Menu(IAuthorRepository authorRepository, IBookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public static void printMenu() {

        System.out.println("1. Для добавления нового автора введите 1");
        System.out.println("2. Для добавления новой книги введите 2");
        System.out.println("3. Для вывода всех авторов введите 3");
        System.out.println("4. Для вывода всех книг введите 4");
        System.out.println("5. Чтобы вывести автора по id введите 5");
        System.out.println("6. Чтобы вывести книгу по id введите 6");
        System.out.println("7. Чтобы удалить книгу по id введите 7");
        System.out.println("8. Чтобы удалить автора по id введите 8");
        System.out.println("9. Чтобы найти книги по имени автора введите 9");
        System.out.println("10. Чтобы найти книги в промежутке между выбранными годами введите 10");
        System.out.println("11. Чтобы удалить таблицу книг введите 11");
        System.out.println("12. Чтобы удалить таблицу авторов введите 12");

        System.out.println("Для выхода нажмите 'Q' ");


    }

    public static void start(Scanner scanner, IBookRepository bookRepository, IAuthorRepository authorRepository) {
        String s;
        bookRepository.initialize();
        authorRepository.initialize();
        while (true) {
            printMenu();
            s = scanner.next();

            switch (s) {

                case "1":
                    saveAuthor(scanner, authorRepository);
                    break;
                case "2":
                    saveBook(scanner, bookRepository, authorRepository);
                    break;
                case "3":
                    Collection<Author> authors = authorRepository.getAll();
                    authorRepository.printAuthors(authors);
                    break;
                case "4":
                    Collection<Book> books = bookRepository.getAll();
                    bookRepository.printBooks(books);
                    break;
                case "5":
                    System.out.println("Для поиска автора по id введите число и нажмите Enter");
                    if (scanner.hasNextInt()) {
                        Optional<Author> author2 = authorRepository.getById(scanner.nextInt());
                        if (!author2.isEmpty()) {
                            authorRepository.printAuthor(author2.get());
                        } else System.out.println("Нет автора с таким id ");
                    } else {
                        System.out.println("Вы ввели не число, попробуйсте снова");
                        break;
                    }
                    break;
                case "6":
                    System.out.println("Для поиска книги по id введите число и нажмите Enter");
                    if (scanner.hasNextInt()) {
                        int n = scanner.nextInt();
                        Optional<Book> book1 = bookRepository.getById(n);
                        if (!book1.isEmpty()) {
                            bookRepository.printBook(book1.get());
                        } else System.out.println("Нет книги с таким id ");
                    } else {
                        System.out.println("Вы ввели не число, попробуйсте снова");
                        break;
                    }
                    break;
                case "7":
                    System.out.println("Для удаления книги по id введите число и нажмите Enter");
                    if (scanner.hasNextInt()) {
                        int n = scanner.nextInt();
                        bookRepository.deleteById(n);
                    } else {
                        System.out.println("Вы ввели не число, попробуйсте снова");
                        break;
                    }
                    break;
                case "8":
                    System.out.println("Для удаления автора по id введите число и нажмите Enter");
                    if (scanner.hasNextInt()) {
                        int n = scanner.nextInt();
                        authorRepository.deleteById(n);
                    } else {
                        System.out.println("Вы ввели не число, попробуйсте снова");
                        break;
                    }
                    break;
                case "9":
                    System.out.println("Для поиска книг по автору введите его имя и нажмите Enter");
                    s = scanner.next();
                    Optional<Collection<Book>> booksByAuthors = bookRepository.searchBookByAuthorName(s);
                    if (!booksByAuthors.isEmpty()) {
                        System.out.println("Найденные книги");
                        Collection<Book> books1 = booksByAuthors.get();
                        bookRepository.printBooks(books1);
                    } else System.out.println("Книг по заданному поиску не найдено");
                    break;
                case "10":
                    int year1;
                    int year2;
                    System.out.println("Введите начало периода для поиска");
                    if (scanner.hasNextInt()) {
                        year1 = scanner.nextInt();
                    } else {
                        System.out.println("Вы ввели не число, повторите поиск");
                        break;
                    }
                    System.out.println("Введите конец периода для поиска");
                    if (scanner.hasNextInt()) {
                        year2 = scanner.nextInt();
                    } else {
                        System.out.println("Вы ввели не число, повторите поиск");
                        break;
                    }
                    Optional<Collection<Book>> booksByPeriod = bookRepository.getBookBetweenYear(year1, year2);
                    if (!booksByPeriod.isEmpty()) {
                        System.out.println("Найденные книги");
                        Collection<Book> booksPeriod = booksByPeriod.get();
                        System.out.println(booksPeriod.isEmpty());
                        bookRepository.printBooks(booksPeriod);
                    } else System.out.println("Книг по заданному поиску не найдено");
                    break;
                case "11":
                    bookRepository.deleteBooksTable();
                    System.out.println("Таблица книг удалена");
                    break;
                case "12":
                    authorRepository.deleteAuthorsTable();
                    System.out.println("Таблица авторов удалена");
                    break;
                case "Q":
                case "q": {
                    System.out.println("До свидания!");
                    return;
                }
                default: {
                    System.out.println("Вы выбрали не корректный пункт меню, повторите пожалуйста свой выбор!");
                }
            }
        }
    }

    private static void saveBook(Scanner scanner, IBookRepository bookRepository, IAuthorRepository authorRepository) {
        String s;
        Book book = new Book();
        System.out.println("Для добавления новой книги введите его название (от 2х до 200 символов) и нажмите Enter");
        s = scanner.next();
        if (s.length() >= 2 && s.length() <= 200) { //проверяем условие по длине
            book.title = s;
        } else {
            System.out.println("Вы ввели некорректное имя, попробуйте снова");
            return;
        }
        System.out.println("Добавьте год публикации  и нажмите Enter");
        if (scanner.hasNextInt()) {
            book.publishYear = scanner.nextInt();
        } else {
            System.out.println("Вы ввели не число, попробуйсте снова");
            return;
        }
        System.out.println("Добавьте количество страниц  и нажмите Enter");
        if (scanner.hasNextInt()) {
            book.pagesCount = scanner.nextInt();
        } else {
            System.out.println("Вы ввели не число, попробуйсте снова");
            return;
        }

        System.out.println("Добавьте автора");

        Author author = saveAuthor(scanner, authorRepository);
        authorRepository.save(author);
        book.author = author;
        bookRepository.save(book);
        System.out.println("Спасибо, книга  была сохранена");
    }

    private static Author saveAuthor(Scanner scanner, IAuthorRepository authorRepository) {
        String s;
        Author author = new Author();
        System.out.println("Для добавления нового автора введите его имя (от 2х до 200 символов) и нажмите Enter");
        s = scanner.next();

        if (s.length() >= 2 && s.length() <= 200) { //проверяем условие по длине
            author.name = s;
        } else {
            System.out.println("Вы ввели некорректное имя, попробуйте снова");
        }

        System.out.println("Добавьте год рождения и нажмите Enter");
        if (scanner.hasNextInt()) {
            author.birthYear = scanner.nextInt();
        } else {
            System.out.println("Вы ввели не число, попробуйсте снова");
        }
        authorRepository.save(author);
        System.out.println("Спасибо, автор был успешно добавлен");
        return author;
    }
}

