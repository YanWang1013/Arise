package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ObjectBlind extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, int blind){
		if(bp == null)
			bp = new S_ObjectBlind(blind);
		else
			((S_ObjectBlind)bp).clone(blind);
		return bp;
	}
	
	public S_ObjectBlind(int blind){
		clone(blind);
	}
	
	public void clone(int blind){
		clear();
		writeC(Opcodes.S_OPCODE_BlindPotion);
		writeC(blind);	// 0:보통 1:멀기 2:괴눈고기먹고멀기
	}
}
