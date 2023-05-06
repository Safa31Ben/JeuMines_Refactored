package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Random;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Board extends JPanel {
    private static final long serialVersionUID = 6195235521361212179L;

    private Random random;

    private static final int NUM_IMAGES = 13;
    private static final int CELL_SIZE = 15;

    private static final int COVER_FOR_CELL = 10;
    private static final int MARK_FOR_CELL = 10;

    private static final int EMPTY_CELL = 0;
    private static final int MINE_CELL = 9;
    private static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private static final int DRAW_MINE = 9;
    private static final int DRAW_COVER = 10;
    private static final int DRAW_MARK = 11;
    private static final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private transient Image[] images;
    private int mines = 40;
    private int rows = 16;
    private int cols = 16;
    private int totalCells;
    private JLabel statusBar;

    public Board(JLabel statusbar) {
        random = new Random();

        this.statusBar = statusbar;
        loadImages();
        setDoubleBuffered(true);
        addMouseListener(new MinesAdapter());
        newGame();
    }

    private void loadImages() {
        images = new Image[NUM_IMAGES];
        for (int i = 0; i < NUM_IMAGES; i++) {
            String imagePath = String.format("%d.gif", i);
            images[i] = new ImageIcon(getClass().getClassLoader().getResource(imagePath)).getImage();
        }
    }
    public void newGame() {
        inGame = true;
        minesLeft = mines;
        totalCells = rows * cols;
        field = new int[totalCells];
        Arrays.fill(field, COVER_FOR_CELL);
        statusBar.setText(Integer.toString(minesLeft));

        int i = 0;
        while (i < mines) {
            int position = (int) (totalCells * random.nextDouble());
            if ((position < totalCells) && (field[position] != COVERED_MINE_CELL)) {
                field[position] = COVERED_MINE_CELL;
                i++;

                int[] cells = { -1 - cols, -1, cols - 1, -cols, cols, -cols + 1, cols + 1, 1 };
                for (int j = 0; j < cells.length; j++) {
                    int cell = position + cells[j];
                    if (cell >= 0 && cell < totalCells && field[cell] != COVERED_MINE_CELL) {
                        field[cell]++;
                    }
                }
            }
        }
    }

    public void findEmptyCells(int j) {
        int cell;

        int[] cellsToCheck = { j - cols - 1, j - 1, j + cols - 1, j - cols, j + cols, j - cols + 1, j + cols + 1,
                j + 1 };

        for (int i = 0; i < cellsToCheck.length; i++) {
            cell = cellsToCheck[i];
            if (cell >= 0 && cell < totalCells && field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    findEmptyCells(cell);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        int uncover = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cell = field[(i * cols) + j];

                if (inGame && cell == MINE_CELL) {
                    inGame = false;
                }

                if (!inGame) {
                    cell = getCellStateAfterGameEnd(cell);
                } else {
                    cell = getCellStateDuringGame(cell);
                    if (cell == DRAW_COVER) {
                        uncover++;
                    }
                }

                g.drawImage(images[cell], (j * CELL_SIZE), (i * CELL_SIZE), this);
            }
        }

        checkWonLost(uncover);
    }

    private int getCellStateAfterGameEnd(int cell) {
        switch (cell) {
            case COVERED_MINE_CELL:
                return DRAW_MINE;
            case MARKED_MINE_CELL:
                return DRAW_MARK;
            default:
                if (cell > COVERED_MINE_CELL) {
                    return DRAW_WRONG_MARK;
                } else if (cell > MINE_CELL) {
                    return DRAW_COVER;
                }
                break;
        }
        return cell;
    }

    private int getCellStateDuringGame(int cell) {
        if (cell > COVERED_MINE_CELL) {
            return DRAW_MARK;
        } else if (cell > MINE_CELL) {
            return DRAW_COVER;
        }
        return cell;
    }

    private void checkWonLost(int uncover) {
        if (uncover == 0 && inGame) {
            inGame = false;
            statusBar.setText("Game won");
        } else if (!inGame) {
            statusBar.setText("Game lost");
        }
    }

    class MinesAdapter extends MouseAdapter {
        private int getClickedColumn(MouseEvent e) {
            return e.getX() / CELL_SIZE;
        }

        private int getClickedRow(MouseEvent e) {
            return e.getY() / CELL_SIZE;
        }

        private boolean isValidCell(int row, int col) {
            return (row >= 0 && row < rows) && (col >= 0 && col < cols);
        }

        private boolean isRightClick(MouseEvent e) {
            return e.getButton() == MouseEvent.BUTTON3;
        }

        private boolean isCovered(int cell) {
            return cell > COVERED_MINE_CELL;
        }

        private boolean isMarked(int cell) {
            return cell > MINE_CELL && cell < MARKED_MINE_CELL;
        }

        private boolean handleRightClick(int row, int col) {
            boolean rep = false;
            if (field[(row * cols) + col] > MINE_CELL) {
                rep = true;
                if (field[(row * cols) + col] <= COVERED_MINE_CELL) {
                    if (minesLeft > 0) {
                        field[(row * cols) + col] += MARK_FOR_CELL;
                        minesLeft--;
                        statusBar.setText(Integer.toString(minesLeft));
                    } else {
                        statusBar.setText("No marks left");
                    }
                } else {
                    field[(row * cols) + col] -= MARK_FOR_CELL;
                    minesLeft++;
                    statusBar.setText(Integer.toString(minesLeft));
                }
            }
            return rep;
        }

        private boolean handleLeftClick(int row, int col) {
            boolean rep = false;
            if (isCovered(field[(row * cols) + col])) {
                return rep;
            }

            if (isMarked(field[(row * cols) + col])) {
                field[(row * cols) + col] -= COVER_FOR_CELL;

                rep = true;

                if (field[(row * cols) + col] == MINE_CELL) {
                    inGame = false;
                }

                if (field[(row * cols) + col] == EMPTY_CELL) {
                    findEmptyCells((row * cols) + col);
                }
            }
            return rep;
        }

        private void handleClick(MouseEvent e) {
            int col = getClickedColumn(e);
            int row = getClickedRow(e);

            if (!inGame) {
                newGame();
                repaint();
            }

            if (isValidCell(row, col)) {
                boolean rep;
                if (isRightClick(e)) {
                    rep = handleRightClick(row, col);
                } else {
                    rep = handleLeftClick(row, col);
                }

                if (rep)
                    repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            handleClick(e);
        }
    }
}