/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mychessmate;

import java.io.Serializable;

/**
 *
 * 
 */

/*
    this class to save location of piece when has moved
*/
public class Move implements Serializable{
    int source_location;            //this is the start location of piece
    int destination;                //this is the end location of piece after move in board
    
    //Initialition
    public Move(){  
        source_location = -1;
        destination = -1;
    }
    
    //Initialition with start and the end point
    public Move(int source_location, int destination) {
        this.source_location = source_location;
        this.destination = destination;
    }
}
