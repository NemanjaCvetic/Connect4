import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

public class Connect4 {

        /**
         * Program: Connect4.java
         * Purpose: Stacking disk game for 2 players
         * Creator: Chris Clarke
         * Created: 19.08.2007
         * Modified: 29.11.2012 (JFrame)
         */

        public static void main(String[] args) {
                Connect4JFrame frame = new Connect4JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
        }
}

class Connect4JFrame extends JFrame implements ActionListener {

        private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
        private Label lblSpacer;
        private Label lblRedMoves;
        private Label lblYellowMoves;
        private Label lblTotalMoves;
        // Fields to keep track of moves
        private int redMoves = 0;
        private int yellowMoves = 0;
        private int totalMoves = 0;
        MenuItem newMI, exitMI, redMI, yellowMI;
        MenuItem humanRedMI, randomRedMI, humanYellowMI, randomYellowMI;
        int[][] theArray;
        boolean end = false;
        boolean gameStart;
        public static final int BLANK = 0;
        public static final int RED = 1;
        public static final int YELLOW = 2;

        public static final int MAXROW = 6; // 6 rows
        public static final int MAXCOL = 7; // 7 columns

        public static final String SPACE = "                  "; // 18 spaces

        int activeColour = RED;

        private Timer gameLoopTimer;
        private boolean isGameRunning = false;
        private int currentTurn = RED;

        // New fields for player types
        private static final int HUMAN = 0;
        private static final int RANDOM = 1;
        private int redPlayerType = HUMAN;
        private int yellowPlayerType = HUMAN;

        private static final int MINIMAX = 2;
        private MenuItem minimaxRedMI, minimaxYellowMI;
        private Label lblLastMoveTime;

        private Random random = new Random(); // Random agent

        public Connect4JFrame() {
                setTitle("Connect4 by Chris Clarke");
                MenuBar mbar = new MenuBar();
                Menu fileMenu = new Menu("File");
                newMI = new MenuItem("New");
                newMI.addActionListener(this);
                fileMenu.add(newMI);
                exitMI = new MenuItem("Exit");
                exitMI.addActionListener(this);
                fileMenu.add(exitMI);
                mbar.add(fileMenu);
                Menu optMenu = new Menu("Options");
                redMI = new MenuItem("Red starts");
                redMI.addActionListener(this);
                optMenu.add(redMI);
                yellowMI = new MenuItem("Yellow starts");
                yellowMI.addActionListener(this);
                optMenu.add(yellowMI);
                mbar.add(optMenu);

                // New Players menu
                Menu playersMenu = new Menu("Players");
                humanRedMI = new MenuItem("Human Red");
                humanRedMI.addActionListener(this);
                playersMenu.add(humanRedMI);
                randomRedMI = new MenuItem("Random Red");
                randomRedMI.addActionListener(this);
                playersMenu.add(randomRedMI);
                humanYellowMI = new MenuItem("Human Yellow");
                humanYellowMI.addActionListener(this);
                playersMenu.add(humanYellowMI);
                randomYellowMI = new MenuItem("Random Yellow");
                randomYellowMI.addActionListener(this);
                playersMenu.add(randomYellowMI);
                mbar.add(playersMenu);
                minimaxRedMI = new MenuItem("Minimax Red");
                minimaxRedMI.addActionListener(this);
                playersMenu.add(minimaxRedMI);
                minimaxYellowMI = new MenuItem("Minimax Yellow");
                minimaxYellowMI.addActionListener(this);
                playersMenu.add(minimaxYellowMI);

                setMenuBar(mbar);

                // Build control panel.
                Panel panel = new Panel();

                btn1 = new Button("1");
                btn1.addActionListener(this);
                panel.add(btn1);
                lblSpacer = new Label(SPACE);
                panel.add(lblSpacer);

                btn2 = new Button("2");
                btn2.addActionListener(this);
                panel.add(btn2);
                lblSpacer = new Label(SPACE);
                panel.add(lblSpacer);

                btn3 = new Button("3");
                btn3.addActionListener(this);
                panel.add(btn3);
                lblSpacer = new Label(SPACE);
                panel.add(lblSpacer);

                btn4 = new Button("4");
                btn4.addActionListener(this);
                panel.add(btn4);
                lblSpacer = new Label(SPACE);
                panel.add(lblSpacer);

                btn5 = new Button("5");
                btn5.addActionListener(this);
                panel.add(btn5);
                lblSpacer = new Label(SPACE);
                panel.add(lblSpacer);

                btn6 = new Button("6");
                btn6.addActionListener(this);
                panel.add(btn6);
                lblSpacer = new Label(SPACE);
                panel.add(lblSpacer);

                btn7 = new Button("7");
                btn7.addActionListener(this);
                panel.add(btn7);

                add(panel, BorderLayout.NORTH);

                // Add labels to display the number of moves
                Panel movePanel = new Panel();
                lblRedMoves = new Label("Red Moves: 0");
                movePanel.add(lblRedMoves);
                lblYellowMoves = new Label("Yellow Moves: 0");
                movePanel.add(lblYellowMoves);
                lblTotalMoves = new Label("Total Moves: 0");
                movePanel.add(lblTotalMoves);
                add(movePanel, BorderLayout.SOUTH);

                lblLastMoveTime = new Label("Last move time: 0ms");
                movePanel.add(lblLastMoveTime);

                // Initialize the game loop timer
                gameLoopTimer = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (isGameRunning) {
                                        makeMove();
                                }
                        }
                });
                gameLoopTimer.start();

                initialize();
                // Set to a reasonable size.
                setSize(1024, 768);
        } // Connect4

        private void startGameLoop() {
                isGameRunning = true;
                gameLoopTimer.start();
                makeMove();
        }

        private void makeMove() {

                if (!end && isGameRunning) {
                        long startTime = System.currentTimeMillis();
                        if (currentTurn == RED) {
                                if (redPlayerType == RANDOM) {
                                        putRandomDisk();
                                } else if (redPlayerType == MINIMAX) {
                                        int col = MinimaxConnect4Player.getBestMove(theArray, RED);
                                        putDisk(col + 1);
                                }
                        } else if (currentTurn == YELLOW) {
                                if (yellowPlayerType == RANDOM) {
                                        putRandomDisk();
                                } else if (yellowPlayerType == MINIMAX) {
                                        int col = MinimaxConnect4Player.getBestMove(theArray, YELLOW);
                                        putDisk(col + 1);
                                }
                        }
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        SwingUtilities.invokeLater(() -> lblLastMoveTime.setText("Last move time: " + duration + "ms"));
                }
        }

        public void initialize() {
                theArray = new int[MAXROW][MAXCOL];
                for (int row = 0; row < MAXROW; row++)
                        for (int col = 0; col < MAXCOL; col++)
                                theArray[row][col] = BLANK;
                gameStart = false;

                redMoves = 0;
                yellowMoves = 0;
                totalMoves = 0;
                updateMoveLabels();
        } // initialize

        public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.BLUE);
                g.fillRect(110, 50, 100 + 100 * MAXCOL, 100 + 100 * MAXROW);
                for (int row = 0; row < MAXROW; row++)
                        for (int col = 0; col < MAXCOL; col++) {
                                if (theArray[row][col] == BLANK)
                                        g.setColor(Color.WHITE);
                                if (theArray[row][col] == RED)
                                        g.setColor(Color.RED);
                                if (theArray[row][col] == YELLOW)
                                        g.setColor(Color.YELLOW);
                                g.fillOval(160 + 100 * col, 100 + 100 * row, 100, 100);
                        }
                check4(g);
        } // paint

        public void putDisk(int n) {
                // put a disk on top of column n
                // if game is won, do nothing
                if (end || !isGameRunning)
                        return;
                gameStart = true;
                int row;
                n--;
                for (row = 0; row < MAXROW; row++)
                        if (theArray[row][n] > 0)
                                break;
                if (row > 0) {
                        theArray[--row][n] = activeColour;

                        if (activeColour == RED) {
                                redMoves++;
                                activeColour = YELLOW;
                                currentTurn = YELLOW;
                        } else {
                                yellowMoves++;
                                activeColour = RED;
                                currentTurn = RED;
                        }
                        totalMoves++;
                        updateMoveLabels();
                        repaint();

                        // Schedule the next move after a longer delay
                        Timer timer = new Timer(5000, e -> SwingUtilities.invokeLater(() -> {
                                repaint(); // Ensure the board is updated
                                makeMove();
                        }));
                        timer.setRepeats(false);
                        timer.start();
                }
        }

        private void updateMoveLabels() {
                lblRedMoves.setText("Red Moves: " + redMoves);
                lblYellowMoves.setText("Yellow Moves: " + yellowMoves);
                lblTotalMoves.setText("Total Moves: " + totalMoves);
        }

        // Method for random agent to make a move
        private void putRandomDisk() {
                Random random = new Random();
                int col;
                do {
                        col = random.nextInt(MAXCOL) + 1;
                } while (!isValidMove(col));
                putDisk(col);
        }

        private boolean isValidMove(int n) {
                n--;
                return theArray[0][n] == BLANK;
        }

        public void displayWinner(Graphics g, int n) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Courier", Font.BOLD, 100));
                if (n == RED)
                        g.drawString("Red wins!", 100, 400);
                else
                        g.drawString("Yellow wins!", 100, 400);
                end = true;
        }

        public void check4(Graphics g) {
                // see if there are 4 disks in a row: horizontal, vertical or diagonal
                // horizontal rows
                for (int row = 0; row < MAXROW; row++) {
                        for (int col = 0; col < MAXCOL - 3; col++) {
                                int curr = theArray[row][col];
                                if (curr > 0
                                                && curr == theArray[row][col + 1]
                                                && curr == theArray[row][col + 2]
                                                && curr == theArray[row][col + 3]) {
                                        displayWinner(g, theArray[row][col]);
                                }
                        }
                }
                // vertical columns
                for (int col = 0; col < MAXCOL; col++) {
                        for (int row = 0; row < MAXROW - 3; row++) {
                                int curr = theArray[row][col];
                                if (curr > 0
                                                && curr == theArray[row + 1][col]
                                                && curr == theArray[row + 2][col]
                                                && curr == theArray[row + 3][col])
                                        displayWinner(g, theArray[row][col]);
                        }
                }
                // diagonal lower left to upper right
                for (int row = 0; row < MAXROW - 3; row++) {
                        for (int col = 0; col < MAXCOL - 3; col++) {
                                int curr = theArray[row][col];
                                if (curr > 0
                                                && curr == theArray[row + 1][col + 1]
                                                && curr == theArray[row + 2][col + 2]
                                                && curr == theArray[row + 3][col + 3])
                                        displayWinner(g, theArray[row][col]);
                        }
                }
                // diagonal upper left to lower right
                for (int row = MAXROW - 1; row >= 3; row--) {
                        for (int col = 0; col < MAXCOL - 3; col++) {
                                int curr = theArray[row][col];
                                if (curr > 0
                                                && curr == theArray[row - 1][col + 1]
                                                && curr == theArray[row - 2][col + 2]
                                                && curr == theArray[row - 3][col + 3])
                                        displayWinner(g, theArray[row][col]);
                        }
                }
        } // end check4

        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == btn1)
                        putDisk(1);
                else if (e.getSource() == btn2)
                        putDisk(2);
                else if (e.getSource() == btn3)
                        putDisk(3);
                else if (e.getSource() == btn4)
                        putDisk(4);
                else if (e.getSource() == btn5)
                        putDisk(5);
                else if (e.getSource() == btn6)
                        putDisk(6);
                else if (e.getSource() == btn7)
                        putDisk(7);
                else if (e.getSource() == newMI) {
                        end = false;
                        initialize();
                        repaint();
                        startGameLoop();
                } else if (e.getSource() == exitMI) {
                        System.exit(0);
                } else if (e.getSource() == redMI) {
                        // don't change colour to play in middle of game
                        if (!gameStart)
                                activeColour = RED;
                } else if (e.getSource() == yellowMI) {
                        if (!gameStart)
                                activeColour = YELLOW;
                } else if (e.getSource() == humanRedMI) {
                        redPlayerType = HUMAN;
                } else if (e.getSource() == randomRedMI) {
                        redPlayerType = RANDOM;
                } else if (e.getSource() == humanYellowMI) {
                        yellowPlayerType = HUMAN;
                } else if (e.getSource() == randomYellowMI) {
                        yellowPlayerType = RANDOM;
                } else if (e.getSource() == minimaxRedMI) {
                        redPlayerType = MINIMAX;
                } else if (e.getSource() == minimaxYellowMI) {
                        yellowPlayerType = MINIMAX;
                }
        } // end ActionPerformed

} // class

class MinimaxConnect4Player {
        private static final int MAX_DEPTH = 6; // Increased depth for better lookahead
        private static final int WIN_SCORE = 1000000;
        private static final int LOSE_SCORE = -1000000;
    
        public static int getBestMove(int[][] board, int player) {
            long startTime = System.currentTimeMillis();
            int bestMove = -1;
            int bestScore = (player == Connect4JFrame.RED) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    
            // Check for immediate winning move
            for (int col = 0; col < Connect4JFrame.MAXCOL; col++) {
                if (isValidMove(board, col)) {
                    int[][] newBoard = makeMove(board, col, player);
                    if (isWinningMove(newBoard, player)) {
                        return col;
                    }
                }
            }
    
            // Check for immediate blocking move
            int opponent = (player == Connect4JFrame.RED) ? Connect4JFrame.YELLOW : Connect4JFrame.RED;
            for (int col = 0; col < Connect4JFrame.MAXCOL; col++) {
                if (isValidMove(board, col)) {
                    int[][] newBoard = makeMove(board, col, opponent);
                    if (isWinningMove(newBoard, opponent)) {
                        return col;
                    }
                }
            }
    
            for (int col = 0; col < Connect4JFrame.MAXCOL; col++) {
                if (isValidMove(board, col)) {
                    int[][] newBoard = makeMove(board, col, player);
                    int score = minimax(newBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, opponent);
    
                    if (player == Connect4JFrame.RED) {
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = col;
                        }
                    } else {
                        if (score < bestScore) {
                            bestScore = score;
                            bestMove = col;
                        }
                    }
                }
            }
    
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Minimax move time: " + duration + "ms");
    
            return bestMove;
        }
    
        private static int minimax(int[][] board, int depth, int alpha, int beta, int player) {
            if (depth == 0 || isGameOver(board)) {
                return evaluateBoard(board, player);
            }
    
            int opponent = (player == Connect4JFrame.RED) ? Connect4JFrame.YELLOW : Connect4JFrame.RED;
    
            if (player == Connect4JFrame.RED) {
                int maxScore = Integer.MIN_VALUE;
                for (int col = 0; col < Connect4JFrame.MAXCOL; col++) {
                    if (isValidMove(board, col)) {
                        int[][] newBoard = makeMove(board, col, player);
                        int score = minimax(newBoard, depth - 1, alpha, beta, opponent);
                        maxScore = Math.max(maxScore, score);
                        alpha = Math.max(alpha, score);
                        if (beta <= alpha)
                            break;
                    }
                }
                return maxScore;
            } else {
                int minScore = Integer.MAX_VALUE;
                for (int col = 0; col < Connect4JFrame.MAXCOL; col++) {
                    if (isValidMove(board, col)) {
                        int[][] newBoard = makeMove(board, col, player);
                        int score = minimax(newBoard, depth - 1, alpha, beta, opponent);
                        minScore = Math.min(minScore, score);
                        beta = Math.min(beta, score);
                        if (beta <= alpha)
                            break;
                    }
                }
                return minScore;
            }
        }
    
        private static int evaluateBoard(int[][] board, int player) {
            int score = 0;
            int opponent = (player == Connect4JFrame.RED) ? Connect4JFrame.YELLOW : Connect4JFrame.RED;
    
            // Check for wins
            if (hasPlayerWon(board, player)) return WIN_SCORE;
            if (hasPlayerWon(board, opponent)) return LOSE_SCORE;
    
            score += evaluateLines(board, player, 3) * 100;
            score += evaluateLines(board, player, 2) * 10;
            score -= evaluateLines(board, opponent, 3) * 80;
            score -= evaluateLines(board, opponent, 2) * 50;
    
            return score;
        }
    
        private static int evaluateLines(int[][] board, int player, int count) {
            int lines = 0;
            lines += countLines(board, player, count, 1, 0); // horizontal
            lines += countLines(board, player, count, 0, 1); // vertical
            lines += countLines(board, player, count, 1, 1); // diagonal /
            lines += countLines(board, player, count, 1, -1); // diagonal \
            return lines;
        }
    
        private static int countLines(int[][] board, int player, int count, int dRow, int dCol) {
            int lines = 0;
            for (int row = 0; row < Connect4JFrame.MAXROW; row++) {
                for (int col = 0; col < Connect4JFrame.MAXCOL; col++) {
                    if (checkLine(board, row, col, player, count, dRow, dCol)) {
                        lines++;
                    }
                }
            }
            return lines;
        }
    
        private static boolean checkLine(int[][] board, int startRow, int startCol, int player, int count, int dRow, int dCol) {
            int playerCount = 0;
            int emptyCount = 0;
            for (int i = 0; i < 4; i++) {
                int row = startRow + i * dRow;
                int col = startCol + i * dCol;
                if (row < 0 || row >= Connect4JFrame.MAXROW || col < 0 || col >= Connect4JFrame.MAXCOL) {
                    return false;
                }
                if (board[row][col] == player) {
                    playerCount++;
                } else if (board[row][col] == Connect4JFrame.BLANK) {
                    emptyCount++;
                } else {
                    return false;
                }
            }
            return playerCount == count && playerCount + emptyCount == 4;
        }
    
        private static boolean isWinningMove(int[][] board, int player) {
            return hasPlayerWon(board, player);
        }
    
        private static boolean hasPlayerWon(int[][] board, int player) {
            return evaluateLines(board, player, 4) > 0;
        }

        private static boolean isValidMove(int[][] board, int col) {
                return board[0][col] == Connect4JFrame.BLANK;
        }

        private static int[][] makeMove(int[][] board, int col, int player) {
                int[][] newBoard = new int[Connect4JFrame.MAXROW][Connect4JFrame.MAXCOL];
                for (int i = 0; i < Connect4JFrame.MAXROW; i++) {
                        System.arraycopy(board[i], 0, newBoard[i], 0, Connect4JFrame.MAXCOL);
                }

                for (int row = Connect4JFrame.MAXROW - 1; row >= 0; row--) {
                        if (newBoard[row][col] == Connect4JFrame.BLANK) {
                                newBoard[row][col] = player;
                                break;
                        }
                }
                return newBoard;
        }

        private static boolean isGameOver(int[][] board) {
                return isBoardFull(board) || hasPlayerWon(board, Connect4JFrame.RED)
                                || hasPlayerWon(board, Connect4JFrame.YELLOW);
        }

        private static boolean isBoardFull(int[][] board) {
                for (int col = 0; col < Connect4JFrame.MAXCOL; col++) {
                        if (board[0][col] == Connect4JFrame.BLANK)
                                return false;
                }
                return true;
        }

       
}