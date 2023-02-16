package main;
import java.util.Collection;
import java.util.Random;

import javax.swing.text.Position;

import display.Button;
import display.StdDraw;
import display.Vector2;
import main.World.ModuleButton;
import module.Module;
import module.Motor;
import module.WeaponControl;
import ship.CrewMember;
import ship.DummyShip;
import ship.DummyShipOpponent;
import ship.Ship;
import ship.Tile;
import weapon.ArmeIonique;
import weapon.ArmeLaser;
import weapon.DummyGun;
import weapon.Projectile;
import weapon.Weapon;

/**
 * The world contains the ships and draws them to screen.
 */
public class World {
	
	private Bindings 		bind;					// The bindings of the game.
	private long 			time;					// The current time 
	
	Ship player;									// The ship of the player
	Ship opponent;									// The ship of the opponent
	
	private int				turn; 					// The number of times the player has defeated an enemy.
	private ModuleButton[]	buttons = null;			// the array of the buttons used to choose the reward at the end of a fight.
	boolean 				hasChoosed = false; 	// Whether the player has choose his upgrade
	String					lastReward = null;		// The name of the last reward given randomly
	private Random 			rand;					// Instance of Random to generate random integers.
	
	/**
	 * Creates the world with the bindings, the player ship
	 * and the opponent ship.
	 */
	public World() {
		bind = new Bindings(this);
		player = new DummyShip(true, new Vector2<Double>(0.3, 0.5));
		opponent = new DummyShipOpponent(false, new Vector2<Double>(0.8, 0.5));
		time = System.currentTimeMillis();
		turn = 1;
		rand = new Random();
	}
	
	/**
	 * Processes the key pressed.
	 */
	public void processKey(){
		this.bind.processKey();
	}
	
	/**
	 * Makes a step in the world.
	 */
public void step() {
		
		if (opponent.isDestroyed()) {
			turn ++;
			player.resetTarget();
			if (lastReward == null )
				randomReward();
			
			StdDraw.text(0.3, 0.65, "You found " + lastReward);
			destroyButtons();
			drawHudEnd();
			
			if (hasChoosed) {
				if (player.getTarget() != null)
					player.getTarget().unmarkTarget();
				player.resetTarget();
				opponent.repairHull();
				opponent.getWeaponControl().setMissile(1);
				randomRewardOpponent();
				destroyButtons(); // Pour supprimer les boutons et Ã©viter de recliquer entre deux images et s'amÃ©liorer deux fois
				lastReward = null;
//				player.setCurrentHull();
				hasChoosed = false;
			}
			
			
		} else {
			player.step(((double) (System.currentTimeMillis()-time))/1000);
			opponent.step(((double) (System.currentTimeMillis()-time))/1000);
			
			opponent.ai(player);
			
			processHit(player.getProjectiles(), true);
			processHit(opponent.getProjectiles(), false);
			
		}
		
		StdDraw.rectangle(0.9, 0.88, 0.08, 0.08);
		StdDraw.line(0.82, 0.88, 0.98, 0.88);
		
		StdDraw.text(0.9, 0.93, "Click on a tile to");
		StdDraw.text(0.9, 0.90, "teleport a member");
		
		StdDraw.text(0.9, 0.85, "Cheats:");
		StdDraw.text(0.9, 0.82, "Press C or V");
		
		time = System.currentTimeMillis();
		
	}

	/**
	 * Choose a random reward for the Opponent
	 * @return the name of the reward.
	 */
	
	public void randomRewardOpponent() {
	   int n = rand.nextInt(3);
	   switch (n) {
	   case 0: {
		   Weapon w = null;
		   WeaponControl weaponControl = opponent.getWeaponControl() ;
		   int r = rand.nextInt(3);
		   switch (r) {
		   case 0: {
			   w = new ArmeLaser();
			   break;
	       }
		   case 1: {
			   w = new ArmeIonique(weaponControl.getAllocatedEnergy());
			   break;
		   }
		   case 2: {
			   w = new DummyGun();
			   break;
			   }
		   }
		   if (weaponControl != null) {
			   Tile t = opponent.getEmptyWeaponTile();
			   if (t != null ) {
				   if (weaponControl.addWeapon(w))
					   t.setWeapon(w);
	               }
	           }
		   break;
		   }
	   case 1: {
		   CrewMember member = new CrewMember("Zorg"); // TODO gérer si c'est c'est plein
		   opponent.addCrewMember(member);
		   break;
	   }
	   case 2:{
		   WeaponControl weaponControl = opponent.getWeaponControl();
		   if (weaponControl != null )
			   weaponControl.addMissile();
		   break;
		   }
	   default: {
		   break;
		   }
	   }
	   Module[] modules = opponent.getModules() ;
	   n = rand.nextInt(modules.length);
	   modules[n].levelUp();
	}
	
	/**
 	* draws the Hud at the end of a fight
 	* and creates the buttons to choose upgrades.
 	*/

	public void drawHudEnd () {
		
		StdDraw.text(0.3, 0.7, "Upgrade your ship");
		
		Module[] playerModules = player.getModules();
		
		buttons = new ModuleButton[playerModules.length];
		
		for (int i = 0 ; i < playerModules.length; i++) {
			
			buttons[i] = new ModuleButton(new Vector2<Double>(0.15+(0.1*i), 0.31),new Vector2<Double>(0.05, 0.05),i);
			buttons[i].draw();
			if(playerModules[i].getName() != null)
				StdDraw.text(0.15+(0.1*i), 0.31, playerModules[i].getName());
			else
				StdDraw.text(0.15+(0.1*i), 0.31, "Reactor");
		}
	}
	
	/**
	 * Choose a random reward
	 * @return the name of the reward. 
	 */
	
	public void randomReward() {
		
		int n = rand.nextInt(4);
		
		switch (n) {
			case 0: {
				
				Weapon w = null;
				boolean set = false;
				WeaponControl weaponControl = player.getWeaponControl() ;
				int r = rand.nextInt(3);
				
				switch (r) {
					case 0: {
						w = new ArmeLaser();
						break;
					}
					case 1: {
						w = new ArmeIonique(weaponControl.getAllocatedEnergy());
						break;
					}
					case 2: {
						w = new DummyGun();
						break;
					}
				}
				if (weaponControl != null) {
					
					Tile t = player.getEmptyWeaponTile();
					
					if (t != null ) {
						set = weaponControl.addWeapon(w);
						if (set)
							t.setWeapon(w);
					}
				}
				lastReward =  "a new " + w.getName() + ".";
				if (!set) {
					lastReward += " But you don't have space for it." ;
					break;
				}
				
				break;
			}
			case 1: {
				int r = rand.nextInt(5)+1;
				player.repairHull(r);
				lastReward =  "materials, your hull has been repaired by " + r + ".";
				break;
			}
			case 2: {
				CrewMember member = new CrewMember("Jean-Emmanuel"); // TODO gérer si c'est c'est plein
				player.addCrewMember(member);
				lastReward = "a new crew member.";
				break;
			}
			case 3: {
				WeaponControl weaponControl = player.getWeaponControl() ;
				boolean done = false;
				if (weaponControl != null ) {
					done = weaponControl.addMissile();
					
				}
				lastReward = "an additional missile.";
				if (!done)
					lastReward += " But you don't have a weaponControl."; 
				break;
			}
			default: {
				lastReward = "nothing: Error !";
				break;
			}
		}
	}
	
	
	/**
	 * Processes the projectiles hit
	 * @param projectiles collection of projectiles to check for hit
	 * @param isPlayer whether the own of the projectiles is the player
	 */
	private void processHit(Collection<Projectile> projectiles, boolean isPlayer) {
		for(Projectile p : projectiles) {
			if(isPlayer) {
				opponent.applyDamage(p, p.getTarget());
				if((p.dodged && p.isOutOfRectangle(player.getPosition().getX(), player.getPosition().getY(), 0.25, 0.25)) || p.hitTarget) {
					projectiles.remove(p);
					return;
				}
			} else {
				player.applyDamage(p, p.getTarget());
				if((p.dodged && p.isOutOfRectangle(opponent.getPosition().getX(), opponent.getPosition().getY() - 0.05, 0.15, 0.25)) || p.hitTarget) {
					projectiles.remove(p);
					return;
				}
			}
		}
//		for(Projectile p : projectiles) {
//			if(isPlayer) {
//				Module[] m = opponent.getModules();
//				for(int i = 0; i < m.length; i++)
//					if(m[i] instanceof Motor && !p.done) {
//						p.dodged(m[i].getAllocatedEnergy(), isPlayer);
//						p.done = true;
//					}
//				if(!p.dodged && p.hitTarget) {
//					opponent.applyDamage(p, player.getTarget());
//					projectiles.remove(p);
//					return;
//				}
//			} else {
//				Module[] m = player.getModules();
//				for(int i = 0; i < m.length; i++)
//					if(m[i] instanceof Motor && !p.done) {
//						p.dodged(m[i].getAllocatedEnergy(), isPlayer);
//						p.done = true;
//					}
//				if(!p.dodged && p.hitTarget) {
//					player.applyDamage(p, opponent.getTarget());
//					projectiles.remove(p);
//					return;
//				}
//			}
//		}
	}
	
	/**
	 * Draws the ships and HUDs.
	 */
	public void draw() {
		player.draw();
		player.drawHUD();
		
		opponent.draw();
		opponent.drawHUD();
		
	}
	
	/**
	 * A ModuleButton is a button associated with a module to teleport a member of its ship.
	 */
	
	public class ModuleButton extends Button {
		
		int indexModule;
		
		public ModuleButton (Vector2<Double> pos, Vector2<Double> dim, int index) {
			super(pos,dim,true);
			this.indexModule = index;
		}

		@Override
		protected void onLeftClick() {
			if (player.modulesMaxed())
				hasChoosed = true;
			
			if (!hasChoosed) {
				if ( player.getModules()[indexModule].levelUp() ) {
					hasChoosed = true;
				}
			}
		}

		@Override
		protected void onRightClick() {
			
		}

		@Override
		protected void onMiddleClick() {
			
		}
		
	}
	
	/**
	 * Destroys all ModuleButtons.
	 */
	
	public void destroyButtons() {
		if (buttons != null) {
			for (int i = 0 ; i < buttons.length; i++) {
				buttons[i].destroy();
			}
		}
	}
}
