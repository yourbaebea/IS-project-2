package EJB.ejb;

import JPA.jpa.*;
import EJB.ejb.EJBEmail;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.Schedule;
import javax.persistence.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Stateless
@LocalBean
public class EJBManager implements EJBManagerRemote {

	private EntityManagerFactory emf;
    private EntityManager em;
    public EJBManager(){}
    public void connect(){
        try {
            emf = Persistence.createEntityManagerFactory("Bus");
            em = emf.createEntityManager();
        }catch(Exception e){
            System.out.println("Error in connection to persistence in EJBManager: "+ e);
        }
    }

    //checks if email is already in, if it isnt we add the manager
    public Manager registerManager(Manager manager) {
        
        TypedQuery<Manager> q = em.createQuery("SELECT DISTINCT m FROM Manager m WHERE m.email=:email", Manager.class)
                .setParameter("email",manager.getEmail());
        List<Manager> m= q.getResultList();
        if(m.size()==0){
            em.getTransaction().begin();
            manager.setPassword(manager.getHashedPassword());
            em.persist(manager);
            em.getTransaction().commit();
            return manager;
        }
        else{
            return null;
        }
    }

    //we need to convert hash to string, password is not yet saved in hash
    public Manager loginManager(String email, String password){
        try{
            TypedQuery<Manager> q = em.createQuery("SELECT DISTINCT m FROM Manager m WHERE m.email=:email", Manager.class)
                .setParameter("email",email);
            Manager m= q.getSingleResult();
            if(m.getPassword().equals(getHashedPassword(password))) return m; //HERE
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
   
    //create a new trip
    public boolean createTrip(Trip trip){
        em.getTransaction().begin();
        em.persist(trip);
        em.getTransaction().commit();
        return true; //"New Trip created!";
    }

    //List trips by date
    public List<Trip> listTrips(Date start , Date end){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.time>=:start AND t.time<:end ORDER BY t.time", Trip.class)
                .setParameter("start",start)
                .setParameter("end",end);

        return q.getResultList();
    }

    //List all trips
    public List<Trip> listAllTrips(){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE t.time> current_date ORDER BY t.time", Trip.class);
        return q.getResultList();
    }

    //List 
    public List<Trip> listDailyTrips(Date start){
        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE  t.time=:day ORDER BY t.time", Trip.class)
        .setParameter("day", start);
        return q.getResultList();
    }
    
    //Delete trip, still missing email stuff IM REALLY NOT SURE HERE HOW TO DELETE ALL OF THIS
    public boolean deleteTrip(Trip trip){
        TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u INNER JOIN Ticket t1 ON u.id = t1.buyer_id WHERE t1.trip.id =:trip_id", Utilizador.class)
        .setParameter("trip_id",trip.getId());

        List<Utilizador> u= q.getResultList();

        if(u.size()!=0){

            for(Utilizador user: u){
                incrementWallet(user, trip.getPrice());
                //whatever email means should be here
            }

           em.getTransaction().begin();
            int deletedCount = em.createQuery("DELETE FROM Ticket t1 WHERE t1.trip.id=:trip_id")
                    .setParameter("trip_id",trip.getId()).executeUpdate();
            em.getTransaction().commit();
        }

        DeletedTripEmail(u, trip);


        em.getTransaction().begin();
        em.remove(trip); //Delete trip
        em.getTransaction().commit();

        return true; //"Trip was deleted, all the purchases were returned and emails sent!";
    
    }

    //List top users
    public List<Object[]> listTopUsers(){
        TypedQuery<Object[]> q = em.createQuery("SELECT u.name, COUNT(t1.buyer_id) as c FROM Ticket t1 INNER JOIN Utilizador u ON t1.buyer_id = u.id GROUP BY u.name ORDER BY c DESC, u.name DESC LIMIT 5",Object[].class);
        try {
            List<Object[]> list = q.getResultList();
            if(list.size()>0) return list;
            else return null;
        } catch (Exception e) {
            return null;
        }
    }

    //List users in trip
    public List<Utilizador> getUsers(Trip trip){
        TypedQuery<Utilizador> q = em.createQuery("SELECT DISTINCT u FROM Utilizador u INNER JOIN Ticket t1 ON u.id = t1.buyer_id WHERE t1.trip.id =:trip_id ORDER BY u.name", Utilizador.class)
        .setParameter("trip_id",trip.getId());
        return q.getResultList();
    }

    private List<Trip> getRevenueTrips() {

        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String date = formatter.format(now);
        String [] date2 = date.split(" ");
        date2[1] = "00:00";
        String todayl = date2[0]+" "+date2[1];
        Date today = null;
        try {
            today = formatter.parse(todayl);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TypedQuery<Trip> q = em.createQuery("SELECT DISTINCT t FROM Trip t WHERE  t.time= :day AND t.time < now()  ORDER BY t.time", Trip.class)
            .setParameter("day", today);
        
        List<Trip> trips= q.getResultList();
        return trips;
    }

    @Schedule(hour="23", minute="59", second="0")
    public void scheduledEmail(){
        List<Trip> trips = getRevenueTrips();

        Double count_money=0.0;
        int count_people=0;
        for(Trip t: trips){
            count_money += t.getOccupancy() * t.getPrice();
            count_people += t.getOccupancy();
        }
        String message;
        if (count_people>0) message="Earned "+ count_money + " euros from " + trips.size() + " trips, with a total of "+ count_people + " tickets bought!\n";
        else{
            message="No one bought tickets today!\n";
        }

        TypedQuery<Manager> q = em.createQuery("SELECT DISTINCT m FROM Manager m", Manager.class);
        List<Manager> m= q.getResultList();
        if(m.size()<=0) return;

        EJBEmail email= new EJBEmail();
        for(Manager manager: m)  email.sendEmail(manager.getEmail(), "Bus company- Daily Revenue", message);

        return;

    }


    public void DeletedTripEmail(List<Utilizador> utilizadores, Trip t){

        if(utilizadores.size()<=0) return;

        EJBEmail email= new EJBEmail();
        String topline="Trip " + t.getId() + " Deleted";
        String message= "Dear customer, the trip\n"+ t.toString() +"\n was unfortunaly cancelled, the value of "+ t.getPrice() + "was returned to your wallet. We are sorry for the inconvinience,\nBus Company\n";
        for(Utilizador u: utilizadores)  email.sendEmail(u.getEmail(), "Bus company- Daily Revenue", message);

        return;

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

}
