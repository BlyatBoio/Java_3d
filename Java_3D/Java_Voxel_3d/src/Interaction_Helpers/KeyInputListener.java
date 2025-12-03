package Interaction_Helpers;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class KeyInputListener implements KeyListener{
    int[] keysDown = new int[0];

    public void addSelf(Component component){
        component.addKeyListener(this);
    }
    public boolean keyIsDown(int keyCode){
        return arrayIncludes(keysDown, keyCode);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        addToKeyCodes(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        subToKeyCodes(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //keyDown = e.getKeyCode();
    }

    public void addToKeyCodes(int value){
        keysDown = addToIntArray(keysDown, value);
    }
    public void subToKeyCodes(int value){
        int[] newKeys = new int[1];

        for(int i = 0; i < keysDown.length; i++){
            if(keysDown[i] != value) newKeys = addToIntArray(newKeys, keysDown[i]);
        }
        keysDown = newKeys;
    }
    public int[] addToIntArray(int[] array, int value){
        int[] newArr = Arrays.copyOf(array, array.length + 1);
        newArr[array.length] = value;
        return newArr;
    }
    public boolean arrayIncludes(int[] array, int value){
        for(int i = 0; i < array.length; i++){
            if(array[i] == value) return true;
        }
        return false;
    }
}
