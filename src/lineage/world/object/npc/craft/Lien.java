package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.ElvenGuard;

public class Lien extends ElvenGuard {

	static public object clone(object o, Npc n){
		if(o == null)
			o = NpcSpawnlistDatabase.newObject(n, new Lien(n));
		else
			o = NpcSpawnlistDatabase.newObject(n, o);
		return o;
	}

	public Lien(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("아데나");
		if(i != null){
			i.getListCraft().put("request bone piece sell", 10);
			craft_list.put("request bone piece sell", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("뼈조각"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("해골투구");
		if(i != null){
			craft_list.put("request skull helmet", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("가죽투구"), 1) );
			l.add( new Craft(ItemDatabase.find("뼈조각"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 800) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("뼈갑옷");
		if(i != null){
			craft_list.put("request bone armor", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("아데나"), 500) );
			l.add( new Craft(ItemDatabase.find("뼈조각"), 20) );
			l.add( new Craft(ItemDatabase.find("중갑가죽조끼"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("골각방패");
		if(i != null){
			craft_list.put("request bone shield", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("아데나"), 800) );
			l.add( new Craft(ItemDatabase.find("징박은 가죽방패"), 1) );
			l.add( new Craft(ItemDatabase.find("뼈조각"), 15) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lien1"));
	}

}
