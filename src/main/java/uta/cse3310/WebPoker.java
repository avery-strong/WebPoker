
// This code is based upon, and derived from the this repository
//            https:/thub.com/TooTallNate/Java-WebSocket/tree/master/src/main/example

// http server include is a GPL licensed package from
//            http://www.freeutils.net/source/jlhttp/

/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package uta.cse3310;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import java.nio.ByteBuffer;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class WebPoker extends WebSocketServer {
    private void setNumPlayers(int N) {
        numPlayers = N;
    }
    
    public WebPoker(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        numPlayers = 0;
    }
    
    public WebPoker(InetSocketAddress address) {
        super(address);
        numPlayers = 0;
    }
    
    public WebPoker(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
        numPlayers = 0;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        /*
            Player enters a game and is given a unique id.
            This does not represent any positions in a list.
            Mostly similar to a naming convention.
        */
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected");

        numPlayers++;
        System.out.println("\n\n" + numPlayers + "\n\n");
        synchronized(mutex){
            // Maximum amount of players currently in the mgame      
            if(game.players_size() >= 5){             
                player = new Player(numPlayers);            // New player is created and given no id
                game.playerQueue_add(player);              // Player is added to a queue waiting to enter a game
            }
            // Not all current players have readied up so the so still in name phase
            else if(game.get_phase() == 0){
                player = new Player(numPlayers);            // New player is created and given their unique Id
                game.players_add(player);                    // Player is added to the game
            }
            // add to queue if a game is in session
            else{       
                player = new Player(numPlayers);            // New player is created and given no id
                game.playerQueue_add(player);
            }
        }

        conn.setAttachment(numPlayers);
        conn.send(player.asJSONString());               // We send the player to the client so the client knows who it is viewing

        System.out.println("\n\n" + player.asJSONString() + "\n\n");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(conn + " has closed");

        // The state is now changed, so every client needs to be informed
        synchronized(mutex){
            int idx = conn.getAttachment();

            System.out.println("removed player index " + idx);

            Player disconnectedPlayer = game.get_player(idx);

            // If there are players in playerQueue (5 players already playing)
            if(game.playerQueue_size() > 0){
                // If player disconnects while a game is in progress
                if(game.get_phase() != 0){          // I dont understand why the phase matters
                    // If disconnected player is in game.players
                    if(game.players_contains(disconnectedPlayer)){       
                        game.nonFolded_remove(disconnectedPlayer);
                        game.players_remove(disconnectedPlayer);
                    }
                    // player is in queue
                    else{ 
                        // remove player with corresponding id
                        // from the queue
                        game.playerQueue_remove(disconnectedPlayer);
                    }
                }
                // DCed player is in queue
                else if(game.playerQueue_contains(disconnectedPlayer)) game.playerQueue_remove(disconnectedPlayer); // Remove the player from the queue
                // if phase is 0 (DCed player is in game)
                else{
                    if(disconnectedPlayer != null && game.nonFolded_contains(disconnectedPlayer)) game.nonFolded_remove(disconnectedPlayer);
                
                    game.players_remove(disconnectedPlayer);
                    // add first in queue
                    game.playerQueue_remove(game.playerQueue_get());
                    game.players_add(game.playerQueue_get());
                }
            }
            // If there are less than 5 players in the game
            else{    
                /*
                    Must use try and catch in cases where the player that disconnected
                    is not in the arrays (could be folded)

                    I dont understand this, whenever a player enters they should be put into an array
                    dependent on the size of players array
                */
                try{
                    game.nonFolded_remove(disconnectedPlayer);
                } catch(Exception e){ System.out.println(e); }
                try{
                    game.players_remove(disconnectedPlayer);
                } catch(Exception e){ System.out.println(e); }

                numPlayers--;
            }

            broadcast(game.exportStateAsJSON());
            System.out.println("the game state" + game.exportStateAsJSON());
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // all incoming messages are processed by the game
        synchronized(mutex){
            game.processMessage(numPlayers, message);
            // and the results of that message are sent to everyone
            // as the "state of the game"

            broadcast(game.exportStateAsJSON());
        }

        System.out.println(conn + ": " + message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message){
        synchronized(mutex){
            broadcast(message.array());
        }

        System.out.println(conn + ": " + message);
    }

    public class upDate extends TimerTask {
        @Override
        public void run(){
            if (game != null){
                synchronized(mutex){
                    if (game.update()) broadcast(game.exportStateAsJSON());
                }
            }
        }
    }


    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
        // some errors like port binding failed may not be assignable to a specific
        // websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
        setNumPlayers(-1);
        // once a second call update
        // may want to start this in the main() function??
        new java.util.Timer().scheduleAtFixedRate(new upDate(), 0, 1000);
        game = new Game();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        // Create and start the http server

        HttpServer H = new HttpServer(8080, "./html");
        H.start();

        // create and start the websocket server

        int port = 8880;

        WebPoker s = new WebPoker(port);
        s.start();
        System.out.println("WebPokerServer started on port: " + s.getPort());

        // Below code reads from stdin, making for a pleasant way to exit
        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String in = sysin.readLine();
            s.broadcast(in);
            if (in.equals("exit")){
                s.stop(1000);
                break;
            }
        }
    }

    /**********************************************

                        Attributes

    **********************************************/

    public static int numPlayers;
    private Game game;
    public static Object mutex = new Object();

    private Player player;
}
