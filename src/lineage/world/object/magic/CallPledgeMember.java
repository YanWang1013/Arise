package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Clan;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.controller.ClanController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.PcInstance;

public class CallPledgeMember {

	static public void init(Character cha, Skill skill, String name){
		// 처리
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true)){
			Clan c = ClanController.find(cha.getClanId());
			PcInstance pc = c.find(name);
			if(pc!=null && pc.getObjectId()!=cha.getObjectId())
				// 729 군주님께서 부르십니다. 소환에 응하시겠습니까? (y/N)
				pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 729));
		}
	}
	
}
