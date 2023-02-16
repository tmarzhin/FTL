package main;
import display.StdDraw;

/**
 * This class starts the game by creating the canvas
 * in which the game will be drawn in and the world as
 * well as the main loop of the game.
 */
public class Start {
	
	public static void main(String[] args) {
		// Creates the canvas of the game
		StdDraw.setCanvasSize(700, 700);
		
		// Enables double buffering to allow animation
		StdDraw.enableDoubleBuffering();
		
		// Creates the world
		World w = new World();
		
		// Game infinite loop
		while(!w.player.isDestroyed()) {
			
			// Clears the canvas of the previous frame
			StdDraw.clear();
			
			// Processes the key pressed during the last frame
			w.processKey();
			
			// Makes a step of the world
			w.step();
			
			// Draws the world to the canvas
			w.draw();
			
			// Shows the canvas to screen
			StdDraw.show();
			
			// Waits for 20 milliseconds before drawing next frame.
			StdDraw.pause(20);
		}
		
		StdDraw.clear();
		StdDraw.text(0.5, 0.5, "Tu es mort :/");
		StdDraw.show();
		
	}

}
