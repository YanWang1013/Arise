package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;

public class C_ObjectMoving extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectMoving(data, length);
		else
			((C_ObjectMoving)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectMoving(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || !isRead(5) || pc.isWorldDelete())
			return this;
		
		int locx = readH();
		int locy = readH();
		int heading = readC();
		switch(heading){
			case 0:
				locy--;
				break;
			case 1:
				locx++;
				locy--;
				break;
			case 2:
				locx++;
				break;
			case 3:
				locx++;
				locy++;
				break;
			case 4:
				locy++;
				break;
			case 5:
				locx--;
				locy++;
				break;
			case 6:
				locx--;
				break;
			case 7:
				locx--;
				locy--;
				break;
			default:
				heading = 0;
				locy--;
				break;
		}
		
		pc.toMoving(locx, locy, heading);
		return this;
	}
}
