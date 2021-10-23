package jpaprimer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Reader {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Players");
        EntityManager em = emf.createEntityManager();
        //Query q = em.createQuery("select * from Professor p");
        Query q = em.createQuery("from Player p");

        List<Player> lp = q.getResultList();
        for (Player p : lp) {
            System.out.println(p);
        }
    }
    
}
