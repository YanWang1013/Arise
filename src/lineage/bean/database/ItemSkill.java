package lineage.bean.database;

public class ItemSkill {
	private String name;
	private String item;
	private int minDmg;
	private int maxDmg;
	private int ranged;
	private int skill;
	private int duration;
	private int effect;
	private boolean effectTarget;		// true:me, false:target
	private int chance;
	private int element;				// 
	private String option;
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public int getChance() {
		return chance;
	}
	public void setChance(int chance) {
		this.chance = chance;
	}
	public int getSkill() {
		return skill;
	}
	public void setSkill(int skill) {
		this.skill = skill;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMinDmg() {
		return minDmg;
	}
	public void setMinDmg(int minDmg) {
		this.minDmg = minDmg;
	}
	public int getMaxDmg() {
		return maxDmg;
	}
	public void setMaxDmg(int maxDmg) {
		this.maxDmg = maxDmg;
	}
	public int getRanged() {
		return ranged;
	}
	public void setRanged(int ranged) {
		this.ranged = ranged;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getEffect() {
		return effect;
	}
	public void setEffect(int effect) {
		this.effect = effect;
	}
	public boolean isEffectTarget() {
		return effectTarget;
	}
	public void setEffectTarget(boolean effectTarget) {
		this.effectTarget = effectTarget;
	}
	public int getElement() {
		return element;
	}
	public void setElement(int element) {
		this.element = element;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
}
