package module;

import java.util.LinkedList;
import java.util.List;

import display.StdDraw;
import display.Vector2;

public class Shield extends Module {
		
	private int level;
	private double currentChargeTime;
	private List<Integer> charge;
	private List<Integer> discharge;
	
	private int differentChargeTime;
	
	/**
	 * Construct the shield owned by the player or the opponent.
	 * The Shield tile is drawn at the location given in tilePos.
	 * The shield HUD is drawn at the location given in hudPos.
	 * The initialLevel of the WeaponControl is the amount of energy it
	 * can allocated when created.
	 * @param hudPos position at which to draw the HUD
	 * @param tilePos position at which to draw the tile
	 * @param isPlayer whether it belongs to the player
	 * @param initialLevel initial amount of energy which it can provide
	 */
	public Shield(Vector2<Double> hudPos, Vector2<Double> tilePos, boolean isPlayer, int initialLevel) {
		super(hudPos, tilePos, isPlayer);
		name = "Shield";
		maxLevel = 8;
		currentLevel = initialLevel > maxLevel ? maxLevel: initialLevel;
		allocatedEnergy = 0;
		amountDamage = 0;
		canBeManned = true;
		level = 0;
		currentChargeTime = 0;
		charge = new LinkedList<Integer>();
		discharge = new LinkedList<Integer>();
		differentChargeTime = 0;
	}
	
	public void addShield() {
		if(allocatedEnergy % 2 == 1)
			discharge.add((allocatedEnergy + 1) / 2);
	}
	
	public void removeShield() {
		if(allocatedEnergy != 0 && allocatedEnergy % 2 == 0) {
			if(!discharge.isEmpty())
				discharge.remove(discharge.size() - 1);
			else
				charge.remove(0);
		}
	}
	
	/**
	 * Give whether the shield is fully charged
	 * @return whether the shield is charged at his right level
	 */
	public boolean charged() {
		if(allocatedEnergy % 2 == 0) {
			if(allocatedEnergy / 2 < level)
				level = allocatedEnergy / 2;
			return allocatedEnergy / 2 == level;
		} else {
			if((allocatedEnergy + 1) / 2 < level)
				level = (allocatedEnergy + 1) / 2;
			return (allocatedEnergy + 1) / 2 == level;
		}
	}
	
	public double chargeTime(int level) {
		if(differentChargeTime != 0)
			return differentChargeTime;
		else if((level + 1)*2 <= allocatedEnergy)
			return 1.5;
		else
			return 2.0;
	}
	
	/**
	 * Charges the shield
	 * @param time the charging time to increase the shield's charge by
	 */
	public void chargeShield(double time) {
		if(disabled)
			return;
		if(!charged()) {
			if(currentChargeTime + time >= (double) chargeTime(discharge.get(0)) - (0.05 * getCrewMembers().size())) {
				level++;
				charge.add(discharge.get(0));
				discharge.remove(0);
				currentChargeTime = 0;
				differentChargeTime = 0;
			} else
				currentChargeTime += time;
		}
	}
	
	/**
	 * Give whether the a shield is activated
	 * @return whether a shield is activated
	 */
	public boolean shieldActive() {
		return level > 0;
	}
	
	/**
	 * deactivates a shield
	 */
	public void deactivate() {
		level--;
		discharge.add(charge.get(charge.size() - 1));
		charge.remove(charge.size() - 1);
	}
	
	public void setChargeTime(int time) {
		differentChargeTime = time;
	}
	
	/**
	 * Draw the shield(s) according to the number of level
	 * @param pos the position to draw the shield(s)
	 * @param isPlayer whether the shield is own by the player or the opponent
	 */
	public void draw(Vector2<Double> pos, boolean isPlayer) {
		if(isPlayer) {
			StdDraw.setPenColor(StdDraw.BOOK_BLUE);
			StdDraw.rectangle(0.1, 0.9, 0.05, 0.005);
			if(!discharge.isEmpty())
				StdDraw.filledRectangle(0.1, 0.9, (currentChargeTime/chargeTime(discharge.get(0)))*0.055, 0.005);
			StdDraw.setPenColor(StdDraw.BLACK);
		}
		for(int i = 0; i < level; i++) {
			switch(charge.get(i)) {
			case 1: 
				StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
				break;
			case 2: 
				StdDraw.setPenColor(StdDraw.BOOK_BLUE);
				break;
			case 3: 
				StdDraw.setPenColor(StdDraw.BLUE);
				break;
			case 4: 
				StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
				break;
			}
			if (isPlayer)
				StdDraw.ellipse(pos.getX(), pos.getY(), 0.14 + 0.01*i, 0.095 + 0.01*i);
			else
				StdDraw.ellipse(pos.getX(), pos.getY(), 0.095 + 0.01*i, 0.14 + 0.01*i);
			StdDraw.setPenColor(StdDraw.BLACK);
		}
	}
	
}
