package ship;

import display.StdDraw;
import display.Vector2;

/**
 * A CrewMember is a character inside the ship.
 */
public class CrewMember {
	
	private String 	name;		// The name of the crew member
	private boolean isSelected; // Whether he/she is selected
	
	/**
	 * Creates a CrewMember.
	 * @param name the name the crew member
	 */
	public CrewMember(String name) {
		this.name = name;
	}

	/**
	 * Draws the CrewMember at the location provided.
	 * @param location where to draw the crew member
	 */
	public void draw(Vector2<Double> location) {
		if (isSelected) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.circle(location.getX(), location.getY(), 0.006);
		}
		StdDraw.setPenColor(StdDraw.BOOK_BLUE);
		StdDraw.filledRectangle(location.getX(), location.getY(), 0.005, 0.0025);
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		StdDraw.filledRectangle(location.getX(), location.getY(), 0.0025, 0.0025);
		StdDraw.setPenColor(StdDraw.BLACK);
	}
	
	/**
	 * Selects the crew member.
	 */
	public void select() {
		isSelected = true;
	}
	
	/**
	 * Unselects the crew member.
	 */
	public void unselect() {
		isSelected = false;
	}
	
	/**
	 * Gives the name of the crew member.
	 * @return the name of the crew member.
	 */
	public String getName() {
		return name;
	}
}
