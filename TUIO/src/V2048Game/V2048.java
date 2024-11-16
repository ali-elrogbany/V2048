package V2048Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class V2048 extends JFrame {
    public static V2048 instance;
    
    private final int SIZE = 4;
    
    private ArrayList<ArrayList<Integer>> board;
    
    private int timeRemaining = 60;
    private Timer timer;
    
    private boolean isPaused = false;
    
    private final Random random = new Random();
    
    private JPanel boardPanel;
    private JLabel timerLabel;

    public V2048() {
        instance = this;

        board = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            ArrayList<Integer> row = new ArrayList<>(Collections.nCopies(SIZE, 0));
            board.add(row);
        }
        generateNewTile();
        generateNewTile();

        selectTimerDuration();
        setupGUI();
        updateBoard();
        startTimer();
    }
    
    public boolean getIsPaused(){
        return isPaused;
    }

    private void selectTimerDuration() {
        String[] options = {"30 seconds", "60 seconds"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose the timer duration:",
                "Timer Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );
        
        if (choice == 0) {
            timeRemaining = 30;
        } else {
            timeRemaining = 60;
        }
    }

    private void setupGUI() {
        setTitle("2048 Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(SIZE, SIZE, 5, 5));
        boardPanel.setBackground(Color.LIGHT_GRAY);
        add(boardPanel, BorderLayout.CENTER);
        
        timerLabel = new JLabel("Time: " + timeRemaining + "s", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(timerLabel, BorderLayout.NORTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> swipeLeft();
                    case KeyEvent.VK_RIGHT -> swipeRight();
                    case KeyEvent.VK_UP -> swipeUp();
                    case KeyEvent.VK_DOWN -> swipeDown();
                    case KeyEvent.VK_P -> onPauseClick();
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void updateBoard() {
        boardPanel.removeAll();
        for (ArrayList<Integer> row : board) {
            for (Integer value : row) {
                JPanel cell = new JPanel();
                cell.setBackground(getColor(value));
                JLabel label = new JLabel(value == 0 ? "" : String.valueOf(value));
                label.setFont(new Font("Arial", Font.BOLD, 24));
                label.setForeground(Color.BLACK);
                cell.add(label);
                boardPanel.add(cell);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private Color getColor(int value) {
        return switch (value) {
            case 2 -> new Color(238, 228, 218);
            case 4 -> new Color(237, 224, 200);
            case 8 -> new Color(242, 177, 121);
            case 16 -> new Color(245, 149, 99);
            case 32 -> new Color(246, 124, 95);
            case 64 -> new Color(246, 94, 59);
            case 128 -> new Color(237, 207, 114);
            case 256 -> new Color(237, 204, 97);
            case 512 -> new Color(237, 200, 80);
            case 1024 -> new Color(237, 197, 63);
            case 2048 -> new Color(237, 194, 46);
            default -> new Color(205, 193, 180);
        };
    }

    private void generateNewTile() {
        int row, col;
        do {
            row = random.nextInt(SIZE);
            col = random.nextInt(SIZE);
        } while (board.get(row).get(col) != 0);
        board.get(row).set(col, random.nextDouble() < 0.9 ? 2 : 4);
    }
    
    private void startTimer() {
        timer = new Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                timerLabel.setText("Time: " + timeRemaining + "s");
            } else {
                timer.stop();
                gameOver();
            }
        });
        timer.start();
    }
    
    public void pauseTimer() {
        if (timer != null && timer.isRunning()) {
            isPaused = true;
            timer.stop();
        }
    }

    public void resumeTimer() {
        if (timer != null && !timer.isRunning()) {
            isPaused = false;
            timer.start();
        }
    }
    
    private void gameOver() {
        isPaused = true;
        JOptionPane.showMessageDialog(this, "Game Over! Time's up.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void onPauseClick(){
        if (!isPaused){
            pauseTimer();
        }
        else if (timeRemaining > 0){
            resumeTimer();
        }
    }

    // Swipe Methods
    public void swipeLeft() {
        if (!isPaused){
            boolean moved = false;
            for (ArrayList<Integer> row : board) {
                moved |= compressRow(row);
                moved |= combineRow(row);
                compressRow(row);
            }
            if (moved) {
                generateNewTile();
                updateBoard();
            }
        }
    }

    public void swipeRight() {
        if (!isPaused){
            boolean moved = false;
            for (ArrayList<Integer> row : board) {
                Collections.reverse(row);
                moved |= compressRow(row);
                moved |= combineRow(row);
                compressRow(row);
                Collections.reverse(row);
            }
            if (moved) {
                generateNewTile();
                updateBoard();
            }
        }
    }

    public void swipeUp() {
        if (!isPaused){
            boolean moved = false;
            for (int col = 0; col < SIZE; col++) {
                ArrayList<Integer> column = getColumn(col);
                moved |= compressRow(column);
                moved |= combineRow(column);
                compressRow(column);
                setColumn(col, column);
            }
            if (moved) {
                generateNewTile();
                updateBoard();
            }
        }
    }

    public void swipeDown() {
        if (!isPaused){
            boolean moved = false;
            for (int col = 0; col < SIZE; col++) {
                ArrayList<Integer> column = getColumn(col);
                Collections.reverse(column);
                moved |= compressRow(column);
                moved |= combineRow(column);
                compressRow(column);
                Collections.reverse(column);
                setColumn(col, column);
            }
            if (moved) {
                generateNewTile();
                updateBoard();
            }
        }
    }

    private boolean compressRow(ArrayList<Integer> row) {
        boolean changed = false;
        row.removeIf(n -> n == 0);
        while (row.size() < SIZE) {
            row.add(0);
            changed = true;
        }
        return changed;
    }

    private boolean combineRow(ArrayList<Integer> row) {
        boolean changed = false;
        for (int i = 0; i < row.size() - 1; i++) {
            if (row.get(i).equals(row.get(i + 1)) && row.get(i) != 0) {
                row.set(i, row.get(i) * 2);
                row.set(i + 1, 0);
                changed = true;
            }
        }
        return changed;
    }

    private ArrayList<Integer> getColumn(int colIndex) {
        ArrayList<Integer> column = new ArrayList<>();
        for (ArrayList<Integer> row : board) {
            column.add(row.get(colIndex));
        }
        return column;
    }

    private void setColumn(int colIndex, ArrayList<Integer> column) {
        for (int row = 0; row < SIZE; row++) {
            board.get(row).set(colIndex, column.get(row));
        }
    }
}
