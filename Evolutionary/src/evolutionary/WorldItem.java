package evolutionary;

public interface WorldItem {	//for organisms, food, and anything else
    //we might place in the world
    public int getXPos();

    public int getYPos();		//I only put accessors instead of properties since
    //we have default return values instead of assignments
    public int getSize();		//for things like food size

    public String getType();
    public World getWorld();
}