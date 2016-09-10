/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author reid
 */
package evolutionary;

public class Food implements WorldItem {
    private int xpos, ypos;
    private World world;

    public Food(World w, int myx, int myy) {
        world = w;
        xpos = myx;
        ypos = myy;
    }

    public int getXPos() {
        return xpos;
    }

    public int getYPos() {
        return ypos;
    }

    public int getSize() {
        return 1;
    }

    public World getWorld(){
        return world;
    }

    public String getType(){
        return "Food";
    }


}