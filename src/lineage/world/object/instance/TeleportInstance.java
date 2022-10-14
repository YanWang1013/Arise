package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.database.NpcTeleport;
import lineage.database.NpcTeleportDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.object;

public class TeleportInstance extends object {
	
	protected Npc npc;
	protected List<NpcTeleport> list;
	
	public TeleportInstance(Npc npc){
		this.npc = npc;
		list = new ArrayList<NpcTeleport>();
		// 텔레포트 정보 추출.
		NpcTeleportDatabase.find(npc.getName(), list);
	}

	private NpcTeleport find(String action){
		for(NpcTeleport nt : list){
			if(nt.getAction().equalsIgnoreCase(action))
				return nt;
		}
		return null;
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		NpcTeleport nt = find(action);
		if(nt != null){
			if( pc.getInventory().isAden(nt.getPrice(), true) )
				pc.toPotal(nt.getX(), nt.getY(), nt.getMap());
			else
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
		}
	}
}
