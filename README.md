# HomeWork28
Java Elementary
1. Объявить классы книг и авторов, где у каждой книги может быть один автор.
2.Определить структуру отдельных таблиц для книг (book) и авторов (author) с использованием первичных ключей. Поле author_id в таблице книг указывает на запись в таблице авторов (связь один-ко-многим).
3. Объявить интерфейс IBookRepository для доступа к книгам:
a. Добавить основные методы getAll, getById, update, delete
b. Добавить дополнительные методы на свое усмотрение
c. Реализовать, используя JDBC и SQL
d. Экземпляр книги включает в себя экземпляр автора, поэтому запросы идут в обе таблицы
4. Объявить интерфейс IAuthorRepository для доступа к авторам:
a. Добавить основные методы getAll, getById, update, delete
b. Добавить дополнительные методы на свое усмотрение
c. Реализовать, используя JDBC и SQL
d. Обновить реализацию IBookRepository с учетом использования IAuthorRepository для доступа к авторам.
