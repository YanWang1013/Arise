package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffStr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class EnchantMighty extends Magic {

	public EnchantMighty(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EnchantMighty(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicStr( cha.getDynamicStr() + skill.getMaxdmg() );
		}
		toBuffUpdate(o);
	}

	@Override
	public void toBuffUpdate(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			// 강해짐을 느낍니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 292));
			if(Lineage.server_version>144)
				cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, getTime(), skill.getMaxdmg()));
			else
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
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicStr( cha.getDynamicStr() - skill.getMaxdmg() );
			if(Lineage.server_version>144)
				cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, 0, skill.getMaxdmg()));
			else
				cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
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
			if(SkillController.isMagic(cha, skill, true)){
				if(SkillController.isClan(cha, o) && SkillController.isFigure(cha, o, skill, false, false)){
					onBuff(o, skill);
				}else{
					// \f1마법이 실패했습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
				}
			}
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, EnchantMighty.clone(BuffController.getPool(EnchantMighty.class), SkillDatabase.find(6, 1), time));
	}

	static public void onBuff(object o, Skill skill){
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, EnchantMighty.clone(BuffController.getPool(EnchantMighty.class), skill, skill.getBuffDuration()));
	}
	
}
