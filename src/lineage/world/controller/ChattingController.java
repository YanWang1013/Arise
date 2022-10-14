package lineage.world.controller;

import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Party;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public final class ChattingController {

	static private boolean global;

	static public void init(){
		TimeLine.start("ChattingController..");

		global = true;

		TimeLine.end();
	}

	static public void toWorldJoin(PcInstance pc){

	}

	static public void toWorldOut(PcInstance pc){

	}

	static public void setGlobal(boolean g){
		global = g;
	}

	static public boolean isGlobal(){
		return global;
	}

	/**
	 * 채팅 처리 함수.
	 * @param o
	 * @param msg
	 * @param mod
	 */
	static public void toChatting(object o, String msg, int mode){
		if(o!=null && o.isBuffChattingClose() && mode!=Lineage.CHATTING_MODE_MESSAGE){
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}
		// 사일런스 상태는 무시.
		if(o!=null && o.isBuffSilence())
			return;

		// 채팅 처리
		switch(mode){
		case Lineage.CHATTING_MODE_NORMAL:
			toNormal(o, msg);
			break;
		case Lineage.CHATTING_MODE_SHOUT:
			toShout(o, msg);
			break;
		case Lineage.CHATTING_MODE_GLOBAL:
			toGlobal(o, msg);
			break;
		case Lineage.CHATTING_MODE_CLAN:
			toClan(o, msg);
			break;
		case Lineage.CHATTING_MODE_PARTY:
			toParty(o, msg);
			break;
		case Lineage.CHATTING_MODE_TRADE:
			toTrade(o, msg);
			break;
		case Lineage.CHATTING_MODE_MESSAGE:
			toMessage(o, msg);
			break;
		}

		// 로그 기록 (시스템메세지는 기록 안함.)
		if(mode!=Lineage.CHATTING_MODE_MESSAGE && (o==null || o instanceof PcInstance))
			Log.appendChatting(o==null ? null : (PcInstance)o, msg, mode);
	}

	/**
	 * 귓소말 처리 함수.
	 * @param o
	 * @param name
	 * @param msg
	 */
	static public void toWhisper(final object o, final String name, final String msg){
		if(o!=null && o.isBuffChattingClose()){
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}
		// 사일런스 상태는 무시.
		if(o!=null && o.isBuffSilence())
			return;

		if(Lineage.server_version<=144 || o==null || o.getLevel()>=Lineage.chatting_level_whisper){
			boolean gui_admin = name.equalsIgnoreCase("******");
			PcInstance user = World.findPc(name);
			if(user!=null || gui_admin){
				if(gui_admin || o==null || (user.isChattingWhisper() && !user.getListBlockName().contains(o.getName()))){
					if(o != null)
						o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), user, 0x09, msg));
					if(user != null)
						user.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, 0x08, msg));
					// gui 처리.
					if(Common.system_config_console == false){
						GuiMain.display.asyncExec(new Runnable(){
							@Override
							public void run(){
								GuiMain.getViewComposite().getChattingComposite().toWhisper(o, name, msg);
							}
						});
					}
				}else{
					if(o != null)
						// \f1%0%d 현재 귓말을 듣고 있지 않습니다.
						o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 205, name));
				}
			}else{
				if(o != null){
					// \f1%0%d 게임을 하고 있지 않습니다.
					o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 73, name));
				}else{
					// gui 처리.
					if(Common.system_config_console == false){
						GuiMain.display.asyncExec(new Runnable(){
							@Override
							public void run(){
								GuiMain.getViewComposite().getChattingComposite().toMessage( String.format("%s 게임을 하고 있지 않습니다.", name) );
							}
						});
					}
				}
			}
		}else{
			if(o != null)
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 404, String.valueOf(Lineage.chatting_level_whisper)));
		}
	}

	/**
	 * 일반 채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toNormal(final object o, final String msg){
		// 명령어 확인 처리.
		if(!CommandController.toCommand(o, msg)){
			if(o instanceof PcInstance){
				// 나에게 표현.
				o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
				// 주변 객체에게 알리기. npc, monster만.
				for(object oo : o.getInsideList()){
					if(oo instanceof NpcInstance || oo instanceof MonsterInstance)
						oo.toChatting(o, msg);
				}
			}
			// 주변사용자에게 표현.
			for(object oo : o.getInsideList()){
				if(oo instanceof PcInstance){
					PcInstance use = (PcInstance)oo;
					// 블럭 안된 이름만 표현하기.
					if(use.getListBlockName().contains(o.getName()) == false)
						use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
				}
			}
			// gui 처리.
			if(Common.system_config_console==false && o instanceof PcInstance){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toNormal(o, msg);
					}
				});
			}
		}
	}

	/**
	 * 외치기 처리.
	 * @param o
	 * @param msg
	 */
	static private void toShout(object o, String msg){
		// 나에게 표현.
		if(o instanceof PcInstance)
			o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_SHOUT, msg));
		// 주변사용자에게 표현.
		for(object oo : o.getAllList()){
			if(oo instanceof PcInstance){
				PcInstance use = (PcInstance)oo;
				// 블럭 안된 이름만 표현하기.
				if(o instanceof QuestInstance || !use.getListBlockName().contains(o.getName()))
					use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_SHOUT, msg));
			}
		}
	}

	/**
	 * 전체채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toGlobal(final object o, final String msg){
		// 처리해도되는지 확인.
		if(!global && o instanceof PcInstance)
			return;

		if(o==null || o.getGm()>=Lineage.GMCODE || Lineage.chatting_level_global <= o.getLevel()){
			for(PcInstance use : World.getPcList()){
				if(o==null || use.isChattingGlobal() && !use.getListBlockName().contains(o.getName()))
					use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_GLOBAL, msg));
			}
			// gui 처리.
			if(Common.system_config_console == false){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toGlobal(o, msg);
					}
				});
			}
		}else{
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 195, String.valueOf(Lineage.chatting_level_global)));
		}
	}

	/**
	 * 혈맹채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toClan(final object o, final String msg){
		Clan c = ClanController.find(o.getClanId());
		if(c!=null){
			c.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_CLAN, msg));
			// gui 처리.
			if(Common.system_config_console == false){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toClan(o, msg);
					}
				});
			}
		}
	}

	/**
	 * 파티채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toParty(final object o, final String msg){
		if(o instanceof PcInstance){
			PcInstance pc = (PcInstance)o;
			Party p = PartyController.find(pc);
			if(p != null){
				p.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_PARTY, msg));
				// gui 처리.
				if(Common.system_config_console == false){
					GuiMain.display.asyncExec(new Runnable(){
						@Override
						public void run(){
							GuiMain.getViewComposite().getChattingComposite().toParty(o, msg);
						}
					});
				}
			}
		}
	}

	/**
	 * 장사채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toTrade(final object o, final String msg){
		// 처리해도되는지 확인.
		if(!global && o instanceof PcInstance)
			return;

		if(o.getGm()>=Lineage.GMCODE || Lineage.chatting_level_global <= o.getLevel()){
			for(PcInstance use : World.getPcList()){
				if(use.isChattingTrade() && !use.getListBlockName().contains(o.getName()))
					use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_TRADE, msg));
			}
			// gui 처리.
			if(Common.system_config_console == false){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toTrade(o, msg);
					}
				});
			}
		}else{
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 195, String.valueOf(Lineage.chatting_level_global)));
		}
	}

	/**
	 * 일반 메세지 표현용.
	 * @param o
	 * @param msg
	 */
	static private void toMessage(object o, final String msg){
		if(o == null){
			if(Common.system_config_console == false){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toMessage(msg);
					}
				});
			}
		}else{
			o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_MESSAGE, msg));
		}
	}

	/**
	 * 월드 메세지 표현용.
	 * @param o
	 * @param msg
	 */
	static public void toWorldMessage(final String msg){
		for(PcInstance use : World.getPcList()){
			use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), use, Lineage.CHATTING_MODE_MESSAGE, msg));
		}
	}
}
