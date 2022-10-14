package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectEffect extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o, int effect){
		if(bp == null)
			bp = new S_ObjectEffect(o, effect);
		else
			((S_ObjectEffect)bp).clone(o, effect);
		return bp;
	}
	
	public S_ObjectEffect(object o, int effect){
		clone(o, effect);
	}
	
	public void clone(object o, int effect){
		clear();
		writeC(Opcodes.S_OPCODE_EFFECT);
		writeD(o.getObjectId());
		writeH(effect);
	}
	
}
