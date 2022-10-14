package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class DioCraft extends CraftInstance {
	
	public DioCraft(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("환생의 보석");
		if(i != null){
			craft_list.put("request Jewel of Rebirth", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 1) );
			l.add( new Craft(ItemDatabase.find("마물의 기운"), 1) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dio2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dio1"));
		
	}

}
