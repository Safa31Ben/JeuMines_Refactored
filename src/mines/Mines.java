package mines;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

// supprimé le commentaire indiquant la source du code 

public class Mines extends JFrame {
    private static final long serialVersionUID = 4772165125287256837L;

    // permet un accès direct à ces valeurs
    private static final int WIDTH = 250;
    private static final int HEIGHT = 290;

    private JLabel statusbar;

    public Mines() {
        // Remplacement de la ligne setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); par
        // setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Minesweeper");

        // supprition de le paramètre explicite de chaîne vide
        statusbar = new JLabel();
        add(statusbar, BorderLayout.SOUTH);

        add(new Board(statusbar));

        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Mines();
    }
}
