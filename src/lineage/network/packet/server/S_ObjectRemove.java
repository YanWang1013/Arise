package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectRemove extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectRemove(o);
		else
			((S_ObjectRemove)bp).clone(o);
		return bp;
	}
	
	public S_ObjectRemove(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_DELETEOBJECT);
		writeD(o.getObjectId());
	}
	
}
