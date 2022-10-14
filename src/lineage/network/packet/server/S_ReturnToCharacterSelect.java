package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ReturnToCharacterSelect extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_ReturnToCharacterSelect();
		else
			((S_ReturnToCharacterSelect)bp).toClone();
		return bp;
	}
	
	public S_ReturnToCharacterSelect(){
		toClone();
	}
	
	public void toClone(){
		clear();

		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x2A);
		writeD(0);
		writeH(0);
	}
}
