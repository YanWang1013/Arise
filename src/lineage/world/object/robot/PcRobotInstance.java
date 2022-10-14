package lineage.world.object.robot;

import lineage.world.object.instance.RobotInstance;

public class PcRobotInstance extends RobotInstance {

	public PcRobotInstance(){
		// 기란마을로 처리하기위해.
		this.x = 33449;
		this.y = 32829;
		this.map = 4;
		this.gfx = 61;
	}
	
	@Override
	public void toWorldJoin(){
		super.toWorldJoin();
	}
	
	@Override
	public void toWorldOut(){
		super.toWorldOut();
	}
}
