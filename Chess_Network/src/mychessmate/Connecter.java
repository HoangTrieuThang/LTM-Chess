/**
 * 
 */
package mychessmate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Connecter {
    public final static String ip = "localhost";
    public final static int port = 22222;
    
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    boolean accepted;
    ServerSocket server_socket;   
    MyChessmate mychessmate;
    
    public Connecter(){
        accepted = false;
        socket = null;
        dis = null;
        dos = null;
        server_socket = null;
    }
    public boolean connect(){
        try{
            socket = new Socket(ip,port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
        }
        catch (IOException e){
            return false;
        }
        System.out.print("Connet to localhost:22222 successfuly\n");
        return true;
    }
    
    public void initializeServer(){
        try{
            server_socket = new ServerSocket(port, 8, InetAddress.getByName(ip));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void listenForServerRequest(){
        Socket socket = null;
        try{
            socket = server_socket.accept();
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
            System.out.print("Client request\n");
        }
        catch(IOException e){
        }
    }
}
