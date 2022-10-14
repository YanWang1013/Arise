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

public class DubleExpPotion extends Magic {

	private double dynamicExp;
	
	public DubleExpPotion(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new DubleExpPotion(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if(skill != null){
			dynamicExp = Util.random(skill.getMindmg(), skill.getMaxdmg()) * 0.01F;
			if(dynamicExp <= 0)
				dynamicExp = 0.9F;
			
			if(o instanceof PcInstance) {
				PcInstance pc = (PcInstance)o;
				pc.setDynamicExp( pc.getDynamicExp() + dynamicExp );

					ChattingController.toChatting(o, "경험치 효과가 대폭 상승하였습니다.", 20);
			}
		}
	}

	@Override
	public void toBuffUpdate(object o) {
		ChattingController.toChatting(o, "아무일도 일어나지 않았습니다.", 20);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		
		if(o instanceof PcInstance) {
			PcInstance pc = (PcInstance)o;
			pc.setDynamicExp( pc.getDynamicExp() - dynamicExp );
			if(skill != null){

					ChattingController.toChatting(o, "경험치 효과가 원래대로 돌아왔습니다.", 20);
			}
		}
	}

	@Override
	public void toBuff(object o) {
		if(skill!=null && skill.getCastGfx()>0)
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
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
