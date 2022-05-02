package russianCorpusAPI.databaseOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import russianCorpusAPI.model.Author;

import javax.persistence.*;

public class AuthorDB_Operations {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("authors_remote_admin");
    private final EntityManager em = emf.createEntityManager();
    private static final Logger logger
            = LoggerFactory.getLogger(AuthorDB_Operations.class);

    public Author getAuthorByName(String name) {
        Author author;
        //EntityManager em = emf.createEntityManager();
        TypedQuery<Author> q = em.createQuery("SELECT a FROM Author a WHERE a.authors_name = '" + name + "'", Author.class);
        try {
            author = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return author;
    }

    public boolean writeAuthor(Author author) {
        //EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();
        t.begin();
        em.persist(author);
        t.commit();
        return true;
    }
}


