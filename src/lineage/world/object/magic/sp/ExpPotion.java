package lineage.world.object.magic.sp;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Magic;

public class ExpPotion extends Magic {

	public ExpPotion(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ExpPotion(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if(skill != null){
			float exp = Util.random(skill.getMindmg(), skill.getMaxdmg()) * 0.01F;
			if(exp <= 0)
				exp = 0.5F;
			
			PcInstance pc = (PcInstance)o;
			pc.setDynamicExp( pc.getDynamicExp() + exp );
			if(Common.requestor.equalsIgnoreCase("giro"))
				ChattingController.toChatting(o, "드래곤의 마법이 온 몸에 스며듭니다.", Lineage.CHATTING_MODE_MESSAGE);
			else
				ChattingController.toChatting(o, "드래곤의 마법에 의해 온 몸의 신경이 날카로워집니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuffUpdate(object o) {
		toBuffStart(o);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(skill != null){
			if(Common.requestor.equalsIgnoreCase("giro"))
				ChattingController.toChatting(o, "드래곤의 마법이 온 몸에서 빠져나갑니다.", Lineage.CHATTING_MODE_MESSAGE);
			else
				ChattingController.toChatting(o, "드래곤의 마법에 의해 날카로워졌던 신경이 정상으로 돌아왔습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
		float exp = Util.random(skill.getMindmg(), skill.getMaxdmg()) * 0.01F;
		if(exp <= 0)
			exp = 0.5F;

		PcInstance pc = (PcInstance)o;
		pc.setDynamicExp( pc.getDynamicExp() - exp );
	}

	@Override
	public void toBuff(object o) {
		if(skill!=null && skill.getCastGfx()>0)
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		if(getTime()==1600)
			ChattingController.toChatting(o, "경험치 추가 효과가 "+getTime()+"초 남았습니다.", 20);
		if(getTime()<5)
			ChattingController.toChatting(o, "경험치 추가 효과가 "+getTime()+"초 남았습니다.", 20);
	}
	
	static public void init(Character cha, int time){
		Skill s = SkillDatabase.find(405);
		if(s != null)
			BuffController.append(cha, ExpPotion.clone(BuffController.getPool(ExpPotion.class), s, time));
	}
	
	static public void onBuff(object o, Skill skill){
		BuffController.append(o, ExpPotion.clone(BuffController.getPool(ExpPotion.class), skill, skill.getBuffDuration()));
	}



}
