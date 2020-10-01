/**
 * 
 */
package mychessmate;

import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import mychessmate.network.Server;
import mychessmate.network.ClientGame;
import mychessmate.network.MD5;


public class MyChessmate extends JFrame implements MouseListener{

    Server server;
    String server_password;
    ClientGame client;
    String client_name;
    Position position;
//    Position position1;
    Position position1;
    ChessBoardPane board_pane;                                      // Bàn cờ chơi game
    HistoryBoardPane history_pane;                                  // Bàn cờ History
    JPanel east_pane,east_pane1,east_pane2;                                               // JPanel chứa button
    Resource resource = new Resource();                             // Lưu resource
    Map<Integer,Image> images = new HashMap<>();                    // Lưu danh sách image
    Map<Integer,Icon> icon_images = new HashMap<>();                // Tương tự lưu danh sách icon
    Move move = new Move();                                         // Nước đi chuyển hiện tại
    boolean piece_selected;                                         // Kiểm tra quân cờ được chọn
    boolean is_white;                                               // Chọn trắng hay đen
    int state,port=3200;                                                      // Lượt chơi của người nào
    MoveSearcher move_searcher;                                     // Tạo một bot tìm đường
    Game game;                                                      // Tính toán ván game về các nước đi
    JLabel new_game,quit,about,history,first,prev,next,last,send,group_chat;   // Nhãn button
    JTextArea text_area_send,text_area_seen,time;
    JTextPane text_pane;
    JPanel main_pane = new JPanel(new BorderLayout());
    JPanel main_pane2 = new JPanel(new BorderLayout());// Màn hình chính
    JPanel main_pane3 = new JPanel(new BorderLayout());
    PreferencesPane play_options;                                   // Màn hình chọn chế độ chơi
    boolean castling;                                               // Kiểm tra đã castling(nhập thành) chưa
    PromotionPane promotion_pane;                                   // Chọn Quân phong cho tốt
    List<Position> history_positions = new ArrayList<>();           // Lưu lịch sử di chuyển
    List<Position> history_positions1 = new ArrayList<>();
    int history_count;                                              // Đếm số lượng nước đi
    Color bg_color = Color.decode("#efd39c");                       // Màu nền chung cho chương trình (hơi nâu xám)
    boolean is_computer;                                            // Chế độ chơi với máy
    boolean is_server;
    boolean is_obsever;
    JButton replay;
    int x=0,z=0,a=0;
    boolean kttime=false;
    
    public MyChessmate(){
        super("MyChessmate "+GameData.VERSION);                     // Gọi hàm khởi tạo parent (JFrame) với tên kèm version
        setContentPane(main_pane);                                  //
        position = new Position();                                  // Vị trí bàn cờ được tạo.
        promotion_pane = new PromotionPane(this);                   // Tạo promotion sẵn trước trên nền của mychessmate
        
        
        //loadMenuIcons();                                            // Load Icon và hình ảnh từ cấu trúc resource
        loadImages(GameData.LOADICON, 0, false);
        //loadBoardImages();
        loadImages(GameData.LOADBOARD, 0, false);
        
        board_pane = new ChessBoardPane();                          // Khởi tạo bàn cờ  
        main_pane.add(createMenuPane(),BorderLayout.WEST);          // Tạo menu button nằm bên trái màn hình
        main_pane3.add(board_pane,BorderLayout.CENTER);              // Tạo bàn cờ nằm giữa
        main_pane3.add(createTime(),BorderLayout.NORTH);
        main_pane.add(main_pane3,BorderLayout.CENTER);  
        main_pane.setBackground(bg_color);                          // Set màu nền
        main_pane2.add(createSend(),BorderLayout.CENTER);
        main_pane2.add(createEastPane(),BorderLayout.EAST);
        main_pane.add(main_pane2,BorderLayout.EAST);
        
        pack();
        Dimension size = getSize();                                 // Cài đặt lại chiều cao của trò chơi                            
        size.height = 623;
        setSize(size);
        addWindowListener(new WindowAdapter(){                      // Sự kiện Tắt của sổ
            @Override
            public void windowClosing(WindowEvent e){
                quit();
            }
        });
    }           
    
    /**
     * 
     * @return Position for observers
     */
    public Position getPosition(){
        for(int i=1;i<position.human_pieces.length;i++){
            if(position.human_pieces[i]!=null)
                System.out.print(position.human_pieces[i].location+" ");
        }
        System.out.println();
        for(int i=1;i<position.computer_pieces.length;i++){
            if(position.computer_pieces[i]!=null)
                System.out.print(position.computer_pieces[i].location+" ");
        }
        return this.position;
    }
    public void addJTextFiled(String s){
        text_area_seen.append(s+"\n");
    }
    public void setLocation(Position position,List<Position> history_position) {
        this.position1=position;
        this.history_positions1=history_position;
    }
    public List<Position> getHistory(){
        return history_positions;
    }
//    public void getPosition1(Position position) {
//        client.sendPositon(position);
//    }
    /**
     * Start when server call.
     */
    public void startGame(){
        kttime=true;
        state = GameData.HUMAN_MOVE;
        play_options.continue_button.setEnabled(true);
        replay.setEnabled(true);
    }
    public void setEditable(boolean set){
        text_area_send.setEditable(set);
    }
    /**
     * 
     * @param move Animation with new move from other player sended
     */
    public void setNewMove(Move move){
        this.move = move; 
        state = GameData.PREPARE_ANIMATION;
        
    }
    public void setTime(int times){
        if(is_server){
                z+=times;
        }else{
                x+=times;
        } 
        if(!is_obsever){
            kttime=true;
            a=0; 
        }else{
            if(state==GameData.HUMAN_MOVE){
                z+=times;
                state=GameData.HUMAN2_MOVE;
            }
            else if(state==GameData.HUMAN2_MOVE){
                x+=times;
                state=GameData.HUMAN_MOVE;
            }
            time.setText("Player 1: "+x+"s\nPlayer 2: "+z+"s");
        }
    }
    public void setTimeOv(int times,int player){
            if(player==1){
                x+=times;
            }
            else if(player==2){
                z+=times;
            }
            time.setText("Player 1: "+x+"s\nPlayer 2: "+z+"s");
    }
    public void Continue() {
        client.sendContinue();
    }
    
    public void ShowMess(){
        int k=JOptionPane.showConfirmDialog(null, "Player 1 đề nghị bạn chơi ván mới", "New Game", JOptionPane.YES_NO_OPTION);
        if(k==0){
            client.sendNewGame();
        }else{
            client.sendNoNewGame();
        }
    }
    public void messReplay(String str){
        int k=JOptionPane.showConfirmDialog(null,  str+" "+"muốn đi lại", "Replay", JOptionPane.YES_NO_OPTION);
        if(k==0){
            client.sendReplay();
        }
        if(k==1){
            client.sendNoReplay();
            state=GameData.WAITING;
        }
    }
    public void messReplay2(String str){
        int k=JOptionPane.showConfirmDialog(null,  str+" "+"muốn đi lại", "Replay", JOptionPane.YES_NO_OPTION);
        if(k==0){
            client.sendReplay2();
        }
        if(k==1){
            client.sendNoReplay();
            state=GameData.WAITING;
        }
    }
    public void noReplay(String str){
        JOptionPane.showMessageDialog(null, str+" Ko muốn cho đi lại");
        if(state==GameData.HUMAN2_MOVE){
            client.sendPlay();
        }
    }
    public void Play(){
        state=GameData.HUMAN_MOVE;
    }
    public void Replay(){
                if(history_positions.get(history_positions.size()-2).pice_source_location!=0){
                    if(history_positions.get(history_positions.size()-2).move_source_location==1){
                            position.human_pieces[history_positions.get(history_positions.size()-2).number_source_location]=
                                new Piece(history_positions.get(history_positions.size()-2).pice_source_location,history_positions.get(history_positions.size()-2).last_move.source_location);
                        position.board[history_positions.get(history_positions.size()-2).last_move.source_location]=history_positions.get(history_positions.size()-2).number_source_location;
                        position.board[history_positions.get(history_positions.size()-2).last_move.destination]=GameData.EMPTY;        
                    }
                    if(history_positions.get(history_positions.size()-2).move_source_location==0){
                        position.computer_pieces[history_positions.get(history_positions.size()-2).number_source_location]=
                                new Piece(history_positions.get(history_positions.size()-2).pice_source_location,history_positions.get(history_positions.size()-2).last_move.source_location);
                        position.board[history_positions.get(history_positions.size()-2).last_move.source_location]=-history_positions.get(history_positions.size()-2).number_source_location;
                        position.board[history_positions.get(history_positions.size()-2).last_move.destination]=GameData.EMPTY;
                    }
                }
                if(history_positions.get(history_positions.size()-2).pice_destination!=0){
                    if(history_positions.get(history_positions.size()-2).move_destination==1){
                            position.human_pieces[history_positions.get(history_positions.size()-2).number_destination]=
                                new Piece(history_positions.get(history_positions.size()-2).pice_destination,history_positions.get(history_positions.size()-2).last_move.destination);
                        position.board[history_positions.get(history_positions.size()-2).last_move.destination]=history_positions.get(history_positions.size()-2).number_destination;      
                    }
                    if(history_positions.get(history_positions.size()-2).move_destination==0){
                        position.computer_pieces[history_positions.get(history_positions.size()-2).number_destination]=
                                new Piece(history_positions.get(history_positions.size()-2).pice_destination,history_positions.get(history_positions.size()-2).pice_destination);
                        position.board[history_positions.get(history_positions.size()-2).last_move.destination]=-history_positions.get(history_positions.size()-2).number_destination;
                    }
                }
                board_pane.repaint();
                history_positions.remove(history_positions.get(history_positions.size()-1));
                history_count=history_positions.size()-1;
                history_pane.repaint();
    }
    public void doiLuot(){
        if(state==GameData.HUMAN2_MOVE){
            System.out.println("HUMAN2_MOVE");
            state = GameData.HUMAN_MOVE;
            kttime=true;
            a=0;
        }
        else if(state==GameData.HUMAN_MOVE){
            System.out.println("HUMAN_MOVE");
            state = GameData.HUMAN2_MOVE;
            kttime=false;
            client.sendTime(a);
            if(is_server)
                x+=a;
            else if(play_options.client_button.isSelected())
                z+=a;
            time.setText("Player 1: "+x+"s\nPlayer 2: "+z+"s");
        }
    }
    
    public void noNewGame(){
        JOptionPane.showMessageDialog(null, "Player 2 ko muốn chơi lại");
    }
    public void setLastMove(Move move){
        for(int i=1; i<position.human_pieces.length; i++)
                            if(position.human_pieces[i]!=null){
                                if(position.human_pieces[i].location==move.source_location){
                                    history_positions.get(history_positions.size()-1).setPice_source_location(position.human_pieces[i].value);
                                    history_positions.get(history_positions.size()-1).setMove_source_location(1);
                                    history_positions.get(history_positions.size()-1).setNumber_source_location(i);
                                    break;
                                }
                            }
                        for(int i=1; i<position.computer_pieces.length; i++)
                            if(position.computer_pieces[i]!=null){
                                if(position.computer_pieces[i].location==move.source_location){
                                    history_positions.get(history_positions.size()-1).setPice_source_location(position.computer_pieces[i].value);
                                    history_positions.get(history_positions.size()-1).setMove_source_location(0);
                                    history_positions.get(history_positions.size()-1).setNumber_source_location(i);
                                    break;
                                }
                            }
        for(int i=1; i<position.human_pieces.length; i++)
                            if(position.human_pieces[i]!=null){
                                if(position.human_pieces[i].location==move.destination){
                                     history_positions.get(history_positions.size()-1).setPice_destination(position.human_pieces[i].value);
                                     history_positions.get(history_positions.size()-1).setMove_destination(1);
                                     history_positions.get(history_positions.size()-1).setNumber_destination(i);
                                    break;
                                }
                            }
                        for(int i=1; i<position.computer_pieces.length; i++)
                            if(position.computer_pieces[i]!=null){
                                if(position.computer_pieces[i].location==move.destination){
                                    history_positions.get(history_positions.size()-1).setPice_destination(position.computer_pieces[i].value);
                                    history_positions.get(history_positions.size()-1).setMove_destination(0);
                                    history_positions.get(history_positions.size()-1).setNumber_destination(i);
                                    break;
                                }
                            }
        history_positions.get(history_positions.size()-1).setLastMove(move);
    }
    public void time(){
        Thread t;
        t = new Thread(){    // Tạo thread lượt di chuyển
            @Override
            public void run(){          // Chạy
                while(true){
                    if(kttime==true){
                        a++;
                        time.setText("Player 1: "+x+"s\nPlayer 2: "+z+"s\nTime: "+a);
                            try {
                                    Thread.sleep(997);
                            } catch (InterruptedException e) {}
                    }
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {}
                }
            }
        };
        t.start();
    }
    /**
     * Khởi tạo Panel riêng chứa các label menu với các icon làm nền cho label
     * Gán nền cho label bằng hình ảnh thể hiện cho button, 
     * Gán màu nền cho panel chứa các label
     * Gán sự kiện cho các label để có thể ấn vào chọn, hoặc houver qua
     * Cho thêm vào Panel Menu
     * @return Menu panel
    */ 
    public JPanel createMenuPane(){
        //Khởi tạo Label với từng icon nền tương ứng (ID icon load từ resource)
        new_game = new JLabel(icon_images.get(GameData.NEW_BUTTON));
        group_chat = new JLabel(icon_images.get(GameData.GROUP_CHAT_BUTTON));
        about = new JLabel(icon_images.get(GameData.ABOUT_BUTTON));
        history = new JLabel(icon_images.get(GameData.HISTORY_BUTTON));
        quit = new JLabel(icon_images.get(GameData.QUIT_BUTTON)); 
        
        // Thêm sự kiện chuột vào từng label
        new_game.addMouseListener(this);
        group_chat.addMouseListener(this);
        about.addMouseListener(this);
        history.addMouseListener(this);
        quit.addMouseListener(this);

        JPanel pane = new JPanel(new GridLayout(5,1));
        pane.add(new_game);
        pane.add(group_chat);        
        pane.add(history);
        pane.add(about);
        pane.add(quit);             
        pane.setBackground(bg_color);
        
        JPanel menu_pane = new JPanel(new BorderLayout());
        menu_pane.add(pane,BorderLayout.SOUTH);
        menu_pane.setBorder(BorderFactory.createEmptyBorder(0,20,20,0));
        menu_pane.setBackground(bg_color);
        return menu_pane;
    }

    public JPanel createSend(){
        east_pane1 = new JPanel();
        text_area_send=new JTextArea(2,18);
        text_area_seen=new JTextArea(22,1);
        text_area_seen.setEditable(false);
        text_area_send.setEditable(false);
        text_pane=new JTextPane();
        send=new JLabel(icon_images.get(GameData.SEND_BUTTON));
        
        JPanel pane1 = new JPanel();
        pane1.add(text_area_send);
        pane1.setBackground(bg_color);
//        pane1.setBorder(BorderFactory.createEmptyBorder(1,0,0,0));
        JPanel pane2 = new JPanel();
        pane2.add(send);
        pane2.setBackground(bg_color);
        
        JPanel pane3 = new JPanel();
        pane3.setLayout(new BoxLayout(pane3,BoxLayout.X_AXIS));
        pane3.add(pane1);
        pane3.add(pane2);
        pane3.setBackground(bg_color);
        pane3.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
//        text_area.setEditable(false);
        JPanel pane4 = new JPanel();
        pane4.setLayout(new BoxLayout(pane4,BoxLayout.Y_AXIS));
//        pane4.add(pane_massager);
        pane4.add(text_area_seen,BorderLayout.CENTER);
        pane4.add(pane3,BorderLayout.SOUTH);
        pane4.setBorder(BorderFactory.createEmptyBorder(30,0,0,0));
        pane4.setBackground(bg_color); 
        
        east_pane1.add(pane4,BorderLayout.SOUTH);                        
        east_pane1.setBorder(BorderFactory.createEmptyBorder(110,0,20,20));
        east_pane1.setBackground(bg_color);                                                     // Thêm sự kiện chuột vào label
        east_pane1.setVisible(false);
        send.addMouseListener(this);
        return east_pane1;
    }
    public JPanel createTime(){
        east_pane2 = new JPanel();
        time=new JTextArea(5, 35);
        time.setText("Player 1: "+x+"s\nPlayer 2: "+z+"s");
        time.setEditable(false);
        east_pane2.add(time);
        east_pane2.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
//        east_pane2.setVisible(false);
        east_pane2.setBackground(bg_color);                                                     // Thêm sự kiện chuột vào label
        return east_pane2;
    }
    /** Khởi tạo EastPane
     *  Tạo Pane phụ chứa các label thể hiện cho button, 
     *  Gắn hình ảnh tương ứng, Gắn sự kiện lắng nghe, Thêm Pane này vào phía dưới của EastPane
     *  Tạo Pane phụ chứa bàn cờ, thêm hình ảnh gắn vào EastPane
     *  Gắn EastPane vào bên phải của main_pane
     */
    public JPanel createEastPane(){
        east_pane = new JPanel(new BorderLayout());                 // Tạo JPanel để chưa history pane
        history_pane = new HistoryBoardPane();                      // Tạo Panel history
        JPanel pane = new JPanel(new GridLayout(1,4));              // Tạo danh sách nút bấm xem history
        first = new JLabel(icon_images.get(GameData.FIRST_BUTTON));
        prev = new JLabel(icon_images.get(GameData.PREV_BUTTON));
        next = new JLabel(icon_images.get(GameData.NEXT_BUTTON));
        last = new JLabel(icon_images.get(GameData.LAST_BUTTON));
                                                                    // Thêm chúng vào Panel chứ label
        pane.add(first);
        pane.add(prev);
        pane.add(next);
        pane.add(last);
        JPanel pane3 = new JPanel();
        replay=new JButton("Replay");
        replay.setEnabled(false);
        pane3.add(replay);
        pane3.setBackground(bg_color);
        pane3.setBorder(BorderFactory.createEmptyBorder(0,0,0,200));
        
        JPanel pane2 = new JPanel();                                // Thêm History vào Panel chứa phía trên
        pane2.setLayout(new BoxLayout(pane2,BoxLayout.Y_AXIS));
        pane2.add(pane3);
        pane2.add(history_pane);
        pane2.add(pane); 
        
        east_pane.add(pane2,BorderLayout.SOUTH);                        
        east_pane.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        east_pane.setBackground(bg_color);
        east_pane.setVisible(false);
//        main_pane.add(east_pane,BorderLayout.EAST);                 // Thên history vào bên phải của trò chơi
        
        pane.setBorder(BorderFactory.createEmptyBorder(0,14,0,14)); // Set khoảng cách và màu nền
        pane.setBackground(bg_color);
                                                                    // Thêm sự kiện chuột vào label
        first.addMouseListener(this);
        prev.addMouseListener(this);
        next.addMouseListener(this);
        last.addMouseListener(this);
        replay.addMouseListener(this);
        return east_pane;
    }    
    
    /** Tạo mới ván chơi
     * Hiển thị màn hình lịch sử (history_pane), xóa lịch sử, tạo lịch sử mới
     * Lấy hình ảnh quân cờ từ resource thông qua *.properties
     * Tạo mới tất cả (move, position, game, board_pane...)
     * Gắn hình vào postition tương ứng trên bàn cờ tùy theo is_while
     * Chuyển sang chế độ thread quản lý trò chơi
     */
    int errCode;
    public void newGame(){   
        errCode = 0;    
        is_white = play_options.white_button.isSelected();      // Kiểm tra người chọn quân trắng hay đen nhờ vào options
        is_computer = play_options.computer_button.isSelected(); // Kiểm tra người chơi chọn chế độ computer hay onlines
        play_options.continue_button.setEnabled(false);
        replay.setEnabled(false);
        if(is_white) state = GameData.HUMAN_MOVE;               // Lấy trạng thái là lượt của Human hay computer tùy màu
        else {
            if(is_computer){
                state = GameData.COMPUTER_MOVE;
            }
            else{
                state = GameData.HUMAN2_MOVE;
            }
        }        
        if(is_computer == false){
            is_server = play_options.server_button.isSelected();
            if(is_server){
                try{
                    server.port=port;
                    if(server.isRunning==true)
                        server.ss.close();
                    server = new Server();
                    errCode = Server.existServer;
                    server.newTable(port, server_password, true, true);
                    client = new ClientGame("localhost", port);
                    client.join(port, true, client_name, MD5.encrypt(server_password));
                    client.setGame(this);
                    state = GameData.WAITING;
                }   
                catch(Exception ex){
                    errCode = 5;
                }  
            }
            else{
                client = new ClientGame("localhost",port);
                if(play_options.client_button.isSelected()){   
                    errCode = client.join(port, true, client_name, MD5.encrypt(server_password));
                    replay.setEnabled(true);
                }
                else{
                    errCode =client.join(port, false, client_name, MD5.encrypt(server_password));
                    client.sendMassagePositon();
                }
                client.setGame(this);
            }
        }
        if (errCode == 0) {
            if (!east_pane.isVisible()) {                             // Kiểm tra và set hiển thị màn hình lịch sử
                east_pane.setVisible(true);
                pack();
                setLocationRelativeTo(null);
            }
            move.source_location = -1;                              // Set tọa độ di chuyển đầu tiên là -1 -1
            move.destination = -1;
            position = new Position();                              // Lên danh sách vị trí mới cho game
            is_obsever=play_options.observer_button.isSelected();
            if(!is_obsever){
                position.initialize(is_white);                          // với màu tương ứng xoay vị trí bàn cờ và quân cua Hậu
                game = new Game(position);                              // Khởi tạo game, với vị các quân và 2 quân vua
            }
            else{
                while(position1==null||history_positions1==null){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MyChessmate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                position.newPosition(position1.human_pieces,position1.computer_pieces);
                game = new Game(position);                       // Khởi tạo game, với vị các quân và 2 quân vua
            }
                                     
            loadImages(GameData.LOADPIECE, 0, false);               // Load hình ảnh của quân cờ theo vị trí lên bàn cờ
            promotion_pane.setIcons(is_white);                      // Set màu của quân được phong trùng với người chơi chọn
            board_pane.repaint();                                   // Load lại bàn cờ để hiển thị hình ảnh
            castling = false;                                       // Khởi tạo chưa nhập thành
            history_positions.clear();                              // Xóa sạch lịch sử, length và tạo mới
            history_count = 0;                                      // 
            newHistoryPosition();
            if(is_obsever){
                history_positions=history_positions1;
                history_count = history_positions.size()-1;
            }
            move_searcher.level = play_options.levelSlider.getValue(); // Level tìm sẽ được gán
            /*Some test*/
            //move = move_searcher.alphaBeta(GameData.COMPUTER, position,Integer.MIN_VALUE, Integer.MAX_VALUE, play_options.levelSlider.getValue()).last_move;    
            //setNewMove(move);
            play();
            if(!is_obsever)
                time();
        }
        else{
            String msg = "";
            switch(errCode){
                case 1:
                    msg = "ID kết nối bảng không tồn tại!";
                    break;
                case 2: 
                    msg = "Đã tối đa người chơi trong phòng!";
                    break;
                case 3:
                    msg = "Chưa đủ người chơi không cho phép người xem";
                    break;
                case 4:
                    msg = "Password bạn nhập sai rồi!";
                    break; 
                case 5:
                    msg = "Server port localhost đã được tạo.";
                    break;
            }
             JOptionPane.showMessageDialog(new JFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }   
    
    public void newGame2(){
        x=0;
        z=0;
        a=0;
        time.setText("Player 1: "+x+"s\nPlayer 2: "+z+"s");
        is_white = play_options.white_button.isSelected();      // Kiểm tra người chọn quân trắng hay đen nhờ vào options
        is_computer = play_options.computer_button.isSelected(); // Kiểm tra người chơi chọn chế độ computer hay onlines
        play_options.continue_button.setEnabled(true);
        if(is_white) state = GameData.HUMAN_MOVE;               // Lấy trạng thái là lượt của Human hay computer tùy màu
        else {
            if(is_computer){
                state = GameData.COMPUTER_MOVE;
            }
            else{
                state = GameData.HUMAN2_MOVE;
            }
        }
            move.source_location = -1;                              // Set tọa độ di chuyển đầu tiên là -1 -1
            move.destination = -1;
            position = new Position();                              // Lên danh sách vị trí mới cho game
            position.initialize(is_white);                          // với màu tương ứng xoay vị trí bàn cờ và quân cua Hậu
            game = new Game(position);                              // Khởi tạo game, với vị các quân và 2 quân vua                   
            loadImages(GameData.LOADPIECE, 0, false);               // Load hình ảnh của quân cờ theo vị trí lên bàn cờ
            promotion_pane.setIcons(is_white);                      // Set màu của quân được phong trùng với người chơi chọn
            board_pane.repaint();                                   // Load lại bàn cờ để hiển thị hình ảnh
            castling = false;                                       // Khởi tạo chưa nhập thành
            history_positions.clear();                              // Xóa sạch lịch sử, length và tạo mới
            history_count = 0;                                      // 
            newHistoryPosition();
            /*Some test*/
            //move = move_searcher.alphaBeta(GameData.COMPUTER, position,Integer.MIN_VALUE, Integer.MAX_VALUE, play_options.levelSlider.getValue()).last_move;    
            //setNewMove(move);
//            play();
    }
    public void newGame3(){
        x=0;
        z=0;
        a=0;
        time.setText("Player 1: "+x+"s\nPlayer 2: "+z+"s");
        if(!is_obsever)
            JOptionPane.showMessageDialog(null, "Player 2 đã thoát khởi tạo lại bàn cờ");
        text_area_send.setEditable(false);
        text_area_seen.setText("");
        replay.setEnabled(false);
        is_white = play_options.white_button.isSelected();      // Kiểm tra người chọn quân trắng hay đen nhờ vào options
        is_computer = play_options.computer_button.isSelected(); // Kiểm tra người chơi chọn chế độ computer hay onlines
        play_options.continue_button.setEnabled(false);
        if(is_white) state = GameData.HUMAN_MOVE;               // Lấy trạng thái là lượt của Human hay computer tùy màu
        else {
            if(is_computer){
                state = GameData.COMPUTER_MOVE;
            }
            else{
                state = GameData.HUMAN2_MOVE;
            }
        }
        state = GameData.WAITING;
            move.source_location = -1;                              // Set tọa độ di chuyển đầu tiên là -1 -1
            move.destination = -1;
            position = new Position();                              // Lên danh sách vị trí mới cho game
            is_obsever=play_options.observer_button.isSelected();
            position.initialize(is_white);                          // với màu tương ứng xoay vị trí bàn cờ và quân cua Hậu
            game = new Game(position);                              // Khởi tạo game, với vị các quân và 2 quân vua                   
            loadImages(GameData.LOADPIECE, 0, false);               // Load hình ảnh của quân cờ theo vị trí lên bàn cờ
            promotion_pane.setIcons(is_white);                      // Set màu của quân được phong trùng với người chơi chọn
            board_pane.repaint();                                   // Load lại bàn cờ để hiển thị hình ảnh
            castling = false;                                       // Khởi tạo chưa nhập thành
            history_positions.clear();                              // Xóa sạch lịch sử, length và tạo mới
            history_count = 0;                                      // 
            newHistoryPosition();
            move_searcher.level = play_options.levelSlider.getValue(); // Level tìm sẽ được gán
            /*Some test*/
            //move = move_searcher.alphaBeta(GameData.COMPUTER, position,Integer.MIN_VALUE, Integer.MAX_VALUE, play_options.levelSlider.getValue()).last_move;    
            //setNewMove(move);
//            play();
    }
    public int geterrCode(){
        return errCode;
    }
    /** Quản lý các luồng chơi, điều khiển computer
     * Lấy các vị trị tốt nhất trong lần đi tiếp theo của computer
     * Sử lý hoạt ảnh -> chuyển động di chuyển qua từng ô
     * Kiểm tra trạng thái game (phe nào thắng hay tiếp tục)
     */
    public void play(){
        Thread t;
        t = new Thread(){    // Tạo thread lượt di chuyển
            @Override
            public void run(){          // Chạy
                while(true){            // lặp đến kết thúc ván cờ                
                    switch(state){                                  // bắt trạng thái di chuyển
                        case GameData.HUMAN_MOVE:                   // Nếu Người chơi thì bỏ qua
                            if(!is_computer){
                            if(gameEnded(GameData.COMPUTER)){       // Nếu lượt computer đi thắng, thì kết thúc game                        
                                state = GameData.GAME_ENDED;
                                break;
                            }
                            }
                            break;
                        case GameData.COMPUTER_MOVE:                // Nếu máy chơi thì bắt đầu tính và di chuyển.
                            System.out.print("Computer move\n");
                            if(gameEnded(GameData.COMPUTER)){       // Nếu lượt computer đi thắng, thì kết thúc game                        
                                state = GameData.GAME_ENDED;
                                break;
                            }
                            // Lấy nước đi cuối trong sự thay đổi vị trí bàn cờ
                            move = move_searcher.alphaBeta(GameData.COMPUTER, position, 
                                    Integer.MIN_VALUE, Integer.MAX_VALUE, play_options.levelSlider.getValue()).last_move;    //
                            state = GameData.PREPARE_ANIMATION;    // chuyển sang trạng thái re lại bàn cờ                           
                            break;
                        case GameData.HUMAN2_MOVE:
                            break;
                        case GameData.PREPARE_ANIMATION:
                            prepareAnimation();
                            break;
                        case GameData.ANIMATING:
                            animate();
                            break;                        
                        case GameData.GAME_ENDED: return;
                    }
                    try{                        
                        Thread.sleep(3);
                    }catch(InterruptedException e){
                    }
                }
            }
        };
        t.start();
    }
    
    // Kiểm tra đã kết thúc game hay chưa
    public boolean gameEnded(int player){
        int result = game.getResult(player);                        // Lấy xem trong nước đi tiếp theo còn bị chiếu không
        boolean end_game = false;
        String color ="";
        if(player == GameData.COMPUTER){                            // Nếu lượt chơi của computer thì lấy màu tùy theo 
            color = (is_white)?"White":"Black";
        }else color = (is_white)?"Black":"White";
        //Xét trường hợp thắng thua khi chơi online
        if(!is_computer){
            if(play_options.client_button.isSelected()){
                color = "Black";
            }
            else{
                color = "While";
            }
        }
        if(result == GameData.CHECKMATE){                           // Nếu trong tất cả nước đi tiếp theo vẫn bị chiếu thì end game
            showEndGameResult(color+" wins by CHECKMATE");
            end_game = true;
        }else if(result == GameData.DRAW){                          // trận đấu hòa (chưa bắt được
            showEndGameResult("DRAW");
            end_game = true;
        }
        return end_game;
    }
    
    // Hiển thị thông báo kết thúc game và hiển thị làm màn hình chọn new game
    public void showEndGameResult(String message){              
        int option = JOptionPane.showOptionDialog(null,
                message,"Game Over",0,JOptionPane.PLAIN_MESSAGE,
                null,new Object[]{"Play again","Cancel"},"Play again");
        if(option == 0){
            play_options.setVisible(true);
        }
    }
    
    // Warning() the computer's state
    public void showNewGameWarning(){
        JOptionPane.showMessageDialog(null,
                "Start a new game after I made my move.\n",
                "Message",JOptionPane.PLAIN_MESSAGE);
    }
    /** Click menu button để thực hiện các thao tác trên menu panel (Jlabel)
     * Xử lý luồn click chuột vào vị trí exit -> thoát
     * Xử xý chuột ấn vào các lable menu
        * Xử lý chuột ấn vào vị trí xem history, prev, first, last, next
     * @param e : sự kiện mouse click
     */
    
    @Override
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        if(source == quit){                         // nếu bám nút thoát, thì thoát
            quit();
        }else if(source == new_game){               // nếu bấm vào label các kiểu thì làm theo
            if(state == GameData.COMPUTER_MOVE){
                showNewGameWarning();
                return;
            }
            else{
                if((!is_computer)&&(false)){                   //****************Bắt trường hợp chịu thua ****************///
                    showNewGameWarning();
                    return;
                }
            }
            if(play_options == null) {                          //Chọn cách chơi
                play_options = new PreferencesPane(this);
                move_searcher = new MoveSearcher(this);
            }
            play_options.setVisible(true);  
        }else if(source == about){                              // Xem về chúng tôi
            AboutPane.createAndShowUI();
        }else if(source == history){                            // Tắt và hiển thị lịch sử
            east_pane.setVisible(!east_pane.isVisible());
            pack();
            setLocationRelativeTo(null);
        }else if(source == group_chat){                            // Tắt và hiển thị lịch sử
            east_pane1.setVisible(!east_pane1.isVisible());
            pack();
            setLocationRelativeTo(null);
        }else if(source == first){                              // trả về hiển thị history đầu tiên, sau, trước, cuối cùng
            history_count = 0;
            history_pane.repaint();            
        }else if(source == prev){
            if(history_count>0){
                history_count--;
                history_pane.repaint();
            }
        }else if(source == next){
            if(history_count<history_positions.size()-1){
                history_count++;
                history_pane.repaint();
            }
        }else if(source == last){
            history_count = history_positions.size()-1;
            history_pane.repaint();
        }else if(source ==send){
            String str_massage=text_area_send.getText().trim();
            if(str_massage.length()!=0){
                text_area_seen.append(client_name+": "+str_massage+"\n");
                text_area_send.setText("");
                client.sendMessageToOtherPlayer(str_massage); 
            }  
        }else if(source == replay){
            if(state==GameData.HUMAN_MOVE){
                if(history_positions.size()>2)
                    client.sendMessReplay();
            }
            else if(state==GameData.HUMAN2_MOVE){
                if(history_positions.size()>=2)
                    client.sendMessReplay2();
            }
        }
        
    }    

    // Sự kiện mouse houver, chuột di chuyển qua label thì đổi nền tương ứng
    @Override
    public void mouseEntered(MouseEvent e) {
        Object source = e.getSource();
        if(source == send){
            send.setIcon(icon_images.get(GameData.SEND_BUTTON2));
        }else if(source == new_game){
            new_game.setIcon(icon_images.get(GameData.NEW_BUTTON2));
        }else if(source == group_chat){
            group_chat.setIcon(icon_images.get(GameData.GROUP_CHAT_BUTTON2));
        }else if(source == about){
            about.setIcon(icon_images.get(GameData.ABOUT_BUTTON2));
        }else if(source == history){
            history.setIcon(icon_images.get(GameData.HISTORY_BUTTON2));
        }else if(source == quit){
            quit.setIcon(icon_images.get(GameData.QUIT_BUTTON2));
        }else if(source == first){
            first.setIcon(icon_images.get(GameData.FIRST_BUTTON2));
        }else if(source == prev){
            prev.setIcon(icon_images.get(GameData.PREV_BUTTON2));
        }else if(source == next){
            next.setIcon(icon_images.get(GameData.NEXT_BUTTON2));
        }else if(source == last){
            last.setIcon(icon_images.get(GameData.LAST_BUTTON2));
        }
    }

    // Nếu mouse di chuyển khỏi label thì đặt lại hình bình thường
    @Override
    public void mouseExited(MouseEvent e) {
        Object source = e.getSource();
        if(source == send){
            send.setIcon(icon_images.get(GameData.SEND_BUTTON));
        }else if(source == new_game){
            new_game.setIcon(icon_images.get(GameData.NEW_BUTTON));
        }else if(source == group_chat){
            group_chat.setIcon(icon_images.get(GameData.GROUP_CHAT_BUTTON));
        }else if(source == about){
            about.setIcon(icon_images.get(GameData.ABOUT_BUTTON));
        }else if(source == history){
            history.setIcon(icon_images.get(GameData.HISTORY_BUTTON));
        }else if(source == quit){
            quit.setIcon(icon_images.get(GameData.QUIT_BUTTON));
        }else if(source == first){
            first.setIcon(icon_images.get(GameData.FIRST_BUTTON));
        }else if(source == prev){
            prev.setIcon(icon_images.get(GameData.PREV_BUTTON));
        }else if(source == next){
            next.setIcon(icon_images.get(GameData.NEXT_BUTTON));
        }else if(source == last){
            last.setIcon(icon_images.get(GameData.LAST_BUTTON));
        }
    }
    
    // Sự kiện nhấn chuột xuống
    @Override
    public void mousePressed(MouseEvent e) { }

    // Sự kiện chuột thả
    @Override
    public void mouseReleased(MouseEvent e) { }

    
    /** Khởi tạo bàn cờ với vị trí các nước đi
     * Set màu sắc, kích thước cho panel Board
     * Xóa ảnh của đi, thay bằng ảnh mới của paintComponent()
     * Hiển thị viền sáng những ô có con vừa đi ở nước di chuyển
     * Mỗi nước đi sẽ vẽ lại bàn cờ để hình ảnh mượt hơn
     */
    public class ChessBoardPane extends JPanel implements MouseListener{     
        Image animating_image;
        int movingX,movingY,desX,desY,deltaX,deltaY;
        public ChessBoardPane(){
            setPreferredSize(new Dimension(450,495));
            setBackground(bg_color);
            addMouseListener(this);
        }
        //vẽ màn hình bàn cờ
        @Override
        public void paintComponent(Graphics g){
            if(position.board == null) return; // Nếu bàn cờ chưa có gì thì thoát
            super.paintComponent(g);            // gọi paitn cha, để xóa hết hình ảnh trước đó
            g.drawImage(images.get(GameData.MYCHESSMATE),20,36,this);   // Vẽ chữ cho game và hình bàn cờ 
            g.drawImage(images.get(GameData.BOARD_IMAGE),20,65,this);       
            for (int i = 0; i < position.board.length-11; i++) {        // Hiển thị dấu vết di chuyển
                if (position.board[i] == GameData.ILLEGAL) continue;    // Không thuộc bàn cờ bỏ qua                                                             
                int x = i%10;
                int y = (i-x)/10;
                
                if (piece_selected && i == move.source_location) {                
                    g.drawImage(images.get(GameData.GLOW), x * 45, y * 45,this);                    
                }else if(!piece_selected && move.destination == i && 
                        (position.board[i]==GameData.EMPTY || position.board[i]<0)){
                    g.drawImage(images.get(GameData.GLOW2), x * 45, y * 45, this);                                        
                }
                
                if (position.board[i] == GameData.EMPTY) continue;      // không có quân bỏ qua
                
                if(state == GameData.ANIMATING && i==move.source_location) continue; // nếu di chuyển xong r thì bỏ qua thì bỏ qua
                if (position.board[i] > 0) {                                        // Hiển thị quân cờ vị trí tương ứng
                    int piece = position.human_pieces[position.board[i]].value;
                    g.drawImage(images.get(piece),x*45,y*45,this);
                }else{
                    int piece = position.computer_pieces[-position.board[i]].value;
                    g.drawImage(images.get(-piece),x*45,y*45,this);
                }               
            }
            if(state == GameData.ANIMATING){        //đang di chuyển thì là cho game mượt
                g.drawImage(animating_image,movingX,movingY,this);
            }
//            System.out.println("OK");
        }
        
        // Chuột click trên Bàn cờ, để chọn quân và điểm đến
        @Override
        public void mouseClicked(MouseEvent e) {
            if(state != GameData.HUMAN_MOVE || play_options.observer_button.isSelected()) return;
            int location = boardValue(e.getY())*10+boardValue(e.getX()); // Lấy điểm click thuộc ô           
            if(position.board[location] == GameData.ILLEGAL) return;
            if((!piece_selected || position.board[location]>0) && position.board[location] != GameData.EMPTY){
                if(position.board[location]>0){
                    piece_selected = true;
                    move.source_location = location;
                    if(!is_computer){
                        for(int i=1; i<position.human_pieces.length; i++)
                            if(position.human_pieces[i]!=null){
                                if(position.human_pieces[i].location==move.source_location){
                                    history_positions.get(history_positions.size()-1).setPice_source_location(position.human_pieces[i].value);
                                    history_positions.get(history_positions.size()-1).setMove_source_location(1);
                                    history_positions.get(history_positions.size()-1).setNumber_source_location(i);
                                    break;
                                }
                            }
                        for(int i=1; i<position.computer_pieces.length; i++)
                            if(position.computer_pieces[i]!=null){
                                if(position.computer_pieces[i].location==move.source_location){
                                    history_positions.get(history_positions.size()-1).setPice_source_location(position.computer_pieces[i].value);
                                    history_positions.get(history_positions.size()-1).setMove_source_location(0);
                                    history_positions.get(history_positions.size()-1).setNumber_source_location(i);
                                    break;
                                }
                            }
                    }
                }
            }else if(piece_selected && validMove(location)){
                piece_selected = false;
                move.destination = location;  
                if(!is_computer){
                        for(int i=1; i<position.human_pieces.length; i++)
                            if(position.human_pieces[i]!=null){
                                if(position.human_pieces[i].location==move.destination){
                                     history_positions.get(history_positions.size()-1).setPice_destination(position.human_pieces[i].value);
                                     history_positions.get(history_positions.size()-1).setMove_destination(1);
                                     history_positions.get(history_positions.size()-1).setNumber_destination(i);
                                    break;
                                }
                            }
                        for(int i=1; i<position.computer_pieces.length; i++)
                            if(position.computer_pieces[i]!=null){
                                if(position.computer_pieces[i].location==move.destination){
                                    history_positions.get(history_positions.size()-1).setPice_destination(position.computer_pieces[i].value);
                                    history_positions.get(history_positions.size()-1).setMove_destination(0);
                                    history_positions.get(history_positions.size()-1).setNumber_destination(i);
                                    break;
                                }
                            }
                }
                history_positions.get(history_positions.size()-1).setLastMove(move);
                state = GameData.PREPARE_ANIMATION;
                if(!is_computer){
                    kttime=false;
                    client.sendMove(120-move.source_location -1, 120-move.destination -1);
                    client.sendTime(a);
                    if(is_server){
                        x+=a;
                        time.setText("Player 1: "+x+"s\nPlayer 2: "+z);
                    }else if(play_options.client_button.isSelected()){
                        z+=a;
                        time.setText("Player 1: "+x+"s\nPlayer 2: "+z);
                    }
                    System.out.println("Send done");
                    
                }
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
    }
    
    // Tương tự với chessboardPane
    public class HistoryBoardPane extends JPanel{
        public HistoryBoardPane(){
            setBackground(bg_color);            
            setPreferredSize(new Dimension(300,330));            
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(images.get(GameData.HISTORY_TITLE),20,15,this);
            g.drawImage(images.get(GameData.BOARD_IMAGE2),14,44,this);
            if(history_positions.size()<=0) return;
            Position _position = history_positions.get(history_count);
            for(int i=0; i<_position.board.length -11; i++){
                if(_position.board[i] == GameData.EMPTY) continue;
                if(_position.board[i] == GameData.ILLEGAL) continue;
                int x = i%10;
                int y = (i-x)/10;
                if (_position.board[i] > 0) {          
                    int piece = _position.human_pieces[_position.board[i]].value;
                    g.drawImage(images.get(piece+10),x*30,y*30,this);
                }else{
                    int piece = _position.computer_pieces[-_position.board[i]].value;
                    g.drawImage(images.get(-piece+10),x*30,y*30,this);
                }
            }
        }
    }
    /**
     * 
     * @param destination (địa chỉ của ô được chọn)
     * @return boolean (Kết quả có thể hay không di chuyển của người chơi)
     * @Contents 
     */
    public boolean validMove(int destination){   
        int source = move.source_location;                              // lấy địa chỉ điểm bắt đầu đi
        int destination_square = position.board[destination];           // Lấy giá trị của ô đến
        if(destination_square == GameData.ILLEGAL) return false;        // Nếu giá trị không nằm trong ô cờ
        if(!game.safeMove(GameData.HUMAN,source,destination)) 
            /*sound*/
            return false; // Nếu cách di chuyển của người chơi không an toàn
        boolean valid = false;                                                  // Gán sự cho phép false
        int piece_value = position.human_pieces[position.board[source]].value;  // Lấy quân cờ tại vị trí chọn                       
        switch(piece_value){                                                    // Kiểm tra quân cờ thì có thể đi những ô nào
            case Piece.PAWN:// có thể đi thẳng, đi chéo lên nếu có quân dịch
                if(destination == source-10 && destination_square == GameData.EMPTY) valid = true;
                if(destination == source-20 && position.board[source-10] == GameData.EMPTY &&
                        destination_square == GameData.EMPTY && source>80) valid = true;
                if(destination == source-9 && destination_square<0) valid = true;
                if(destination == source-11 && destination_square<0) valid = true;
                break;
            case Piece.KNIGHT: // Xếp chung vì cùng có mảng 8 vị trí đi, xét từng kiểu mà tạo mảng
            case Piece.KING:
                if(piece_value == Piece.KING) valid = checkCastling(destination);           //Kiểm tra có thể castling hay không và thực hiện
                int[] destinations = null;
                if(piece_value == Piece.KNIGHT) destinations = new int[]{source-21,source+21,source+19,source-19,                    
                    source-12,source+12,source-8,source+8};
                else destinations = new int[]{source+1,source-1,source+10,source-10,
                    source+11,source-11,source+9,source-9};
                for(int i=0; i<destinations.length; i++){
                    if(destinations[i] == destination){
                        if(destination_square == GameData.EMPTY || destination_square<0){
                            valid = true;
                            break;
                        }
                    }
                }                
                break;
            case Piece.BISHOP:      //Xếp chung vì có thể đi 4 xa và dài 
            case Piece.ROOK:
            case Piece.QUEEN:
                int[] deltas = null;
                if(piece_value == Piece.BISHOP) deltas = new int[]{11,-11,9,-9};    // 4 ô chéo
                if(piece_value == Piece.ROOK) deltas = new int[]{1,-1,10,-10};      // 4 ô vuông góc
                if(piece_value == Piece.QUEEN) deltas = new int[]{1,-1,10,-10,11,-11,9,-9}; // 8 ô xung quanh, chéo và vuông góc
                for (int i = 0; i < deltas.length; i++) {   
                    int des = source + deltas[i];                                   // Lấy điểm đến là điểm bắt đầu + deltas tìm ô tiếp theo đi
                    valid = true;
                    while (destination != des) {                                    // Trong khi di chuyển trong còn trống thì còn di chuyển đến khi có điểm cản
                        destination_square = position.board[des];  
                        if(destination_square != GameData.EMPTY){
                            valid = false;
                            break;
                        }                        
                        des += deltas[i];
                    }
                    if(valid) break;
                }
                break;
        }        
        return valid;
    }
    
    //Kiểm tra xem có thể nhập thành được hay không (tướng chưa di chuyển, chưa bị chiếu, xe chưa di chuyển, k có quân cản
    public boolean checkCastling(int destination){        
        Piece king = position.human_pieces[8];
        Piece right_rook = position.human_pieces[6];
        Piece left_rook = position.human_pieces[5];
        
        if(king.has_moved) return false;              
        int source = move.source_location;
        
        if(right_rook == null && left_rook == null) return false;           // Không có quân xe
        if(right_rook != null && right_rook.has_moved &&                    // Nếu cả 2 xe đã di chuyển
                left_rook != null && left_rook.has_moved) return false;
            
        if(is_white){                                                       // Với trường hợp quân màu trắng
            if(source != 95) return false;                                  // vị trí chọn không phải vua
            if(destination != 97 && destination != 93) return false;        // Vị trí đi tới không phải là vị trí nhập thành
            if(destination == 97){                                          // Nếu là bên phải
                if(position.board[96] != GameData.EMPTY) return false;      // Nếu cp1 quân cản tại 2 vị trí
                if(position.board[97] != GameData.EMPTY) return false;      
                if(!game.safeMove(GameData.HUMAN,source,96)) return false;  // Khi nhập thành thì bị chiếu
                if(!game.safeMove(GameData.HUMAN,source,97)) return false;
            }else if(destination == 93){                                    // Tương tự với bên trái
                if(position.board[94] != GameData.EMPTY) return false;
                if(position.board[93] != GameData.EMPTY) return false;
                if(!game.safeMove(GameData.HUMAN,source,94)) return false;
                if(!game.safeMove(GameData.HUMAN,source,93)) return false;
            }
        }else{                                                                  // Tương tự với quân đen
            if(source != 94) return false;            
            if(destination != 92 && destination != 96) return false;
            if(destination == 92){
                if(position.board[93] != GameData.EMPTY) return false;
                if(position.board[92] != GameData.EMPTY) return false;
                if(!game.safeMove(GameData.HUMAN,source,93)) return false;
                if(!game.safeMove(GameData.HUMAN,source,92)) return false;
            }else if(destination == 96){
                if(position.board[95] != GameData.EMPTY) return false;
                if(position.board[96] != GameData.EMPTY) return false;
                if(!game.safeMove(GameData.HUMAN,source,95)) return false;
                if(!game.safeMove(GameData.HUMAN,source,96)) return false;
            }
        }        
        return castling=true;
    }
    
    public int boardValue(int value){
        return value/45;
    }
    
    
    public void prepareAnimation(){
        int animating_image_key = 0;
        if(position.board[move.source_location]>0){     /* Vị trí hiện tại của quân cờ*/                // Nếu vị trí chọn là quân của người
            animating_image_key = position.human_pieces[position.board[move.source_location]].value;    // Lấy giá trị là quân cờ nào
        }else {
            animating_image_key = -position.computer_pieces[-position.board[move.source_location]].value;
        }        
        board_pane.animating_image = images.get(animating_image_key);                                   // Lấy hình ảnh tương ứng với key   
        int x = move.source_location%10;                    // Lấy địa chỉ x-y                                               
        int y = (move.source_location-x)/10;
        board_pane.desX = move.destination%10;              // 
        board_pane.desY = (move.destination-board_pane.desX)/10;
        int dX = board_pane.desX-x;
        int dY = board_pane.desY-y;           
        board_pane.movingX = x*45;
        board_pane.movingY = y*45;
        if(Math.abs(dX)>Math.abs(dY)){
            if(dY == 0){
                board_pane.deltaX = (dX>0)?1:-1;
                board_pane.deltaY = 0;
            }else{
                board_pane.deltaX = (dX>0)?Math.abs(dX/dY):-(Math.abs(dX/dY));
                board_pane.deltaY = (dY>0)?1:-1;
            }
        }else{
            if(dX == 0){
                board_pane.deltaY = (dY>0)?1:-1;
                board_pane.deltaX = 0;
            }else{
                board_pane.deltaX = (dX>0)?1:-1;
                board_pane.deltaY = (dY>0)?Math.abs(dY/dX):-(Math.abs(dY/dX));
            }
        }
        state = GameData.ANIMATING;
    }
    
    
    public void animate(){
        if (board_pane.movingX == board_pane.desX * 45 && board_pane.movingY == board_pane.desY * 45) {                                           
            board_pane.repaint();            
            int source_square = position.board[move.source_location];            
            if(source_square>0){ 
                if(is_computer)
                    state = GameData.COMPUTER_MOVE;   
                else
                    state = GameData.HUMAN2_MOVE;                
            }else {
                if(move.destination > 90 && move.destination<99 
                        && position.computer_pieces[-source_square].value == Piece.PAWN){
                    promoteComputerPawn();
                }
                state = GameData.HUMAN_MOVE;
            }                        
            position.update(move);       
            if(source_square>0){
                if(castling){   
                    prepareCastlingAnimation();
                      state = GameData.PREPARE_ANIMATION;
                }else if(move.destination > 20 && move.destination < 29 && 
                        position.human_pieces[source_square].value == Piece.PAWN){
                            if(!is_obsever)
                                promoteHumanPawn();
                            else if(is_obsever)
                                promoteComputerPawn2();
                }
            }else{
                if (gameEnded(GameData.HUMAN)) {
                    state = GameData.GAME_ENDED;
                    return;
                }
            }
            if(!castling && state != GameData.PROMOTING) {
                newHistoryPosition();//a
//                System.out.println("OK 3");
            }
            if(castling) castling = false;
            //if(state != GameData.ANIMATING){                
            //}
            for (Piece human_piece : position.human_pieces) {
                if (human_piece == null) {
                    continue;
                }
                //System.out.print(position.human_pieces[i].value+" ");
            }
        }
        board_pane.movingX += board_pane.deltaX;
        board_pane.movingY += board_pane.deltaY;
        board_pane.repaint();
    }
    
    //Phong chốt cho người chơi
    public void promoteHumanPawn(){        
        promotion_pane.location = move.destination;
        promotion_pane.index = position.board[move.destination];
        if(!is_obsever)
            promotion_pane.setVisible(true);
        state = GameData.PROMOTING;
    }
    
    //Phong hậu cho Computer
    public void promoteComputerPawn(){
        int piece_index = position.board[move.source_location];
        position.computer_pieces[-piece_index] = new Piece(Piece.QUEEN,move.destination);
    }
    
    public void promoteComputerPawn2(){
        int piece_index = position.board[move.destination];
        position.human_pieces[piece_index] = new Piece(Piece.QUEEN,move.destination);
    }
    
    // Nhập thành cho người chơi
    public void prepareCastlingAnimation(){
        if(move.destination == 97 || move.destination == 96){
            move.source_location = 98;
            move.destination -= 1;
        }else if(move.destination == 92 || move.destination == 93){
            move.source_location = 91;
            move.destination += 1;
        }
    }
    
    //Khởi tạo mảng và chỉ số hiển thị
    public void newHistoryPosition(){        
        history_positions.add(new Position(position));
        history_count = history_positions.size()-1;
        history_pane.repaint();
    }
   
    /**
     * Hàm router kiểu ảnh, và loại ảnh.
     * @param type (GameData) loại ảnh cần load
     * @param style (int) Kiểu số mấy
     * @param isRepaint (boolean) Có cần paint lại game không
     */
    public void loadImages(int type, int style, boolean isRepaint){
        style = 0;
        if(type == GameData.LOADPIECE){
            switch (style){
                case 0:
                    loadPieceImages(style);
                    break;
                case 1:
                    
                    break;
                default:
                    break;
            }
        }       
        else{
            if(type == GameData.LOADICON){
                loadMenuIcons(style);
            }
            else{
                if(type == GameData.LOADBOARD){
                    loadBoardImages(style);
                }
            }
        }
        if(isRepaint){
            repaint();
        }
    }
    
    /**
     * 
     * @param style (int) Kiểu cần thêm vào key
     * @return String[] thể hiện key đã được thêm style
     */
    private String[] addStyleKey(String[] strArr, int style){
        if(style>0){
            int lenth = strArr.length;
            for(int i = 0; i<lenth; i++){
                if(!strArr[i].isEmpty())
                strArr[i]+= Integer.toString(style);
            }
        }
   
        return strArr;
    }
    //Load hình ảnh quân cờ, gán key với cờ của người là k, và -k nếu của computer
    private void loadPieceImages(int style){
        String[] resource_keys = {"p","n","b","r","q","k"};
        addStyleKey(resource_keys, 0);
        if(style == 0){
            System.out.print("Đã load ảnh piece style 1\n");
        }
        
        int[] images_keys = {Piece.PAWN,Piece.KNIGHT,Piece.BISHOP,Piece.ROOK,Piece.QUEEN,Piece.KING};
        try{
            for(int i=0; i<resource_keys.length; i++){             
                images.put(images_keys[i],ImageIO.read(resource.getResource((is_white?"w":"b")+resource_keys[i])));
                images.put(-images_keys[i],ImageIO.read(resource.getResource((is_white?"b":"w")+resource_keys[i])));   
                images.put(images_keys[i]+10,ImageIO.read(resource.getResource((is_white?"w":"b")+resource_keys[i]+'2')));
                images.put(-images_keys[i]+10,ImageIO.read(resource.getResource((is_white?"b":"w")+resource_keys[i]+'2'))); 
            }               
        }catch(IOException ex){
        }        
    }
    
    //Load hình ảnh cho bàn cờ và gán ID cho nó
    public void loadBoardImages(int style){
        String[] resource_keys = {"chessboard","history_board","glow","glow2","history_title","mychessmate"};
        
        if(style==0){
            System.out.print("Đã load ảnh Board style 1\n");
        }
        try{ 
            images.put(GameData.BOARD_IMAGE,ImageIO.read(resource.getResource(resource_keys[0])));
            images.put(GameData.BOARD_IMAGE2,ImageIO.read(resource.getResource(resource_keys[1])));
            images.put(GameData.GLOW,ImageIO.read(resource.getResource(resource_keys[2])));
            images.put(GameData.GLOW2,ImageIO.read(resource.getResource(resource_keys[3])));            
            images.put(GameData.HISTORY_TITLE,ImageIO.read(resource.getResource(resource_keys[4])));
            images.put(GameData.MYCHESSMATE,ImageIO.read(resource.getResource(resource_keys[5])));
        }catch(IOException ex){
        }        
    }
    
    //Load tất cả icon trong resource và gán ID để sử dụng
    public void loadMenuIcons(int style){
        String[] resource_keys = {"new_game","new_game_hover","quit","quit_hover","history","history_hover","about","about_hover","first","first_hover","next","next_hover","previous","previous_hover","last","last_hover","send","send_hover","group_chat","group_chat_hover"};
        
        if(style==0){
            System.out.print("Đã load ảnh MenuIcon style 1\n");
        }
        
        icon_images.put(GameData.NEW_BUTTON,new ImageIcon(resource.getResource(resource_keys[0])));
        icon_images.put(GameData.NEW_BUTTON2,new ImageIcon(resource.getResource(resource_keys[1])));
        icon_images.put(GameData.QUIT_BUTTON,new ImageIcon(resource.getResource(resource_keys[2])));
        icon_images.put(GameData.QUIT_BUTTON2,new ImageIcon(resource.getResource(resource_keys[3])));
        icon_images.put(GameData.HISTORY_BUTTON,new ImageIcon(resource.getResource(resource_keys[4])));
        icon_images.put(GameData.HISTORY_BUTTON2,new ImageIcon(resource.getResource(resource_keys[5])));
        icon_images.put(GameData.ABOUT_BUTTON,new ImageIcon(resource.getResource(resource_keys[6])));
        icon_images.put(GameData.ABOUT_BUTTON2,new ImageIcon(resource.getResource(resource_keys[7])));
        
        icon_images.put(GameData.FIRST_BUTTON,new ImageIcon(resource.getResource(resource_keys[8])));
        icon_images.put(GameData.FIRST_BUTTON2,new ImageIcon(resource.getResource(resource_keys[9])));
        icon_images.put(GameData.NEXT_BUTTON,new ImageIcon(resource.getResource(resource_keys[10])));
        icon_images.put(GameData.NEXT_BUTTON2,new ImageIcon(resource.getResource(resource_keys[11])));
        icon_images.put(GameData.PREV_BUTTON,new ImageIcon(resource.getResource(resource_keys[12])));
        icon_images.put(GameData.PREV_BUTTON2,new ImageIcon(resource.getResource(resource_keys[13])));
        icon_images.put(GameData.LAST_BUTTON,new ImageIcon(resource.getResource(resource_keys[14])));
        icon_images.put(GameData.LAST_BUTTON2,new ImageIcon(resource.getResource(resource_keys[15])));
        icon_images.put(GameData.SEND_BUTTON,new ImageIcon(resource.getResource(resource_keys[16])));
        icon_images.put(GameData.SEND_BUTTON2,new ImageIcon(resource.getResource(resource_keys[17])));
        icon_images.put(GameData.GROUP_CHAT_BUTTON,new ImageIcon(resource.getResource(resource_keys[18])));
        icon_images.put(GameData.GROUP_CHAT_BUTTON2,new ImageIcon(resource.getResource(resource_keys[19])));
    }
    
    //Quit game
    public void quit(){
        int option = JOptionPane.showConfirmDialog(null,"Are you sure you want to quit?", 
                    "MyChessmate1.1", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(option == JOptionPane.YES_OPTION){
            try{
                if(play_options!=null)
                    if(play_options.online_button.isSelected())    
                        if(!is_server)
                            client.sendExit();
            }catch(Exception ex){}
            System.exit(0);
        }  
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            @SuppressWarnings("UseSpecificCatch")
            public void run(){
                try{
                    boolean nimbusFound = false;
                        for(UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()){
                            if(info.getName().equals("Nimbus")){
                                UIManager.setLookAndFeel(info.getClassName());
                                nimbusFound = true;
                                break;
                            }
                        }
                        if(!nimbusFound){
                            int option = JOptionPane.showConfirmDialog(null,
                                    "Nimbus Look And Feel not found\n"+
                                    "Do you want to proceed?",
                                    "Warning",JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE);
                            if(option == JOptionPane.NO_OPTION){
                                System.exit(0);
                            }
                        }
                    MyChessmate mcg = new MyChessmate();
                   // mcg.pack();
                    mcg.setLocationRelativeTo(null);
                    mcg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    mcg.setResizable(false);
                    mcg.setVisible(true); 
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, e.getStackTrace());
                }
            }
        });
    }
}
