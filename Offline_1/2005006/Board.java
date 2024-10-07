public class Board{
    int nMoves;
    Index blankIndex;
    Board prevBoard;
    int[][] currentBoard;
    int[][] goalBoard;
    String method;
    int cost;

    public Board(Board prevBoard,Index blankIndex, int[][] currentBoard,int[][] goalBoard){
        this.prevBoard = prevBoard;
        this.blankIndex = blankIndex;
        this.currentBoard = copy(currentBoard, currentBoard.length);
        this.goalBoard = goalBoard;
        this.method = this.prevBoard.method;
        this.nMoves = this.prevBoard.nMoves + 1;
        this.cost = this.nMoves + this.getHeuristic();
    }

    public Board(Index blankIndex, String method, int[][] currentBoard, int[][] goalBoard){
        this.currentBoard = copy(currentBoard, currentBoard.length);
        this.nMoves = 0;
        this.prevBoard = null;
        this.goalBoard = goalBoard;
        this.blankIndex = blankIndex;
        this.method = method;
        this.cost = this.getHeuristic();
    }

    public int[][] copy(int[][] board, int boardSize){
        int[][] newBoard = new int[boardSize][boardSize];
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

    int getHeuristic(){
        if(method.equalsIgnoreCase("ham")){
            return getHammingDistance();
        }
        else if(method.equalsIgnoreCase("man")){
            return getManhattanDistance();
        }
        else{
            throw new IllegalArgumentException("Invalid heuristic method!!!");
        }
    }

    Index getGoalBoardIndex(int value){
        for(int i = 0; i < goalBoard.length; i++){
            for(int j = 0; j < goalBoard[i].length; j++){
                if(goalBoard[i][j] == value){
                    return new Index(i,j);
                }
            }
        }
        return null;
    }

    int getManhattanDistance(){
        int distance = 0;
        for(int i = 0; i < currentBoard.length; i++){
            for(int j = 0; j < currentBoard.length; j++){
                if(currentBoard[i][j] != '*'){
                    Index goalIndex = getGoalBoardIndex(currentBoard[i][j]);
                    distance += Math.abs(i - goalIndex.i) + Math.abs(j - goalIndex.j);
                } 
            }
        }

        return distance;
    }

    int getHammingDistance(){
        int distance = 0;
        for(int i = 0; i < currentBoard.length; i++){
            for(int j = 0; j < currentBoard[i].length; j++){
                if((currentBoard[i][j] != '*')&&(currentBoard[i][j] != goalBoard[i][j])){
                    distance++;
                }
            }
        }
        return distance;
    }


}

