package lineage.bean.event;

import lineage.database.ServerDatabase;
import lineage.gui.GuiMain;
import lineage.network.Server;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.Mysql;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.AuctionController;
import lineage.world.controller.BossController;
import lineage.world.controller.BuffController;
import lineage.world.controller.BugScanningController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ColosseumController;
import lineage.world.controller.ElvenforestController;
import lineage.world.controller.InnController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.NoticeController;
import lineage.world.controller.QuestController;
import lineage.world.controller.RankController;
import lineage.world.controller.RobotController;
import lineage.world.controller.ShipController;
import lineage.world.controller.SlimeRaceController;
import lineage.world.controller.SummonController;


public class Timer implements Event {
	
	private long time;
	
	/**
	 * 풀링에서 객체를 꺼냈는데 해당 객체가 null일수 있음. 그래서 이곳에서 동적으로 생성.
	 * @param e
	 * @param c
	 * @return
	 */
	static public Event clone(Event e, long time){
		if(e == null)
			e = new Timer();
		((Timer)e).setTime(time);
		return e;
	}
	
	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void init() {
		// 매초마다 서버오브젝트아이디 갱신
		ServerDatabase.toSave();
		// 핑 체크.
		/*try { Server.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("핑 체크.");
			lineage.share.System.println(e);
		}*/
		// 공지사항 관리.
		try { NoticeController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("공지사항 관리.");
			lineage.share.System.println(e);
		}
		// 케릭터 관리.
		try { CharacterController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("케릭터 관리.");
			lineage.share.System.println(e);
		}
		// 월드 시간
		try { World.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("월드 시간");
			lineage.share.System.println(e);
		}
		// 선착장
		try { ShipController.toTimer(); } catch (Exception e) {
			lineage.share.System.println("선착장");
			lineage.share.System.println(e);
		}
		// 버프 관리
		BuffController.toTimer(time);
		// 서먼몬스터 관리
		try { SummonController.toTimer(); } catch (Exception e) {
			lineage.share.System.println("서먼몬스터 관리");
			lineage.share.System.println(e);
		}
		// 성 관리
		try { KingdomController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("성 관리");
			lineage.share.System.println(e);
		}
		// 여관 관리
		try { InnController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("여관 관리");
			lineage.share.System.println(e);
		}
		// 경매 관리
		try { if(Lineage.server_version >= 200){AuctionController.toTimer(time);} } catch (Exception e) {
			lineage.share.System.println("경매 관리");
			lineage.share.System.println(e);
		}
		// 아지트 관리
		try { if(Lineage.server_version >= 200){AgitController.toTimer(time);} } catch (Exception e) {
			lineage.share.System.println("아지트 관리");
			lineage.share.System.println(e);
		}
		// 퀘스트 관리
		try { QuestController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("퀘스트 관리");
			lineage.share.System.println(e);
		}
		// 콜로세움 관리
		try { if(Lineage.server_version >= 200){ColosseumController.toTimer(time);} } catch (Exception e) {
			lineage.share.System.println("콜로세움 관리");
			lineage.share.System.println(e);
		}
		// 요정숲 관리
		try { ElvenforestController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("요정숲 관리");
			lineage.share.System.println(e);
		}
		// 보스몬스터 관리.
		try { BossController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("보스몬스터 관리.");
			lineage.share.System.println(e);
		}
		// 슬라임레이스 관리. 
		try { SlimeRaceController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("슬라임레이스 관리.");
			lineage.share.System.println(e);
		}
		// mysql 관리.
		try { Mysql.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("mysql 관리.");
			lineage.share.System.println(e);
		}
		// log 관리.
		try { Log.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("log 관리.");
			lineage.share.System.println(e);
		}
		// bug 관리. 
		try { BugScanningController.toTimer(); } catch (Exception e) {
			lineage.share.System.println("bug 관리.");
			lineage.share.System.println(e);
		}
		// robot 관리.
		try { RobotController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("robot 관리.");
			lineage.share.System.println(e);
		}
		// 랭킹 관리.
		try { RankController.toTimer(time); } catch (Exception e) {
			lineage.share.System.println("랭킹 관리.");
			lineage.share.System.println(e);
		}
		// gui 관리.
		try {
			if(Common.system_config_console == false){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.toTimer(time);
					}
				});
			}
		} catch (Exception e) {
			lineage.share.System.println("gui 관리.");
			lineage.share.System.println(e);
		}
	}

	@Override
	public void close() {
		// 할거 없음..
	}
	
}
