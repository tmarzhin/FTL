package weapon;

public class ArmeLaser extends Laser {

	/**
	 * An "ArmeLaser" is a weapon that shoots lasers
	 * If it hits a module it deals a certain amount of damage to it and to the hull
	 * If it hits a shield the shield is deactivated
	 */
	public ArmeLaser() {
		name = "Laser";
		requiredPower = 1;
		chargeTime = 2;
		shotPerCharge = 2;
	}
	
}
