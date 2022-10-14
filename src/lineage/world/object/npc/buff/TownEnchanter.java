package lineage.world.object.npc.buff;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.BlessedArmor;
import lineage.world.object.magic.EnchantWeapon;

public class TownEnchanter extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "enchanterb1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("encb1")){
			if(pc.getInventory().isAden(3000, true)){
				Skill s = SkillDatabase.find(2, 3);
				if(s != null)
					EnchantWeapon.onBuff(pc, pc.getInventory().getSlot(Lineage.SLOT_WEAPON), s, s.getBuffDuration());
			}else{
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
			}
		}
	
			if(action.equalsIgnoreCase("encb2")){
			if(pc.getInventory().isAden(3000, true)){
				Skill s2 = SkillDatabase.find(3, 4);
				if(s2 != null)
					BlessedArmor.onBuff(pc, pc.getInventory().getSlot(Lineage.SLOT_ARMOR), s2, s2.getBuffDuration());
			}else{
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
			}
		}
	}

}
