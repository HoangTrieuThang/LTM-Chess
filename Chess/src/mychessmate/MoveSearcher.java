package mychessmate;

public class MoveSearcher {
    MyChessmate chessmate;      // Toàn bộ game
    int level;                  // Level của computer
    
    // Init
    public MoveSearcher(MyChessmate chessmate){
        this.chessmate = chessmate;
    }
    
    //Trả ra vị trí tốt nhất có thể tìm ra 
    public Position alphaBeta(int player, Position position, int alpha, int beta, int depth){
        if(depth == 0) return position;                                     // Nếu 
        Position best_position = null;                                      // Khởi tạo best_position để có thể return
        MoveGenerator move_generator = new MoveGenerator(position,player);  // Khởi tạo kiểm tra nước đi có thể
        move_generator.generateMoves();                                     // Tạo danh sách các nước có thể đi của Player
        Position[] positions = move_generator.getPositions();               // Lấy danh sách các nước đi đó kiểu mảng
        if(positions.length == 0) return position;                          // Nếu không còn nước đi nào khác thì trả về vị trí hiện tại
            
        Evaluator evaluator = new Evaluator();                              // Tạo mới một biến tính điểm mới
        for(Position _position:positions){                                     
            if(best_position == null) best_position = _position;            // Khởi tạo vị trí tốt nhất tiếp theo là _position
            if(player == GameData.HUMAN){
                Position opponent_position = alphaBeta(GameData.COMPUTER,_position,alpha,beta,depth-1);    // Tính toán đường đi chống lại nước vừa đi            
                int score = evaluator.evaluate(opponent_position);          // lấy điểm
                //if(score>=beta && level > 4) return _position;
                if(score>alpha){                                            // Nếu điểm đi lượt này của người chơi tốt hơn
                    best_position = _position;                              // thì lưu lại điểm tốt nhất và lấy vị trị tốt nhất
                    alpha = score;
                }
            }else{
                Position opponent_position = alphaBeta(GameData.HUMAN,_position,alpha,beta,depth-1); // Tìm kiếm  đến khi nào check được                
                if(new Game(opponent_position).isChecked(GameData.HUMAN)){ // Nếu nước đi đó chiếu được tướng thì trả ra ngay
                    return _position;
                }
                int score = evaluator.evaluate(opponent_position);          // tính điểm 
                if(score<=alpha && level > 4) return _position;             // 
                if(score<beta){                                             // Nếu điểm âm hơn. tốt hơn thì lưu lại
                    best_position = _position;
                    beta = score;
                }              
            }
        }
        return best_position;
    }
}
