/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychessmate.network;

import mychessmate.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 */
public class Table
{//Table: {two player, one chessboard and x observers}

    private Client clientPlayer1;                    // Client đại diện player thứ nhất
    private Client clientPlayer2;                    // Client đại diện cho player thứ 2
    private ArrayList<Client> clientObservers;       // Các client xem ván đánh
    public String password;                          // Password của ván game
    private boolean canObserversJoin;                // Báo còn có thể join vào xem không
    private boolean enableChat;                      // Báo cáo cho phép chat không
    //Phát triển cho chế độ xin đi lại
    //private ArrayList<Move> movesList;               // Danh sách các nước đi
    private Move move;                      // Vị trí mới nhất vừa di chuyển
    private MyChessmate chessmate;
//    private Position position;

    Table(String password, boolean canObserversJoin, boolean enableChat)
    {
        this.password = password;
        this.enableChat = enableChat;
        this.canObserversJoin = canObserversJoin;

        if (canObserversJoin)
        {
            clientObservers = new ArrayList<>();
        }

        //movesList = new ArrayList<Move>();
    }

    //Gửi lượt di chuyển tới tất cả client trừ client vừa di chuyển
    public void sendMoveToOther(Client sender, int source_location, int destination ) throws IOException
    {
        // Thông báo ra console
        print("running function: sendMoveToOther(" + sender.nick + ", " + source_location + ", " + destination);

        if (sender == clientPlayer1 || sender == clientPlayer2) //Chỉ có thể 1 trong 2 player có thể di chuyển
        {
            Client receiver = (clientPlayer1 == sender) ? clientPlayer2 : clientPlayer1; // gửi tới 1 trong 2 play đang chờ
            receiver.output.writeUTF("#move");
            receiver.output.writeInt(source_location);
            receiver.output.writeInt(destination);
            print(receiver.nick +" is sending.");
            receiver.output.flush();
            
            if (canObserversJoin())
            {   if(sender == clientPlayer1)
                    for (Client observer : clientObservers)
                    {
                        observer.output.writeUTF("#move");
                        observer.output.writeInt(source_location);
                        observer.output.writeInt(destination);
                        observer.output.flush();
                    }
                else{
                    for (Client observer : clientObservers)
                    {
                        observer.output.writeUTF("#move");
                        observer.output.writeInt(119-source_location);
                        observer.output.writeInt(119-destination);
                        observer.output.flush();
                    }
                }
            }
        }
    }
    public void sendTime(Client sender,int time) throws IOException
    {
        // Thông báo ra console

        if (sender == clientPlayer1 || sender == clientPlayer2) //Chỉ có thể 1 trong 2 player có thể di chuyển
        {
            Client receiver = (clientPlayer1 == sender) ? clientPlayer2 : clientPlayer1; // gửi tới 1 trong 2 play đang chờ
            receiver.output.writeUTF("#time");
            receiver.output.writeInt(time);
            print(receiver.nick +" is sending.");
            receiver.output.flush();
            
            if (canObserversJoin())
            {   if(sender == clientPlayer1)
                    for (Client observer : clientObservers)
                    {
                        observer.output.writeUTF("#timeOv");
                        observer.output.writeInt(1);
                        observer.output.writeInt(time);
                        observer.output.flush();
                    }
                else{
                    for (Client observer : clientObservers)
                    {
                        observer.output.writeUTF("#timeOv");
                        observer.output.writeInt(2);
                        observer.output.writeInt(time);
                        observer.output.flush();
                    }
                }
            }
        }
    }
    //gửi thông báo lấy position đến client 2
    public void sendToClientPlayer2() throws IOException{
        if (clientPlayer2 != null){
            clientPlayer2.output.writeUTF("#massagePosition");
            clientPlayer2.output.flush();
        }        
    }
    // Client gửi position đến tất cả các client khác
    public void sendPositonToObserver(Position positon,List<Position> history_position) throws IOException{
        if (canObserversJoin()){
            Client observer = clientObservers.get(clientObservers.size()-1);
            observer.output.writeUTF("#position");
            observer.output.writeObject(positon);
            observer.output.writeObject(history_position);
            observer.output.flush();
        }
    }
    
    // Gửi thông tin đến tất cả client trừ client đã viết
    public void sendToAll( Client sender, String msg ) throws IOException
    {
        if( sender == clientPlayer1 || sender == clientPlayer2 )
        {
            Client receiver = (clientPlayer1 == sender) ? clientPlayer2 : clientPlayer1;
            receiver.output.writeUTF( msg );
            receiver.output.flush();
            
            if (canObserversJoin())
            {
                for (Client observer : clientObservers)
                {
                    observer.output.writeUTF( msg );
                    observer.output.flush();
                }
            }            
        }
    }
    
    // Chỉ gửi tin nhắn đến người đang chơi với mình
    public void sendToOtherPlayer( Client sender, String msg ) throws IOException
    {
        if( sender == clientPlayer1 || sender == clientPlayer2 )
        {
            Client receiver = (clientPlayer1 == sender) ? clientPlayer2 : clientPlayer1;
            receiver.output.writeUTF( msg );
            receiver.output.flush();     
        }
    }

    //Báo lỗi kết nối đến người chơi cùng bàn
    //send only if sender is player (not observer)
    public void sendErrorConnectionToOther(Client sender) throws IOException
    {
        print("SendErrorConnectionToOther(" + sender.nick);

        if (sender == clientPlayer1 || sender == clientPlayer2) // Nếu 1 trong 2 là player bị mất kết nối
        {
            if (clientPlayer1 != sender)
            {
                clientPlayer1.output.writeUTF("#errorConnection");
                clientPlayer1.output.flush();
            }
            if (clientPlayer2 != sender)
            {
                clientPlayer2.output.writeUTF("#errorConnection");
                clientPlayer2.output.flush();
            }

            if (canObserversJoin())
            {
                for (Client observer : clientObservers)
                {
                    observer.output.writeUTF("#errorConnection");
                    observer.output.flush();
                }
            }
        }
    }
    //Gọi Player1 start game
    public void sendStartGameToPlayer1()throws IOException{
        print("SendStartGameToPlayer1");
        clientPlayer1.output.writeUTF("#StartGame");
        clientPlayer1.output.flush();
    }
    //setEditable(true)
    public void sendToAllPlayer()throws IOException{
        clientPlayer1.output.writeUTF("#setEditable");
        clientPlayer1.output.flush();
        clientPlayer2.output.writeUTF("#setEditable");
        clientPlayer2.output.flush();
        if (canObserversJoin()){
            for(Client client:clientObservers){
                client.output.writeUTF("#setEditable");
                client.output.flush();
            }
        }
    }
    public void sendToObserver() throws IOException{
        if (canObserversJoin()){
            clientObservers.get(clientObservers.size()-1).output.writeUTF("#setEditable");
            clientObservers.get(clientObservers.size()-1).output.flush();
        }
    }
    //gửi tin nhắn đến tất cả các người chời khác trừ mình
    public void sendMessageToOtherPlayer(Client sender, String str)throws IOException{
        if( sender == clientPlayer1 || sender == clientPlayer2 )
        {
            Client receiver = (clientPlayer1 == sender) ? clientPlayer2 : clientPlayer1;
            receiver.output.writeUTF("#sendMessage");
            receiver.output.writeUTF( str );
            receiver.output.flush();
            if (canObserversJoin())
            {
                for (Client observer : clientObservers)
                {
                    observer.output.writeUTF("#sendMessage");
                    observer.output.writeUTF( str );
                    observer.output.flush();
                }
            }            
        }
        else{
            if (clientPlayer1 != null){
                clientPlayer1.output.writeUTF("#sendMessage");
                clientPlayer1.output.writeUTF(str);
                clientPlayer1.output.flush();
            }

            if (clientPlayer2 != null){
                clientPlayer2.output.writeUTF("#sendMessage");
                clientPlayer2.output.writeUTF(str);
                clientPlayer2.output.flush();
            }
            if (canObserversJoin())
            {
                for (Client observer : clientObservers)
                {
                    if(sender!=observer){
                        observer.output.writeUTF("#sendMessage");
                        observer.output.writeUTF( str );
                        observer.output.flush();
                    }
                }
            }  
        }
    }
    //Gửi tin nhắn đến tất cả client từ 1 client nào đó
    public void sendMessageToAll(String str) throws IOException
    {
        print("SendMessageToAll(" + str);

        if (clientPlayer1 != null)
        {
            clientPlayer1.output.writeUTF("#message");
            clientPlayer1.output.writeUTF(str);
            clientPlayer1.output.flush();
        }

        if (clientPlayer2 != null)
        {
            clientPlayer2.output.writeUTF("#message");
            clientPlayer2.output.writeUTF(str);
            clientPlayer2.output.flush();
        }

        if (canObserversJoin())
        {
            for (Client observer : clientObservers)
            {
                observer.output.writeUTF("#message");
                observer.output.writeUTF(str);
                observer.output.flush();
            }
        }
    }
    public void sendContinuePlayer2() throws IOException{
        clientPlayer2.output.writeUTF("#sendContinue");
        clientPlayer2.output.flush();
    }
    
    public void sendNewGame() throws IOException{
        print("New Game");
        if (clientPlayer1 != null)
        {
            clientPlayer1.output.writeUTF("#newGame");
            clientPlayer1.output.flush();
        }

        if (clientPlayer2 != null)
        {
            clientPlayer2.output.writeUTF("#newGame");
            clientPlayer2.output.flush();
        }

        if (canObserversJoin())
        {
            for (Client observer : clientObservers)
            {
                observer.output.writeUTF("#newGame");
                observer.output.flush();
            }
        }
    }
    public void sendNoNewGame() throws IOException{
        clientPlayer1.output.writeUTF("#noNewGame");
        clientPlayer1.output.flush();
    }
    public void sendMessReplay(Client client) throws IOException{
        if( client == clientPlayer1 || client == clientPlayer2 )
        {
            String str="";
            Client receiver = (clientPlayer1 == client) ? clientPlayer2 : clientPlayer1;
            if(receiver==clientPlayer1)
                str="Player 2";
            else if(receiver==clientPlayer2)
                str="Player 1";
            receiver.output.writeUTF("#messReplay");
            receiver.output.writeUTF(str);
            receiver.output.flush();
        }
    }
    public void sendMessReplay2(Client client) throws IOException{
        if( client == clientPlayer1 || client == clientPlayer2 )
        {
            String str="";
            Client receiver = (clientPlayer1 == client) ? clientPlayer2 : clientPlayer1;
            if(receiver==clientPlayer1)
                str="Player 2";
            else if(receiver==clientPlayer2)
                str="Player 1";
            receiver.output.writeUTF("#messReplay2");
            receiver.output.writeUTF(str);
            receiver.output.flush();
        }
    }
    public void sendReplay() throws IOException{
        if (clientPlayer1 != null)
        {
            clientPlayer1.output.writeUTF("#Replay");
            clientPlayer1.output.flush();
        }

        if (clientPlayer2 != null)
        {
            clientPlayer2.output.writeUTF("#Replay");
            clientPlayer2.output.flush();
        }

        if (canObserversJoin())
        {
            for (Client observer : clientObservers)
            {
                observer.output.writeUTF("#Replay");
                observer.output.flush();
            }
        }
    }
    public void sendReplay2() throws IOException{
        if (clientPlayer1 != null)
        {
            clientPlayer1.output.writeUTF("#Replay2");
            clientPlayer1.output.flush();
        }

        if (clientPlayer2 != null)
        {
            clientPlayer2.output.writeUTF("#Replay2");
            clientPlayer2.output.flush();
        }

        if (canObserversJoin())
        {
            for (Client observer : clientObservers)
            {
                observer.output.writeUTF("#Replay2");
                observer.output.flush();
            }
        }
    }
    public void sendNoReplay(Client client) throws IOException{
        if( client == clientPlayer1 || client == clientPlayer2 )
        {
            String str="";
            Client receiver = (clientPlayer1 == client) ? clientPlayer2 : clientPlayer1;
            if(receiver==clientPlayer1)
                str="Player 1";
            else if(receiver==clientPlayer2)
                str="Player 2";
            receiver.output.writeUTF("#noReplay");
            receiver.output.writeUTF(str);
            receiver.output.flush();
        }
    }
    public void sendPlay(Client client) throws IOException{
        if( client == clientPlayer1 || client == clientPlayer2 )
        {
            Client receiver = (clientPlayer1 == client) ? clientPlayer2 : clientPlayer1;
            receiver.output.writeUTF("#Play");
            receiver.output.flush();
        }
    }
    public void Exit(Client client) throws IOException{
        if(client==clientPlayer2){
            clientPlayer2=null;
            if(clientPlayer1!=null){
                clientPlayer1.output.writeUTF("#exit");
                clientPlayer1.output.flush();
            }
            if (canObserversJoin()){
                for (Client observer : clientObservers)
                {
                    observer.output.writeUTF("#exit");
                    observer.output.flush();
                }
            }
        }
        else if(client!=clientPlayer2&&client!=clientPlayer1){
            for(Client c:clientObservers){
                if(c==client){
                    System.out.println(client+" Exit");
                    clientObservers.remove(c);
                    return;
                }
            }
        }
    }
    // Báo đã hết người chơi, đã đủ người chơi
    public boolean isAllPlayers()
    {
        //is it all playing players
        return !(clientPlayer1 == null || clientPlayer2 == null);
    }

    // Kiểm tra xem có Người xem nào không
    public boolean isObservers()
    {
        return !clientObservers.isEmpty();
    }
    
    // Kiểm tra có thể cho người xem vào phòng không
    public boolean canObserversJoin()
    {
        return this.canObserversJoin;
    }

    // Thêm một Player mới
    // thứ tự là 1 rồi mối đến 2
    public void addPlayer(Client client)
    {
        if (clientPlayer1 == null)
        {
            clientPlayer1 = client;
            print("Player1 connected");
        }
        else if (clientPlayer2 == null)
        {
            clientPlayer2 = client;
            print("Player2 connected");
        }
    }
    
    // Thêm người xem vào phòng
    public void addObserver(Client client)
    {
        System.out.println(client);
        clientObservers.add(client);
    }
    
    /**
     * Get client 1 hay là người chơi 1
     * @return Client1
     */
    public Client getPlayerOne(){
        return clientPlayer1;
    }
    /**
     * Gét client 2 hay là người chơi 2
     * @return Client2
     */
    public Client getPlayerTwo(){
        return clientPlayer2;
    }
    /**
     * Get client là Observers 
     * @return Client is Observers
     */
    public ArrayList<Client> getObservers(){
        return clientObservers;
    }
    
    private void print(String msg){
        System.out.println("Table: "+ msg);
    }    
}
