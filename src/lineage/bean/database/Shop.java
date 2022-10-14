package lineage.bean.database;

public class Shop {
	private int Uid;
	private String NpcName;
	private String ItemName;
	private int ItemCount;
	private int ItemBress;
	private int ItemEnLevel;
	private boolean ItemSell;
	private boolean ItemBuy;
	private boolean gamble;
	private int price;
	private int slimeraceUid;
	private String adenType;
	public Shop(){
		//
	}
	public Shop(String ItemName, int ItemCount, int ItemBress){
		this.ItemName = ItemName;
		this.ItemCount = ItemCount;
		this.ItemBress = ItemBress;
		ItemBuy = true;
		ItemSell = true;
	}
	public int getUid() {
		return Uid;
	}
	public void setUid(int uid) {
		Uid = uid;
	}
	public String getNpcName() {
		return NpcName;
	}
	public void setNpcName(String npcName) {
		NpcName = npcName;
	}
	public String getItemName() {
		return ItemName;
	}
	public void setItemName(String itemName) {
		ItemName = itemName;
	}
	public int getItemCount() {
		return ItemCount;
	}
	public void setItemCount(int itemCount) {
		ItemCount = itemCount;
	}
	public int getItemBress() {
		return ItemBress;
	}
	public void setItemBress(int itemBress) {
		ItemBress = itemBress;
	}
	public int getItemEnLevel() {
		return ItemEnLevel;
	}
	public void setItemEnLevel(int itemEnLevel) {
		ItemEnLevel = itemEnLevel;
	}
	public boolean isItemSell() {
		return ItemSell;
	}
	public void setItemSell(boolean itemSell) {
		ItemSell = itemSell;
	}
	public boolean isItemBuy() {
		return ItemBuy;
	}
	public void setItemBuy(boolean itemBuy) {
		ItemBuy = itemBuy;
	}
	public boolean isGamble() {
		return gamble;
	}
	public void setGamble(boolean gamble) {
		this.gamble = gamble;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getSlimeraceUid() {
		return slimeraceUid;
	}
	public void setSlimeraceUid(int slimeraceUid) {
		this.slimeraceUid = slimeraceUid;
	}
	public String getAdenType() {
		return adenType;
	}
	public void setAdenType(String adenType) {
		this.adenType = adenType;
	}
}
