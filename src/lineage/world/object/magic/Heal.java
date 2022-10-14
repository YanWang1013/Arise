package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Heal {

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
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
				// 적용
				onBuff(cha, o, skill, skill.getCastGfx(), 0);
			}
		}
	}
	
	/**
	 * 몬스터가 사용중
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect){
		mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		if(SkillController.isMagic(mi, ms, true) && SkillController.isFigure(mi, o, ms, false, false))
			onBuff(mi, o, ms.getSkill(), effect>0 ? effect : ms.getSkill().getCastGfx(), Util.random(ms.getMindmg(), ms.getMaxdmg()));
	}

	/**
	 * 체력회복 공통 처리 구간.
	 *  : 마법주문서 (힐) 도 이걸 이용함.
	 *  : HelperNovice 에서도 이걸 이용함.
	 */
	static public void onBuff(Character cha, object o, Skill skill, int effect, int alpha_dmg){
		// 이팩트
		if(effect>0 && o!=null)
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), o instanceof PcInstance);
		// 처리
		int dmg = SkillController.getDamage(cha, o, o, skill, alpha_dmg, skill.getElement());
		if(dmg > 0){
			if(o instanceof MonsterInstance && !(o.getSummon()!=null && o.getSummon().getMasterObjectId()==cha.getObjectId())){
				MonsterInstance mon = (MonsterInstance)o;
				if(mon.getMonster().getResistanceUndead() < 0){
					int a = mon.getMonster().getResistanceUndead();
					a = (~a) + 1;
					// 데미지 처리
					dmg = (int)((double)dmg*((double)a*0.01) );
					DamageController.toDamage(cha, mon, dmg, Lineage.ATTACK_TYPE_MAGIC);
					// 패킷 처리
					mon.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mon, Lineage.GFX_MODE_DAMAGE), false);
					return;
				}
			}

			o.setNowHp( o.getNowHp() + dmg );
		}
	}

	static public int chaInt(Character cha){
		int dmg = 0;
		int stat = cha.getTotalInt();
		switch(stat){
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				dmg = 0;
				break;
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				dmg = 1;
				break;
			case 13:
			case 14:
				dmg = 2;
				break;
			case 15:
			case 16:
				dmg = 4;
				break;
			case 17:
			case 18:
				dmg = 5;
				break;
			case 19:
			case 20:
				dmg = 6;
				break;
			case 21:
			case 22:
				dmg = 7;
				break;
			case 23:
			case 24:
				dmg = 8;
				break;
			default:
				dmg = stat - 16;
				break;
		}
		return dmg;
	}
	
}
