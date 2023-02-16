package ship;
import java.util.LinkedList;
import java.util.List;

import display.StdDraw;
import display.Vector2;
import weapon.Weapon;

/**
 * A tile is a cell of the ship's layout.
 * A weapon can be attached to the tile and a crew member 
 * can be on the tile.
 */
public class Tile {
	
	private 		Weapon 			weapon;		// The weapon assigned to the tile
	private 		List<CrewMember>members;	// The crew member on the tile
	private 		boolean 		isAimed;	// Whether the tile aimed at
	private 		boolean 		isPlayer;	// Whether the tile is owned by the player
	protected final Vector2<Double> tilePos;	// The position of the tile
	protected 		boolean 		isFirstTile; // Whether the tile is the first tile of its ship
	
	/**
	 * Creates a tile for the player of the opponent
	 * which is drawn at the given position.
	 * @param position location to draw the tile
	 * @param isPlayer whether it is owned by the player ship
	 */
	public Tile(Vector2<Double> position, boolean isPlayer) {
		this.tilePos = position;
		this.isPlayer = isPlayer;
		this.isFirstTile = false;
		members = new LinkedList<CrewMember>();
	}
	
	/**
	 * Checks whether a crew member is inside the tile.
	 * @return whether the tile has a crew member
	 */
	public boolean hasCrewMember() {
		return !members.isEmpty();
	}
	
	public boolean hasWeapon() {
		return weapon != null;
	}
	
	/**
	 * Sets the given crew member has inside the tile.
	 * @param member the crew member to put inside the tile
	 */
	public void setCrewMember(CrewMember member) {
		this.members.add(member);
	}
	
	/**
	 * Draws the tile, the member inside and the weapon.
	 */
	public void draw() {
		if (tilePos == null)
			return;
		
		double x = tilePos.getX();
		double y = tilePos.getY();
		if (weapon != null) {
			if (isPlayer) {
				if (isFirstTile)
					drawWeaponHorizontalPlayer(x,y);
				else
					drawWeaponVerticalPlayer(x,y);
			} else {
				if (isFirstTile)
					drawWeaponVerticalOpponent(x,y);
				else
					drawWeaponHorizontalOpponent(x,y);
			}
		}
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.filledRectangle(x-0.01, y-0.01, 0.01, 0.01);
		StdDraw.setPenColor(StdDraw.BLACK);
		drawHorizontalWall(x,y);
		y-=0.02;
		drawVerticalWall(x,y);
		drawHorizontalWall(x,y);
		x-=0.02;
		drawVerticalWall(x,y);
		y+=0.02;
		x+=0.02;
		if (isAimed) {
			if(isPlayer)
				StdDraw.setPenColor(StdDraw.PINK);
			else
				StdDraw.setPenColor(StdDraw.RED);
			StdDraw.filledRectangle(x-0.01, y-0.01, 0.01, 0.01);
			StdDraw.setPenColor(StdDraw.BLACK);
		}
		if(!members.isEmpty())
			members.get(0).draw(getCenterPosition()); //TODO changer cette ligne pour afficher tous les membres d'ï¿½quipages
		StdDraw.setPenColor(StdDraw.BLACK);
	}
	
	public void setIsFirst() {
		this.isFirstTile = true;
	}
	
	/**
	 * Draws a wall of the tile horizontally.
	 * @param x X start position
	 * @param y Y start position
	 */
	private void drawHorizontalWall(double x, double y) {
			StdDraw.line(x-0.005, y, x-0.015, y);
			StdDraw.line(x, y, x-0.005, y);
			StdDraw.line(x-0.015, y, x-0.02, y);
	}
	
	/**
	 * Draws a wall of the tile vertically.
	 * @param x X start position
	 * @param y Y start position
	 */
	private void drawVerticalWall(double x, double y) {
			StdDraw.line(x, y+0.005, x, y+0.015);
			StdDraw.line(x, y, x, y+0.005);
			StdDraw.line(x, y+0.015, x, y+0.02);
	}
	
	/**
	 * Draws the weapon of the tile horizontally.
	 * @param x X start position
	 * @param y Y start position
	 */
	private void drawWeaponHorizontalPlayer(double x, double y) {
		StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
		StdDraw.filledRectangle(x+0.01, y-0.01, 0.01, 0.005);
		StdDraw.setPenColor(StdDraw.BLACK);
	}
	
	private void drawWeaponVerticalPlayer(double x, double y) {
		StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
		

		StdDraw.filledRectangle(x-0.0075, y+0.0115, 0.0075, 0.00375);
		
		
		StdDraw.filledRectangle(x-0.0125, y+0.0075, 0.00375, 0.0075);
		
		StdDraw.setPenColor(StdDraw.BLACK);
	}
	
	/**
	 * Draws the weapon of the tile vertically
	 * @param x X start position
	 * @param y Y start position
	 */
	private void drawWeaponVerticalOpponent(double x, double y) {
		StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
		StdDraw.filledRectangle(x-0.01, y+0.01, 0.005, 0.01);
		StdDraw.setPenColor(StdDraw.BLACK);

	}
	
	private void drawWeaponHorizontalOpponent(double x, double y) {
		StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
		
		StdDraw.filledRectangle(x+0.0075, y-0.015, 0.0075, 0.00375);
		
		
		StdDraw.filledRectangle(x+0.01125, y-0.00875, 0.00375, 0.0075);
		
		
		StdDraw.setPenColor(StdDraw.BLACK);
	}

	/**
	 * Assigns a weapon to the tile.
	 * @param w the weapon to assign
	 */
	public void setWeapon(Weapon w) {
		if (weapon == null)
			weapon = w;
	}

	/**
	 * Gives the assigned weapon.
	 * @return the weapon
	 */
	public Weapon getWeapon() {
		return weapon;
	}

	/**
	 * Gives the horizontal position of the weapon.
	 * @return the position
	 */
	private Vector2<Double> getWeaponHorizontalPosition() {
		return new Vector2<Double>(tilePos.getX()+0.01, tilePos.getY()-0.01);
	}
	
	/**
	 * Gives the vertical position of the weapon.
	 * @return the position
	 */
	private Vector2<Double> getWeaponVerticalPosition() {
		return new Vector2<Double>(tilePos.getX()-0.01, tilePos.getY()+0.01);
	}
	
	/**
	 * Gives the position of the weapon.
	 * @return the position
	 */
	public Vector2<Double> getWeaponPosition() { // TODO Ne marche plus à voir si on en as besoin
		if (isPlayer)
			return getWeaponHorizontalPosition();
		else	
			return getWeaponVerticalPosition();
	}

	/**
	 * Gives the position of the tile.
	 * @return the position
	 */
	public Vector2<Double> getPosition() {
		return tilePos;
	}
	
	/**
	 * Gives the center position of the tile.
	 * @return the position
	 */
	public Vector2<Double> getCenterPosition() {
		return new Vector2<Double>(tilePos.getX()-0.01, tilePos.getY()-0.01);
	}
	
	/**
	 * Marks the tile as targeted.
	 */
	public void markTarget() {
		isAimed = true;
	}
	
	/**
	 * Unmarks the tile as targeted.
	 */
	public void unmarkTarget() {
		isAimed = false;
	}
	
	/**
	 * Checks whether the given crew member is the on in the tile.
	 * @param member the crew member to compare it to
	 * @return whether the crew member is the one in the tile
	 */
	public boolean isCrewMember(CrewMember member) {
		return this.members.contains(member);
	}

	/**
	 * Removes the crew member of the tile.
	 */
	public void removeCrewMember(CrewMember selected) {
		members.remove(selected);
	}
	
	public List<CrewMember> getCrewMembers () { return members; }
	
}
