package lineage.bean.database;

public class Drop {
	private String Name;
	private String MonName;
	private String ItemName;
	private int ItemBress;
	private int CountMin;
	private int CountMax;
	private int Chance;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getMonName() {
		return MonName;
	}
	public void setMonName(String monName) {
		MonName = monName;
	}
	public String getItemName() {
		return ItemName;
	}
	public void setItemName(String itemName) {
		ItemName = itemName;
	}
	public int getItemBress() {
		return ItemBress;
	}
	public void setItemBress(int itemBress) {
		ItemBress = itemBress;
	}
	public int getCountMin() {
		return CountMin;
	}
	public void setCountMin(int countMin) {
		CountMin = countMin;
	}
	public int getCountMax() {
		return CountMax;
	}
	public void setCountMax(int countMax) {
		CountMax = countMax;
	}
	public int getChance() {
		return Chance;
	}
	public void setChance(int chance) {
		Chance = chance;
	}
	
}
