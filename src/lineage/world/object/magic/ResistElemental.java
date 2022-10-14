package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class ResistElemental extends Magic {

	public ResistElemental(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ResistElemental(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicEarthress( cha.getDynamicEarthress() + skill.getMaxdmg() );
			cha.setDynamicWaterress( cha.getDynamicWaterress() + skill.getMaxdmg() );
			cha.setDynamicFireress( cha.getDynamicFireress() + skill.getMaxdmg() );
			cha.setDynamicWindress( cha.getDynamicWindress() + skill.getMaxdmg() );
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
			cha.setDynamicEarthress( cha.getDynamicEarthress() - skill.getMaxdmg() );
			cha.setDynamicWaterress( cha.getDynamicWaterress() - skill.getMaxdmg() );
			cha.setDynamicFireress( cha.getDynamicFireress() - skill.getMaxdmg() );
			cha.setDynamicWindress( cha.getDynamicWindress() - skill.getMaxdmg() );
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true)){
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			BuffController.append(cha, ResistElemental.clone(BuffController.getPool(ResistElemental.class), skill, skill.getBuffDuration()));
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, ResistElemental.clone(BuffController.getPool(ResistElemental.class), SkillDatabase.find(18, 1), time));
	}
	
}
