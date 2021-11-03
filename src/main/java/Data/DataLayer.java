package Data;

import javax.persistence.*;

import Classes.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    /*------------------------------------------------USER FUNCTIONS--------------------------------------------------------------*/

    //checks if email is already in, if it isnt we add the user
    public Utilizador registerUser(Utilizador user) {
        
        TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u WHERE u.email=:email", Utilizador.class)
                .setParameter("email",user.getEmail());
        List<Utilizador> u= q.getResultList();
        if(u.size()==0){
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
        else{
            return null;
        }
    }

    //we need to convert hash to string, password is not yet saved in hash
    public Utilizador loginUser(String email, String password){
        try{
            TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u WHERE u.email=:email", Utilizador.class)
                .setParameter("email",email);
            Utilizador u= q.getSingleResult();
            if(u.getPassword()==password) return u; //HERE
            return null;
        } catch(NoResultException e) {
            return null;
        }
    }

    public void incrementWallet(Utilizador user ,Double value){
        em.getTransaction().begin();
        user.balanceWallet(value);
        em.getTransaction().commit();

    }

    public void changeData(Utilizador user, String change , Integer option){
        em.getTransaction().begin();
        switch(option){
            case 1: user.setName(change); break;
            case 2: user.setPassword(change); break;
            case 3: user.setEmail(change); break;
            case 4: user.setAddress(change); break;
            case 5: user.setPhone(Integer.parseInt(change)); break;
        }
        em.getTransaction().commit();
    }

    //List trips by date
    public List<Trip> listTrips(Date start , Date end){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.time>=:start AND t.time<=:end", Trip.class)
        .setParameter("start",start)
        .setParameter("end",end);

        return q.getResultList();
    }

    // list all future trips of the user
    public List<Trip> listUserTrips(Long user_id) {

        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t INNER JOIN Ticket t1 ON t.id = t1.trip_id WHERE t1.buyer_id =:user_id AND t.time > GETDATE()", Trip.class)
                .setParameter("user_id", user_id);

        return q.getResultList();
    }

    //list all future trips
    public List<Trip> listAvailableTrips(String origin , String destination){

        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.destination=:destination AND t.origin =:origin AND t.time > GETDATE() AND (t.capacity - t.occupancy> 0)", Trip.class)
                .setParameter("destination",destination)
                .setParameter("origin",origin);

        return q.getResultList();

    }

    //create a new ticket
    public String createTicket(Trip trip, Utilizador user){        
        if(user.checkWallet( -trip.getPrice())){

            if(trip.buyTicket(user.getId())){
                user.balanceWallet(-trip.getPrice());
                em.persist(user);
                em.persist(trip); // im not sure here tho
                return "Ticket was purchased!";
            }
            return "Ticket was not purchased, you already have a ticket for this trip...";
            
        }
        return  "Wallet problems," + user.getWallet() +" is not enought to purchase this trip, please transfer some money to your account and try again";
    }

    //returns the ticket value and deletes ticket from db
    public String returnTicket(Trip trip, Utilizador user){

        TypedQuery<Ticket> q = em.createQuery("SELECT DISTINCT t1 FROM Ticket t1 WHERE t1.buyer_id =:user_id AND t1.trip_id=:trip_id", Ticket.class)
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

    /*------------------------------------------------MANAGER FUNCTIONS--------------------------------------------------------------*/







}
   