package org.example;

import javax.swing.*;
import java.awt.*;

public class HowToPlay {
    private JPanel panel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Runnable launchGameCallback;

    public HowToPlay(CardLayout cardLayout, JPanel cardPanel, Runnable launchGameCallback) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.launchGameCallback = launchGameCallback;
        this.panel = createHowToPlayScreen();
    }

    public JPanel getPanel() {
        return panel;
    }

    private JPanel createHowToPlayScreen() {
        JPanel panel = new BackgroundPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.setFocusable(true);

        // Title
        JLabel titleLabel = new JLabel("HOW TO PLAY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 44));
        titleLabel.setForeground(new Color(0x00FF00));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setOpaque(false);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Wrapper panel to center content
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setOpaque(false);
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));

        // Left spacer
        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);

        // Main content panel - centered
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        contentPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        // Instructions with better formatting
        String[] instructions = {
                "JUMP",
                "Press SPACE to jump",
                "",
                "PASS RINGS",
                "Pass through rings matching your color",
                "",
                "AVOID COLLISIONS",
                "Don't hit wrong colors",
                "",
                "CHANGE COLOR",
                "Collect color changers",
                "",
                "QUIT",
                "Press ESC to return"
        };

        for (int i = 0; i < instructions.length; i++) {
            String instruction = instructions[i];
            JLabel label = new JLabel(instruction);

            if (instruction.isEmpty()) {
                // Empty line for spacing
                label.setPreferredSize(new Dimension(0, 12));
            } else if (instruction.equals("JUMP") || instruction.equals("PASS RINGS") || 
                       instruction.equals("AVOID COLLISIONS") || instruction.equals("CHANGE COLOR") || 
                       instruction.equals("QUIT")) {
                // Section headers - larger and bold
                label.setFont(new Font("Arial", Font.BOLD, 22));
                label.setForeground(new Color(0xFFFF00));
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            } else {
                // Description text - larger for better readability
                label.setFont(new Font("Arial", Font.PLAIN, 16));
                label.setForeground(new Color(0xCCCCCC));
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            }

            label.setOpaque(false);
            contentPanel.add(label);
        }

        // Wrap content in a scroll pane for better handling
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add scroll pane to wrapper with spacers for centering
        wrapperPanel.add(Box.createHorizontalGlue());
        wrapperPanel.add(scrollPane);
        wrapperPanel.add(Box.createHorizontalGlue());

        panel.add(wrapperPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 40, 30, 40));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backButton = createStyledButton("BACK", new Color(0x666666), Color.WHITE);
        backButton.setMaximumSize(new Dimension(140, 45));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "START"));

        JButton startButton = createStyledButton("START", new Color(0x00FF00), Color.BLACK);
        startButton.setMaximumSize(new Dimension(140, 45));
        startButton.addActionListener(e -> launchGameCallback.run());

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createHorizontalGlue());

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add keyboard listener for ESC and ENTER keys
        panel.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    // ESC goes back to start
                    cardLayout.show(cardPanel, "START");
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    // ENTER starts the game
                    launchGameCallback.run();
                }
            }
        });

        // Request focus when panel is shown
        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                panel.requestFocusInWindow();
            }
        });

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Custom panel that renders the starfield background with gradient
     */
    private static class BackgroundPanel extends JPanel {
        private long startTime = System.currentTimeMillis();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw gradient background
            java.awt.GradientPaint gradient = new java.awt.GradientPaint(
                    0, 0, new Color(0x0A0A1A),
                    0, getHeight(), new Color(0x1E1E2E)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw stars with twinkling effect
            java.util.Random random = new java.util.Random(42);
            int starCount = 200;
            long elapsed = System.currentTimeMillis() - startTime;

            for (int i = 0; i < starCount; i++) {
                int x = random.nextInt(getWidth());
                int y = random.nextInt(getHeight());

                // Twinkling effect based on time and star index
                float twinkle = (float) Math.sin((elapsed + i * 100) / 500.0f);
                float brightness = 0.3f + (0.7f * (twinkle + 1) / 2);
                brightness = Math.min(1.0f, brightness);

                // Draw star with varying size based on brightness
                int size = brightness > 0.7f ? 3 : 2;
                g2d.setColor(new Color(brightness, brightness, brightness * 0.9f));
                g2d.fillOval(x, y, size, size);
            }

            // Draw subtle glow effect
            g2d.setColor(new Color(0, 100, 200, 10));
            for (int i = 0; i < 3; i++) {
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        }
    }
}
