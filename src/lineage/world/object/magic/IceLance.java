package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.BackgroundDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;

public class IceLance extends Magic {

	private BackgroundInstance ice;
	
	public IceLance(Skill skill){
		super(null, skill);
		
		ice = BackgroundInstance.clone(BackgroundDatabase.getPool(BackgroundInstance.class));
		ice.setGfx(176);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new IceLance(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
	
	public object getIce(){
		return ice;
	}
		
	@Override
	public void toBuffStart(object o){
		// 아이스 표현
		ice.setObjectId(ServerDatabase.nextEtcObjId());
		ice.toTeleport(o.getX(), o.getY(), o.getMap(), false);
		// 객체 얼리기
		o.setLockHigh(true);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 12));
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		// 아이스 표현 제거
		ice.clearList(true);
		World.remove(ice);
		
		if(o.isWorldDelete())
			return;
		
		// 객체 풀기
		o.setLockHigh(false);
		o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 13));
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
	}
	
	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		if(o!=null && SkillController.isMagic(cha, skill, true)){
			// 데미지 처리
			int dmg = SkillController.getDamage(cha, o, o, skill, 0, skill.getElement());
			DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_MAGIC);
			
			// 패킷 처리
			cha.setHeading( Util.calcheading(cha, o.getX(), o.getY()) );
			cha.toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), cha, o, Lineage.GFX_MODE_SPELL_DIRECTION, dmg, skill.getCastGfx(), false, false, 0, 0), true);
			
			// 얼리기 처리
			if(SkillController.isFigure(cha, o, skill, true, false))
				BuffController.append(o, IceLance.clone(BuffController.getPool(IceLance.class), skill, skill.getBuffDuration()));
		}
	}

	static public void init(object o, int time){
		BuffController.append(o, IceLance.clone(BuffController.getPool(IceLance.class), SkillDatabase.find(7, 1), time));
	}
	
}
