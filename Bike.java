/*
Authour: Qabas Imbewa
File Name: Bike.java
Description: This is a class for bike objects in the game tron. Each bike can move in one of four directions: left, right, up, and down. 
             The bikes leave behind a trail of points as they move around the grid, and they remain valid (i.e. the trail/round will not stop)
             unless the trail touches the boundary of the grid, or the trail intersects itself (or another trail, but that is not controlled
             by this class)
*/

import java.awt.*;
import java.util.*;

public class Bike {

    // magic numbers (public so that they can be accessed in the panel class)
    public static final int LEFT = 0;
    public static final int UP = 1;
    public static final int RIGHT = 2;
    public static final int DOWN = 3;

    private int direction; 
    private Color color;
    private ArrayList<Point> trail = new ArrayList<Point>(0); // list of points that have been accessed by the bike
    private int right, left, up, down, x, y; // imageToDisplay represents an index from 0-5 (inclusive)
    private final int SPEED = 5; // speed is constant
    private boolean valid = true;

    private Rectangle bounds = new Rectangle(100, 70, 700, 460);

    public Bike(Color color, int right, int left, int up, int down, int direction, Point firstSpot){ // if the bike is not an AI
        this.color = color;
        this.right = right;
        this.left = left;
        this.up = up;
        this.down = down;
        this.direction = direction;
        trail.add(firstSpot);
        x = (int)firstSpot.getX();
        y = (int)firstSpot.getY();
    }

    public Bike(Color color, int direction, Point firstSpot){ // if the bike is an AI
        this.color = color;
        this.direction = direction;
        trail.add(firstSpot);
        x = (int)firstSpot.getX();
        y = (int)firstSpot.getY();
    }

    public void move(){

        // this is the move function for when the bike is an AI - i.e. it is not controlled by a real user and it just does random moves within some basic rules
        // generally, the bike is more likely to continue on in its direction rather than change it. As well, it will not crash into the walls of the boundary and it 
        // more likely to avoid crashing into itself than not. However, it is still a pretty bad player.

        ArrayList<Integer> possibleMoves = new ArrayList<Integer>(0); // the only impossible moves are outside the game boundary

        if(bounds.contains(new Point(x-SPEED, y))){
            if(trail.contains(new Point(x-SPEED, y)) == false){ // more likely to not crash into itself than crash
                for(int i = 0; i < 10; i++){
                    possibleMoves.add(LEFT);
                }
            }else{
                possibleMoves.add(LEFT);
            }
        }if(bounds.contains(new Point(x, y-SPEED))){
            if(trail.contains(new Point(x, y-SPEED)) == false){
                for(int i = 0; i < 10; i++){
                    possibleMoves.add(UP);
                }
            }else{
                possibleMoves.add(UP);
            }
        }if(bounds.contains(new Point(x+SPEED, y))){
            if(trail.contains(new Point(x+SPEED, y)) == false){
                for(int i = 0; i < 10; i++){
                    possibleMoves.add(RIGHT);
                }
            }else{
                possibleMoves.add(RIGHT);
            }
        }if(bounds.contains(new Point(x, y+SPEED))){
            if(trail.contains(new Point(x, y+SPEED)) == false){
                for(int i = 0; i < 10; i++){
                    possibleMoves.add(DOWN);
                }
            }else{
                possibleMoves.add(DOWN);
            }
        }

        if(possibleMoves.contains(direction)){
            for(int i = 0; i < 1000; i++){
                possibleMoves.add(direction); // more likely not to change directions
            }
        }

        Collections.shuffle(possibleMoves);

        if(possibleMoves.size() > 0){
            if(possibleMoves.get(1) == LEFT){
                x -= SPEED;
                direction = LEFT;
            }else if(possibleMoves.get(1) == UP){
                y -= SPEED;
                direction = UP;
            }else if(possibleMoves.get(1) == RIGHT){
                x += SPEED;
                direction = RIGHT;
            }else if(possibleMoves.get(1) == DOWN){
                y += SPEED;
                direction = DOWN;
            }
        }

        if(trail.contains(new Point(x, y))){ // if it has already been at that point (i.e. it crashes into itself), invalid
            valid = false;
        }else{
            trail.add(new Point(x, y));
        }

        if(bounds.contains(new Point(x, y)) == false){ 
            valid = false;
        }

    }

    public void move(boolean [] keys){

        // move function for when the bike is controlled by a human. same as the other one except the moves are given by the keys

        if(keys[left] && direction != LEFT && direction != RIGHT){ // cannot do a 180 degree turn
            direction = LEFT;
        }

        if(keys[up] && direction != UP && direction != DOWN){
            direction = UP;
        }

        if(keys[right] && direction != RIGHT && direction != LEFT){
            direction = RIGHT;
        }

        if(keys[down] && direction != DOWN && direction != UP){
            direction = DOWN;
        }

        if(direction == LEFT && bounds.contains(new Point(x, y))){
            x -= SPEED;
        }else if(direction == UP && bounds.contains(new Point(x, y))){
            y -= SPEED;
        }else if(direction == RIGHT && bounds.contains(new Point(x, y))){
            x += SPEED;
        }else if(direction == DOWN && bounds.contains(new Point(x, y))){
            y += SPEED;
        }

        if(trail.contains(new Point(x, y))){
            valid = false;
        }else{
            trail.add(new Point(x, y));
        }

        if(bounds.contains(new Point(x, y)) == false){
            valid = false;
        }

    }

    public ArrayList<Point> getTrail(){
        return trail;
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, 10, 10); // draws a rectangle represeenting the bike

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5));

        for(int i = 1; i < trail.size(); i++){ // draws a line connecting all the points (i.e. the trail)
            g2.drawLine((int)trail.get(i-1).getX(), (int)trail.get(i-1).getY(), (int)trail.get(i).getX(), (int)trail.get(i).getY());
        }
    }

    public boolean getValidState(){
        if(valid){
            return true;
        }
        return false;
    }

    public void setValidStateFalse(){
        valid = false;
    }
}
