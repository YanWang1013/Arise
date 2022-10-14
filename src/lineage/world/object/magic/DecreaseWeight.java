package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class DecreaseWeight extends Magic {
	
	public DecreaseWeight(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new DecreaseWeight(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	@Override
	public void toBuffStart(object o){
		o.setBuffDecreaseWeight(true);
		if(o instanceof Character)
			o.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), (Character)o));
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBuffDecreaseWeight(false);
		if(o instanceof Character)
			o.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), (Character)o));
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}
	
	/**
	 * 중복코드 방지용
	 *  : 마법주문서 (디크리즈 웨이트) 에서도 사용중.
	 * @param cha
	 * @param skill
	 */
	static public void onBuff(Character cha, Skill skill){
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
		BuffController.append(cha, DecreaseWeight.clone(BuffController.getPool(DecreaseWeight.class), skill, skill.getBuffDuration()));
	}
	
}
