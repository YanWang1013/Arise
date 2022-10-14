package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 공지확인 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new 공지확인();
		else
			((공지확인)bp).toClone();
		return bp;
	}
	
	public 공지확인(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.C_OPCODE_NOTICEOK);
		writeC(1);
	}

}
