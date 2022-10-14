package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Party;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.PartyController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class EyeOfStorm extends Magic {
	
	public EyeOfStorm(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EyeOfStorm(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffEyeOfStorm(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffEyeOfStorm(false);
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true) && cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			Party p = PartyController.find(pc);
			if(p == null){
				onBuff(pc, skill, skill.getBuffDuration());
			}else{
				for(PcInstance use : p.getList()){
					if(Util.isDistance(cha, use, Lineage.SEARCH_LOCATIONRANGE))
						onBuff(use, skill, skill.getBuffDuration());
				}
			}
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, EyeOfStorm.clone(BuffController.getPool(EyeOfStorm.class), SkillDatabase.find(20, 3), time));
	}
	
	static public void onBuff(PcInstance pc, Skill skill, int time){
		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, skill.getCastGfx()), true);
		BuffController.append(pc, EyeOfStorm.clone(BuffController.getPool(EyeOfStorm.class), skill, time));
	}

}
