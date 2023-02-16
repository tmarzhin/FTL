package module;

import display.Vector2;

public class Motor extends Module {
	
	/**
	 * Construct a Motor owned by the player or the opponent.
	 * The Motor's tile is drawn at the location given in tilePos.
	 * The Motor's HUD is drawn at the location given in hudPos.
	 * The initialLevel of the Motor is the amount of energy it
	 * can allocated when created.
	 * @param hudPos position at which to draw the HUD
	 * @param tilePos position at which to draw the tile
	 * @param isPlayer whether it belongs to the player
	 * @param initialLevel initial amount of energy which can be allocated
	 * @param amountWeapons the size of the weapon inventory
	 */
	public Motor(Vector2<Double> hudPos, Vector2<Double> tilePos, boolean isPlayer, int initialLevel) {
		super(hudPos, tilePos, isPlayer);
		name = "Motor";
		maxLevel = 8;
		currentLevel = initialLevel > maxLevel ? maxLevel: initialLevel;
		allocatedEnergy = 0;
		amountDamage = 0;
		canBeManned = true;
	}
}
