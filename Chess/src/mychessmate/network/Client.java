/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychessmate.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mychessmate.Move;
import mychessmate.MyChessmate;
import mychessmate.Piece;
import mychessmate.Position;

/**
 *
 * 
 */

class Client implements Runnable //connecting client
{

    //Network fields
    private Socket s;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public String nick;
    private Table table;
    protected boolean wait4undoAnswer = false;
    
    // Game logic fields
    private boolean myTurn = false;
    private final static MyChessmate myChessmate = new MyChessmate();
    private Move move = new Move();
    private boolean isEndGame = false;

    
    //
    Client(Socket s, ObjectInputStream input, ObjectOutputStream output, String nick, Table table)
    {
        this.s = s;
        this.input = input;
        this.output = output;
        this.nick = nick;
        this.table = table;

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() //listening
    {

        System.out.print("Client Virtual: running function: run()\n");
        boolean isOK = true;
        while (isOK)
        {
            try {
                String in = input.readUTF();
                switch (in) {
                    case "#move":
                        //new move
                        int source_location = input.readInt();
                        int destination = input.readInt();
                        table.sendMoveToOther(this, source_location, destination);
                        break;
                    case "#time":
                        //new move
                        int time = input.readInt();
                        table.sendTime(this, time);
                        break;
                    case "#message":
                        //new message
                        String str = input.readUTF();
                        table.sendMessageToAll(nick + ": " + str);
                        break;
                    case "#sendMessage":
                        //new message
                        String strs = input.readUTF();
                        String str_send=nick + ": " + strs;
                        table.sendMessageToOtherPlayer(this, str_send);
                        break;
                    case "#massagePosition":
                        table.sendToClientPlayer2();
                        break;
                    case "#position":
                        try {
                            Position positon=(Position) input.readObject();
                            List<Position> history_position=(List<Position>) input.readObject();  
                            table.sendPositonToObserver(positon,history_position);
                        } catch (ClassNotFoundException ex) {}
                        break;
                    case "#sendContinue":
                        table.sendContinuePlayer2();
                        break;
                    case "#newGame" :
                        table.sendNewGame();
                        break;
                    case "#noNewGame" :
                        table.sendNoNewGame();
                        break;
                    case "#messReplay" :
                        table.sendMessReplay(this);
                        break;
                    case "#messReplay2" :
                        table.sendMessReplay2(this);
                        break;
                    case "#Replay" :
                        table.sendReplay();
                        break;
                    case "#Replay2" :
                        table.sendReplay2();
                        break;
                    case "#noReplay" :
                        table.sendNoReplay(this);
                        break;
                    case "#Play" :
                        table.sendPlay(this);
                        break;
                    case "#exit" :
                        s.close();
                        input.close();
                        output.close();
                        table.Exit(this);
                        isOK = false;
                        break;
                    default:
                        break;
                }
            }
            catch (IOException ex)
            {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                isOK = false;
                try
                {
                    table.sendErrorConnectionToOther(this);
                    return;
                }
                catch (IOException ex1)
                {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }

        }
    }
}
