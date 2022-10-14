package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffectLocation;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class FogOfSleeping extends Magic {

	static private List<object> list_temp = new ArrayList<object>();
	
	public FogOfSleeping(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new FogOfSleeping(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffFogOfSleeping(true);
		if(!o.isLockLow()){
			o.setLockLow(true);
			o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x02));
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete() || !o.isLock())
			return;
		o.setBuffFogOfSleeping(false);
		o.setLockLow(false);
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x00));
	}
	
	static public void init(Character cha, Skill skill, int x, int y){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true)){
			cha.toSender(S_ObjectEffectLocation.clone(BasePacketPooling.getPool(S_ObjectEffectLocation.class), skill.getCastGfx(), x, y), true);
			
			synchronized (list_temp) {
				// 초기화.
				list_temp.clear();
				// 추출.
				for(object o : cha.getInsideList()){
					if(o instanceof Character && Util.isDistance(x, y, cha.getMap(), o.getX(), o.getY(), o.getMap(), skill.getRange()) && !list_temp.contains(o))
						list_temp.add(o);
				}
				// 적용.
				for(object o : list_temp){
					if(SkillController.isFigure(cha, o, skill, true, false))
						onBuff(cha, o, skill, skill.getBuffDuration());
				}
			}
		}
	}
	
	/**
	 * 중복 코드 방지용.
	 * @param o
	 * @param skill
	 * @param time
	 */
	static public void onBuff(Character cha, object o, Skill skill, int time){
		BuffController.append(o, FogOfSleeping.clone(BuffController.getPool(FogOfSleeping.class), skill, time));
		// 공격당한거 알리기.
		o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
	}
	
}
