package mychessmate;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Position implements Serializable{
    Move last_move;                                 // Lần đi cuối cùng
    int[] board = new int[120];                     // Mảng lưu bàn cờ 120 vì tại vị trị cuối bàn cờ có thể lấy vị trí của Quân mã nằm ngoài bàn cờ cách 2 ô,
    Piece[] human_pieces = new Piece[17];           // Mảng lưu  quân cờ của human
    Piece[] computer_pieces = new Piece[17];
    int pice_destination=0;
    int pice_source_location=0;
    int number_destination=0;
    int number_source_location=0;
    int move_destination=0;
    int move_source_location=0;
        
    // Khởi tạo Mảng lưu bàn cờ với GameData.EMPTY nghĩa là bàn cờ trống
    public Position(){
        for(int i=0; i<board.length; i++){
            board[i] = GameData.EMPTY;
        }
    }
    
    //Khởi tạo bàn cờ với các vị trí sẵn có trên bàn cờ.
    public Position(Position position){
        this(position,null);
    }
    
    //Khở tạo bàn cờ với vị trí các quân đã có và lượt đi chuyển củng có
    public Position(Position position, Move last_move){
        //Copy lại bàn cờ
        System.arraycopy(position.board, 0, this.board, 0, board.length);
        
        //Copy lại quân cờ của computer và human
        for(int i=1; i<human_pieces.length; i++){
            if(position.human_pieces[i] != null){
                this.human_pieces[i] = position.human_pieces[i].clone();
            }
            if(position.computer_pieces[i] != null){
                this.computer_pieces[i] = position.computer_pieces[i].clone();
            }
        }
        //Update lại vị trí đã đi lần cuối
        if(last_move != null) update(last_move);
    }    
    public void newPosition(Piece[] human_piecess,Piece[] computer_piecess){
//        System.out.println("OK");
        //Copy lại bàn cờ
        human_pieces=human_piecess;
        computer_pieces=computer_piecess;
        //Khởi tạo bàn cờ(1-120 phần tử) với các giá trị ngoài EMPTY là ILIEGAL và từ 21-28,31-38,41-48,51-58,61-68,71-78,81-88,91-98 là EMPTY
        board = new int[]{
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL
        };
        // sắp hai bảng cờ vào bàn cờ với vị trí tương ứng
        // Đặt giá trị của board tại vị trí i dương nếu là human và âm nếu là computer
        // Tức quân cờ có giá trị âm là quân của computer và ngược lại
        for(int i=0; i<board.length; i++){                        
            for(int k=1; k<human_pieces.length; k++){
                if(human_pieces[k]!=null)
                    if(i==human_pieces[k].location){
                        board[i] = k;
                    }
                if(computer_pieces[k]!=null)
                    if(i==computer_pieces[k].location){
                        board[i] = -k;
                    }
            }
        }
    }
    // Khởi tạo ban đầu với các giá trị tương ứng của mảng Human và computer
    // Tham số truyền vào xác định là người chọn quân trắng hay đen
    public void initialize(boolean humanWhite){         
        //bàn cờ được hiểu như sau
        /**
         * 21 22 23 24 25 26 27 28      ComputerROOK...ComputerQueen...ComputerROOK
         * 31                   38
         * 41                   48
         * 51                   58
         * 61                   68
         * 71                   78
         * 81                   88
         * 91 92 93 94 95 96 97 98      HumanROOK....HumanQUEEN...HumanROOK
         */
        human_pieces[1] = new Piece(Piece.KNIGHT,92);
        human_pieces[2] = new Piece(Piece.KNIGHT,97);
        human_pieces[3] = new Piece(Piece.BISHOP,93);
        human_pieces[4] = new Piece(Piece.BISHOP,96);
        human_pieces[5] = new Piece(Piece.ROOK,91);
        human_pieces[6] = new Piece(Piece.ROOK,98);
        human_pieces[7] = new Piece(Piece.QUEEN,humanWhite?94:95);
        human_pieces[8] = new Piece(Piece.KING,humanWhite?95:94);
        
        computer_pieces[1] = new Piece(Piece.KNIGHT,22);
        computer_pieces[2] = new Piece(Piece.KNIGHT,27);
        computer_pieces[3] = new Piece(Piece.BISHOP,23);
        computer_pieces[4] = new Piece(Piece.BISHOP,26);
        computer_pieces[5] = new Piece(Piece.ROOK,21);
        computer_pieces[6] = new Piece(Piece.ROOK,28);
        computer_pieces[7] = new Piece(Piece.QUEEN,humanWhite?24:25);
        computer_pieces[8] = new Piece(Piece.KING,humanWhite?25:24); 
        
        int j = 81;                                     // 
        for(int i=9; i<human_pieces.length; i++){
            human_pieces[i] = new Piece(Piece.PAWN,j);
            computer_pieces[i] = new Piece(Piece.PAWN,j-50);
            j++;
        }      
        //Khởi tạo bàn cờ(1-120 phần tử) với các giá trị ngoài EMPTY là ILIEGAL và từ 21-28,31-38,41-48,51-58,61-68,71-78,81-88,91-98 là EMPTY
        board = new int[]{
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL
        };
        // sắp hai bảng cờ vào bàn cờ với vị trí tương ứng
        // Đặt giá trị của board tại vị trí i dương nếu là human và âm nếu là computer
        // Tức quân cờ có giá trị âm là quân của computer và ngược lại
        for(int i=0; i<board.length; i++){                        
            for(int k=1; k<human_pieces.length; k++){
                if(i==human_pieces[k].location){
                    board[i] = k;
                }else if(i==computer_pieces[k].location){
                    board[i] = -k;
                }
            }
        }
    }   
    
    private int[] initBoard(){
        int[] newBoard = new int[120];
        //Khởi tạo bàn cờ(1-120 phần tử) với các giá trị ngoài EMPTY là ILIEGAL và từ 21-28,31-38,41-48,51-58,61-68,71-78,81-88,91-98 là EMPTY
        newBoard = new int[]{
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.EMPTY,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,
            GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL,GameData.ILLEGAL
        };
        return newBoard;
    }
    public Position nevgative() throws CloneNotSupportedException {
        Position nevPosition = (Position) this.clone();
        for(int i = 21; i<= 98; i++){
            nevPosition.board[i] = this.board[98-i];
        }
        for(int i=0;i<17; i++){
            nevPosition.human_pieces[i].has_moved = this.human_pieces[i].has_moved;
            nevPosition.human_pieces[i].location = this.human_pieces[i].location-98+21;
            nevPosition.human_pieces[i].value = this.human_pieces[i].value;
            nevPosition.computer_pieces[i].has_moved = this.computer_pieces[i].has_moved;
            nevPosition.computer_pieces[i].location = this.computer_pieces[i].location-98+21;
            nevPosition.computer_pieces[i].value = this.computer_pieces[i].value;
        }
        return nevPosition;
    }
    //
    public void update(Move move){
        this.last_move = move;                                                      
        int source_index = board[move.source_location]; // lấy giá trị tại ô bắt đầu đi
        int destination_index = board[move.destination];  // Lấy giá trị tại ô kết thúc
         
        if(source_index>0){                                             // Nếu là quân cờ của human thì 
            human_pieces[source_index].has_moved = true;                // Bật đèn đã đi chuyển lên cho quân tại vị trí bắt đầu đi
            human_pieces[source_index].location = move.destination;     // Và gán lại vị trí địch đến của quân cờ đã di chuyển
            if(destination_index<0){                                    // Nếu tại vị trí đích đến là quân địch (computer)                
                computer_pieces[-destination_index] = null;             // Gán quân của computer tại vị trí -(-k) là null ( tức là bị ăn)
            }   
            //Ngược lại với lần đi của computer
        }else{
            computer_pieces[-source_index].has_moved = true;
            computer_pieces[-source_index].location = move.destination;
            if(destination_index>0 && destination_index != GameData.EMPTY){                
                human_pieces[destination_index] = null;
            }            
        }
        //Cuối cùng, trả lại vị vị trí bắt đầu đi là trống 
        // vị trí đến là giá trị của quân cờ trước đó đi đến
        board[move.source_location] = GameData.EMPTY;
        board[move.destination] = source_index;
    }
    public Piece[] getHuman_pieces(){
        return human_pieces;
    }
    public Piece[] getComputer_pieces(){
        return computer_pieces;
    }
    public void setLastMove(Move move){
        this.last_move=move;
    }

    public int getPice_destination() {
        return pice_destination;
    }

    public void setPice_destination(int pice_destination) {
        this.pice_destination = pice_destination;
    }

    public int getPice_source_location() {
        return pice_source_location;
    }

    public void setPice_source_location(int pice_source_location) {
        this.pice_source_location = pice_source_location;
    }

    public int getMove_destination() {
        return move_destination;
    }

    public void setMove_destination(int move_destination) {
        this.move_destination = move_destination;
    }

    public int getMove_source_location() {
        return move_source_location;
    }

    public void setMove_source_location(int move_source_location) {
        this.move_source_location = move_source_location;
    }

    public int getNumber_destination() {
        return number_destination;
    }

    public void setNumber_destination(int number_destination) {
        this.number_destination = number_destination;
    }

    public int getNumber_source_location() {
        return number_source_location;
    }

    public void setNumber_source_location(int number_source_location) {
        this.number_source_location = number_source_location;
    }
    
}
