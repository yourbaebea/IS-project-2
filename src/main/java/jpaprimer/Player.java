package jpaprimer;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private float height;
    private Date date_of_birth;
    private int position;

    public Player() {}

    public Player(String name, Date date_of_birth, float height, int position) {
        this.name = name;
        this.date_of_birth= date_of_birth;
        this.height= height;
        this.position=position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "Player "+ this.name + "\nDOB: " + this.date_of_birth + "\nHeight: "+ this.height + "\nPosition:" + this.position;
    }

}
