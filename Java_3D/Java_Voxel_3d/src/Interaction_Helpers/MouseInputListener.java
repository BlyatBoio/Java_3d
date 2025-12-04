package Interaction_Helpers;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

public class MouseInputListener implements MouseMotionListener, MouseListener{
    private float movedX = 0;
    private float movedY = 0;
    private float prevXPos = 0;
    private float prevYPos = 0;
    private int timeSinceMoved = 0;
    private boolean mouseIsDown;
    

    public MouseInputListener(){
        Timer time = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                mouseNotMoved();
            }
        };
        time.scheduleAtFixedRate(task, 0, 16);
    }
    private void mouseNotMoved(){
        if(timeSinceMoved == 0){
            movedX = 0;
            movedY = 0;
        }
        timeSinceMoved ++;
    }
    public void addSelf(Component component){
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }
    public float getMovedX(){
        return movedX;
    }
    public float getMovedY(){
        return movedY;
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        timeSinceMoved = 0;
        movedX = e.getXOnScreen() - prevXPos;
        movedY = e.getYOnScreen() - prevYPos;

        prevXPos = e.getXOnScreen();
        prevYPos = e.getYOnScreen();
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mousePressed(MouseEvent e) {
        mouseIsDown = true;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseIsDown = false;
    }
    public boolean isMouseDown(){
        return mouseIsDown;
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
}
