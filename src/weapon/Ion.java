package weapon;

import display.StdDraw;
import display.Vector2;

public abstract class Ion extends Weapon{
	
	protected int timeLapse;
	
	private class IonProjectile extends Projectile {
		public IonProjectile(Vector2<Double> pos, Vector2<Double> dir) {
			super(0.09, 0.002);
			this.x = pos.getX();
			this.y = pos.getY();
			this.cSpeed = 0.3;
			this.xSpeed = dir.getX()*cSpeed;
			this.ySpeed = dir.getY()*cSpeed;
			this.color = StdDraw.BLUE;
			this.damage = shotDamage;
			this.canBeBlocked = true;
		}
	}
	
	public void setTimeLapse(int allocatedEnergy) {
		timeLapse = allocatedEnergy;
	}
	
	@Override
	public Projectile shot(Vector2<Double> pos,Vector2<Double> dir ) {
		return new IonProjectile(pos, dir);
	}
}

