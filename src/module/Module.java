package module;
import display.StdDraw;
import display.Vector2;
import ship.Tile;

/**
 * A module is an implementation of Tile which handles energy.
 * This tile has a HUD to display its current energy level.
 */
public abstract class Module extends Tile {

	protected	String				name;				// Name of the module
	protected	int 				maxLevel;			// Maximum level of the module
	protected 	int 				currentLevel;		// Current level of the module
	protected 	int 				allocatedEnergy;	// Amount of energy allocated to the module
	protected 	int					amountDamage;		// Amount of damage done to the module
	protected  	boolean 			canBeManned; 		// Can a crew member man this module
	protected 	Vector2<Double> 	hudPos;				// HUD position of the module
	protected	int					repairTime;
	protected	double				currentRepair;
	
	public 		boolean				disabled;				// Whether the module is disabled
	private 	int					chargeTime;				// Deactivation time 
	private 	double				currentCharge;			// The current state of charge to activate the module
	
	/**
	 * Construct a module owned by the player or the opponent.
	 * The module's tile is drawn at the location given in tilePos.
	 * The module's HUD is drawn at the location given in hudPos.
	 * @param hudPos position at which to draw the HUD
	 * @param tilePos position at which to draw the tile
	 * @param isPlayer whether it belongs to the player
	 */
	public Module(Vector2<Double> hudPos, Vector2<Double> tilePos, boolean isPlayer) {
		super(tilePos, isPlayer);
		this.hudPos = hudPos;
		repairTime = 2;
		currentRepair = repairTime*(currentLevel - amountDamage);
		disabled = false;
	}
	
	/**
	 * Increments energy allocated to the module.
	 * @return whether the energy has been added or not
	 */
	public boolean addEnergy() {
		if (allocatedEnergy < currentLevel - amountDamage) {
			allocatedEnergy++;
			return true;
		}
		return false;
	}
	
	/**
	 * Decrements energy allocated to the module.
	 * @return whether the energy has been added or not
	 */
	public boolean removeEnergy() {
		if (allocatedEnergy > 0) {
			--allocatedEnergy;
			return true;
		}
		return false;
	}
	
	/**
	 * Repairs the module by the time provided
	 * @param time The time provided
	 */
	public void repairTime(double time) {
        if (amountDamage!=0 && getCrewMembers().size() != 0) {
                double repairTimeDamagedLvl = repairTime*(currentLevel-amountDamage); // time to repair all damaged levels
                if (currentRepair-repairTimeDamagedLvl + (this.getCrewMembers().size() * time ) < repairTime) { // On ne peut pas encore r�parer un niveau enti�rement
                    currentRepair += (this.getCrewMembers().size() * time );
                } else { // un niveau r�par� ( et peu �tre un peu plus )
                    currentRepair += (this.getCrewMembers().size() * time );
                    amountDamage--;
                    if (currentRepair >= repairTime*currentLevel) currentRepair = repairTime*currentLevel; // pour ne pas d�passer si jamais on se fait endommager � nouveau
                }
        }
    }
	
	// Draw Methods
	
	/**
	 * Draw the module's tile. 
	 */
	@Override
	public void draw() {
		super.draw();	
		if (name != null && name.length() > 0)
			StdDraw.text(tilePos.getX()-0.01, tilePos.getY()-0.01, ""+name.charAt(0));
	}
	
	/**
	 * Draw the module's HUD.
	 */
	public void drawHud() {
		double x = hudPos.getX();
		double y = hudPos.getY();
		if (getName() != null)
			StdDraw.text(x, y, getName());
		int j = allocatedEnergy;
		int k = currentLevel - amountDamage;
		for (int i = 1; i <= currentLevel; i++)
			if(k-- <= 0) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.filledRectangle(x, (y+0.01)+(i*0.015), 0.015, 0.005);
				StdDraw.setPenColor(StdDraw.BLACK);
			} else if (j-- > 0) {
				StdDraw.filledRectangle(x, (y+0.01)+(i*0.015), 0.015, 0.005);
			} else
				StdDraw.rectangle(x, (y+0.01)+(i*0.015), 0.015, 0.005);
	}

	/**
	 * Gives the name of the module.
	 * @return name of the module
	 */
	public String getName() {
		return name;
	}

	/////////////
	// Getters //
	/////////////
	
	public int 		getMaxLevel() 			{ return maxLevel;			}
	public int 		getCurrentLevel()		{ return currentLevel; 		}
	public int		getAllocatedEnergy()	{ return allocatedEnergy;	}
	public int		getAmountDamage()		{ return amountDamage;		}
	public boolean 	getCanBeManned() 		{ return canBeManned; 		}
	
	/**
	 * Deals damage to the module and refresh his current repair
	 * @param damage the amount of damage to deal
	 */
	
	public void setDamage(int damage) {
		amountDamage += damage;
		if(amountDamage > currentLevel)
			amountDamage = currentLevel;
		currentRepair = repairTime*(currentLevel - amountDamage);
	}
	
	/**
	 * Level up a module if it is not already at his max level.
	 * @return whether the operation has been done
	 */
	
	public boolean levelUp() {
		if (currentLevel == maxLevel)
			return false;
		currentLevel ++;
		return true;
	}
	
	/**
	 * @return whether the module is at his max level.
	 */
	
	public boolean isMaxed() {
		return maxLevel == currentLevel;
	}
	
	/**
	 * Sets the current level to the max level of the module
	 */
	public void maxLevel() {
		currentLevel=maxLevel;
	}
	
	/**
	 * @return whether the module is not damaged.
	 */
	public boolean isRepaired() {
		return amountDamage <= 0 ;
	}

	/**
	 * Temporary deactivates the module.
	 * @param chargeTime the time to reactivate the module
	 */
	
	public void deactivate(int chargeTime) {
		this.disabled = true;
		this.chargeTime = chargeTime;
		System.out.println(chargeTime);
	}
	
	/**
	 * Refresh the time remaining to activate the module
	 * Activates the module if possible
	 * @param time the time elapsed
	 */
	
	public void activate(double time) {
		if (currentCharge + time >= chargeTime) {
			disabled = false;
			currentCharge = 0;
		}
		else {
			currentCharge = currentCharge + time;
			System.out.println(currentCharge + time);
		}
	}
	
}
