package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.database.ItemDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.InnInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetMasterInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.instance.TeleportInstance;
import lineage.world.object.npc.ClanMaker;
import lineage.world.object.npc.Ellyonne;
import lineage.world.object.npc.Maid;
import lineage.world.object.npc.Sedia;
import lineage.world.object.npc.Siris;
import lineage.world.object.npc.Slimerace;
import lineage.world.object.npc.TalkNpc;
import lineage.world.object.npc.buff.ArmorEnchanter;
import lineage.world.object.npc.buff.Curer;
import lineage.world.object.npc.buff.Hadesty;
import lineage.world.object.npc.buff.Haste;
import lineage.world.object.npc.buff.PolymorphMagician;
import lineage.world.object.npc.buff.WeaponEnchanter;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomChamberlain;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.kingdom.KingdomDoorman;
import lineage.world.object.npc.quest.GatekeeperAnt;
import lineage.world.object.npc.quest.Richard;
import lineage.world.object.npc.quest.SearchAnt;

public class Map {
	private List<object> list_temp;
	private List<object> list;
	public int mapid;
	public int locX1;
	public int locX2;
	public int locY1;
	public int locY2;
	public int size;
	public byte[] data;
	public byte[] dataDynamic;
	public int data_size;
	public String name;
	
	public Map(){
		list_temp = new ArrayList<object>();
		list = new ArrayList<object>();
	}
	
	public void append(object o){
		synchronized (list) {
			list.add(o);
		}
		o.setWorldDelete( false );
		
//		lineage.share.System.printf("World (%d) : %d\r\n", mapid, list.size());
	}
	
	public void remove(object o){
		synchronized (list) {
			list.remove(o);
		}
		o.setWorldDelete( true );
		
//		lineage.share.System.printf("World (%d) : %d\r\n", mapid, list.size());
	}
	
	public void getList(object o, int loc, List<object> r_list){
		synchronized (list) {
			for(object oo : list){
				if(o.getObjectId()!=oo.getObjectId() && Util.isDistance(o, oo, loc))
					r_list.add(oo);
			}
		}
	}
	
	public void clearWorldItem(){
		// 추출.
		list_temp.clear();
		synchronized (list) {
			for(object o : list){
				if(o instanceof ItemInstance)
					list_temp.add(o);
			}
		}
		// 삭제.
		for(object o : list_temp){
			remove(o);
			o.clearList(true);
			ItemDatabase.setPool((ItemInstance)o);
		}
	}
	
	
	
	public int getSize(){
		return list.size();
	}
	
	/**
	 * 해당 맵에 스폰된 객체 분류해서 리턴함.
	 * @param player
	 * @param monster
	 * @param npc
	 */
	public void searchObject(List<object> r_list, boolean item, boolean npc, boolean monster, boolean background, boolean player, boolean shop){
		synchronized (list) {
			for(object o : list){
				if(npc){
					if(o instanceof NpcInstance)
						r_list.add(o);
					if(o instanceof ArmorEnchanter)
						r_list.add(o);
					if(o instanceof ClanMaker)
						r_list.add(o);
					if(o instanceof Curer)
						r_list.add(o);
					if(o instanceof DwarfInstance)
						r_list.add(o);
					if(o instanceof Ellyonne)
						r_list.add(o);
					if(o instanceof GatekeeperAnt)
						r_list.add(o);
					if(o instanceof Hadesty)
						r_list.add(o);
					if(o instanceof Haste)
						r_list.add(o);
					if(o instanceof InnInstance)
						r_list.add(o);
					if(o instanceof KingdomChamberlain)
						r_list.add(o);
					if(o instanceof KingdomDoorman)
						r_list.add(o);
					if(o instanceof Maid)
						r_list.add(o);
					if(o instanceof PetMasterInstance)
						r_list.add(o);
					if(o instanceof PolymorphMagician)
						r_list.add(o);
					if(o instanceof Richard)
						r_list.add(o);
					if(o instanceof SearchAnt)
						r_list.add(o);
					if(o instanceof Sedia)
						r_list.add(o);
					if(o instanceof Siris)
						r_list.add(o);
					if(o instanceof Slimerace)
						r_list.add(o);
					if(o instanceof TalkNpc)
						r_list.add(o);
					if(o instanceof TeleportInstance)
						r_list.add(o);
					if(o instanceof WeaponEnchanter)
						r_list.add(o);
					if(o instanceof KingdomCastleTop)
						r_list.add(o);
					if(o instanceof KingdomDoor)
						r_list.add(o);
				}
				if(shop){
					if(o instanceof ShopInstance)
						r_list.add(o);
				}
				if(monster){
					if(o instanceof MonsterInstance)
						r_list.add(o);
				}
				if(player){
					if(o instanceof PcInstance)
						r_list.add(o);
				}
				if(item){
					if(o instanceof ItemInstance)
						r_list.add(o);
				}
				if(background){
					if(o instanceof BackgroundInstance)
						r_list.add(o);
				}
			}
		}
	}
}
