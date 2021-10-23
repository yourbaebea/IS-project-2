package Classes;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class Ticket implements Serializable {
    //@Id @GeneratedValue(strategy = GenerationType.AUTO)
	//private Long id;
    
	private Long buyer_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    public Ticket(){}

    public Ticket(Long buyer_id){
        this.buyer_id= buyer_id;
    }

    public Long getBuyer_id() {
        return buyer_id;
    }

    
    
}
