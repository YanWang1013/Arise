package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class HolyWalk extends Magic {

	public HolyWalk(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new HolyWalk(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		o.setBrave(true);
		if (Lineage.server_version > 200)
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1,
					Lineage.holywalk_frame ? o.getClassType() : Lineage.LINEAGE_CLASS_KNIGHT, getTime()), true);
		else
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1,
					Lineage.holywalk_frame ? Lineage.LINEAGE_CLASS_WIZARD : Lineage.LINEAGE_CLASS_KNIGHT, getTime()), true);
	}

	@Override
	public void toBuffUpdate(object o) {
		o.setBrave(true);
		if (Lineage.server_version > 200)
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1,
					Lineage.holywalk_frame ? o.getClassType() : Lineage.LINEAGE_CLASS_KNIGHT, getTime()), true);
		else
			o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1,
					Lineage.holywalk_frame ? Lineage.LINEAGE_CLASS_WIZARD : Lineage.LINEAGE_CLASS_KNIGHT, getTime()), true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o.isWorldDelete())
			return;
		o.setBrave(false);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 0, 0), true);
	}

	static public void init(Character cha, Skill skill) {
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if (SkillController.isMagic(cha, skill, true)) {
			// ?????? ??????
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()),
					true);
			// ??????.
			if (cha.getSpeed() != 2) {
				// ????????? ?????? ??????
				// BuffController.remove(cha, Wisdom.class);

				// ????????? ????????? ????????????
				BuffController.append(cha,
						HolyWalk.clone(BuffController.getPool(HolyWalk.class), skill, skill.getBuffDuration()));
			} else {
				// ????????? ??????????????? ????????? ??????.
				BuffController.remove(cha, Slow.class);
			}
		}
	}

	static public void init(Character cha, int time) {
		// ?????? ??????
		// BuffController.remove(cha, Wisdom.class);
		// ??????
		BuffController.append(cha,
				HolyWalk.clone(BuffController.getPool(HolyWalk.class), SkillDatabase.find(7, 3), time));
	}

}
