package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 종료 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new 종료();
		else
			((종료)bp).toClone();
		return bp;
	}
	
	public 종료(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.C_OPCODE_QUITGAME);
	}

}
