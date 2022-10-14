package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.Character;

public class S_TradeStart extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, Character pc){
		if(bp == null)
			bp = new S_TradeStart(pc);
		else
			((S_TradeStart)bp).toClone(pc);
		return bp;
	}
	
	public S_TradeStart(Character pc){
		toClone(pc);
	}
	
	public void toClone(Character pc){
		clear();
		
		writeC(Opcodes.S_OPCODE_TRADE);
		writeS(pc.getName());
	}
}
