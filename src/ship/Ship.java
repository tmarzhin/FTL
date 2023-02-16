package ship;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import display.Button;
import display.StdDraw;
import display.Vector2;
import module.Module;
import module.Motor;
import module.Reactor;
import module.Shield;
import module.WeaponControl;
import weapon.Ion;
import weapon.Projectile;
import weapon.Weapon;

/**
 * A ship is the composed of modules, tiles and crew members.
 * The ship has a hull that can be damaged by an opponent's ship.
 */
public abstract class Ship {
	
	protected Vector2<Double>			position;		// The position of the ship
	protected int						totalHull;		// The total hull integrity of the ship
	protected int						currentHull;	// The current hull integrity of the ship
	
	protected Reactor					reactor;		// The reactor of the ship
	protected WeaponControl				weaponControl;	// The weapon control system
	protected Shield					shield;			// The shield system
	protected Motor						motor;			// The motor system
	
	protected Collection<CrewMember> 	crew;			// The crew members in the ship
	protected Collection<Tile>			layout;			// The layout of the ship
	protected boolean					isPlayer;		// Whether this ship is owned by the player
	protected Module[]					modules;		// The modules on the ship
	protected Collection<Projectile>	projectiles;	// The projectiles shot by the ship
	protected Tile						target;			// The targeted tile of the enemy ship
	protected CrewMember				selectedMember; // The currently selected crew member
	
	private Random ran;									// Instance of Random used to generate random numbers
	private Collection <ShipButton> shipBtns;			// The clicable buttons on the ship to teleport a member
	
	/**
	 * Creates a Ship for the player or the opponent at the provided position.
	 * @param isPlayer whether the ship is for the player
	 * @param position the location to create it
	 */
	public Ship(boolean isPlayer, Vector2<Double> position) {
		this.isPlayer = isPlayer;
		this.position = position;
		crew = new ArrayList<CrewMember>();
		projectiles = new ArrayList<Projectile>();
		layout = new ArrayList<Tile>();
		shipBtns = new ArrayList <ShipButton>();
		ran = new Random();
	}
	
	
	/**
	 * Processes the action of the AI.
	 * @param player the enemy of the AI
	 */
	public void ai(Ship player) {
		if (isPlayer)
			return;
		else {
	           
            //allocation uniforme des niveaux
            if (reactor!=null){
                int m = 0;
                for (int lvl = 0; lvl<reactor.getAllocatedEnergy()+1 ;lvl++) {
                    if (!(modules[m%modules.length] instanceof Reactor) && reactor.getAllocatedEnergy() >0) {
                        if (modules[m%modules.length].addEnergy()) {
                        	if(modules[m%modules.length] instanceof Shield)
                        		shield.addShield();
                            reactor.removeEnergy();
                        }
                    }
                    m++;
                }
            }
           
            // chargement de toutes les armes et tir
            if (weaponControl !=null) {
                for (int i = 0; i<weaponControl.getWeaponLength(); i++) {
                    if (weaponControl.getWeapon(i)!=null) {
                        //weaponControl.getWeapon(i).activate();
                    	weaponControl.activeWeapon(i);
                    	//target=player.getFirstTile(); // A changer plus tard et rajouter un .marketTarget() ?
                        target =(Tile) player.getLayout()[ran.nextInt(5)];
                    	//target = (Tile) player.getLayout()[3];
                        shotWeapon(i);
                    }
                }
            }
            
           // R�paration automatique : t�l�portation des membres disponibles dans les modules endommag�s
            int n = 0;
    		for ( int i=0; i<layout.size();i++) {
    			Tile currentTile = ((ArrayList<Tile>) layout).get(i);
    			if ( currentTile instanceof Module && !((Module) currentTile).isRepaired()) {
    				if (n<((ArrayList<CrewMember>) crew).size()) {
    					CrewMember currentMember = ((ArrayList<CrewMember>) crew).get(n);
    					selectedMember = currentMember;
    					teleportSelected(i);
    					selectedMember = null;
    					n++;
    				} else return;
    			}
    		}
        }
	}
	

    /**
     * Fully repairs the hull
     */
    public void repairHull() {
        currentHull = totalHull;
    }
	
	
	/**
	 * Processes the time elapsed between the steps.
	 * @param elapsedTime the time elapsed since the last step
	 */
	public void step(double elapsedTime) {
		chargeWeapons(elapsedTime);
		processProjectiles(elapsedTime);
		repairModules(elapsedTime);
		chargeShield(elapsedTime);
	}
	
	public void chargeShield(double time) {
		if ( shield != null)
			shield.chargeShield(time);
	}
	
	public void repairModules(double time) {
        for (int i=0; i < modules.length;i++) {
            if (modules[i]!=null) {
            	if(modules[i].disabled)
            		modules[i].activate(time);
                modules[i].repairTime(time);
            }
        }
    }
	
	// Drawing Methods
	
	/**
	 * Draws the ship and all its components.
	 */
	public void draw() {
		if (isPlayer)
			StdDraw.picture(getCenterPosition().getX(), getCenterPosition().getY(), "Ship.png", 0.32, 0.2235, 0);
		else {
			StdDraw.picture(getCenterPosition().getX(), getCenterPosition().getY(), "Ship.png", 0.32, 0.2235, 90);
		}
		if (!isDestroyed()) {
			drawTiles();
			if (shield != null)
				shield.draw(getCenterPosition(), isPlayer);
			for (Projectile p : projectiles) {
				p.draw();
			}
		} else if (!isPlayer ) {
			StdDraw.picture(getCenterPosition().getX()-0.04, getCenterPosition().getY()-0.07, "explosion.png",0.05,0.05);
			StdDraw.picture(getCenterPosition().getX()+0.03, getCenterPosition().getY()+0.05, "explosion.png",0.05,0.05);
		}
	}

	/**
	 * Draw the tiles of the ship.
	 */
	private void drawTiles() {
		for (Tile t : layout)
			t.draw();
	}

	/**
	 * Draws the HUD of the ship.
	 */
	public void drawHUD() {
		if (isPlayer)
			drawPlayerHUD();
		else
			drawOpponentHUD();
	}
	
	/**
	 * Draws the HUD of the player.
	 */
	private void drawPlayerHUD() {
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.rectangle(position.getX(), position.getY(), 0.25, 0.25);
		StdDraw.setPenColor(StdDraw.BLACK);
		// Modules
		for (Module m : modules)
			m.drawHud();
		// Hull
		StdDraw.text(0.025, 0.97, "Hull");
		int j = currentHull;
		for (int i = 1; i <= totalHull; i++)
			if (j > 0) {
				StdDraw.filledRectangle(0.05+(i*0.015), 0.97, 0.005, 0.015);
				j--;
			} else
				StdDraw.rectangle(0.05+(i*0.015), 0.97, 0.005, 0.015);
		
	}
	
	/**
	 * Draws the HUD of the opponent.
	 */
	private void drawOpponentHUD() {
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.rectangle(position.getX(), position.getY() - 0.05, 0.15, 0.25);
		StdDraw.setPenColor(StdDraw.BLACK);
		int j = currentHull;
		for (int i = 1; i <= totalHull; i++)
			if (j > 0) {
				StdDraw.filledRectangle(0.67+(i*0.0075), 0.75, 0.0025, 0.0075);
				j--;
			} else
				StdDraw.rectangle(0.67+(i*0.0075), 0.75, 0.0025, 0.0075);
	}
	
	// Crew Methods
	
	/**
	 * Check if a crew member is currently selected.
	 * @return whether a crew member is selected
	 */
	public boolean isCrewMemberSelected() {
		return selectedMember != null;
	}

	/**
	 * Unselects the selected crew member.
	 */
	public void unselectCrewMember() {
		if (!isCrewMemberSelected())
			return;
		selectedMember.unselect();
		selectedMember = null;
	}

	/**
	 * Selects the crew member.
	 * @param i -th crew member
	 */
	public void selectMember(int i) {
		int j = 0;
		for (CrewMember m : crew)
			if (j++ == i) {
				if (selectedMember != null)
					selectedMember.unselect();
				selectedMember = m;
				selectedMember.select();
				return;
			}
				
	}
	
	/**
	 * Adds a crew member to the ship.
	 * @param member the crew member to add
	 */
	public void addCrewMember(CrewMember member) {
		Tile t = getEmptyTile();
		if (t == null) {
			System.err.println("The ship is full ! Sorry " + member.getName() + "...");
			return;
		}
		crew.add(member);
		t.setCrewMember(member);
	}
	
	// Layout Methods
	
	/**
	 * Give the layout of the ship
	 * @return the layout of the ship
	 */
	public Object[] getLayout() { return layout.toArray(); }
	
	/**
	 * Adds a tile to the ship.
	 * @param t the tile to add
	 */
	protected void addTile(Tile t) {
		layout.add(t);
		shipBtns.add(new ShipButton( t.getCenterPosition(),new Vector2<Double> (0.01,0.01),layout.size()-1));
		
	}
	
	private Vector2<Double> getCenterPosition() {
		double x = 1.0;
		if (isPlayer) {
			for(Tile t : layout) {
				if (t.getPosition().getX() - 0.02 < x)
					x = t.getPosition().getX() - 0.02;
			}
			return new Vector2<Double>((x + position.getX()) / 2, position.getY() - 0.01);
		} else {
			for(Tile t : layout) {
				if (t.getPosition().getY() - 0.02 < x)
					x = t.getPosition().getY() - 0.02;
			}
			return new Vector2<Double>(position.getX() - 0.01, (x + position.getY()) / 2);
		}
	}
	
	/**
	 * Gives an empty tile of the ship
	 * @return a tile empty of crew member
	 */
	private Tile getEmptyTile() {
		Iterator<Tile> it = layout.iterator();
		while(it.hasNext()) {
			Tile t = it.next();
			if (!t.hasCrewMember())
				return t;
		}
		return null;
	}
	
	/**
	 * Gives the first tile of the ship.
	 * @return the first tile of the ship
	 */
	private Tile getFirstTile() {
		return layout.iterator().next();
	}
	
	// Energy Methods

	/**
	 * Decreased the energy allocated in the module. 
	 * @param module the module to remove energy from
	 */
	public void removeEnergy(int module) {
		if (module >= modules.length)
			return;
		if (modules[module].removeEnergy())
			reactor.addEnergy();
		if (modules[module] instanceof Shield)
			shield.removeShield();
	}
	
	/**
	 * Increases the energy allocated in the module.
	 * @param module the module to add energy to
	 */
	public void addEnergy(int module) {
		if (module >= modules.length)
			return;
		if (reactor.getAllocatedEnergy() > 0 && modules[module].addEnergy()) 
			reactor.removeEnergy();	
		if (modules[module] instanceof Shield)
			shield.addShield();
	}
	
	/**
	 * Gives the array of module of the ship
	 * @return modules of this ship
	 */
	public Module[] getModules() {
		return modules;
	}
	
	/**
	 * @return whether all the modules of the ship are at their max level.
	 */
	public boolean modulesMaxed() {
		boolean res = true;
		
		for ( int i = 0 ; i < modules.length; i++) {
			res = res && modules[i].isMaxed();
		}
		return res;
		
	}
	
	// Weapon Methods
	
	/**
	 * Deactivates a weapon. 
	 * @param weapon the weapon to deactivate
	 */
	public void deactiveWeapon(int weapon) {
		weaponControl.deactiveWeapon(weapon);
	}
	
	/**
	 * Activates a weapon. 
	 * @param weapon the weapon to activate
	 */
	public void activeWeapon(int weapon) {
		weaponControl.activeWeapon(weapon);
	}
	
	/**
	 * Charges the weapons by the time provided
	 * @param time the time to charge the weapons by
	 */
	public void chargeWeapons(double time) {
		weaponControl.chargeTime(time);
	}

	/**
	 * Shots a weapon.
	 * @param weapon the weapon to shot
	 */
	public void shotWeapon(int weapon) {
		if(target!=null) {
			Projectile p = null;
			if(isPlayer) {
				p = weaponControl.shotWeapon(weapon, getWeaponTile(weaponControl.getWeapon(weapon)), new Vector2<Double>(position.getX() + 0.7, 0.0));
				if(p != null) p.isVertical = false;
			} else {
				p = weaponControl.shotWeapon(weapon, getWeaponTile(weaponControl.getWeapon(weapon)), new Vector2<Double>(0.0, position.getY()));				
				if(p != null) p.isVertical = true;
			}
			if (p != null) {
				if(weaponControl.getWeapon(weapon) instanceof Ion)
					p.setDeactivationTime(weaponControl.getAllocatedEnergy());
				p.setTarget(target);
				projectiles.add(p);
			}
		}
	}
	
	/**
	 * Gives the tile on which the weapon is.
	 * @param w the weapon to get the tile from
	 * @return the tile on which the weapon is attached
	 */
	private Tile getWeaponTile(Weapon w) {
		for (Tile t : layout)
			if (t.getWeapon() == w)
				return t;
		return null;
	}
	
	/**
	 *  Gives a tile with no weapon attached
	 * @return an empty tile or null if all tiles have a weapon attached
	 */
	public Tile getEmptyWeaponTile() {
		if (weaponControl == null)
			return null;
		for (Tile t : layout)
			if (t.getWeapon() == null)
				return t;
		return null;
	}
	
	/**
	 * Gives the weaponControl of the ship.
	 * @return the weaponControl of the ship.
	 */
	public WeaponControl getWeaponControl() {
		return weaponControl;
	}
	
	// Projectile Methods
	
	/**
	 * Processes the projectiles with the time elapsed since
	 * the last processing.
	 * @param elapsedTime the time elapsed since the last call
	 */
	private void processProjectiles(double elapsedTime) {
		for (Projectile p : projectiles) {
			if (p != null) {
				p.step(elapsedTime);
				if(!p.dodged) {
					if(isPlayer) {
						if(!p.positionSetted && p.isOutOfRectangle(position.getX(), position.getY(), 0.25, 0.25)) {
							setPosDir(p);
							p.positionSetted = true;
						} else
						if(!p.isOutOfRectangle(p.getTarget().getCenterPosition().getX(), p.getTarget().getCenterPosition().getY(), 0.015, 0.015)) {
							p.hitTarget = true;
						}
					} else {
						if(!p.positionSetted && p.isOutOfRectangle(position.getX(), position.getY() - 0.05, 0.15, 0.25)) {
							setPosDir(p);
							p.positionSetted = true;
						} else
						if(!p.isOutOfRectangle(p.getTarget().getCenterPosition().getX(), p.getTarget().getCenterPosition().getY(), 0.015, 0.015)) {
							p.hitTarget = true;
						}
					}
				}
			}
		}
	}
	/**
	 * set
	 * @param p
	 */
	private void setPosDir(Projectile p) {
		double x = 0.0;
		double y = 0.0;
		int edge = ran.nextInt(4);
		if(isPlayer) {
			switch(edge) {
			case 0: 
				x = (double) (ran.nextInt(30) + 65) / 100;
				y = 0.7;
				p.isVertical = true;
				break;
			case 1:
				x = 0.95;
				y = (double) (ran.nextInt(55) + 20) / 100;
				break;
			case 2:
				x = (double) (ran.nextInt(30) + 65) / 100;
				y = 0.2;
				p.isVertical = true;
				break;
			case 3:
				x = 0.65;
				y = (double) (ran.nextInt(55) + 20) / 100;
				break;
			}
		} else {
			switch(edge) {
			case 0: 
				x = (double) (ran.nextInt(50) + 5) / 100;
				y = 0.75;
				break;
			case 1:
				x = 0.55;
				y = (double) (ran.nextInt(50) + 25) / 100;
				p.isVertical = false;
				break;
			case 2:
				x = (double) (ran.nextInt(50) + 5) / 100;
				y = 0.25;
				break;
			case 3:
				x = 0.05;
				y = (double) (ran.nextInt(50) + 25) / 100;
				p.isVertical = false;
				break;
			}
		}
		p.setPosition(x, y);
		p.computeDirection(p.getTarget().getCenterPosition());
	}
	
	/**
	 * Gives the projectiles shot by the ship.
	 * @return A collection of projectiles
	 */
	public Collection<Projectile> getProjectiles(){
		return projectiles;
	}

	/**
	 * Applies the damage a projectile did.
	 * @param p the projectile to process
	 */
	
	public void applyDamage(Projectile p, Tile targeted) {
		if(!motor.disabled && p.dodgingProb <= (0.05*motor.getAllocatedEnergy() + 0.05*motor.getCrewMembers().size())) {
			if(isPlayer)
				p.computeDirection(new Vector2<Double>(0.65,0.7));
			else
				p.computeDirection(new Vector2<Double>(0.55,0.25));
			p.dodged = true;
		} else if(shield != null && p.hitTarget && p.canBeBlocked && shield.shieldActive()) {
			shield.deactivate();
			if(p.deactivationTime > 0)
				shield.setChargeTime(p.deactivationTime);
		} else if(p.hitTarget) {			
			currentHull -= p.getDamage();
			if(targeted instanceof Reactor)
				return;
			else if(targeted instanceof Module) {
				Module m = (Module) targeted;
				m.setDamage(p.getDamage());
				if(p.deactivationTime > 0)
					m.deactivate(p.deactivationTime);
			}
		}
	}
	
	// Hull Methods
	
	/**
	 * @return Whether the integrity of the ship is lower than 0.
	 */
	public boolean isDestroyed() {
		return currentHull <= 0 ;
	}
	
	/**
	 * Repair the hull
	 * @param n the amount of reparation to apply
	 */
	public void repairHull(int n) {
		currentHull += n;
		if ( currentHull > totalHull)
			currentHull = totalHull;
	}
	

	// Other Methods
	
	/**
	 * give the position of the Ship
	 * @return the position of the ship
	 */
	public Vector2<Double> getPosition() { return position; }
	/**
	 * give the target aimed by the ship
	 * @return the target aimed by the ship
	 */
	public Tile getTarget() { return target; }
	
	/**
	 * Sets the target of the ship to null.
	 */
	public void resetTarget() {
		target = null;
	}
	
	/**
	 * Sets all the modules to their max level and fill the reactor
	 */
	public void maxModules() {
		for (int i =0; i<modules.length;i++) {
			modules[i].maxLevel();
		}
		reactor.setFullEnergy();
	}
	
	// Aiming Methods
	
	/**
	 * Aims the guns up.
	 * @param opponent the ship to aim at
	 */
	public void aimUp(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		
		} else {
			target.unmarkTarget();
			Vector2<Double> targetPos = target.getPosition();
			for (Tile t: opponent.layout) {
				Vector2<Double> tilePos = t.getPosition();
				double round = (double)Math.round((targetPos.getY() + 0.02) * 100000) / 100000;
				if (round == tilePos.getY()) {
					target = t;
					target.markTarget();
					return;
				}
			}
		}
	}
	
	/**
	 * Aims the guns down.
	 * @param opponent the ship to aim at
	 */
	public void aimDown(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		} else {
			target.unmarkTarget();
			Vector2<Double> targetPos = target.getPosition();
			for (Tile t: opponent.layout) {
				Vector2<Double> tilePos = t.getPosition();
				double round = (double)Math.round((targetPos.getY() - 0.02) * 100000) / 100000;
				if (round == tilePos.getY()) {
					target = t;
					target.markTarget();
					return;
				}
			}
		}
	}
	
	/**
	 * Aims the guns left.
	 * @param opponent the ship to aim at
	 */
	public void aimLeft(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		} else {
			target.unmarkTarget();
			Vector2<Double> targetPos = target.getPosition();
			for (Tile t: opponent.layout) {
				Vector2<Double> tilePos = t.getPosition();
				double round = (double)Math.round((targetPos.getX() - 0.02) * 100000) / 100000;
				if (round == tilePos.getX()) {
					target = t;
					target.markTarget();
					return;
				}
			}
		}
	}

	/**
	 * Aims the guns right.
	 * @param opponent the ship to aim at
	 */
	public void aimRight(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		} else {
			target.unmarkTarget();
			Vector2<Double> targetPos = target.getPosition();
			for (Tile t: opponent.layout) {
				Vector2<Double> tilePos = t.getPosition();
				double round = (double)Math.round((targetPos.getX() + 0.02) * 100000) / 100000;
				if (round == tilePos.getX()) {
					target = t;
					target.markTarget();
					return;
				}
			}
		}
	}
	
	/**
	 * Teleports the selected member to an other tile
	 * @param index the index of this tile on the layout of the ship
	 */
	public void teleportSelected(int index) {
		
		Tile destination = ((ArrayList<Tile>) layout).get(index);

//		if (selectedMember != null && destination.getCrewMember() == null) {
		if (selectedMember != null) {
		for ( int i =0; i<layout.size();i++) { // On cherche l'index de la case qui contient le selectedMember dans layout.
			Tile current = ((ArrayList<Tile>) layout).get(i);
			
			if (current.getCrewMembers().size() != 0 && current.getCrewMembers().contains(selectedMember)) {
				current.removeCrewMember(selectedMember); // et on retire le membre de cette case mais il reste selectionn�, membre de l'�quipage etc
			}
		}
		
		destination.setCrewMember(selectedMember);
		}
	}
	
	
	/**
	 * A ShipButtons is a button associated with a tile of the ship
	 * When pressed the selected member is teleported to the tile associated with it.
	 */
	
	private class ShipButton extends Button {
		
		int indexLayout;
		
		public ShipButton (Vector2<Double> pos, Vector2<Double> dim, int index) {
			super(pos,dim,true);
			this.indexLayout = index;
		}
		
		
		@Override
		protected void onLeftClick() {
			
			teleportSelected(indexLayout);
			
		}
		
		@Override
		protected void onRightClick() {}
		
		@Override
		protected void onMiddleClick() {}
	}


}
