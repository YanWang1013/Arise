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
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Detecter extends CraftInstance {
	
	public Detecter(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("고대의 대검");
		if(i != null){
			craft_list.put("request ancient greatsword", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대의 주문서"), 1) );
			l.add( new Craft(ItemDatabase.find("잊혀진 대검"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고대의 검");
		if(i != null){
			craft_list.put("request ancient sword", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대의 주문서"), 1) );
			l.add( new Craft(ItemDatabase.find("잊혀진 검"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고대의 보우건");
		if(i != null){
			craft_list.put("request ancient bowgun", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대의 주문서"), 1) );
			l.add( new Craft(ItemDatabase.find("잊혀진 보우건"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고대의 판금 갑옷");
		if(i != null){
			craft_list.put("request ancient plate mail", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대의 주문서"), 1) );
			l.add( new Craft(ItemDatabase.find("잊혀진 판금 갑옷"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고대의 가죽 갑옷");
		if(i != null){
			craft_list.put("request ancient leather armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대의 주문서"), 1) );
			l.add( new Craft(ItemDatabase.find("잊혀진 가죽 갑옷"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고대의 로브");
		if(i != null){
			craft_list.put("request ancient robe", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대의 주문서"), 1) );
			l.add( new Craft(ItemDatabase.find("잊혀진 로브"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("고대의 비늘 갑옷");
		if(i != null){
			craft_list.put("request ancient scale mail", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대의 주문서"), 1) );
			l.add( new Craft(ItemDatabase.find("잊혀진 비늘 갑옷"), 1) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "detecter1"));
	}

}
