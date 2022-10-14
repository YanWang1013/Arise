package lineage.world.object.npc.background;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.PcInstance;

public class Cracker extends BackgroundInstance {
	
	@Override
	public void toDamage(Character cha, int dmg, int type){
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			if(pc.getLevel()<40)
				pc.toExp(this, Util.random(1, 2));
			if(Lineage.view_cracker_damage)
				ChattingController.toChatting(cha, String.format("\\fT당신의  Damage - \\fR(%d) \\fT입니다.", dmg), Lineage.CHATTING_MODE_MESSAGE);
		}
		setHeading(++heading);
	}

	@Override
	public void setHeading(int heading){
		if(Lineage.server_version <= 163){
			if(heading > 2)
				heading = 0;
		}
		super.setHeading(heading);
		
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
	}
	
}
