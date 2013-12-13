package PresentationGui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class MainFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
			            // Set System L&F
					UIManager.setLookAndFeel(
			            UIManager.getSystemLookAndFeelClassName());

					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {		
		initialize();
	}
	
	private void initialize()
	{
		
		menuBar 			= new JMenuBar();
		mnFile 				= new JMenu("File");
		mntmExit 			= new JMenuItem("Exit");
		mnHelp 				= new JMenu("Help");
		mntmAbout 			= new JMenuItem("About");
		contentPane 		= new JPanel();
		tabbedPane 			= new JTabbedPane(JTabbedPane.TOP);
		footerPanel 		= new JPanel();
		dlgAbout			= new AboutDialog();
		try {
			backgroundImage		= ImageIO.read(this.getClass().getClassLoader().getResource("resources/background.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		serverRunModePanel	= new ServerRunModePanel(backgroundImage);
		trainingPanel		= new TrainingPanel(backgroundImage);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(tabbedPane.getSelectedComponent() == serverRunModePanel){
					serverRunModePanel.refreshTags();
					System.out.println("Chnaged");
				}
			}
		});
		
		setBounds(100, 100, 662, 650);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {	
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				onApplicationExit();
			}
		});
		
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onApplicationExit();
			}
		});
		
		mntmAbout.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onAboutClick();
			}
		});
		
		setJMenuBar(menuBar);
		menuBar.add(mnFile);
		mnFile.add(mntmExit);
		menuBar.add(mnHelp);
		mnHelp.add(mntmAbout);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		tabbedPane.setBounds(0, 0, 650, 501);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><span style='font-size:14pt;font-weight:bold;font-family:Arial'>RunMode</span></body></html>", serverRunModePanel);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><span style='font-size:14pt;font-weight:bold;font-family:Arial'>Training</span></body></html>", trainingPanel);
		
		contentPane.add(tabbedPane);	
		footerPanel.setBounds(10, 514, 626, 76);
		contentPane.add(footerPanel);
	}

	protected void onAboutClick() {
		dlgAbout.setVisible(true);
	}

	protected void onApplicationExit() {
        int confirm = JOptionPane.showOptionDialog(this,
                "Are You Sure You Want To Exit?",
                "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        
        if (confirm == JOptionPane.YES_OPTION) {
    			try 
    			{
    				serverRunModePanel.closeServer();
    			} 
    			catch (IOException e) 
    			{
    				e.printStackTrace();
    			}
        		System.exit(0);
        }
	}
	
	/*
	 * Gui Components
	 */
	
	private JMenu 				mnFile;
	private JMenuBar 			menuBar;
	private JMenuItem 			mntmExit;
	private JMenu 				mnHelp;
	private JMenuItem 			mntmAbout;
	private JTabbedPane 		tabbedPane;
	private JPanel 				footerPanel;
	private ServerRunModePanel 	serverRunModePanel;
	private TrainingPanel 		trainingPanel;
	private Image				backgroundImage;
	private AboutDialog			dlgAbout;
}