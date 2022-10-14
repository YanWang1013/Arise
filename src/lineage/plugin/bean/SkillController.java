package lineage.plugin.bean;

import lineage.network.packet.ClientBasePacket;
import lineage.plugin.Plugin;
import lineage.world.object.Character;

public class SkillController implements Plugin {

	public boolean toSkill(Character cha, final ClientBasePacket cbp, int level, int number){
		return false;
	}

	@Override
	public Object init(Class<?> c, Object... opt) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
