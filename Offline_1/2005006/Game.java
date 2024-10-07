import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Game {
    public static boolean isSolvable(Board initial,int boardSize){
        int inversions = 0;
        int[] linearArray = new int[boardSize*boardSize];
        int k = 0;
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                linearArray[k++] = initial.currentBoard[i][j];
            }
        }

        for(int i = 0; i < linearArray.length; i++){
            if(linearArray[i] == '*'){
                continue;
            }
            for(int j = i+1; j < linearArray.length; j++){
                if((linearArray[j] != '*')&&(linearArray[i] > linearArray[j])){
                    inversions++;
                }
            }
        }

        if(boardSize % 2 == 1){
            boolean decision = (inversions % 2 == 0);
            if(!decision){
                System.out.println("\nThe board size is odd and the number of inversions is odd.\nHence, the puzzle instance is not solvable!!!");
            }
            return decision;
        }
        else{
            int blankRowFromBottom = boardSize - initial.blankIndex.i;
            boolean decision = false;
            if(blankRowFromBottom % 2 == 0){
                if(inversions % 2 == 0){
                    System.out.println("\nThe board size is even, the blank is in an even row and the number of inversions is even.\nHence, the puzzle instance is not solvable!!!");
                    decision = false;

                }
                else{
                    decision = true;
                }
            }
            else if((blankRowFromBottom % 2 == 1)){
                if(inversions % 2 == 1){
                    System.out.println("\nThe board size is even, the blank is in an odd row and the number of inversions is odd.\nHence, the puzzle instance is not solvable!!!");
                    decision = false;
                }
                else{
                    decision = true;
                }
            }
            return decision;
        }

    }

    public static int[][] copy(int[][] board, int boardSize){
        int[][] newBoard = new int[boardSize][boardSize];
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

    public static boolean contains(ArrayList<Board> list, int[][] board){
        boolean mismatch = false;
        for(Board b : list){
            mismatch = false;
            for(int i = 0; i< b.currentBoard.length; i++){
                for(int j = 0; j < b.currentBoard.length; j++){
                    if(b.currentBoard[i][j] != board[i][j]){
                        mismatch = true;
                        break;
                    }
                }
                if(mismatch){
                    break;
                }
            }
            if(!mismatch){
                return true;
            }
        }
        return false;
    }

    public static int generateChildBoards(PriorityQueue<Board> openList, ArrayList<Board> closedList, Board current, int boardSize){
        int blank_i = current.blankIndex.i;
        int blank_j = current.blankIndex.j;
        int[][] newBoard = copy(current.currentBoard, boardSize);
        int nAddedNodes  = 0;
        //Move blank space up
        if(blank_i - 1 >= 0){
            newBoard[blank_i][blank_j] = newBoard[blank_i - 1][blank_j];
            newBoard[blank_i - 1][blank_j] = '*';
            if(!contains(closedList, newBoard)){
                Board child = new Board(current,new Index(blank_i - 1, blank_j), newBoard, current.goalBoard);
                openList.add(child);
                nAddedNodes++;
            }
            //Revert the changes
            newBoard[blank_i - 1][blank_j] = newBoard[blank_i][blank_j];
            newBoard[blank_i][blank_j] = '*';
        }

        //Move blank space down
        if(blank_i + 1 < boardSize){
            newBoard[blank_i][blank_j] = newBoard[blank_i + 1][blank_j];
            newBoard[blank_i + 1][blank_j] = '*';
            if(!contains(closedList, newBoard)){
                Board child = new Board(current, new Index(blank_i + 1, blank_j) , newBoard, current.goalBoard);
                openList.add(child);
                nAddedNodes++;
            }
            //Revert the changes
            newBoard[blank_i + 1][blank_j] = newBoard[blank_i][blank_j];
            newBoard[blank_i][blank_j] = '*';
        }

        //Move blank space left
        if(blank_j - 1 >= 0){
            newBoard[blank_i][blank_j] = newBoard[blank_i][blank_j - 1];
            newBoard[blank_i][blank_j - 1] = '*';
            if(!contains(closedList, newBoard)){
                Board child = new Board(current, new Index(blank_i, blank_j - 1), newBoard, current.goalBoard);
                openList.add(child);
                nAddedNodes++;
            }
            //Revert the changes
            newBoard[blank_i][blank_j - 1] = newBoard[blank_i][blank_j];
            newBoard[blank_i][blank_j] = '*';
        }

        //Move blank space right
        if(blank_j + 1 < boardSize){
            newBoard[blank_i][blank_j] = newBoard[blank_i][blank_j + 1];
            newBoard[blank_i][blank_j + 1] = '*';
            if(!contains(closedList, newBoard)){
                Board child = new Board(current, new Index(blank_i, blank_j + 1), newBoard, current.goalBoard);
                openList.add(child);
                nAddedNodes++;
            }
            //Revert the changes
            newBoard[blank_i][blank_j + 1] = newBoard[blank_i][blank_j];
            newBoard[blank_i][blank_j] = '*';
        }

        return nAddedNodes;

    }

    public static void printBoard(int[][] board){
        System.out.println();
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] == '*'){
                    System.out.print("* ");
                }
                else{
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printPath(Board current){
        ArrayList<Board> path = new ArrayList<>();
        while(current != null){
            path.add(current);
            current = current.prevBoard;
        }
        Collections.reverse(path);
        for(Board b : path){
            printBoard(b.currentBoard);
        }
    }

    public static void gameInstance(Index initialBlankIndex, String method, int[][] initialBoard, int[][] goalBoard){
        Board initial = new Board(initialBlankIndex, method, initialBoard, goalBoard);
        if(!Game.isSolvable(initial,initialBoard.length)){
            System.exit(0);
        }
        //Used to store the boards in the ascending order of their cost
        Comparator<Board> comparator = new Comparator<Board>(){
            @Override
            public int compare(Board b1, Board b2){
                return b1.cost - b2.cost;
            }
        };
        int nExploredNodes = 0;
        int nExpandedNodes = 0;
        PriorityQueue<Board> openList = new PriorityQueue<>(comparator);
        ArrayList<Board> closedList = new ArrayList<>();
        openList.add(initial);
        nExploredNodes++;
        while(true){
            Board current = openList.poll();
            nExpandedNodes++;
            if(current.getHeuristic() == 0){
                nExpandedNodes--; //The goal board is not expanded
                closedList.add(current);
                System.out.println("\nGoal board reached!!!");
                if(method.equalsIgnoreCase("ham")){
                    System.out.println("Method : Hamming Distance");
                }
                else if(method.equalsIgnoreCase("man")){
                    System.out.println("Method : Manhattan Distance");
                }
                System.out.println("Number of moves: " + current.nMoves);
                System.out.println("Optimal cost : "+current.cost);
                System.out.println("Number of explored nodes: " + nExploredNodes);
                System.out.println("Number of expanded nodes: " + nExpandedNodes);
                System.out.println("\nPath:");
                printPath(current);
                break;
            }
            else{
                closedList.add(current);
                nExploredNodes += generateChildBoards(openList, closedList, current, current.currentBoard.length);
            }
        }

    }
    public static void main(String[] args) {
        int boardSize;
        int[][] initialBoard;
        int[][] goalBoard;
        //Initializing with (0,0), will be changed later
        Index initialBlankIndex = new Index(0, 0);
        boolean hasBlank = false;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the size of the board: ");
        boardSize = sc.nextInt();
        initialBoard = new int[boardSize][boardSize];
        goalBoard = new int[boardSize][boardSize];

        System.out.println("\nEnter the initial board:");
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                String input = sc.next();
                if(input.equals("*")){
                    initialBoard[i][j] = '*';
                    initialBlankIndex.i = i;
                    initialBlankIndex.j = j;
                    hasBlank = true;
                }
                else{
                    initialBoard[i][j] = Integer.parseInt(input);
                }
            }
        }
        if(!hasBlank){
            System.out.println("\nInitial board must have a blank space represented by *");
            System.exit(0);
        }

        //Setting the goal board
        int k = 1;
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                goalBoard[i][j] = k++;
            }
        }
        goalBoard[boardSize - 1][boardSize - 1] = '*';
        sc.close();
        // ham - Hamming Distance method
        // man - Manhattan Distance method
        Game.gameInstance(initialBlankIndex, "ham", initialBoard, goalBoard);
        Game.gameInstance(initialBlankIndex, "man", initialBoard, goalBoard);

    }
}
