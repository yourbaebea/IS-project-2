package Bean;

import org.hibernate.annotations.Remove;

import Classes.*;

@Remove
public interface UserBeanRemote {
	public void RegisterUser(User user);
    
}
