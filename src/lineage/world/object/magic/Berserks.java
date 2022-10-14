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

public class Berserks extends Magic {

	public Berserks(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Berserks(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		// 대상은 근거리 공격력이 +5 상승하는 대신 AC가 10 하락하고, HP가 자연 회복되지 않는다.
		o.setBuffBerserks(true);
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAc( cha.getDynamicAc()-skill.getMaxdmg() );
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		
		o.setBuffBerserks(false);
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAc( cha.getDynamicAc()+skill.getMaxdmg() );
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}
	
	static public void init(Character cha, Skill skill, long object_id){
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
			// 확인.
			if(SkillController.isMagic(cha, skill, true) && SkillController.isParty(cha, o)){
				// 이팩트 표현.
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				// 적용.
				BuffController.append(o, Berserks.clone(BuffController.getPool(Berserks.class), skill, skill.getBuffDuration()));
			}
		}
	}

}
