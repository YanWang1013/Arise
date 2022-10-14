package lineage.bean.database;

import java.util.HashMap;
import java.util.Map;

public class Item {
	private String Name;
	private String NameId;
	private String Type1;
	private String Type2;
	private int NameIdNumber;
	private int Material;
	private int DmgMin;	
	private int DmgMax;	
	private double Weight;
	private int InvGfx;
	private int GroundGfx;
	private int GfxMode;
	private int Action1;
	private int Action2;
	private boolean Sell;
	private boolean Piles;
	private boolean Trade;
	private boolean Drop;
	private boolean Warehouse;
	private boolean Enchant;
	private int SafeEnchant;
	private int Royal;
	private int Knight;
	private int Elf;
	private int Wizard;
	private int DarkElf;
	private int DragonKnight;
	private int BlackWizard;
	private int AddHit;
	private int AddDmg;
	private int Ac;
	private int AddStr;
	private int AddDex;
	private int AddCon;
	private int AddInt;
	private int AddWis;
	private int AddCha;
	private int AddHp;
	private int AddMp;
	private int AddSp;
	private int AddMr;
	private boolean Canbedmg;
	private int LevelMin;
	private int LevelMax;
	private int Effect;
	private boolean Tohand;
	private int SetId;
	private int Continuous;
	private int Waterress;
	private int Windress;
	private int Earthress;
	private int Fireress;
	private double AddWeight;
	private int TicHp;
	private int TicMp;
	private int ShopPrice;
	private int DropChance;
	private int Solvent;
	private int AttributeCrystal;
	private boolean bookChaoticZone;
	private boolean bookLawfulZone;
	private boolean bookMomtreeZone;
	private boolean bookNeutralZone;
	private boolean bookTowerZone;
	private String polyName;
	private boolean isInventorySave;
	private boolean isAqua;
	private int StealHp;
	private int StealMp;

	private int Slot;							// 서버 메모리에 등록되는 슬롯 위치
	private int equippedSlot;					// 클라이언트 스탯창에 장착되는 슬롯 위치
	private Map<String, Integer> list_craft;	// 제작처리 구간에서 사용함. 지급될아이템에 제곱값.
	
	public Item(){
		list_craft = new HashMap<String, Integer>();
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getNameId() {
		return NameId;
	}
	public void setNameId(String nameId) {
		NameId = nameId;
	}
	public String getType1() {
		return Type1;
	}
	public void setType1(String type1) {
		Type1 = type1;
	}
	public String getType2() {
		return Type2;
	}
	public void setType2(String type2) {
		Type2 = type2;
	}
	public int getNameIdNumber() {
		return NameIdNumber;
	}
	public void setNameIdNumber(int nameIdNumber) {
		NameIdNumber = nameIdNumber;
	}
	public int getMaterial() {
		return Material;
	}
	public void setMaterial(int material) {
		Material = material;
	}
	public int getDmgMin() {
		return DmgMin;
	}
	public void setDmgMin(int dmgMin) {
		DmgMin = dmgMin;
	}
	public int getDmgMax() {
		return DmgMax;
	}
	public void setDmgMax(int dmgMax) {
		DmgMax = dmgMax;
	}
	public double getWeight() {
		return Weight;
	}
	public void setWeight(double weight) {
		Weight = weight;
	}
	public int getInvGfx() {
		return InvGfx;
	}
	public void setInvGfx(int invGfx) {
		InvGfx = invGfx;
	}
	public int getGroundGfx() {
		return GroundGfx;
	}
	public void setGroundGfx(int groundGfx) {
		GroundGfx = groundGfx;
	}
	public int getGfxMode() {
		return GfxMode;
	}
	public void setGfxMode(int gfxMode) {
		GfxMode = gfxMode;
	}
	public int getAction1() {
		return Action1;
	}
	public void setAction1(int action1) {
		Action1 = action1;
	}
	public int getAction2() {
		return Action2;
	}
	public void setAction2(int action2) {
		Action2 = action2;
	}
	public boolean isSell() {
		return Sell;
	}
	public void setSell(boolean sell) {
		Sell = sell;
	}
	public boolean isPiles() {
		return Piles;
	}
	public void setPiles(boolean piles) {
		Piles = piles;
	}
	public boolean isTrade() {
		return Trade;
	}
	public void setTrade(boolean trade) {
		Trade = trade;
	}
	public boolean isDrop() {
		return Drop;
	}
	public void setDrop(boolean drop) {
		Drop = drop;
	}
	public boolean isWarehouse() {
		return Warehouse;
	}
	public void setWarehouse(boolean warehouse) {
		Warehouse = warehouse;
	}
	public boolean isEnchant() {
		return Enchant;
	}
	public void setEnchant(boolean enchant) {
		Enchant = enchant;
	}
	public int getSafeEnchant() {
		return SafeEnchant;
	}
	public void setSafeEnchant(int safeEnchant) {
		SafeEnchant = safeEnchant;
	}
	public int getRoyal() {
		return Royal;
	}
	public void setRoyal(int royal) {
		Royal = royal;
	}
	public int getKnight() {
		return Knight;
	}
	public void setKnight(int knight) {
		Knight = knight;
	}
	public int getElf() {
		return Elf;
	}
	public void setElf(int elf) {
		Elf = elf;
	}
	public int getWizard() {
		return Wizard;
	}
	public void setWizard(int wizard) {
		Wizard = wizard;
	}
	public int getDarkElf() {
		return DarkElf;
	}
	public void setDarkElf(int darkElf) {
		DarkElf = darkElf;
	}
	public int getDragonKnight() {
		return DragonKnight;
	}
	public void setDragonKnight(int dragonKnight) {
		DragonKnight = dragonKnight;
	}
	public int getBlackWizard() {
		return BlackWizard;
	}
	public void setBlackWizard(int blackWizard) {
		BlackWizard = blackWizard;
	}
	public int getAddHit() {
		return AddHit;
	}
	public void setAddHit(int addHit) {
		AddHit = addHit;
	}
	public int getAddDmg() {
		return AddDmg;
	}
	public void setAddDmg(int addDmg) {
		AddDmg = addDmg;
	}
	public int getAc() {
		return Ac;
	}
	public void setAc(int ac) {
		Ac = ac;
	}
	public int getAddStr() {
		return AddStr;
	}
	public void setAddStr(int addStr) {
		AddStr = addStr;
	}
	public int getAddDex() {
		return AddDex;
	}
	public void setAddDex(int addDex) {
		AddDex = addDex;
	}
	public int getAddCon() {
		return AddCon;
	}
	public void setAddCon(int addCon) {
		AddCon = addCon;
	}
	public int getAddInt() {
		return AddInt;
	}
	public void setAddInt(int addInt) {
		AddInt = addInt;
	}
	public int getAddWis() {
		return AddWis;
	}
	public void setAddWis(int addWis) {
		AddWis = addWis;
	}
	public int getAddCha() {
		return AddCha;
	}
	public void setAddCha(int addCha) {
		AddCha = addCha;
	}
	public int getAddHp() {
		return AddHp;
	}
	public void setAddHp(int addHp) {
		AddHp = addHp;
	}
	public int getAddMp() {
		return AddMp;
	}
	public void setAddMp(int addMp) {
		AddMp = addMp;
	}
	public int getAddSp() {
		return AddSp;
	}
	public void setAddSp(int addSp) {
		AddSp = addSp;
	}
	public int getAddMr() {
		return AddMr;
	}
	public void setAddMr(int addMr) {
		AddMr = addMr;
	}
	public boolean isCanbedmg() {
		return Canbedmg;
	}
	public void setCanbedmg(boolean canbedmg) {
		Canbedmg = canbedmg;
	}
	public int getLevelMin() {
		return LevelMin;
	}
	public void setLevelMin(int levelMin) {
		LevelMin = levelMin;
	}
	public int getLevelMax() {
		return LevelMax;
	}
	public void setLevelMax(int levelMax) {
		LevelMax = levelMax;
	}
	public int getEffect() {
		return Effect;
	}
	public void setEffect(int effect) {
		Effect = effect;
	}
	public boolean isTohand() {
		return Tohand;
	}
	public void setTohand(boolean tohand) {
		Tohand = tohand;
	}
	public int getSetId() {
		return SetId;
	}
	public void setSetId(int setId) {
		SetId = setId;
	}
	public int getContinuous() {
		return Continuous;
	}
	public void setContinuous(int continuous) {
		Continuous = continuous;
	}
	public int getWaterress() {
		return Waterress;
	}
	public void setWaterress(int waterress) {
		Waterress = waterress;
	}
	public int getWindress() {
		return Windress;
	}
	public void setWindress(int windress) {
		Windress = windress;
	}
	public int getEarthress() {
		return Earthress;
	}
	public void setEarthress(int earthress) {
		Earthress = earthress;
	}
	public int getFireress() {
		return Fireress;
	}
	public void setFireress(int fireress) {
		Fireress = fireress;
	}
	public double getAddWeight() {
		return AddWeight;
	}
	public void setAddWeight(double addWeight) {
		AddWeight = addWeight;
	}
	public int getTicHp() {
		return TicHp;
	}
	public void setTicHp(int ticHp) {
		TicHp = ticHp;
	}
	public int getTicMp() {
		return TicMp;
	}
	public void setTicMp(int ticMp) {
		TicMp = ticMp;
	}
	public int getShopPrice() {
		return ShopPrice;
	}
	public void setShopPrice(int shopPrice) {
		ShopPrice = shopPrice;
	}
	public int getDropChance() {
		return DropChance;
	}
	public void setDropChance(int dropChance) {
		DropChance = dropChance;
	}
	public int getSlot() {
		return Slot;
	}
	public void setSlot(int slot) {
		Slot = slot;
	}
	public int getEquippedSlot() {
		return equippedSlot;
	}
	public void setEquippedSlot(int equippedSlot) {
		this.equippedSlot = equippedSlot;
	}
	public Map<String, Integer> getListCraft(){
		return list_craft;
	}
	public int getSolvent() {
		return Solvent;
	}
	public void setSolvent(int solvent) {
		Solvent = solvent;
	}
	public int getAttributeCrystal() {
		return AttributeCrystal;
	}
	public void setAttributeCrystal(int attributeCrystal) {
		AttributeCrystal = attributeCrystal;
	}
	public boolean isBookChaoticZone() {
		return bookChaoticZone;
	}
	public void setBookChaoticZone(boolean bookChaoticZone) {
		this.bookChaoticZone = bookChaoticZone;
	}
	public boolean isBookLawfulZone() {
		return bookLawfulZone;
	}
	public void setBookLawfulZone(boolean bookLawfulZone) {
		this.bookLawfulZone = bookLawfulZone;
	}
	public boolean isBookMomtreeZone() {
		return bookMomtreeZone;
	}
	public void setBookMomtreeZone(boolean bookMomtreeZone) {
		this.bookMomtreeZone = bookMomtreeZone;
	}
	public boolean isBookNeutralZone() {
		return bookNeutralZone;
	}
	public void setBookNeutralZone(boolean bookNeutralZone) {
		this.bookNeutralZone = bookNeutralZone;
	}
	public boolean isBookTowerZone() {
		return bookTowerZone;
	}
	public void setBookTowerZone(boolean bookTowerZone) {
		this.bookTowerZone = bookTowerZone;
	}
	public String getPolyName() {
		return polyName;
	}
	public void setPolyName(String polyName) {
		this.polyName = polyName;
	}
	public boolean isInventorySave() {
		return isInventorySave;
	}
	public void setInventorySave(boolean isInventorySave) {
		this.isInventorySave = isInventorySave;
	}
	public boolean isAqua() {
		return isAqua;
	}
	public void setAqua(boolean isAqua) {
		this.isAqua = isAqua;
	}
	public int getStealHp() {
		return StealHp;
	}
	public void setStealHp(int stealHp) {
		StealHp = stealHp;
	}
	public int getStealMp() {
		return StealMp;
	}
	public void setStealMp(int stealMp) {
		StealMp = stealMp;
	}
}
