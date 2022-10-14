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

public class Hector extends CraftInstance {
	
	public Hector(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("강철 장갑");
		if(i != null){
			craft_list.put("request iron gloves", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("장갑"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 150) );
			l.add( new Craft(ItemDatabase.find("아데나"), 25000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 면갑");
		if(i != null){
			craft_list.put("request iron visor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("기사의 면갑"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 120) );
			l.add( new Craft(ItemDatabase.find("아데나"), 16500) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 방패");
		if(i != null){
			craft_list.put("request iron shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("사각 방패"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 200) );
			l.add( new Craft(ItemDatabase.find("아데나"), 16000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 장화");
		if(i != null){
			craft_list.put("request iron boots", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("부츠"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 160) );
			l.add( new Craft(ItemDatabase.find("아데나"), 8000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("강철 판금 갑옷");
		if(i != null){
			craft_list.put("request iron plate mail", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("판금 갑옷"), 1) );
			l.add( new Craft(ItemDatabase.find("철괴"), 450) );
			l.add( new Craft(ItemDatabase.find("아데나"), 30000) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "hector2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "hector1"));
		
	}

}
