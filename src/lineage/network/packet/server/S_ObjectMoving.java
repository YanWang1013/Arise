package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectMoving extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectMoving(o);
		else
			((S_ObjectMoving)bp).clone(o);
		return bp;
	}
	
	public S_ObjectMoving(object o){
		clone(o);
	}

	public void clone(object o){
		clear();
		int x = o.getX();
		int y = o.getY();
		switch(o.getHeading()){
			case 0:
				y += 1;
				break;
			case 1:
				x -= 1;
				y += 1;
				break;
			case 2:
				x -= 1;
				break;
			case 3:
				x -= 1;
				y -= 1;
				break;
			case 4:
				y -= 1;
				break;
			case 5:
				x += 1;
				y -= 1;
				break;
			case 6:
				x += 1;
				break;
			case 7:
				x += 1;
				y += 1;
				break;
			default:
				return;
		}
		writeC(Opcodes.S_OPCODE_MOVEOBJECT);
		writeD(o.getObjectId());
		writeH(x);
		writeH(y);
		writeC(o.getHeading());
	}
	
}
