package JPA.jpa;

import java.text.*;
import java.util.*;

import EJB.ejb.EJBManagerRemote;
import EJB.ejb.EJBManager;
import jakarta.validation.constraints.Null;
import org.apache.commons.validator.routines.EmailValidator;


public class JPAWrite
{

    public static final Scanner sc = new Scanner(System.in);
    public static EJBManager mb =null;

	public JPAWrite(){}

	boolean connect()
	{
		try
		{
			mb= new EJBManager();
			mb.connect();

			//bean =(ManagerBeanRemote)context.lookup();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args)
	{
		JPAWrite manager = new JPAWrite();
			if(manager.connect()) { // this connects the EJB to the db
				mb.registerManager(new Manager("Hugo", "1", "Rua 1", Integer.parseInt("924306192"),"1@gmail.com"));
				mb.registerManager(new Manager("Beatriz", "2", "Rua 2", Integer.parseInt("924306142"),"2@gmail.com"));
			}

		System.out.println("Just for debug");
		/*List<Manager> managers= manager.mb.getManagers();
		for(Manager m: managers) m.toString();*/

		System.out.println("Leaving JPA of Manager Registration, this file should not be for the public");
    }
}
