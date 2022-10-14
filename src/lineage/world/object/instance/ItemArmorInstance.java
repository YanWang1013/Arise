package lineage.world.object.instance;

import java.sql.Connection;

import lineage.bean.lineage.Inventory;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.object.Character;

public class ItemArmorInstance extends ItemIllusionInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ItemArmorInstance();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(isLvCheck(cha)) {
			if(isClassCheck(cha)){
				Inventory inv = cha.getInventory();
				if(inv!=null && isEquipped(cha, inv) && (PolyDatabase.toEquipped(cha, this) || equipped)){
					if(equipped){
						if(bress==2){
							// \f1그렇게 할 수 없습니다. 저주 받은 것 같습니다.
							cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 150));
							return;
						}
						setEquipped(false);
					}else{
						if(item.getType2().equalsIgnoreCase("shield")){
							ItemInstance weapon = inv.getSlot(Lineage.SLOT_WEAPON);
							if(weapon!=null && weapon.getItem().isTohand()){
								// \f1두손 무기를 무장하고 방패를 착용할 수 없습니다.
								cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 129));
								return;
							}
						}
						setEquipped(true);
					}

					toSetoption(cha, true);
					toEquipped(cha, inv);
					toOption(cha, true);
				}else{
					// \f1이미 뭔가를 착용하고 있습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 124));
				}
			}else{
				// \f1당신의 클래스는 이 아이템을 사용할 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 264));
			}
		}
	}

	/**
	 * 방어구 착용 및 해제 처리 메서드.
	 */
	@Override
	public void toEquipped(Character cha, Inventory inv){
		if(equipped){
			cha.setAc(cha.getAc() + getTotalAc());
		}else{
			cha.setAc(cha.getAc() - getTotalAc());
		}

		switch(item.getSlot()){
			case Lineage.SLOT_RING_LEFT:
			case Lineage.SLOT_RING_RIGHT:
				if(equipped){
					if(inv.getSlot(Lineage.SLOT_RING_RIGHT)==null){
						inv.setSlot(Lineage.SLOT_RING_RIGHT, this);
					}else{
						inv.setSlot(Lineage.SLOT_RING_LEFT, this);
					}
				}else{
					if(inv.getSlot(Lineage.SLOT_RING_RIGHT)!=null && inv.getSlot(Lineage.SLOT_RING_RIGHT).getObjectId()==getObjectId()){
						inv.setSlot(Lineage.SLOT_RING_RIGHT, null);
					}else if(inv.getSlot(Lineage.SLOT_RING_LEFT)!=null && inv.getSlot(Lineage.SLOT_RING_LEFT).getObjectId()==getObjectId()){
						inv.setSlot(Lineage.SLOT_RING_LEFT, null);
					}else{
						inv.setSlot(Lineage.SLOT_RING_RIGHT, null);
						inv.setSlot(Lineage.SLOT_RING_LEFT, null);
					}
				}
				break;
			default:
				inv.setSlot(item.getSlot(), equipped ? this : null);
				break;
		}

		if(getBress()==2 && equipped) {
			//\f1%0%s 손에 달라 붙었습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 149, getName()));
		}

		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}

	/**
	 * 방어구 착용순서 체크하기.
	 */
	private boolean isEquipped(Character cha, Inventory inv){
		// 착용해제 하려는가?
		if(equipped){
			// 갑옷해제시 망토 확인
			if(item.getSlot()==Lineage.SLOT_ARMOR && inv.getSlot(Lineage.SLOT_CLOAK)!=null){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 127));
				return false;
			}
			// 티셔츠해제시 망토 확인
			if(item.getSlot()==Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_CLOAK)!=null){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 127));
				return false;
			}
			// 티셔츠해제시 아머 확인
			if(item.getSlot()==Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_ARMOR)!=null){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 127));
				return false;
			}
		// 착용 하려는가?
		}else{
			// 갑옷 착용시 망토 확인
			if(item.getSlot()==Lineage.SLOT_ARMOR && inv.getSlot(Lineage.SLOT_CLOAK)!=null){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 126, "$226", "$225"));
				return false;
			}
			// 티셔츠 착용시 갑옷 확인
			if(item.getSlot()==Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_ARMOR)!=null){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 126, "$168", "$226"));
				return false;
			}
			// 티셔츠 착용시 망토 확인
			if(item.getSlot()==Lineage.SLOT_SHIRT && inv.getSlot(Lineage.SLOT_CLOAK)!=null){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 126, "$168", "$225"));
				return false;
			}
			// 방패 착용시 양손무기 확인
			if(item.getSlot()==Lineage.SLOT_SHIELD && inv.getSlot(Lineage.SLOT_WEAPON)!=null && inv.getSlot(Lineage.SLOT_WEAPON).getItem().isTohand()){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 129));
				return false;
			}
			switch(item.getSlot()){
				case Lineage.SLOT_RING_LEFT:
				case Lineage.SLOT_RING_RIGHT:
					if(inv.getSlot(Lineage.SLOT_RING_LEFT)!=null && inv.getSlot(Lineage.SLOT_RING_RIGHT)!=null){
						if(Lineage.item_equipped_type && inv.getSlot(item.getSlot()).getBress()!=2){
							do{
								inv.getSlot(item.getSlot()).toClick(cha, null);
							}while(inv.getSlot(item.getSlot()) != null);
						}else{
							return false;
						}
					}
					return true;
				default:
					if(inv.getSlot(item.getSlot()) != null){
						if(Lineage.item_equipped_type && inv.getSlot(item.getSlot()).getBress()!=2){
							do{
								inv.getSlot(item.getSlot()).toClick(cha, null);
							}while(inv.getSlot(item.getSlot()) != null);
						}else{
							return false;
						}
					}
					return true;
			}
		}
		return true;
	}

	/**
	 * 방어구 상태에따라 ac전체값 계산하여 리턴.
	 */
	private int getTotalAc(){
		int ac = getItem().getAc() + getEnLevel() + getDynamicAc();
		return ac<0 ? 0 : ac;
	}

	/**
	 * 리니지 월드에 접속했을때 착용중인 아이템 처리를 위해 사용되는 메서드.
	 */
	@Override
	public void toWorldJoin(Connection con, PcInstance pc){
		super.toWorldJoin(con, pc);
		if(equipped){
			toSetoption(pc, false);
			toEquipped(pc, pc.getInventory());
			toOption(pc, false);
		}
	}

	/**
	 * 인첸트 활성화 됫을때 아이템의 뒷처리를 처리하도록 요청하는 메서드.
	 */
	@Override
	public void toEnchant(PcInstance pc, int en){
		if(en!=0){
			if(equipped && (en<0 ? getTotalAc()>=0 : getTotalAc()>0)){
				pc.setAc(pc.getAc() + en);
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
			}
		}else{
			Inventory inv = pc.getInventory();
			if(equipped){
				setEquipped(false);
				toEquipped(pc, inv);
				toOption(pc, true);
				toSetoption(pc, true);
			}
			inv.count(this, 0, true);
		}
	}
	
}
