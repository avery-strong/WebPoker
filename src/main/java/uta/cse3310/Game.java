package uta.cse3310;

import java.util.ArrayList;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uta.cse3310.UserEvent.UserEventType;

public class Game{
    public Game(){
        System.out.println("creating a Game Object");
        create_deck();
        shuffle_deck();
        pot = new Pot();
        playerStandFold = 0;
        phase = 0;
    }

    /**************************************
                
                    Players

    **************************************/

    public void player_add(Player p)            { players.add(p); }
    public void player_next(){ // swap players
        if(currentplayer == nonFoldedPlayers.get(0) || nonFoldedPlayers.indexOf(currentplayer) < nonFoldedPlayers.size()-1){
            currentplayer = nonFoldedPlayers.get(nonFoldedPlayers.indexOf(currentplayer) + 1); // next player
        }
        // go back to starting player / next round
        else currentplayer = startingplayer;
    }
    public void player_fold(Player p){
        p.set_fold(); 
        empty_hand(p); 
    }

    // determine tthe winner between two players
    // print to console the winner.
    // only checks between first two players
    // will need to expand for more players
    public void determine_winner(){
        // SETTING TO DEFAULT PLAYER IN ARRAY BC whoWinds doesnt work
        // so other code can be added

        for(Player p : nonFoldedPlayers) p.pHand = new Hand(p.Cards);

        int i = nonFoldedPlayers.size()-1;
        boolean tie = false;
        int failingCounter = 0;

        while(nonFoldedPlayers.size() > 1 && i >= 1){
            if(nonFoldedPlayers.get(i).pHand.is_equal(nonFoldedPlayers.get(i-1).pHand)){
                if(nonFoldedPlayers.size() == 2){
                    tie = true;
                    break;
                }
            }
            else if(nonFoldedPlayers.get(i).pHand.is_better_than(nonFoldedPlayers.get(i-1).pHand)) nonFoldedPlayers.remove(i-1);
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
        // add cards back to deck (call create_deck())
        // shuffle (call shuffle_deck())
        // give players new cards starting essentially a new game
    }

    /***************************************
     
                player_queue

    ****************************************/

    public void player_queue_add(Player p)  { player_queue.add(p); }
    public void player_queue_remove(int id) { player_queue.remove(id); }
    public Player player_queue_get()        { return player_queue.get(0); }
    public int player_queue_get_size()      { return player_queue.size(); }

    /**************************************
     
                Game Logic
     
    **************************************/

    public String exportStateAsJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**************************************
    
                    Events

    **************************************/

    public void event_name(UserEvent event){        // Phase 00 logic
        // If there's currently more than 5 players add player 5 into a queue
        if(event.playerID >= 5){   
            for(int i = 0; i < player_queue.size(); i++)
                if(player_queue.get(i).get_id() == event.playerID) player_queue.get(i).set_name(event.name);
            
        }
        else players.get(event.playerID).set_name(event.name);

        determine_player_message();
    }
    public void event_ready(UserEvent event){       // Phase 00 logic
        players.get(event.playerID).set_ready();                        // Set the status of the player ot ready

        if(players.size() >= 2  && all_players_ready()){                // Check if all players are ready
            for(Player p : players){
                for(int j = 0; j < 5; j++) p.add_card(draw_card());     // add cards into the players hands
                    
                bet_place_ante(p.id);
                p.set_cards();                                          // add cards to the cards array for javascript functionality
                nonFoldedPlayers.add(p);
            }
            currentplayer = nonFoldedPlayers.get(0);
            phase = 1;
            turn = 0;
            timeRemaining = 30;
        }
        else if(players.size() >= 2 && num_players_ready() >= 2) timeRemaining = 10;
        
        determine_player_message();
    }
    public void event_check(UserEvent event){       // (Player check) logic
        players.get(event.playerID).set_stand();
        players.get(event.playerID).set_bet();
        event_move();
    }
    public void event_bet_01(UserEvent event){      // Phase 01 (First Bet Phase) logic
        bet_place(event.playerID, event);

        if(turn != players.size()-1){
            turn++;                                 // Changes turn for the next player                        
            timeRemaining = 30;                     // Resets the timer to 30 seconds
        }
        else{
            phase++;                                // Sets to draw phase
            turn = 0;                               // Resets the turn so first player is now drawing
        }

        determine_player_message();
    }
    // Not an actual event/action performed by the user but is accessed by check
    // Could maybe be added to event_check rather than be its own method
    public void event_move(){                       // Phase 01(changes player after checking)
        if(all_bets_equal() && nonFoldedPlayers.size() >= 1){
            player_next();
            turn++;

             // every player made a single turn
            if(all_players_bet()){
                set_all_bet();
                turn = 0;
                currentplayer = nonFoldedPlayers.get(0);
                phase++;
            }

            timeRemaining = 30;
        }
        else{
            currentplayer = player_next_bet_player();
            turn = player_next_bet();
            timeRemaining = 30;
        }
    }
    public void event_draw(UserEvent event){        // Phase 02 logic
        new_cards(event.playerID, event);

        player_next();
        turn++;
        // every player made a single turn
        if(currentplayer == startingplayer){
            turn = 0;
            currentplayer = nonFoldedPlayers.get(0);
            phase++;
        }
        
        timeRemaining = 30;
        
        determine_player_message();
    }
    public void event_bet_03(UserEvent event){      // Phase 03 (Second Bet Phase) logic
        if(all_bets_equal() && nonFoldedPlayers.size() >= 1){
            player_next();
            turn++;
            // every player made a single turn
            if(all_players_bet()){
                set_all_bet();
                currentplayer = nonFoldedPlayers.get(0);
                turn = 0;
                phase = 5;
                determine_winner();
                turn = -1;
                set_players_notReady();
                timeRemaining = -1;
            }
            if(phase != 5) timeRemaining = 30;
        }
    
        currentplayer = player_next_bet_player();
        turn = player_next_bet();
        timeRemaining = 30;

        determine_player_message();
    }
    // Not an actual event/action performed by the user 
    public void event_reset(UserEvent event){              // Phase 04 (Reset) logic
        timeRemaining = -1;
        nonFoldedPlayers.clear();

        for(int i = 0; i < players.size(); i++){
            players.get(i).folded = false;
            empty_hand(players.get(i));
        }

        deck.clear();
        create_deck();
        shuffle_deck();

        phase = 0;

        determine_player_message();
    }

    public void processMessage(int playerId, String msg){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // take the string we just received, and turn it into a user event
        UserEvent event = gson.fromJson(msg, UserEvent.class);

        System.out.println("\n\n" + msg + "\n\n");

        // if player is in queue and NAME event happens
        if(event.event == UserEventType.NAME){
            // note this does not add the player to game.players
            // only sets their name
            for(Player p : player_queue)
                if(p.get_id() == event.playerID) p.set_name(event.name);
        }

        if(event.event == UserEventType.NAME && phase == 0)         event_name(event);
        if(event.event == UserEventType.READY)                      event_ready(event);   
        if(event.event == UserEventType.CHECK && all_bets_equal())  event_check(event);
        if(event.event == UserEventType.BET){
            if(phase == 1)      event_bet_01(event);
            else if(phase == 3) event_bet_03(event);
        }
        // any player can sort at any time
        if(event.event == UserEventType.SORT)   sort_cards(event.playerID, event);
        if(phase == 4)                          event_reset(event);            // Phase 04 logic (idk)
    }

    public void determine_player_message(){
        highestBet = max_player_bet();
        if(phase == 0) {
            playerMessage = "Phase: PreGame"
                          + "\n"
                          + "Waiting for players to ready up...";
        }
        else if(phase == 1){
            playerMessage = "Phase: First Bet Phase"
            + "\n"
            + "Turn: " + currentplayer.get_name();
        }

        else if(phase == 2){
            playerMessage = "Phase: Draw Phase"
            + "\n"
            + "Turn: " + currentplayer.get_name();
        }

        else if(phase == 3){
            playerMessage = "Phase: Second Bet Phase"
            + "\n"
            + "Turn: " + currentplayer.get_name();
        }

        else if(phase == 4){
            playerMessage = "Phase: Showdown"
            + "\n"
            + "Turn: " + currentplayer.get_name();
        }

        else if(phase == 5){
            if(winner != -1){
              playerMessage = "Winner: Player "
              + winningPlayer.get_id() + " (" + winningPlayer.get_name() + ")"
              + " won " + winStr + " chips";
            }
            // tie situation
            else playerMessage = "Tie! Both Players" + " won " + winnings/2 + " chips"; 
        }
    }

    /*
        this method is called on a periodic basis (once a second) by a timer
        it is to allow time based situations to be handled in the game
        if the game state is changed, it returns a true.
     
        expecting that returning a true will trigger a send of the game
        state to everyone
    */
    public boolean update(){
        while((phase == 0 || phase == 5) && players.size() < 5 && player_queue.size() > 0){
            player_queue_remove(0);
            player_add(player_queue.get(0));
            if(players.size() == 5 || player_queue.size() == 0) return true;
        }
        if(players.size() == 0){
            // no players in game
            phase = 0;
            pot.empty_pot();
            timeRemaining = -1;
            nonFoldedPlayers.clear();
            player_queue.clear();
            deck.clear();
            create_deck();
            shuffle_deck();
            winningPlayer = null;
            currentplayer = null;
            startingplayer = null;
            return true;
        }

        if(players.size() == 0 || timeRemaining == -1) return false;
        if(players.size() >= 2 && timeRemaining > 0) timeRemaining--;
        if(timeRemaining == 0 && phase != 0) fold_current_player();
        else if(timeRemaining == 0 && num_players_ready() >= 2 && phase == 0){
            kick_not_ready();
            phase = 0;
            timeRemaining = -1;
            if(players.size() >= 2  && all_players_ready()){
                for(Player p : players){
                    for(int j = 0; j < 5; j++) p.add_card(draw_card());
                    bet_place_ante(p.id);
                    p.set_cards();
                    nonFoldedPlayers.add(p);
                }
                currentplayer = nonFoldedPlayers.get(0);
                phase = 1;
                turn = 0;
                timeRemaining = 30;
            }
        }

        if(nonFoldedPlayers.size() == 1 && phase != 0 && phase != 5){
            // if all other players fold
            // last one standing wins
            winner = nonFoldedPlayers.get(0).get_id();
            winningPlayer = nonFoldedPlayers.get(0);
            determine_winner();
            phase = 5;
            nonFoldedPlayers.clear();
            turn = -1;
            set_players_notReady();
            timeRemaining = -1;
            winningPlayer.pHand = new Hand(winningPlayer.Cards);
            winningPlayer.pHand.get_handName();
        }
        determine_player_message();
        return true;
    }

    public boolean stand_fold_check(){
        int count = 0;

        for(int i = 0; i < players.size(); i++) if(players.get(i).get_stand()) count++;           // if stand is true increment count

        if(count == players.size()) return true;

        return false;
    }

    public boolean all_bets_equal(){
        for(Player p : nonFoldedPlayers)
            if(p.currentBet != max_player_bet() && p.wallet != 0) return false;
            
        return true;
    }
    public int player_next_bet(){
        Player temp = nonFoldedPlayers.get(0);
        for(int i = 1; i < nonFoldedPlayers.size(); i++){
            if(temp.get_bet() < nonFoldedPlayers.get(i).get_bet())      return temp.get_id();
            else if(temp.get_bet() > nonFoldedPlayers.get(i).get_bet()) return nonFoldedPlayers.get(i).get_id();
            
            temp = nonFoldedPlayers.get(i);
        }

        return 0;
    }

    public Player player_next_bet_player(){
        if(all_bets_equal()){
            for(Player p : nonFoldedPlayers) p.set_check();
            
            player_next();
        }
        else{
            for(Player p : nonFoldedPlayers) p.set_check();
            
            player_next();
            while(currentplayer.get_wallet() == 0 && !all_bets_equal()) player_next();
            
            if(currentplayer.get_current_bet() < max_player_bet()) return currentplayer;
        }

        return currentplayer;
    }

    public boolean all_players_bet(){
        for(Player p : nonFoldedPlayers)
            if(p.get_bet() == false) return false;
            
        return true;
    }

    public void set_all_bet(){
        for(Player p : players){
            p.reset_bet();
        }
    }

    public int max_player_bet(){
        int temp = 0;
        for(Player p : nonFoldedPlayers)
            if(p.get_bet() > temp) temp = p.get_bet();
            
        return temp;
    }

    public boolean all_players_ready(){
        for(int i = 0; i < players.size(); i++)
            if(players.get(i).get_ready() != true) return false;
            
        return true;
    }

    public int num_players_ready(){
        int count = 0;
        for(int i = 0; i < players.size(); i++)
            if(players.get(i).get_ready() == true) count++;
        
        return count;
    }

    public void set_players_notReady(){
        for(int i = 0; i < players.size(); i++){
            players.get(i).set_raised();
            players.get(i).set_fold();
            players.get(i).set_current_bet(0);
        }
    }

    public void fold_current_player(){
        currentplayer.set_fold();
        nonFoldedPlayers.remove(currentplayer);
        startingplayer = nonFoldedPlayers.get(0);
        turn = player_next_bet();
        currentplayer = player_next_bet_player();
        timeRemaining = 30;
    }

    public void kick_not_ready(){
        ArrayList<Player> removeList = new ArrayList<>();
        synchronized(WebPoker.mutex){
            for(int i = 0; i < players.size(); i++)
                if(players.get(i).ready == false) removeList.add(players.get(i));

            for(int i = 0; i < removeList.size(); i++) players.remove(removeList.get(i));
        }
    }

    public void rearrange_ids(){
        for(int i = 0; i < players.size(); i++) players.get(i).set_id(i);
    }

    public int get_next_id(){
        // flag to signal id is currently used
        boolean flag = false;
        int id = 0;
        if(players.size() == 0) return 0;
        
        // temporarly store players in both game and queue
        ArrayList<Player> allPlayers = new ArrayList<>();
        for(Player p : players)         allPlayers.add(p);
        for(Player p : player_queue)    allPlayers.add(p);

        for(int i = 0; i < allPlayers.size(); i++){
            for(Player p : allPlayers){
                // id is found in players/cant use so move on
                if(p.get_id() != i) flag = false;
                else{
                    flag = true;
                    break;
                }
            }
            if(flag == false) return i;
            if(i == players.size() && player_queue.size() == 0) return i;
    
            id = i;
        }
        // if all used add one to biggests id number
        return (id+1);
    }

    public Player get_player(int id){
        for(int i = 0; i < players.size(); i++)
            if(players.get(i).get_id() == id) return players.get(i);
        
        return null;
    }

    public Player get_player_in_queue(int id){
        for(Player p : player_queue)
            if(p.get_id() == id) return p;
            
        return null;
    }


    /**************************************
    
                Deck Builders
     
    **************************************/

    public void sort_cards(int id, UserEvent event){
        Hand.sortHand(players.get(id).Cards);     // This actually sorts the cards
        // This sorts the ArrayList hand so if Draw is clicked the correct order is sent back to the client
        for(int i = 0; i < 5; i++) players.get(id).hand.set(i, players.get(id).Cards[i]);
    }

    public void new_cards(int id, UserEvent event){
        int indexes[] = event.give_card_indexes;

        for (int i = 0; i < indexes.length; i++) // 0 is default stating the card shouldnt change
            if (indexes[i] > 0) players.get(id).hand.set(indexes[i] - 1, draw_card()); // Remove the card at the specified index
            
        players.get(id).set_cards();
    }

    // gets a card from the front of passed in deck
    public Card draw_card() {
        Card card = deck.get(0);
        deck.remove(0);
        return card;
    }

    // remove all player's Cards
    // and randomize the order in the shuffle_deck
    // will be 52 cards in size

    public void empty_hand(Player p){
        for(int i = 0; i < p.hand.size(); i++) deck.add(p.hand.get(i));
        
        p.hand.clear();
        p.Cards = new uta.cse3310.Card[5];
    }

    public void shuffle_deck(){
        // shuffle current deck
        try{
            Collections.shuffle(deck);
            // Error handling for an incorrect deck
            if(deck.size() != 52) throw new Exception("Error in deck shuffle, not 52 cards in deck.");
        } catch(Exception e){ System.out.println(e); }
    }

    // this adds all 52 cards to the deck in order
    // will need to shuffle in game
    public void create_deck(){
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

    /**********************************
     
                Betting
     
    ***********************************/

    public void bet_place_ante(int id){
        players.get(id).subtract_wallet(20);
        pot.add_to_pot(20);
    }

    public void bet_place(int id, UserEvent event){
        players.get(id).set_bet();
        call_bet(id, event);

        //Check if player is betting more than they have, change bet to whatever is left in their wallet.
        if(event.amount_to_bet > players.get(id).get_wallet()) event.amount_to_bet = players.get(id).get_wallet();
        
        players.get(id).subtract_wallet(event.amount_to_bet);
        players.get(id).set_current_bet(event.amount_to_bet);
        pot.add_to_pot(event.amount_to_bet);
    }

    public void call_bet(int id, UserEvent event){
        int betDifference = max_player_bet() - players.get(id).currentBet;

        if(betDifference >= 0 && (players.get(id).wallet - betDifference) >= 0) event.amount_to_bet = betDifference;
        else event.amount_to_bet = players.get(id).wallet;
        
        players.get(id).subtract_wallet(event.amount_to_bet);
        players.get(id).set_current_bet(event.amount_to_bet);
        pot.add_to_pot(event.amount_to_bet);
    }

    /****************************************************

                Just gonna leave this here.....

    ****************************************************/

    public boolean is_id_in_players(int id){
        for(Player p : players)
            if(p.get_id() == id) return true;

        return false;
    }

    /**********************************

                Attributes

    **********************************/

    public ArrayList<Player> players = new ArrayList<>();               // players of the game
    public ArrayList<Player> nonFoldedPlayers = new ArrayList<>();      // Players who have not folded
    public ArrayList<Player> player_queue = new ArrayList<>();          // Players who have not entere the game due to player cap

    public ArrayList<Card> deck = new ArrayList<>();                    // stored cards not in players hands
    private ArrayList<Hand> hands = new ArrayList<>();

    // store non folded players seperately


    // turns
    Player startingplayer;
    Player currentplayer;
    Player winningPlayer;

    private String playerMessage;


    // round - these are used with javascript to determine certain displays
    public int phase;
    int turn = -1;
    public int highestBet;
    // do not change these or display will break
    int winner = -1;
    int winnings = -1;
    String winStr = "";
    // 0 will be pregame
    // 1 will be first bet phase
    // 2 wil be draw phase
    // 3 will be second bet phase
    // 4 will be showdown
    // 5 is winner screen

    public int timeRemaining = -1;

    // Count for number of players who have stand/fold
    // This is necessary to determine when showdown begins
    private int playerStandFold;
    private Pot pot; // total of chips being bet
}
