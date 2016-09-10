/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolutionary;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

/**
 *
 * @authors Jordan Anderson & Reid Anderson & Rafael Seferyan
 */

public class Evolutionary {

    /**
     * @param args the command line arguments
     */


    public static void main(String[] args) throws IOException {
        final int NUMBER_OF_ITERATIONS = 10000;
        // TODO code application logic here
        World ourWorld = new World();
        // Place all of the food
        for (int i=0;i<8000;i++) {
            ourWorld.placeFood();
        }
        // Place all the original organisms
        for (int j=0;j<20;j++) {
            ourWorld.placeOrganism();
        }

        int count = 1;
        /*for(Organism o: organisms){
        	System.out.println("Organism " + count + ":");
        	count ++;
        	o.dump();
        }*/

        File file = new File("Evolutionary.txt"); // Creating a file to out put numbers to
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("Generation \t Resolution\n");

        //Run the simulation k times
        int genNum = 0;
        boolean isDone = false;
        ArrayList<Organism> organisms;
        organisms = ourWorld.getOrganisms();
        while(!isDone) {
            if (genNum > NUMBER_OF_ITERATIONS) {
                isDone = true;
            }
            boolean allDead = true;
            while (!allDead) {
                for (Organism o : organisms) {
                    if (o.getHealth() > 0) {
                        allDead = false;
                    }
                    o.step();
                }
            }

            organisms = ourWorld.breed();

            ArrayList<Food> foodList = ourWorld.getFood();
            int newFood = 500-foodList.size();
            for(int i = 0;i<newFood;i++) {
                ourWorld.placeFood();
            }

            //OUTPUTTING ALL WINNING ORGANISMS AFTER EVERY GENERATION
            // May have to delete
            double temp=0.0; // make a temp variable to help calculate the average
            DecimalFormat df = new DecimalFormat("#.00"); // makes the decimals format properly
            df.setRoundingMode(RoundingMode.CEILING);
            for(Organism o: organisms){
                temp = temp + o.getRes();
                //writer.write(o.getSize()+"\n");
            }
            temp = temp/organisms.size();
            //System.out.println("The average size of generation " + genNum + " is " + temp);
            String formatStr = "%-12s %-12s\n";
            writer.write(String.format(formatStr,genNum,df.format(temp)));
            //***
            //writer.write("\n"); // adds a space to separate the generations
            genNum++;
            System.out.println(genNum);
        }

        ArrayList<Organism> organisms2 = ourWorld.getOrganisms();

        for(Organism o: organisms2){
            System.out.println("Organism " + count + ":");
            count ++;
            o.dump();

        }
        writer.flush();
        writer.close(); // closes the file

    }
}