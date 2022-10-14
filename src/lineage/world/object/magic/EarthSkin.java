package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffShield;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class EarthSkin extends Magic {
	
	public EarthSkin(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EarthSkin(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAc( cha.getDynamicAc() + skill.getMaxdmg() );
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			cha.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), getTime(), skill.getMaxdmg()));
		}
	}

	@Override
	public void toBuffUpdate(object o){
		o.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), getTime(), skill.getMaxdmg()));
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAc( cha.getDynamicAc() - skill.getMaxdmg() );
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			cha.toSender(S_BuffShield.clone(BasePacketPooling.getPool(S_BuffShield.class), 0, skill.getMaxdmg()));
		}
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
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false))
				onBuff(o, skill);
		}
	}
	
	static public void onBuff(object o, Skill skill){
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);

		// 쉴드 제거
		BuffController.remove(o, Shield.class);
		// 브레스오브어스 제거
		BuffController.remove(o, BlessOfEarth.class);
		// 아이언스킨 제거
		BuffController.remove(o, IronSkin.class);
		// 어스스킨 적용
		BuffController.append(o, EarthSkin.clone(BuffController.getPool(EarthSkin.class), skill, skill.getBuffDuration()));
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, EarthSkin.clone(BuffController.getPool(EarthSkin.class), SkillDatabase.find(19, 6), time));
	}
	
}
