package weapon;

import display.StdDraw;
import display.Vector2;

public abstract class Missile extends Weapon {
	
	int available;
	
	private class MissileProjectile extends Projectile {
		public MissileProjectile(Vector2<Double> pos, Vector2<Double> dir) {
			super(0.01, 0.01);
			this.x = pos.getX();
			this.y = pos.getY();
			this.cSpeed = 0.2;
			this.xSpeed = dir.getX()*cSpeed;
			this.ySpeed = dir.getY()*cSpeed;
			this.color = StdDraw.LIGHT_GRAY;
			this.damage = shotDamage;
			this.canBeBlocked = false;
		}
	}
	
	@Override
	public Projectile shot(Vector2<Double> pos,Vector2<Double> dir ) {
		available -- ;
		if (!isAvailable())
				this.deactive();
		return new MissileProjectile(pos, dir);
	}
	
	public boolean isAvailable() { return available > 0; }
	
	public void addMissile() {
		available++;
	}
	
	 public void setMissile(int n) {
	        available = n;
	    }
}
