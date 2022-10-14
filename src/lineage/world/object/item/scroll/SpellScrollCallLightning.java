package lineage.world.object.item.scroll;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.EnergyBolt;

public class SpellScrollCallLightning extends ItemInstance {
	
	private Skill skill;
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellScrollCallLightning();
		item.setSkill( SkillDatabase.find(5, 1) );
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
		// 처리불가능한 패킷상태는 무시.
		if(!cbp.isRead(4))
			return;
		// 초기화
		object o = cha.findInsideList(cbp.readD());
		if(o != null){
			// 처리
			EnergyBolt.toBuff(cha, o, skill, Lineage.GFX_MODE_SPELL_DIRECTION, skill.getCastGfx(), 0);
			// 수량 하향
			cha.getInventory().count(this, getCount()-1, true);
		}else{
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
	}

}
