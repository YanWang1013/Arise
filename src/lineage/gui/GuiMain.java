package lineage.gui;

import lineage.Main;
import lineage.gui.composite.ConsoleComposite;
import lineage.gui.composite.ViewComposite;
import lineage.share.Lineage;
import lineage.util.Shutdown;
import lineage.world.controller.CommandController;
import lineage.world.controller.EventController;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.swtdesigner.SWTResourceManager;

public final class GuiMain {

	// gui 컴포넌트들.
	static public Display display;
	static public Shell shell;
	static private ViewComposite viewComposite;
	static private ConsoleComposite consoleComposite;
	static private MenuItem menu_system_1_item_1;		// 서버가동
	static private MenuItem menu_system_1_item_2;		// 서버종료
	static private MenuItem menuItem;					// 이벤트
	static private MenuItem menuItem_1;					// 변신 이벤트
	static private MenuItem menuItem_2;					// 명령어
	static private MenuItem menuItem_5;					// 자동버프 이벤트
	static private MenuItem menuItem_7;					// 환상 이벤트
	static private MenuItem menuItem_8;					// 크리스마스 이벤트
	static private MenuItem menuItem_9;					// 할로윈 이벤트
	static private MenuItem menuItem_10;				// 토템 이벤트
	
	// 서버팩 버전
	static public final String SERVER_VERSION = "Ver.163";
	
	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	static public void open() {
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(1600, 1200);
		shell.setText( String.format("Lineage %s", SERVER_VERSION) );
		shell.setImage( SWTResourceManager.getImage("images/icon.ico") );
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.horizontalSpacing = 0;
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem menu_system = new MenuItem(menu, SWT.CASCADE);
		menu_system.setText("시스템");
		
		Menu menu_system_1 = new Menu(menu_system);
		menu_system.setMenu(menu_system_1);
		
		menu_system_1_item_1 = new MenuItem(menu_system_1, SWT.NONE);
		menu_system_1_item_1.setText("서버 가동");
		
		menu_system_1_item_2 = new MenuItem(menu_system_1, SWT.NONE);
		menu_system_1_item_2.setText("서버 종료");
		menu_system_1_item_2.setEnabled(false);
		
		new MenuItem(menu_system_1, SWT.SEPARATOR);
		
		MenuItem menuItem_6 = new MenuItem(menu_system_1, SWT.NONE);
		menuItem_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Tray tray = display.getSystemTray();
				if(tray != null){
					// 현재 윈도우 감추기.
					shell.setVisible(false);
					// 트레이 활성화.
					final TrayItem item = new TrayItem(tray, SWT.NONE);
					item.setToolTipText( String.format("%s : %d", SERVER_VERSION, Lineage.server_version) );
					item.setImage( SWTResourceManager.getImage("images/icon.ico") );
					// 이벤트 등록.
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							item.dispose();
							shell.setVisible(true);
							shell.setFocus();
						}
					});
				}
			}
		});
		menuItem_6.setText("트레이 모드");
		
		MenuItem menu_lineage = new MenuItem(menu, SWT.CASCADE);
		menu_lineage.setText("리니지");
		
		Menu menu_2 = new Menu(menu_lineage);
		menu_lineage.setMenu(menu_2);
		
		menuItem = new MenuItem(menu_2, SWT.CASCADE);
		menuItem.setEnabled(false);
		menuItem.setText("이벤트");
		
		Menu menu_1 = new Menu(menuItem);
		menuItem.setMenu(menu_1);
		
		menuItem_1 = new MenuItem(menu_1, SWT.CHECK);
		menuItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toPoly( menuItem_1.getSelection() );
			}
		});
		menuItem_1.setText("변신 이벤트");
		
		menuItem_5 = new MenuItem(menu_1, SWT.CHECK);
		menuItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toBuff( menuItem_5.getSelection() );
			}
		});
		menuItem_5.setText("자동버프 이벤트");
		
		menuItem_7 = new MenuItem(menu_1, SWT.CHECK);
		menuItem_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toIllusion( menuItem_7.getSelection() );
			}
		});
		menuItem_7.setText("환상 이벤트");
		
		menuItem_8 = new MenuItem(menu_1, SWT.CHECK);
		menuItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toChristmas( menuItem_8.getSelection() );
			}
		});
		menuItem_8.setText("크리스마스 이벤트");
		
		menuItem_9 = new MenuItem(menu_1, SWT.CHECK);
		menuItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toHalloween( menuItem_9.getSelection() );
			}
		});
		menuItem_9.setText("할로윈 이벤트");
		
		menuItem_10 = new MenuItem(menu_1, SWT.CHECK);
		menuItem_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toTotem( menuItem_10.getSelection() );
			}
		});
		menuItem_10.setText("토템 이벤트");
		
		menuItem_2 = new MenuItem(menu_2, SWT.CASCADE);
		menuItem_2.setEnabled(false);
		menuItem_2.setText("명령어");
		
		Menu menu_4 = new Menu(menuItem_2);
		menuItem_2.setMenu(menu_4);
		
		MenuItem menuItem_3 = new MenuItem(menu_4, SWT.NONE);
		menuItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toBuffAll(null);
			}
		});
		menuItem_3.setText("올버프");
		
		MenuItem menuItem_4 = new MenuItem(menu_4, SWT.NONE);
		menuItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toWorldItemClear(null);
			}
		});
		menuItem_4.setText("청소");
		
		MenuItem menu_help = new MenuItem(menu, SWT.CASCADE);
		menu_help.setText("도움말");
		
		Menu menu_3 = new Menu(menu_help);
		menu_help.setMenu(menu_3);
		
		MenuItem mntmHttpshuromcom = new MenuItem(menu_3, SWT.NONE);
		mntmHttpshuromcom.setText("http://홈페이지 주소 적으세요.");
		
		viewComposite = new ViewComposite(shell, SWT.NONE);
		viewComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		consoleComposite = new ConsoleComposite(shell, SWT.NONE);
		GridData gd_consoleComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_consoleComposite.heightHint = 125;
		consoleComposite.setLayoutData(gd_consoleComposite);
		
		// 이벤트 등록.
		menu_system_1_item_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 서버 정보 로드.
				Main.init();
				// 맵뷰어 랜더링 시작.
				viewComposite.getScreenRenderComposite().start();
				// 정보 변경.
				menu_system_1_item_1.setEnabled(false);
				menu_system_1_item_2.setEnabled(true);
			}
		});
		menu_system_1_item_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread(Shutdown.getInstance()).start();
			}
		});

		// 매니저를 윈도우화면 가운데 좌표로 변경.
		shell.setBounds((display.getBounds().width/2)-(shell.getBounds().width/2), (display.getBounds().height/2)-(shell.getBounds().height/2), shell.getBounds().width, shell.getBounds().height);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Exception e) { }
		}
		
		Main.close();
	}
	
	static public ViewComposite getViewComposite() {
		return viewComposite;
	}
	
	static public ConsoleComposite getConsoleComposite() {
		return consoleComposite;
	}
	
	static public void toTimer(long time){
		// 뷰어 처리.
		viewComposite.toTimer(time);
		// 초기화 안된 상태.
		if(!menuItem.isEnabled()){
			// 메뉴 활성화.
			menuItem.setEnabled(true);
			menuItem_2.setEnabled(true);
			// Lineage 설정 정보 갱신
			menuItem_1.setSelection( Lineage.event_poly );
			menuItem_5.setSelection( Lineage.event_buff );
			menuItem_7.setSelection( Lineage.event_illusion );
			menuItem_8.setSelection( Lineage.event_christmas );
			menuItem_9.setSelection( Lineage.event_halloween );
			menuItem_10.setSelection( Lineage.event_lyra );
		}
	}
	
	/**
	 * 경고창 띄울때 사용.
	 * @param msg
	 */
	static public void toMessageBox(final String msg){
		toMessageBox(SERVER_VERSION, msg);
	}
	static public void toMessageBox(final String title, final String msg){
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
		messageBox.setText( String.format("경고 :: %s", title) );
		messageBox.setMessage(msg);
		messageBox.open();
	}
}
