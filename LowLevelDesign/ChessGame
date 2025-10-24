package LowLevelDesign.ChessGame_AtharvWani;

//Game should be able to add two players with respective color
//We should have all pieces and canMove() should tell if we can move that place
//Board should check check mate (kingIsChecked and that color(any piece) shouldnt have a legal move) or stale mate (king is not check and still doesnt have legal move)
//print the board at every move
//declare the winner at last


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Color{
    WHITE, BLACK
}
class Cell{
    int x, y;
    Piece piece;
    public Cell(int x, int y){
        this.x = x;
        this.y = y;
    }
}
abstract class Piece{
    Cell cell;
    Color color;
    public Piece(Color color){
        this.color = color;
    }
    public Cell getCell(){
        return this.cell;
    }
    public void setCell(Cell cell){
        this.cell = cell;
    }
    public abstract boolean canMove(Cell from, Cell to, Board board);
}
class Rook extends Piece{
    public Rook(Color color){
        super(color);
    }
    @Override
    public boolean canMove(Cell from, Cell to, Board board){
        int xdiff = Math.abs(to.x - from.x);
        int ydiff = Math.abs(to.y - from.y);

        return (xdiff == 0 && ydiff >= 1) || (ydiff == 0 && xdiff >= 1);
    }
}
class Knight extends Piece{
    public Knight(Color color){
        super(color);
    }
    @Override
    public boolean canMove(Cell from, Cell to, Board board){
        int xdiff = Math.abs(to.x - from.x);
        int ydiff = Math.abs(to.y - from.y);

        return (xdiff == 1 && ydiff == 2) || (ydiff == 1 && xdiff == 2);
    }
}
class Bishop extends Piece{
    public Bishop(Color color){
        super(color);
    }
    @Override
    public boolean canMove(Cell from, Cell to, Board board){
        int xdiff = Math.abs(to.x - from.x);
        int ydiff = Math.abs(to.y - from.y);

        return (xdiff == ydiff && xdiff > 0);
    }
}
class Queen extends Piece{
    public Queen(Color color){
        super(color);
    }
    @Override
    public boolean canMove(Cell from, Cell to, Board board){
        int xdiff = Math.abs(to.x - from.x);
        int ydiff = Math.abs(to.y - from.y);

        return (xdiff == ydiff && xdiff > 0) || (xdiff == 0 && ydiff >= 1) || (ydiff == 0 && xdiff >= 1);
    }
}
class King extends Piece{
    public King(Color color){
        super(color);
    }
    @Override
    public boolean canMove(Cell from, Cell to, Board board){
        int xdiff = Math.abs(to.x - from.x);
        int ydiff = Math.abs(to.y - from.y);

        return xdiff <= 1 && ydiff <= 1;
    }
}
class Pawn extends Piece{
    public Pawn(Color color) {
        super(color);
    }
    @Override
    public boolean canMove(Cell from, Cell to, Board board){
        int xdiff = to.x - from.x;
        int ydiff = Math.abs(to.y - from.y);

        if(color == Color.WHITE){
            return (xdiff == 1 && ydiff == 0) || (from.x == 1 && xdiff == 2 && ydiff == 0) || (ydiff == 1 && xdiff == 1 && board.getPiece(to.x, to.y) != null);
        }
        else if(color == Color.BLACK){
            return (xdiff == -1 && ydiff == 0) || (from.x == 6 && xdiff == -2 && ydiff == 0) || (ydiff == 1 && xdiff == -1 && board.getPiece(to.x, to.y) != null);
        }
        return false;
    }
}
class Board{
    private final Cell[][] grid;
    public Board(){
        grid = new Cell[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                grid[i][j] = new Cell(i, j);
            }
        }
        setupPieces();
    }
    private void setupPieces(){
        grid[0][0].piece = new Rook(Color.WHITE);
        grid[0][1].piece = new Knight(Color.WHITE);
        grid[0][2].piece = new Bishop(Color.WHITE);
        grid[0][3].piece = new Queen(Color.WHITE);
        grid[0][4].piece = new King(Color.WHITE);
        grid[0][5].piece = new Bishop(Color.WHITE);
        grid[0][6].piece = new Knight(Color.WHITE);
        grid[0][7].piece = new Rook(Color.WHITE);

        grid[1][0].piece = new Pawn(Color.WHITE);
        grid[1][1].piece = new Pawn(Color.WHITE);
        grid[1][2].piece = new Pawn(Color.WHITE);
        grid[1][3].piece = new Pawn(Color.WHITE);
        grid[1][4].piece = new Pawn(Color.WHITE);
        grid[1][5].piece = new Pawn(Color.WHITE);
        grid[1][6].piece = new Pawn(Color.WHITE);
        grid[1][7].piece = new Pawn(Color.WHITE);

        grid[7][0].piece = new Rook(Color.BLACK);
        grid[7][1].piece = new Knight(Color.BLACK);
        grid[7][2].piece = new Bishop(Color.BLACK);
        grid[7][3].piece = new Queen(Color.BLACK);
        grid[7][4].piece = new King(Color.BLACK);
        grid[7][5].piece = new Bishop(Color.BLACK);
        grid[7][6].piece = new Knight(Color.BLACK);
        grid[7][7].piece = new Rook(Color.BLACK);

        grid[6][0].piece = new Pawn(Color.BLACK);
        grid[6][1].piece = new Pawn(Color.BLACK);
        grid[6][2].piece = new Pawn(Color.BLACK);
        grid[6][3].piece = new Pawn(Color.BLACK);
        grid[6][4].piece = new Pawn(Color.BLACK);
        grid[6][5].piece = new Pawn(Color.BLACK);
        grid[6][6].piece = new Pawn(Color.BLACK);
        grid[6][7].piece = new Pawn(Color.BLACK);
    }

    public Piece getPiece(int x, int y){
        return grid[x][y].piece;
    }

    public void setPiece(int x, int y, Piece piece){
        this.grid[x][y].piece = piece;
    }

    private boolean isKingChecked(Color color){
        int kposx = 0;
        int kposy = 0;
        boolean kingFound = false;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if (getPiece(i, j) instanceof King && getPiece(i, j).color == color){
                    kposx = i;
                    kposy = j;
                    kingFound = true;
                    break;
                }
            }
            if (kingFound)
                break;
        }

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 8; j++) {
                if(getPiece(i, j) != null && getPiece(i, j).color != color) {
                    Piece pieceOfOpp = getPiece(i, j);
                    Cell src = new Cell(i, j);
                    Cell dest = new Cell(kposx, kposy);
                    if(pieceOfOpp.canMove(src, dest, this)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasAnyLegalMoves(Color color){
        for (int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Piece from = this.getPiece(i, j);
                Cell fromCell = new Cell(i, j);
                if(from.color != color)
                    continue;
                for(int r = 0; r < 8; r++){
                    for(int c = 0; c < 8; c++){
                        Piece to = this.getPiece(r, c);
                        Cell toCell = new Cell(r, c);

                        if(from.canMove(fromCell, toCell, this)) {
                            this.setPiece(i, j, null);
                            this.setPiece(r, c, to);
                            boolean check=isKingChecked(color);
                            setPiece(i, j, from);
                            setPiece(r, c, to);

                            if(!check)
                                return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    public boolean checkMate(Color color){
        return isKingChecked(color) && !hasAnyLegalMoves(color);
    }
    public boolean staleMate(Color color){
        return !isKingChecked(color) && !hasAnyLegalMoves(color);
    }

}
class Player{
    String name;
    Color color;
    public Player(String name, Color color){
        this.name = name;
        this.color = color;
    }
}
class Game{
    private static volatile Game instance;
    Board board;
    List<Player> players;
    int currentPlayerIndex = 0;
    private Game(){
        board = new Board();
        players = new ArrayList<>();
    }

    public static synchronized Game getInstance(){
        if(instance == null)
            instance = new Game();
        return instance;
    }

    public void addPlayer(Player player){
        players.add(player);
    }
    public void start(){
        Scanner sc = new Scanner(System.in);

        while(!gameOver()){
            printBoard();
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println(currentPlayer.color + "'s turn ");
            System.out.print("Enter src row ");
            int sx = sc.nextInt();
            System.out.print("Enter src col ");
            int sy = sc.nextInt();
            Piece sp = board.getPiece(sx, sy);
            if(sp == null){
                System.out.println("No piece found at position " + sx + ", " + sy + ", Pls try again !");
                continue;
            }
            if(sp.color != currentPlayer.color){
                System.out.println("Incorrect piece chosen, Pls try again !");
                continue;
            }
            System.out.print("Enter dest row ");
            int dx = sc.nextInt();
            System.out.print("Enter dest col ");
            int dy = sc.nextInt();
            Cell src = new Cell(sx, sy);
            Cell dest = new Cell(dx, dy);
            if (sp.canMove(src, dest, board)) {
                if(board.getPiece(dx, dy) != null && sp.color == board.getPiece(dx, dy).color)
                    continue;
                move(src, dest, board, sp);
                currentPlayerIndex += 1;
                currentPlayerIndex = currentPlayerIndex % 2;
            }
            else{
                System.out.println("Incorrect move Pls try again !");
            }



        }
    }

    private boolean gameOver() {
        return board.checkMate(Color.WHITE) || board.checkMate(Color.BLACK) || board.staleMate(Color.WHITE) || board.staleMate(Color.BLACK);
    }

    public void printBoard(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++) {
                if (board.getPiece(i, j) instanceof King)
                    System.out.print(board.getPiece(i, j).color.toString().charAt(0) + "K ");
                if (board.getPiece(i, j) instanceof Queen)
                    System.out.print(board.getPiece(i, j).color.toString().charAt(0) + "Q ");
                if (board.getPiece(i, j) instanceof Bishop)
                    System.out.print(board.getPiece(i, j).color.toString().charAt(0) + "B ");
                if (board.getPiece(i, j) instanceof Knight)
                    System.out.print(board.getPiece(i, j).color.toString().charAt(0) + "k ");
                if (board.getPiece(i, j) instanceof Rook)
                    System.out.print(board.getPiece(i, j).color.toString().charAt(0) + "R ");
                if (board.getPiece(i, j) instanceof Pawn)
                    System.out.print(board.getPiece(i, j).color.toString().charAt(0) + "P ");
                if (board.getPiece(i, j) == null)
                    System.out.print("** ");
            }
            System.out.println();
        }
        printBoard();
        declareResult();

    }

    private void declareResult() {
        if (board.checkMate(Color.WHITE)) {
            System.out.println("✅ BLACK wins by checkmate!");
        } else if (board.checkMate(Color.BLACK)) {
            System.out.println("✅ WHITE wins by checkmate!");
        } else if (board.staleMate(Color.WHITE) || board.staleMate(Color.BLACK)) {
            System.out.println("🤝 Game ends in stalemate!");
        }
    }
    private void move(Cell src, Cell dest, Board board, Piece sp){
        board.setPiece(src.x, src.y, null);
        board.setPiece(dest.x, dest.y, sp);
    }
}

public class Solution {
    public static void main(String[] args) {

        Game game = Game.getInstance();
        Player p1 = new Player("Atharv", Color.WHITE);
        Player p2 = new Player("Ayushi", Color.BLACK);
        game.addPlayer(p1);
        game.addPlayer(p2);

        game.start();


    }
}

// Chess Board Config 

//  0 1 2 3 4 5 6 7
//0 x x x x x x x x W
//1 x x x x x x x x W
//2 x x x x x x x x
//3 x x x x x x x x
//4 x x x x x x x x              
//5 x x x x x x x x    
//6 x x x x x x x x B  
//7 x x x x x x x x B
