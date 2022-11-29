var amount = 0;
var amountDraw = 0;
var cardIdx = 0;
var eventType;
var indexes = [0, 0, 0];
var infoToggle = false;
var inQueue = false;
var name;
var phase = 0;
var playerID;
var playerObj;
var gameObj;

//document.getElementById('chips').src = "";
document.getElementById('infoButton').style = "text-align:center";

var serverUrl ="ws://" + window.location.hostname + ":8880";
var connection = new WebSocket(serverUrl);

/**************************************
 
                Connections
 
**************************************/

connection.onopen = function(evt){
    console.log("Open: \n" + evt.data);
    document.getElementById("name_display").style.display = "block";
}
connection.onmessage = function(evt){
    var msg;
    msg = evt.data;
    // Take the msg and turn it into a javascript object
    const obj = JSON.parse(msg);

    gameObj = obj;

    display_info(obj);
    if(phase > 0) display_cards();
    display_buttons(obj);
}
function send(){
    const msg = {
        event: eventType,
        name: name,
        playerID: playerID,
        amount_to_bet: amount,
        amount_to_draw: amountDraw,
        give_card_indexes: indexes
    };
    connection.send(JSON.stringify(msg));
}

/**************************************
 
                Events
 
**************************************/

function event_bet_05(){
    eventType = "BET";
    amount = 5;
    send();
}
function event_bet_10(){
    eventType = "BET";
    amount = 10;
    send();
}
function event_bet_20(){
    eventType = "BET";
    amount = 20;
    send();
}
function event_call(){
    eventType = "CALL";
    send();
}
function event_check(){
    eventType = "CHECK";

    gameObj.players.forEach(player =>{
        document.getElementById("buttons_display").style.display = "block";
        document.getElementById("sendCheck").disabled = true;
    });

    send();
}
function event_draw(){
    eventType = "DRAW";

    if(document.getElementById('card1checkbox').checked == true){
        for(var i = 0; i < 3; i++){
            if(indexes[i] == 0){
                indexes[i] = 1;
                break;
            }
        }
    }
    if(document.getElementById('card2checkbox').checked == true){
        for(var i = 0; i < 3; i++){
            if(indexes[i] == 0){
                indexes[i] = 2;
                break;
            }
        }
    }
    if(document.getElementById('card3checkbox').checked == true){
        for(var i = 0; i < 3; i++){
            if(indexes[i] == 0){
                indexes[i] = 3;
                break;
            }
        }
    }
    if(document.getElementById('card4checkbox').checked == true){
        for(var i = 0; i < 3; i++){
            if(indexes[i] == 0){
                indexes[i] = 4;
                break;
            }
        }
    }
    if(document.getElementById('card5checkbox').checked == true){
        for(var i = 0; i < 3; i++){
            if(indexes[i] == 0){
                indexes[i] = 5;
                break;
            }
        }
    }

    for(var i; i < 3; i++){
        if(indexes[i] != 0){
            amountDraw++;
            break;
        }
    }

    document.getElementById('card1checkbox').checked = false;
    document.getElementById('card2checkbox').checked = false;
    document.getElementById('card3checkbox').checked = false;
    document.getElementById('card4checkbox').checked = false;
    document.getElementById('card5checkbox').checked = false;
    // get the indexes of the cards

    gameObj.players.forEach(player =>{
        document.getElementById("buttons_display").style.display = "block";
        document.getElementById("sendCheck").disabled = false;
        document.getElementById("sendReady").disabled = false;
    });

    send();
}
function event_fold(){
    eventType = "FOLD";

    document.getElementById("buttons_display").style.display = "none";

    send();
}
function event_name(){
    eventType = "NAME";
    name = document.getElementById("event_name").value;

    // Disables the name display
    document.getElementById("name_display").style.display = "none";

    // Enables the info and buttons display
    document.getElementById("info_display").style.display = "block";
    document.getElementById("buttons_display").style.display = "block";

    // Ready will be the first button
    document.getElementById("sendReady").style.display = "inline-block";

    send();    
}
function event_ready(){
    eventType = "READY";
    
    document.getElementById("sendReady").disabled = true;

    send();
}
function event_sort(){
    eventType = "SORT";
    send();
}

/**************************************
 
                Display
 
**************************************/

function display_bet_01(obj){         
    // display the player's cards
    display_cards();
}
function display_buttons(obj){
    // only ready is displayed


    // Finally got my idea worked out
    // Diable buttons in the event function
    // Display them using the display_buttons function call
    // Probably keep the call in its current spot just need to uncomment

    switch(obj.phase){
        case 0: 
            document.getElementById("cards_display").style.display = "none";

            document.getElementById("sendReady").style = "text-align:center";

            document.getElementById('sendCheck').style.display  = "none";
            document.getElementById('sendCall').style.display   = "none";
            document.getElementById('sendFold').style.display   = "none";
            document.getElementById('sendBet1').style.display   = "none";
            document.getElementById('sendBet2').style.display   = "none";
            document.getElementById('sendBet3').style.display   = "none";
            document.getElementById('sendDraw').style.display   = "none";
            document.getElementById('sendSort').style.display   = "none";

            break;
        case 1:
            // bet 05, bet 10, bet 20, call, check, fold, sort
            document.getElementById("sendReady").style.display = "none";                                        
            document.getElementById('sendCheck').style          = "text-align:center";
            document.getElementById('sendCall').style           = "text-align:center";
            document.getElementById('sendFold').style           = "text-align:center";
            document.getElementById('sendBet1').style           = "text-align:center";
            document.getElementById('sendBet2').style           = "text-align:center";
            document.getElementById('sendBet3').style           = "text-align:center";
            document.getElementById('sendDraw').style.display   = "none";
            document.getElementById('sendSort').style           = "text-align:center";

            if(obj.turn.name != obj.players[0].name) document.getElementById('sendCheck').disabled = true;

            //if(obj.players[0].check) 
            
            break;
        case 2:    // Draw Phase
            document.getElementById('sendCheck').style.display  = "none";
            document.getElementById('sendCall').style.display   = "none";
            document.getElementById('sendFold').style.display   = "none";
            document.getElementById('sendBet1').style.display   = "none";
            document.getElementById('sendBet2').style.display   = "none";
            document.getElementById('sendBet3').style.display   = "none";
            document.getElementById('sendDraw').style           = "text-align:center";
            document.getElementById('sendSort').style           = "text-align:center";
            
            break;
        case 3:    // Bet 02
            document.getElementById('sendCheck').style          = "text-align:center";
            document.getElementById('sendCall').style           = "text-align:center";
            document.getElementById('sendFold').style           = "text-align:center";
            document.getElementById('sendBet1').style           = "text-align:center";
            document.getElementById('sendBet2').style           = "text-align:center";
            document.getElementById('sendBet3').style           = "text-align:center";
            document.getElementById('sendDraw').style.display   = "none";
            document.getElementById('sendSort').style           = "text-align:center";

            if(obj.players[0].check) document.getElementById('sendCheck').disabled = true;
            
            break;
        case 4:    // Showdown
            if(!obj.tie) alert(obj.winningPlayer.name + " won" + " with a "+ obj.winningPlayer.pHand.handname);
            else alert("Tie!");  

            document.getElementById("sendReady").style          = "text-align:center";
            document.getElementById("sendReady").style.disabled = false;

            document.getElementById('sendCheck').style.display  = "none";
            document.getElementById('sendFold').style.display   = "none";
            document.getElementById('sendCall').style.display   = "none";
            document.getElementById('sendBet1').style.display   = "none";
            document.getElementById('sendBet2').style.display   = "none";
            document.getElementById('sendBet3').style.display   = "none";
            document.getElementById('sendDraw').style.display   = "none";
            document.getElementById('sendSort').style.display   = "none";

            break;
        default:
            break;
    }  
}
function display_cards(){
    document.getElementById("cards_display").style.display = "block";

    for(var i = 0; i < 5; i++){
        // if cards are null str becomes empty so nothing is displayed
        var str = "";
        var checkboxnum = "card" + (i+1) + "checkbox";

        //for(var j = 0; j < playerObj.playerHand.cards.length; j++) console.log(card.value);

        if(playerObj.playerHand.cards[i].value == "ACE")             str += "A";
        else if(playerObj.playerHand.cards[i].value == "TWO")        str += "2";
        else if(playerObj.playerHand.cards[i].value == "THREE")      str += "3";
        else if(playerObj.playerHand.cards[i].value == "FOUR")       str += "4";
        else if(playerObj.playerHand.cards[i].value == "FIVE")       str += "5";
        else if(playerObj.playerHand.cards[i].value == "SIX")        str += "6";
        else if(playerObj.playerHand.cards[i].value == "SEVEN")      str += "7";
        else if(playerObj.playerHand.cards[i].value == "EIGHT")      str += "8";
        else if(playerObj.playerHand.cards[i].value == "NINE")       str += "9";
        else if(playerObj.playerHand.cards[i].value == "TEN")        str += "T";
        else if(playerObj.playerHand.cards[i].value == "JACK")       str += "J";
        else if(playerObj.playerHand.cards[i].value == "QUEEN")      str += "Q";
        else if(playerObj.playerHand.cards[i].value == "KING")       str += "K";

        if(playerObj.playerHand.cards[i].suite == "SPADES")          str += "S";
        else if(playerObj.playerHand.cards[i].suite == "HEARTS")     str += "H";
        else if(playerObj.playerHand.cards[i].suite == "CLUBS")      str += "C";
        else if(playerObj.playerHand.cards[i].suite == "DIAMONDS")   str += "D";

        str += ".svg";
        
        var cardnumber = "card" + (i+1);
        document.getElementById(cardnumber).src = str;
    }
}
function display_connect(obj){}
function display_info(obj){         // Most events will need to call display_info()
    /*
        When onOpen is called onMessage is called as well and sent just the first bit of Player JSON
        At this point we will add playerID from obj.id so we can find what we need later on

        It appears when onOpen/onMessage are called the first time the object that exists is solely a player object.
        After a name has been set the objects coming in are now the entire game
    */
    if(obj.name == "not set"){
        playerID = obj.id;
        playerObj = obj;
    }
    // Essentially after a name is set display_info will move here every time
    else{
        playerObj = obj.players[playerID];
        
        console.log(
            "Player Name: "       + playerObj.name
            + "\n\tID: "          + playerObj.id
            + "\n\tWallet: "      + playerObj.wallet
            + "\n\tCurrent Bet: " + playerObj.currentBet
            + "\n\tTotal Bet: "   + playerObj.totalBet
            + "\n\tBet: "         + playerObj.bet
            + "\n\tCheck: "       + playerObj.check
            + "\n\tDraw: "        + playerObj.draw
            + "\n\tFold: "        + playerObj.fold
            + "\n\tRaised: "      + playerObj.raised
            + "\n\tReady: "       + playerObj.ready
            + "\n\tPhase: "       + obj.phase
            + "\n\tTurn: "        + obj.turn.name
            + "\n\tPlayer: "      + obj.players[0].name
            + "\n");
        
        // display whose turn it is
        document.getElementById("gameMessage").innerHTML = obj.gameMessage;
          
        document.getElementById("info").innerHTML = 
            "Player Wallet: "   + playerObj.wallet
            + "\nPlayer Name: " + playerObj.name
            + "\nPlayer Id: "   + playerObj.id
            + "\nPlayer Hand: " + playerObj.playerHand.hand;

        // display the amount in the pot
        document.getElementById("pot").innerHTML = "Total Pot: " + obj.pot.pot;
        //document.getElementById('chips').src = "chips.png";

        // display_time(obj);

        // display the total bets of all players
        var playerTotalBetString = "";

        obj.players.forEach(player =>{
            playerTotalBetString += "id: " + player.id 
                + " | " + player.name
                + " | Current Bet: " + player.currentBet
                + " Wallet: " + player.wallet
                + " ready: " + (player.ready?"Yes":"No")
                + " fold: " + (player.fold?"Yes":"No")
                + "\n";
        });

        document.getElementById("playersTotalBets").innerHTML = playerTotalBetString;

        phase = obj.phase;
    }
}
function display_time(obj){
    if(obj.timeRemaining > -1) document.getElementById("timeRemaining").innerHTML = " Time Left: " + obj.timeRemaining;
    else document.getElementById("timeRemaining").innerHTML = "";
  
    if(obj.timeRemaining <= 10 && obj.timeRemaining > -1 && playerObj.id == obj.currentplayer.id && obj.phase != 0)
        document.getElementById("timeWarning").innerHTML = "Please make a choice or you will be folded!";
    else if( obj.phase != 0 && playerObj.id == obj.currentplayer.id )
        document.getElementById("timeWarning").innerHTML = "It is your turn!";
  
    else document.getElementById("timeWarning").innerHTML = "";
  
    if(obj.phase == 1 && playerObj.id == obj.currentplayer.id){
        document.getElementById('turnHelp').innerHTML = "Your bet must match the highest to keep playing. Total per player: " + obj.highestBet;
    }
    else if(obj.phase == 2 && playerObj.id == obj.currentplayer.id){
        document.getElementById('turnHelp').innerHTML = "Draw up to 3 selected cards.";
    }
    else if(obj.phase == 3 && playerObj.id == obj.currentplayer.id){
        document.getElementById('turnHelp').innerHTML = "Your bet must match the highest to keep playing. Total per player: " + obj.highestBet;
    }
    else document.getElementById('turnHelp').innerHTML = "";

    document.getElementById('timeWarning').style = "text-align:center";
    document.getElementById('turnHelp').style = "text-align:center";
}
function toggleInfo(){
    if(!infoToggle){
        infoToggle = true;
        document.getElementById('info1').style.display = "none";
        document.getElementById('info2').style.display = "none";
        document.getElementById('info3').style.display = "none";
        document.getElementById('info4').style.display = "none";
        document.getElementById('info5').style.display = "none";
        document.getElementById('info1text').style.display = "none";
        document.getElementById('info2text').style.display = "none";
        document.getElementById('info3text').style.display = "none";
        document.getElementById('info4text').style.display = "none";
        document.getElementById('info5text').style.display = "none";
    }
    else{
        infoToggle = false;
        document.getElementById('info1').style.display = "block";
        document.getElementById('info2').style.display = "block";
        document.getElementById('info3').style.display = "block";
        document.getElementById('info4').style.display = "block";
        document.getElementById('info5').style.display = "block";
        document.getElementById('info1text').style.display = "block";
        document.getElementById('info2text').style.display = "block";
        document.getElementById('info3text').style.display = "block";
        document.getElementById('info4text').style.display = "block";
        document.getElementById('info5text').style.display = "block";
    }
}