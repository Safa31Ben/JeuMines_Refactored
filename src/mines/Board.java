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

    public void find_empty_cells(int j) {

        int current_col = j % cols;
        int cell;

        if (current_col > 0) { 
            cell = j - cols - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols - 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }

        cell = j - cols;
        if (cell >= 0)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        cell = j + cols;
        if (cell < all_cells)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        if (current_col < (cols - 1)) {
            cell = j - cols + 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }

    }

    public void paint(Graphics g) {

        int cell = 0;
        int uncover = 0;


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                cell = field[(i * cols) + j];

                if (inGame && cell == MINE_CELL)
                    inGame = false;

                if (!inGame) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }


                } else {
                    if (cell > COVERED_MINE_CELL)
                        cell = DRAW_MARK;
                    else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                    (i * CELL_SIZE), this);
            }
        }


        if (uncover == 0 && inGame) {
            inGame = false;
            statusbar.setText("Game won");
        } else if (!inGame)
            statusbar.setText("Game lost");
    }


    class MinesAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean rep = false;


            if (!inGame) {
                newGame();
                repaint();
            }


            if ((x < cols * CELL_SIZE) && (y < rows * CELL_SIZE)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (field[(cRow * cols) + cCol] > MINE_CELL) {
                        rep = true;

                        if (field[(cRow * cols) + cCol] <= COVERED_MINE_CELL) {
                            if (mines_left > 0) {
                                field[(cRow * cols) + cCol] += MARK_FOR_CELL;
                                mines_left--;
                                statusbar.setText(Integer.toString(mines_left));
                            } else
                                statusbar.setText("No marks left");
                        } else {

                            field[(cRow * cols) + cCol] -= MARK_FOR_CELL;
                            mines_left++;
                            statusbar.setText(Integer.toString(mines_left));
                        }
                    }

                } else {

                    if (field[(cRow * cols) + cCol] > COVERED_MINE_CELL) {
                        return;
                    }

                    if ((field[(cRow * cols) + cCol] > MINE_CELL) &&
                        (field[(cRow * cols) + cCol] < MARKED_MINE_CELL)) {

                        field[(cRow * cols) + cCol] -= COVER_FOR_CELL;
                        rep = true;

                        if (field[(cRow * cols) + cCol] == MINE_CELL)
                            inGame = false;
                        if (field[(cRow * cols) + cCol] == EMPTY_CELL)
                            find_empty_cells((cRow * cols) + cCol);
                    }
                }

                if (rep)
                    repaint();

            }
        }
    }
}