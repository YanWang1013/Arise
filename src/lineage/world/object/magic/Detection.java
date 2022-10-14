package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Detection {

	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}
	
	/**
	 * 중복코드 방지용.
	 *  : 마법주문서 (디텍션) 에서도 사용중.
	 * @param cha
	 * @param skill
	 */
	static public void onBuff(Character cha, Skill skill){
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
		
		onBuff(cha);
		for(object o : cha.getInsideList()) {
			o.toMagicalAttackEncounters(cha);
			onBuff(o);
		}
	}
	
	static public void onBuff(object o){
		// 운영자는 무시.
		if(o.getGm()>=Lineage.GMCODE)
			return;
		if( o.isInvis() ){
			o.setInvis(false);
			// 인비지마법 제거.
			BuffController.remove(o, InvisiBility.class);
		}
	}
}
