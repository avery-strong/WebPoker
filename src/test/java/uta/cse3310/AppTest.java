
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
        Card card1 = new Card();
        Card card2 = new Card();
        Card card3 = new Card();
        Card card4 = new Card();
        Card card5 = new Card();
        card1.value = Card.Value.FIVE;
        card1.suite = Card.Suite.SPADES;
        card2.value = Card.Value.SIX;
        card2.suite = Card.Suite.SPADES;
        card3.value = Card.Value.SEVEN;
        card3.suite = Card.Suite.SPADES;
        card4.value = Card.Value.EIGHT;
        card4.suite = Card.Suite.SPADES;
        card5.value = Card.Value.NINE;
        card5.suite = Card.Suite.SPADES;
        Card cards[] = {card3,card4,card2,card5,card1};
        Card sortedCards[] = {card1,card2,card3,card4,card5};
        Hand.sortHand(cards);
        assertTrue(cards[0] == sortedCards[0] && cards[1] == sortedCards[1] &&
            cards[2] == sortedCards[2] && cards[3] == sortedCards[3] &&
            cards[4] == sortedCards[4]);
    }

    // test empty_hand method in Game.java
    @Test
    public void emptyHandTest(){
        Game game = new Game();
        Player player = new Player(0);
        game.players_add(player);
        game.empty_hand(player);
        assertTrue(player.hand.size() == 0);
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
