package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectMode extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectMode(o);
		else
			((S_ObjectMode)bp).clone(o);
		return bp;
	}
	
	public S_ObjectMode(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_OBJECTMODE);
		writeD(o.getObjectId());
		writeC(o.getGfxMode());
		writeC(0xff);
		writeC(0xff);
	}
}
