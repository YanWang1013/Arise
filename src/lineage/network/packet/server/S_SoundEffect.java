package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_SoundEffect extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, int effect){
		if(bp == null)
			bp = new S_SoundEffect(effect);
		else
			((S_SoundEffect)bp).clone(effect);
		return bp;
	}

	public S_SoundEffect(int effect){
		clone(effect);
	}

	public void clone(int effect){
		clear();

		writeC(Opcodes.S_OPCODE_SOUNDEFFECT);
		writeC(0);
		writeH(effect);
	}
}
