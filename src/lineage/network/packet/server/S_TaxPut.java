package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_TaxPut extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_TaxPut(o);
		else
			((S_TaxPut)bp).toClone(o);
		return bp;
	}
	
	public S_TaxPut(object o){
		toClone(o);
	}
	
	public void toClone(object o){
		clear();
		
		writeC(Opcodes.S_OPCODE_CASTLETAXIN);
		writeD(o.getObjectId());
	}

}
