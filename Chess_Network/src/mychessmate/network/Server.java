/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychessmate.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 
 */
public class Server implements Runnable
{
    //private static final Logger LOG = Logger.getLogger(Server.class);
    
    public static boolean isPrintEnable = true;     //In tất cả messages
    public static Map<Integer, Table> tables;       // Lưu lại và gửi nhận các client thông qua server
    public static int port = 3200;                  // port
    public static ServerSocket ss;                 // Server socket
    public static boolean isRunning = false;       // Kiểm tra server đang chạy
    private Table newtable;                         // Table lưu lại client
    public  static int existServer ;    
    private boolean myTurn = false;
    private boolean isWhiteplayer1 = false;         // Chọn player 1 có màu đen
    // Khởi tạo server mới
    public Server()
    {
        existServer = 0;
//        if (!Server.isRunning) //Chạy server nếu trước đó chưa chạy
//        {
            runServer();
            Thread thread = new Thread(this);
            thread.start();
            print("Server Started"); 
            Server.isRunning = true;        // Báo lại rằng server đã chạy
//        }
    }
    
    // Kiểm tra giá trị server đang chạy không
    public static boolean isRunning()
    {
        return isRunning;
    }
    
    // Chạy server
    private static void runServer()
    {
        try
        {
            ss = new ServerSocket(port);        // Mở server lắng nghe tại port 22222
            print("Server running");                   
        }
        catch (IOException ex)                  // Bắt lỗi nếu không mở được Server
        {
            print(ex.getMessage());
            existServer = 5;
        }
        tables = new HashMap<>();
    }

    // Lắng nghe kết nối từ client từ port 3200
    @Override
    public void run() 
    {
        print("Listening port: " + port);
        while (true)
        {
            Socket s;
            ObjectInputStream input;
            ObjectOutputStream output;

            try
            {
                s = ss.accept();
                input = new ObjectInputStream(s.getInputStream());
                output = new ObjectOutputStream(s.getOutputStream());

                print("New connection");

                //readed all data
                int tableID = input.readInt();
                print("readed table ID: " + tableID);
                boolean joinAsPlayer = input.readBoolean();
                print("readed joinAsPlayer: " + joinAsPlayer);
                String nick = input.readUTF();
                print("readed nick: " + nick);
                String password = input.readUTF();
                print("readed password: " + password);
                //---------------
                if (!tables.containsKey(tableID))
                {
                    print("bad table ID");
                    output.writeInt(Connection_info.err_bad_table_ID.getValue());
                    output.flush();
                    continue;
                }
                Table table = tables.get(tableID);

                if (!MD5.encrypt(table.password).equals(password))
                {
                    print("bad password: " + MD5.encrypt(table.password) + " != " + password);
                    output.writeInt(Connection_info.err_bad_password.getValue());
                    output.flush();
                    continue;
                }

                if (joinAsPlayer)
                {
                    print("join as player");
                    if (table.isAllPlayers())
                    {
                        print("error: was all players at this table");
                        output.writeInt(Connection_info.err_table_is_full.getValue());
                        output.flush();
                        continue;
                    }
                    else
                    {
                        print("wasn't all players at this table");

                        output.writeInt(Connection_info.all_is_ok.getValue());
                        output.flush();
                        table.addPlayer(new Client(s, input, output, nick, table));
                        table.sendMessageToAll("** Player "+ nick +" has joined the game **");

                        if (table.isAllPlayers())
                        {
                            table.sendStartGameToPlayer1();
                            table.sendToAllPlayer();
                            table.sendMessageToAll("** New game, it will start: " + table.getPlayerOne().nick + "**");
                        }
                        else
                        {
                            table.sendMessageToAll("** Waiting for another player **");
                        }
                    }
                }
                else //join as observer
                {

                    print("join as observer");
                    if (!table.canObserversJoin()||table.isAllPlayers()==false)
                    {
                        print("Observers can't join");
                        output.writeInt(Connection_info.err_game_without_observer.getValue());
                        output.flush();
                        continue;
                    }
                    else
                    {
                        output.writeInt(Connection_info.all_is_ok.getValue());
                        output.flush();

                        table.addObserver(new Client(s, input, output, nick, table));
                        table.sendToObserver();
                        if (table.getPlayerTwo() != null) //all players is playing
                        {
                            //table.sendSettingsAndMovesToNewObserver();
                        }

                        table.sendMessageToAll("** Observator "+ nick +" joined the game **");
                    }
                }
            }
            catch (IOException ex)
            {
                continue;
            }
        }
    }
    
    public static void print(String str)
    {
        if (isPrintEnable)
        {
            //LOG.debug("Server: " + str);
            System.out.print("Server: " + str + "\n");
        }
    }

    public void newTable(int idTable, String password, boolean withObserver, boolean enableChat) //create new table
    {

        print("create new table - id: " + idTable);
        tables.put(idTable, new Table(password, withObserver, enableChat));
    }
}