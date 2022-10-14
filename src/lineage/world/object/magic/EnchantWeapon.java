package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class EnchantWeapon extends Magic {
	
	public EnchantWeapon(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EnchantWeapon(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffEnchantWeapon(true);
//		if(o instanceof ItemInstance){
//			ItemInstance ii = (ItemInstance)o;
//			if(ii.getCharacter() != null)
//				ii.getCharacter().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 161, ii.toString(), "$245", "$247"));
//		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffEnchantWeapon(false);
		if(o instanceof ItemInstance){
			ItemInstance ii = (ItemInstance)o;
			if(ii.getCharacter() != null)
				// 무기가 보통으로 돌아왔습니다.
				ii.getCharacter().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 718));
		}
	}

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.getInventory().value(object_id);
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && o instanceof ItemWeaponInstance)
				onBuff(cha, o, skill, skill.getBuffDuration());
		}
	}

	/**
	 * 중복코드 방지용
	 * @param cha
	 * @param o
	 * @param skill
	 * @param time
	 */
	static public void onBuff(Character cha, object o, Skill skill, int time){
		if(cha!=null && o!=null && skill!=null){
			// 버프 등록
			BuffController.append(o, EnchantWeapon.clone(BuffController.getPool(EnchantWeapon.class), skill, time));
			
			// 패킷 처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 161, o.toString(), "$245", "$247"));
		}
	}
	
}
