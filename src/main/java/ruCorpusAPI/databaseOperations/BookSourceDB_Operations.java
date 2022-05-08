package ruCorpusAPI.databaseOperations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ruCorpusAPI.model.Author;
import ruCorpusAPI.model.BookSource;

import javax.persistence.*;

public class BookSourceDB_Operations {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("books_source_remote_admin");
    private final EntityManager em = emf.createEntityManager();
    private static final Logger logger = LogManager.getLogger(BookSourceDB_Operations.class);

    public BookSource getBookSourceByID(int id) {
        //EntityManager em = emf.createEntityManager();
        return em.find(BookSource.class, id);
    }


    public BookSource getBookByName(String bookName) {
        BookSource bookSource;
        //EntityManager em = emf.createEntityManager();
        TypedQuery<BookSource> q = em.createQuery("SELECT a FROM BookSource a WHERE a.book_title = '" + bookName + "'", BookSource.class);

        try {
            bookSource = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return bookSource;
    }

    public boolean writeBookSource(BookSource bookSource) {
        //EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();
        t.begin();
        em.persist(bookSource);
        t.commit();
        return true;
    }

    public BookSource getBookByNameAndAuthor(String bookName, Author author) {
        BookSource bookSource;
        //EntityManager em = emf.createEntityManager();
        TypedQuery<BookSource> q = em.createQuery("SELECT a FROM BookSource a WHERE a.book_title = '" + bookName + "' AND a.author.authors_name = '" + author.getAuthors_name() + "'", BookSource.class);
        try {
            bookSource = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return bookSource;
    }

    public BookSource getBookByNameAndAuthor(String bookName, String authorName) {
        BookSource bookSource;
        //EntityManager em = emf.createEntityManager();
        TypedQuery<BookSource> q = em.createQuery("SELECT a FROM BookSource a WHERE a.book_title = '" + bookName + "' AND a.author.authors_name = '" + authorName + "'", BookSource.class);
        try {
            bookSource = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return bookSource;
    }

}
