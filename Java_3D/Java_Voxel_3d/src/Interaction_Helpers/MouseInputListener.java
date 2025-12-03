package Interaction_Helpers;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

public class MouseInputListener implements MouseMotionListener{
    float movedX = 0;
    float movedY = 0;
    float prevXPos = 0;
    float prevYPos = 0;
    int timeSinceMoved = 0;

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
}
