package lineage.world.object.instance;

import java.sql.Connection;

import lineage.bean.database.ItemSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Inventory;
import lineage.database.ItemSkillDatabase;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class ItemWeaponInstance extends ItemIllusionInstance {

	private int skill_dmg;
	private int skill_effect;
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ItemWeaponInstance();
		return item;
	}

	@Override
	public void close(){
		super.close();
		
		skill_dmg = skill_effect = 0;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(isLvCheck(cha)) {
			if(isClassCheck(cha)){
				if(PolyDatabase.toEquipped(cha, this) || equipped){
					Inventory inv = cha.getInventory();
					if(inv != null){
						if(equipped){
							if(bress==2){
								// \f1그렇게 할 수 없습니다. 저주 받은 것 같습니다.
								cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 150));
								return;
							}
							setEquipped(false);
						}else{
							if(getItem().isTohand()){
								if(inv.getSlot(Lineage.SLOT_SHIELD) != null){
									// \f1방패를 착용하고서는 두손 무기를 쓸수 없습니다.
									cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 128));
									return;
								}
							}
							if(inv.getSlot(Lineage.SLOT_WEAPON) != null){
								if(Lineage.item_equipped_type && inv.getSlot(Lineage.SLOT_WEAPON).getBress()!=2){
									inv.getSlot(Lineage.SLOT_WEAPON).toClick(cha, null);
								}else{
									// \f1이미 뭔가를 착용하고 있습니다.
									cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 124));
									return;
								}
							}
							setEquipped(true);
						}

						toSetoption(cha, true);
						toEquipped(cha, inv);
						toOption(cha, true);
					}
				}
			}else{
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 264));
			}
		}
	}

	/**
	 * 무기 착용 및 해제 처리 메서드.
	 */
	@Override
	public void toEquipped(Character cha, Inventory inv){
		if(inv == null)
			return;
		
		if(equipped){
			inv.setSlot(item.getSlot(), this);
			if(cha.getGfx()==cha.getClassGfx()){
				// 변신상태가 아닐때만 변경하도록 함.
				cha.setGfxMode(cha.getGfxMode()+item.getGfxMode());
				cha.toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), cha), true);
			}
			if(getBress()==2) {
				//\f1%0%s 손에 달라 붙었습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 149, getName()));
			}
		}else{
			inv.setSlot(item.getSlot(), null);
			if(cha.getGfx()==cha.getClassGfx()){
				// 변신상태가 아닐때만 변경하도록 함.
				cha.setGfxMode(cha.getGfxMode()-item.getGfxMode());
				cha.toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), cha), true);
			}
		}
		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}

	/**
	 * 리니지 월드에 접속했을때 착용중인 아이템 처리를 위해 사용되는 메서드.
	 */
	@Override
	public void toWorldJoin(Connection con, PcInstance pc){
		super.toWorldJoin(con, pc);
		if(equipped){
			toSetoption(pc, false);
			pc.getInventory().setSlot(item.getSlot(), this);
			toOption(pc, false);
		}
	}

	/**
	 * 인첸트 활성화 됫을때 아이템의 뒷처리를 처리하도록 요청하는 메서드.
	 */
	@Override
	public void toEnchant(PcInstance pc, int en){
		if(en!=0){
			if(isEquipped()){
				// 성공햇으면서 착용중이면 뭔갈 해줘야할까?
			}
		}else{
			Inventory inv = pc.getInventory();
			if(equipped){
				setEquipped(false);
				toSetoption(pc, true);
				toEquipped(pc, inv);
				toOption(pc, true);
			}
			// 인벤에서 제거하면서 메모리도 함께 제거함.
			inv.count(this, 0, true);
		}
	}

	@Override
	public boolean toDamage(Character cha, object o){
		if(o==null || cha==null || o.isDead() || cha.isDead() || item==null)
			return false;
		
		boolean r_bool = false;
		
		// 마나 스틸하기
		if(o.getNowMp()>0 && item.getStealMp()>0 && (o instanceof MonsterInstance || o instanceof PcInstance)){
			// 1~3랜덤 추출
			int steal_mp = Util.random(1, item.getStealMp());
			// 인첸트 수치만큼 +@
			if(getEnLevel()>0){
				steal_mp += getEnLevel();
				// 만약 고인첸 일경우 7부터 +1 되도록 하기
				if(getEnLevel()>6)
					steal_mp += getEnLevel()-6;
			}
			// 타켓에 mp가 스틸할 값보다 작을경우 현재 가지고있는 mp값으로 변경
			if(o.getNowMp()<steal_mp)
				steal_mp = o.getNowMp();
			// mp제거하기.
			o.setNowMp(o.getNowMp()-steal_mp);
			// mp추가하기.
			cha.setNowMp(cha.getNowMp()+steal_mp);
			r_bool = true;
		}
		
		// 체력 스틸하기
		if(o.getNowHp()>0 && item.getStealHp()>0 && (o instanceof MonsterInstance || o instanceof PcInstance)){
			// 1~3랜덤 추출
			int steal_hp = Util.random(1, item.getStealHp());
			// 인첸트 수치만큼 +@
			if(getEnLevel()>0){
				steal_hp += getEnLevel();
				// 만약 고인첸 일경우 7부터 +1 되도록 하기
				if(getEnLevel()>6)
					steal_hp += getEnLevel()-6;
			}
			// 타켓에 mp가 스틸할 값보다 작을경우 현재 가지고있는 mp값으로 변경
			if(o.getNowHp()<steal_hp)
				steal_hp = o.getNowHp();
			// hp제거하기.
			o.setNowHp(o.getNowHp()-steal_hp);
			// hp추가하기.
			cha.setNowHp(cha.getNowHp()+steal_hp);
			r_bool = true;
		}
		
		// 마법 발동.
		skill_dmg = skill_effect = 0;
		ItemSkill is = ItemSkillDatabase.find(item.getName());
		if(is!=null && Util.random(0, 100)<=is.getChance()){
			r_bool = true;
			// 스킬을 통한 데미지와 이팩트 추출.
			Skill skill = SkillDatabase.find(is.getSkill());
			if(!SkillController.toSkill(cha, o, skill, is.isEffectTarget(), is.getDuration(), is.getOption())){
				skill_dmg = SkillController.getDamage(cha, o, o, skill, 0, is.getElement());
				skill_effect = skill.getCastGfx();
			}
			// 스킬이팩트에 의존하지않고 있을경우 
			if(is.getEffect() > 0)
				skill_effect = is.getEffect();
			// 데미지 +@ 처리
			if(is.getMinDmg()>0 && is.getMaxDmg()>=is.getMinDmg())
				skill_dmg += Util.random(is.getMinDmg(), is.getMaxDmg());
			// 이팩트가 자기자신에게 표현해야할경우 처리.
			if(is.isEffectTarget() && skill_effect>0){
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill_effect), true);
				skill_effect = 0;
			}
		}
		
		return r_bool;
	}
	
	@Override
	public int toDamage(int dmg){
		return skill_dmg;
	}
	
	@Override
	public int toDamageEffect(){
		return skill_effect;
	}
}
