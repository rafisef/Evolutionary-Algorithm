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


public class World {
    private ArrayList<Organism> organisms;
    private ArrayList<Food> foods;

    // Initialize our food and organisms which make up our world
    public World() {
        organisms = new ArrayList<Organism>();
        foods = new ArrayList<Food>();
    }

    // Add the organism to the arrayList
    public void placeOrganism() {
        Organism o = new Organism(this, false);
        organisms.add(o);
    }

    // Add the food to the arrayList
    public void placeFood() {
        Food f = new Food(this,(int)Math.floor(Math.random()*100),(int)Math.floor(Math.random()*100));
        foods.add(f);
    }

    public void remove(WorldItem i){
        if(i.getType().equals("Organism")){
            organisms.remove(i);
        }
        else if(i.getType().equals("Food")){
            foods.remove(i);
        }
    }

    // Check if we're over food
    public boolean checkOverlap(Organism o, WorldItem f) {
        double distance = getDistance(f.getXPos(),o.getXPos(),f.getYPos(),o.getYPos());
        if (distance < (o.getSize()+1)) {
            return true;
        } else {
            return false;
        }
    }

    // I'm not sure if I'm handling the resolution right, but I think this works
    // Check if we are over the expanded fuzzy "food area"
    public boolean checkOverlap(Organism o, WorldItem f,int res) {
        double distance = getDistance(f.getXPos(),o.getXPos(),f.getYPos(),o.getYPos());
        if (distance-(f.getSize()*res-f.getSize()) < o.getSize()) {
            return true;
        } else {
            return false;
        }
    }

    // Return the food that is closest to us, but also visible
    public WorldItem findTarget(Organism o, ArrayList<WorldItem> visibleFood) {
        int orientation = o.getOrientation();
        int fov = o.getFOV();
        WorldItem closest = new Food(this, 0, 0);
        double minDist = -1;
        for (WorldItem f : visibleFood) {
            double angleToFood = Math.toDegrees(Math.atan2(f.getXPos()-o.getXPos(),f.getYPos()-o.getYPos()));
            if(angleToFood > orientation+fov || angleToFood < orientation-fov) {
                double distance = getDistance(f.getXPos(),o.getXPos(),f.getYPos(),o.getYPos());
                if (distance < minDist || minDist == -1) {
                    minDist = distance;
                    closest = f;
                }
            }
        }

        return closest;
    }

    private double getDistance(int x1, int x2, int y1, int y2) {
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    // Return an array list of all the food the organism can see
    public ArrayList<WorldItem> visibleFood(Organism o) {
        int orientation = o.getOrientation();
        int fov = o.getFOV();
        ArrayList<WorldItem> visibleToOrg = new ArrayList<WorldItem>();
        for (Food f : foods) {
            double angleToFood = Math.toDegrees(Math.atan2(f.getXPos()-o.getXPos(),f.getYPos()-o.getYPos()));
            if(angleToFood > orientation+fov || angleToFood < orientation-fov) {
                visibleToOrg.add(f);
            }
        }

        if(o.isCarnivore()){
            for (Organism org : organisms) {
                if(!org.isAlive()){
                    double angleToFood = Math.toDegrees(Math.atan2(org.getXPos()-o.getXPos(),org.getYPos()-o.getYPos()));
                    if(angleToFood > orientation+fov || angleToFood < orientation-fov) {
                        visibleToOrg.add(org);
                    }
                }
            }
        }
        return visibleToOrg;
    }

    public ArrayList<Organism> select() {
        ArrayList<Organism> bestOrganisms = new ArrayList<Organism>();


        for (int i = 0; i<10; i++) {
            // I picked a large negative number, probably better to do this
            // a different way
            int curMaxHealth = -10000000;
            int index=-1;
            for(int j = 0; j<organisms.size();j++) {
                int health = organisms.get(j).getHealth();
                if (health > curMaxHealth) {
                    curMaxHealth = health;
                    index = j;
                }
            }
            bestOrganisms.add(organisms.get(index));
        }
        return bestOrganisms;
    }

    public ArrayList<Organism> breed() {
        ArrayList<Organism> curGen;
        ArrayList<Organism> nextGen = new ArrayList<Organism>();
        ArrayList<Organism> curParents = new ArrayList<Organism>();
        curGen = select();
        for (Organism o : curGen) {
            if (curParents.size() == 2) {
                Organism parent1 = curParents.get(0);
                Organism parent2 = curParents.get(1);
                // 240 is 11110000 in binary, so AND then XOR will leave the first
                // 4 digits as 0. Shifting right 4, then left 4 will leave the second
                // 4 digits 0. OR performs the switch.
                int newTheta1 = (((parent1.getFOV()>>4)<<4) | parent2.getFOV()&15);
                int newTheta2 = (((parent2.getFOV()>>4)<<4) | parent1.getFOV()&15);
                int newRes1 = (((parent1.getRes()>>2)<<2) | parent2.getRes()&3);
                int newRes2 = (((parent2.getRes()>>2)<<2) | parent1.getRes()&3);
                // Mutate and get a new random number
                double mutationRate = Math.random();
                if (mutationRate > .5) { //originally .1
                    newTheta1 = (int) Math.floor(mutationRate*40);
                    newRes1 = 1+(int) Math.floor(mutationRate*9);
                }
                if (mutationRate > .5) { //originally .1
                    newTheta2 = (int) Math.floor(mutationRate*40);
                    newRes2 = 1+(int) Math.floor(mutationRate*9);
                }
                // Add to the nextGen
                nextGen.add(new Organism(this, newTheta1, (int)(Math.random()*30), (int)(Math.random()*10), newRes1, (int) Math.floor(Math.random()*365), (int) Math.floor(Math.random()*100), (int) Math.floor(Math.random()*100), (int)(Math.random()*10)+1, parent1.isCarnivore() || parent2.isCarnivore()));
                nextGen.add(new Organism(this, newTheta2, (int)(Math.random()*30), (int)(Math.random()*10), newRes2, (int) Math.floor(Math.random()*365), (int) Math.floor(Math.random()*100), (int) Math.floor(Math.random()*100), (int)(Math.random()*10)+1, parent1.isCarnivore() || parent2.isCarnivore()));
                curParents.clear();

            } else {
                curParents.add(o);
            }
        }
        organisms = new ArrayList<Organism>(nextGen);
        for (Organism o : nextGen) {
            organisms.add(o);
        }
        return nextGen;
    }

    public ArrayList<Organism> getOrganisms(){
        return organisms;
    }

    public ArrayList<Food> getFood() {
        return foods;
    }

}