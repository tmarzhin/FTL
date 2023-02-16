package module;

import display.Vector2;

/**
 * A Reactor is a specific module that cannot be interacted with
 * and provides energy throughout the ship. 
 */
public class Reactor extends Module {
	
	/**
	 * Construct a Reactor owned by the player or the opponent.
	 * The Reactor tile is drawn at the location given in tilePos.
	 * The Reactor HUD is drawn at the location given in hudPos.
	 * The initialLevel of the WeaponControl is the amount of energy it
	 * provides in the ship.
	 * @param hudPos position at which to draw the HUD
	 * @param tilePos position at which to draw the tile
	 * @param isPlayer whether it belongs to the player
	 * @param initialLevel initial amount of energy which it can provide
	 */
	public Reactor(Vector2<Double> hudPos, Vector2<Double> tilePos, boolean isPlayer, int initialLevel) {
		super(hudPos, tilePos, isPlayer);
		// The reactor does not have a name to distinct it from the other modules
		// Indeed, this module cannot be destroyed and is 'really' placed in the ship
		name = null;
		maxLevel = 15;
		currentLevel = initialLevel > maxLevel ? maxLevel: initialLevel;
		allocatedEnergy = initialLevel;
		amountDamage = -1;
		canBeManned = false;
	}
	@Override
	public boolean levelUp() {
		if (currentLevel == maxLevel)
			return false;
		currentLevel ++;
		allocatedEnergy++;
		return true;
	}
	
	public void setFullEnergy() {
		allocatedEnergy=currentLevel;
	}
	
	
}
