package uta.cse3310;

import java.util.*;

public class Pot{

    public Pot(){}

    /***********************************
                    Getters
    ***********************************/

    public int get_pot(){ return this.pot; }

    /***********************************
                    Setters
    ***********************************/

    public void add_to_pot(int amount){ this.pot += amount; }
    public void empty_pot()           { this.pot = 0; }

    /***********************************
                    Attributes
    ***********************************/

    private int pot;
}
