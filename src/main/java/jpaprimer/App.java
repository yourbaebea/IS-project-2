package jpaprimer;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


import com.github.javafaker.Faker;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Players");
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        // update here

        List<Player> players= Fake(10);

        et.begin();

        for (Player p : players)
            em.persist(p);
        et.commit();

    }


    
    public static List<Player> Fake(int number){
        
        Faker faker = new Faker(new Locale("en-US"));
        String name;
        Date date_of_birth;
        float height;
        int position;

        List<Player> players= new ArrayList<>();
        

        for(int i=0; i<number; i++){
            //String name, Date date_of_birth, float height, int position
            name= faker.name().fullName();
            date_of_birth= faker.date().birthday();
            height= faker.number().numberBetween((long) 1.60,(long) 2.00);
            position= faker.number().numberBetween(1,7);
            players.add(new Player(name, date_of_birth, height, position));

        }

        return players;

        
    }


}
