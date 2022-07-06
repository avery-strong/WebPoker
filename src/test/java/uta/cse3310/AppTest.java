
package uta.cse3310;

import uta.cse3310.Card;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */

public class AppTest
{
    /**
     * Rigorous Test :-)
     */
     // test if Game constructor is working
     // very basic but most important aspects

    @Test
    public void shouldAnswerWithTrue(){
        Game game = new Game();
        assertTrue( game != null);
    }

    // test the create_deck method
    // true if correct size
    @Test
    public void shuffleTest(){
        Game game = new Game();

        assertTrue( game.deck_size() == 52 );
    }

    // test the sortHand method in Hand.java
    @Test
    public void sortHandTest(){
        Card c = new Card();
        Card cards[] = new Card[5];

        c.value = Card.Value.NINE;
        c.suite = Card.Suite.SPADES;
        cards[0] = c;
        c.value = Card.Value.SIX;
        c.suite = Card.Suite.SPADES;
        cards[1] = c;
        c.value = Card.Value.SEVEN;
        c.suite = Card.Suite.SPADES;
        cards[2] = c;
        c.value = Card.Value.EIGHT;
        c.suite = Card.Suite.SPADES;
        cards[3] = c;
        c.value = Card.Value.FIVE;
        c.suite = Card.Suite.SPADES;
        cards[4] = c;


        Hand hand = new Hand(cards);
        
        Card sortedCards[] = {cards[4], cards[1], cards[2], cards[3], cards[0]};
        hand.sort_by_value();

        assertTrue(cards[0] == sortedCards[0] 
            && cards[1] == sortedCards[1] 
            && cards[2] == sortedCards[2] 
            && cards[3] == sortedCards[3] 
            &&cards[4] == sortedCards[4]);
    }

    // test empty_hand method in Game.java
    @Test
    public void emptyHandTest(){
        Game game = new Game();
        Player player = new Player(0);
        game.players_add(player);
        game.players_empty_hand(player);
        assertTrue(player.get_player_hand().cards[0] == null);
    }

    // test players_set_notReady method in Game.java
    @Test
    public void setPlayersNotReadyTest(){
        Game game = new Game();
        Player p0 = new Player(0);
        p0.set_ready(false);             
        Player p1 = new Player(1);
        p1.set_ready(false);             
        Player p2 = new Player(2);
        p2.set_ready(false);             
        Player p3 = new Player(3);
        p3.set_ready(false);             
        game.players_add(p0);
        game.players_add(p1);
        game.players_add(p2);
        game.players_add(p3);
        game.players_set_notReady();

        assertTrue(!p0.get_ready() && !p1.get_ready() && !p2.get_ready() && !p3.get_ready());
    }

    // test players_num_ready method in Game.java
    @Test
    public void numPlayersReadyTest(){
        Game game = new Game();
        Player p0 = new Player(0);
        p0.set_ready(true);             // = true;
        Player p1 = new Player(1);
        p1.set_ready(true);             // = true;
        Player p2 = new Player(2);
        Player p3 = new Player(3);
        game.players_add(p0);
        game.players_add(p1);
        game.players_add(p2);
        game.players_add(p3);

        assertTrue(game.players_num_ready() == 2);
    }

    // test players_all_ready method in Game.java
    @Test
    public void allPlayersReadyTest(){
        Game game = new Game();
        Player p0 = new Player(0);
        p0.set_ready(true);             // = true;
        Player p1 = new Player(1);
        p1.set_ready(true);             // = true;
        Player p2 = new Player(2);
        p2.set_ready(true);             // = true;
        Player p3 = new Player(3);
        p3.set_ready(true);             // = true;
        game.players_add(p0);
        game.players_add(p1);
        game.players_add(p2);
        game.players_add(p3);

        assertTrue( game.players_all_ready() );
    }

    // test players_all_ready method in Game.java
    @Test
    public void allPlayersReadyTest2(){
        Game game = new Game();
        Player p0 = new Player(0);
        p0.set_ready(true);             // = true;
        Player p1 = new Player(1);
        p1.set_ready(true);             // = true;
        Player p2 = new Player(2);
        p2.set_ready(true);             // = true;
        Player p3 = new Player(3);
        game.players_add(p0);
        game.players_add(p1);
        game.players_add(p2);
        game.players_add(p3);

        assertTrue( !game.players_all_ready() );
    }
/*
    // test bet_all_equal method in Game.java
    @Test
    public void allBetEqualTest(){
        Game game = new Game();
        Player p0 = new Player(0);
        p0.set_current_bet(10);
        Player p1 = new Player(1);
        p1.set_current_bet(10);
        Player p2 = new Player(2);
        p2.set_current_bet(10);
        Player p3 = new Player(3);
        p3.set_current_bet(10);
        game.players_add(p0);
        game.nonFolded_add(p0);
        game.players_add(p1);
        game.nonFolded_add(p1);
        game.players_add(p2);
        game.nonFolded_add(p2);
        game.players_add(p3);
        game.nonFolded_add(p3);

        assertTrue(game.bet_all_equal());
    }
*/
    // test player_fold_current() method in Game.java
    @Test
    public void foldCurrentPlayerTest(){
        Game game = new Game();

        Player p0 = new Player(0);
        Player p1 = new Player(1);

        game.players_add(p0);
        game.players_add(p1);
        game.nonFolded_add(p0);
        game.nonFolded_add(p1);

        game.players_set_current(p0);

        assertTrue(game.players_size() == 2 && game.nonFolded_size() == 2);
    }
}
