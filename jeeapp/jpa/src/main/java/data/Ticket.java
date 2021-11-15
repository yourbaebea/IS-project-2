package data;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class Ticket implements Serializable {
    //@Id @GeneratedValue(strategy = GenerationType.AUTO)
	//private Long id;

	private Long buyer_id;

    @ManyToOne()
	@JoinColumn(name="trip_id", referencedColumnName = "id")
    private Trip trip;

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Ticket(){}

    public Ticket(Long buyer_id,Trip trip){
        this.buyer_id= buyer_id;
        this.trip=trip;
    }

    public Long getBuyer_id() {
        return buyer_id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Trip getTrip() {
        return trip;
    }
}
