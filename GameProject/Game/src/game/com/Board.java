package game.com;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Board extends JPanel {

    private Timer timer;
    private String message = "Game Over";
    private Ball ball;
    private Paddle paddle;
    private Brick bricks[];
    private boolean ingame = true;
    private int points = 0;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setFocusable(true);

        bricks = new Brick[Constants.N_OF_BRICKS];
        setDoubleBuffered(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), Constants.DELAY, Constants.PERIOD);
    }

    @Override
    public void addNotify() {

        super.addNotify();
        gameInit();
    }

    private void gameInit() {

        ball = new Ball();
        paddle = new Paddle();

        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                bricks[k] = new Brick(j * 41 + 25, i * 11 + 50);
                k++;
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        if (ingame) {

            drawObjects(g2d);

        } else {

            gameFinished(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics2D g2d) {

        try {
            g2d.drawImage(ImageIO.read(getClass().getResource("/bckgr.jpg")), 0, 0, 300, 400, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getWidth(), ball.getHeight(), this);
        g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getWidth(), paddle.getHeight(), this);

        for (int i = 0; i < Constants.N_OF_BRICKS; i++) {
            if (!bricks[i].isDestroyed()) {
                g2d.drawImage(bricks[i].getImage(), bricks[i].getX(),
                        bricks[i].getY(), bricks[i].getWidth(),
                        bricks[i].getHeight(), this);
            }
        }
    }

    private void gameFinished(Graphics2D g2d) {

        Font font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics metr = this.getFontMetrics(font);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString(message,
                (Constants.WIDTH - metr.stringWidth(message)) / 2,
                Constants.WIDTH / 2);
        String pointsMessage = String.format("Points: %d", points);
        g2d.drawString(String.format(pointsMessage),
                (Constants.WIDTH - metr.stringWidth(pointsMessage)) / 2,
                Constants.WIDTH / 2 + 30);
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            paddle.keyPressed(e);
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {

            ball.move();
            paddle.move();
            checkCollision();
            repaint();
        }
    }

    private void stopGame() {

        ingame = false;
        timer.cancel();
    }

    private void checkCollision() {

        if (ball.getRect().getMaxY() > Constants.BOTTOM_EDGE) {
            stopGame();
        }

        for (int i = 0, j = 0; i < Constants.N_OF_BRICKS; i++) {

            if (bricks[i].isDestroyed()) {
                j++;
            }

            if (j == Constants.N_OF_BRICKS) {

                message = "Victory";
                stopGame();
            }
        }

        if ((ball.getRect()).intersects(paddle.getRect())) {

            int paddleLPos = (int) paddle.getRect().getMinX();
            int ballLPos = (int) ball.getRect().getMinX();

            int first = paddleLPos + 8;
            int second = paddleLPos + 16;
            int third = paddleLPos + 24;
            int fourth = paddleLPos + 32;

            if (ballLPos < first) {
                ball.setXDir(-1);
                ball.setYDir(-1);
            }

            if (ballLPos >= first && ballLPos < second) {
                ball.setXDir(-1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos >= second && ballLPos < third) {
                ball.setXDir(0);
                ball.setYDir(-1);
            }

            if (ballLPos >= third && ballLPos < fourth) {
                ball.setXDir(1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos > fourth) {
                ball.setXDir(1);
                ball.setYDir(-1);
            }
        }

        for (int i = 0; i < Constants.N_OF_BRICKS; i++) {

            if ((ball.getRect()).intersects(bricks[i].getRect())) {

                int ballLeft = (int) ball.getRect().getMinX();
                int ballHeight = (int) ball.getRect().getHeight();
                int ballWidth = (int) ball.getRect().getWidth();
                int ballTop = (int) ball.getRect().getMinY();

                Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                Point pointLeft = new Point(ballLeft - 1, ballTop);
                Point pointTop = new Point(ballLeft, ballTop - 1);
                Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if (!bricks[i].isDestroyed()) {
                    if (bricks[i].getRect().contains(pointRight)) {
                        ball.setXDir(-1);
                    } else if (bricks[i].getRect().contains(pointLeft)) {
                        ball.setXDir(1);
                    }

                    if (bricks[i].getRect().contains(pointTop)) {
                        ball.setYDir(1);
                    } else if (bricks[i].getRect().contains(pointBottom)) {
                        ball.setYDir(-1);
                    }

                    bricks[i].setDestroyed(true);
                    points += 10;
                }
            }
        }
    }
}