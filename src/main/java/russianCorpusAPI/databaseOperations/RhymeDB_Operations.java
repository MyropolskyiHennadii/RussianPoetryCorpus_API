package russianCorpusAPI.databaseOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import russianCorpusAPI.model.LineOfPoem;
import russianCorpusAPI.model.Rhyme;

import javax.persistence.*;
import java.util.List;

public class RhymeDB_Operations {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("rhymes_remote_admin");
    private final EntityManager em = emf.createEntityManager();
    private static final Logger logger
            = LoggerFactory.getLogger(RhymeDB_Operations.class);

    public Rhyme getRhymeByID(int id) {
        //EntityManager em = emf.createEntityManager();
        return em.find(Rhyme.class, id);
    }

    public Rhyme getRhymeByLineOfPoem(LineOfPoem lineOfPoem) {
        Rhyme rhyme;
        TypedQuery<Rhyme> q = em.createQuery("SELECT a FROM Rhyme a WHERE a.lineOfPoem =: lineOfPoem", Rhyme.class);
        q.setParameter("lineOfPoem", lineOfPoem);
        try {
            rhyme = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return rhyme;
    }


    public boolean writeLines(List<Rhyme> rhymeList) {
        EntityTransaction t = em.getTransaction();
        t.begin();
        for (Rhyme rhyme : rhymeList) {
            em.persist(rhyme);
        }
        t.commit();
        return true;
    }
}
