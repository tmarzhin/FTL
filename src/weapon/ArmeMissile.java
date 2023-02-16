package weapon;

public class ArmeMissile extends Missile {

	/**
	 * An "ArmeMissile" is a weapon that shoots Missiles
	 * It deals damage to modules and hull or deactivate the shield if touched
	 * It has a limited amount of ammo, but new ammo can be found at the end of a fight.
	 */
	public ArmeMissile() {
		name = "Missile";
		requiredPower = 1;
		chargeTime = 3;
		shotDamage = 4;
		shotPerCharge = 1;
		available = 3;
	}
	
	public void fired() {
		available--;
	}
	
	
}
