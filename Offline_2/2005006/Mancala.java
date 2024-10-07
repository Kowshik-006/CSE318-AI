import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Mancala{
    public static int nBins = 6;
    public static int nStones = 4;
    public static int player1 = 1;
    public static int player2 = 2;
    // public int maxPlayer;
    // public int minPlayer;
    public int gamePlayer;
    public int opponent;
    public static int storageP1 = nBins;
    public static int storageP2 = 2*nBins + 1; 
    public int heuristic1;
    public int heuristic2;
    public int W1;
    public int W2;
    public int W3;
    public int W4;
    public int additionalMoves;
    public int stonesCaptured;
    // nBins on both top and bottom and 2 storages
    public static int[] board = new int[2*nBins + 2];

    public Mancala(int gamePlayer, int opponent, int maxPlayer, int minPlayer, int heuristic1, int heuristic2){
        //Initializing the board
        for(int i = 0; i < nBins; i++){
            board[i] = nStones;
            board[nBins + 1 + i] = nStones;
        }

        this.gamePlayer = gamePlayer;
        this.opponent = opponent;
        // this.maxPlayer = maxPlayer;
        // this.minPlayer = minPlayer;
        this.heuristic1 = heuristic1;
        this.heuristic2 = heuristic2;
        additionalMoves = 0;
        stonesCaptured = 0;
    }
    public void setWN(int W1, int W2, int W3, int W4){
        this.W1 = W1;
        this.W2 = W2;
        this.W3 = W3;
        this.W4 = W4;
    }



    boolean gameOver(){
        int sumP1 = 0;
        int sumP2 = 0;
        for(int i = 0; i<nBins; i++){
            sumP1 += board[i];
            sumP2 += board[nBins + 1 + i];
        }

        if(sumP1 == 0){
            for(int i = 0; i<nBins; i++){
                board[nBins + 1 + i] = 0;
            }
            board[storageP2] += sumP2;
            return true;
        }
        if(sumP2 == 0){
            for(int i = 0; i<nBins; i++){
                board[i] = 0;
            }
            board[storageP1] += sumP1;
            return true;
        }
        return false;
    }

    int getScore(int callingPlayer){
        int selector = (callingPlayer == player1) ? 1 : -1;
        int heuristic;
        if(callingPlayer == gamePlayer){
            heuristic = heuristic1;
        }
        else{
            heuristic = heuristic2;
        }

        if(heuristic == 2){
            int sumP1 = 0;
            int sumP2 = 0;
            for(int i = 0; i<nBins; i++){
                sumP1 += board[i];
                sumP2 += board[nBins + 1 + i];
            }            
            return selector*(W1*(board[storageP1]- board[storageP2]) + W2*(sumP1  - sumP2));            
        }

        if(heuristic == 3){
            int sumP1 = 0;
            int sumP2 = 0;
            for(int i = 0; i<nBins; i++){
                sumP1 += board[i];
                sumP2 += board[nBins + 1 + i];
            }
            int result = selector*(W1*(board[storageP1]- board[storageP2]) + W2*(sumP1  - sumP2)) + W3*additionalMoves;
            return result;
        }

        if(heuristic == 4){
            int sumP1 = 0;
            int sumP2 = 0;
            for(int i = 0; i<nBins; i++){
                sumP1 += board[i];
                sumP2 += board[nBins + 1 + i];
            }
            int result = selector*(W1*(board[storageP1]- board[storageP2]) + W2*(sumP1  - sumP2)) + W3*additionalMoves + W4*stonesCaptured;
            return result;
        }
        
        // heuristic == 1
        // default heuristic
        return selector*(board[storageP1] - board[storageP2]);

    }

    int makeMove(int player, int bin, int callingPlayer){
        int stones = board[bin];
        board[bin] = 0; //emptying the bin
        int nextBin = bin;

        while(stones > 0){
            nextBin = (nextBin + 1)%(board.length);
            if(player == player1 && nextBin == storageP2){
                continue;
            }
            else if(player == player2 && nextBin == storageP1){
                continue;
            }
            else{
                board[nextBin]++;
                stones--;
            }
        }

        // nextBin has the index of the bin the last stone has fallen into
        // Last stone has fallen into the player's own storage, hence another move is rewarded
        if(player == player1 && nextBin == storageP1){
            if(player == callingPlayer){
                additionalMoves++;
            }
            return player1;
        }
        if(player == player2 && nextBin == storageP2){
            if(player == callingPlayer){
                additionalMoves++;
            }
            return player2;
        }

        // Last stone has fallen into an empty bin on the player's own half 
        // The player get to put that stone along with the stones present in the opposite bin in his storage
        if(player == player1 && nextBin < storageP1 && board[nextBin] == 1 && board[storageP1 + (storageP1 - nextBin)] != 0){
            int captured = board[nextBin] + board[storageP1 + (storageP1 - nextBin)];
            board[storageP1] += captured;

            if(player == callingPlayer){
                stonesCaptured += captured;
            }

            board[nextBin] = 0;
            board[storageP1 + (storageP1 - nextBin)] = 0;
            // No extra move rewarded in this case
            return player2;
        }
        if(player == player2 && nextBin < storageP2 && board[nextBin] == 1 && board[storageP1 - (nextBin - storageP1)] != 0){
            int captured = board[nextBin] + board[storageP1 - (nextBin - storageP1)];
            board[storageP2] += captured;

            if(player == callingPlayer){
                stonesCaptured += captured;
            }

            board[nextBin] = 0;
            board[storageP1 - (nextBin - storageP1)] = 0;
            return player1;
        }

        return player == player1 ? player2 : player1;
    }

    private int getStartBin(int player){
        if(player == player1){
            return 0;
        }
        return storageP1+1;
    }

    private Integer[] randomIndices(int startBin){
        Integer[] indices = new Integer[nBins];
        for(int i = 0; i < nBins; i++){
            indices[i] = startBin + i;
        }

        List<Integer> indicesList = Arrays.asList(indices);

        Collections.shuffle(indicesList);
        indicesList.toArray(indices);
        return indices;
    }
    value_index minimax(int player,int depth,int alpha, int beta, int callingPlayer, boolean isMaxPlayer){
        // depth == 0 indicates a leaf node
        if(depth == 0 || gameOver()){
            int score = getScore(callingPlayer);
            additionalMoves = 0;
            stonesCaptured = 0;
            return new value_index(score, -1);    
        }

        if(isMaxPlayer){
            int maxVal = Integer.MIN_VALUE;
            int maxValIndex = -1;
            int startBin = getStartBin(player);
            Integer[] indices = randomIndices(startBin);
            
            for(int i : indices){
                if(board[i] == 0){
                    continue;
                }

                // We are trying to find the best move.
                // Thus, we are testing each move on the same board configuration.
                int[] boardCopy = board.clone();

                int nextPlayer = makeMove(player, i, callingPlayer);
                value_index result = minimax(nextPlayer, depth-1, alpha, beta, callingPlayer, player == nextPlayer);
                int val = result.value;

                if(maxVal < val){
                    maxVal = val;
                    maxValIndex = i;
                }
                alpha = Math.max(alpha, val);

                board = boardCopy;

                // If the condition below is true, that indicates that, 
                // already a better path exists than what the new path can provide.
                // Hence, we don't need to traverse through that path
                if(alpha >= beta){
                    break;
                }
            }

            return new value_index(maxVal, maxValIndex);
        }

        else{
            int minVal = Integer.MAX_VALUE;
            int minValIndex = -1;
            int startBin = getStartBin(player);

            Integer[] indices = randomIndices(startBin);

            // for(int i = startBin; i < startBin + nBins; i++){
            for(int i : indices){
                if(board[i] == 0){
                    continue;
                }

                int[] boardCopy = board.clone();

                int nextPlayer = makeMove(player,i, callingPlayer);
                value_index result = minimax(nextPlayer, depth-1, alpha, beta, callingPlayer, player != nextPlayer);
                int val = result.value;

                if(val < minVal){
                    minVal = val;
                    minValIndex = i;
                }
                beta = Math.min(beta, val);

                board = boardCopy;

                if(alpha >= beta){
                    break;
                }
            }

            return new value_index(minVal, minValIndex);
        }
    }

    void printBoard(){
        System.out.println("\n\t\t\t\t\t\t\tPlayer 2");
        System.out.print("___________________________________________________________________________"); 
        System.out.println("___________________________________________________________________________\n"); 
        System.out.print("Mancala_P2:\t" + board[storageP2] + "\t|\t");
        for(int i = 2*nBins; i > nBins; i--){
            System.out.print(board[i] + "\t|\t");
        }
        System.out.println();
        System.out.print("\t\t\t|\t");
        for(int i = 0; i < nBins; i++){
            System.out.print(board[i] + "\t|\t");
        }
        System.out.println("Mancala_P1:\t" + board[storageP1]);
        System.out.print("___________________________________________________________________________"); 
        System.out.println("___________________________________________________________________________\n"); 
        System.out.println("\t\t\t\t\t\t\tPlayer 1\n");
    }

    void printResult(){
        System.out.println("\nFinal Score:\n");
        System.out.println("Player 1: " + board[storageP1]);
        System.out.println("Player 2: " + board[storageP2]);

        if(board[storageP1] > board[storageP2]){
            System.out.println("\nPlayer 1 wins!\n");
        }
        else if(board[storageP1] < board[storageP2]){
            System.out.println("\nPlayer 2 wins!\n");
        }
        else{
            System.out.println("\nIt's a draw!\n");
        }
    }

    int getWinner(){
        if(board[storageP1] > board[storageP2]){
            return player1 == gamePlayer ? gamePlayer : opponent;
        }
        else if(board[storageP1] < board[storageP2]){
            return player2 == gamePlayer ? gamePlayer : opponent;
        }
        else{
            return 0;
        }
    }

}