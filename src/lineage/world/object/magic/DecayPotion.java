package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class DecayPotion extends Magic {

	public DecayPotion(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new DecayPotion(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffDecayPotion(true);
		// 공격당한거 알리기.
		o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffDecayPotion(false);
	}
	
	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				BuffController.append(o, DecayPotion.clone(BuffController.getPool(DecayPotion.class), skill, skill.getBuffDuration()));
			}
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, DecayPotion.clone(BuffController.getPool(DecayPotion.class), SkillDatabase.find(9, 6), time));
	}
	
}
