package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Party;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffShield;
import lineage.network.packet.server.S_CharacterStat;
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

public class BlessOfEarth extends Magic {
	
	public BlessOfEarth(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new BlessOfEarth(skill);
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
		BuffController.append(cha, BlessOfEarth.clone(BuffController.getPool(BlessOfEarth.class), SkillDatabase.find(20, 2), time));
	}
	
	static public void onBuff(PcInstance pc, Skill skill, int time){
		pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, skill.getCastGfx()), true);
		
		// 쉴드 제거
		BuffController.remove(pc, Shield.class);
		// 어스스킨 제거
		BuffController.remove(pc, EarthSkin.class);
		// 아이언스킨 제거
		BuffController.remove(pc, IronSkin.class);
		// 브레스오브어스 적용
		BuffController.append(pc, BlessOfEarth.clone(BuffController.getPool(BlessOfEarth.class), skill, time));
	}
}
