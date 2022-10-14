package lineage.network.packet.dummy;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class 로그인요청 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, String id, String pw){
		if(bp == null)
			bp = new 로그인요청(id, pw);
		else
			((로그인요청)bp).toClone(id, pw);
		return bp;
	}
	
	public 로그인요청(String id, String pw){
		toClone(id, pw);
	}
	
	public void toClone(String id, String pw){
		clear();
		writeC(Opcodes.C_OPCODE_LOGINPACKET);
		writeS(id);
		writeS(pw);
	}
	
}
