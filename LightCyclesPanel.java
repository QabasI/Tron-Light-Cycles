/*
Authour: Qabas Imbewa
File Name: LightCyclesPanel.java
Description: This class controls all of the functionality in the game. It creates one bike intitially, which acts as the user, and
             it waits until the user selects a game mode (1 or 2 player) to create another bike that will act as either the AI or
             the other user, respectively. There are a few main screens: "intro", where the user can select play to begin, "choose
             mode" where the user can pick the game mode, "game", where the game occurs, and "end", which occurs when the game ends.
*/

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

class LightCyclesPanel extends JPanel implements ActionListener, MouseListener, KeyListener{
    boolean [] keys;
    Timer timer;
    private Image introBackground, introBackgroundEdited, start, startHover, grid, gameOverClear, gameOverYES, gameOverNO, solo, duo, choose;
    private Bike player1, player2;
    private String screen = "intro"; // begins at intro
    private int score1, score2;
    private Rectangle gameBounds = new Rectangle(100, 70, getWidth()-200, getHeight()-140);
    private Rectangle startRect = new Rectangle(295, 470, 310, 35);
    private Rectangle soloButtonRect = new Rectangle(130, 270, 190, 40);
    private Rectangle duoButtonRect = new Rectangle(520, 270, 190, 40);
    private Rectangle restartButtonRect = new Rectangle(360, 400, 65, 30);
    private Rectangle notRestartButtonRect = new Rectangle(470, 400, 65, 30);
    private String gameMode;
    private boolean paused = false; // if a round is over (doesn't need a page as it is just a transparent overlay)
    private int image = 0;

    public LightCyclesPanel(){
        super();
        keys = new boolean[2000];
        introBackground = new ImageIcon("tronBackground.jpg").getImage();
        introBackgroundEdited = new ImageIcon("tronBackgroundEdited.jpg").getImage();
        grid = new ImageIcon("grid.png").getImage();
        gameOverClear = new ImageIcon("gameOverClear.png").getImage();
        gameOverYES = new ImageIcon("gameOverYES.png").getImage();
        gameOverNO = new ImageIcon("gameOverNO.png").getImage();
        start = new ImageIcon("start.png").getImage();
        startHover = new ImageIcon("startHover.png").getImage();
        solo = new ImageIcon("1Player.png").getImage();
        duo = new ImageIcon("2Players.png").getImage();
        choose = new ImageIcon("choose.png").getImage();

        player1 = new Bike(Color.RED, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, Bike.RIGHT, new Point(150, 450)); // user (always at least 1 user, by default it is player 1)
        
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
        timer = new Timer(30, this);
        timer.start();
    }

    public void move(){

        if(screen.equals("game") && !paused){
            player1.move(keys);
            if(gameMode.equals("duo")){
                player2.move(keys);
            }else{
                player2.move();
            }
    
            checkCollision(player1, player2); // checks if 1 crashed into 2
            checkCollision(player2, player1); // checks if 2 crashed into 1
    
            if(player1.getValidState() == false){ // if player 1 is invalid, then player 2 gets the point
                paused = true;
                score2 ++;
                player1 = new Bike(Color.RED, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, Bike.RIGHT, new Point(150, 450));
                if(gameMode.equals("duo")){ // there are multiple constructors, depending on whether the keys are needed
                    player2 = new Bike(Color.BLUE, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_W, KeyEvent.VK_Z, Bike.LEFT, new Point(750, 150));
                }else if(gameMode.equals("solo")){
                    player2 = new Bike(Color.BLUE, Bike.LEFT, new Point(750, 300));
                }
            }else if(player2.getValidState() == false){
                paused = true;
                score1 ++;
                player1 = new Bike(Color.RED, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, Bike.RIGHT, new Point(150, 450));
                if(gameMode.equals("duo")){
                    player2 = new Bike(Color.BLUE, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_W, KeyEvent.VK_Z, Bike.LEFT, new Point(750, 150));
                }else if(gameMode.equals("solo")){
                    player2 = new Bike(Color.BLUE, Bike.LEFT, new Point(750, 300));
                }
            }

            if(paused){ // only need to check for score when it is updated
                if(score1 >= 5 || score2 >= 5){
                    screen = "end";
                    score1 = 0;
                    score2 = 0;
                }
            }
    }
    }   

    public void checkCollision(Bike player, Bike otherPlayer){ // checks collision
        ArrayList<Point> points = player.getTrail();
        ArrayList<Point> otherPoints = otherPlayer.getTrail(); 
        if(otherPoints.contains(points.get(points.size()-1))){ // if the tip of one intersects any point in the trail of the other, the other gains a point
            player.setValidStateFalse();
        }
    }

    public void drawGame(Graphics g){

        int mx = (int)MouseInfo.getPointerInfo().getLocation().getX() - (int)this.getLocationOnScreen().getX(); 
        int my = (int)MouseInfo.getPointerInfo().getLocation().getY() - (int)this.getLocationOnScreen().getY();

        if(screen.equals("game")){
            g.setColor(Color.BLACK);
            g.fillRect(0,0, getWidth(), getHeight());
            g.setColor(new Color(30, 42, 59, 190));
            g.drawImage(grid, (int)gameBounds.getX(), (int)gameBounds.getY(), null); // the main grid (it is the exact same size as the game bounds)
            player1.draw(g); // draws each of the bikes and its trail
            player2.draw(g);
            g.setFont(new Font("monospaced", Font.BOLD, 24));
            g.drawString("PLAYER 1: "+score1, (int)gameBounds.getX(), (int)gameBounds.getY()); // presents the points of each
            g.drawString("PLAYER 2: "+score2, (int)gameBounds.getX() + 550, (int)gameBounds.getY());
            if(paused){
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0,0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.drawString("Click anywhere to continue.", (getWidth()-g.getFontMetrics().stringWidth("Click anywhere to continue."))/2, 300);
            }
        }else if(screen.equals("end")){
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // hover effects
            if(restartButtonRect.contains(new Point(mx, my))){
                g.drawImage(gameOverYES, (getWidth()-gameOverClear.getWidth(null))/2, (getHeight()-gameOverClear.getHeight(null))/2, null);
            }else if(notRestartButtonRect.contains(new Point(mx, my))){
                g.drawImage(gameOverNO, (getWidth()-gameOverClear.getWidth(null))/2, (getHeight()-gameOverClear.getHeight(null))/2, null);
            }else{
                g.drawImage(gameOverClear, (getWidth()-gameOverClear.getWidth(null))/2, (getHeight()-gameOverClear.getHeight(null))/2, null);
            }
        }else if(screen.equals("intro")){

            // the title flickers
            if(image % 10 == 0){
                g.drawImage(introBackgroundEdited, 0, -200, null);
            }else{
                g.drawImage(introBackground, 0, -200, null);
            }
            image ++;

            // hover effects
            if(startRect.contains(new Point(mx, my))){
                g.drawImage(startHover, (getWidth()-start.getWidth(null))/2, 400, null);
            }else{
                g.drawImage(start, (getWidth()-start.getWidth(null))/2, 400, null);
            }
        }else if(screen.equals("choose mode")){
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.drawImage(solo, (getWidth()-solo.getWidth(null)*2 - 300)/2, (getHeight()-solo.getHeight(null))/2, null);
            g.drawImage(duo, (getWidth()-solo.getWidth(null) + 300)/2, (getHeight()-solo.getHeight(null))/2, null);

            // hover effects
            if(soloButtonRect.contains(new Point(mx, my))){
                g.drawImage(choose, (getWidth()-solo.getWidth(null)*2 - 300)/2 - 70, (getHeight()-solo.getHeight(null))/2-10, null);
            }else if(duoButtonRect.contains(new Point(mx, my))){
                g.drawImage(choose, (getWidth()-solo.getWidth(null) + 300)/2 - 70, (getHeight()-solo.getHeight(null))/2-10, null);
            }
            
        }
    }

    @Override
    public void paint(Graphics g){
        drawGame(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
		keys[k] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
		keys[k] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = (int)MouseInfo.getPointerInfo().getLocation().getX() - (int)this.getLocationOnScreen().getX();
        int my = (int)MouseInfo.getPointerInfo().getLocation().getY() - (int)this.getLocationOnScreen().getY();

        if(paused){
            paused = false;
        }if(screen.equals("intro")){
            // when user clicks on "start" on the intro page, go to the page that allows them to choose the mode
            if(startRect.contains(new Point(mx, my))){
                screen = "choose mode";
            }
        }if(screen.equals("end")){
            // when user clicks "yes" on the "end" page, restart the game (go back to the intro page)
            if(restartButtonRect.contains(new Point(mx, my))){
                screen = "intro";
            }
        }if(screen.equals("choose mode")){

            // lets user choose their mode

            if(soloButtonRect.contains(new Point(mx, my))){
                gameMode = "solo";
                screen = "game";
            }else if(duoButtonRect.contains(new Point(mx, my))){
                gameMode = "duo";
                screen = "game";
            }
            // makes the initial player2 based on the mode (if AI, keys are not needed)
            if(gameMode.equals("duo")){
                player2 = new Bike(Color.BLUE, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_W, KeyEvent.VK_Z, Bike.LEFT, new Point(750, 150));
            }else if(gameMode.equals("solo")){
                player2 = new Bike(Color.BLUE, Bike.LEFT, new Point(750, 150));
            }
        }
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
		repaint(); 
    }

    
}
