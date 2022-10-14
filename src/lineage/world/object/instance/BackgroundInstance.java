package lineage.world.object.instance;

import lineage.world.object.object;

public class BackgroundInstance extends object {

	static public BackgroundInstance clone(BackgroundInstance bi){
		if(bi == null)
			bi = new BackgroundInstance();
		return bi;
	}
	
}
