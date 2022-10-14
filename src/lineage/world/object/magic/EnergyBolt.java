package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class EnergyBolt {

	/**
	 * 사용자 용
	 * @param cha
	 * @param skill
	 * @param object_id
	 */
	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		if(o!=null && SkillController.isMagic(cha, skill, true))
			toBuff(cha, o, skill, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getCastGfx(), 0);
	}
	
	/**
	 * 몬스터용
	 *  : 장로가 사용중.
	 *  : 다크마르가 사용중.
	 * @param cha
	 * @param o
	 * @param skill
	 * @param action
	 */
	static public void init(Character cha, object o, MonsterSkill skill, int action, int effect){
		if(o!=null && SkillController.isMagic(cha, skill, true))
			toBuff(cha, o, skill.getSkill(), action, effect, Util.random(skill.getMindmg(), skill.getMaxdmg()));
	}
	
	/**
	 * 중복코드 방지용
	 * @param cha
	 * @param o
	 * @param skill
	 * @param action
	 * @param effect
	 */
	static public void toBuff(Character cha, object o, Skill skill, int action, int effect, int alpha_dmg){
		// 데미지 처리
		int dmg = SkillController.getDamage(cha, o, o, skill, alpha_dmg, skill.getElement());
		DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_MAGIC);
		
		// 패킷 처리
		cha.setHeading( Util.calcheading(cha, o.getX(), o.getY()) );
		cha.toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), cha, o, action, dmg, effect, false, false, 0, 0), cha instanceof PcInstance);	
	}
}
