package screens;

import Man.AnimGLEventListener1;
import Man.AnimGLEventListener2;
import Man.AnimGLEventListener3;
import Man.AnimGLEventListener4;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomeScreen extends JPanel {

    public HomeScreen(JFrame frame) {
        setLayout(new BorderLayout());

        JLabel backgroundLabel = new JLabel(new ImageIcon(getClass().getResource("/Assets/tank_images/tankBackground.jpg")));
        backgroundLabel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton onlineMatchButton = createButton("Start Online Match");
        JButton localGameButton = createButton("Start Local Game");
        JButton computerGameButton = createButton("Start Computer Game");
        JButton exitButton = createButton("Exit");

//        onlineMatchButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Online Match Starting..."));
        onlineMatchButton.addActionListener(e -> openLoginForm());

        localGameButton.addActionListener(e -> {
            frame.getContentPane().removeAll();

            GLCanvas glCanvas = new GLCanvas();
            AnimGLEventListener1 gameListener = new AnimGLEventListener1();
            glCanvas.addGLEventListener(gameListener);
            glCanvas.addKeyListener(gameListener);

            frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

            Animator animator = new FPSAnimator(glCanvas, 60);
            animator.start();

            frame.revalidate();
            frame.repaint();

            glCanvas.requestFocus();
        });

        computerGameButton.addActionListener(e -> openGameLevelForm());

//        computerGameButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Computer Game Starting..."));
        exitButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.add(onlineMatchButton);
        buttonPanel.add(localGameButton);
        buttonPanel.add(computerGameButton);
        buttonPanel.add(exitButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        backgroundLabel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundLabel.add(buttonPanel, gbc);

        add(backgroundLabel, BorderLayout.CENTER);
    }
    private void openLoginForm() {
        JDialog loginDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Login Form", true);
        loginDialog.setSize(350, 250);
        loginDialog.setLayout(null);
        loginDialog.setLocationRelativeTo(this);

        // Username
        JLabel usernameLabel = new JLabel("USERNAME");
        usernameLabel.setBounds(50, 30, 100, 25);
        loginDialog.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 30, 130, 25);
        loginDialog.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setBounds(50, 70, 100, 25);
        loginDialog.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 130, 25);
        loginDialog.add(passwordField);

        // Show the password
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(150, 100, 130, 25);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022'); // bullet
            }
        });
        loginDialog.add(showPassword);

        // Login and Reset
        JButton loginButton = new JButton("LOGIN");
        loginButton.setBounds(80, 150, 80, 30);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loginDialog.dispose();
            }
        });
        loginDialog.add(loginButton);

        JButton resetButton = new JButton("RESET");
        resetButton.setBounds(180, 150, 80, 30);
        resetButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            showPassword.setSelected(false);
            passwordField.setEchoChar('\u2022');
        });
        loginDialog.add(resetButton);

        loginDialog.setVisible(true);
    }

    private void openGameLevelForm() {
        JDialog levelDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Game level", true);
        levelDialog.setSize(300, 150);
        levelDialog.setLayout(new GridBagLayout());
        levelDialog.setLocationRelativeTo(this);

        // LVLs
        JLabel selectLevelLabel = new JLabel("Select level:");
        String[] levels = {"Easy", "Normal", "Hard"};
        JComboBox<String> levelDropdown = new JComboBox<>(levels);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // Button Actions
//        okButton.addActionListener(e -> {
//            String selectedLevel = (String) levelDropdown.getSelectedItem();
//            JOptionPane.showMessageDialog(levelDialog, "Selected Level: " + selectedLevel);
//            levelDialog.dispose();
//        });

//        okButton.addActionListener(e -> {
//            String selectedLVL = (String) levelDropdown.getSelectedItem();
////            GameLevelSetup.pickinglvll(selectedLVL);
//            pickingLVL(selectedLVL);
//            levelDialog.dispose();
//        });
        okButton.addActionListener(e -> {
            // Get the selected level from the dropdown
            String selectedLVL = (String) levelDropdown.getSelectedItem();

            // Get the current frame or create a new frame if necessary
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(levelDialog);
//            if (frame == null) {
//                frame = new JFrame("Game Frame");
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setSize(800, 600);
//                frame.setLocationRelativeTo(null);
//            }

            // Call GameLevelSetup.setupLevel() with the selected level
            GameLevelSetup.setupLevel(frame, selectedLVL);

            // Make the frame visible (if it's not already)
            frame.setVisible(true);

            // Close the level selection dialog
            levelDialog.dispose();
        });



        cancelButton.addActionListener(e -> levelDialog.dispose());

        // Layout Setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        levelDialog.add(selectLevelLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        levelDialog.add(levelDropdown, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        levelDialog.add(buttonPanel, gbc);

        levelDialog.setVisible(true);
    }
//    private void pickingLVL(String level) {
//        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
//
//        switch (level) {
//            case "Easy":
//                frame.getContentPane().removeAll();
//
//                // GLCanvas for the game with AnimGLEventListener2
//                GLCanvas glCanvasEasy = new GLCanvas();
//                AnimGLEventListener2 gameListenerEasy = new AnimGLEventListener2();
//                glCanvasEasy.addGLEventListener(gameListenerEasy);
//                glCanvasEasy.addKeyListener(gameListenerEasy);
//
//                frame.getContentPane().add(glCanvasEasy, BorderLayout.CENTER);
//
//                // Add "Back to Menu" button in Easy level
//                JPanel easyOverlayPanel = createOverlayPanel(frame);
//                frame.getContentPane().add(easyOverlayPanel, BorderLayout.SOUTH);
//
//                // Animator for Easy level
//                Animator easyAnimator = new FPSAnimator(glCanvasEasy, 60);
//                easyAnimator.start();
//
//                frame.revalidate();
//                frame.repaint();
//
//                glCanvasEasy.requestFocus();
//                break;
//
//            case "Normal":
//                frame.getContentPane().removeAll();
//
//                // GLCanvas for the game with AnimGLEventListener3
//                GLCanvas glCanvasNormal = new GLCanvas();
//                AnimGLEventListener3 gameListenerNormal = new AnimGLEventListener3();
//                glCanvasNormal.addGLEventListener(gameListenerNormal);
//                glCanvasNormal.addKeyListener(gameListenerNormal);
//
//                frame.getContentPane().add(glCanvasNormal, BorderLayout.CENTER);
//
//                // Add "Back to Menu" button in Normal level
//                JPanel normalOverlayPanel = createOverlayPanel(frame);
//                frame.getContentPane().add(normalOverlayPanel, BorderLayout.SOUTH);
//
//                // Animator for Normal level
//                Animator normalAnimator = new FPSAnimator(glCanvasNormal, 60);
//                normalAnimator.start();
//
//                frame.revalidate();
//                frame.repaint();
//
//                glCanvasNormal.requestFocus();
//                break;
//
//            case "Hard":
//                frame.getContentPane().removeAll();
//
//                // GLCanvas for the game with AnimGLEventListener4
//                GLCanvas glCanvasHard = new GLCanvas();
//                AnimGLEventListener4 gameListenerHard = new AnimGLEventListener4();
//                glCanvasHard.addGLEventListener(gameListenerHard);
//                glCanvasHard.addKeyListener(gameListenerHard);
//
//                frame.getContentPane().add(glCanvasHard, BorderLayout.CENTER);
//
//                // Add "Back to Menu" button in Hard level
//                JPanel hardOverlayPanel = createOverlayPanel(frame);
//                frame.getContentPane().add(hardOverlayPanel, BorderLayout.SOUTH);
//
//                // Animator for Hard level
//                Animator hardAnimator = new FPSAnimator(glCanvasHard, 60);
//                hardAnimator.start();
//
//                frame.revalidate();
//                frame.repaint();
//
//                glCanvasHard.requestFocus();
//                break;
//
//            default:
//                // Default - Gray background
//                JPanel grayPanel = new JPanel();
//                grayPanel.setBackground(Color.GRAY);
//                updateContentPane(frame, grayPanel);
//                break;
//        }
//
//    }
    // to handle the picking lvl
    public class GameLevelSetup {

        public static void setupLevel(JFrame frame, String level) {
            // remove anything
            frame.getContentPane().removeAll();

            // GLCanvas and Animator
            GLCanvas glCanvas = new GLCanvas();
            Animator animator;

            // what r we gonna pick from AnimGLEventListenersss
            GLEventListener gameListener = pickinglvll(level);
            glCanvas.addGLEventListener(gameListener);
            glCanvas.addKeyListener((KeyListener) gameListener);

            frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

            // Add "Back to Menu" button or overlay
            JPanel overlayPanel = createOverlayPanel(frame);
            frame.getContentPane().add(overlayPanel, BorderLayout.SOUTH);

            // Start the animator
            animator = new FPSAnimator(glCanvas, 60);
            animator.start();

            // Revalidate and repaint the frame
            frame.revalidate();
            frame.repaint();

            glCanvas.requestFocus();
        }

        private static GLEventListener pickinglvll(String level) {
            switch (level) {
                case "Easy":
                    return new AnimGLEventListener2();
                case "Normal":
                    return new AnimGLEventListener3();
                case "Hard":
                    return new AnimGLEventListener4();
                default:
                    throw new IllegalArgumentException("there is no level bt that name!: " + level);
            }
        }
    }

    private static JPanel createOverlayPanel(JFrame frame) {
        // Transparent panel for overlay
        JPanel overlayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        overlayPanel.setOpaque(false); // make the panal transparent

        // Add the red "Back to Menu" button using the reusable function
        JButton backButton = createBackToMenuButton("Back to Menu", e -> {
            //Navigate back to the Home Screen
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new HomeScreen(frame));
            frame.revalidate();
            frame.repaint();
        });

        overlayPanel.add(backButton);
        return overlayPanel;
    }


//    public class GLCanvasSetup {
//        public static void setupGLCanvas(JFrame frame, GLEventListener glEventListener, KeyListener keyListener) {
//            GLCanvas glCanvas = new GLCanvas();
//
//            // Add the provided GLEventListener to the canvas
//            glCanvas.addGLEventListener(glEventListener);
//
//            // Add the provided KeyListener to the canvas
//            glCanvas.addKeyListener(keyListener);
//
//            // Add the GLCanvas to the JFrame
//            frame.getContentPane().add(glCanvas, BorderLayout.CENTER);
//        }
//    }
    private static JButton createBackToMenuButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.RED); // red background
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.addActionListener(actionListener);
        return button;
    }


    // Helper method to update the content pane with a panel
//    private void updateContentPane(JFrame frame, JPanel panel) {
//        frame.getContentPane().removeAll();
//        frame.getContentPane().add(panel);
//        frame.revalidate();
//        frame.repaint();
//    }



    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 150));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setUI(new RoundedButtonUI());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.orange);
                button.setBackground(new Color(0, 0, 0, 180));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(0, 0, 0, 150));
            }
        });
        return button;
    }
}

class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        JButton button = (JButton) c;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(5, 5, button.getWidth() - 10, button.getHeight() - 10, 30, 30);
        g2.setColor(button.getBackground());
        g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 30, 30);
        g2.setColor(button.getForeground());
        g2.setFont(button.getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (button.getWidth() - fm.stringWidth(button.getText())) / 2;
        int y = (button.getHeight() + fm.getAscent()) / 2 - 4;
        g2.drawString(button.getText(), x, y);
    }
}