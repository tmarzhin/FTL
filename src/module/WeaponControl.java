package module;
import display.Button;
import display.StdDraw;
import display.Vector2;
import ship.Tile;
import weapon.ArmeMissile;
import weapon.Ion;
import weapon.Laser;
import weapon.Missile;
import weapon.Projectile;
import weapon.Weapon;

/**
 * A WeaponControl is a Module which handles weapons energy and activation.
 * This module has a specific HUD to display the weapons along with buttons
 * to interact with them.
 */
public class WeaponControl extends Module {
	
	private Weapon[] 		weapons;	// The weapon slots
	private WeaponButton[] 	weaponBtns;	// The button linked to the weapon slot
	
	/**
	 * A WeaponButton is an implementation of a Button
	 * which activates/deactivates the linked weapon when
	 * left/right clicked.
	 */
	private class WeaponButton extends Button {
		
		private int weaponIndex;
		
		public WeaponButton(Vector2<Double> pos, Vector2<Double> dim, int weaponIndex) {
			super(pos, dim);
			this.weaponIndex = weaponIndex;
		}

		@Override
		protected void onLeftClick() {
			activeWeapon(weaponIndex);
		}

		@Override
		protected void onRightClick() {
			deactiveWeapon(weaponIndex);
		}

		@Override
		protected void onMiddleClick() {}
		
	}
	
	/**
	 * Construct a WeaponControl owned by the player or the opponent.
	 * The WeaponControl's tile is drawn at the location given in tilePos.
	 * The WeaponControl's HUD is drawn at the location given in hudPos.
	 * The initialLevel of the WeaponControl is the amount of energy it
	 * can allocated when created.
	 * The amountWeapons defines the size of the weapon inventory.
	 * @param hudPos position at which to draw the HUD
	 * @param tilePos position at which to draw the tile
	 * @param isPlayer whether it belongs to the player
	 * @param initialLevel initial amount of energy which can be allocated
	 * @param amountWeapons the size of the weapon inventory
	 */
	public WeaponControl(Vector2<Double> hudPos, Vector2<Double> tilePos, boolean isPlayer, int initialLevel, int amountWeapons) {
		super(hudPos, tilePos, isPlayer);
		name = "Weapon";
		maxLevel = 4;
		currentLevel = initialLevel > maxLevel ? maxLevel: initialLevel;
		allocatedEnergy = 0;
		amountDamage = 0;
		canBeManned = true;
		weapons = new Weapon[amountWeapons];
		weaponBtns = new WeaponButton[amountWeapons];
	}
	
	/**
	 * Adds a weapon in the weapon inventory.
	 * @param w the weapon to add 
	 * @return whether the weapon has been added
	 */
	public boolean addWeapon(Weapon w) {
		int i;
		for (i = 0; i < weapons.length; i++) {
			if (weapons[i] == null)
				break;
		}
		if (i == weapons.length)
			return false;
		weapons[i] = w;
		return true;
	}

    /**
     * Sets the number of missile of the Missile weapon
     * @param n the new number of missile
     */
    public void setMissile(int n) {
        for (int i =0; i<weapons.length; i++) {
            if (weapons[i] instanceof Missile) {
                 ((Missile) weapons[i]).setMissile(n);
            }
        }
    }
	
	/**
	 * Activates the weapon.
	 * @param weapon the index in the inventory of the weapon
	 * @return whether the weapon has been activated
	 */
	public boolean activeWeapon(int weapon) {
		if(!disabled) {
			if (weapons[weapon] == null || (weapons[weapon] instanceof Missile && !((Missile) weapons[weapon]).isAvailable()))
				return false;
			int energy = 0;
			for (Weapon w : weapons)
				if (w != null)
					energy += w.isActivated() ? w.getRequiredPower() : 0;
			Weapon w = weapons[weapon];
			if ( (currentLevel-amountDamage < energy + w.getRequiredPower()) || allocatedEnergy < energy + w.getRequiredPower() )
				return false;
			w.activate();
			return true;
		} else
			return false;
	}
	
	/**
	 * Deactivates the weapon.
	 * @param weapon the index in the inventory of the weapon
	 */
	public void deactiveWeapon(int weapon) {
		weapons[weapon].deactive();
	}
	
	/**
	 * Gives the weapon of the inventory
	 * @param index location of the weapon in the inventory
	 * @return the weapon at location index
	 */
	public Weapon getWeapon(int index) {
		return weapons[index];
	}
	
	 /**
     * Adds a number of missile to the Missile weapon
     * @return whether the operation was done.
     */
	public boolean addMissile() {
		for (int i =0; i<weapons.length; i++) {
			if (weapons[i] instanceof Missile) {
				 ((Missile) weapons[i]).addMissile();
				 return true;
			}
		}
		return false;
	}
	
	/**
	 * Charges the weapon.
	 * @param time the charging time to increase the weapon's charge by
	 */
	public void chargeTime(double time) {
		if(!disabled) {
			for (Weapon w : weapons)
				if (w != null) {
					// TODO
					if (w.isActivated())
						w.charge(time);
					else
						w.charge(-3*time);
				}
		}
	}
	
	/**
	 * Draws the weapon inventory and the weapon in it as well
	 * as their charging time.
	 */
	@Override
	public void drawHud() {
		super.drawHud();
		double x = hudPos.getX();
		double y = hudPos.getY();
		StdDraw.rectangle(x+0.135+(0.05*weapons.length), y+0.1, (0.05*weapons.length), 0.04);
		for (int i = 0; i < weapons.length; i++) {
			Weapon w = weapons[i];
			if (w == null)
				continue;
			
			StdDraw.setPenColor(StdDraw.GRAY);
			if (w.getCurrentCharge() == w.getChargeTime())
				StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.filledRectangle(x+0.185+(0.1*i), y+0.1, 0.045, (w.getCurrentCharge()/w.getChargeTime())*0.035);
			if (w.isActivated())
				StdDraw.setPenColor(StdDraw.CYAN);
			if (weaponBtns[i] == null)
				weaponBtns[i] = new WeaponButton(new Vector2<Double>(x+0.185+(0.1*i), y+0.1), new Vector2<Double>(0.045, 0.035), i);
			else
				weaponBtns[i].draw();
			StdDraw.rectangle(x+0.185+(0.1*i), y+0.1, 0.045, 0.035);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(x+0.185+(0.1*i), y+0.1, w.getName());
		}
	}
	
	/**
	 * Shots the weapon from the tile in the direction provided.
	 * @param weapon the weapon to shot
	 * @param tile the tile to shot it from
	 * @param dir the direction in which to shot it
	 * @return the projectile created by the weapon
	 */
	public Projectile shotWeapon(int weapon, Tile tile, Vector2<Double> dir) {
		if(!disabled) {
			if (weapons[weapon] == null || !weapons[weapon].isCharged())
				return null;
			if(weapons[weapon] instanceof Laser) {
				Laser l = (Laser) weapons[weapon];
				l.setDamage(allocatedEnergy);
			}
//			if(weapons[weapon] instanceof Ion) {
//				Ion l = (Ion) weapons[weapon];
//				l.setTimeLapse(allocatedEnergy);
//			}
			Vector2<Double> v = tile.getPosition();
			weapons[weapon].resetCharge();
			return weapons[weapon].shot(new Vector2<Double>(v.getX(), v.getY()), dir);
		} else
			return null;
	}
	
	/**
	 * Removes energy for the WeaponControl and deactivates
	 * weapons if energy is not sufficient anymore. 
	 */
	@Override
	public boolean removeEnergy() {
		if (allocatedEnergy > 0) {
			--allocatedEnergy;
			int energy = 0;
			for (Weapon w : weapons)
				if (w != null)
					energy += w.isActivated() ? w.getRequiredPower() : 0;
			Weapon last = null;
			if (energy > allocatedEnergy)
				for (Weapon w : weapons)
					if (w != null && w.isActivated())
						last = w;
			if (last != null)
				last.deactive();
			return true;
		}
		return false;
	}
	
	/**
	 * Add a bonus to all weapons.
	 */
	public void bonusCrewMember() {
		for(Weapon w : weapons)
			w.setBonusCrewMembers(0.05*getCrewMembers().size());
	}

	public int getWeaponLength() {
		return weapons.length;
	}
	
}
