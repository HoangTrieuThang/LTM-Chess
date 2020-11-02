
package mychessmate;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


//Class thiết kế JPanel phong chốt, khi nó về đến cuối hàng bên kia
public class PromotionPane extends JDialog implements ActionListener{
    int index;                  //
    int location;               // Vị trí của chốt phong
    JPanel main_pane;           // Tạo mới một JPanel để hiển thị
    MyChessmate chessmate;      // Bàn cờ trước khi phong chốt
    
    //Khởi tạo với bàn cờ đang chơi
    public PromotionPane(MyChessmate chessmate){
        setTitle("New Piece");                              // JDialog với title là New Piece
        this.chessmate = chessmate;                         // lưu lại bàn cờ trước đó
        main_pane = new JPanel(new GridLayout(1,4,10,0));   // Xác nhận tọa độ cho Jpanel chứ các quân cờ (button)
        Color bg_color = Color.decode("#efd39c");           // Lấy màu chung của Jpane
        main_pane.setBackground(bg_color);                  // Cài màu nền cho Jpanel
        //Resource resource = new Resource();                 // Lấy resource chuẩn bị lấy ảnh

        int[] cmdActions = {                                // Khởi tạo các hành động có thể chọn của đối tượng
            Piece.QUEEN,Piece.ROOK,Piece.BISHOP,Piece.KNIGHT
        };
        for(int i=0; i<cmdActions.length; i++){             //Tạo button mới kèm theo tên và sự kiện listen click
            JButton button = new JButton();
            button.addActionListener(this);
            button.setActionCommand(cmdActions[i]+"");              
            main_pane.add(button);                          // Thêm chúng vào JPanel
        }
        setContentPane(main_pane);        
        setResizable(false);
        addWindowListener(new WindowAdapter(){              // Bắt lỗi sự kiện nếu người chơi tắt mà không chọn quân phong
            public void windowClosing(WindowEvent e){       // Mặc định là Phong hậu
                resumeGame(Piece.QUEEN);
            }
        });
    }
    
    // 
    public void setIcons(boolean white){
        Component[] components = main_pane.getComponents(); // Lấy con của JPanel
        Resource resource = new Resource();                 // Lấu resource từ qua file *.properties     
        String[] resourceStrings = {"q","r","b","n"};       // Đặt tên các con cần lấy đã lưu để kết hợp với w hoặc b
        
        //Lần lượt các button của JPanel phong chốt được lấy ra sét hình ảnh, 
        // Tùy vào quân chốt phong là trắng hay đen mà phong quan trắng hay đen
        for(int i=0; i<components.length; i++){         
            JButton button = (JButton)components[i];
            button.setIcon(new ImageIcon(
                    resource.getResource((white?"w":"b")+resourceStrings[i])));
        }
        pack();
        setLocationRelativeTo(null);
    }
    
    // Tự động gọi khi người dùng chọn quân cờ tương ứng và trả ra vị trí trong cmdAction
    // tương ứng với các quân đó 
    @Override
    public void actionPerformed(ActionEvent e){
        int promotion_piece = Integer.parseInt(e.getActionCommand());
        setVisible(false);
        resumeGame(promotion_piece);
    }
    
    // Hàm tiếp tục game với quân cờ được phong từ chốt
    public void resumeGame(int promotion_piece){  
        chessmate.position.human_pieces[index] = new Piece(promotion_piece, location); // Thêm quân cờ được phòng vào vị trí trên bàn cờ vào mảng cờ của Human
        chessmate.newHistoryPosition(); // Thêm mới vào mảng Lịch sử lưu trử
        chessmate.board_pane.repaint(); // Vẽ lại bàn cờ
        if (chessmate.is_computer) {
            chessmate.state = GameData.COMPUTER_MOVE;  // Chuyển lượt sang Computer
        }
        
        //------------------------Mở rộng chuyển lượt sang người chơi khác tại đây
    }
}
