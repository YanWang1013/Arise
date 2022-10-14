package lineage.world.object.magic;

import lineage.bean.database.Poly;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.ItemMaplewandDatabase;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectPolyIcon;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class ShapeChange extends Magic {

	public ShapeChange(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ShapeChange(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		
		o.setGfx(o.getClassGfx());
		if(o.getInventory()!=null && o.getInventory().getSlot(Lineage.SLOT_WEAPON)!=null)
			o.setGfxMode(o.getClassGfxMode()+o.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
		else
			o.setGfxMode(o.getClassGfxMode());
		o.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), o), true);
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
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
				// 변신
				onBuff(cha, o, null, skill.getBuffDuration(), true, true);
				// 이팩트
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			}
		}
	}
	
	static public void init(Character cha, int time){
		if(Lineage.server_version > 182)
			cha.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), time));
		BuffController.append(cha, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(9, 2), time));
	}
	
	/**
	 * 몬스터가 사용하는 변신 처리 함수.
	 *  : 몬스터 몇셀 주변에 있는 모든 사용자들을 강제 변신시킴.
	 *  : 리치가 사용중.
	 * @param mi
	 */
	static public void init(MonsterInstance mi, int loc, Poly poly, int time){
		for(object o : mi.getInsideList()){
			if(o instanceof PcInstance){
				// 변신
				onBuff(mi, o, poly, time, true, true);
			}
		}
	}
	
	/**
	 * 변신주문서 에서 호출해서 사용중..
	 * @param cha
	 * @param target
	 * @param p
	 * @param time
	 */
	static public void init(Character cha, Character target, Poly p, int time, int bress){
		if(p != null){
			if(cha.getGm() >= Lineage.GMCODE || p.getMinLevel()<=cha.getLevel() || Lineage.event_poly || (bress==0 && Lineage.item_polymorph_bless)) {
				onBuff(cha, target, p, time, false, true);
			}else{
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 181));
			}
		}else{
			BuffController.remove(cha, ShapeChange.class);
		}
	}
	
	/**
	 * 변신 최종 뒷처리 구간
	 *  : 마법을 통해서 이쪽으로 옴.
	 *  : 변막을 통해서 이쪽으로 옴.
	 * @param cha
	 * @param o
	 * @param time
	 */
	static public void onBuff(Character cha, object o, Poly p, int time, boolean ring, boolean packet){
		if(cha==null || o==null || !(o instanceof PcInstance))
			return;
		
		if(o.getInventory()!=null && ring && o.getInventory().isRingOfPolymorphControl()){
			// 변신할 괴물의 이름을 넣으십시오.
			if(packet)
				o.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 180));
		}else{
			if(p == null)
				p = ItemMaplewandDatabase.randomPoly();
			if(p!=null && !o.isDead()){
				if(o instanceof Character)
					// 장비 해제.
					PolyDatabase.toEquipped((Character)o, p);
				// 변신
				o.setGfx(p.getGfxId());
				o.setGfxMode(p.getGfxMode());
				if(packet){
					o.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), o), true);
					if(Lineage.server_version > 182)
						o.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), time));
				}
				// 버프등록
				BuffController.append(o, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(9, 2), time));
			}else{
				if(packet)
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			}
		}
	}
	
}
