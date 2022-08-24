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
    public Hand(Card[] c){ 
        this.cards = c;
        this.hand = "";
    }

    /*********************************
              Sorting Methods
    *********************************/

    public void sort_by_value(){      // Bubble sort :D
        for(int i = 0; i < cards.length-1; i++){
            for (int j = 0; j < cards.length-i-1; j++){
                if(cards[j+1].value.ordinal() < cards[j].value.ordinal()){
                    Card temp = cards[j];
                    cards[j] = cards[j+1];
                    cards[j+1] = temp;
                }
            }
        }

        // Resets the position of each card in the event the there is a straight with 10, jack, queen, king, ace
        if(cards[0].value.ordinal() == 0 
        && cards[1].value.ordinal() == 9
        && cards[2].value.ordinal() == 10
        && cards[3].value.ordinal() == 11
        && cards[4].value.ordinal() == 12){

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



    public boolean is_flush(){             // Method for finding a flush
        if(cards.length != 5) return(false);  
        if(cards[0].suite == cards[1].suite && 
           cards[0].suite == cards[2].suite &&
           cards[0].suite == cards[3].suite &&
           cards[0].suite == cards[4].suite){

            return true;
        }   

        return false; 
    }
    public boolean is_royal_flush(){       // Method for finding a royal flush
        if(is_flush() && is_straight() && cards[4].value.ordinal() == 0) return true;

        return false;
    }
    public boolean is_straight_flush(){
        if(is_flush() && is_straight()) return true;

        return false;
    }
    
    public boolean is_straight(){          // Method for finding a straight
        if(cards.length != 5) return(false);    

        if(cards[4].value.ordinal() == 0) return true;    // Straight 10, jack, queen, king, ace

        else{
            int test_value = cards[0].value.ordinal()+1;

            for (int i = 1; i < 5; i++ ){
                if(cards[i].value.ordinal() != test_value) return(false);        // Straight fails if values are not eqaul

                test_value++;
            }

            return true;        
        }
    }
    public boolean three_of_kind(){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[2].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[2].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public boolean four_of_kind(){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public boolean full_house(){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[1].value.ordinal() && cards[1].value.ordinal() != cards[2].value.ordinal()) return true;
        if(cards[3].value.ordinal() == cards[4].value.ordinal() && cards[3].value.ordinal() != cards[2].value.ordinal()) return true;

        return false;
    }
    public boolean two_pairs(){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[1].value.ordinal() && cards[2].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[0].value.ordinal() == cards[1].value.ordinal() && cards[3].value.ordinal() == cards[4].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[2].value.ordinal() && cards[3].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public boolean one_pair(){
        if(cards.length != 5) return(false);

        if(cards[0].value.ordinal() == cards[1].value.ordinal()) return true;
        if(cards[1].value.ordinal() == cards[2].value.ordinal()) return true;
        if(cards[2].value.ordinal() == cards[3].value.ordinal()) return true;
        if(cards[3].value.ordinal() == cards[4].value.ordinal()) return true;

        return false;
    }
    public void determine_hand(){
        if(is_flush()){
            if(is_straight_flush()){
                if(is_royal_flush()){                                   // Royal Flush
                    this.hand     = Handenum.ROYAL.toString();         
                    this.strength = Handenum.ROYAL.ordinal();
                }  
                else{                                                   // Straight Flush
                    this.hand     = Handenum.STRAIGHTFLUSH.toString();
                    this.strength = Handenum.STRAIGHTFLUSH.ordinal();
                }                    
            }
            else{                                                       // Flush
                this.hand     = Handenum.FLUSH.toString();
                this.strength = Handenum.FLUSH.ordinal();
            }                                 
        }             
        else if(is_straight()){                                              // Straight
            this.hand     = Handenum.STRAIGHT.toString();
            this.strength = Handenum.STRAIGHT.ordinal();
        }
        else if(three_of_kind()){
            if(four_of_kind()){                                         // Four of a kind
               this.hand     = Handenum.FOUR.toString(); 
               this.strength = Handenum.FOUR.ordinal();
            } 
            if(full_house()){                                           // Full House
                this.hand     = Handenum.HOUSE.toString();
                this.strength = Handenum.HOUSE.ordinal();
            } 
            else{                                                       // Three of a kind
                this.hand     = Handenum.THREE.toString();
                this.strength = Handenum.THREE.ordinal();
            }               
        }        
        else if(one_pair()){
            if(two_pairs()){                                            // Two Pairs 
               this.hand     = Handenum.TWO.toString();
               this.strength = Handenum.TWO.ordinal(); 
            } 
            else{                                                       // One Pair
                this.hand     = Handenum.ONE.toString();
                this.strength = Handenum.ONE.ordinal();
            }
        } 
        else{
            this.hand     = Handenum.HIGH.toString();
            this.strength = Handenum.HIGH.ordinal(); 
        }  
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