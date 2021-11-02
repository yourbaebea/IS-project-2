package Data;

import javax.persistence.*;

import Classes.*;

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

    public void RegisterUser(Utilizador user) {
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

        }catch(Exception f){
            System.out.println(f);
        }
    }

    public boolean LoginUser(String email, String password){
        //new query
        //Select * from users where email=:email
        //hashed password!!!!!!! we need to convert

        Query q = em.createQuery(
                        "SELECT c FROM Utilizador c WHERE c.email=:emailUser and c.password=:passwordUser")
                .setParameter("emailUser", email)
                .setParameter("passwordUser",password);

        List<Utilizador> lp = q.getResultList();
        return lp.size() > 0;

    }
	/*
    
    public void example(String name) {
    	String jpqlU = "SELECT DISTINCT r FROM Users r WHERE r.name = :name";
		
		TypedQuery<User> typedQueryU = em.createQuery(jpqlU, User.class);
		typedQueryU.setParameter("name", name);
		User mylistU = typedQueryU.getSingleResult();
    	
		do stuff.........
    	em.persist(.....);  	
    }
	*/

}
   