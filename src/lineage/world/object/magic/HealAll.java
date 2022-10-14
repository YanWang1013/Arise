package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Summon;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ClanController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class HealAll {

	static private List<object> list_temp = new ArrayList<object>();
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true))
			onBuff(cha, skill);
	}
	
	static public void onBuff(object o, Skill skill){
		synchronized (list_temp) {
			list_temp.clear();
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			
			if( !(o instanceof Character) )
				return;
			
			// 혈맹원 추출.
			if(o instanceof PcInstance){
				Clan c = ClanController.find((PcInstance)o);
				if(c != null){
					for(PcInstance pc : c.getList()){
						if(Util.isDistance(o, pc, Lineage.SEARCH_LOCATIONRANGE))
							list_temp.add(pc);
					}
				}
			}
			// 서먼객체 추출.
			Summon s = SummonController.find(o);
			if(s != null){
				for(object oo : s.getList()){
					if(Util.isDistance(o, oo, Lineage.SEARCH_LOCATIONRANGE))
						list_temp.add(oo);
				}
			}
			
			// 힐 처리.
			for(object oo : list_temp)
				Heal.onBuff((Character)o, oo, skill, 0, 0);
		}
	}
}
