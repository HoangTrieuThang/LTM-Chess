package mychessmate.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mychessmate.Move;
import mychessmate.MyChessmate;
import mychessmate.Position;


public class ClientGame implements Runnable
{
    
    //private static final Logger LOG = Logger.getLogger(Client.class);

    public static boolean isPrintEnable = true;                 // In tất cả tin nhắn
    protected Socket socket;                                    // Tạo socket
    protected ObjectOutputStream output;                        // Dữ liệu Socket kiểu Object
    protected ObjectInputStream input;                              
    protected String ip;                                        // Địa chỉ IP
    protected int port;                                         // Port kết nối server
    protected boolean isObserver = false;                       // Kiểu Người xem
    protected MyChessmate game;
    List<Position> history_positionss = new ArrayList<>();  
    public ClientGame(String ip, int port)                      // Khởi tạo với IP và port
    {
        print("Client running ");
        this.ip = ip;
        this.port = port;
    }

    /* Method responsible for joining to the server on 
     * witch the game was created
     */
    public int join(int tableID, boolean asPlayer, String nick, String password) throws Error //join to server
    {
        int servCode = 0; //GET ERROR FROM SERVER
        print("running function: join(" + tableID + ", " + asPlayer + ", " + nick + ")");
        try
        {
            print("join to server: ip:" + getIp() + " port:" + getPort());
            this.setIsObserver(!asPlayer);
            try
            {
                // Mở Socket
                setSocket(new Socket(getIp(), getPort()));
                setOutput(new ObjectOutputStream(getSocket().getOutputStream()));
                setInput(new ObjectInputStream(getSocket().getInputStream()));
                
                //Gửi dữ liệu tới Server
                print("send to server: table ID");
                getOutput().writeInt(tableID);
                print("send to server: player or observer");
                getOutput().writeBoolean(asPlayer);
                print("send to server: player nick");
                getOutput().writeUTF(nick);
                print("send to server: password");
                getOutput().writeUTF(password);
                getOutput().flush();

                servCode = getInput().readInt(); //Thông số truyền từ server
                print("connection info: " + Connection_info.get(servCode).name());
                if (Connection_info.get(servCode).name().startsWith("err_"))
                {
                    throw new Error(Connection_info.get(servCode).name());
                }
                if (servCode == Connection_info.all_is_ok.getValue())
                {
                    Thread thread = new Thread(this);
                    thread.start();
                    return 0;    // có thể kết nối
                }
                else    //một số lỗi khác từ server
                {
                    return servCode;
                }
            }
            catch (Error | ConnectException err)
            {
                return servCode;
            }
        }
        catch (UnknownHostException ex)
        {
            return servCode;
        }
        catch (IOException ex)
        {
            return servCode;
        }
    }

    /* Method responsible for running of the game
     */
    @Override
    public void run()
    {
        print("123.running function: run()");
        boolean isOK = true;
        while (isOK)
        {
            try
            {
                String in = getInput().readUTF();
                print("input code: " + in);

                switch (in) {
                //getting new move from server
                    case "#move":
                        int beginX = getInput().readInt();
                        int beginY = getInput().readInt();
                        getGame().setLastMove(new Move(beginX, beginY));
                        getGame().setNewMove(new Move(beginX, beginY));
                        break;
                    case "#time":
                        int time = getInput().readInt();
                        System.out.println(time);
                        getGame().setTime(time);
                        break;
                    case "#timeOv":
                        int player = getInput().readInt();
                        int times = getInput().readInt();
                        getGame().setTimeOv(times,player);
                        break;
                //getting message from server
                    case "#message":
                        String str = getInput().readUTF();
//                        getGame().getChat().addMessage(str);
                        break;
                    case "#sendMessage":
                        String strs = getInput().readUTF();
                        getGame().addJTextFiled(strs);
                        break; 
                    case "#setEditable":
                        getGame().setEditable(true);
                        break; 
                    case "#StartGame":
                        getGame().startGame();
                        System.out.println("Here");
                        break;
                    case "#errorConnection":
                        break;
                    case "#massagePosition":
                        Position position= new Position();
                        position=getGame().getPosition();
                        List<Position> history_position=getGame().getHistory();
                        this.sendPositon(position,history_position);
                        break;
                    case "#position":
                        try {
                            print("OK");
                            Position positions= (Position) getInput().readObject();
                            List<Position> history_position2=(List<Position>) input.readObject();
                            getGame().setLocation(positions, history_position2);
                        } catch (ClassNotFoundException ex) {}
                        break;
                    case "#sendContinue":
                        getGame().ShowMess();
                        break;
                    case "#newGame":
                        getGame().newGame2();
                        break;
                    case "#noNewGame":
                        getGame().noNewGame();
                        break;
                    case "#messReplay":
                        String mess = getInput().readUTF();
                        getGame().messReplay(mess);
                        break;
                    case "#messReplay2":
                        String messs = getInput().readUTF();
                        getGame().messReplay2(messs);
                        break;
                    case "#Replay":
//                        getGame().viTri();
//                        getGame().viTri2(history_positionss);
                        getGame().Replay();
                        {
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        getGame().Replay();
                        break;
                    case "#Replay2":
                        getGame().Replay();
                        {
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        getGame().doiLuot();
                        break;
                    case "#noReplay":
                        String me = getInput().readUTF();
                        getGame().noReplay(me);
                        break;
                    case "#Play":
                        getGame().Play();
                        break;
                    case "#exit":
                        getGame().newGame3();
                        break;
                    default:
                        break;
                }
            }
            catch (IOException ex)
            {
                isOK = false;
                //getGame().getChat().addMessage("** "+Settings.lang("error_connecting_to_server")+" **");
            }
        }
    }
    
    /* Method responsible for sending the move witch was taken by a player
     */
    public void sendMove(int beginX, int beginY) //sending new move to server
    {
        print("running function: sendMove(" + beginX + ", " + beginY + ")");
        try
        {
            getOutput().writeUTF("#move");
            getOutput().writeInt(beginX);
            getOutput().writeInt(beginY);
            getOutput().flush();
        }
        catch (IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }
    }
     public void sendTime(int time) //sending new move to server
    {
        try
        {
            getOutput().writeUTF("#time");
            getOutput().writeInt(time);
            getOutput().flush();
        }
        catch (IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }
    }
    public void sendMassagePositon(){
        print("sendMassagePositon");
        try
        {   
            getOutput().writeUTF("#massagePosition");
            getOutput().flush();
        }
        catch (IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }
        
    }
    public void sendPositon(Position position,List<Position> positions){
        print("sendPositon");
//        for(int i=1;i<location2.length;i++)
//            System.out.print(location2[i]+" ");
//        System.out.println();
        try
        {   getOutput().reset();
            getOutput().writeUTF("#position");
            getOutput().writeObject(position);
            getOutput().writeObject(positions);
            getOutput().flush();
        }
        catch (IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }
        
    }
    public void sendUndoAnswerPositive()
    {
        try
        {
            getOutput().writeUTF("#undoAnswerPositive");
            getOutput().flush();
        }
        catch(IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }        
    }
    
    public void sendUndoAnswerNegative()
    {
        try
        {
            getOutput().writeUTF("#undoAnswerNegative");
            getOutput().flush();
        }
        catch(IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }        
    }    
    
    /* Method responsible for sending to the server informations about
     * moves of a player
     */
    public void sendMassage(String str) //sending new move to server
    {
        print("running function: sendMessage(" + str + ")");
        try
        {
            getOutput().writeUTF("#message");
            getOutput().writeUTF(str);
            getOutput().flush();
        }
        catch (IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }
    }
    public void sendMessageToOtherPlayer(String str)
    {
        print("running function: sendMessage(" + str + ")");
        try
        {
            getOutput().writeUTF("#sendMessage");
            getOutput().writeUTF(str);
            getOutput().flush();
        }
        catch (IOException ex)
        {
            //LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
        }
    }
    public void sendContinue() {
        try {
            getOutput().writeUTF("#sendContinue");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendNewGame(){
        try {
            getOutput().writeUTF("#newGame");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendNoNewGame(){
        try {
            getOutput().writeUTF("#noNewGame");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendExit(){
        try {
            getOutput().writeUTF("#exit");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendMessReplay(){
        try {
            getOutput().writeUTF("#messReplay");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendMessReplay2(){
        try {
            getOutput().writeUTF("#messReplay2");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendReplay(){
        try {
            getOutput().writeUTF("#Replay");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendReplay2(){
        try {
            getOutput().writeUTF("#Replay2");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendNoReplay(){
        try {
            getOutput().writeUTF("#noReplay");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendPlay(){
        try {
            getOutput().writeUTF("#Play");
            getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void print(String str){
        System.out.println(str);
    }
    /**
     * @return the game
     */
    public MyChessmate getGame()
    {
        return game;
    }

    /**
     * @param game the game to set
     */
    public void setGame(MyChessmate game)
    {
        this.game = game;
    }

    /**
     * return the socket
     * @return socket
     */
    public Socket getSocket()
    {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket)
    {
        this.socket = socket;
    }

    /**
     * @return the output
     */
    public ObjectOutputStream getOutput()
    {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(ObjectOutputStream output)
    {
        this.output = output;
    }

    /**
     * @return the input
     */
    public ObjectInputStream getInput()
    {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(ObjectInputStream input)
    {
        this.input = input;
    }

    /**
     * @return the ip
     */
    public String getIp()
    {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @return the isObserver
     */
    public boolean isIsObserver()
    {
        return isObserver;
    }

    /**
     * @param isObserver the isObserver to set
     */
    public void setIsObserver(boolean isObserver)
    {
        this.isObserver = isObserver;
    }
}
