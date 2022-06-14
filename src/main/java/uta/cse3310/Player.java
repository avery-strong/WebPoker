package uta.cse3310;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Player {
    public Player(int id) {
        this.id = id;
        this.name = "not set";
        this.wallet = 100;
    }

    public Player(){
        this.name = "not set";
        this.wallet = 100;
    }

    /*************************************

                    Setters

    *************************************/

    public void set_bet(boolean b){ this.bet = b; }
    public void set_cards(){        // This is redundant but I didnt feel like removing array list logic
        for(int i = 0; i < 5; i++) Cards[i] = hand.get(i);
    }
    public void set_check(boolean b)        { this.check = b; }
    public void set_current_bet(int bet)    { this.currentBet += bet; }
    public void set_fold(boolean b)         { this.fold = b; }
    public void set_id(int id)              { this.id = id; }
    public void set_name(String n)          { this.name = n; }
    public void set_raised(boolean b)       { this.raised = b; }
    public void set_ready(boolean b)        { this.ready = b; }
    public void set_player_hand(Card cards[]){ this.playerHand = new Hand(cards);}
    public void set_stand(boolean b)        { this.stand = b; }
    public void set_wallet(int amount)      { this.wallet = amount; }

    public void add_card(Card card)     { this.hand.add(card); }
    public void add_wallet(int bet)     { this.wallet += bet; }
    public void subtract_wallet(int bet){ this.wallet -= bet; }

    public void reset_bet(){
        this.bet = false;
        this.check = true;
    }

    /*************************************

                Getters

    *************************************/

    public int get_id()             { return this.id; }
    public int get_wallet()         { return this.wallet; }
    public int get_current_bet()    { return this.currentBet; }
    public String get_name()        { return this.name; }
    public Card get_card(int i)     { return this.hand.get(i); }
    public boolean get_bet()        { return this.bet; }
    public boolean get_check()      { return this.check; }
    public boolean get_fold()       { return this.fold; }
    public Hand get_player_hand()   { return this.playerHand; }
    public boolean get_raised()     { return this.raised; }
    public boolean get_ready()      { return this.ready; }
    public boolean get_stand()      { return this.stand; }
    
    /*************************************

                Other methods

    *************************************/

    public String asJSONString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /*************************************

                Attributes

    *************************************/

    private int id;                                         // Identifiication so the game knows who the player is
    private int wallet;                                     // Players starting dollar amount
    private int currentBet;                                 // amount player has bet this round
    private int totalBet;                                   // amount player has bet in the game

    private String name;                                    // Player name

    private Hand playerHand;

    ArrayList<Card> hand = new ArrayList<>();               // Array list for players hand to be passed to cards
    uta.cse3310.Card Cards[] = new uta.cse3310.Card[5];

    private boolean bet = false;
    private boolean check = true;
    private boolean fold = false;                                   // boolean to check if the player has folded in the round
    private boolean raised = false;
    private boolean ready = false;
}
