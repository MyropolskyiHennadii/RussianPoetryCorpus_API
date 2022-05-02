package russianCorpusAPI.databaseOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import russianCorpusAPI.model.GrammaticalAnalysisOfLine;
import russianCorpusAPI.model.LineOfPoem;

import javax.persistence.*;
import java.util.List;

public class GrammaticalAnalysisOfLineDB_Operations {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("grammatical_lines_remote_admin");
    private static final Logger logger
            = LoggerFactory.getLogger(GrammaticalAnalysisOfLineDB_Operations.class);
    private final EntityManager em = emf.createEntityManager();

    public GrammaticalAnalysisOfLine getGrammaticalAnalysisOfLineByID(int id) {
        //EntityManager em = emf.createEntityManager();
        return em.find(GrammaticalAnalysisOfLine.class, id);
    }


    public GrammaticalAnalysisOfLine getGrammaticalLineOfPoemByLineOfPoem(LineOfPoem lineOfPoem) {
        GrammaticalAnalysisOfLine grammaticalAnalysisOfLine;
        TypedQuery<GrammaticalAnalysisOfLine> q = em.createQuery("SELECT a FROM GrammaticalAnalysisOfLine a WHERE a.lineOfPoem =: lineOfPoem", GrammaticalAnalysisOfLine.class);
        q.setParameter("lineOfPoem", lineOfPoem);
        try {
            grammaticalAnalysisOfLine = q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return grammaticalAnalysisOfLine;
    }

    public boolean writeLines(List<GrammaticalAnalysisOfLine> grammaticalAnalysisOfLinesList) {
        //EntityManager em = emf.createEntityManager();
        EntityTransaction t = em.getTransaction();
        t.begin();
        for (GrammaticalAnalysisOfLine line : grammaticalAnalysisOfLinesList) {
            em.persist(line);
        }
        t.commit();
        return true;
    }

    public void geleteAll() {
        TypedQuery<GrammaticalAnalysisOfLine> q = em.createQuery("SELECT a FROM GrammaticalAnalysisOfLine a", GrammaticalAnalysisOfLine.class);
        List<GrammaticalAnalysisOfLine> listToDelete = q.getResultList();

        EntityTransaction t = em.getTransaction();
        t.begin();
        for (GrammaticalAnalysisOfLine line : listToDelete) {
            em.remove(line);
        }
        t.commit();
    }

}
