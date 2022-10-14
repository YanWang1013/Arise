package lineage.world.object.instance;

import java.sql.Connection;

import lineage.bean.database.Item;
import lineage.bean.database.ItemSetoption;
import lineage.bean.database.Poly;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Inventory;
import lineage.database.ItemSetoptionDatabase;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.item.MagicFlute;
import lineage.world.object.magic.ShapeChange;

public class ItemInstance extends object implements BuffInterface {

	protected Character cha;		// 아이템을 소지하고있는 객체
	protected Item item;
	protected int bress;			// 축저주 여부
	protected int quantity;			// 소막같은 수량
	protected int enLevel;			// en
	protected int durability;		// 손상도
	protected int dynamicMr;		// mr
	protected boolean definite;		// 확인 여부
	protected boolean equipped;		// 착용 여부
	protected int nowTime;			// 아이템 사용 남은 시간.
	public int TempPrice;			// 상점 처리시 임시 사용되는 변수.
	protected long click_delay;		// 아이템 클릭 딜레이를 주기위한 변수.
	protected int dynamicLight;		// 동적 라이트값. 현재는 양초쪽에서 사용중. 해당 아이템에 밝기값을 저장하기 위해.
	protected long time_drop;		// 드랍된 시간값.
	protected int dynamicAc;		// 동적 ac 값.
	
	// 개인상점에 사용되는 변수
	private int usershopIdx;		// sell 처리시 위치값 지정용.
	private int usershopBuyPrice;	// 판매 가격
	private int usershopSellPrice;	// 구입 가격
	private int usershopBuyCount;	// 판매 갯수
	private int usershopSellCount;	// 구입 갯수

	public ItemInstance(){
		//
	}

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ItemInstance();
		return item;
	}

	public ItemInstance clone(Item item){
		this.item = item;
		name = item.getNameId();
		gfx = item.getGroundGfx();
		
		return this;
	}

	@Override
	public void close(){
		super.close();
		// 메모리 초기화 함수.
		item = null;
		cha = null;
		time_drop = click_delay = quantity = enLevel = durability = dynamicMr = nowTime = usershopBuyPrice = 
		usershopSellPrice = usershopBuyCount = usershopSellCount = usershopIdx = dynamicLight = dynamicAc = 
		TempPrice = 0;
		bress = 1;
		definite = equipped = false;
	}
	
	/**
	 * 아이템을 사용해도 되는지 확인해주는 함수.<br/>
	 *  : 아이템 더블클릭하면 젤 우선적으로 호출됨.<br/>
	 *  : C_ItemClick 에서 사용중.<br/>
	 * @return
	 */
	public boolean isClick(PcInstance pc){
		if(pc != null){
			// 맵에따른 아이템 제한 확인.
			switch(pc.getMap()){
				case 22:
					// 게라드 시험 퀘 맵일경우 비취물약만 사용가능하도록 하기.
					if(item.getNameIdNumber() != 233){
						// 귀환이나 순간이동 주문서 사용시 케릭동작에 락이 걸리기때문에 그것을 풀기위한것.
						pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
						return false;
					}
					break;
				case 201:
					// 마법사 30 퀘 일때. 귀환 빼고 다 불가능.
					if(item.getNameIdNumber() != 505){
						pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
						return false;
					}
					break;
			}
			// 마법의 플룻에 따른 제한 확인.
			if(BuffController.find(pc).find(MagicFlute.class) != null){
				pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 343));
				return false;
			}
		}
		// 딜레이 확인.
		long time = System.currentTimeMillis();
		if(time-click_delay>=item.getContinuous()){
			click_delay = time;
			return true;
		}
		return false;
	}

	public long getTimeDrop() {
		return time_drop;
	}

	public void setTimeDrop(long time_drop) {
		this.time_drop = time_drop;
	}

	public int getDynamicLight() {
		return dynamicLight;
	}

	public void setDynamicLight(int dynamicLight) {
		this.dynamicLight = dynamicLight;
	}

	public int getDynamicAc() {
		return dynamicAc;
	}

	public void setDynamicAc(int dynamicAc) {
		this.dynamicAc = dynamicAc;
	}

	@Override
	public Character getCharacter() {
		return cha;
	}

	public Item getItem() {
		return item;
	}

	public int getBress() {
		return bress;
	}

	public void setBress(int bress) {
		this.bress = bress;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getEnLevel() {
		return enLevel;
	}

	public void setEnLevel(int enLevel) {
		this.enLevel = enLevel;
	}

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		if(durability > Lineage.item_durability_max)
			durability = Lineage.item_durability_max;
		else if(durability < 0)
			durability = 0;
		this.durability = durability;
			
	}

	public int getDynamicMr() {
		return dynamicMr;
	}

	public void setDynamicMr(int dynamicMr) {
		this.dynamicMr = dynamicMr;
	}

	public boolean isDefinite() {
		return definite;
	}

	public void setDefinite(boolean definite) {
		this.definite = definite;
	}

	public boolean isEquipped() {
		return equipped;
	}

	public void setEquipped(boolean equipped) {
		this.equipped = equipped;
	}

	public int getNowTime() {
		return nowTime<0 ? 0 : nowTime;
	}

	public void setNowTime(int nowTime) {
		if(nowTime >= 0)
			this.nowTime = nowTime;
	}
	
	public int getWeight(){
		return (int)Math.round(item.getWeight()*count);
	}

	public int getUsershopBuyPrice() {
		return usershopBuyPrice;
	}

	public void setUsershopBuyPrice(int usershopBuyPrice) {
		this.usershopBuyPrice = usershopBuyPrice;
	}

	public int getUsershopSellPrice() {
		return usershopSellPrice;
	}

	public void setUsershopSellPrice(int usershopSellPrice) {
		this.usershopSellPrice = usershopSellPrice;
	}

	public int getUsershopBuyCount() {
		return usershopBuyCount;
	}

	public void setUsershopBuyCount(int usershopBuyCount) {
		this.usershopBuyCount = usershopBuyCount;
	}

	public int getUsershopSellCount() {
		return usershopSellCount;
	}

	public void setUsershopSellCount(int usershopSellCount) {
		this.usershopSellCount = usershopSellCount;
	}

	public int getUsershopIdx() {
		return usershopIdx;
	}

	public void setUsershopIdx(int usershopIdx) {
		this.usershopIdx = usershopIdx;
	}

	/**
	 * 리니지 월드에 접속했을때 착용중인 아이템 처리를 위해 사용되는 메서드.
	 * @param pc
	 */
	public void toWorldJoin(Connection con, PcInstance pc){
		cha = pc;
	}

	@Override
	public void toPickup(Character cha){
		this.cha = cha;
	}

	/**
	 * 해당 아이템이 드랍됫을때 호출되는 메서드.
	 * @param cha
	 */
	public void toDrop(Character cha){
		this.cha = null;
		time_drop = System.currentTimeMillis();
	}

	/**
	 * 아이템 착용 및 해제시 호출되는 메서드.
	 * @param cha
	 * @param inv
	 */
	public void toEquipped(Character cha, Inventory inv){}

	/**
	 * 인첸트 활성화 됫을때 아이템의 뒷처리를 처리하도록 요청하는 메서드.
	 * @param pc
	 * @param en
	 */
	public void toEnchant(PcInstance pc, int en){}
	
	/**
	 * 마법책 및 수정에 스킬값 지정하는 함수.
	 * @param skill_level
	 */
	public void setSkill(Skill skill){ }
	
	/**
	 * 아이템을 이용해 cha 가 o 에게 피해를 입히면 호출되는 함수.
	 * @param cha
	 * @param o
	 * @return
	 */
	public boolean toDamage(Character cha, object o){ return false; }
	
	/**
	 * toDamage(Character cha, object o) 거친후 값이 true가 될경우
	 * 아래 함수를 호출해 추가적으로 데미지를 더하도록 함.
	 * @return
	 */
	public int toDamage(int dmg){ return 0; }
	
	/**
	 * toDamage(Character cha, object o) 거친후 값이 true가 될경우
	 * 이팩트를 표현할 값을 턴.
	 * @return
	 */
	public int toDamageEffect(){ return 0; }
	
	/**
	 * 펫의 오프젝트값 리턴.
	 * @return
	 */
	public long getPetObjectId(){ return 0; }
	public void setPetObjectId(final long id){}

	/**
	 * 여관방 열쇠 키값
	 * @return
	 */
	public long getInnRoomKey(){ return 0; }
	public void setInnRoomKey(final long key){}

	/**
	 * 편지지 디비 연결 고리인 uid
	 * @return
	 */
	public int getLetterUid(){ return 0; }
	public void setLetterUid(final int uid){}
	
	/**
	 * 슬라임 레이스 관련 함수
	 * @return
	 */
	public String getSlimeRaceTicket(){ return ""; }
	public void setSlimeRaceTicket(String ticket){}
	
	public int getBressPacket(){
		if(Lineage.server_version>144)
			return definite ? bress : 3;
		else
			return bress;
	}
	
	/**
	 * 레벨 제한 체크
	 */
	protected boolean isLvCheck(Character cha){
		// 착용하지 않은 상태에서만 체크
		if(!isEquipped()){
			if(item.getLevelMin()>0 && item.getLevelMin()>cha.getLevel()){
	//			cha.toSender(new SItemLevelFails(item.Level));
				// 672 : 이 아이템은 %d레벨 이상이 되어야 사용할 수 있습니다.
				ChattingController.toChatting(cha, String.format("이 아이템은 %d레벨 이상이 되어야 사용할 수 있습니다.", item.getLevelMin()), Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
			if(item.getLevelMax()>0 && item.getLevelMax()<cha.getLevel()){
				// 673 : 이 아이템은 %d레벨 이하일때만 사용할 수 있습니다.
				ChattingController.toChatting(cha, String.format("이 아이템은 %d레벨 이하일때만 사용할 수 있습니다.", item.getLevelMin()), Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 클레스 착용가능 여부 체크 부분
	 */
	protected boolean isClassCheck(Character cha){
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:	// 군주
				return item.getRoyal()>0;
			case Lineage.LINEAGE_CLASS_KNIGHT:	// 기사
				return item.getKnight()>0;
			case Lineage.LINEAGE_CLASS_ELF:	// 요정
				return item.getElf()>0;
			case Lineage.LINEAGE_CLASS_WIZARD: // 법사
				return item.getWizard()>0;
			case Lineage.LINEAGE_CLASS_DARKELF:	// 다크엘프
				return item.getDarkElf()>0;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:	// 용기사
				return item.getDragonKnight()>0;
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:		// 환술사
				return item.getBlackWizard()>0;
		}
		return true;
	}

	/**
	 * 아이템 부가옵션 적용및 해제 부분
	 */
	public void toOption(Character cha, boolean sendPacket){
		if(getItem().getAddStr() != 0){
			if(equipped){
				cha.setDynamicStr(cha.getDynamicStr() + getItem().getAddStr());
			}else{
				cha.setDynamicStr(cha.getDynamicStr() - getItem().getAddStr());
			}
		}
		if(getItem().getAddDex() != 0){
			if(equipped){
				cha.setDynamicDex(cha.getDynamicDex() + getItem().getAddDex());
			}else{
				cha.setDynamicDex(cha.getDynamicDex() - getItem().getAddDex());
			}

		}
		if(getItem().getAddCon() != 0){
			if(equipped){
				cha.setDynamicCon(cha.getDynamicCon() + getItem().getAddCon());
			}else{
				cha.setDynamicCon(cha.getDynamicCon() - getItem().getAddCon());
			}
		}
		if(getItem().getAddInt() != 0){
			if(equipped){
				cha.setDynamicInt(cha.getDynamicInt() + getItem().getAddInt());
			}else{
				cha.setDynamicInt(cha.getDynamicInt() - getItem().getAddInt());
			}
		}
		if(getItem().getAddCha() != 0){
			if(equipped){
				cha.setDynamicCha(cha.getDynamicCha() + getItem().getAddCha());
			}else{
				cha.setDynamicCha(cha.getDynamicCha() - getItem().getAddCha());
			}
		}
		if(getItem().getAddWis() != 0){
			if(equipped){
				cha.setDynamicWis(cha.getDynamicWis() + getItem().getAddWis());
			}else{
				cha.setDynamicWis(cha.getDynamicWis() - getItem().getAddWis());
			}
		}
		if(getItem().getAddHp() > 0){
			if(equipped){
				cha.setDynamicHp(cha.getDynamicHp() + getItem().getAddHp());
			}else{
				cha.setDynamicHp(cha.getDynamicHp() - getItem().getAddHp());
			}
		}
		if(getItem().getAddMp() > 0){
			if(equipped){
				cha.setDynamicMp(cha.getDynamicMp() + getItem().getAddMp());
			}else{
				cha.setDynamicMp(cha.getDynamicMp() - getItem().getAddMp());
			}
		}
		if(getItem().getAddMr()>0 || getDynamicMr()>0){
			if(equipped){
				cha.setDynamicMr(cha.getDynamicMr() + getItem().getAddMr() + getDynamicMr());
			}else{
				cha.setDynamicMr(cha.getDynamicMr() - getItem().getAddMr() - getDynamicMr());
			}
		}
		if(getItem().getAddSp() > 0){
			if(equipped){
				cha.setDynamicSp(cha.getDynamicSp() + getItem().getAddSp());
			}else{
				cha.setDynamicSp(cha.getDynamicSp() - getItem().getAddSp());
			}
		}
		if(getItem().getAddWeight() > 0){
			if(equipped){
				cha.setItemWeight(cha.getItemWeight() + getItem().getAddWeight());
			}else{
				cha.setItemWeight(cha.getItemWeight() - getItem().getAddWeight());
			}
		}
		if(getItem().getTicHp() > 0){
			if(equipped){
				cha.setDynamicTicHp(cha.getDynamicTicHp() + getItem().getTicHp());
			}else{
				cha.setDynamicTicHp(cha.getDynamicTicHp() - getItem().getTicHp());
			}
		}
		if(getItem().getTicMp() > 0){
			if(equipped){
				cha.setDynamicTicMp(cha.getDynamicTicMp() + getItem().getTicMp());
			}else{
				cha.setDynamicTicMp(cha.getDynamicTicMp() - getItem().getTicMp());
			}
		}
		if(getItem().getEarthress() > 0){
			if(equipped){
				cha.setDynamicEarthress(cha.getDynamicEarthress() + getItem().getEarthress());
			}else{
				cha.setDynamicEarthress(cha.getDynamicEarthress() - getItem().getEarthress());
			}
		}
		if(getItem().getFireress() > 0){
			if(equipped){
				cha.setDynamicFireress(cha.getDynamicFireress() + getItem().getFireress());
			}else{
				cha.setDynamicFireress(cha.getDynamicFireress() - getItem().getFireress());
			}
		}
		if(getItem().getWindress() > 0){
			if(equipped){
				cha.setDynamicWindress(cha.getDynamicWindress() + getItem().getWindress());
			}else{
				cha.setDynamicWindress(cha.getDynamicWindress() - getItem().getWindress());
			}
		}
		if(getItem().getWaterress() > 0){
			if(equipped){
				cha.setDynamicWaterress(cha.getDynamicWaterress() + getItem().getWaterress());
			}else{
				cha.setDynamicWaterress(cha.getDynamicWaterress() - getItem().getWaterress());
			}
		}
		if(getItem().getPolyName()!=null && getItem().getPolyName().length()>0){
			Poly p = PolyDatabase.getPolyName(getItem().getPolyName());
			// 변신 상태가 아니거나 변신하려는 gfx 와 같을때만 처리.
			if(cha.getGfx()==cha.getClassGfx() || cha.getGfx()==p.getGfxId()){
				if(equipped){
					ShapeChange.onBuff(cha, cha, p, -1, false, sendPacket);
				}else{
					BuffController.remove(cha, ShapeChange.class);
				}
			}
		}

		if(sendPacket && cha instanceof PcInstance){
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
		}
	}

	/**
	 * 셋트아이템 착용여부 확인하여 옵션 적용및 해제처리하는 함수.
	 *  : 인벤토리와 연동하여 적용된 세트가 있는지 확인.
	 *    있는데 현재 착용된 아이템에서 없을경우 옵션 해제.
	 *    없는데 전체 셋트착용중일경우 옵션 적용.
	 */
	public void toSetoption(Character cha, boolean sendPacket){
		Inventory inv = cha.getInventory();
		if(inv!=null && item.getSetId()>0){
			ItemSetoption is = ItemSetoptionDatabase.find(item.getSetId());
			if(is!=null){
				if(equipped){
					// 적용된 셋트가 없다면.
					if(!inv.isSetoption(is)){
						// 셋트아이템 갯수 이상일 경우에만 적용.
						int cnt = 1;	// 해당 아이템이 착용될 것이기때문에 초기값을 1
						for(int i=0 ; i<=Lineage.SLOT_NONE ; ++i){
							ItemInstance slot = inv.getSlot(i);
							if(slot!=null && slot.getItem().getSetId()==is.getUid())
								cnt += 1;
						}
						if(is.getCount()<=cnt){
							inv.appendSetoption(is);
							ItemSetoptionDatabase.setting(cha, is, equipped, sendPacket);
						}
					}
				}else{
					// 적용된 셋트가 있다면
					if(inv.isSetoption(is)){
						// 셋트아이템 갯수 미만일경우에만 해제.
						int cnt = 0;
						for(int i=0 ; i<=Lineage.SLOT_NONE ; ++i){
							ItemInstance slot = inv.getSlot(i);
							if(slot!=null && slot.getItem().getSetId()==is.getUid())
								cnt += 1;
						}
						if(is.getCount()<=cnt){
							inv.removeSetoption(is);
							ItemSetoptionDatabase.setting(cha, is, equipped, sendPacket);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 아이템을 사용해도 되는 상태인지 확인해주는 함수.
	 * @param cha
	 * @return
	 */
	protected boolean isClick(Character cha){
		if(cha.isBuffDecayPotion())
			return false;
		
		return true;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha instanceof PcInstance)
			cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 330, toString()) );
	}

	@Override
	public String toString(){
		StringBuffer msg = new StringBuffer();
		if(definite && (this instanceof ItemWeaponInstance || this instanceof ItemArmorInstance)){
			if(enLevel>=0)
				msg.append("+");
			msg.append(enLevel);
			msg.append(" ");
		}
		msg.append(name);
		if(count > 1){
			msg.append(" (");
			msg.append(count);
			msg.append(")");
		}
		
		return msg.toString();
	}

	// .인벤 명령어 시작
	public String toStringDB(){
		StringBuffer msg = new StringBuffer();
		if(definite && (this instanceof ItemWeaponInstance || this instanceof ItemArmorInstance)){
			if(enLevel>=0)
				msg.append("+");
			msg.append(enLevel);
			msg.append(" ");
		}
		msg.append( item.getName() );
		if(getCount() > 1){
			msg.append(" (");
			msg.append(getCount());
			msg.append(")");
		}
		return msg.toString();
	}
	// .인벤 명령어 끝
	
	@Override
	public Skill getSkill() { return null; }

	@Override
	public void setTime(int time) { }

	@Override
	public int getTime() { return 0; }

	@Override
	public void setCharacter(Character cha) { }

	@Override
	public boolean isBuff(long time) { return --nowTime > 0; }

	@Override
	public void toBuffStart(object o) { }

	@Override
	public void toBuffUpdate(object o) { }

	@Override
	public void toBuff(object o) { }

	@Override
	public void toBuffStop(object o) { }

	@Override
	public void toBuffEnd(object o) { }
}
