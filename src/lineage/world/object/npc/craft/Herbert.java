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

public class Herbert extends CraftInstance {
	
	public Herbert(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("티셔츠");
		if(i != null){
			craft_list.put("request t-shirt", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 3) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 2) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 30000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("마법 망토");
		if(i != null){
			craft_list.put("request cloak of magic resistance", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 2) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 1000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("보호 망토");
		if(i != null){
			craft_list.put("request cloak of protection", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 5) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 5) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 20000) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "herbert2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "herbert1"));
	}

}
