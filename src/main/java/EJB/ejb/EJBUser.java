package EJB.ejb;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.*;

import JPA.jpa.Utilizador;
import JPA.jpa.*;

@Stateless
@LocalBean
public class EJBUser implements EJBUserRemote {

	private EntityManagerFactory emf;
    private EntityManager em;

    public EJBUser(){}
    public void connect(){
        try {
            emf = Persistence.createEntityManagerFactory("Bus");
            em = emf.createEntityManager();
        }catch(Exception e){
            System.out.println("Error in connection to persistence in EJBUser: "+ e);
        }
    }

    
    //checks if email is already in, if it isnt we add the user
    public Utilizador registerUser(Utilizador user) {
        
        TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u WHERE u.email=:email", Utilizador.class)
                .setParameter("email",user.getEmail());
        List<Utilizador> u= q.getResultList();
        if(u.size()==0){
            em.getTransaction().begin();
            user.setPassword(user.getHashedPassword()); //Encriptar a passe
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
        else{
            return null;
        }
    }

    //Login User
    public Utilizador loginUser(String email, String password){
        try{
            TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u WHERE u.email=:email", Utilizador.class)
                .setParameter("email",email);
            Utilizador u= q.getSingleResult();
            if(u.getPassword().equals(getHashedPassword(password))) return u; //HERE
            return null;
        } catch(NoResultException e) {
            return null;
        }
    }

    //Increment Money on Wallet
    public void incrementWallet(Utilizador user ,Double value){
        em.getTransaction().begin();
        user.balanceWallet(value);
        em.getTransaction().commit();

    }

    //Change personal information
    public boolean changeData(Utilizador user, String change , Integer option){
        em.getTransaction().begin();
        switch(option){
            case 1: user.setName(change); break;
            case 2: user.setPassword(getHashedPassword(change)); break;
            case 3: user.setEmail(change); break;
            case 4: user.setAddress(change); break;
            case 5: user.setPhone(Integer.parseInt(change)); break;
        }
        em.getTransaction().commit();
        if(option<=0 && option > 5) return false;
        return true;
    }

    //List trips by date
    public List<Trip> listTrips(Date start , Date end){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.time>=:start AND t.time<:end ORDER BY t.time", Trip.class)
        .setParameter("start",start)
        .setParameter("end",end);

        return q.getResultList();
    }

    // list all future trips of the user
    public List<Trip> listUserTrips(Long user_id) {

        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t INNER JOIN Ticket t1 ON t.id = t1.trip.id WHERE t1.buyer_id =:user_id AND t.time > now()  ORDER BY t.time", Trip.class)
                .setParameter("user_id", user_id);

        return q.getResultList();
    }

    //list all future trips
    public List<Trip> listAvailableTrips(String origin , String destination){

        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.destination=:destination AND t.origin =:origin AND t.time > now() AND t.occupancy < t.capacity ORDER BY t.time", Trip.class)
                .setParameter("destination",destination)
                .setParameter("origin",origin);


        return q.getResultList();

    }

	//create a new ticket
	public int createTicket(Trip trip, Utilizador user){        
        if(user.checkWallet( - trip.getPrice())){
            Ticket t = trip.buyTicket(user.getId());
            if(t!=null){
                user.balanceWallet(-trip.getPrice());
                em.getTransaction().begin();
				em.persist(t);
                em.getTransaction().commit();
                return 1; //"Ticket was purchased!"
            }
            return -1 ; //"Ticket was not purchased, you already have a ticket for this trip...
        }
        return -2; // "Wallet problems," + user.getWallet() +" is not enought to purchase this trip, please transfer some money to your account and try again";
    }

    //returns the ticket value and deletes ticket from db
    public String returnTicket(Trip trip, Utilizador user){

        TypedQuery<Ticket> q = em.createQuery("SELECT DISTINCT t1 FROM Ticket t1 WHERE t1.buyer_id =:user_id AND t1.trip.id=:trip_id", Ticket.class)
                .setParameter("user_id", user.getId())
                .setParameter("trip_id", trip.getId());
        
        Ticket t;
        try{
            t=  q.getSingleResult();
        }
        catch(NoResultException e){
            return "Ticket was not return, this actually should never happen";
        }
        em.getTransaction().begin();
        em.remove(t);
        user.balanceWallet(trip.getPrice());
        trip.returnTicket(user.getId());

        em.getTransaction().commit();

        return "Ticket was returned!";

    }

    public String getHashedPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            String hash = String.format("%X", ByteBuffer.wrap(digest).getLong()); //Hex.encodeHexString(digest);//.toLowerCase();
            return hash;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            // return not hashed password
            return password;
        }
    }

    public void deleteAccount(Utilizador user){
        //Eliminar os bilhetes do user
        TypedQuery<Ticket> q = em.createQuery("SELECT DISTINCT t1 FROM Ticket t1 WHERE t1.buyer_id =:user_id", Ticket.class)
                .setParameter("user_id", user.getId());
        List<Ticket> tickets = q.getResultList();

        //Retorna os bilhetes do user e elimina na bd
        for(Ticket t : tickets ){
            returnTicket(t.getTrip(),user);
        }

        //Elimina o user
        em.getTransaction().begin();
        em.remove(user);
        em.getTransaction().commit();
    }

}
