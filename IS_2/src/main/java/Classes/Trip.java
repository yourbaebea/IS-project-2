package Classes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
public class Trip implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    
	private String origin;
    private String destination;
    private Date time;
    private double price;
	private int capacity;
    private int occupancy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip")
    private List<Ticket> tickets;

    public Trip(){}

    public Trip(String origin, String destination, Date time, double price, int capacity){
        this.origin= origin;
        this.destination=destination;
        this.time=time;
        this.price=price;
        this.capacity=capacity;
        this.occupancy=0;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean returnTicket(Long buyer_id){
        if (this.occupancy>0){
            for(Ticket t: this.tickets) if(t.getBuyer_id()== buyer_id){
                this.tickets.remove(t);
                this.occupancy--;
                return true;
            }
        }
        return false;

    }

    public double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Trip: origin "+ this.origin+ ", destination "+ this.destination + ", date "+ this.time + ", price "+ this.price + ", occupancy "+ this.occupancy+ "/"+ this.capacity;
    }
    
}
