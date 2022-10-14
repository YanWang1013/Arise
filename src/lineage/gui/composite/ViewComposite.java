package lineage.gui.composite;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabItem;

public class ViewComposite extends Composite {

	private TabFolder tabFolder;
	
	private ServerInfoComposite serverInfoComposite;
	private ScreenRenderComposite screenRenderComposite;
	private ChattingComposite chattingComposite;
	private SpChattingComposite	spChattingComposite;
	private int tabSelectIdx;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ViewComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("시스템");
		
		serverInfoComposite = new ServerInfoComposite(tabFolder, SWT.NONE);
		tabItem.setControl(serverInfoComposite);
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("리니지");
		
		screenRenderComposite = new ScreenRenderComposite(tabFolder, SWT.NONE);
		tabItem_1.setControl(screenRenderComposite);
		
		TabItem tabItem_2 = new TabItem(tabFolder, SWT.NONE);
		tabItem_2.setText("채팅");
		
		chattingComposite = new ChattingComposite(tabFolder, SWT.NONE);
		tabItem_2.setControl(chattingComposite);
		/*
		TabItem tbtmSp = new TabItem(tabFolder, SWT.NONE);
		tbtmSp.setText("SP 채팅");
		
		spChattingComposite = new SpChattingComposite(tabFolder, SWT.NONE);
		tbtmSp.setControl(spChattingComposite);
*/
	}
	
	public void toTimer(long time){
		tabSelectIdx = tabFolder.getSelectionIndex();
		
		// 서버정보 표현 갱신.
		if(tabSelectIdx == 0)
			serverInfoComposite.toUpdate();
		// 맵뷰어 랜더링 표현.
		if(tabSelectIdx == 1)
			screenRenderComposite.toUpdate();
	}
	
	public ScreenRenderComposite getScreenRenderComposite() {
		return screenRenderComposite;
	}
	
	public ChattingComposite getChattingComposite(){
		return chattingComposite;
	}
	
	public int getTabSelectIdx(){
		return tabSelectIdx;
	}
}
