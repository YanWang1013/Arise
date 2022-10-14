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
import lineage.world.object.magic.EnchantWeapon;

public class WeaponEnchanter extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "enchanterw1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("encw")){
			if(pc.getInventory().isAden(100, true)){
				Skill s = SkillDatabase.find(2, 3);
				if(s != null)
					EnchantWeapon.onBuff(pc, pc.getInventory().getSlot(Lineage.SLOT_WEAPON), s, s.getBuffDuration());
			}else{
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
			}
		}
	}

}
