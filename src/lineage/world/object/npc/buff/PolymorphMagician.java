package lineage.world.object.npc.buff;

import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class PolymorphMagician extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "newgypsy"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("skeleton nbmorph")){
			type = "skeleton polymorph";
		}else if(action.equalsIgnoreCase("lycanthrope nbmorph")){
			type = "lycanthrope";
		}else if(action.equalsIgnoreCase("shelob nbmorph")){
			type = "shelob";
		}else if(action.equalsIgnoreCase("ghoul nbmorph")){
			type = "ghoul";
		}else if(action.equalsIgnoreCase("ghast nbmorph")){
			type = "ghast";
		}else if(action.equalsIgnoreCase("atuba orc nbmorph")){
			type = "atuba orc";
		}else if(action.equalsIgnoreCase("skeleton axeman nbmorph")){
			type = "skeleton axeman polymorph";
		}else{
			type = "troll";
		}
		
		if(type!=null && pc.getInventory().isAden(100, true)){
			ShapeChange.onBuff(pc, pc, PolyDatabase.getPolyName(type), 1800, false, true);
		}else{
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
		}
	}

}
