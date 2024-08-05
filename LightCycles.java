/*
Authour: Qabas Imbewa
File Name: LightCycles.java
Description: 
*/
import javax.swing.*;

class LightCycles extends JFrame {
    public LightCycles(){
        super("Tron Light Cycle");
        LightCyclesPanel pane = new LightCyclesPanel();
        add(pane);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String [] args){
        LightCycles frame = new LightCycles();
        frame.setSize(900, 600);
        frame.setResizable(false);
    }
}