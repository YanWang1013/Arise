package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 핑 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new 핑();
		else
			((핑)bp).toClone();
		return bp;
	}
	
	public 핑(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.C_OPCODE_PING);
	}

}
