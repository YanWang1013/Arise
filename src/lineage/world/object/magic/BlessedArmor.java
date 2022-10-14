package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class BlessedArmor extends Magic {
	
	public BlessedArmor(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new BlessedArmor(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if(o instanceof ItemInstance){
			ItemInstance item = (ItemInstance)o;
			item.setDynamicAc( item.getDynamicAc() + skill.getMaxdmg() );
			if(item.isEquipped() && item.getCharacter()!=null){
				item.getCharacter().setAc(item.getCharacter().getAc() + skill.getMaxdmg());
				item.getCharacter().toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), item.getCharacter()));
			}
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o instanceof ItemInstance){
			ItemInstance item = (ItemInstance)o;
			item.setDynamicAc( item.getDynamicAc() - skill.getMaxdmg() );
			if(item.isEquipped() && item.getCharacter()!=null && !item.getCharacter().isWorldDelete()){
				item.getCharacter().setAc(item.getCharacter().getAc() - skill.getMaxdmg());
				item.getCharacter().toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), item.getCharacter()));
			}
		}
	}

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.getInventory().value(object_id);
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && o instanceof ItemInstance)
				onBuff(cha, (ItemInstance)o, skill, skill.getBuffDuration());
		}
	}
	
	/**
	 * 중복코드 방지용.
	 * @param cha
	 * @param item
	 * @param skill
	 * @param time
	 */
	static public void onBuff(Character cha, ItemInstance item, Skill skill, int time){
		if(item!=null && skill!=null && item.getItem().getType2().equalsIgnoreCase("armor")){
			// 버프 등록
			BuffController.append(item, BlessedArmor.clone(BuffController.getPool(BlessedArmor.class), skill, time));
			
			// 패킷 처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 161, item.toString(), "$245", "$247"));
		}
	}
	
}
