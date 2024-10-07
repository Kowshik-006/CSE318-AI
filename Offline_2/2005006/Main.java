public class Main {
    public static void main(String[] args) {
        int startPlayer = Mancala.player1;
        int currentPlayer;
        int depth = 7;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int[] wins = new int[5];
        int[] draws = new int[5];

        int totalHeuristics = 4;
        int totalGamesForEachPair = 100;
        int totalGamesForEachHeuristic = ((totalHeuristics * (totalHeuristics - 1) / 2) * totalGamesForEachPair) / 2;
        // 25 10 15 10
        int w1=25,w2=10,w3=15,w4=10;
        System.out.println(w1 + " " +  w2 + " " + w3 + " " + w4 + "\n");
        for(int i = 1 ; i<totalHeuristics; i++){
            for(int j = i+1 ; j<=totalHeuristics ; j++){
                int win_i = 0;
                int win_j = 0;
                int draw = 0;
                for(int k = 0; k<totalGamesForEachPair; k++){
                    depth++;
                    if(depth > 12){
                        depth = 8;
                    }
                    startPlayer = (startPlayer == Mancala.player1) ? Mancala.player2 : Mancala.player1;
                    int otherPlayer = (startPlayer == Mancala.player1) ? Mancala.player2 : Mancala.player1; 
                    currentPlayer = startPlayer;
                    Mancala game = new Mancala(Mancala.player1, Mancala.player2, startPlayer, otherPlayer, i, j);
                    game.setWN(w1, w2, w3, w4);
                    while(!game.gameOver()){
                        value_index bestMove_pair = game.minimax(currentPlayer, depth, alpha, beta, currentPlayer,true);
                        int bestMove = bestMove_pair.index;
                        // callingPlayer = 0, in order to avoid changing additional moves and stones captured
                        currentPlayer = game.makeMove(currentPlayer, bestMove, 0);
                    }

                    int winner = game.getWinner();

                    if(winner == game.gamePlayer){
                        win_i++;
                    }
                    else if(winner == game.opponent){
                        win_j++;
                    }
                    else{
                        draw++;
                    }
                    
                }

                System.out.println("Heuristic "+i+" wins: " + win_i);
                System.out.println("Heuristic "+j+" wins: " + win_j);
                System.out.println("Heuristic "+i+ " and "+j +" draws: "+ draw);
                System.out.println();
                wins[i] += win_i;
                wins[j] += win_j;
                draws[i] += draw;
                draws[j] += draw;
            }
        }

        System.out.println("Win percentage for each heuristic:");
        for(int i = 1; i<=4; i++){
            float winPercentage = ((float)wins[i] / totalGamesForEachHeuristic) * 100;
            System.out.println("Heuristic "+i+" win percentage: " + winPercentage+"%");
        }

        System.out.println("\nDraw percentage for each heuristic:");
        for(int i = 1; i<=4; i++){
            float drawPercentage = ((float)draws[i] / totalGamesForEachHeuristic) * 100;
            System.out.println("Heuristic "+i+" draw percentage: " + drawPercentage+"%");
        }

        
    }
}
