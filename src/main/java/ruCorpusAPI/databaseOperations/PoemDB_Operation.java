package ruCorpusAPI.databaseOperations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ruCorpusAPI.model.Author;
import ruCorpusAPI.model.BookSource;
import ruCorpusAPI.model.Poem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class PoemDB_Operation {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("metrical_lines_remote_admin");
    private final EntityManager em = emf.createEntityManager();

    private static final Logger logger = LogManager.getLogger(PoemDB_Operation.class);

    public Poem getPoemByID(int id) {
        //EntityManager em = emf.createEntityManager();
        return em.find(Poem.class, id);
    }

    public Poem getPoemByAuthorAndBookAndOldID(String authorName, String bookName, int old_id,
                                               AuthorDB_Operations authorDB_operations, BookSourceDB_Operations bookSourceDB_operations) {
        Author author = authorDB_operations.getAuthorByName(authorName);
        if (author == null) {
            logger.error("Unknown author for {} {} {}", authorName, bookName, old_id);
            return null;
        }
        BookSource bookSource = bookSourceDB_operations.getBookByNameAndAuthor(bookName, author);
        if (bookSource == null) {
            logger.error("Unknown book_source for {} {} {}", authorName, bookName, old_id);
            return null;
        }
        Poem poem = null;
        //EntityManager em = emf.createEntityManager();
        TypedQuery<Poem> q = em.createQuery("SELECT a FROM Poem a WHERE a.old_id = '" + old_id + "' AND a.book_source = :bookSource", Poem.class);
        q.setParameter("bookSource", bookSource);
        try {
            poem = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return poem;
    }

    public boolean writePoem(List<Poem> poems) {
        //EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();
        t.begin();
        for (Poem poem : poems) {
            em.persist(poem);
        }
        t.commit();
        return true;
    }

    public List<Poem> getPoemsByAuthorID(int id){
        TypedQuery<Poem> q = em.createQuery("SELECT a FROM Poem a WHERE a.book_source.author.id_author = '" + id + "'", Poem.class);
        return q.getResultList();
    }

    public List<Poem> getPoemsByBook(BookSource bookSource) {
        List<Poem> poemList = new ArrayList<>();
        TypedQuery<Poem> q = em.createQuery("SELECT a FROM Poem a WHERE a.book_source = :bookSource", Poem.class);
        q.setParameter("bookSource", bookSource);
        try {
            poemList = q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
        return poemList;
    }

}
