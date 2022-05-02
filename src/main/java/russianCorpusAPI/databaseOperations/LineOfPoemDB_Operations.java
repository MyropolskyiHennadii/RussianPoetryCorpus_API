package russianCorpusAPI.databaseOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import russianCorpusAPI.model.LineOfPoem;
import russianCorpusAPI.model.Poem;

import javax.persistence.*;
import java.util.List;

public class LineOfPoemDB_Operations {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("poems_remote_admin");
    private final EntityManager em = emf.createEntityManager();
    private static final Logger logger
            = LoggerFactory.getLogger(LineOfPoemDB_Operations.class);

    public LineOfPoem getLineOfPoemByID(int id) {
        //EntityManager em = emf.createEntityManager();
        return em.find(LineOfPoem.class, id);
    }

    public LineOfPoem getLineOfPoemByPoemAndRowKey(Poem poem, int row_key) {
        LineOfPoem lineOfPoem;
        TypedQuery<LineOfPoem> q = em.createQuery("SELECT a FROM LineOfPoem a WHERE a.row_key = '" + row_key + "' AND a.poem = :poem", LineOfPoem.class);
        q.setParameter("poem", poem);
        try {
            lineOfPoem = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return lineOfPoem;
    }

    public boolean writeLines(List<LineOfPoem> lineOfPoemList) {
        //EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();
        t.begin();
        for (LineOfPoem line : lineOfPoemList) {
            em.persist(line);
        }
        t.commit();
        return true;
    }

    public boolean updateLines(List<LineOfPoem> lineOfPoemList) {
        //EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();
        t.begin();
        for (LineOfPoem line : lineOfPoemList) {
            em.merge(line);
        }
        t.commit();
        return true;
    }
}
