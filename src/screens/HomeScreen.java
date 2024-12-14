package screens;

import Man.AnimGLEventListener4;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

import javax.media.opengl.GLCanvas;
import javax.swing.*;
import java.awt.*;
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

        onlineMatchButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Online Match Starting..."));
        localGameButton.addActionListener(e -> {
            frame.getContentPane().removeAll();

            GLCanvas glCanvas = new GLCanvas();
            AnimGLEventListener4 gameListener = new AnimGLEventListener4();
            glCanvas.addGLEventListener(gameListener);
            glCanvas.addKeyListener(gameListener);

            frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

            Animator animator = new FPSAnimator(glCanvas, 60);
            animator.start();

            frame.revalidate();
            frame.repaint();

            glCanvas.requestFocus();
        });

        computerGameButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Computer Game Starting..."));
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
