package wood.graphics;

import wood.game.WoodPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class PlayerScoreDisplayPanel extends JPanel implements Observer {
    private static final Color defaultRedFontColor = new Color(179, 0, 0);
    private static final Color defaultBlueFontColor = new Color(0, 0, 179);

    private WoodPlayer redPlayer;
    private WoodPlayer bluePlayer;
    private String redPlayerName;
    private String bluePlayerName;
    private int preferredWidth;

    public PlayerScoreDisplayPanel(int preferredWidth, int boardSize, WoodPlayer redPlayer, WoodPlayer bluePlayer) {
        super();
        this.setDoubleBuffered(true);
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.redPlayerName = getPlayerName(redPlayer);
        this.bluePlayerName = getPlayerName(bluePlayer);

        // Ensure screen size is an exact multiple of the board size
        this.preferredWidth = preferredWidth;
        int numExtraPixels = (preferredWidth % boardSize);
        if(numExtraPixels != 0) {
            this.preferredWidth += boardSize - numExtraPixels;
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D brush = (Graphics2D) g;
        Color redFont = defaultRedFontColor;
        Color blueFont = defaultBlueFontColor;

        fillBackground(brush, Color.WHITE);
        drawPlayerScores(brush, redFont, blueFont);
        drawBorder(brush, Color.BLACK);
    }

    private void fillBackground(Graphics2D brush, Color backgroundColor) {
        brush.setColor(backgroundColor);
        brush.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    private void drawPlayerScores(Graphics2D brush, Color redFontColor, Color blueFontColor) {
        String redPlayerScore = String.valueOf(redPlayer.getScore());
        String bluePlayerScore = String.valueOf(bluePlayer.getScore());

        brush.setFont(new Font("TimesRoman", Font.PLAIN, 32));
        FontMetrics fontMetrics = brush.getFontMetrics();
        int redPlayerNameWidth = fontMetrics.stringWidth(redPlayerName);
        int scorePixelWidth = fontMetrics.stringWidth(redPlayerScore);

        // Red player score is aligned to the right
        brush.setColor(redFontColor);
        brush.drawString(redPlayerName, (this.getWidth() / 2) - redPlayerNameWidth - 10, 5 * this.getHeight() / 12);
        brush.drawString(redPlayerScore, (this.getWidth() / 2) - scorePixelWidth - 10, 5 * this.getHeight() / 6);

        // Blue player score is aligned to the left
        brush.setColor(blueFontColor);
        brush.drawString(bluePlayerName, this.getWidth() / 2 + 10, 5 * this.getHeight() / 12);
        brush.drawString(bluePlayerScore, this.getWidth() / 2 + 10, 5 * this.getHeight() / 6);
    }

    private void drawBorder(Graphics2D brush, Color borderColor) {
        int borderWidth = 8;

        // Draw the border around the background
        brush.setColor(borderColor);
        brush.setStroke(new BasicStroke(borderWidth));
        brush.drawRect(0, 0, this.getWidth(), this.getHeight());

        // Draw a smaller stroke to separate the scores
        brush.setStroke(new BasicStroke(borderWidth / 2));
        brush.drawRect(this.getWidth() / 2, 0, this.getWidth(), this.getHeight());
    }

    private String getPlayerName(WoodPlayer player) {
        try {
            return player.getStrategy().getName().trim();
        } catch(Exception e) {
            // If you thrown an exception getting your name, that's just your name now
            return e.getClass().getSimpleName();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(preferredWidth, 75);
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }
}
