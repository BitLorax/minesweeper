package org.cis120.minesweeper;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class RunMinesweeper implements Runnable {
    public void run() {
        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Minesweeper");
        frame.setLocation(300, 300);

        // Game board
        final GameBoard board = new GameBoard();
        frame.add(board, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        JButton status = new JButton();
        status.setBorderPainted(false);
        status.setContentAreaFilled(false);
        status.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.reset();
            } // reset game when clicked
        });
        control_panel.add(status);
        board.setStatus(status);

        JButton replay = new JButton();
        replay.setBorderPainted(false);
        replay.setContentAreaFilled(false);
        replay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                board.replay();
            } // replay when
                                                                                     // clicked
        });
        control_panel.add(replay);
        board.setReplay(replay);

        JButton help = new JButton();
        help.setBorderPainted(false);
        help.setContentAreaFilled(false);
        try {
            BufferedReader instructionsReader = new BufferedReader(new FileReader(
                    new File("files/instructions.txt")));
            final String instructions = instructionsReader.lines().collect(
                    Collectors.joining(System.lineSeparator()));
            help.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    JOptionPane.showMessageDialog(frame, instructions); // show instructions written
                                                                        // in instructions.txt
                }
            });
            control_panel.add(help);
            board.setHelp(help);
            JOptionPane.showMessageDialog(frame, instructions);
        } catch (IOException e) {
            System.out.println("IOException: Missing instructions file");
        }

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game
        board.loadGame();
    }
}