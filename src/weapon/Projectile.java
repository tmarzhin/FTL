package weapon;

import java.awt.Color;

import display.StdDraw;
import display.Vector2;
import ship.Tile;

/**
 * A projectile is shot by a weapon at a position and 
 * follows the direction provided at a constant speed.
 */
public abstract class Projectile {
	
	protected 		double 	cSpeed;	// The constant speed
	protected 		double 	x;		// The X position
	protected 		double 	y;		// The Y position
	protected final double 	width;	// The width
	protected final double 	height;	// The height
	protected 		double 	xSpeed;	// The current x speed
	protected 		double 	ySpeed;	// The current y speed
	protected 		Color	color;	// The color 
	protected 		int 	damage;	// The amount of damage the projectile does
	
	public 			boolean hitTarget;		// Whether the projectile had hit the target
	public 			boolean dodged;			// Whether the opponent ship had dodged the projectile
	public 			boolean positionSetted;	// Whether the new position in the opponent rectangle has been setted
	public 			boolean canBeBlocked;	// Whether the projectile could be blocked by a shield
	
	public	final 	double	dodgingProb;		// The probability of dodging the projectile
	public			int 	deactivationTime;	// TODO
	public 			boolean	isVertical;			// Whether to draw the projectile vertically
	
	protected 		Tile	target;	// The target aimed by the projectile
	
	/**
	 * Creates a projectile of the provided dimensions.
	 * @param width of the projectile
	 * @param height of the projectile
	 */
	protected Projectile(double width, double height) {
		this.width = width;
		this.height = height;
		this.dodgingProb = Math.random();
		this.deactivationTime = 0;
	}
	
	/**
	 *  Sets the deactivation time
	 * @param deactivationTime the new deactivation time.
	 */
	public void setDeactivationTime(int deactivationTime) {
		this.deactivationTime = deactivationTime;
	}
	
	/**
	 * Choose if the projectile has been dodged
	 * @param allocatedEnergy TODO
	 * @param isPlayer
	 */
	public void dodged(int allocatedEnergy, boolean isPlayer) {
		dodged = Math.random() <= (double) Math.round((0.05*allocatedEnergy) * 100000) / 100000;
		if(dodged) {
			if(isPlayer)
				computeDirection(new Vector2<Double>(1.0,1.0));
			else
				computeDirection(new Vector2<Double>(0.0,1.0));
		}
	}
	
	/**
	 * Moves the projectile by the time provided.
	 * @param time elapsed time
	 */
	public void step(double time) {
		x += xSpeed*time;
		y += ySpeed*time;
	}
	
	/**
	 * Draws the projectile.
	 */
	public void draw() {
		StdDraw.setPenColor(color);
		if(isVertical)
			StdDraw.filledRectangle(x, y, height/2, width/2);
		else
			StdDraw.filledRectangle(x, y, width/2, height/2);
		StdDraw.setPenColor(StdDraw.BLACK);
	}
	
	/**
	 * set the target aimed by the projectile
	 * @param target the tile aimed by the projectile
	 */
	public void setTarget(Tile target) {
		this.target = target;
	}
	
	/**
	 * Give the Tile aimed by the projectile
	 * @return the Tile aimed by the projectile
	 */
	public Tile getTarget() {
		return target;
	}
	
	/**
	 * Gives the position of the projectile
	 * @return the position of the projectile
	 */
	public Vector2<Double> getPosition() {
		return new Vector2<Double>(x, y);
	}

	/**
	 * Sets the position of the projectile. 
	 * @param x X position
	 * @param y Y position
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Checks whether the projectile is out of the screen.
	 * @return whether the projectile is out of the screen
	 */
	public boolean isOutOfScreen() {
		return x > 1 || y > 1 || x < 0 || y < 0;
	}
	
	/**
	 * Checks whether the projectile is out of a provided rectangle.
	 * @param xCenter X center of the rectangle
	 * @param yCenter Y center of the rectangle
	 * @param halfWidth half of the width of the rectangle
	 * @param halfHeight half of the height of the rectangle
	 * @return whether the projectile is out of the rectangle
	 */
	public boolean isOutOfRectangle(double xCenter, double yCenter, double halfWidth, double halfHeight) {
		return x < xCenter-halfWidth || x > xCenter+halfWidth || y < yCenter-halfHeight || y > yCenter+halfHeight;
	}
	
	/**
	 * Given a location to target, compute the direction of the projectile.
	 * @param targetPos the target to aim at
	 */
	public void computeDirection(Vector2<Double> targetPos) {
		double norm = Math.sqrt(Math.pow((targetPos.getX() - x),2) + Math.pow(targetPos.getY() - y, 2));
		this.xSpeed = (targetPos.getX() - x)/norm;
		this.ySpeed = (targetPos.getY() - y)/norm;
		this.xSpeed *= cSpeed;
		this.ySpeed *= cSpeed;
	}
	
	/**
	 * Gives the amount of damage the projectile does.
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}
	
}
