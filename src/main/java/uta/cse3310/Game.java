package uta.cse3310;

import java.util.ArrayList;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uta.cse3310.UserEvent.UserEventType;

public class Game{
    public Game(){
        System.out.println("creating a Game Object");
        deck_create();
        deck_shuffle();
        pot = new Pot();
        phase = 0;
    }

    /**************************************
     
                Game Logic
     
    **************************************/

    public String exportStateAsJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void determine_winner(){
        // SETTING TO DEFAULT PLAYER IN ARRAY BC whoWinds doesnt work
        // so other code can be added

        for(Player p01 : players){
            // if the player has folded
            if(p01.get_fold()) continue;
            for(Player p02 : players){
                // if the player has folded
                if(p02.get_fold()) continue;
                // if the players are the same
                if(p01.get_name().equals(p02.get_name())) continue;
                // if the players hands are equal
                if(p01.get_player_hand().strength == p02.get_player_hand().strength){
                    tie = true;
                }
                // if the first players hand is stronger than the second
                else if(p01.get_player_hand().strength > p02.get_player_hand().strength){
                    winningPlayer = p01;
                    p02.set_fold(true);
                }
                // if the second players hand is stronger than the second
                else{
                    winningPlayer = p02;
                    p01.set_fold(true);
                }
            }
        }

        winningPlayer.add_wallet(pot.get_pot());
        winStr = String.valueOf(pot.get_pot());
        pot.empty_pot();
    }
/*
        while(nonFoldedPlayers.size() > 1 && i >= 1){
            // if the players hands are equal
            if(nonFoldedPlayers.get(i).get_player_hand().is_equal(nonFoldedPlayers.get(i-1).get_player_hand())){
                if(nonFoldedPlayers.size() == 2){
                    tie = true;
                    break;
                }
            }
            // if player i's hand is stronger than player i-1
            else if(nonFoldedPlayers.get(i).get_player_hand().is_better_than(nonFoldedPlayers.get(i-1).get_player_hand())) nonFoldedPlayers.remove(i-1);
            // if player i-1's hand is stronger than player i
            else nonFoldedPlayers.remove(i);
            
            i--;
            // shouldn't be needed but just in case.
            failingCounter++;
            if(failingCounter >= 25) break;
            
        }
        if(!tie){
            winningPlayer = nonFoldedPlayers.get(0);
            winner = winningPlayer.get_id();
        }
        else winner = -1;

        // update winner wallet
        winnings = pot.reward_pot();
        if(winner != -1){
            Player workingPlayer = get_player(winner);
            workingPlayer.add_wallet(winnings);
            winStr = String.valueOf(winnings);
            pot.empty_pot();
        }
        else{
            nonFoldedPlayers.get(0).add_wallet(pot.reward_pot()/2);
            nonFoldedPlayers.get(1).add_wallet(pot.reward_pot()/2);
            winStr = String.valueOf(winnings/2);
            pot.empty_pot();
        }

        // After we determine the winner we need to
        // Save the winner
        // Save the hand
        // Clear hands
        // Broadcast Winner and the winning hand
        // Update winner wallet
        // add cards back to deck (call deck_create())
        // shuffle (call deck_shuffle())
        // give players new cards starting essentially a new game
    }
*/    
    public void determine_player_message(Player p){
        if(phase == 0){
            playerMessage = "Phase: PreGame"
                          + "\n"
                          + "Waiting for players to ready up...";
        }
        else if(phase == 1){
            playerMessage = "Phase: First Bet Phase"
            + "\n"
            + "Turn: " + players.get(turn).get_name();
        }

        else if(phase == 2){
            playerMessage = "Phase: Draw Phase"
            + "\n"
            + "Turn: " + players.get(turn).get_name();
        }

        else if(phase == 3){
            playerMessage = "Phase: Second Bet Phase"
            + "\n"
            + "Turn: " + players.get(turn).get_name();
        }
        else if(phase == 4){
            // tie situation
            if(tie) playerMessage = "Tie! Both Players" + " won " + winnings/2 + " chips"; 
            else{
              playerMessage = "Winner: Player "
              + winningPlayer.get_id() + " (" + winningPlayer.get_name() + ")"
              + " won " + winStr + " chips";
            }
        }
    }

/*
    CURRENTLY NOT CALLED

        this method is called on a periodic basis (once a second) by a timer
        it is to allow time based situations to be handled in the game
        if the game state is changed, it returns a true.
     
        expecting that returning a true will trigger a send of tghe game
        state to everyone



    public boolean update(){
        if(players.size() >= 2 && players_all_ready()){
            for(Player p : players){
                for(int j = 0; j < 5; j++) p.add_card(players_draw_card());
                bet_place_ante(p.get_id());
                p.set_cards();
                nonFoldedPlayers.add(p);
            }
            currentPlayer = nonFoldedPlayers.get(0);
            phase = 1;
            turn = 0;
            timeRemaining = 30;
        }

        if(players_num_ready() >= 2 && phase == 0){
            kick_not_ready();
            phase = 0;
            timeRemaining = -1;
        }

        if(nonFoldedPlayers.size() == 1 && phase != 0 && phase != 5){
            // if all other players fold
            // last one standing wins
            winner = nonFoldedPlayers.get(0).get_id();
            winningPlayer = nonFoldedPlayers.get(0);
            //determine_winner();
            phase = 5;
            nonFoldedPlayers.clear();
            turn = -1;
            players_set_notReady();
            timeRemaining = -1;
            winningPlayer.set_player_hand(winningPlayer.Cards);
            winningPlayer.get_player_hand().get_handName();
        }
        //determine_player_message();
        return true;
    }
*/
    public void kick_not_ready(){
        ArrayList<Player> removeList = new ArrayList<>();
        synchronized(WebPoker.mutex){
            for(int i = 0; i < players.size(); i++)
                if(players.get(i).get_ready() == false) removeList.add(players.get(i));

            for(int i = 0; i < removeList.size(); i++) players.remove(removeList.get(i));
        }
    }
    public void rearrange_ids(){
        for(int i = 0; i < players.size(); i++) players.get(i).set_id(i);
    }
    public Player get_player(int id){ return this.players.get(id); }
    public Player get_player_in_queue(int id){
        for(Player p : playerQueue)
            if(p.get_id() == id) return p;
            
        return null;
    }
    public void processMessage(String msg){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // take the string we just received, and turn it into a user event
        UserEvent event = gson.fromJson(msg, UserEvent.class);

        System.out.println("\n\nPlayer: " 
            + players.get(event.playerID).get_name()
            + "\n\nEvent: " + event.event);

        Player event_player = players.get(event.playerID);

        switch(event.event){
            case BET:
                event_bet(event);

                turn++;

                if(turn == players.size()){
                    phase++;                                
                    turn = 0;
                    roundBet = 0;
                }
                
                // Set the highest bet
                for(Player p : players)
                    if(p.get_current_bet() > highestBet) highestBet = p.get_current_bet();

                roundBet = highestBet;
                
                break; 
            case CALL:
                event.amount_to_bet = highestBet;
                event_bet(event);

                turn++;

                if(turn == players.size() && bet_all_equal()){
                    phase++;
                    turn = 0;
                }

                break;
            case CHECK:
                event_check(event_player);

                turn++;

                break;
            case DRAW:
                event_draw(event);

                turn++;

                if(players_all_draw()){
                    phase = 3;
                    turn = 0;
                    players.get(0).set_check(false);    // need to reset check (weird here I know)
                }

                break;
            case FOLD:
                event_fold(event);

                turn++;

                int foldedCount = 0;

                for(Player p : players)
                    if(p.get_fold()) foldedCount++;

                if(foldedCount == 4) phase = 4;

                break;
            case NAME:
                if(phase == 0){
                    event_name(event);

                    for(int i = 0; i < 5; i++) players.get(event.playerID).add_card(players_draw_card(), i);
                }    
                
                for(Player p : playerQueue)
                    if(p.get_id() == event.playerID) p.set_name(event.name);
                
                break;
            case READY:
                event_ready(event); 

                if(players_all_ready()) phase = 1;
                
                break;
            case SORT:
                player_sort_cards(players.get(event.playerID));

                break;
            default:

                break;
        }              
        
        if(phase == 4){
            determine_winner();
            event_reset(event);            // Phase 04 logic (idk)   
        }

        players.get(event.playerID).determineHand();

        //players.get(event.playerID).get_player_hand().hand = Hand.determineHand(players.get(event.playerID).get_player_hand());
        //players.get(event.playerID).get_player_hand().hand = Card.Handenum.values()[players.get(event.playerID).get_player_hand().strength].toString();
        
        determine_player_message(players.get(event.playerID));
/*
        if(turn == players.size()-1){
            // Everything below used to be event_move but was only called by event_check
            if(bet_all_equal()){
                turn++;
    
                 // every player made a single turn
                if(bet_all_players()){
                    bet_set_all();
                    turn = 0;
                    currentPlayer = nonFoldedPlayers.get(0);
                    phase++;
                }
    
                timeRemaining = 30;
            }
            else{
                currentPlayer = bet_player_next();
                turn = bet_next_player();
                timeRemaining = 30;
            }
        }
*/
    }

    /**************************************
    
                    Events

    **************************************/

    public void event_bet(UserEvent event){                     // Phase 01 (First Bet Phase) logic
        //Check if player is betting more than they have, change bet to whatever is left in their wallet.
        if(event.amount_to_bet > players.get(event.playerID).get_wallet()) 
            event.amount_to_bet = players.get(event.playerID).get_wallet();
        
        players.get(event.playerID).subtract_wallet(event.amount_to_bet);
        players.get(event.playerID).set_current_bet(event.amount_to_bet);
        pot.add_to_pot(event.amount_to_bet);

        players.get(event.playerID).set_bet(true);
    }
    public void event_check(Player p){ p.set_check(true); }     // (Player check) logic
    public void event_draw(UserEvent event){                    // Phase 02 logic
        deck_new_cards(event);

        players.get(event.playerID).set_draw(true);
    }
    public void event_fold(UserEvent event){
        players.get(event.playerID).set_fold(true);
        nonFoldedPlayers.remove(currentPlayer);
    }
    public void event_name(UserEvent event){                    // Phase 00 logic
        // If there's currently more than 5 players add player 6+ into a queue
        if(event.playerID >= 5){   
            for(int i = 0; i < playerQueue.size(); i++)
                if(playerQueue.get(i).get_id() == event.playerID) playerQueue.get(i).set_name(event.name);
            
        }
        else players.get(event.playerID).set_name(event.name);
    }
    public void event_ready(UserEvent event){ players.get(event.playerID).set_ready(true); }    // Phase 00 
    public void event_reset(UserEvent event){                   // Phase 04 (Reset) logicplayer_
        // Not an actual event/action performed by the user    
        timeRemaining = -1;
        nonFoldedPlayers.clear();

        for(int i = 0; i < players.size(); i++){
            players.get(i).set_fold(false);      // false
            players_empty_hand(players.get(i));
        }

        deck.clear();
        deck_create();
        deck_shuffle();

        phase = 0;
    }

    /**************************************
                
                    Players

    **************************************/

    public boolean players_all_check(){
        for(Player p : players)
            if(!p.get_check()) return false;
            
        return true;
    }
    public boolean players_all_ready(){
        for(Player p : players)
            if(!p.get_ready()) return false;
            
        return true;
    }
    public boolean players_all_draw(){
        for(Player p : players)
            if(!p.get_draw()) return false;

        return true;
    }
    public boolean players_contains(Player p){
        if(this.players.contains(p)) return true; 

        return false;
    }
    public Card    players_draw_card(){
        // gets a card from the front of passed in deck
        Card card = deck.get(0);
        deck.remove(0);
        return card;
    }  
    public int     players_num_ready(){
        int count = 0;
        for(int i = 0; i < players.size(); i++)
            if(players.get(i).get_ready() == true) count++;
        
        return count;
    }
    public int     players_size()       { return this.players.size(); }
    public void    players_add(Player p){ this.players.add(p); }
    public void    players_empty_hand(Player p){
        // remove all player's Cards
        // and randomize the order in the deck_shuffle
        // will be 52 cards in size
        for(int i = 0; i < p.get_player_hand().cards.length; i++) deck.add(p.get_player_hand().cards[i]);
        
        p.get_player_hand().cards = new Card[5];
    }
    public void    players_fold(Player p){
        p.set_fold(false); 
        players_empty_hand(p); 
    }
    public void    players_next(){ // swap players
        // If player equals first player or position of player is less than size of players-1
        if(currentPlayer == nonFoldedPlayers.get(0) || nonFoldedPlayers.indexOf(currentPlayer) < nonFoldedPlayers.size()-1)
            currentPlayer = nonFoldedPlayers.get(nonFoldedPlayers.indexOf(currentPlayer) + 1); // next player
        
        // go back to starting player / next round
        else currentPlayer = nonFoldedPlayers.get(0);
    }
    public void    players_remove(Player p){ this.players.remove(p); }
    public void    players_set_current(Player p){ this.currentPlayer = p; }
    public void    players_set_notReady(){
        for(int i = 0; i < players.size(); i++){
            players.get(i).set_raised(false);
            players.get(i).set_fold(false);
            players.get(i).set_current_bet(0);
        }
    }
    public void    player_sort_cards(Player p){ 
        p.get_player_hand().sort_by_value(); 
        //p.get_player_hand().sort_by_suit();
    }
    
    /***************************************
     
                playerQueue

    ****************************************/

    public int      playerQueue_size()                  { return this.playerQueue.size(); }
    public boolean  playerQueue_contains(Player p){ 
        if(this.playerQueue.contains(p)) return true; 

        return false;
    }
    public Player   playerQueue_get()                   { return this.playerQueue.get(0); }
    public void     playerQueue_remove(Player p)        { this.playerQueue.remove(p); }
    public void     playerQueue_add(Player p)           { this.playerQueue.add(p); }
    

    /***************************************
     
                nonFoldedPlayers

    ****************************************/

    public void     nonFolded_add(Player p){ this.nonFoldedPlayers.add(p); }
    public boolean  nonFolded_contains(Player p){ 
        if(this.nonFoldedPlayers.contains(p)) return true;

        return false;
    }
    public void     nonFolded_remove(Player p){ this.nonFoldedPlayers.remove(p); }
    public int      nonFolded_size(){ return this.nonFoldedPlayers.size(); }

    /**************************************
    
                Deck Builders
     
    **************************************/

    public void deck_create(){
        // this adds all 52 cards to the deck in order
        // will need to shuffle in game

        // for each suite and each value add the card
        for(Card.Suite suite : Card.Suite.values()){
            for(Card.Value value : Card.Value.values()){
                Card card = new Card();
                card.suite = suite;
                card.value = value;
                deck.add(card);
            }
        }
    }
    public void deck_new_cards(UserEvent event){
        int indexes[] = event.give_card_indexes;

        for(int i = 0; i < indexes.length; i++){
            if(indexes[i] > 0){                                                 // 0 is default stating the card shouldnt change
                players.get(event.playerID).get_player_hand().cards[indexes[i]-1] = players_draw_card();    // Remove the card at the specified index
            }                      
        }
    }
    public void deck_shuffle(){
        // shuffle current deck
        try{
            Collections.shuffle(deck);
            if (deck.size() != 52) throw new Exception("Error in deck shuffle, not 52 cards in deck.");     // Error handling for an incorrect deck
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public int  deck_size(){ return this.deck.size(); }

    /**********************************
     
                Betting
     
    ***********************************/

    public boolean  bet_all_equal(){
        for(Player p : players)
            if(p.get_current_bet() != highestBet) return false;
        
        return true;
    }
    public boolean  bet_all_players(){
        for(Player p : nonFoldedPlayers)
            if(p.get_bet() == false) return false;
            
        return true;
    }
    public int      bet_next_player(){
        Player temp = nonFoldedPlayers.get(0);
        for(int i = 1; i < nonFoldedPlayers.size(); i++){
            if(temp.get_current_bet() < nonFoldedPlayers.get(i).get_current_bet())      return temp.get_id();
            else if(temp.get_current_bet() > nonFoldedPlayers.get(i).get_current_bet()) return nonFoldedPlayers.get(i).get_id();
            
            temp = nonFoldedPlayers.get(i);
        }

        return 0;
    }
    public int      bet_max_player(){
        int temp = 0;
        for(Player p : nonFoldedPlayers)
            if(p.get_current_bet() > temp) temp = p.get_current_bet();
            
        return temp;
    }
    public Player   bet_player_next(){
        if(bet_all_equal()){
            for(Player p : nonFoldedPlayers) p.set_check(true);
            
            players_next();
        }
        else{
            for(Player p : nonFoldedPlayers) p.set_check(false);
            
            players_next();
            while(currentPlayer.get_wallet() == 0 && !bet_all_equal()) players_next();
            
            if(currentPlayer.get_current_bet() < bet_max_player()) return currentPlayer;
        }

        return currentPlayer;
    }
    public void     bet_call(UserEvent event){
        int betDifference = bet_max_player() - players.get(event.playerID).get_current_bet();
        players.get(event.playerID).set_bet(true);

        if(betDifference >= 0 && (players.get(event.playerID).get_wallet() - betDifference) >= 0) event.amount_to_bet = betDifference;
        else event.amount_to_bet = players.get(event.playerID).get_wallet();
        
        players.get(event.playerID).subtract_wallet(event.amount_to_bet);
        players.get(event.playerID).set_current_bet(event.amount_to_bet);
        pot.add_to_pot(event.amount_to_bet);
    }
    public void     bet_place(UserEvent event){
        
    }
    public void     bet_place_ante(int id){
        players.get(id).subtract_wallet(20);
        pot.add_to_pot(20);
    }
    public void     bet_set_all(){
        for(Player p : players){
            p.reset_bet();
        }
    }

    /**********************************
     
                Phases
     
    ***********************************/

    public int get_phase(){ return this.phase; }

    /****************************************************

                Just gonna leave this here.....

    ****************************************************/

    /**********************************

                Attributes

    **********************************/

    private ArrayList<Player> players = new ArrayList<>();               // players of the game
    private ArrayList<Player> nonFoldedPlayers = new ArrayList<>();      // Players who have not folded
    private ArrayList<Player> playerQueue = new ArrayList<>();          // Players who have not entere the game due to player cap

    private ArrayList<Card> deck = new ArrayList<>();                    // stored cards not in players hands
    private ArrayList<Hand> hands = new ArrayList<>();

    private boolean tie = false;

    // turns
    private Player currentPlayer;
    private Player winningPlayer;

    private String playerMessage;

    // round - these are used with javascript to determine certain displays
    private int phase = 0;
    private int turn = 0;
    private int highestBet = 0;
    private int roundBet = 0;

    // do not change these or display will break
    private int winner = -1;
    private int winnings = -1;
    private int timeRemaining = -1;
    private String winStr = "";
 
    private Pot pot; // total of chips being bet
}
