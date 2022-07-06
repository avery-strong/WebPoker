package uta.cse3310;

public class Card{
   public enum Suite{
      HEARTS,CLUBS,DIAMONDS,SPADES
   }

   public enum Value{
      ACE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,
      TEN,JACK,QUEEN,KING 
   }
   
   public enum Handenum{
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
    
   public Suite suite;
   public Value value;
    

   public Card(){}
}
