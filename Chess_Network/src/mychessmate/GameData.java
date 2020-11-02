/**
 * Đặt tên để tiện quy đổi dê dàng thao tác mà chỉ cần nhớ tên
 */
package mychessmate;

/**
 *
 * 
 */
public class GameData {
    public final static int EMPTY = 55;                 // Ô cờ trống
    public final static int ILLEGAL = 77;               // Ô không thuộc trong bàn cờ
    public final static int WAITING = 3;                // Waiting other player
    public final static int HUMAN = 1;                  // Người chơi
    public final static int COMPUTER = 0;               // Máy chơi
    public final static int HUMAN2 = 2;                 // Người chơi qua mạng
    public final static int BOARD_IMAGE = 1000;         // Hình ảnh bàn cờ
    public final static int GLOW = 1001;                // Điểm sáng điểm bắt đầu
    public final static int GLOW2 = 1002;               // Điểm sáng điểm đi đến
    public final static int PREPARE_ANIMATION = 1003;   //
    public final static int ANIMATING = 1004;           // Nhập thành
    public final static int HUMAN_MOVE = 1005;          // Lượt di chuyển của HUmam
    public final static int COMPUTER_MOVE = 1006;       // Lượt di chuyển của Computer
    public final static int HUMAN2_MOVE = 1007;
    public final static int GAME_ENDED = 1008;          // Game đã kết thúc
    public final static int DRAW = 0;                   // Game đang tiếp tục
    public final static int CHECKMATE = 1;              // Đang bị check
    public final static int NEW_BUTTON = 10078;         // 
    public final static int NEW_BUTTON2 = 10079;        //
    public final static int QUIT_BUTTON = 10080;        // Nút thoát 1
    public final static int QUIT_BUTTON2 = 10081;       // Nút thoát 2
    public final static int ABOUT_BUTTON = 10082;       // Nút about
    public final static int ABOUT_BUTTON2 = 10083;      //
    public final static int HISTORY_BUTTON = 10084;     //
    public final static int HISTORY_BUTTON2 = 10085;    //
    public final static int FIRST_BUTTON = 10086;       //
    public final static int FIRST_BUTTON2 = 10087;      //
    public final static int LAST_BUTTON = 10088;        //
    public final static int LAST_BUTTON2 = 10089;       //
    public final static int PREV_BUTTON = 10090;        //
    public final static int PREV_BUTTON2 = 10091;       //
    public final static int NEXT_BUTTON = 10092;        //
    public final static int NEXT_BUTTON2 = 10093;       //
    public final static int PROMOTING = 10086;          //  Phong chốt
    public final static int BOARD_IMAGE2 = 10087;       //  Bàn cờ nhỏ history
    public final static int HISTORY_TITLE = 10094;      //  
    public final static int MYCHESSMATE = 10095;        //  
    public final static int LOADPIECE = 20001;          // Loại hình load ảnh cờ
    public final static int LOADBOARD = 20002;          // Loại hình load ảnh liên quan bàn cờ
    public final static int LOADICON = 20003;           // Loại hình load ảnh hỗ trợ nút bấm
    public final static String VERSION = new Resource().getResourceString("version");
    public final static String AUTHOR = new Resource().getResourceString("author");
    public final static String DATECREATE = new Resource().getResourceString("datecreate");
    public final static int SEND_BUTTON=10070;
    public final static int SEND_BUTTON2=10071;
    public final static int GROUP_CHAT_BUTTON=10072;
    public final static int GROUP_CHAT_BUTTON2=10073;
    //Some game
    /*public final static int[] HUMAN_PAWN_TABLE = {
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,10, 10, 10, 10, 10, 10, 10, 10,0,
        0,7, 7, 7, 7, 7, 7, 7, 7,0,
        0,4, 4, 5, 6, 6, 5, 4, 4,0,
        0,2, 1, 1, 5, 5, 1, 1, 2,0,
        0,1, 1, 0, 2, 2, 0, 1, 1,0,
        0,1, 1, 1, 0, 0, 1, 1, 1,0,
        0,2, 0, 0, 0, 0, 0, 0, 2,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0
    };
    public final static int[] COMPUTER_PAWN_TABLE = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 
        0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 
        0, 1, 1, 0, 2, 2, 0, 1, 1, 0, 
        0, 2, 1, 1, 5, 5, 1, 1, 2, 0, 
        0, 4, 4, 5, 6, 6, 5, 4, 4, 0, 
        0, 7, 7, 7, 7, 7, 7, 7, 7, 0, 
        0, 10, 10, 10, 10, 10, 10, 10, 10, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    public final static int[] HUMAN_KNIGHT_TABLE = {
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,1, 0, 0, 0, 0, 0, 0, 1,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 1, 1, 1, 1, 0, 0,0,
        0,0, 1, 1, 2, 2, 1, 1, 0,0,
        0,0, 0, 1, 2, 2, 1, 0, 0,0,
        0,0, 0, 3, 1, 1, 3, 0, 0,0,
        0,0, 0, 1, 2, 2, 1, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0
    };
    public final static int[] COMPUTER_KNIGHT_TABLE = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 1, 2, 2, 1, 0, 0, 0, 
        0, 0, 0, 3, 1, 1, 3, 0, 0, 0, 
        0, 0, 0, 1, 2, 2, 1, 0, 0, 0, 
        0, 0, 1, 1, 2, 2, 1, 1, 0, 0, 
        0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    public final static int[] HUMAN_BISHOP_TABLE = {
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,-20, -10, -10, -10, -10, -10, -10, -20,0,
        0,-10, 0, 0, 0, 0, 0, 0, -10,0,
        0,-10, 0, 5, 10, 10, 5, 0, -10,0,
        0,-10, 5, 5, 10, 10, 5, 5, -10,0,
        0,-10, 0, 10, 10, 10, 10, 0, -10,0,
        0,-10, 10, 10, 10, 10, 10, 10, -10,0,
        0,-10, 5, 0, 0, 0, 0, 5, -10,0,
        0,-20, -10, -40, -10, -10, -40, -10, -20,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0,
        0,0, 0, 0, 0, 0, 0, 0, 0,0
    };   
    public final static int[] COMPUTER_BISHOP_TABLE = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, -20, -10, -40, -10, -10, -40, -10, -20, 0, 
        0, -10, 5, 0, 0, 0, 0, 5, -10, 0, 
        0, -10, 10, 10, 10, 10, 10, 10, -10, 0, 
        0, -10, 0, 10, 10, 10, 10, 0, -10, 0, 
        0, -10, 5, 5, 10, 10, 5, 5, -10, 0, 
        0, -10, 0, 5, 10, 10, 5, 0, -10, 0, 
        0, -10, 0, 0, 0, 0, 0, 0, -10, 0, 
        0, -20, -10, -10, -10, -10, -10, -10, -20, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }; 
    public static void main(String[] args){
        int j = 119;
        for(int i=0; i<120; i++){            
            if(i%10==0) System.out.println();
            System.out.print(HUMAN_PAWN_TABLE[j--]+", ");            
        }
    }*/
}
