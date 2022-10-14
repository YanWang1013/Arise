package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 접속시도 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, String name){
		if(bp == null)
			bp = new 접속시도(name);
		else
			((접속시도)bp).toClone(name);
		return bp;
	}
	
	public 접속시도(String name){
		toClone(name);
	}
	
	public void toClone(String name){
		clear();
		writeC(Opcodes.C_OPCODE_LOGINTOSERVER);
		writeS(name);
	}


}
