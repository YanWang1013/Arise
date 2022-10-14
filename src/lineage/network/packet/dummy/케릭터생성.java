package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 케릭터생성 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, String name){
		if(bp == null)
			bp = new 케릭터생성(name);
		else
			((케릭터생성)bp).toClone(name);
		return bp;
	}
	
	public 케릭터생성(String name){
		toClone(name);
	}
	
	public void toClone(String name){
		clear();
		writeC(Opcodes.C_OPCODE_NEWCHAR);
		writeS(name);
		writeC(3);
		writeC(0);
		
		writeC(16);
		writeC(12);
		writeC(18);
		writeC(9);
		writeC(12);
		writeC(8);
	}

}
