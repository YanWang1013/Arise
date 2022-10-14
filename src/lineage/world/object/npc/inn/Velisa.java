package lineage.world.object.npc.inn;

import lineage.bean.database.Npc;
import lineage.bean.lineage.InnKey;
import lineage.world.World;
import lineage.world.object.instance.InnInstance;
import lineage.world.object.instance.PcInstance;

public class Velisa extends InnInstance {
	
	public Velisa(Npc n){
		super(n);
		
		inn_room_map = 20480;
		inn_hall_map = 20992;
	}
	
	@Override
	protected void toEnter(PcInstance pc){
		// 룸
		InnKey ik = find(pc);
		if(ik != null){
			pc.toPotal(32745, 32803, inn_room_map);
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
					pc.toPotal(32628, 33167, 4);
			}
		}
	}

}
