package Data;

import javax.persistence.*;

import Classes.*;

import java.util.Date;
import java.util.List;

import static java.lang.System.*;

/**
 * Session Bean implementation class Bean
 */
//@Stateless
//@LocalBean
public class DataLayer{

	private EntityManagerFactory emf;
    private EntityManager em;
    public DataLayer() {
        super();
        try {
            emf = Persistence.createEntityManagerFactory("Bus");
            em = emf.createEntityManager();
        }catch(Exception e){
           System.out.println(e);
        }
    }

    public void registerUser(Utilizador user) {
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

        }catch(Exception f){
            System.out.println(f);
        }
    }

    public Utilizador loginUser(String email, String password){
        //new query
        //Select * from users where email=:email
        //hashed password!!!!!!! we need to convert

        Query q = em.createQuery("SELECT c FROM Utilizador c ");
        List<Utilizador> lp = q.getResultList();
        for(Utilizador l : lp){
            if(l.getEmail().equals(email) && l.getPassword().equals(password)){
                return l;
            }
        }
        return null;
    }

    public void incrementWallet(Utilizador user ,Double value){
        em.getTransaction().begin();
        user.balanceWallet(value);
        em.getTransaction().commit();

    }

    public void changeData(Utilizador user, String change , Integer option){
        em.getTransaction().begin();
        switch(option){
            case 1:
                user.setName(change);
                break;
            case 2:
                user.setPassword(change);
                break;
            case 3:
                user.setEmail(change);
                break;
            case 4:
                user.setAddress(change);
                break;
            case 5:
                user.setPhone(Integer.parseInt(change));
                break;
        }
        em.getTransaction().commit();
    }

    public List<Trip> listTrips(Date start , Date end){

        Query f = em.createQuery("SELECT t FROM Trip t WHERE t.time>=:start OR t.time<=:end")
                .setParameter("start",start)
                .setParameter("end",end);

        return f.getResultList();
    }
}
   