package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class EarthBind  extends Magic {

	public EarthBind(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new EarthBind(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setLockHigh(true);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 4));
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setLockHigh(false);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 5));
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
	}
	
	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		if(o!=null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true)){
				// 투망상태 해제
				Detection.onBuff(cha);
				// 공격당한거 알리기.
				o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
	
				// 처리
				if(SkillController.isFigure(cha, o, skill, true, false)){
					o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
					BuffController.append(o, EarthBind.clone(BuffController.getPool(EarthBind.class), skill, skill.getBuffDuration()));
					return;
				}
				// \f1마법이 실패했습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
			}
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, EarthBind.clone(BuffController.getPool(EarthBind.class), SkillDatabase.find(20, 4), time));
	}
	
}
