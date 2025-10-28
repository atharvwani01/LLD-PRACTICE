package LowLevelDesign.TicTacToe_AtharvWani;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Symbol {
    X, O, EMPTY
}

class Cell {
    int x, y;
    Symbol symbol;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.symbol = Symbol.EMPTY;
    }
}

class Board {
    private final Cell[][] grid;
    private final int size;

    public Board(int n) {
        this.size = n;
        grid = new Cell[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    public boolean isEmpty(int x, int y) {
        return grid[x][y].symbol == Symbol.EMPTY;
    }

    public Symbol getSymbol(int x, int y) {
        return grid[x][y].symbol;
    }

    public int getSize() {
        return size;
    }

    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].symbol == Symbol.EMPTY)
                    return false;
            }
        }
        return true;
    }

    public void setSymbol(int x, int y, Symbol symbol) {
        grid[x][y].symbol = symbol;
    }
}

class Player {
    String name;
    Symbol symbol;

    public Player(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}

interface WinningStrategy {
    boolean checkWinner(Board board, Symbol symbol);
}

class RowWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWinner(Board board, Symbol symbol) {
        for (int i = 0; i < board.getSize(); i++) {
            boolean isWinning = true;
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getSymbol(i, j) != symbol) {
                    isWinning = false;
                    break;
                }
            }
            if (isWinning)
                return true;
        }
        return false;
    }
}

class ColumnWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWinner(Board board, Symbol symbol) {
        for (int j = 0; j < board.getSize(); j++) {
            boolean isWinning = true;
            for (int i = 0; i < board.getSize(); i++) {
                if (board.getSymbol(i, j) != symbol) {
                    isWinning = false;
                    break;
                }
            }
            if (isWinning)
                return true;
        }
        return false;
    }
}

class DiagonalWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWinner(Board board, Symbol symbol) {
        boolean isWinning = true;

        for (int i = 0; i < board.getSize(); i++) {
            if (board.getSymbol(i, i) != symbol) {
                isWinning = false;
                break;
            }
        }
        if (isWinning)
            return true;

        isWinning = true;
        for (int i = 0; i < board.getSize(); i++) {
            if (board.getSymbol(i, board.getSize() - 1 - i) != symbol) {
                isWinning = false;
                break;
            }
        }
        return isWinning;
    }
}

class Game {
    private static volatile Game instance;
    private final List<Player> players;
    private int playerCurrentIndex;
    private final Board board;
    private final List<WinningStrategy> winningStrategies;

    private Game() {
        playerCurrentIndex = 0;
        players = new ArrayList<>();
        board = new Board(3);
        winningStrategies = List.of(
                new RowWinningStrategy(),
                new ColumnWinningStrategy(),
                new DiagonalWinningStrategy()
        );
    }

    public static synchronized Game getInstance() {
        if (instance == null)
            instance = new Game();
        return instance;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        displayBoard();

        while (true) {
            Player currPlayer = players.get(playerCurrentIndex);
            System.out.println(currPlayer.name + "'s turn (" + currPlayer.symbol + ")!");

            System.out.print("Enter row (0-" + (board.getSize() - 1) + "): ");
            int r = sc.nextInt();
            System.out.print("Enter col (0-" + (board.getSize() - 1) + "): ");
            int c = sc.nextInt();

            if (r < 0 || r >= board.getSize() || c < 0 || c >= board.getSize() || !board.isEmpty(r, c)) {
                System.out.println("❌ Invalid cell! Try again.");
                continue;
            }

            board.setSymbol(r, c, currPlayer.symbol);
            displayBoard();

            if (isWinner(currPlayer)) {
                System.out.println("🏆 " + currPlayer.name + " wins!");
                break;
            }

            if (board.isFull()) {
                System.out.println("🤝 Game Drawn!");
                break;
            }

            playerCurrentIndex = (playerCurrentIndex + 1) % players.size();
        }

        sc.close();
    }

    private boolean isWinner(Player player) {
        for (WinningStrategy strategy : winningStrategies) {
            if (strategy.checkWinner(board, player.symbol))
                return true;
        }
        return false;
    }

    private void displayBoard() {
        System.out.println("\nCurrent Board:");
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                Symbol s = board.getSymbol(i, j);
                System.out.print((s == Symbol.EMPTY ? "-" : s) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

public class Solution {
    public static void main(String[] args) {
        Game game = Game.getInstance();
        game.addPlayer(new Player("Atharv", Symbol.X));
        game.addPlayer(new Player("Ayushi", Symbol.O));
        game.start();
    }
}
