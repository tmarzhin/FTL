package ship;
import display.Vector2;
import module.Module;
import module.Motor;
import module.Reactor;
import module.Shield;
import module.WeaponControl;
import weapon.ArmeIonique;
import weapon.ArmeLaser;
import weapon.ArmeMissile;
import weapon.DummyGun;
import weapon.Weapon;

public class DummyShip extends Ship {
	
	public DummyShip(boolean isPlayer, Vector2<Double> position) {
		// Creates the ship
		super(isPlayer, position);
		
		// Sets the characteristics of the ship.
		totalHull 		= 30;
		currentHull		= 30;
		modules = new Module[4];
		
		// Creates the tiles for the layout of the ship
		
		weaponControl = new WeaponControl(new Vector2<Double>(0.08, 0.015), getNextTilePosition(), isPlayer, 1, 4);
		addTile(weaponControl);
		weaponControl.setIsFirst();
		
		Tile front = new Tile(getNextTilePosition(), isPlayer);
		addTile(front);
//		front.setIsFirst();
		
		reactor = new Reactor(new Vector2<Double>(0.025, 0.015), getNextTilePosition(),isPlayer, 3);
		addTile(reactor);
//		
//		Tile mid = new Tile(getNextTilePosition(), isPlayer);
//		addTile(mid);
		
//		weaponControl = new WeaponControl(new Vector2<Double>(0.08, 0.015), getNextTilePosition(), isPlayer, 1, 4);
//		addTile(weaponControl);
		
//		Tile back = new Tile(getNextTilePosition(), isPlayer);
//		addTile(back);
		
		shield = new Shield(new Vector2<Double>(0.135, 0.015), getNextTilePosition(), isPlayer, 1);
		addTile(shield);
		
		motor = new Motor(new Vector2<Double>(0.19, 0.015), getNextTilePosition(), isPlayer, 1);
		addTile(motor);
		
		// Assigns the modules
		modules[0] = reactor;
		modules[1] = weaponControl;
		modules[2] = shield;
		modules[3] = motor;
		
		// Creates the gun of the ship
		//Weapon w = new DummyGun();
		Weapon w1 = new ArmeLaser();
		Weapon w2 = new ArmeIonique(weaponControl.getAllocatedEnergy());
		Weapon w3 = new ArmeMissile();
		// Assigns the gun to the weapon control
//		if (weaponControl.addWeapon(w))
//			front.setWeapon(w);
		if (weaponControl.addWeapon(w1))
			reactor.setWeapon(w1);
		if (weaponControl.addWeapon(w2))
			weaponControl.setWeapon(w2);
		if (weaponControl.addWeapon(w3))
			motor.setWeapon(w3);
		
		// Places the weapon at the front
//		front.setWeapon(w);
//		reactor.setWeapon(w1);
//		weaponControl.setWeapon(w2);
//		motor.setWeapon(w3);
		
		// Adds a crew member to the ship
		addCrewMember(new CrewMember("Jose"));
	}
	
	private Vector2<Double> getNextTilePosition() {
		if (isPlayer)
			return new Vector2<Double>(position.getX()-(layout.size()*0.02), position.getY()); 
		else
			return new Vector2<Double>(position.getX(), position.getY()-(layout.size()*0.02));
	}

}