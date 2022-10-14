package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Party;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ClanController;
import lineage.world.controller.PartyController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class GlowingAura extends Magic {

	static private List<object> list_temp = new ArrayList<object>();
	
	public GlowingAura(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new GlowingAura(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBuffGlowingAura(true);
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAc( cha.getDynamicAc() + 2 );
			cha.setDynamicMr( cha.getDynamicMr() + 5 );
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
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
		o.setBuffGlowingAura(false);
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicAc( cha.getDynamicAc() - 2 );
			cha.setDynamicMr( cha.getDynamicMr() - 5 );
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
		}
	}
	
	static public void init(Character cha, Skill skill){
		// 패킷
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		// 시전가능 확인
		if(SkillController.isMagic(cha, skill, true) && cha instanceof PcInstance){
			
			synchronized (list_temp) {
				// 초기화
				PcInstance royal = (PcInstance)cha;
				list_temp.clear();
				list_temp.add(royal);
				
				// 혈맹원 추출.
				Clan c = ClanController.find(royal);
				if(c != null){
					for(PcInstance pc : c.getList()){
						if(Util.isDistance(royal, pc, Lineage.SEARCH_LOCATIONRANGE) && !list_temp.contains(pc))
							list_temp.add(pc);
					}
				}
				
				// 파티원 추출.
				Party p = PartyController.find(royal);
				if(p != null){
					for(PcInstance pc : p.getList()){
						if(Util.isDistance(royal, pc, Lineage.SEARCH_LOCATIONRANGE) && !list_temp.contains(pc))
							list_temp.add(pc);
					}
				}
				
				// 처리.
				for(object o : list_temp)
					onBuff(o, skill);
			}
		}
	}
	
	/**
	 * 중복코드 방지용.
	 * @param cha
	 * @param pc
	 * @param skill
	 */
	static public void onBuff(object o, Skill skill){
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		BuffController.append(o, GlowingAura.clone(BuffController.getPool(GlowingAura.class), skill, skill.getBuffDuration()));
	}
	
}
