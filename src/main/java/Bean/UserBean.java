package Bean;

import java.util.List;

import javax.persistence.*;

import Classes.*;

/**
 * Session Bean implementation class Bean
 */
//@Stateless
//@LocalBean
public class UserBean implements UserBeanRemote {

	
    public UserBean() {
        // TODO Auto-generated constructor stub
    }
    
    public void RegisterUser(User user) {
		//em.persist(user)
    	
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
   