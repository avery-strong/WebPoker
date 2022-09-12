
package uta.cse3310;

import uta.cse3310.Card;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */

public class AppTest{
    /**
     * Rigorous Test :-)
     */
     // test if Game constructor is working
     // very basic but most important aspects

    @Test
    public void answers_true(){
        game = new Game("Answers true");
        assertTrue( game != null);
    }

    // test the create_deck method
    // true if correct size
    @Test
    public void shuffle_test(){
        game = new Game("Shuffle");

        assertTrue( game.deck_size() == 52 );
    }

    // test the sortHand method in Hand.java
    @Test
    public void sort_value_test(){
        game = new Game("Sort value");
        Card c = new Card();
        Card[] cards = new Card[5];

        Card c_00 = new Card();
        c_00.value = Card.Value.NINE;
        c_00.suite = Card.Suite.SPADES;
        cards[0] = c_00;

        Card c_01 = new Card();
        c_01.value = Card.Value.SIX;
        c_01.suite = Card.Suite.SPADES;
        cards[1] = c_01;

        Card c_02 = new Card();
        c_02.value = Card.Value.SEVEN;
        c_02.suite = Card.Suite.SPADES;
        cards[2] = c_02;

        Card c_03 = new Card();
        c_03.value = Card.Value.EIGHT;
        c_03.suite = Card.Suite.SPADES;
        cards[3] = c_03;

        Card c_04 = new Card();
        c_04.value = Card.Value.FIVE;
        c_04.suite = Card.Suite.SPADES;
        cards[4] = c_04;

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
    public void empty_hand(){
        game = new Game("Empty hand");
        Player player = new Player(0);
        game.players_add(player);
        game.get_players().get(0).reset_player();
        assertTrue(player.get_player_hand().cards[0] == null);
    }

    // test players_set_notReady method in Game.java
    @Test
    public void set_players_not_ready(){
        game = new Game("Players not ready");
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
    public void num_players_ready(){
        game = new Game("Number of players ready");
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
    public void all_players_ready(){
        game = new Game("All players ready");
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
    public void all_players_ready_2(){
        game = new Game("All players ready 2");
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
        game = new Game("Fold current players");

        Player p0 = new Player(0);
        Player p1 = new Player(1);

        game.players_add(p0);
        game.players_add(p1);
        game.nonFolded_add(p0);
        game.nonFolded_add(p1);

        game.players_set_current(p0);

        assertTrue(game.players_size() == 2 && game.nonFolded_size() == 2);
    }
    @Test
    public void royal_flush(){
        game = new Game("Royal flush");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.HEARTS;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.KING;
        c.suite = Card.Suite.HEARTS;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.JACK;
        c.suite = Card.Suite.HEARTS;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.QUEEN;
        c.suite = Card.Suite.HEARTS;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.TEN;
        c.suite = Card.Suite.HEARTS;
        cards[4] = c;

        hand = new Hand(cards);

        hand.sort_by_value();

        assertTrue(hand.is_royal_flush());
    }
    @Test
    public void straight_flush(){
        game = new Game("Straight flush");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.TWO;
        c.suite = Card.Suite.DIAMONDS;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.FIVE;
        c.suite = Card.Suite.DIAMONDS;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.FOUR;
        c.suite = Card.Suite.DIAMONDS;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.THREE;
        c.suite = Card.Suite.DIAMONDS;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.DIAMONDS;
        cards[4] = c;

        hand = new Hand(cards);
        
        hand.sort_by_value();
        
        assertTrue(hand.is_straight_flush());
    }
    @Test
    public void four_kind(){
        game = new Game("Four of a kind");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.CLUBS;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.SIX;
        c.suite = Card.Suite.CLUBS;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.CLUBS;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.CLUBS;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.CLUBS;
        cards[4] = c;

        hand = new Hand(cards);
        
        hand.sort_by_value();
        
        assertTrue(hand.four_of_kind());
    }
    @Test
    public void flush(){
        game = new Game("Flush");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.TWO;
        c.suite = Card.Suite.SPADES;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.FIVE;
        c.suite = Card.Suite.SPADES;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.FOUR;
        c.suite = Card.Suite.SPADES;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.THREE;
        c.suite = Card.Suite.SPADES;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.SPADES;
        cards[4] = c;

        hand = new Hand(cards);
        
        hand.sort_by_value();
        
        assertTrue(hand.is_flush());
    }
    @Test
    public void straight(){
        game = new Game("Straight");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.NINE;
        c.suite = Card.Suite.SPADES;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.SIX;
        c.suite = Card.Suite.HEARTS;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.SEVEN;
        c.suite = Card.Suite.HEARTS;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.EIGHT;
        c.suite = Card.Suite.CLUBS;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.FIVE;
        c.suite = Card.Suite.DIAMONDS;
        cards[4] = c;

        hand = new Hand(cards);
        
        hand.sort_by_value();
        
        assertTrue(hand.is_straight());
    }
    @Test
    public void three_kind(){
        game = new Game("Three of a kind");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.SPADES;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.HEARTS;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.SEVEN;
        c.suite = Card.Suite.HEARTS;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.KING;
        c.suite = Card.Suite.CLUBS;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.DIAMONDS;
        cards[4] = c;

        hand = new Hand(cards);
        
        hand.sort_by_value();

        assertTrue(hand.three_of_kind());
    }
    @Test
    public void two_pairs(){
        game = new Game("Two pairs");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.SPADES;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.FOUR;
        c.suite = Card.Suite.HEARTS;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.SEVEN;
        c.suite = Card.Suite.HEARTS;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.CLUBS;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.FOUR;
        c.suite = Card.Suite.DIAMONDS;
        cards[4] = c;

        hand = new Hand(cards);
        
        hand.sort_by_value();

        assertTrue(hand.two_pairs());
    }
    @Test
    public void one_pair(){
        game = new Game("One Pair");
        cards = new Card[5];

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.SPADES;
        cards[0] = c;

        c = new Card();
        c.value = Card.Value.JACK;
        c.suite = Card.Suite.HEARTS;
        cards[1] = c;

        c = new Card();
        c.value = Card.Value.SEVEN;
        c.suite = Card.Suite.HEARTS;
        cards[2] = c;

        c = new Card();
        c.value = Card.Value.FOUR;
        c.suite = Card.Suite.CLUBS;
        cards[3] = c;

        c = new Card();
        c.value = Card.Value.ACE;
        c.suite = Card.Suite.DIAMONDS;
        cards[4] = c;

        hand = new Hand(cards);
        
        hand.sort_by_value();

        assertTrue(hand.one_pair());
    }

    private Game game;
    private Card[] cards;
    private Card c;
    private Hand hand;
}
//for(int i = 0; i < hand.cards.length; i++) System.out.println(hand.cards[i].value + "\n" + hand.cards[i].suite);