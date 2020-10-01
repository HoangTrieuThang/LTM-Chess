/**
 * Kiểm tra và khởi tạo các nước có thể đi của từng quân cờ trong bàn cờ.
 */
package mychessmate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 */
public class MoveGenerator {    
    Position position;                                      // Vị trí hiện tại
    int player;                                             // Lượt chơi của X
    List<Position> positions = new ArrayList<Position>();   // Mảng các vị trí có thể di chuyển
    Game gs;
    
    // Khởi tạo đường đi của máy với vị trí hiện tại và 
    public MoveGenerator(Position position, int player){
        this.position = position;
        this.player = player;
        this.gs = new Game(position);
    }
    // Phương thức getPositions để chuyển class thành mảng để
    public Position[] getPositions(){                
        return positions.toArray(new Position[positions.size()]);
    }
    
    //Khởi tạo 
    public void generateMoves(){
        if(player == GameData.HUMAN){                           // Nểu player là người chơi thì
            for(int i=1; i<position.human_pieces.length; i++){  // Từng quân cờ của người chơi đang sử dụng             
                Piece piece = position.human_pieces[i];         // Khởi tạo con cờ tương ứng tại vị trí I trong mảng HumanPiece
                if(piece == null) continue;                     // Nếu người chơi không có quân tại đây, do lỗi
                switch(piece.value){                            
                    case Piece.PAWN:
                        humanPawnMoves(piece);                  
                        break;
                    case Piece.KNIGHT:
                        humanKnightMoves(piece);
                        break;
                    case Piece.KING:
                        humanKingMoves(piece);
                        break;
                    case Piece.BISHOP:
                        humanBishopMoves(piece);
                        break;
                    case Piece.ROOK:
                        humanRookMoves(piece);
                        break;
                    case Piece.QUEEN:
                        humanQueenMoves(piece);
                }
            }
        }else{                                                      //Nếu người chơi là máy tương tự nhưng gọi hàm tương ứng với máy
            for(int i=1; i<position.computer_pieces.length; i++){
                Piece piece = position.computer_pieces[i];    
                if(piece == null) continue;
                switch(piece.value){
                    case Piece.PAWN:
                        computerPawnMoves(piece);
                        break;
                    case Piece.KNIGHT:
                        computerKnightMoves(piece);
                        break;
                    case Piece.KING:
                        computerKingMoves(piece);
                        break;
                    case Piece.BISHOP:
                        computerBishopMoves(piece);
                        break;
                    case Piece.ROOK:
                        computerRookMoves(piece);
                        break;
                    case Piece.QUEEN:
                        computerQueenMoves(piece);
                }
            }
        }
    }
    
    // Tính toán
    public void humanPawnMoves(Piece pawn){        
        int location = pawn.location;                                               // Lấy vị trị của chốt hiện tại
        int forward_piece_index = position.board[location-10];                      // Lấy vị trí của quân trước mặt
        if(forward_piece_index != GameData.ILLEGAL){                                // Nếu không phải vị trí cấm k trong bàn cờ
            if(forward_piece_index == GameData.EMPTY && gs.safeMove(GameData.HUMAN,location,location-10)) {
                // Nếu vị trí phía trước trống và di chuyển không làm tướng bị chiếu (di chuyển an toàn)
                
                // Thêm vị trí mới vào danh sách
                positions.add(new Position(position,new Move(location,location-10)));
            }
        }
        
        // Nếu vị trí quân chốt nằm ở vị trí đầu chưa đi và phía trước 2 ô không có quân ngáng đường thì di chuyển lên
        if(location > 80 && forward_piece_index == GameData.EMPTY && 
                position.board[location-20] == GameData.EMPTY && gs.safeMove(GameData.HUMAN,location,location-20)) {            
                positions.add(new Position(position,new Move(location,location-20)));
        }
        
        // Lấy vị trí của quân cờ bên phải trước mặt
        int right_piece_index = position.board[location-9];
        
        // Nếu không phải nằm ngoài bàn cờ
        if(right_piece_index != GameData.ILLEGAL) {
            // Kiểm tra nếu là quân của đối phương (cpu) và di chuyển an toàn thì thêm vào vị trí mới
            if(right_piece_index<0 && gs.safeMove(GameData.HUMAN,location,location-9))
                positions.add(new Position(position,new Move(location,location-9)));
        }
        
        // Lấy giá trị quân cờ nằm bên trên phải và kiểm tra tương tự ở trên
        int left_piece_index = position.board[location-11];
        if(left_piece_index != GameData.ILLEGAL) {
            if(left_piece_index<0 && gs.safeMove(GameData.HUMAN,location,location-11))
                positions.add(new Position(position,new Move(location,location-11)));
        }        
    }    
    
    // Tương tự như HumanPawnMoves, khác chỉ số ngược lại, xoay ngược bàn cờ
    public void computerPawnMoves(Piece pawn){      
        int location = pawn.location;
        int forward_piece_index = position.board[location+10];
        if(forward_piece_index != GameData.ILLEGAL){
            if(forward_piece_index == GameData.EMPTY && gs.safeMove(GameData.COMPUTER,location,location+10)){ 
                positions.add(new Position(position,new Move(location,location+10)));
            }
        }
        
        if(location < 39 && forward_piece_index == GameData.EMPTY && 
                position.board[location+20] == GameData.EMPTY && gs.safeMove(GameData.COMPUTER,location,location+20)) {            
                positions.add(new Position(position,new Move(location,location+20)));
        }
        
        int right_piece_index = position.board[location+11];
        if(right_piece_index != GameData.ILLEGAL) {
            if(right_piece_index>0 && right_piece_index != GameData.EMPTY &&
                    gs.safeMove(GameData.COMPUTER,location,location+11))
                positions.add(new Position(position,new Move(location,location+11)));
        }
        int left_piece_index = position.board[location+9];
        if(left_piece_index != GameData.ILLEGAL) {
            if(left_piece_index>0 && left_piece_index != GameData.EMPTY &&
                    gs.safeMove(GameData.COMPUTER,location,location+9))
                positions.add(new Position(position,new Move(location,location+9)));
        }        
    }    
    
    // Kiểm tra và thay đổi vị trí của quân Mã trong bàn cờ khi di chuyển
    public void humanKnightMoves(Piece knight){
        int location = knight.location;
        int[] destinations = {location-21,location+21,location+19,location-19,
            location-12,location+12,location-8,location+8};
        for(int i=0; i<destinations.length; i++){
            int des_piece_index = position.board[destinations[i]];
            if(des_piece_index != GameData.ILLEGAL && (des_piece_index == GameData.EMPTY || des_piece_index<0)
                     && gs.safeMove(GameData.HUMAN,location,destinations[i]))
                positions.add(new Position(position,new Move(location,destinations[i])));
        }
    }
    
    
    public void computerKnightMoves(Piece knight){
        int location = knight.location;
        int[] destinations = {location-21,location+21,location+19,location-19,
            location-12,location+12,location-8,location+8};
        for(int i=0; i<destinations.length; i++){
            int des_piece_index = position.board[destinations[i]];
            if(des_piece_index != GameData.ILLEGAL && (des_piece_index == GameData.EMPTY || des_piece_index>0) &&
                    gs.safeMove(GameData.COMPUTER,location,destinations[i])){
                positions.add(new Position(position,new Move(location,destinations[i])));
            }
        }
    }
    
    // Kiểm tra và di chuyển Quân vua
    public void humanKingMoves(Piece king){
        int location = king.location;                                       // Lấy vị trí quân vua
        int[] destinations = {location+1,location-1,location+10,location-10,
            location+11,location-11,location+9,location-9};                 // Lấy các điểm đến có thể
        for(int i=0; i<destinations.length; i++){
            int des_piece_index = position.board[destinations[i]];          // Lấy giá trị tại ô kiểm tra xem có gì ở đó
            // Nếu là vị trí trong bàn cờ, (vị trí đó trống, hoặc là quân địch) và di chuyển k bị chiếu thì thêm vị trí di chuyển đó vào
            if(des_piece_index != GameData.ILLEGAL && (des_piece_index == GameData.EMPTY || des_piece_index<0)
                    && gs.safeMove(GameData.HUMAN,location,destinations[i])){
                positions.add(new Position(position,new Move(location,destinations[i])));
            }
        }
    }
    public void computerKingMoves(Piece king){
        int location = king.location;
        int[] destinations = {location+1,location-1,location+10,location-10,
            location+11,location-11,location+9,location-9};
        for(int i=0; i<destinations.length; i++){
            int des_piece_index = position.board[destinations[i]];
            if(des_piece_index != GameData.ILLEGAL && (des_piece_index == GameData.EMPTY || des_piece_index>0) &&
                    gs.safeMove(GameData.COMPUTER,location,destinations[i])){
                positions.add(new Position(position,new Move(location,destinations[i])));
            }
        }
    }
    
    //Kiểm tra và di chuyển quân Tượng
    public void humanBishopMoves(Piece bishop){
        int location = bishop.location;                 // lấy vị trí quân TƯợng trên bàn cờ
        int[] deltas = {11,-11,9,-9};                   // Lấy hiệu số di chuyển để lấy vị trí trên đường chéo
        for (int i = 0; i < deltas.length; i++) {       // Kiểm tra từng vị trí trên đường chéo từ vị trí di chuyển đến điểm đến
            int des = location + deltas[i];             // Tạo điểm đến là ô chéo
            while (true) {                              // tạo vòng lặp để 
                int des_piece_index = position.board[des];
                if (des_piece_index == GameData.ILLEGAL) {  // nếu dích đến ra khỏi bàn cờ thì dừng
                    break;
                }
                boolean safe_move = gs.safeMove(GameData.HUMAN,location,des);       // biến kiểm tra nếu di chuyển đến vị trí x thì có an toàn cho vua k
                if (des_piece_index == GameData.EMPTY || des_piece_index < 0){      // Nếu là trống hoặc quân dịch thì
                    if(safe_move){                                                  // nếu vẫn an toàn cho vua 
                        positions.add(new Position(position,new Move(location,des)));
                        if (des_piece_index != GameData.EMPTY || !safe_move) {                        
                            break;
                        }
                    }else if(des_piece_index != GameData.EMPTY) break;
                } else if(des_piece_index>0 && des_piece_index != GameData.EMPTY){
                    break;
                }
                des += deltas[i];
            }
        }
    }
    public void computerBishopMoves(Piece bishop){
        int location = bishop.location;
        int[] deltas = {11,-11,9,-9};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == GameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = gs.safeMove(GameData.COMPUTER,location,des);
                if (des_piece_index == GameData.EMPTY || des_piece_index > 0) {
                    if(safe_move){
                        positions.add(new Position(position,new Move(location,des)));
                        if (des_piece_index != GameData.EMPTY || !safe_move) {                        
                            break;
                        }
                    }else if(des_piece_index != GameData.EMPTY) break;
                } else if(des_piece_index<0){
                    break;
                }
                des += deltas[i];
            }
        }
    }
    public void humanRookMoves(Piece rook){
        int location = rook.location;
        int[] deltas = {1,-1,10,-10};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == GameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = gs.safeMove(GameData.HUMAN,location,des);
                if (des_piece_index == GameData.EMPTY || des_piece_index < 0) {
                    if(safe_move){
                        positions.add(new Position(position,new Move(location,des)));
                        if (des_piece_index != GameData.EMPTY) {          
                            break;
                        }
                    }else if(des_piece_index != GameData.EMPTY) break;
                } else if(des_piece_index>0 && des_piece_index != GameData.EMPTY){
                    break;
                }
                des += deltas[i];
            }
        }
    }
    public void computerRookMoves(Piece rook){
        int location = rook.location;
        int[] deltas = {1,-1,10,-10};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];           
            while (true) {
                 int des_piece_index = position.board[des];
                if (des_piece_index == GameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = gs.safeMove(GameData.COMPUTER,location,des);
                if (des_piece_index == GameData.EMPTY || des_piece_index > 0) {
                    if(safe_move){
                        positions.add(new Position(position,new Move(location,des)));
                        if (des_piece_index != GameData.EMPTY) {                        
                            break;
                        }
                    }else if(des_piece_index != GameData.EMPTY) break;
                } else if(des_piece_index<0){
                    break;
                }
                des += deltas[i];
            }
        }
    }
    public void humanQueenMoves(Piece queen){
        int location = queen.location;
        int[] deltas = {1,-1,10,-10,11,-11,9,-9};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == GameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = gs.safeMove(GameData.HUMAN,location,des);
                if (des_piece_index == GameData.EMPTY || des_piece_index < 0) {
                    if(safe_move){
                        positions.add(new Position(position,new Move(location,des)));
                        if (des_piece_index != GameData.EMPTY) {                        
                            break;
                        }
                    }else if(des_piece_index != GameData.EMPTY) break;
                } else if(des_piece_index>0 && des_piece_index != GameData.EMPTY){
                    break;
                }
                des += deltas[i];
            }
        }
    }
    public void computerQueenMoves(Piece queen){
        int location = queen.location;
        int[] deltas = {1,-1,10,-10,11,-11,9,-9};
        for (int i = 0; i < deltas.length; i++) {
            int des = location + deltas[i];            
            while (true) {
                int des_piece_index = position.board[des];
                if (des_piece_index == GameData.ILLEGAL) {
                    break;
                }
                boolean safe_move = gs.safeMove(GameData.COMPUTER,location,des);
                if (des_piece_index == GameData.EMPTY || des_piece_index > 0) {
                    if(safe_move){
                        positions.add(new Position(position,new Move(location,des)));
                        if (des_piece_index != GameData.EMPTY) {                        
                            break;
                        }
                    }else if(des_piece_index != GameData.EMPTY) break;
                } else if(des_piece_index<0){
                    break;
                }
                des += deltas[i];
            }
        }
    }
}
