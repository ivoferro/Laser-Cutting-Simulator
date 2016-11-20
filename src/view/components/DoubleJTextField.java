/*
 * Package location for UI components.
 */
package view.components;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 * Represents a text field for double fields.
 *
 * @author Daniel Gonçalves 1151452
 * @author Eric Amaral 1141570
 * @author Ivo Ferro 1151159
 * @author Tiago Correia 1151031
 */
public class DoubleJTextField extends JTextField {

    /**
     * Creates an instance for DoubleJTextField.
     *
     * @param columns number of columns to display
     */
    public DoubleJTextField(int columns) {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();

                if (!isNumber(ch) && !validatePoint(ch) && ch != '\b') {
                    e.consume();
                }
            }
        });
        setColumns(columns);
    }

    /**
     * Verify if a character is number.
     *
     * @param ch character to verify
     * @return true if it is number, false otherwise
     */
    private boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Verify if the sign is valid.
     *
     * @param ch character to verify
     * @return true if it is valid, false otherwise
     */
    private boolean isValidSignal(char ch) {
        if ((getText() == null || "".equals(getText().trim())) && ch == '-') {
            return true;
        }

        return false;
    }

    /**
     * Verify if the point is valid.
     *
     * @param ch character to verify
     * @return true if it is valid, false otherwise
     */
    private boolean validatePoint(char ch) {
        if (ch != '.' || getText().contains(".")) {
            return false;
        }

        if (getText() == null || "".equals(getText().trim())) {
            setText("0");
        } else if ("-".equals(getText())) {
            setText("-0");
        }

        return true;
    }
}