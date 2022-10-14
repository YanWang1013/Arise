package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 리스 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new 리스();
		else
			((리스)bp).toClone();
		return bp;
	}
	
	public 리스(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.C_OPCODE_REQUESTCHARSELETE);
	}

}
