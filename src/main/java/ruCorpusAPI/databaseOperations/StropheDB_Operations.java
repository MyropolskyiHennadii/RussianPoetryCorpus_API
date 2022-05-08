package ruCorpusAPI.databaseOperations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ruCorpusAPI.model.LineOfPoem;
import ruCorpusAPI.model.Strophe;

import javax.persistence.*;
import java.util.List;

public class StropheDB_Operations {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("strophes_remote_admin");
    private EntityManager em = emf.createEntityManager();
    private static final Logger logger = LogManager.getLogger(StropheDB_Operations.class);

    public Strophe getStropheByID(int id) {
       // EntityManager em = emf.createEntityManager();
        return em.find(Strophe.class, id);
    }

    public Strophe getStropheByLineOfPoem(LineOfPoem lineOfPoem) {
        Strophe strophe;
        TypedQuery<Strophe> q = em.createQuery("SELECT a FROM Strophe a WHERE a.lineOfPoem =: lineOfPoem", Strophe.class);
        q.setParameter("lineOfPoem", lineOfPoem);
        try {
            strophe = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return strophe;
    }

    public boolean writeLines(List<Strophe> stropheList) {
        EntityTransaction t = em.getTransaction();
        t.begin();
        for (Strophe strophe : stropheList) {
            em.persist(strophe);
        }
        t.commit();
        return true;
    }
}
