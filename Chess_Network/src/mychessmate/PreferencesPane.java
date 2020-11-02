
package mychessmate;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Logger;


// Class thiết kế New game với computer
public class PreferencesPane extends JFrame implements ActionListener{
     JSlider levelSlider;                           // Slide chọn mức độ khó của computer
     JRadioButton white_button;                     // Radio chọn quân trắng
     JRadioButton black_button;                     // Radio chọn quân đen
     JRadioButton computer_button;                  // Chế độ đánh máy
     JRadioButton online_button;                    // Chế độ chơi online
     JRadioButton server_button;                    // Server
     JRadioButton client_button;                    // Client is Player
     JRadioButton observer_button;                   // Client is Oberser
     
     JButton continue_button;                        //tạo ván mới
     JButton ok;                                    // Button Ok
     JButton cancel;                                // Button Cancel
     final static int WHITE = 0;                    // Mặc định quân Trắng là 0
     final static int BLACK = 1;                    // Mặc định quân Đen là 1 trong lần gọi đầu, và những lần sau k cần khởi tạo
     MyChessmate chessmate;                         // Bàn cờ để lưu trữ
     
     protected JPanel mainPane = new JPanel(new BorderLayout());       // Tạo Jpanel chứa 3 Jpanel là level, color and button   
     protected JPanel Level;
     
     private JPanel online_pane;
     
    //Contruction với bàn cờ trước đó
    public PreferencesPane(MyChessmate chessmate){
        super("Options");                                       //Khởi tạo JFram với title là Options
        
        this.chessmate = chessmate;                             // Lưu lại chessmate
        
        online_pane = createNetworkPane();
        mainPane.add(online_pane,BorderLayout.CENTER);
        modifyOnline(false);
        Level = createLevelPane();
        mainPane.add(Level,BorderLayout.NORTH);
        //mainPane.add(createColorPane(),BorderLayout.CENTER);
        mainPane.add(createModelPane(),BorderLayout.WEST);
        mainPane.add(createButtonPane(),BorderLayout.SOUTH);
        mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setContentPane(mainPane);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);             // Chỉ tắt JFrame hiện tại
        online_button.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        continue_button.addActionListener(this);
    }
    
    //Khởi tạo Level JPanel
    public JPanel createLevelPane(){
        levelSlider = new JSlider(JSlider.HORIZONTAL,1,5,4);    //Tạo với slide với 5 mức từ 1 đến 5
        JPanel levelPane = new JPanel();        
        JPanel levelPane1 = new JPanel(new GridLayout(1,2));
        levelSlider.setMajorTickSpacing(1);                     // Tạo khoảng cách giữ các số là 1           
        levelSlider.setPaintTicks(true);                        // Thêm dấu gạch vào bên dưới slide phân chia
        levelSlider.setPaintLabels(true);                     // Thêm số vào các dấu gạch
        levelPane1.add(levelSlider);                             //
        
        levelPane1.setBorder(BorderFactory.createCompoundBorder( //Cài đặt border chìm xuống dưới 
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createTitledBorder("Level")));
        
        levelPane.setBorder(BorderFactory.createCompoundBorder( // Tạo boder chìm xuống trong Jpanel
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createTitledBorder("Level computer and Color of you")));
        
        levelPane.add(levelPane1, BorderLayout.WEST);
        levelPane.add(createColorPane(),BorderLayout.EAST);
        return levelPane;                               
    }
    
    // Khởi tạo ColorPanel
    public JPanel createColorPane(){
        JPanel colorPane = new JPanel(new GridLayout(1,2));     // Bật chế độ tự thiết kế layout với khoảng cách 1 va 2
        white_button = new JRadioButton("White",true);          // Auto check khi mở nut trắng
        black_button = new JRadioButton("Black");
        ButtonGroup group = new ButtonGroup();                  // Tạo group button để chỉ có thể tick 1 trong 2 radiobutton
        group.add(white_button);                                
        group.add(black_button);
        colorPane.add(white_button);
        colorPane.add(black_button);        
        
        colorPane.setBorder(BorderFactory.createCompoundBorder( //Cài đặt border chìm xuống dưới 
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createTitledBorder("Color")));
        return colorPane;
    }
    
    // Khởi tạo thêm chọn client server
    public JPanel createNetworkPane(){
        JPanel networkPane = new JPanel(new GridLayout(1,2));     // Bật chế độ tự thiết kế layout với khoảng cách 1 va 2
        server_button = new JRadioButton("Server",true);          // Auto check khi mở nut trắng
        client_button = new JRadioButton("Player");
        observer_button = new JRadioButton("Observer");
        ButtonGroup group = new ButtonGroup();                  // Tạo group button để chỉ có thể tick 1 trong 2 radiobutton
        group.add(server_button);                                
        group.add(client_button);
        group.add(observer_button);
        networkPane.add(server_button);
        networkPane.add(client_button); 
        networkPane.add(observer_button);
        
        //add event
        client_button.addActionListener(this);
        observer_button.addActionListener(this);
        server_button.addActionListener(this);
        networkPane.setBorder(BorderFactory.createCompoundBorder( //Cài đặt border chìm xuống dưới 
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createTitledBorder("Network")));
        return networkPane;
    }
    
    public JPanel createModelPane(){
        JPanel modelPane = new JPanel(new GridLayout(1,2));
        computer_button = new JRadioButton("Computer", true);   // Chế độ chơi với máy
        online_button = new JRadioButton("Online");             // Chế độ chơi online
        ButtonGroup group2 = new ButtonGroup();
        group2.add(computer_button);
        group2.add(online_button);
        
        modelPane.add(computer_button);
        modelPane.add(online_button);
        
        //add action
        computer_button.addActionListener(this);
        //Cài đặt border chìm xuống dưới 
        modelPane.setBorder(BorderFactory.createCompoundBorder( 
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createTitledBorder("Model")));
        return modelPane;
    }
    //Khởi tạo JPanel chứ 2 button và không có border
    public JPanel createButtonPane(){
        JPanel buttonPane = new JPanel(new BorderLayout());
        JPanel pane = new JPanel(new GridLayout(1,2,5,0));
        pane.add(ok = new JButton("OK"));
        pane.add(cancel = new JButton("Cancel"));
        pane.add(continue_button = new JButton("Continue"));
        continue_button.setEnabled(false);
        buttonPane.add(pane,BorderLayout.EAST);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        return buttonPane;
    }
    
    //Bắt sự kiện người dùng click vào nút Ok thì tạo mới bàn cờ
    // Ra cờ hiệu là bàn cờ trức đó đã kết thúc
     @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == ok){
            //set new game, and old game ended
            chessmate.state = GameData.GAME_ENDED;  
            
            boolean isPassword = false; //Kiểm tra password đã input
            boolean isPort = false;
            // Nếu chơi online thì input tên và password
            if(online_button.isSelected()){    
                if(inputPort()){
                    if(inputName()==false){
                        chessmate.client_name = "No name";
                    }
                    isPassword = inputPassword();

                    if(isPassword){
                        chessmate.newGame();
                        if(chessmate.geterrCode()==0){
                            chessmate.east_pane.setVisible(true);
//                            chessmate.east_pane2.setVisible(true);
                        }
                    }
                }
            }
            else{
                chessmate.newGame();
                chessmate.east_pane.setVisible(true);
            }
        }
        if(e.getSource() == continue_button){
            int k=JOptionPane.showConfirmDialog(null, "Bạn có chơi ván mới", "New Game", JOptionPane.YES_NO_OPTION);
            if(k==0)
                chessmate.Continue();
//            System.out.println("Hello");
        }
        if (e.getSource() == online_button){
            modifyOnline(true);
            pack();
            setLocationRelativeTo(null);
            return;
        }
        if (e.getSource() == computer_button){
            modifyOnline(false);
            pack();
            setLocationRelativeTo(null);
            return;
        }
        if(e.getSource() != client_button&&e.getSource() != observer_button){
        } else {
            black_button.setSelected(true);
            pack();
            setLocationRelativeTo(null);
            return;
         }
        if(e.getSource() == server_button){
            white_button.setSelected(true);
            pack();
            setLocationRelativeTo(null);
            return;
        }
        // Ẩn màn hình chọn
        setVisible(false);
    }
    
    private void modifyOnline(boolean isEnabled){
        if(white_button!= null&&black_button!=null){
            black_button.setEnabled(!isEnabled);
            white_button.setEnabled(!isEnabled);
        }
        server_button.setEnabled(isEnabled);
        client_button.setEnabled(isEnabled);
        observer_button.setEnabled(isEnabled);
        online_pane.setEnabled(isEnabled);
    }
    

    private boolean inputName(){
        //enter name server
        chessmate.client_name = 
                JOptionPane.showInputDialog("What is your name?").trim();  
        return chessmate.client_name.length()!=0;
    }
    private boolean inputPort(){
        //enter name server
        String str="";
        try{
            if((str=JOptionPane.showInputDialog("Input port sever:").trim()).length()!=0){
                chessmate.port = 
                        Integer.valueOf(str); 
            }else{
                chessmate.port = 3200;
                JOptionPane.showMessageDialog(rootPane, "Tự khởi tạo Port=3200");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, "Port là một số", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private boolean inputPassword(){
        chessmate.server_password = 
                JOptionPane.showInputDialog("Input password server: ");  
//        if("".equals(chessmate.server_password)){
//            return false;
//        }
        return chessmate.server_password != null;
    }
}