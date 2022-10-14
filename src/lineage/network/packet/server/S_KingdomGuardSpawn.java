package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class S_KingdomGuardSpawn extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o, PcInstance pc){
		if(bp == null)
			bp = new S_KingdomGuardSpawn(o, pc);
		else
			((S_KingdomGuardSpawn)bp).toClone(o, pc);
		return bp;
	}
	
	public S_KingdomGuardSpawn(object o, PcInstance pc){
		toClone(o, pc);
	}
	
	public void toClone(object o, PcInstance pc){
		clear();
		writeC(Opcodes.S_OPCODE_KINGDOMGUARDSPAWN);
		writeD(o.getObjectId());
		writeH(0);	// ?
		writeH(0);	// 고용된 총 용병수
		writeH(0);	// ?
		writeS(pc.getName());
		writeH(0);	// ?
		writeH(0);	// ?
		writeH(0);	// 배치된 용병수
		writeH(0);	// ?
	}

}
