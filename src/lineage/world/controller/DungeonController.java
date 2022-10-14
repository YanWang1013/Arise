package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Dungeon;
import lineage.database.DungeonDatabase;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public final class DungeonController {

	static private List<ItemInstance> search_list;
	
	static public void init(){
		TimeLine.start("DungeonController..");
		
		search_list = new ArrayList<ItemInstance>();
		
		TimeLine.end();
	}

	/**
	 * pcinstance들이 이동하다가 필드값이 127과 일치할때 호출 그리고
	 * 이동된 좌표를 확인하여, 디비와 일치하는 좌표라면 뒷처리 함.
	 */
	static public void toDungeon(object o){
		Dungeon d = DungeonDatabase.find(o);
		if(d != null){
			search_list.clear();
			// 성공적으로 텔레포트를 이행할지를 판단용으로 사용하는 변수.
			boolean tel = true;
			// 아이템이 필요한 던전일경우 확인하기.
			if(d.getItemCount()>0 && o instanceof PcInstance){
				// 인벤에 존재하는지 확인해야하므로 false로 기본 설정.
				tel = false;
				// 인벤에 가지고있는 아이템목록에서 해당하는 아이템 전체 불러오기.
				o.getInventory().findDbNameId(d.getItemNameid(), search_list);
				if(d.getItemNameid()==954){
					// 여관키 일경우 전체 순회하기
					for( ItemInstance item : search_list ){
						// 해당 여관키와 연결된 현재 방이 존재하는지 확인.
						// 근데 어떤 여관인지 확인도 해야되네.. 좌표 확인해야할듯.
//						tel = InnController::isRoom(o, *p);
//						if(tel)
//							break;
					}
				}else{
					// 그외에는 1개 이상일경우 무조건 이동.
					tel = search_list.size()>0;
				}
			}

			if(tel)
				o.toPotal(d.getGotoX(), d.getGotoY(), d.getGotoM());

		}
	}
	
}
