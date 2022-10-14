package lineage.network.packet.client;

import lineage.bean.lineage.Kingdom;
import lineage.database.TeleportHomeDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectRestart extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectRestart(data, length);
		else
			((C_ObjectRestart)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectRestart(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		// 근처 마을 좌표로 변경.
		// 성이 존재한다면 내성으로 (잊섬 제외)
		Kingdom k = KingdomController.find(pc);
		if(k!=null && pc.getMap()!=70){
			pc.setHomeX( k.getX() );
			pc.setHomeY( k.getY() );
			pc.setHomeMap( k.getMap() );
		}else{
			TeleportHomeDatabase.toLocation(pc);
		}
		// 죽은 정보 복구.
		pc.toReset(false);
		// 마을로 이동.
		pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), false);
		return this;
	}
	
}
