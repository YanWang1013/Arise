package lineage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.database.BackgroundDatabase;
import lineage.database.BadIpDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.DefiniteDatabase;
import lineage.database.DungeonDatabase;
import lineage.database.ExpDatabase;
import lineage.database.GmCommandDatabase;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemMaplewandDatabase;
import lineage.database.ItemPinewandDatabase;
import lineage.database.ItemSetoptionDatabase;
import lineage.database.ItemSkillDatabase;
import lineage.database.ItemTeleportDatabase;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterDropDatabase;
import lineage.database.MonsterSkillDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcShopDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.NpcTeleportDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.ServerNoticeDatabase;
import lineage.database.ServerOpcodesDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.database.SummonListDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.database.TeleportResetDatabase;
import lineage.gui.GuiMain;
import lineage.network.Server;
import lineage.network.WebServer;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.plugin.Plugin;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.Mysql;
import lineage.share.Socket;
import lineage.share.Web;
import lineage.thread.AiThread;
import lineage.thread.EventThread;
import lineage.thread.GuiThread;
import lineage.thread.TimerThread;
import lineage.util.Shutdown;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.AuctionController;
import lineage.world.controller.BoardController;
import lineage.world.controller.BookController;
import lineage.world.controller.BossController;
import lineage.world.controller.BuffController;
import lineage.world.controller.BugScanningController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.ColosseumController;
import lineage.world.controller.CraftController;
import lineage.world.controller.DamageController;
import lineage.world.controller.DungeonController;
import lineage.world.controller.ElvenforestController;
import lineage.world.controller.EventController;
import lineage.world.controller.FriendController;
import lineage.world.controller.InnController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.LetterController;
import lineage.world.controller.NoticeController;
import lineage.world.controller.PartyController;
import lineage.world.controller.QuestController;
import lineage.world.controller.RankController;
import lineage.world.controller.RobotController;
import lineage.world.controller.ShipController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SlimeRaceController;
import lineage.world.controller.SummonController;
import lineage.world.controller.TradeController;
import lineage.world.controller.UserShopController;
import lineage.world.object.instance.PcInstance;

public final class Main implements Runnable {
	
	static public boolean running;
	
	/**
	 * 리니지 서버 시작
	 */
	static public void init(){
		if(running == true)
			return;
		
		if(Common.system_config_console == false)
			// gui모드 넣으면서 유연한 처리를위해 쓰레드를 따로빼서 처리함.
			// 이렇게 안하면 단일쓰레드에서 처리하다보니 이 과정이 끝나후 gui처리가 이뤄져서 순간 렉이 발생.
			new Thread(new Main()).start();
		else
			toLoading();
	}
	
	/**
	 * 리니지 서버 종료
	 */
	static public void close(){
		if(running == false)
			return;
		
		if(Common.system_config_console == false)
			// gui모드 넣으면서 유연한 처리를위해 쓰레드를 따로빼서 처리함.
			// 이렇게 안하면 단일쓰레드에서 처리하다보니 이 과정이 끝나여 gui처리가 이뤄져서 순간 렉이 발생.
			new Thread(new Main()).start();
		else
			toDelete();
	}
	
	@Override
	public void run(){
		if(running){
			toDelete();
		}else{
			toLoading();
		}
	}
	
	/**
	 * 서버 로딩 처리.
	 */
	static private void toLoading() {
		
		try {
			running = true;
			
			if(Common.system_config_console)
				Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

			Log.init();
			GuiThread.init();
			Common.init();
			Mysql.init();
			Lineage.init();
			Socket.init();
			Web.init();
			EventThread.init();
			TimerThread.init();
			AiThread.init();
			World.init();
			BookController.init();
			InventoryController.init();
			BasePacketPooling.init();
			SkillController.init();
			DamageController.init();
			CharacterController.init();
			TradeController.init();
			DungeonController.init();
			ShipController.init();
			BuffController.init();
			SummonController.init();
			PartyController.init();
			InnController.init();
			BoardController.init();
			LetterController.init();
			UserShopController.init();
			ChattingController.init();
			ColosseumController.init();
			CraftController.init();
			BossController.init();
			NoticeController.init();
			FriendController.init();
			BugScanningController.init();
			
			// dbcp 활성화.
			DatabaseConnection.init();
			// 디비로부터 메모리 초기화.
			Connection con = DatabaseConnection.getLineage();
			ServerDatabase.init(con);
			SlimeRaceController.init(con);
			ClanController.init(con);
			KingdomController.init(con);
			AgitController.init(con);
			AuctionController.init(con);
			ItemDatabase.init(con);
			ItemSetoptionDatabase.init(con);
			ItemSkillDatabase.init(con);
			ItemBundleDatabase.init(con);
			ItemTeleportDatabase.init(con);
			PolyDatabase.init(con);
			DungeonDatabase.init(con);
			DefiniteDatabase.init(con);
			ExpDatabase.init(con);
			BackgroundDatabase.init(con);
			SpriteFrameDatabase.init(con);
			SkillDatabase.init(con);
			NpcDatabase.init(con);
			NpcShopDatabase.init(con);
			NpcTeleportDatabase.init(con);
			NpcSpawnlistDatabase.init(con);
			EventController.init();
			MonsterDatabase.init(con);
			MonsterDropDatabase.init(con);
			MonsterSkillDatabase.init(con);
			MonsterSpawnlistDatabase.init(con);
			MonsterBossSpawnlistDatabase.init(con);
			BadIpDatabase.init(con);
			QuestController.init(con);
			ServerOpcodesDatabase.init(con);
			ServerNoticeDatabase.init(con);
			RankController.init(con);
			TeleportHomeDatabase.init(con);
			TeleportResetDatabase.init(con);
			ItemPinewandDatabase.init(con);
			ItemMaplewandDatabase.init(con);
			SummonListDatabase.init(con);
			GmCommandDatabase.init(con);
			DatabaseConnection.close(con);
			
			// 성 스폰처리
			KingdomController.readKingdom();
			// 요정숲 관리 초기화. 디비값을 참고하기때문에 디비로딩후 처리해야함.
			ElvenforestController.init();
			// 로봇 처리. etc_objectid 때문에 여기에서 처리.
			RobotController.init();
			
			// 마지막으로 처리된 기타오브젝트값 기록.
			ServerDatabase.updateEtcObjId();
			
			// 소켓 활성화.
			Server.init();
			// 웹서버 활성화.
			WebServer.init();
			// psjump 서버 접속
		//	PsjumpThread.init();
			
			// 필요한 쓰레드 활성화.
			EventThread.start();
			TimerThread.start();
			AiThread.start();
			GuiThread.start();
			
			// 서버 기본정보 표현.
			lineage.share.System.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
			// Version 200
			lineage.share.System.printf("| Version %d\r\n", Lineage.server_version);
			// Port 2000
			lineage.share.System.printf("| Port %d\r\n", Socket.PORT);
			// AutoAccount true
			lineage.share.System.printf("| AutoAccount %s\r\n", Lineage.account_auto_create ? "true" : "false");
			// Rate enchant: drop: exp: aden: party:
			lineage.share.System.printf("| Rate enchant:%f drop:%f exp:%f aden:%f party:%f\r\n", Lineage.rate_enchant, Lineage.rate_drop, Lineage.rate_exp, Lineage.rate_aden, Lineage.rate_party);
			// Web true
			lineage.share.System.println( String.format("| Web %s", Web.is ? "true" : "false") );
			if(Web.is)
				// BoardModule gnu
				lineage.share.System.println( String.format("| BoardModule %s", Web.board_module) );
			lineage.share.System.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
			
			lineage.share.System.printf( "'%s' 가동되었습니다.\r\n", ServerDatabase.getName() );
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : toLoading()\r\n", Main.class.toString());
			lineage.share.System.println(e);
		}
		
		// 서버 시작 알리기.
		Plugin p = PluginController.find(Main.class);
		if(p != null)
			((lineage.plugin.bean.Main)p).toStart();
		
	}
	
	/**
	 * 서버 종료처리 함수.
	 */
	static private void toDelete(){
		try {
			running = false;
			
			// 종료 알리기.
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "서버가 종료 됩니다.."));
			Thread.sleep(1000);
			// 쓰레드 종료
			lineage.share.System.println("Thread close..");
			EventThread.close();
			TimerThread.close();
			AiThread.close();
			Thread.sleep(1000);
			// 메모리 저장
			lineage.share.System.println("Save Database..");
			Connection con = DatabaseConnection.getLineage();
			ServerDatabase.close(con);
			ClanController.close(con);
			KingdomController.close(con);
			AuctionController.close(con);
			AgitController.close(con);
			QuestController.close(con);
			TradeController.close();
			
			List<PcInstance> pc_list = new ArrayList<PcInstance>();
			pc_list.addAll(World.getPcList());
			for(PcInstance pc : pc_list){
				if(pc.isWorldDelete())
					continue;
				// true 로 해놔야 나중에 클라 메모리 제거할때 사용자가 월드에 없다고 판단해서 저장 처리를 반복 하지 않음.
				try { pc.setWorldDelete(true); } catch (Exception e) {}
					// 죽어있을경우에 처리를 위해.
				try { pc.toReset(true); } catch (Exception e) {}
					// 저장
				try { pc.toSave(con); } catch (Exception e) {}
			}
			
			// 메모리 제거.
			Thread.sleep(500);
			// 소켓 닫기
			lineage.share.System.println("Socket close..");
			Server.close(con);
			lineage.share.System.println("ok..");
			// 로그 저장.
			Log.close();
			// 디비 컨넥션 닫기.
			DatabaseConnection.close(con);
			DatabaseConnection.close();
			Thread.sleep(500);
		} catch (Exception e) {
			System.out.println(Main.class+" : toDelete()");
			System.out.println(e);
		}
		
		// 서버 종료 알리기.
		Plugin p = PluginController.find(Main.class);
		if(p != null)
			((lineage.plugin.bean.Main)p).toEnd();
		
		// 콘솔모드에서 Runtime.getRuntime().addShutdownHook(Shutdown.getInstance()) 때문에 이 구간을 이행하면 재차 해당 함수가 호출됨.
		if(!Common.system_config_console)
			Runtime.getRuntime().exit(0);
	}

	public static void main(String[] args) {
		Common.system_config_console = args.length==0;
//		Common.system_config_console = false;
		if(Common.system_config_console)
			init();
		else
			GuiMain.open();
		
		/*String ip = "192.168.120.2";
		StringTokenizer st = new StringTokenizer(ip, ".");
		long a = Integer.valueOf(st.nextToken());
		long b = Integer.valueOf(st.nextToken());
		long c = Integer.valueOf(st.nextToken());
		long d = Integer.valueOf(st.nextToken());
		long ip_number;
		
		// 1
		long number = d &0xff;
		number |= c << 8 &0xff00;
		number |= b << 16 &0xff0000;
		number |= a << 24 &0xff000000;
		
		// 2
		a = a * 16777216;
		b = b * 65536;
		c = c * 256;
		d = d * 1;
		ip_number = a + b + c + d;
		
		System.out.println(ip_number);
		System.out.println(number);*/


	}

}
