package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.monster.CurseGhoul;

public class Cancellation {

	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true)){
				// 투망상태 해제
				Detection.onBuff(cha);
				// 공격당한거 알리기.
				o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);

				if(SkillController.isFigure(cha, o, skill, true, false) || SkillController.isClan(cha, o) || Lineage.server_version<=144)
					onBuff(o, skill);
				else
					// \f1마법이 실패했습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
				
			}
		}
	}
	
	static public void onBuff(object o, Skill skill){
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		
		// 라이트
		BuffController.remove(o, Light.class);
		// 쉴드
		BuffController.remove(o, Shield.class);
		// 홀리 웨폰
		BuffController.remove(o, HolyWeapon.class);
		// 커스:포이즌
		BuffController.remove(o, CursePoison.class);
		// 디크리즈 웨이트
		BuffController.remove(o, DecreaseWeight.class);
		// 커스: 블라인드
		// 다크니스
		BuffController.remove(o, CurseBlind.class);
		// 인첸트 덱스터리
		BuffController.remove(o, EnchantDexterity.class);
		// 슬로우
		BuffController.remove(o, Slow.class);
		// 카운터 매직
		BuffController.remove(o, CounterMagic.class);
		// 메디테이션
		BuffController.remove(o, Meditation.class);
		// 커스: 패럴라이즈
		BuffController.remove(o, CurseParalyze.class);
		// 인첸트 마이티
		BuffController.remove(o, EnchantMighty.class);
		// 헤이스트
		if(o.getInventory()==null || !o.getInventory().isSetOptionHaste())
			BuffController.remove(o, Haste.class);
		// 아이스 랜스
		BuffController.remove(o, IceLance.class);
		// 디지즈
		BuffController.remove(o, Disease.class);
		// 사일런스
//		BuffController.remove(o, 64, false);
		// 포그 오브 슬리프
//		BuffController.remove(o, 66, false);
		// 세이프 체인지
		BuffController.remove(o, ShapeChange.class);
		// 이뮨 투 암
		BuffController.remove(o, ImmuneToHarm.class);
		// 디케이 포션
		BuffController.remove(o, DecayPotion.class);
		// 앱솔루트 배리어
		BuffController.remove(o, AbsoluteBarrier.class);

		// 레지스트매직
		BuffController.remove(o, ResistMagic.class);
		// 레지스트엘리멘탈
		BuffController.remove(o, ResistElemental.class);
		// 프로텍션프롬엘리멘탈
//				BuffController.remove(o, 92, false);
		// 파이어웨폰
		BuffController.remove(o, FireWeapon.class);
		// 윈드샷
		BuffController.remove(o, WindShot.class);
		// 윈드 워크
		BuffController.remove(o, WindWalk.class);
		// 브레스 오브 파이어
		BuffController.remove(o, BlessOfFire.class);
		// 아이오브스톰
		BuffController.remove(o, EyeOfStorm.class);
		// 어스바인드
		BuffController.remove(o, EarthBind.class);
		// 에어리어오브사일런스
//		BuffController.remove(o, 105, false);
		// 버닝웨폰
		BuffController.remove(o, BurningWeapon.class);
		// 스톰샷
		BuffController.remove(o, StormShot.class);
		// 아이언스킨
		BuffController.remove(o, IronSkin.class);

		// 글로잉오라
		BuffController.remove(o, GlowingAura.class);
		// 샤이닝오라
		BuffController.remove(o, ShiningAura.class);

		// 용기
		if(o.getInventory()==null || !o.getInventory().isSetOptionBrave())
			BuffController.remove(o, Bravery.class);
		// 아쿠아물약 (에바물약)
		BuffController.remove(o, Eva.class);
		// 지혜물약
		BuffController.remove(o, Wisdom.class);
		// 마력회복물약
		BuffController.remove(o, Blue.class);
		// 와퍼
		if(o.getInventory()==null || !o.getInventory().isSetOptionWafer())
			BuffController.remove(o, Wafer.class);
		// 구울 독
		BuffController.remove(o, CurseGhoul.class);
	}
	
}
