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

public class Pin extends CraftInstance {
	
	public Pin(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("아데나");
		if(i != null){
			i.getListCraft().put("request lumps of metal sell", 5);
			craft_list.put("request lumps of metal sell", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("철괴"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("징박은 가죽모자");
		if(i != null){
			craft_list.put("request studded leather cap", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("가죽모자"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 10) );
			l.add( new Craft(ItemDatabase.find("고급피혁"), 2) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("징박은 가죽샌달");
		if(i != null){
			craft_list.put("request studded leather sandal", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("가죽샌달"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 12) );
			l.add( new Craft(ItemDatabase.find("고급피혁"), 3) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("징박은 가죽조끼");
		if(i != null){
			craft_list.put("request studded leather vest", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("가죽조끼"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 10) );
			l.add( new Craft(ItemDatabase.find("고급피혁"), 2) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("징박은 가죽방패");
		if(i != null){
			craft_list.put("request studded leather shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("가죽방패"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 20) );
			l.add( new Craft(ItemDatabase.find("고급피혁"), 5) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "farlinC1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "farlin1"));
	}

}
