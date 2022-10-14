package lineage.world.object.item.scroll;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

import lineage.world.object.magic.Meditation;

public class SpellScrollMeditation extends ItemInstance {
	
	private Skill skill;
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellScrollMeditation();
		item.setSkill( SkillDatabase.find(4, 7) );
		return item;
	}
	
	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		// 처리
		Meditation.onBuff(cha, skill, skill.getBuffDuration());
		// 수량 하향
		cha.getInventory().count(this, getCount()-1, true);
	}

}
