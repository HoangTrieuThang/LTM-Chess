/**
 * Khởi tạo sẵn các con cờ với số điểm tham khảo tại "https://en.wikipedia.org/wiki/Chess_piece_relative_value"
 * Một quân cờ có giá trị, vị trí và lưu lại đã từng di chuyển hay chưa
 */
package mychessmate;

import java.io.Serializable;

/**
 * 
 * 
 */


public class Piece implements Serializable{
    // Đặt số điểm chỉ khởi tạo trong class chỉ lần đầu chạy chương trình gọi
    public final static int PAWN = 100;                        // Số điểm của chốt
    public final static int KNIGHT = 320;                      //
    public final static int BISHOP = 333;                      //
    public final static int ROOK = 520;                        //
    public final static int QUEEN = 960;                       //
    public final static int KING =  350;//1000000;             //      
    int value;                                                 //
    int location;                                              //
    boolean has_moved;                                         //
    
    //Initialization the Piece with Location and value(the piece) in new game
    public Piece(int value,int location){                   
        this(value,location,false);
    }
    
    //Initialization the Piece with location and hasMoved. check the piece has move
    public Piece(int value,int location,boolean hasMoved){
        this.value = value;
        this.location = location;
        this.has_moved = hasMoved;
    }
    
    //
    @Override
    public Piece clone(){
        return new Piece(value,location,has_moved);
    }
    public int getLocation(){
        return location;
    }
}
