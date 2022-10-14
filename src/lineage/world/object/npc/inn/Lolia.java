package lineage.world.object.npc.inn;

import lineage.bean.database.Npc;
import lineage.bean.lineage.InnKey;
import lineage.world.World;
import lineage.world.object.instance.InnInstance;
import lineage.world.object.instance.PcInstance;

public class Lolia extends InnInstance {
	
	public Lolia(Npc n){
		super(n);
		
		inn_room_map = 17408;
		inn_hall_map = 17920;
	}
	
	@Override
	protected void toEnter(PcInstance pc){
		// 룸
		InnKey ik = find(pc);
		if(ik != null){
			pc.toPotal(32744, 32803, inn_room_map);
			return;
		}

		// 홀
		ik = findHall(pc);
		if(ik != null){
			pc.toPotal(32745, 32807, inn_hall_map);
			return;
		}
	}
	
	@Override
	protected void toOut(InnKey ik){
		for(PcInstance pc : World.getPcList()){
			// 해당하는 맵만 처리.
			if(pc.getMap()==inn_room_map || pc.getMap()==inn_hall_map){
				if(isOut(ik, pc))
					pc.toPotal(32630, 32750, 4);
			}
		}
	}

}
