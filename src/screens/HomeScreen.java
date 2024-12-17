package screens;

import Man.*;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

import javax.media.opengl.GLCanvas;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Objects;

public class HomeScreen extends JPanel {
    private SoundManager soundManager;
    private JButton soundToggleButton;

    public HomeScreen(JFrame frame) {
        setLayout(new BorderLayout());

        JLabel backgroundLabel = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/Tank_Images/tankBackground.jpg"))));
        backgroundLabel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton onlineMatchButton = createButton("Start Online Match");
        JButton localGameButton = createButton("Start Local Game");
        JButton computerGameButton = createButton("Start Computer Game");
        JButton exitButton = createButton("Exit");

        soundToggleButton = new JButton();
        soundToggleButton.setIcon(new ImageIcon(getClass().getResource("/Assets/music/sound_on.png")));
        soundToggleButton.setBorderPainted(false);
        soundToggleButton.setContentAreaFilled(false);
        soundToggleButton.setFocusPainted(false);
        soundToggleButton.setOpaque(false);
        soundToggleButton.addActionListener(e -> {
            soundManager.toggleSound();
            updateSoundButtonIcon();
        });

        soundManager = new SoundManager("/Assets/music/background.wav");
        soundManager.playBackgroundMusic();




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

    private void updateSoundButtonIcon() {
        if (soundManager.isMusicPlaying()) {
            soundToggleButton.setIcon(new ImageIcon(getClass().getResource("/Assets/music/sound_on.png")));
        } else {
            soundToggleButton.setIcon(new ImageIcon(getClass().getResource("/Assets/music/sound_off.png")));
        }
    }


    private void openLoginForm() {
        JDialog loginDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Login Form", true);
        loginDialog.setSize(350, 250);
        loginDialog.setLayout(null);
        loginDialog.setLocationRelativeTo(this);

        JLabel usernameLabel = new JLabel("USERNAME");
        usernameLabel.setBounds(50, 30, 100, 25);
        loginDialog.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 30, 130, 25);
        loginDialog.add(usernameField);

        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setBounds(50, 70, 100, 25);
        loginDialog.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 130, 25);
        loginDialog.add(passwordField);

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(150, 100, 130, 25);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022'); // Bullet character

            }
        });
        loginDialog.add(showPassword);
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

        JLabel selectLevelLabel = new JLabel("Select level:");
        String[] levels = {"Easy", "Normal", "Hard"};
        JComboBox<String> levelDropdown = new JComboBox<>(levels);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

//        okButton.addActionListener(e -> {
//            String selectedLevel = (String) levelDropdown.getSelectedItem();
//            JOptionPane.showMessageDialog(levelDialog, "Selected Level: " + selectedLevel);
//            levelDialog.dispose();
//        });

        okButton.addActionListener(e -> {
            String selectedLVL = (String) levelDropdown.getSelectedItem();
            pickingLVL(selectedLVL);
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
    private void pickingLVL(String level) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Animator animator=null;
        GLCanvas glCanvas;
        // Check the level and perform corresponding actions
        switch (level) {
            case "Easy":
                // Trigger the Local Game action here
                frame.getContentPane().removeAll();

                glCanvas = new GLCanvas();
                AnimGLEventListener2 gameListenerEasyLevel = new AnimGLEventListener2();
                glCanvas.addGLEventListener(gameListenerEasyLevel);
                glCanvas.addKeyListener(gameListenerEasyLevel);

                frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

                animator = new FPSAnimator(glCanvas, 60);
                animator.start();

                frame.revalidate();
                frame.repaint();

                glCanvas.requestFocus();
                break;

            case "Normal":
                // Mid-Level - Yellow background
                frame.getContentPane().removeAll();

                glCanvas = new GLCanvas();
                AnimGLEventListener3 gameListenerMidLevel = new AnimGLEventListener3();
                glCanvas.addGLEventListener(gameListenerMidLevel);
                glCanvas.addKeyListener(gameListenerMidLevel);

                frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

                animator = new FPSAnimator(glCanvas, 60);
                animator.start();

                frame.revalidate();
                frame.repaint();

                glCanvas.requestFocus();
                break;
            case "Hard":
                // Hard-Level
                frame.getContentPane().removeAll();

                glCanvas = new GLCanvas();
                AnimGLEventListener4 gameListenerMidLevelHardLevel = new AnimGLEventListener4();
                glCanvas.addGLEventListener(gameListenerMidLevelHardLevel);
                glCanvas.addKeyListener(gameListenerMidLevelHardLevel);

                frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

                animator = new FPSAnimator(glCanvas, 60);
                animator.start();

                frame.revalidate();
                frame.repaint();

                glCanvas.requestFocus();
                break;
        }
    }
    // Helper method to update the content pane with a panel
    private void updateContentPane(JFrame frame, JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }



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
                soundManager.playSoundOnce("/Assets/music/button_sound.wav");
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
