package uta.cse3310;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uta.cse3310.Card;
import uta.cse3310.Card.Value;
import uta.cse3310.Card.Suite;

enum Handenum{
    HIGH(0),
    ONE(1),
    TWO(2),
    THREE(3),
    STRAIGHT(4),
    FLUSH(5),
    HOUSE(6),
    FOUR(7), 
    STRAIGHTFLUSH(8),
    ROYAL(9);
      
    Handenum(int s){ this.strength = s; }
      
    public int strength;
}

public class Hand{
    // private transient int i=10;
    // marked transient they will not serialized / deserialized

    public Hand(){ 
        this.cards = new Card[5];
        this.hand = ""; 
    }
    public Hand(Card c[]){ 
        this.cards = c; 
        this.hand = "";
    }

    /*********************************
              Sorting Methods
    *********************************/

    public void sort_by_value(){      // Bubble sort :D
        for(int i = 0; i < cards.length-1; i++){
            for(int h = 0; h < cards.length; h++) System.out.print(cards[h].value.ordinal() + " ");
            System.out.println("\n");
            for (int j = 0; j < cards.length-i-1; j++){
                if(cards[j+1].value.ordinal() < cards[j].value.ordinal()){
                    Card temp = cards[j];
                    cards[j] = cards[j+1];
                    cards[j+1] = temp;
                }
            }
        }

        for(int i = 0; i < cards.length; i++) System.out.println(cards[i].value);
/*
        // Resets the position of each card in the event the there is a straight with 10, jack, queen, king, ace
        if(cards[0].value.ordinal() == 0 && cards[1].value.ordinal() == 9 && cards[4].value.ordinal() == 12){
            Card temp = cards[0];
            cards[0] = cards[1];
            cards[1] = cards[2];
            cards[2] = cards[3];
            cards[3] = cards[4];
            cards[4] = temp;
        }
*/
    }   
    public void sort_by_suit(){
        for(int i = 0; i < cards.length; i++){
            for(int j = i+1; j < cards.length; j++){
                if(cards[j].suite.ordinal() < cards[i].suite.ordinal()){
                    Card temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp; 

                }
            }
        }
    }

    /*********************************
              Hand Methods
    *********************************/

    public static boolean is_flush(Card[] c){             // Method for finding a flush
        if(cards.length != 5) return(false);   

        h.sort_by_suit();     

        return(cards[0].suite == cards[4].suite);     
    }
    public static boolean is_royal_flush(Card[] c){       // Method for finding a royal flush
        if(is_flush(h) && is_straight(h) && cards[4].value.ordinal() == 0) return true;

        return false;
    }
    public static boolean is_straight_flush(Card[] c){
        if(is_flush(h) && is_straight(h)) return true;

        return false;
    }
    
    public static boolean is_straight(Card[] c){          // Method for finding a straight
        if(cards.length != 5) return(false);    

        if(cards[4].value.ordinal() == 0) return true;    // Straight 10, jack, queen, king, ace

        else{
            int test_value = cards[0].value.ordinal() + 1;

            for (int i = 1; i < 5; i++ ){
                if(cards[i].value.ordinal() != test_value) return(false);        // Straight fails if values are not eqaul

                test_value++;
            }

            return true;        
        }
    }
    public static boolean three_of_kind(Card[] c){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[2].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[2].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public static boolean four_of_kind(Card[] c){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public static boolean is_full_house(Card[] c){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[1].value.ordinal() && cards[1].value.ordinal() != cards[2].value.ordinal()) return true;
        if(cards[3].value.ordinal() == cards[4].value.ordinal() && cards[3].value.ordinal() != cards[2].value.ordinal()) return true;

        return false;
    }
    public static boolean is_two_pairs(Card[] c){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[1].value.ordinal() && cards[2].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[0].value.ordinal() == cards[1].value.ordinal() && cards[3].value.ordinal() == cards[4].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[2].value.ordinal() && cards[3].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public static boolean is_one_pair(Card[] c){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[1].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[2].value.ordinal()) return true;
        if(cards[2].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[3].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public String determineHand(){
        if(is_flush(cards)){
            if(is_straight_flush(cards)){
                if(is_royal_flush(cards)) hand = Handenum.ROYAL.ordinal();       // Royal Flush
                else hand = Handenum.STRAIGHTFLUSH.toString(); // Straight Flush
            }
            else hand = Handenum.FLUSH.toString();                                 // Flush
        }             

        if(is_straight(cards)) return Handenum.STRAIGHT.ordinal();               // Straight
        if(three_of_kind(cards)){
            if(four_of_kind(cards)) return Handenum.FOUR.ordinal();              // Four of a kind
            if(is_full_house(cards)) return Handenum.HOUSE.ordinal();            // Full House
            else hand = Handenum.THREE.toString();                                 // Three of a kind
        }        

        if(is_one_pair(cards)){
            if(is_two_pairs(cards)) hand = Handenum.TWO.ordinal();               // Two Pairs 
            else hand = Handenum.ONE.toString();                                   // One Pair
        }    
    }

    /****************************************
              Better Than Methods
    *****************************************/

    public boolean is_better_than(Hand h){ 
        if(Handenum.hand.ordinal() > Handenum.h.hand.ordinal()) return true; 

        return false;
    }

    public boolean is_equal(Hand H){ return false; }

    /***************************************
                    Attributes
    ***************************************/

    public Card[] cards;

    public int strength;

    public String hand;
}