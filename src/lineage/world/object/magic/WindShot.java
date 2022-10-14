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

public class WindShot extends Magic {

	public WindShot(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new WindShot(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffWindShot(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffWindShot(false);
	}
	
	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
				// 중복되지않게 다른 버프 제거.
				// 블레스 웨폰
				BuffController.remove(o, BlessWeapon.class);
				// 파이어 웨폰
				BuffController.remove(o, FireWeapon.class);
				// 스톰샷
				BuffController.remove(o, StormShot.class);
				// 브레스 오브 파이어
				BuffController.remove(o, BlessOfFire.class);
				// 버닝 웨폰
				BuffController.remove(o, BurningWeapon.class);
				
				BuffController.append(o, WindShot.clone(BuffController.getPool(WindShot.class), skill, skill.getBuffDuration()));
				
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			}
		}
	}
	
}
