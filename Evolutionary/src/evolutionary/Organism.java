/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jordan Anderson & Reid Anderson
 */
package evolutionary;
import java.util.*;
import java.io.*;

public class Organism implements WorldItem {
    private int health, fov, res, dir, xpos, ypos, size;
    private World world;
    private boolean alive, hasTarget, carnivore;
    private WorldItem target;
    private int range; 	//sight range
    private int nightVision;

    public Organism(World w, int theta, int v, int n, int r, int orientation, int x, int y, int rad, boolean carn) {	//constructor
        fov = theta;
        res = r;
        size = rad; 		//size of organism
        health = rad*10 + 50;
        world = w;
        alive = true;		//organism is alive
        dir = orientation; 	//measured from positive x axis. [0,360]
        hasTarget = false;
        xpos = x;
        ypos = y;
        range = v;
        nightVision = n;
        if(carn){				//more likely to be carnivorous if parent carried trait
            if(Math.random()<0.5){
                carnivore = true;
            }
        }
        else if(Math.random()< 0.1){	//always some base chance of carnivorism
            carnivore = true;
        }
        else{
            carnivore = false;
        }
    }

    public Organism(World w, boolean carn){	//overloaded constructor
        world = w;							//for easier population
        fov = (int)(Math.random()*40)+1;
        range = (int)(Math.random()*30);	//possiblity for blindness 0-29
        nightVision = (int)(Math.random()*10);
        res = (int)(Math.random()*10)+1;
        dir = (int)(Math.random()*360);
        xpos = (int)(Math.random()*100);
        ypos = (int)(Math.random()*100);
        size = (int)(Math.random()*10)+1;
        carnivore = carn;

    }
    /*step is the primary method
     * that will use the world and organism
     * conditions to determine which action to take.
     * World class is responsible for killing organisms,
     * and decrementing health even after death allows us
     * to recover the order in which organisms died
     */
    public void step(){
        if(isAlive()){
            if(!hasTarget) {	//not already on a target
                detect();		//look for food
            }
            if(!hasTarget){		//nothing found
                int r = (int)(Math.random()*360); //rotate randomly
                rotate(r);
            }
            else{		// have/found a target food source
                if(nearFood(target)){
                    bite(target);
                }
                else move(target);
            }
        }
        else{
            loseHealth(1);	//keep decrementing health after death
        }					//to keep track of order
    }

    public void rotate(int theta) {	//change the orientation
        dir += theta;
        while(dir < 0) {	//should never get low enough to need
            dir += 360;  	//more than one iteration, but just in case
        }
        if(dir > 360) {		//wrap around
            dir %= 360;
        }
        loseHealth(1);
    }

    public void bite(WorldItem f){		//attempt to bite food
        World w = getWorld();

        if(onFood(f)){		//actually on food?
            gainHealth(10);
            w.remove(f);
        }
        else {
            loseHealth(1);
            if(getHealth() > 0){		//survived failed bite?
                int r = (int)(Math.random()*360); //rotate randomly
                rotate(r);
            }
        }
        hasTarget = false;
    }

    public void move(WorldItem f) {
        int randx, randy, foodx, foody;
        foodx = f.getXPos();
        foody = f.getYPos();
        randx = (int) (2*Math.random()*getRes()+1) - getRes();
        randy = (int) (2*Math.random()*getRes()+1) - getRes();
        int dif = Math.abs(xpos-randx) + Math.abs(ypos-randy);
        xpos = randx + foodx;
        ypos = randy + foody;
        loseHealth(dif);
    }
    public void die(){
        alive = false;
    }

    public void gainHealth(int h){
        health += h;
    }

    public void setTarget(WorldItem f){
        target = f;
    }

    public void loseHealth(int h){
        health -= h;
    }

    public boolean isAlive(){
        return alive;
    }

    public boolean nearFood(WorldItem f) {
        World w = getWorld();
        return w.checkOverlap(this, f, getRes());
        //need to overload checkOverlap parameters
    }

    public boolean onFood(WorldItem f){	//close enough to eat food?
        World w = getWorld();
        if(w.checkOverlap(this, f)) {	//need hasFood in world class
            return true;
        }
        else
            return false;
    }

    public void detect(){		//is there any food in
        World w = getWorld();	//the organisms fov?
		/*
		 *If this is true, world needs to call org.findTarget(org O)
		 *or something using the organism's setTarget method to
		 *determine which food particle the organism
		 *moves to.
		 */
        ArrayList<WorldItem> visible = w.visibleFood(this);
        if(visible.size() > 0){
            WorldItem f = w.findTarget(this,visible);
            setTarget(f);
            hasTarget = true;
        }
        else{
            hasTarget = false;
        }
    }

    public boolean isCarnivore(){
        return carnivore;
    }
    public int getXPos(){
        return xpos;
    }

    public int getYPos(){
        return ypos;
    }

    public int getSize(){
        return size;
    }

    public int getOrientation(){
        return dir;
    }

    public int getFOV(){
        return fov;
    }

    public int getRes(){
        return res;
    }

    public int getHealth(){
        return health;
    }

    public int getRange(){
        return range;
    }

    public int getNightVision(){
        return nightVision;
    }

    public World getWorld(){
        return world;
    }

    public String getType(){
        return "Organism";
    }

    public void dump(){
        System.out.println("Position: " + getXPos() + " " + getYPos());
        System.out.println("Radius: " + getSize());
        System.out.println("FOV: " + getFOV());
        System.out.println("Resolution: " + getRes());
        System.out.println("Health: " + getHealth());
        System.out.println("Range: " + getRange());
        System.out.println("Night Vision " + getNightVision());
        if(isCarnivore()){
            System.out.println("Scavenger");
        }
        else
            System.out.println("Herbivore");
        System.out.println();
    }
}