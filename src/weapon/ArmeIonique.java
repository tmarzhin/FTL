package weapon;

public class ArmeIonique extends Ion{
	/**
	 * An "ArmeIonique" is a gun which shots ion projectiles
	 * It can temporary deactivate a module when hit
	 * Or deactivate a shield
	 * But it deals no damage to a module or to the hull
	 */
	public ArmeIonique(int weaponControlAllocatedenergy) {
		name = "Ionisator";
		requiredPower = 1;
		chargeTime = 4;
		shotDamage = 0;
		shotPerCharge = 1;
		timeLapse = weaponControlAllocatedenergy;
	}
	
}
