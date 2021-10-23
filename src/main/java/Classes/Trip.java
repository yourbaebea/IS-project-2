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

    public boolean createTicket(Long buyer_id){
        if (this.capacity>this.occupancy){
            for(Ticket t: this.tickets) if(t.getBuyer_id()== buyer_id){
                return false;
            }
            this.occupancy++;
            this.tickets.add(new Ticket(buyer_id));
            return true;
        }
        return false;

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


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Trip: origin "+ this.origin+ ", destination "+ this.destination + ", date "+ this.time + ", price "+ this.price + ", occupancy "+ this.occupancy+ "/"+ this.capacity;
    }
    
}
