package uta.cse3310;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uta.cse3310.Card;
import uta.cse3310.Card.Value;
import uta.cse3310.Card.Suite;

enum Handenum{
    HIGH,
    ONE,
    TWO,
    THREE,
    STRAIGHT,
    FLUSH,
    HOUSE,
    FOUR, 
    STRAIGHTFLUSH,
    ROYAL;
}

public class Hand{
    // private transient int i=10;
    // marked transient they will not serialized / deserialized

    public Hand()        { this.cards = new Card[5]; }
    public Hand(Card c[]){ this.cards = c; }

    /*********************************
              Sorting Methods
    *********************************/

    public void sort_by_value(){      // 
        for(int i = 0; i < cards.length; i++){
            for(int j = i+1; j < cards.length; j++){
                if(cards[j].value.ordinal() < cards[i].value.ordinal()){
                    Card temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp;
                }
            }
        }

        // Resets the position of each card in the event the there is a straight with 10, jack, queen, king, ace
        if(cards[0].value.ordinal() == 0 && cards[1].value.ordinal() == 9 && cards[4].value.ordinal() == 12){
            Card temp = cards[0];
            cards[0] = cards[1];
            cards[1] = cards[2];
            cards[2] = cards[3];
            cards[3] = cards[4];
            cards[4] = temp;
        }
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

    public static boolean is_royal_flush(Hand h){       // Method for finding a royal flush
        if(is_flush(h) && is_straight(h) && h.cards[4].value.ordinal() == 0) return true;

        return false;
    }
    public static boolean is_straight_flush(Hand h){
        if(is_flush(h) && is_straight(h)) return true;

        return false;
    }
    public static boolean is_flush(Hand h){             // Method for finding a flush
        if(h.cards.length != 5) return(false);   

        h.sort_by_suit();     

        return(h.cards[0].suite == h.cards[4].suite);     
    }
    public static boolean is_straight(Hand h){          // Method for finding a straight
        if(h.cards.length != 5) return(false);  

        h.sort_by_value();  

        if(h.cards[4].value.ordinal() == 0) return true;    // Straight 10, jack, queen, king, ace

        else{
            int test_value = h.cards[0].value.ordinal() + 1;

            for (int i = 1; i < 5; i++ ){
                if(h.cards[i].value.ordinal() != test_value) return(false);        // Straight fails if values are not eqaul

                test_value++;
            }

            return true;        
        }
    }
    public static boolean three_of_kind(Hand h){
        if(h.cards.length != 5) return(false);

        h.sort_by_value();         // Sort by value

        if(h.cards[0].value.ordinal() == h.cards[2].value.ordinal()) return true;
        if(h.cards[1].value.ordinal() == h.cards[3].value.ordinal()) return true;
        if(h.cards[2].value.ordinal() == h.cards[4].value.ordinal()) return true;

        return false;
    }
    public static boolean four_of_kind(Hand h){
        if(h.cards.length != 5) return(false);

        h.sort_by_value();         // Sort by value

        if(h.cards[0].value.ordinal() == h.cards[3].value.ordinal()) return true;
        if(h.cards[1].value.ordinal() == h.cards[4].value.ordinal()) return true;

        return false;
    }
    public static boolean is_full_house(Hand h){
        if(h.cards.length != 5) return(false);

        h.sort_by_value();

        if(h.cards[0].value.ordinal() == h.cards[1].value.ordinal() && h.cards[1].value.ordinal() != h.cards[2].value.ordinal()) return true;
        if(h.cards[3].value.ordinal() == h.cards[4].value.ordinal() && h.cards[3].value.ordinal() != h.cards[2].value.ordinal()) return true;

        return false;
    }
    public static boolean is_two_pairs(Hand h){
        if(h.cards.length != 5) return(false);

        h.sort_by_value();

        if(h.cards[0].value.ordinal() == h.cards[1].value.ordinal() && h.cards[2].value.ordinal() == h.cards[3].value.ordinal()) return true;
        if(h.cards[0].value.ordinal() == h.cards[1].value.ordinal() && h.cards[3].value.ordinal() == h.cards[4].value.ordinal()) return true;
        if(h.cards[1].value.ordinal() == h.cards[2].value.ordinal() && h.cards[3].value.ordinal() == h.cards[4].value.ordinal()) return true;

        return false;
    }
    public static boolean is_one_pair(Hand h){
        if(h.cards.length != 5) return(false);

        h.sort_by_value();

        if(h.cards[0].value.ordinal() == h.cards[1].value.ordinal()) return true;
        if(h.cards[1].value.ordinal() == h.cards[2].value.ordinal()) return true;
        if(h.cards[2].value.ordinal() == h.cards[3].value.ordinal()) return true;
        if(h.cards[3].value.ordinal() == h.cards[4].value.ordinal()) return true;

        return false;
    }
    public static int determineHand(Hand h){
        if(is_flush(h)){
            if(is_straight_flush(h)){
                if(is_royal_flush(h)) return Handenum.ROYAL.ordinal();       // Royal Flush

                return Handenum.STRAIGHTFLUSH.ordinal();                     // Straight Flush
            }

            return Handenum.FLUSH.ordinal();                                 // Flush
        }             

        if(is_straight(h)) return Handenum.STRAIGHT.ordinal();               // Straight
        if(three_of_kind(h)){
            if(four_of_kind(h)) return Handenum.FOUR.ordinal();              // Four of a kind
            if(is_full_house(h)) return Handenum.HOUSE.ordinal();            // Full House

            return Handenum.THREE.ordinal();                                 // Three of a kind
        }        

        if(is_one_pair(h)){
            if(is_two_pairs(h)) return Handenum.TWO.ordinal();               // Two Pairs 

            return Handenum.ONE.ordinal();                                   // One Pair
        }    

        return Handenum.FLUSH.ordinal();
    }

    /****************************************
              Better Than Methods
    *****************************************/

    public boolean is_better_than(Hand h){ 
        if(strength > h.strength) return true; 

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