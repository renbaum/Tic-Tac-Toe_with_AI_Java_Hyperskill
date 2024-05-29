package tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Board implements Cloneable{

    private char[][] board;

    public Board() {
        board = new char[3][3];
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                board[i][j] = ' ';
            }
        }
    }
    public Board(String init_setting){
        this();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                char setting = init_setting.charAt(j + i * 3);
                if(setting != 'X' && setting != 'O') setting = ' ';
                board[i][j] = setting;
            }
        }
    }

    public Board clone() {
        try {
            Board board = (Board) super.clone();
            board.board = new char[3][3];
            for(int i = 0; i < 3; i++){
                for (int j = 0; j < 3; j++){
                    board.board[i][j] = this.board[i][j];
                }
            }
            return board;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void show() {
        System.out.println("---------");
        for(int i = 0; i < 3; i++) {
            System.out.printf("| %c %c %c |\n", board[i][0], board[i][1], board[i][2]);
        }
        System.out.println("---------");
    }

    public boolean checkWin(char player){
        // Check each row.
        for (int i = 0; i < 3; ++i)
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;

        // Check each column.
        for (int j = 0; j < 3; ++j)
            if (board[0][j] == player && board[1][j] == player && board[2][j] == player)
                return true;

        // Check the diagonals.
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;

        // No winner.
        return false;
    }

    private int[] countChecks(){
        int[] counts = {0, 0};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                switch(board[i][j]){
                    case 'X':
                        counts[0]++;
                        break;
                    case 'O':
                        counts[1]++;
                        break;
                }
            }
        }
        return counts;
    }

    public int getCurrentUser(){
        int[] counts = countChecks();
        if(counts[0] > counts[1]) return 1;
        return 0;
    }

    public boolean checkBoard(){
        int[] counts = countChecks();
        // if difference bigger than 1 then it is a not possible state
        if(Math.abs(counts[0] - counts[1]) > 1){
            System.out.println("Impossible");
            return false;
        }
        boolean winX = checkWin('X');
        boolean winY = checkWin('O');
        // it can't have two winner at the same time
        if(winX && winY){
            System.out.println("Impossible");
            return false;
        }
        // check for a winner
        if(winX || winY){
            System.out.printf("%c wins\n", winX ? 'X' : 'O');
            return true;
        }
        // check if it is a draw
        if (counts[0] + counts[1] == 9) {
            System.out.println("Draw");
            return true;
        }

        // the game is not yet finished
        // System.out.println("Game not finished");
        return false;
    }


    private boolean checkPosition(int x, int y){
        if (board[x][y] == 'X' || board[x][y] == 'O'){
            return false;
        }
        return true;
    }

    public List<Move> getMoves(){
        List<Move> moves = new ArrayList<Move>();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(checkPosition(i, j)) moves.add(new Move(i, j));
            }
        }
        return moves;
    }

    public int[] getInput(){
        Scanner scanner = new Scanner(System.in);
        int[] input = new int[2];
        do {
            System.out.print("Enter the coordinates:");
            String[] substring = scanner.nextLine().split(" ");
            if(substring.length != 2){
                System.out.println("You should enter numbers!");
                continue;
            }
            try {
                boolean rangeFalse = false;
                for (int i = 0; i < substring.length; i++) {
                    // parse the string to a number
                    input[i] = Integer.parseInt(substring[i]) - 1;
                    if (input[i] < 0 || input[i] > 2) {
                        System.out.println("Coordinates should be from 1 to 3!");
                        rangeFalse = true;
                        continue;
                    }
                }
                if (rangeFalse) {
                    continue;
                }

            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
                continue;
            }

            if(!checkPosition(input[0], input[1])){
                System.out.println("This cell is occupied! Choose another one!");
                continue;
            }
            break;
        }while(true);
        //scanner.close();
        return input;
    }

    public boolean setPosition(char player, int x, int y){
        if(!checkPosition(x, y)){
            return false;
        }
        board[x][y] = player;
        return true;
    }
}

class Move{
    int x;
    int y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }

}

abstract class Player{
    public abstract int[] getMove(Board board, char thisPlayer, char oppositePlayer);

}

class HumanPlayer extends Player{
    public int[] getMove(Board board, char thisPlayer, char oppositePlayer){
        return board.getInput();
    }

}

class ComputerEasy extends Player{
    public int[] getMove(Board board, char thisPlayer, char oppositePlayer){
        List<Move> moves = board.getMoves();
        int randomIndex = new Random().nextInt(moves.size());
        Move randomMove = moves.get(randomIndex);
        int[] move = {randomMove.x, randomMove.y};
        System.out.println("Making move level \"easy\"");
        return move;
    }

}

class ComputerMedium extends Player{
    private boolean checkForWinner(Board board, char symbol, int x, int y){
        Board dummy = board.clone();
        dummy.setPosition(symbol, x, y);
        return dummy.checkWin(symbol);
    }

    public int[] getMove(Board board, char thisPlayer, char oppositePlayer) {
        List<Move> moves = board.getMoves();
        List<Move> blockingMoves = new ArrayList<>();

        System.out.println("Making move level \"medium\"");

        for(Move m : moves){
            if(checkForWinner(board, thisPlayer, m.x, m.y)){
                int[] result = {m.x, m.y};
                return result;
            }
            if(checkForWinner(board, oppositePlayer, m.x, m.y)){
                blockingMoves.add(m);
            }
        }
        if(blockingMoves.size() != 0){
            Move bestMove = blockingMoves.get(0);
            int[] result = {bestMove.x, bestMove.y};
            return result;
        }

        int randomIndex = new Random().nextInt(moves.size());
        Move randomMove = moves.get(randomIndex);
        int[] move = {randomMove.x, randomMove.y};
        return move;
    }
}

class ComputerHard extends Player{
    public int[] getMove(Board board, char thisPlayer, char oppositePlayer) {
        int[] bestMove = minimax(board, thisPlayer, oppositePlayer);
        return bestMove;
    }

    public int[] minimax(Board board, char thisPlayer, char oppositePlayer) {
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = new Move(0, 0);

        List<Move> moves = board.getMoves();

        for (Move move : moves) {
            Board newBoard = board.clone();
            newBoard.setPosition(thisPlayer, move.x, move.y);
            int score = minimaxHelper(newBoard, 0, false, thisPlayer, oppositePlayer);
            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }
        int[] move = {bestMove.x, bestMove.y};
        return move;
    }

    private int minimaxHelper (Board board,int depth, boolean isMaximizing, char thisPlayer, char oppositePlayer)
    {
        if (board.checkWin(thisPlayer)) {
            return 10 - depth;
        } else if (board.checkWin(oppositePlayer)) {
            return depth - 10;
        } else if (board.getMoves().isEmpty()) {
            return 0;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : board.getMoves()) {
                Board newBoard = board.clone();
                newBoard.setPosition(thisPlayer, move.x, move.y);
                int eval = minimaxHelper(newBoard, depth + 1, false, thisPlayer, oppositePlayer);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.getMoves()) {
                Board newBoard = board.clone();
                newBoard.setPosition(oppositePlayer, move.x, move.y);
                int eval = minimaxHelper(newBoard, depth + 1, true, thisPlayer, oppositePlayer);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
}

enum PlayerType{
    USER("user"),
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String value;

    PlayerType(String value){this.value = value;}

    public String getValue(){return value;}
}

class FactoryPlayer{
    boolean exit = false;
    String[] players = new String[2];

    boolean addPlayer(String player, int index){
        for(PlayerType playerName : PlayerType.values()){
            if(playerName.getValue().equals(player)){
                players[index] = playerName.getValue();
                return true;
            }
        }
        return false;
    }

    Player getPlayer(int index){
        switch(players[index]){
            case "user":
                return new HumanPlayer();
            case "easy":
                return new ComputerEasy();
            case "medium":
                return new ComputerMedium();
            case "hard":
                return new ComputerHard();
        }
        return null;
    }

    void getInput() {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("Input command:");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                exit = true;
                return;
            }
            String[] tokens = input.split(" ");
            if (tokens.length != 3) {
                System.out.println("Bad parameters!");
                continue;
            }
            if (!tokens[0].equals("start")) {
                System.out.println("Bad parameters!");
                continue;
            }

            if (!addPlayer(tokens[1], 0)){
                System.out.println("Bad parameters!");
                continue;
            }
            if (!addPlayer(tokens[2], 1)){
                System.out.println("Bad parameters!");
                continue;
            }
            break;
        }while(true);
        //scanner.close();
    }
}

public class Main {
    public static void main(String[] args) {
        char[] player = {'X', 'O'};
        Player[] players = new Player[2];

        do {
            FactoryPlayer factoryPlayer = new FactoryPlayer();
            factoryPlayer.getInput();
            if(factoryPlayer.exit) break;
            players[0] = factoryPlayer.getPlayer(0);
            players[1] = factoryPlayer.getPlayer(1);
            int currentPlayer = 0;

            Board board = new Board();
            //currentPlayer = board.getCurrentUser();
            board.show();
            do {
                //int[] pos = board.getInput();
                int[] pos = players[currentPlayer].getMove(board, player[currentPlayer], player[(currentPlayer + 1) % 2] );
                board.setPosition(player[currentPlayer], pos[0], pos[1]);
                board.show();
                if (board.checkBoard()) {
                    break;
                }
                currentPlayer = (currentPlayer + 1) % 2;
            } while (true);
        }while(true);

    }
}
