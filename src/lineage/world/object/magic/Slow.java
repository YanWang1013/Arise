package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class Slow extends Magic {
	
	public Slow(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Slow(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setSpeed(2);
		toBuffUpdate(o);
		// 공격당한거 알리기.
		o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
	}

	@Override
	public void toBuffUpdate(object o) {
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), getTime()), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setSpeed(0);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), 0), true);
	}
	
	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, true, false))
				onBuff(o, skill);
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, Slow.clone(BuffController.getPool(Slow.class), SkillDatabase.find(4, 4), time));
	}
	
	static public void onBuff(object o, Skill skill){
		// 무기중 광전사의 도끼를 착용하고 있을경우 처리를 하지 않는다.
		// 방패중 에바의 방패역시 처리하지 않는다.
		ItemInstance item1 = o.getInventory()!=null ? o.getInventory().getSlot(Lineage.SLOT_WEAPON) : null;
		ItemInstance item2 = o.getInventory()!=null ? o.getInventory().getSlot(Lineage.SLOT_SHIELD) : null;
		if( (item1!=null && item1.getItem().getNameIdNumber()==418) ||
			(item2!=null && item2.getItem().getNameIdNumber()==419)	){
			// 무시..
		}else{
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			if(o.isBrave()){
				// 용기 와퍼 제거
				BuffController.remove(o, Bravery.class);
				BuffController.remove(o, Wafer.class);
			}else if(o.getSpeed()==1){
				// 촐기 및 헤이스트 제거
				BuffController.remove(o, Haste.class);
			}else{
				// 슬로우 등록 및 활성화.
				BuffController.append(o, Slow.clone(BuffController.getPool(Slow.class), skill, skill.getBuffDuration()));
			}
		}
	}
}
