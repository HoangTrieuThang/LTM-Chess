/**
 * Kiểm tra game hiện tại với các vị trí trên bàn cờ và 2 quân vua
 * Kiểm tra về tính bị chiếu của quân vua
 */
package mychessmate;

/**
 *
 * 
 */
public class Game {          
    Position position;                                      //         
    Piece human_king;                                       //
    Piece computer_king;
    
    public Game(Position position){
        human_king = position.human_pieces[8];              // Lấy quân cờ King
        computer_king = position.computer_pieces[8]; 
        this.position = position;
    }
    
    // Kết quả của nước đi (bị chiếu hay không)
    public int getResult(int player){
        int state = -1;
        MoveGenerator mg = new MoveGenerator(position,player);  // Khởi tạo 1 bàn cờ với các giá trị
        mg.generateMoves();                                     //
        Position[] positions = mg.getPositions();       // Lấy position kiểu mảng từ kiểu arraylist               
        if(positions.length == 0){
            if(isChecked(player)) {
                state = GameData.CHECKMATE;
            }
            else state = GameData.DRAW;                             
        }
        return state;
    }    
    
    //Kiểm tra bước đi chuyển của người chơi từ vị trí source 
    //đển destination có để lộ mặt tướng cho quân địch chiểu không
    public boolean safeMove(int player, int source,int destination){
        Move _move = new Move(source,destination);              // Tạo nước đi mới
        Position _position = new Position(position,_move);      // Tạo mới một bàn cờ và vị trị vừa đi
        Game gs = new Game(_position);                          // Bắt đầu game với bàn cờ đã tạo
        return !gs.isChecked(player);                           // Trả ra người chơi có bị check khi đi quân tại vị trí Move to
    }
    
    // Kiểm tra bị chiếu bởi con gì, Nếu bị chiếu thì trả ra true
    public boolean isChecked(int player){
        boolean checked = false;
        Piece king = (player == GameData.HUMAN) ? human_king : computer_king;
        if(king == null) return false;
        checked = checkedByPawn(king);
        if(!checked) checked = checkedByKnight(king);
        if(!checked) checked = checkedByBishop(king);
        if(!checked) checked = checkedByRook(king);
        if(!checked) checked = checkedByQueen(king);
        if(!checked) checked = desSquareAttackedByKing(king);       
        return checked;
    }
    
    // Kiểm xem có bị chiếu bởi con chốt hay không
    private boolean checkedByPawn(Piece king){
        boolean checked = false;   
        int location = king.location;                               // lấy vị trí của quân vua trên board
        if(king == human_king){                                     // Nếu là vua của người chơi
            int right_square = position.board[location-9];          // Lấy giá trị 2 ô vuông chéo trước mặt
            int left_square = position.board[location-11];
            if (right_square != GameData.ILLEGAL) {  // Nếu vị trí đó không đúng giá trị thì bỏ qua
                if (right_square < 0 && position.computer_pieces[-right_square].value == Piece.PAWN)// Nếu là giá trị âm (quân dịch) và có giá trị là quân chốt thì bị chiếu
                {
                    checked = true;
                }
            }
            if (left_square != GameData.ILLEGAL) {
                if (left_square < 0 && position.computer_pieces[-left_square].value == Piece.PAWN) {
                    checked = true;
                }
            }
        }else{ // Ngược lại tương tự với computer 
            int right_square = position.board[location+11];
            int left_square = position.board[location+9];
            if(right_square != GameData.ILLEGAL){
                if(right_square>0 && right_square != GameData.EMPTY && 
                        position.human_pieces[right_square].value == Piece.PAWN)
                    checked = true;
            }
            if(left_square != GameData.ILLEGAL){
                if(left_square>0 && left_square != GameData.EMPTY && 
                        position.human_pieces[left_square].value == Piece.PAWN)
                    checked = true;
            }
        }
        return checked;
    }
    
    // Kiểm tra xem có bị chiếu bởi con mã hay không
    private boolean checkedByKnight(Piece king){
        boolean checked = false;
        int location = king.location;
        int[] destinations = {location-21,location+21,location+19,location-19,
            location-12,location+12,location-8,location+8};  // 8 vị trí theo hình đường đi của quân mã tại vị trí King
        for (int destination : destinations) {
            int des_square = position.board[destination]; // Lấy giá trị tại ô đó
            if(des_square == GameData.ILLEGAL) continue;  // Nếu vị trí không nằm trong bàn cờ thì tăng destination
            if (king == human_king) {
                if (des_square < 0 && position.computer_pieces[-des_square].value == Piece.KNIGHT) {
                    checked = true;
                    break;
                }
            } else {
                if (des_square > 0 && des_square != GameData.EMPTY
                        && position.human_pieces[des_square].value == Piece.KNIGHT) {
                    checked = true;
                    break;
                }
            }
        }
        return checked;
    }
    
    //Nếu đi đụng quân vua khác
    private boolean desSquareAttackedByKing(Piece king){
        boolean checked = false;
        int location = king.location;
        int[] destinations = {location+1,location-1,location+10,location-10,
            location+11,location-11,location+9,location-9};   /// 8 vị trí xung quanh
        for(int destination:destinations){
            int des_square = position.board[destination];    // giá trị tại vị trí đang xét
            if(des_square == GameData.ILLEGAL) continue;     // Nếu
            if(king == human_king){                
                if(des_square<0 && position.computer_pieces[-des_square].value == Piece.KING){
                    checked = true;
                    break;
                }
            }else{
                if(des_square>0 && des_square != GameData.EMPTY && 
                        position.human_pieces[des_square].value == Piece.KING){
                    checked = true;
                    break;
                }
            }
        }
        return checked;
    }
    
    // Kiểm tra có bị chiếu bởi quân Tượng không.
    private boolean checkedByBishop(Piece king){
        boolean checked = false;
        int[] deltas = {11,-11,9,-9};
        for(int i=0; i<deltas.length; i++){
            int delta = king.location+deltas[i];
            while(true){
                int des_square = position.board[delta];
                if(des_square == GameData.ILLEGAL) {
                    checked = false;
                    break;
                }
                if(king == human_king){
                    if(des_square<0 && position.computer_pieces[-des_square].value == Piece.BISHOP){
                        checked = true;
                        break;
                    }else if(des_square != GameData.EMPTY) break;
                }else if(king == computer_king){
                    if(des_square>0 && des_square != GameData.EMPTY && 
                            position.human_pieces[des_square].value == Piece.BISHOP){
                        checked = true;
                        break;
                    }else if(des_square != GameData.EMPTY) break;
                }
                delta += deltas[i];
            }
            if(checked) break;
        }
        return checked;
    }    
    
    // Kiểm tra có bị chiếu bởi con xe hay không
    private boolean checkedByRook(Piece king){
        boolean checked = false;
        int[] deltas = {1,-1,10,-10};
        for(int i=0; i<deltas.length; i++){
            int delta = king.location+deltas[i];
            while(true){
                int des_square = position.board[delta];
                if(des_square == GameData.ILLEGAL) {
                    checked = false;
                    break;
                }
                if(king == human_king){
                    if(des_square<0 && position.computer_pieces[-des_square].value == Piece.ROOK){
                        checked = true;
                        break;
                    }else if(des_square != GameData.EMPTY) break;
                }else if(king == computer_king){
                    if(des_square>0 && des_square != GameData.EMPTY && 
                            position.human_pieces[des_square].value == Piece.ROOK){
                        checked = true;
                        break;
                    }else if(des_square != GameData.EMPTY) break;
                }
                delta += deltas[i];
            }
            if(checked) break;
        }
        return checked;
    }    
    
    // Kiểm tra bị chiếu bởi quân Hậu
    private boolean checkedByQueen(Piece king){
        boolean checked = false;
        int[] deltas = {1,-1,10,-10,11,-11,9,-9};
        for(int i=0; i<deltas.length; i++){
            int delta = king.location+deltas[i];
            while(true){
                int des_square = position.board[delta];
                if(des_square == GameData.ILLEGAL) {
                    checked = false;
                    break;
                }
                if(king == human_king){
                    if(des_square<0 && position.computer_pieces[-des_square].value == Piece.QUEEN){
                        checked = true;
                        break;
                    }else if(des_square != GameData.EMPTY) break;
                }else if(king == computer_king){
                    if(des_square>0 && des_square != GameData.EMPTY && 
                            position.human_pieces[des_square].value == Piece.QUEEN){
                        checked = true;
                        break;
                    }else if(des_square != GameData.EMPTY) break;
                }
                delta += deltas[i];
            }
            if(checked) break;
        }
        return checked;
    }
}
