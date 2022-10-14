package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class BurningWeapon extends Magic {

	public BurningWeapon(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new BurningWeapon(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffBurningWeapon(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffBurningWeapon(false);
	}
	
	static public void init(Character cha, Skill skill, long object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, 19), true);
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, SkillController.isClan(cha, o)))
				onBuff(o, skill);
		}
	}
	
	static public void onBuff(object o, Skill skill) {
		// 중복되지않게 다른 버프 제거.
		// 블레스 웨폰
		BuffController.remove(o, BlessWeapon.class);
		// 파이어 웨폰
		BuffController.remove(o, FireWeapon.class);
		// 윈드 샷
		BuffController.remove(o, WindShot.class);
		// 브레스 오브 파이어
		BuffController.remove(o, BlessOfFire.class);
		// 스톰샷
		BuffController.remove(o, StormShot.class);
		
		// 버프 등록
		BuffController.append(o, BurningWeapon.clone(BuffController.getPool(BurningWeapon.class), skill, skill.getBuffDuration()));
	}
	
}
//		// 처리
//		if(o != null){
//			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
//			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
//				// 중복되지않게 다른 버프 제거.
//				// 블레스 웨폰
//				BuffController.remove(o, BlessWeapon.class);
//				// 파이어 웨폰
//				BuffController.remove(o, FireWeapon.class);
//				// 윈드 샷
//				BuffController.remove(o, WindShot.class);
//				// 브레스 오브 파이어
//				BuffController.remove(o, BlessOfFire.class);
//				// 스톰샷
//				BuffController.remove(o, StormShot.class);
//				
//				// 버프 등록
//				BuffController.append(o, BurningWeapon.clone(BuffController.getPool(BurningWeapon.class), skill, skill.getBuffDuration()));
//				
//				// 패킷 처리
//				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
//			}
//		}
//	}
//	
//}
