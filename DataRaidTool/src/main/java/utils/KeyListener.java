package utils;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class KeyListener {

    public static void main(String[] argv) throws Exception {

        JTextField textField = new JTextField();

        textField.addKeyListener(new MKeyListener());

        JFrame jframe = new JFrame();

        jframe.add(textField);

        jframe.setSize(400, 350);

        jframe.setVisible(true);

    }
}


