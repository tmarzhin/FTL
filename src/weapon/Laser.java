package weapon;

import display.StdDraw;
import display.Vector2;

public abstract class Laser extends Weapon{

	private class LaserProjectile extends Projectile {
		public LaserProjectile(Vector2<Double> pos, Vector2<Double> dir) {
			super(0.03, 0.005);
			this.x = pos.getX();
			this.y = pos.getY();
			this.cSpeed = 0.5;
			this.xSpeed = dir.getX()*cSpeed;
			this.ySpeed = dir.getY()*cSpeed;
			this.color = StdDraw.RED;
			this.damage = shotDamage;
			this.canBeBlocked = true;
		}
	}
	
	public void setDamage(int allocatedEnergy) {
		shotDamage = allocatedEnergy;
	}
	
	@Override
	public Projectile shot(Vector2<Double> pos,Vector2<Double> dir ) {
		return new LaserProjectile(pos, dir);
	}
}
