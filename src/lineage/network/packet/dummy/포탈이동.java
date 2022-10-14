package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 포탈이동 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new 포탈이동();
		else
			((포탈이동)bp).toClone();
		return bp;
	}
	
	public 포탈이동(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.C_OPCODE_POTALOK);
	}

}
