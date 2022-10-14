package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Party;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffStr;
import lineage.network.packet.server.S_BuffDex;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ClanController;
import lineage.world.controller.PartyController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ShiningAura extends Magic {

	public ShiningAura(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new ShiningAura(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		//
		if(o instanceof Character){
			Character cha = (Character)o;
			
			cha.setDynamicAddHitBow( cha.getDynamicAddHitBow() + 5 );
			cha.setDynamicAddHit( cha.getDynamicAddHit() + 5 );
			cha.setDynamicAddDmgBow( cha.getDynamicAddDmgBow() + 5 );
			cha.setDynamicAddDmg( cha.getDynamicAddDmg() + 5 );

			cha.setDynamicStr( cha.getDynamicStr() + 3) ;
			cha.setDynamicDex( cha.getDynamicDex() + 3) ;
			cha.setDynamicInt( cha.getDynamicInt() + 3) ;
			cha.setDynamicSp( cha.getDynamicSp() + 3) ;

			cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, getTime(), 5));
			cha.toSender(S_BuffDex.clone(BasePacketPooling.getPool(S_BuffDex.class), cha, getTime(), 5));
			cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		//
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAddHitBow( cha.getDynamicAddHitBow() - 5 );
			cha.setDynamicAddHit( cha.getDynamicAddHit() - 5 );
			cha.setDynamicAddDmgBow( cha.getDynamicAddDmgBow() - 5 );
			cha.setDynamicAddDmg( cha.getDynamicAddDmg() - 5 );

			cha.setDynamicStr( cha.getDynamicStr() - 3) ;
			cha.setDynamicDex( cha.getDynamicDex() - 3) ;
			cha.setDynamicInt( cha.getDynamicInt() - 3) ;
			cha.setDynamicSp( cha.getDynamicSp() - 3) ;

			cha.toSender(S_BuffStr.clone(BasePacketPooling.getPool(S_BuffStr.class), cha, 0, 5));
			cha.toSender(S_BuffDex.clone(BasePacketPooling.getPool(S_BuffDex.class), cha, 0, 5));
			cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}
	
	static public void init(Character cha, Skill skill){
		// 처리
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, 19), true);
		if(SkillController.isMagic(cha, skill, true) && cha instanceof PcInstance) {
			// 초기화
			PcInstance royal = (PcInstance)cha;
			List<object> list_temp = new ArrayList<object>();
			list_temp.add(royal);
			
			// 혈맹원 추출.
			Clan c = ClanController.find(royal);
			if(c != null){
				for(PcInstance pc : c.getList()){
					if(!list_temp.contains(pc))
						list_temp.add(pc);
				}
			}
	/*		
			// 파티원 추출.
			Party p = PartyController.find(royal);
			if(p != null){
				for(PcInstance pc : p.getList()){
					if(!list_temp.contains(pc))
						list_temp.add(pc);
				}
			}
			*/
			// 처리.
			for(object o : list_temp)
				onBuff(o, skill);
		}
	}
	
	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, ShiningAura.clone(BuffController.getPool(ShiningAura.class), skill, skill.getBuffDuration()));

		// 글로윙 오라 제거
	//	BuffController.remove(o, GlowingAura.class);
	}
}

//아래는 샤이닝오라 원본소스
//package lineage.world.object.magic;
//
//import lineage.bean.database.Skill;
//import lineage.bean.lineage.BuffInterface;
//import lineage.network.packet.BasePacketPooling;
//import lineage.network.packet.server.S_ObjectAction;
//import lineage.share.Lineage;
//import lineage.world.controller.SkillController;
//import lineage.world.object.Character;
//import lineage.world.object.object;
//
//public class ShiningAura extends Magic {
//
//	public ShiningAura(Skill skill){
//		super(null, skill);
//	}
//	
//	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
//		if(bi == null)
//			bi = new ShiningAura(skill);
//		bi.setSkill(skill);
//		bi.setTime(time);
//		return bi;
//	}
//
//	@Override
//	public void toBuffStart(object o) {
//		//
//	}
//
//	@Override
//	public void toBuffStop(object o){
//		toBuffEnd(o);
//	}
//
//	@Override
//	public void toBuffEnd(object o){
//		if(o.isWorldDelete())
//			return;
//	}
//	
//	static public void init(Character cha, Skill skill){
//		// 처리
//		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
//		if(SkillController.isMagic(cha, skill, true)){
//			// 전체 혈맹, 파티원들에게 ac+8 효과.
//		}
//	}
//	
//}
