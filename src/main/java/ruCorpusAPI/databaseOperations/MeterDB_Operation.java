package ruCorpusAPI.databaseOperations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ruCorpusAPI.model.MeterGroup;

import javax.persistence.*;

public class MeterDB_Operation {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("meters_remote_admin");
    private static final Logger logger = LogManager.getLogger(MeterDB_Operation.class);

    public MeterGroup getMeterByID(int id) {
        EntityManager em = emf.createEntityManager();
        return em.find(MeterGroup.class, id);
    }

    public MeterGroup getMeterByName(String name){
        MeterGroup meterGroup = null;
        EntityManager em = emf.createEntityManager();
        TypedQuery<MeterGroup> q = em.createQuery("SELECT a FROM MeterGroup a WHERE a.meter_name = '" + name + "'", MeterGroup.class);
        try {
            EntityTransaction t = em.getTransaction();
            try {
                t.begin();
                meterGroup = q.getSingleResult();
                t.commit();
            } catch (NoResultException e){
                return null;
            }finally {
                if (t.isActive()) {
                    logger.error("-----------------Something wrong with getting Meter from database!");
                    t.rollback();
                }
            }
        } finally {
            em.close();
        }
        return meterGroup;
    }

    public boolean writeMeter(MeterGroup meterGroup) {
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction t = em.getTransaction();
            try {
                t.begin();
                em.persist(meterGroup);
                t.commit();
            } finally {
                if (t.isActive()) {
                    logger.error("-----------------Something wrong with writing Meter to database!");
                    t.rollback();
                }
                return false;
            }
        } finally {
            em.close();
            return true;
        }
    }
}
