package lineage.network.packet.server;

import java.util.List;

import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectAttackMagic extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o, object t, List<object> list, boolean none, int action, int dmg, int gfx, int x, int y){
		if(bp == null)
			bp = new S_ObjectAttackMagic(o, t, list, none, action, dmg, gfx, x, y);
		else
			((S_ObjectAttackMagic)bp).clone(o, t, list, none, action, dmg, gfx, x, y);
		return bp;
	}
	
	public S_ObjectAttackMagic(object o, object t, List<object> list, boolean none, int action, int dmg, int gfx, int x, int y){
		clone(o, t, list, none, action, dmg, gfx, x, y);
	}
	
	public void clone(object o, object t, List<object> list, boolean none, int action, int dmg, int gfx, int x, int y){
		clear();
		
		writeC(Opcodes.S_OPCODE_MagicAttackPacket);
		writeC(action);
		writeD(o.getObjectId());
		if(gfx==170 || gfx==171 || gfx == 245 || gfx==758 || gfx==757 || gfx==1812 || gfx==3932 || gfx==1819){
			writeH(o.getX());
			writeH(o.getY());
		}else{
			writeH(x);
			writeH(y);
		}
		writeC(o.getHeading());
		writeD(ServerDatabase.nextEtcObjId());
		writeH(gfx);
		if(none){
			if(list.size()>0){
				writeC(0x00);
				writeH(0);
				writeH(list.size());
				for(object oo : list){
					writeD(oo.getObjectId());
					writeC(1);
				}
			}else{
				writeC(0x00);
				writeH(64715);
				writeH(0);
				writeC(0);
			}
		}else{
			writeC(0x08);
			writeH(0);
			writeH(list.size()+1);
			writeD(t.getObjectId());
			writeC(dmg);
			for(object oo : list){
				writeD(oo.getObjectId());
				writeC(1);
			}
		}
	}
	
}
