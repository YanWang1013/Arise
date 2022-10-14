package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemWeaponInstance;

public class EnchantVenom extends Magic {
	
	public EnchantVenom(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EnchantVenom(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffEnchantVenom(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffEnchantVenom(false);
	}

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.getInventory().value(object_id);
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && o instanceof ItemWeaponInstance)
				onBuff(cha, skill, skill.getBuffDuration());
		}
	}

	/**
	 * 중복코드 방지용
	 * @param cha
	 * @param o
	 * @param skill
	 * @param time
	 */
	static private void onBuff(Character cha, Skill skill, int time){
		if(cha!=null && skill!=null){
			// 버프 등록
			BuffController.append(cha, EnchantVenom.clone(BuffController.getPool(EnchantVenom.class), skill, time));
			
			// 패킷 처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
		}
	}

}
