package lineage.network.packet.server;

import lineage.bean.lineage.Useshop;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectAction extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectAction(o);
		else
			((S_ObjectAction)bp).clone(o);
		return bp;
	}

	static public BasePacket clone(BasePacket bp, object o, int action){
		if(bp == null)
			bp = new S_ObjectAction(o, action);
		else
			((S_ObjectAction)bp).clone(o, action);
		return bp;
	}

	static public BasePacket clone(BasePacket bp, object o, Useshop us){
		if(bp == null)
			bp = new S_ObjectAction(o, us);
		else
			((S_ObjectAction)bp).clone(o, us);
		return bp;
	}
	
	public S_ObjectAction(object o){
		clone(o);
	}
	
	public S_ObjectAction(object o, int action){
		clone(o, action);
	}
	
	public S_ObjectAction(object o, Useshop us){
		clone(o, us);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_DOACTION);
		writeD(o.getObjectId());
		writeC(o.getGfxMode());
	}
	
	public void clone(object o, int action){
		clear();
		writeC(Opcodes.S_OPCODE_DOACTION);
		writeD(o.getObjectId());
		writeC(action);
	}
	
	public void clone(object o, Useshop us){
		clear();
		writeC(Opcodes.S_OPCODE_DOACTION);
		writeD(o.getObjectId());
		writeC(o.getGfxMode());
		if(us.getMsg() != null)
			writeB(us.getMsg());
	}
}
