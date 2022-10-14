package lineage.world.object.npc;

import lineage.world.object.object;

public class Slimerace extends object {

	public boolean finish;		// 
	public boolean Lucky;		// 
	public int Status;			// 
	public double Theory;		// 
	public int idx;				// 
	public int countting;		// 표를 구매한 사람들의 갯수 배당처리를 위해 필요.
	
	public void clean(){
		finish = false;
		Lucky = false;
		Status = 0;
		Theory = 0;
		countting = 0;
	}
	
}
