package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectLawful extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectLawful(o);
		else
			((S_ObjectLawful)bp).clone(o);
		return bp;
	}
	
	public S_ObjectLawful(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		
		writeC(Opcodes.S_OPCODE_DISPOSITION);
		writeD(o.getObjectId());
		writeD(o.getLawful());
	}
}
