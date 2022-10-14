package lineage.bean.database;

import lineage.world.object.object;

public class Exp {
	private object o;
	private int level;
	private double exp;
	private double bonus;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public double getExp() {
		return exp;
	}
	public void setExp(double exp) {
		this.exp = exp;
	}
	public double getBonus() {
		return bonus;
	}
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	public void close(){
		exp = bonus = level = 0;
		o = null;
	}
	public object getObject() {
		return o;
	}
	public void setObject(object o) {
		this.o = o;
	}
}
