package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.Magic;

public class FightConsciousnessMagic extends Magic {

	public FightConsciousnessMagic(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new FightConsciousnessMagic(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		// 지속시간:30분, 추타3 공성3 활타격치3 활명중치3 주술력3
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAddDmg( cha.getDynamicAddDmg()+3 );
			cha.setDynamicAddHit( cha.getDynamicAddHit()+3 );
			cha.setDynamicAddDmgBow( cha.getDynamicAddDmgBow()+3 );
			cha.setDynamicAddHitBow( cha.getDynamicAddHitBow()+3 );
			cha.setDynamicSp( cha.getDynamicSp()+3 );
		}
		
		if(Common.requestor.equalsIgnoreCase("giro"))
			ChattingController.toChatting(o, "전투 의식 주문서의 효과가 당신을 감쌉니다.", Lineage.CHATTING_MODE_MESSAGE);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAddDmg( cha.getDynamicAddDmg()-3 );
			cha.setDynamicAddHit( cha.getDynamicAddHit()-3 );
			cha.setDynamicAddDmgBow( cha.getDynamicAddDmgBow()-3 );
			cha.setDynamicAddHitBow( cha.getDynamicAddHitBow()-3 );
			cha.setDynamicSp( cha.getDynamicSp()-3 );
		}
		
		if(Common.requestor.equalsIgnoreCase("giro"))
			ChattingController.toChatting(o, "당신의 기분이 원래대로 돌아왔습니다.", Lineage.CHATTING_MODE_MESSAGE);
	}

}
