package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class Npc {
	private String Name;
	private String NameId;
	private String Type;
	private int NameIdNumber;
	private boolean Ai;
	private int Gfx;
	private int GfxMode;
	private int Hp;
	private int Lawful;
	private int Light;
	private int AreaAtk;
	private int arrowGfx;
	private List<Shop> shop_list = new ArrayList<Shop>();

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public String getNameId() {
		return NameId;
	}

	public void setNameId(String nameId) {
		NameId = nameId;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public int getNameIdNumber() {
		return NameIdNumber;
	}

	public void setNameIdNumber(int nameIdNumber) {
		NameIdNumber = nameIdNumber;
	}

	public boolean isAi() {
		return Ai;
	}

	public void setAi(boolean ai) {
		Ai = ai;
	}

	public int getGfx() {
		return Gfx;
	}

	public void setGfx(int gfx) {
		Gfx = gfx;
	}

	public int getGfxMode() {
		return GfxMode;
	}

	public void setGfxMode(int gfxMode) {
		GfxMode = gfxMode;
	}

	public int getHp() {
		return Hp;
	}

	public void setHp(int hp) {
		Hp = hp;
	}

	public int getLawful() {
		return Lawful;
	}

	public void setLawful(int lawful) {
		Lawful = lawful;
	}

	public int getLight() {
		return Light;
	}

	public void setLight(int light) {
		Light = light;
	}

	public int getAtkRange() {
		return AreaAtk;
	}

	public void setAreaAtk(int areaAtk) {
		AreaAtk = areaAtk;
	}

	public int getArrowGfx() {
		return arrowGfx;
	}

	public void setArrowGfx(int arrowGfx) {
		this.arrowGfx = arrowGfx;
	}

	public List<Shop> getShop_list() {
		return shop_list;
	}
	
	public int getBuySize(){
		int size = 0;
		for( Shop s : shop_list ){
			if(s.isItemBuy())
				++size;
		}
		return size;
	}

	public Shop findShop(final int uid){
		for( Shop s : shop_list ){
			if(s.getUid() == uid)
				return s;
		}
		return null;
	}

	public Shop findShopItemId(final String name){
		for( Shop s : shop_list ){
			if(s.getItemName().equalsIgnoreCase(name))
				return s;
		}
		return null;
	}
	
}
