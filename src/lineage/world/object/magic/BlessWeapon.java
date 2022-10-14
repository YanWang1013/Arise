package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class BlessWeapon extends Magic {

	public BlessWeapon(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Haste(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffBlessWeapon(true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffBlessWeapon(false);
		if(o instanceof ItemInstance){
			ItemInstance ii = (ItemInstance)o;
			if(ii.getCharacter() != null)
				// 무기가 보통으로 돌아왔습니다.
				ii.getCharacter().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 718));
		}
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
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true) && o.getInventory()!=null && SkillController.isFigure(cha, o, skill, false, false))
				onBuff(o, skill);
		}
	}
	
	static public void onBuff(object o, Skill skill){
		if(o.getInventory() == null)
			return;
		
		ItemInstance item = o.getInventory().getSlot(Lineage.SLOT_WEAPON);
		if(item != null){
			// 중복되지않게 다른 버프 제거.
			// 윈드샷
			BuffController.remove(o, WindShot.class);
			// 브레스 오브 파이어
			BuffController.remove(o, BlessOfFire.class);
			// 버닝웨폰
			BuffController.remove(o, BurningWeapon.class);
			// 스톰샷
			BuffController.remove(o, StormShot.class);
			
			// 버프 등록
			BuffController.append(item, BlessWeapon.clone(BuffController.getPool(BlessWeapon.class), skill, skill.getBuffDuration()));
			
			// 패킷 처리
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 161, item.toString(), "$245", "$247"));
		}
	}
	
}
