package display;

public abstract class Button {

	private 		double			t = 0;
	private final	Vector2<Double> pos;
	private final 	Vector2<Double> dim;
	private 		boolean			draw;
	private 		boolean 		running = true;
	private final 	Thread			thread;
	
	public Button(Vector2<Double> pos, Vector2<Double> dim) { this(pos, dim, false); }

	public Button(Vector2<Double> pos, Vector2<Double> dim, boolean draw) {
		this.pos = pos;
		this.dim = dim;
		this.draw = draw;
		thread = new Thread(
				()
				->
				{
					while (running) {
						if (StdDraw.isMousePressed())
							if (
								StdDraw.mouseX() < pos.getX()+dim.getX() && StdDraw.mouseX() > pos.getX()-dim.getX() &&
								StdDraw.mouseY() < pos.getY()+dim.getY()&& StdDraw.mouseY() > pos.getY()-dim.getY() &&
								t < System.currentTimeMillis()-300)
							{
								t = System.currentTimeMillis();
								try {
									if(StdDraw.isLeftClick())
										onLeftClick();
									if(StdDraw.isRightClick())
										onRightClick();
									if(StdDraw.isMiddleClick())
										onMiddleClick();
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}
					}
				}
				);
		thread.start();
	}
	
	protected abstract void onLeftClick();
	protected abstract void onRightClick();
	protected abstract void onMiddleClick();
	
	public void draw() {
		if (draw)
			StdDraw.rectangle(pos.getX(), pos.getY(), dim.getX(), dim.getY());
	}
	
	public Vector2<Double> getPosition(){
		return pos;
	}
	
	public Vector2<Double> getDimension(){
		return dim;
	}
	
	public boolean destroy() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}
}
