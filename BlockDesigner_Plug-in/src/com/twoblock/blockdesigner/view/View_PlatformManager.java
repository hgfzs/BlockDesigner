package com.twoblock.blockdesigner.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.twoblock.blockdesigner.command.Handler_Command;
import com.twoblock.blockdesigner.command.Handler_SimulationInitThread;
import com.twoblock.blockdesigner.command.Hanlder_CallBack;
import com.twoblock.blockdesigner.datastore.Module;
import com.twoblock.blockdesigner.datastore.ModuleInfo;
import com.twoblock.blockdesigner.datastore.Parameter;
import com.twoblock.blockdesigner.datastore.Port;
import com.twoblock.blockdesigner.datastore.Set_BDPMD;
import com.twoblock.blockdesigner.popup.BDF;
import com.twoblock.blockdesigner.popup.BDMemoryMapEditor;
import com.twoblock.blockdesigner.popup.BDMemoryMapItem;
import com.twoblock.blockdesigner.popup.IBDMemoryMapEditorListener;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class View_PlatformManager extends ViewPart {
	protected Composite shell;
	public static List list_All;
	public static List list_cpu;
	public static List list_bus;
	public static List list_mem;
	public static List list_other;
	private TabFolder tab_ModuleList;
	private ExpandBar expandBar;
	private int port_index;
	private int par_index;

	private Table table_port;
	private Table table_par;
	private ArrayList<Button> btnCheckButton = new ArrayList<Button>();
	private ArrayList<Composite> composite_ExpandItem = new ArrayList<Composite>();
	private Device device = Display.getCurrent();
	private Color color_gray = new Color(device, 150, 150, 150);
	private Color color_skyblue = new Color(device, 204, 204, 255);
	private ArrayList<ExpandItem> Expanditem = new ArrayList<ExpandItem>();

	private JSONParser parser = new JSONParser();
	private Object obj;
	private JSONObject jsonObject;
	private JSONArray ModuleInfoList;
	private int list_Cnt = 0;
	private static ArrayList<String> UsedModuleList = new ArrayList<String>();
	private String PastItem;
	private CCombo PastLinkedModule;
	private int PastLinkedModuleIndex = 0;
	private static ModuleInfo ModuleInfoData;
	private static ModuleInfo UsedModuleDataList = new ModuleInfo();
	private static Module ModuleData;
	private int Selected_Index;
	private int UsedModuleIndex;
	private String Instance_Module_Name;
	protected boolean mouseClickState;
	private ScrolledComposite composite_PlatformViewer;
	public static Display display = null;

	public void createPartControl(Composite parent) {
		parent.getParent().addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// TODO Auto-generated method stub
				Handler_SimulationInitThread.SetInitCheck(false);
				Hanlder_CallBack.CallBack_RM();
			}
		});

		display = Display.getDefault();
		Hanlder_CallBack.CallBack_Func();
		shell = parent;
		shell.setLayout(new GridLayout(3, false));

		Composite composite_Toolbar = new Composite(shell, SWT.NONE);
		composite_Toolbar.setLayout(new FillLayout(SWT.HORIZONTAL));

		// ImageDescriptor idNew =
		// ImageDescriptor.createFromFile(this.getClass(),
		// "/images/new_btn.png");
		// Image imgNew = idNew.createImage();
		// ImageDescriptor idOpen =
		// ImageDescriptor.createFromFile(this.getClass(),
		// "/images/open_btn.png");
		// Image imgOpen = idOpen.createImage();
		ImageDescriptor idSave = ImageDescriptor.createFromFile(this.getClass(), "/images/save_btn.png");
		Image imgSave = idSave.createImage();
		ImageDescriptor idClose = ImageDescriptor.createFromFile(this.getClass(), "/images/close_btn_16.png");
		Image imgClose = idClose.createImage();

		// Button btn_new = new Button(composite_Toolbar, SWT.NONE);
		// btn_new.setImage(imgNew);
		// Button btn_open = new Button(composite_Toolbar, SWT.NONE);
		// btn_open.setImage(imgOpen);

		Button btn_save = new Button(composite_Toolbar, SWT.NONE);
		btn_save.setImage(imgSave);
		btn_save.addSelectionListener(new SelectionListener() {
			BufferedWriter out;

			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

				FileDialog fdlg = new FileDialog(shell.getShell(), SWT.SAVE);
				fdlg.setFilterNames(new String[] { "BDPMD", "ALL Files(*.*)" });
				fdlg.setFilterExtensions(new String[] { "*.BDPMD" });
				fdlg.setFilterPath(System.getProperty("user.home"));
				fdlg.setText("Save BDPMD");
				String saveDir = fdlg.open();
				try {
					Set_BDPMD setBD = new Set_BDPMD(UsedModuleDataList);
					String str = setBD.Get_BDPMD();
					out = new BufferedWriter(new FileWriter(saveDir));
					out.write(str);
					out.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		Button btn_close = new Button(composite_Toolbar, SWT.NONE);
		btn_close.setImage(imgClose);
		btn_close.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				Handler_SimulationInitThread.SetInitCheck(false);
				list_All.removeAll();
				list_cpu.removeAll();
				list_bus.removeAll();
				list_mem.removeAll();
				list_other.removeAll();
				UsedModuleList.clear();
				UsedModuleDataList = new ModuleInfo();
				for(int removeIndex=0; removeIndex <ModuleInfoData.mList.size() ;removeIndex++){
					ModuleInfoData.mList.get(removeIndex).Port_List.clear();
				}
				
				ModuleInfoData.mList.clear();
				
				for(int removeIndex=0; removeIndex <Expanditem.size() ;removeIndex++){
					Expanditem.get(removeIndex).dispose();
				}
				Handler_Command.Command_Func(0, 1, "CLOSE", "NULL", "NULL", "NULL", "NULL");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		PastLinkedModule = new CCombo(composite_Toolbar, SWT.NONE);
		PastLinkedModule.setVisible(false);

		new Label(shell, SWT.NONE);

		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));

		composite_PlatformViewer = new ScrolledComposite(shell, SWT.V_SCROLL);
		composite_PlatformViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_PlatformViewer.setLayout(new FillLayout(SWT.VERTICAL));

		expandBar = new ExpandBar(composite_PlatformViewer, SWT.NONE);

		{
			Composite composite_ModuleSetter = new Composite(shell, SWT.NONE);
			composite_ModuleSetter.setLayout(new GridLayout(1, false));
			GridData gd_composite_ModuleSetter = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
			gd_composite_ModuleSetter.widthHint = 25;
			composite_ModuleSetter.setLayoutData(gd_composite_ModuleSetter);

			Label lbl_Blank_top = new Label(composite_ModuleSetter, SWT.NONE);
			lbl_Blank_top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

			Button btn_Module_Add = new Button(composite_ModuleSetter, SWT.FLAT | SWT.CENTER);
			btn_Module_Add.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			btn_Module_Add.setText("<");

			btn_Module_Add.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					InstanceName_Dialog();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			new Label(composite_ModuleSetter, SWT.NONE);
			new Label(composite_ModuleSetter, SWT.NONE);

			Button btn_Module_Delete = new Button(composite_ModuleSetter, SWT.FLAT | SWT.CENTER);
			btn_Module_Delete.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			btn_Module_Delete.setText(">");

			Label lbl_Blank_bottom = new Label(composite_ModuleSetter, SWT.NONE);
			lbl_Blank_bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

			btn_Module_Delete.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					DeleteModuleSelected();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub

				}
			});

		}

		Composite composite_ModuleList = new Composite(shell, SWT.NONE);
		composite_ModuleList.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		composite_ModuleList.setLayout(new GridLayout(1, false));

		tab_ModuleList = new TabFolder(composite_ModuleList, SWT.NONE);
		tab_ModuleList.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));

		{
			TabItem tb_All = new TabItem(tab_ModuleList, SWT.NONE);
			tb_All.setText("All");
			list_All = new List(tab_ModuleList, SWT.NONE);
			tb_All.setControl(list_All);
			list_All.addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub
					if ((arg0.keyCode == SWT.ARROW_LEFT) | (arg0.keyCode == SWT.CR))
						InstanceName_Dialog();
				}

				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub

				}
			});

			list_All.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleClicked(list_All);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleDoubleClicked(list_All);
				}
			});
		}
		{
			TabItem tb_cpu = new TabItem(tab_ModuleList, SWT.NONE);
			tb_cpu.setText("cpu");

			list_cpu = new List(tab_ModuleList, SWT.NONE);
			tb_cpu.setControl(list_cpu);
			list_cpu.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleClicked(list_cpu);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleDoubleClicked(list_cpu);
				}
			});
		}
		{
			TabItem tb_bus = new TabItem(tab_ModuleList, SWT.NONE);
			tb_bus.setText("bus");

			list_bus = new List(tab_ModuleList, SWT.NONE);
			tb_bus.setControl(list_bus);
			list_bus.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleClicked(list_bus);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleDoubleClicked(list_bus);
				}
			});
		}
		{
			TabItem tb_mem = new TabItem(tab_ModuleList, SWT.NONE);
			tb_mem.setText("mem");

			list_mem = new List(tab_ModuleList, SWT.NONE);
			tb_mem.setControl(list_mem);
			list_mem.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleClicked(list_mem);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleDoubleClicked(list_mem);
				}
			});
		}
		{
			TabItem tb_other = new TabItem(tab_ModuleList, SWT.NONE);
			tb_other.setText("other");

			list_other = new List(tab_ModuleList, SWT.NONE);
			tb_other.setControl(list_other);
			list_other.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleClicked(list_other);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					ModuleDoubleClicked(list_other);
				}
			});
		}
		tab_ModuleList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				list_All.setSelection(-1);
				list_cpu.setSelection(-1);
				list_bus.setSelection(-1);
				list_mem.setSelection(-1);
				list_other.setSelection(-1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		Button btn_AddModule = new Button(composite_ModuleList, SWT.NONE);
		btn_AddModule.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btn_AddModule.setText("Add Module");
		btn_AddModule.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				boolean initcheck = Handler_SimulationInitThread.GetInitCheck();
				if (!initcheck) {
					initcheck = true;
					Handler_SimulationInitThread.SimInitThread_Func();
					Handler_SimulationInitThread.SetInitCheck(initcheck);
				}

				FileDialog file_dlg = new FileDialog(shell.getShell(), SWT.OPEN);

				file_dlg.setText("Load Block Designer Module.");
				String[] filterExt = { "*.so" };
				file_dlg.setFilterExtensions(filterExt);
				file_dlg.setFilterPath(System.getProperty("user.home"));

				String Module_Location = file_dlg.open();

				if (Module_Location != null) {
					// File directory = new File(saveTarget);
					Module_Location = Module_Location.replace("\\", "/");

					// Handler_Command.Command_Func(Module_Location);
					Handler_Command.Command_Func(0, 6, Module_Location, "NULL", "NULL", "NULL", "NULL");
					Handler_Command.Command_Func(1, 0, "NULL", "NULL", "NULL", "NULL", "NULL");

					try {
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO: handle exception
					}

					// Hanlder_CallBack.CallBack_Func();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		expandBar.setSpacing(5);
	}

	protected void AddModuleSelected(final String Module_Name) {
		// TODO Auto-generated method stub
		String Module_Title = null;
		UsedModuleIndex = 0;
		Module_Title = Instance_Module_Name + " (" + Module_Name + ")";

		// make Drawed module list.
		UsedModuleList.add(Instance_Module_Name);
		UsedModuleIndex = UsedModuleList.size() - 1;

		Expanditem.add(UsedModuleIndex,new ExpandItem(expandBar, SWT.NONE));
		Expanditem.get(UsedModuleIndex).setExpanded(false);
		Expanditem.get(UsedModuleIndex).setText(Module_Title);

		// composite_PlatformViewer
		// make composite, divide (port&par) grid
		composite_ExpandItem.add(UsedModuleIndex, new Composite(expandBar, SWT.VIRTUAL));
		composite_ExpandItem.get(UsedModuleIndex).setBackground(color_gray);
		composite_ExpandItem.get(UsedModuleIndex).setLayout(new GridLayout(2, false));
		composite_ExpandItem.get(UsedModuleIndex).setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));

		Expanditem.get(UsedModuleIndex)
				.setHeight(composite_ExpandItem.get(UsedModuleIndex).computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		Expanditem.get(UsedModuleIndex).setControl(composite_ExpandItem.get(UsedModuleIndex));

		{/* --- port table --- */
			table_port = new Table(composite_ExpandItem.get(UsedModuleIndex),
					SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
			GridData gd_table_port = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
			gd_table_port.heightHint = 150;
			table_port.setLayoutData(gd_table_port);
			table_port.setHeaderVisible(true);
			table_port.setLinesVisible(true);
			TableColumn tblclmnNewColumn = new TableColumn(table_port, SWT.CENTER);
			tblclmnNewColumn.setWidth(180);
			tblclmnNewColumn.setText("Port Name");

			TableColumn tblclmnNewColumn_1 = new TableColumn(table_port, SWT.CENTER);
			tblclmnNewColumn_1.setWidth(120);
			tblclmnNewColumn_1.setText("dest Module");

			TableColumn tblclmnNewColumn_4 = new TableColumn(table_port, SWT.CENTER);
			tblclmnNewColumn_4.setWidth(140);
			tblclmnNewColumn_4.setText("dest Port");

			int port_Cnt = 0;
			String module = null;

			// find selected module(Index) for get info(ModuleInfo.java)
			for (Selected_Index = 0; Selected_Index < ModuleInfoData.mList.size(); Selected_Index++) {
				module = ModuleInfoData.mList.get(Selected_Index).module_name;
				// module = UsedModuleList.get(Selected_Index);
				if (Module_Name.equals(module) == true)
					break;
			}

			// Create DrawModule Struct(ArrayList).
			// get module in port
			Module NewModule = new Module(ModuleInfoData.mList.get(Selected_Index));
			ModuleData = NewModule;
			UsedModuleDataList.mList.add(NewModule);
			UsedModuleDataList.mList.get(UsedModuleIndex).module_instance_name = Instance_Module_Name;

			final ArrayList<Port> PortDataList = UsedModuleDataList.mList.get(UsedModuleIndex).Port_List;
			port_Cnt = PortDataList.size();

			switch (UsedModuleDataList.mList.get(UsedModuleIndex).module_type) {
			case "cpu":
				ImageDescriptor idItem1 = ImageDescriptor.createFromFile(this.getClass(), "/images/img_core16.png");
				Image imgItemIcon1 = idItem1.createImage();
				Expanditem.get(UsedModuleIndex).setImage(imgItemIcon1);
				break;
			case "mem":
				ImageDescriptor idItem2 = ImageDescriptor.createFromFile(this.getClass(), "/images/img_memory16.png");
				Image imgItemIcon2 = idItem2.createImage();
				Expanditem.get(UsedModuleIndex).setImage(imgItemIcon2);
				break;
			case "bus":
				ImageDescriptor idItem3 = ImageDescriptor.createFromFile(this.getClass(), "/images/img_bus16.png");
				Image imgItemIcon3 = idItem3.createImage();
				Expanditem.get(UsedModuleIndex).setImage(imgItemIcon3);
				break;
			case "other":
				ImageDescriptor idItem4 = ImageDescriptor.createFromFile(this.getClass(), "/images/img_etc16.png");
				Image imgItemIcon4 = idItem4.createImage();
				Expanditem.get(UsedModuleIndex).setImage(imgItemIcon4);
				break;
			}

			for (int i = 0; i < port_Cnt - 2; i++) {
				new TableItem(table_port, SWT.NONE);
			}

			TableItem[] items = table_port.getItems();

			for (port_index = 2; port_index < items.length + 2; port_index++) {

				TableEditor editor = new TableEditor(table_port);

				editor = new TableEditor(table_port);
				Text text = new Text(table_port, SWT.READ_ONLY);
				text.setTouchEnabled(true);

				text.setText((String) PortDataList.get(port_index).sc_type + "<"
						+ PortDataList.get(port_index).data_type + ">" + PortDataList.get(port_index).port_name);

				editor.grabHorizontal = true;
				editor.setEditor(text, items[port_index - 2], 0);

				editor = new TableEditor(table_port);
				final CCombo cmb_DestModule = new CCombo(table_port, SWT.READ_ONLY);
				cmb_DestModule.setText("none");
				cmb_DestModule.addListener(SWT.DROP_DOWN, new Listener() {
					final String source_module_name = Instance_Module_Name;

					@Override
					public void handleEvent(Event event) {
						PastItem = cmb_DestModule.getText();
						// TODO Auto-generated method stub
						cmb_DestModule.removeAll();
						// Modification is necessary code
						for (int destModule = 0; destModule < UsedModuleList.size(); destModule++) {
							if (UsedModuleList.get(destModule) != source_module_name) {
								cmb_DestModule.add((String) UsedModuleList.get(destModule));
							}
						}
						cmb_DestModule.setText(PastItem);
					}
				});

				editor.grabHorizontal = true;
				editor.setEditor(cmb_DestModule, items[port_index - 2], 1);

				editor = new TableEditor(table_port);

				// final CCombo cmb_DestPort = new CCombo(table_port,
				// SWT.READ_ONLY);
				PortDataList.get(port_index).cmb_dPort = new CCombo(table_port, SWT.READ_ONLY);
				if (PortDataList.get(port_index).SM_Index > 0) {
					PortDataList.get(port_index).cmb_dPort.setEnabled(false);
				}
				PortDataList.get(port_index).cmb_dPort.setText("");

				// when you click dropdown, show destmodule in port
				PortDataList.get(port_index).cmb_dPort.addListener(SWT.DROP_DOWN, new Listener() {
					final int SelectedPort_Index = port_index;

					@Override
					public void handleEvent(Event event) {
						// TODO Auto-generated method stub
						if ((PortDataList.get(SelectedPort_Index).cmb_dPort.getText().equals(""))
								&& (cmb_DestModule.getText().equals("none") == false)
								&& (cmb_DestModule.getText().equals("") == false)) {
							String module = null;
							int port_Cnt = 0;
							int DestModule_Index = 0;
							for (DestModule_Index = 0; DestModule_Index < UsedModuleDataList.mList
									.size(); DestModule_Index++) {
								module = UsedModuleDataList.mList.get(DestModule_Index).module_instance_name;
								if (cmb_DestModule.getText().equals(module) == true)
									break;
							}
							port_Cnt = UsedModuleDataList.mList.get(DestModule_Index).Port_List.size();

							PortDataList.get(SelectedPort_Index).cmb_dPort.removeAll();

							// draw equal data_type&sc_type
							for (int destPort = 0; destPort < port_Cnt; destPort++) {

								String source_DataType = PortDataList.get(SelectedPort_Index).data_type;
								String dest_DataType = UsedModuleDataList.mList.get(DestModule_Index).Port_List
										.get(destPort).data_type;
								String source_scType = PortDataList.get(SelectedPort_Index).sc_type;
								String dest_scType = UsedModuleDataList.mList.get(DestModule_Index).Port_List
										.get(destPort).sc_type;
								if (source_DataType.equals(dest_DataType)) {
									if (source_scType.equals("sc_inout") && dest_scType.equals("sc_inout")) {
										PortDataList.get(SelectedPort_Index).cmb_dPort
												.add(UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).sc_type
												+ "<"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).data_type + ">"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).port_name);
									} else if (source_scType.equals("sc_in") && dest_scType.equals("sc_out")) {
										PortDataList.get(SelectedPort_Index).cmb_dPort
												.add(UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).sc_type
												+ "<"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).data_type + ">"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).port_name);
									} else if (source_scType.equals("sc_out") && dest_scType.equals("sc_in")) {
										PortDataList.get(SelectedPort_Index).cmb_dPort
												.add(UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).sc_type
												+ "<"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).data_type + ">"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).port_name);
									}
								}
								if ((source_DataType.equals("SM") && dest_DataType.equals("SS"))
										| (source_DataType.equals("SS") && dest_DataType.equals("SM"))) {
									if (UsedModuleDataList.mList.get(DestModule_Index).Port_List.get(destPort).cmb_dPort
											.isEnabled()) {
										PortDataList.get(SelectedPort_Index).cmb_dPort
												.add(UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).sc_type
												+ "<"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).data_type + ">"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).port_name);
									}
								}else if ((source_DataType.equals("MM") && dest_DataType.equals("MS"))
										| (source_DataType.equals("MS") && dest_DataType.equals("MM"))) {
									if (UsedModuleDataList.mList.get(DestModule_Index).Port_List.get(destPort).cmb_dPort
											.isEnabled()) {
										PortDataList.get(SelectedPort_Index).cmb_dPort
												.add(UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).sc_type
												+ "<"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).data_type + ">"
												+ UsedModuleDataList.mList.get(DestModule_Index).Port_List
														.get(destPort).port_name);
									}
								}
							}
							mouseClickState = false;
						}
					}
				});

				// if you select DestModule, Compare (past DestModule) to
				// (selection DestModule)
				cmb_DestModule.addSelectionListener(new SelectionListener() {
					final int SelectedModule_Index = port_index;

					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						if (PastItem.equals(cmb_DestModule.getItem((cmb_DestModule.getSelectionIndex()))) == false)
							PortDataList.get(SelectedModule_Index).cmb_dPort.removeAll();

					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub

					}
				});

				// if you select DestPort, setting to DestModule(DestPort)
				// object.
				PortDataList.get(port_index).cmb_dPort.addSelectionListener(new SelectionListener() {
					final int SelectedPort_Index = port_index;
					final int sourceModule_Index = UsedModuleIndex;

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						if (mouseClickState == true) {
							// TODO Auto-generated method stub
							// SM SETTING
							if (PortDataList.get(SelectedPort_Index).SM_Index >= 0) {
								int ptr = PortDataList.get(SelectedPort_Index).SM_Index;
								PortDataList.get(SelectedPort_Index + 1).cmb_dPort.setEnabled(true);
								for (int finder = SelectedPort_Index; finder < PortDataList.size(); finder++) {
									if (PortDataList.get(finder).SM_Index == ptr + 1) {
										PortDataList.get(finder).cmb_dPort.setEnabled(true);
									}
								}
							}
							// SM SETTING END

							String Destmodule = cmb_DestModule.getText();
							String module = null;
							int finder = 0;
							for (finder = 0; finder < UsedModuleDataList.mList.size(); finder++) {
								module = UsedModuleDataList.mList.get(finder).module_instance_name;
								if (Destmodule.equals(module) == true)
									break;
							}

							String dport = UsedModuleDataList.mList.get(sourceModule_Index).Port_List
									.get(SelectedPort_Index).cmb_dPort.getText();
							String[] getport = dport.split(">");

							int GetDestPortIndex = 0;
							for (GetDestPortIndex = 0; GetDestPortIndex < UsedModuleDataList.mList.get(finder).Port_List
									.size(); GetDestPortIndex++) {
								if ((UsedModuleDataList.mList.get(finder).Port_List.get(GetDestPortIndex).port_name)
										.equals(getport[1]))
									break;
							}
							Port SourcePort = PortDataList.get(SelectedPort_Index);
							Port DestPort = UsedModuleDataList.mList.get(finder).Port_List.get(GetDestPortIndex);

							if (UsedModuleDataList.mList.get(sourceModule_Index).Port_List
									.get(SelectedPort_Index).Dest_Port != null) {
								UsedModuleDataList.mList.get(sourceModule_Index).Port_List
										.get(SelectedPort_Index).Dest_Port.cmb_dPort.setText("");
							}

							UsedModuleDataList.mList.get(finder).Port_List.get(GetDestPortIndex).Dest_Port = SourcePort;
							UsedModuleDataList.mList.get(sourceModule_Index).Port_List
									.get(SelectedPort_Index).Dest_Port = DestPort;

							String DestModuleSet = UsedModuleDataList.mList.get(sourceModule_Index).Port_List
									.get(SelectedPort_Index).sc_type
									+ "<"
									+ UsedModuleDataList.mList.get(sourceModule_Index).Port_List
											.get(SelectedPort_Index).data_type
									+ ">" + UsedModuleDataList.mList.get(sourceModule_Index).Port_List
											.get(SelectedPort_Index).port_name;

							UsedModuleDataList.mList.get(finder).Port_List.get(GetDestPortIndex).cmb_dPort
									.setText(DestModuleSet);
							// SM SETTING
							if (UsedModuleDataList.mList.get(finder).Port_List.get(GetDestPortIndex).SM_Index >= 0) {
								int ptr = UsedModuleDataList.mList.get(finder).Port_List.get(GetDestPortIndex).SM_Index;
								UsedModuleDataList.mList.get(finder).Port_List.get(GetDestPortIndex + 1).cmb_dPort
										.setEnabled(true);
								for (int checker = SelectedPort_Index; checker < UsedModuleDataList.mList
										.get(finder).Port_List.size(); checker++) {
									if (UsedModuleDataList.mList.get(finder).Port_List.get(checker).SM_Index == ptr
											+ 1) {
										UsedModuleDataList.mList.get(finder).Port_List.get(checker).cmb_dPort
												.setEnabled(true);
									}
								}
							}
							// SM SETTING END

							Control[] ctr_DestModuleList = UsedModuleDataList.mList.get(finder).Port_List
									.get(GetDestPortIndex).cmb_dPort.getParent().getChildren();
							CCombo ctr_cmb_DestModule = (CCombo) ctr_DestModuleList[GetDestPortIndex * 3 - 5];

							ctr_cmb_DestModule
									.setText(PortDataList.get(SelectedPort_Index).Parent.module_instance_name);

							try {
								Control[] ctr_TempModule = PastLinkedModule.getParent().getChildren();
								CCombo ctr_cmb_TempPort = (CCombo) ctr_TempModule[PastLinkedModuleIndex];
								if (ctr_cmb_TempPort.getText().equals("") | ctr_cmb_TempPort.getText().equals(null))
									PastLinkedModule.setText("none");
							} catch (Exception e) {
								// TODO: handle exception
							}

							PastLinkedModule = ctr_cmb_DestModule;
							PastLinkedModuleIndex = (GetDestPortIndex * 3 - 5);
						}
						mouseClickState = true;
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
					}
				});

				editor.grabHorizontal = true;
				editor.setEditor(PortDataList.get(port_index).cmb_dPort, items[port_index - 2], 2);

			}
			port_index = 2;

		}

		{/* --- para table --- */
			table_par = new Table(composite_ExpandItem.get(UsedModuleIndex),
					SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
			GridData gd_table_par = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
			gd_table_par.heightHint = 150;
			table_par.setLayoutData(gd_table_par);
			table_par.setHeaderVisible(true);
			table_par.setLinesVisible(true);

			TableColumn tb_clmn_Parameter = new TableColumn(table_par, SWT.NONE);
			tb_clmn_Parameter.setWidth(100);
			tb_clmn_Parameter.setText("Parameter");

			TableColumn tb_clmn_Value = new TableColumn(table_par, SWT.NONE);
			tb_clmn_Value.setWidth(100);
			tb_clmn_Value.setText("Value");

			TableColumn tb_clmn_type = new TableColumn(table_par, SWT.NONE);
			tb_clmn_type.setWidth(150);
			tb_clmn_type.setText("Type");

			TableColumn tb_clmn_TypeWide = new TableColumn(table_par, SWT.NONE);
			tb_clmn_TypeWide.setWidth(50);
			tb_clmn_TypeWide.setText("Wide");

			int par_Cnt = 0;
			try {
				ArrayList<Parameter> ParameteData = ModuleInfoData.mList.get(Selected_Index).Parameter_List;
				par_Cnt = ParameteData.size();

				for (int i = 0; i < par_Cnt; i++) {
					new TableItem(table_par, SWT.NONE);
				}

				TableItem[] items = table_par.getItems();
				for (par_index = 0; par_index < items.length; par_index++) {

					TableEditor editor_par_table = new TableEditor(table_par);
					editor_par_table = new TableEditor(table_par);
					Text txt_ParameterName = new Text(table_par, SWT.READ_ONLY);
					txt_ParameterName.setTouchEnabled(true);
					txt_ParameterName.setText(ParameteData.get(par_index).par_name);
					editor_par_table.grabHorizontal = true;
					editor_par_table.setEditor(txt_ParameterName, items[par_index], 0);

					editor_par_table = new TableEditor(table_par);
					final Text txt_ParameterValue = new Text(table_par, SWT.NONE);
					editor_par_table.grabHorizontal = true;
					txt_ParameterValue.setText(ParameteData.get(par_index).default_value);
					txt_ParameterValue.addKeyListener(new KeyListener() {
						final int FocusedModuleIndex = UsedModuleIndex;
						final int FocusedModuleIn_ParIndex = par_index;

						@Override
						public void keyReleased(KeyEvent arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void keyPressed(KeyEvent arg0) {
							// TODO Auto-generated method stub
							UsedModuleDataList.mList.get(FocusedModuleIndex).Parameter_List
									.get(FocusedModuleIn_ParIndex).default_value = txt_ParameterValue.getText();
						}
					});
					editor_par_table.setEditor(txt_ParameterValue, items[par_index], 1);

					editor_par_table = new TableEditor(table_par);
					Text txt_ParameterType = new Text(table_par, SWT.READ_ONLY);
					txt_ParameterType.setTouchEnabled(true);
					switch (ParameteData.get(par_index).data_type) {
					case "0":
						txt_ParameterType.setText("UINT");
						break;
					case "1":
						txt_ParameterType.setText("BOOL");
						break;
					case "2":
						txt_ParameterType.setText("FLOAT");
						break;
					case "3":
						txt_ParameterType.setText("STRING");
						break;
					case "4":
						txt_ParameterType.setText("INT");
						break;
					}

					editor_par_table.grabHorizontal = true;
					editor_par_table.setEditor(txt_ParameterType, items[par_index], 2);

					editor_par_table = new TableEditor(table_par);
					Text txt_ParameterWide = new Text(table_par, SWT.READ_ONLY);
					txt_ParameterWide.setTouchEnabled(true);
					txt_ParameterWide.setText(ParameteData.get(par_index).bits_wide);
					editor_par_table.grabHorizontal = true;
					editor_par_table.setEditor(txt_ParameterWide, items[par_index], 3);
				}
				par_index = 0;

			} catch (NullPointerException e) {
				System.err.println(e);
			}

		}

		String type;
		int padding = 0;
		type = ModuleData.module_type;
		// set padding value from grid height.
		if (type.equals("bus") == true) {
			Button btnMemoryMapSetting = new Button(composite_ExpandItem.get(UsedModuleIndex), SWT.FLAT);
			btnMemoryMapSetting.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			btnMemoryMapSetting.setBackground(color_skyblue);
			btnMemoryMapSetting.setText("Memory Map Setting");
			padding = 15;

			btnMemoryMapSetting.addSelectionListener(new SelectionListener() {
				final int MemoryMap_Index = UsedModuleIndex;

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					BDMemoryMapEditor MMEditor = new BDMemoryMapEditor(shell.getShell(), "System BUS(AXI)",
							new IBDMemoryMapEditorListener() {

						@Override
						public void onOk(ArrayList<BDMemoryMapItem> items) {
							// TODO Auto-generated method stub
							int slave_cnt = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List.size();
							int setter_index = 0;
							for (BDMemoryMapItem item : items) {
//								System.out.println(String.format(
//										"ModuleName:%s, PortNAme:%s, StartAddr.:%s, AddrSize:%s, EndAddr:%s",
//										item.SlaveName, item.SlavePort, BDF.LongDecimalToStringHex(item.StartAddr, 8),
//										BDF.LongDecimalToStringHex(item.Size, 8),
//										BDF.LongDecimalToStringHex(item.EndAddr, 8)));
								for (; setter_index < slave_cnt; setter_index++) {
									if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(setter_index).Dest_Port != null) {
										if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
												.get(setter_index).Dest_Port.port_name.equals(item.SlavePort)) {
											UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
													.get(setter_index).startAddr = BDF
															.LongDecimalToStringHex(item.StartAddr, 8);
											UsedModuleDataList.mList.get(MemoryMap_Index).Port_List.get(
													setter_index).addrSize = BDF.LongDecimalToStringHex(item.Size, 8);
											setter_index++;
											break;
										}
									}
								}
							}
						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub
							System.out.println("BDMemoryMapEditor has dismissed");
						}
					});

					// Dummy data
					int port_size = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List.size();
					for (int slave_index = 0; slave_index < port_size; slave_index++) { // portList
																						// for
						if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List.get(slave_index).SM_Index >= 0) { // SM
																														// Port
																														// find
							if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List.get(slave_index).cmb_dPort
									.getEnabled()) { // Used SM port
								if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
										.get(slave_index).Dest_Port != null) {
									String Sname = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).Dest_Port.Parent.module_instance_name;
									String Dport = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).Dest_Port.port_name;
									String addrSize = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).addrSize;
									String startAddr = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).startAddr;
									MMEditor.addSlave(Sname, Dport, startAddr, addrSize);
//									System.err.println(
//											"Checker=" + Sname + "/" + Dport + "/" + startAddr + "/" + addrSize);
								}
							}
						}
						if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List.get(slave_index).data_type
								.equals("MM")) {
							if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List.get(slave_index).cmb_dPort
									.getEnabled()) { // Used SM port
								if (UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
										.get(slave_index).Dest_Port != null) {
									String Sname = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).Dest_Port.Parent.module_instance_name;
									String Dport = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).Dest_Port.port_name;
									String addrSize = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).addrSize;
									String startAddr = UsedModuleDataList.mList.get(MemoryMap_Index).Port_List
											.get(slave_index).startAddr;
									MMEditor.addSlave(Sname, Dport, startAddr, addrSize);
								}
							}
						}
					}

					MMEditor.show();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
				}
			});
		} else {
			Label lbl_null = new Label(composite_ExpandItem.get(UsedModuleIndex), SWT.NONE);
			lbl_null.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			lbl_null.setBackground(color_gray);
			padding = 10;
		}

		btnCheckButton.add(UsedModuleIndex,new Button(composite_ExpandItem.get(UsedModuleIndex), SWT.CHECK));
		btnCheckButton.get(UsedModuleIndex).setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		btnCheckButton.get(UsedModuleIndex).setText("Delete  ");
		btnCheckButton.get(UsedModuleIndex).setBackground(color_gray);

		btnCheckButton.get(UsedModuleIndex).pack();
		table_par.pack();
		table_port.pack();
		if (table_par.getSize().y >= table_port.getSize().y) {
			Expanditem.get(UsedModuleIndex).setHeight(200 + padding);
		} else {
			Expanditem.get(UsedModuleIndex).setHeight(200 + padding);
		}

		expandBar.setSpacing(5);

		Listener updateScrolledSize = new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						composite_PlatformViewer.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					}
				});
			}
		};
		expandBar.addListener(SWT.Expand, updateScrolledSize);
		expandBar.addListener(SWT.Collapse, updateScrolledSize);
		composite_PlatformViewer.setContent(expandBar);
		composite_PlatformViewer.setExpandHorizontal(true);
		composite_PlatformViewer.setExpandVertical(true);
		composite_PlatformViewer.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	public void viewsetting(String ori_pmml) {
		final String pmml = ori_pmml;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				/* --- Parser Module list setting--- */
				String module = null;
				String type = null;
				try {
					obj = (JSONObject) parser.parse(pmml);
					jsonObject = (JSONObject) obj;
					ModuleInfoList = (JSONArray) jsonObject.get("PMML");
					ModuleInfoData = new ModuleInfo(ModuleInfoList);
					// UsedModuleDataList = new ModuleInfo();

					list_Cnt = ModuleInfoData.mList.size();

					list_All.removeAll();
					list_cpu.removeAll();
					list_bus.removeAll();
					list_mem.removeAll();
					list_other.removeAll();

					for (int i = 0; i < list_Cnt; i++) {
						Module m = ModuleInfoData.mList.get(i);

						module = m.module_name;
						type = m.module_type;

						list_All.add(module);
						if (type.equals("cpu") == true)
							list_cpu.add(module);
						else if (type.equals("bus") == true)
							list_bus.add(module);
						else if (type.equals("mem") == true)
							list_mem.add(module);
						else if (type.equals("other") == true)
							list_other.add(module);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				/* --- Parser --- */
			}
		});
	}

	void ModuleClicked(List SelectedTab) {
//		String SelectedModule = SelectedTab.getItem(SelectedTab.getSelectionIndex());
	}

	void ModuleDoubleClicked(List SelectedTab) {
		// String SelectedModule =
		// SelectedTab.getItem(SelectedTab.getSelectionIndex());
		// AddModuleSelected(SelectedModule);
	}

	protected void DeleteModuleSelected() {
		// TODO Auto-generated method stub
		try {
			for (int delete_index = UsedModuleList.size() - 1; delete_index >= 0; delete_index--) {
				if (btnCheckButton.get(delete_index).getSelection() == true) {

					UsedModuleList.remove(delete_index);
					UsedModuleDataList.mList.remove(delete_index);

					Expanditem.get(delete_index).dispose();
					Expanditem.remove(delete_index);

					btnCheckButton.get(delete_index).dispose();
					btnCheckButton.remove(delete_index);

					composite_ExpandItem.get(delete_index).dispose();
					composite_ExpandItem.remove(delete_index);
				}
			}
		} catch (SWTException e) {
			// TODO: handle exception
			System.err.println(e);
		}
	}

	public void setFocus() {
	}

	public void InstanceName_Dialog() {
		final Shell dialog = new Shell(shell.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new FillLayout(SWT.HORIZONTAL));
		dialog.setText("Please write instance name");

		final Text txt_InstanceName = new Text(dialog, SWT.NONE);
		txt_InstanceName.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.keyCode == SWT.CR) {
					Instance_Module_Name = txt_InstanceName.getText();
					String Module_name = null;
					if (list_All.getSelectionIndex() != -1) {
						Module_name = list_All.getItem(list_All.getSelectionIndex());
						InstanceNameCheck(Module_name);
					} else if (list_cpu.getSelectionIndex() != -1) {
						Module_name = list_cpu.getItem(list_cpu.getSelectionIndex());
						InstanceNameCheck(Module_name);
					} else if (list_bus.getSelectionIndex() != -1) {
						Module_name = list_bus.getItem(list_bus.getSelectionIndex());
						InstanceNameCheck(Module_name);
					} else if (list_mem.getSelectionIndex() != -1) {
						Module_name = list_mem.getItem(list_mem.getSelectionIndex());
						InstanceNameCheck(Module_name);
					} else if (list_other.getSelectionIndex() != -1) {
						Module_name = list_other.getItem(list_other.getSelectionIndex());
						InstanceNameCheck(Module_name);
					}
					dialog.close();
				}

			}

			private void InstanceNameCheck(String module_name) {
				// TODO Auto-generated method stub
				if (Instance_Module_Name.length() >= 1) {
					boolean isExist = false;
					for (int index = 0; index < UsedModuleList.size(); index++) {
						if (Instance_Module_Name.equals(UsedModuleList.get(index))) {
							isExist = true;
						}
					}
					if (!isExist)
						AddModuleSelected(module_name);
				}

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		txt_InstanceName.pack();
		Button btn_OK = new Button(dialog, SWT.PUSH);
		btn_OK.setText("OK");
		btn_OK.pack();
		btn_OK.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				Instance_Module_Name = txt_InstanceName.getText();
				String Module_name = null;
				if (list_All.getSelectionIndex() != -1) {
					Module_name = list_All.getItem(list_All.getSelectionIndex());
					AddModuleSelected(Module_name);
				} else if (list_cpu.getSelectionIndex() != -1) {
					Module_name = list_cpu.getItem(list_cpu.getSelectionIndex());
					AddModuleSelected(Module_name);
				} else if (list_bus.getSelectionIndex() != -1) {
					Module_name = list_bus.getItem(list_bus.getSelectionIndex());
					AddModuleSelected(Module_name);
				} else if (list_mem.getSelectionIndex() != -1) {
					Module_name = list_mem.getItem(list_mem.getSelectionIndex());
					AddModuleSelected(Module_name);
				} else if (list_other.getSelectionIndex() != -1) {
					Module_name = list_other.getItem(list_other.getSelectionIndex());
					AddModuleSelected(Module_name);
				}
				dialog.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}

		});

		dialog.pack();
		dialog.open();

		// Move the dialog to the center of the top level shell.
		Rectangle shellBounds = shell.getBounds();
		Point dialogSize = dialog.getSize();

		dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
				shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
		dialog.setSize(300, 65);
	}
}
