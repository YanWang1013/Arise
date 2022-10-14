package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectSpeed extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o, int type, int speed, int time){
		if(bp == null)
			bp = new S_ObjectSpeed(o, type, speed, time);
		else
			((S_ObjectSpeed)bp).clone(o, type, speed, time);
		return bp;
	}
	
	public S_ObjectSpeed(object o, int type, int speed, int time){
		clone(o, type, speed, time);
	}
	
	public void clone(object o, int type, int speed, int time){
		clear();
		switch(type){
			case 0:		// 촐기, 헤이등 1차가속
				writeC(Opcodes.S_OPCODE_SKILLHASTE);
				break;
			case 1:		// 용기, 와퍼등 2차가속+
				writeC(Opcodes.S_OPCODE_SKILLBRAVE);
				break;
		}
		writeD(o.getObjectId());
		writeC(speed);	// 속도	0:노멀, 그외에 클레스값으로 세팅하면 그 클레스에 프레임으로 처리함.
		writeH(time);	// 시간
	}
}
