package PresentationGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import businessLogic.RunTimeCase;
import businessLogic.painServer.Server;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.basic.BasicSliderUI;

import dataLayer.ProjectConfig;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * A GUI for representing the actual pain recognition
 * @author Eliran Arbeli , Arie Gaon
 *
 */
public class PainMeasureGui  implements Observer{

	
	private Server painRecognitionServer ;
	private ImageIcon Image = new ImageIcon();;
	private ArrayList<ImageIcon> painIcons;
	private int alarmCycle;
	private boolean toPlay;
	private ReviseDialog reviseDialog;
	private JPanel backgroundPanel;
	/**
	 * Create new PainMEasureGui window.
	 */
	public PainMeasureGui(Server painRecognitionServer) {
		this.painRecognitionServer = painRecognitionServer;
		alarmCycle=ProjectConfig.getOptInt("CYCLES_FOR_ALARM"); 
		painIcons = getPainIcons();
		reviseDialog=new ReviseDialog();
		initialize();
		
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		JLabel sliderMinPainImage;
		JLabel sliderMaxPainImage;
		JLabel frameBackground=new JLabel();
		iconPlay=new ImageIcon(this.getClass().getClassLoader().getResource("resources/play.png"));
		iconPause=new ImageIcon(this.getClass().getClassLoader().getResource("resources/pause.png"));
		ImageIcon iconBackground=new ImageIcon(this.getClass().getClassLoader().getResource("resources/PainMeasureBG.jpg"));
		frameBackground.setIcon(iconBackground);
		
		frame = new JFrame();
		
		frame.setResizable(false);
		lblFaceImage=new JLabel();
		frame.setPreferredSize(new Dimension(624,464));
		
		try {
			backgroundPanel = new BackgroundPanel(ImageIO.read(this.getClass().getClassLoader().getResource("resources/PainMeasureBG.jpg")));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		backgroundPanel.setBounds(0,0,624,424);
		backgroundPanel.setLayout(null);
		frame.getContentPane().add(backgroundPanel);
		frameBackground.setBounds(0,0,700,730);
		
		frame.addComponentListener(new ComponentAdapter() {
	        public void componentShown ( ComponentEvent e )
	        {
	        	onFrameLoading();
	        }
		});
		sliderMinPainImage=new JLabel(painIcons.get(7));
		sliderMaxPainImage=new JLabel(painIcons.get(8));
		
		slider = new JSlider();
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setForeground(Color.RED);
		slider.setMajorTickSpacing(25);
		slider.setMinorTickSpacing(10);
		slider.setBounds(150, 350, 180, 45);
		sliderMinPainImage.setBounds(50,320,100,82);
		sliderMaxPainImage.setBounds(333,320,100,82);
		slider.setMinimum(0);
		slider.setMaximum(100);
		
		
		frame.getContentPane().setLayout(null);
		lblFaceImage.setIcon(painIcons.get(0));
	  
		lblFaceImage.setBounds(74, 29, 349, 283);
		backgroundPanel.add(lblFaceImage);
		
		slider.setUI(new MySliderUI(slider));
		slider.setOpaque(false);
		backgroundPanel.add(slider);
		backgroundPanel.add(sliderMinPainImage);
		backgroundPanel.add(sliderMaxPainImage);
		frame.repaint();
		 scrollPane = new JScrollPane (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		 scrollPane.setBounds(457, 22, 66, 290);
		 backgroundPanel.add(scrollPane);
		
		btnPlayPause = new JButton();
		btnPlayPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClickPlayPause(arg0);
			}
		});
		toPlay=false;
		btnPlayPause.setIcon(iconPlay);
		btnPlayPause.setBounds(443, 320, 89, 59);
		btnPlayPause.setOpaque(false);
		btnPlayPause.setContentAreaFilled(false);
		btnPlayPause.setBorderPainted(false);
		backgroundPanel.add(btnPlayPause);
		
		 lstLastCases = new JList<String>(new CasesListModel());
		 scrollPane.setViewportView(lstLastCases);
		 lstLastCases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 lstLastCases.addMouseListener(new MouseAdapter() {
		 	public void mouseClicked(MouseEvent e){
		 		onClickCasesList(e);
		 	}
		 });
		
		
		
		frame.pack();
		//frame.getContentPane().add(textArea);
	}
	
	
	/*
	 * Member Functions
	 */
	
	public void openWindow(){
		
		this.frame.setVisible(true);
	}

	public void closeWindow(){
		this.frame.setVisible(false);
		lstLastCases.removeAll();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof String){
			handleServerStatus((String)arg);
		}
		
		
		if(!(arg instanceof RunTimeCase) || toPlay==false)
			return;
		RunTimeCase rtCase=(RunTimeCase)arg;
		double painMeasure =rtCase.getSolutionOutput()[0];
		
		setImageFace(painMeasure);
		setSliderValue(painMeasure);
		
		/* Determine action here */
		((CasesListModel)lstLastCases.getModel()).add(rtCase);
		if(painMeasure > ProjectConfig.getOptDouble("PAIN_SENSITIVITY")){
			
			alarmCycle--;
			if(alarmCycle<=0)
			{
				lstLastCases.setBackground(Color.red);
			}
		}
		else
		{
			alarmCycle=ProjectConfig.getOptInt("CYCLES_FOR_ALARM");
			lstLastCases.setBackground(Color.white);

		}
		
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
		
	}
	

	/*
	 * Components CallBacks
	 */
	protected void onClickCasesList(MouseEvent evt) {
	    if(evt.getSource() != lstLastCases || toPlay==true){
	    	return;
	    }
        if (evt.getClickCount() == 2 && ! evt.isConsumed()) {
        	
        	evt.consume();
            RunTimeCase rtCase = ((CasesListModel)lstLastCases.getModel()).getCase(lstLastCases.getSelectedIndex());
            double[] newSol=reviseDialog.showReviseDialog(rtCase);
            if(newSol==null)
            	return;
            boolean isSuccess = painRecognitionServer.handleReviseRequest(rtCase,  newSol);
            if(isSuccess){
            	JOptionPane.showMessageDialog(this.frame, "Revise Done!", "Revise", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
            	JOptionPane.showMessageDialog(this.frame, "Revise Failed!", "Revise", JOptionPane.ERROR_MESSAGE);
            }
            
        } 	
	}

	protected void onClickPlayPause(ActionEvent arg0) {
		
		if(toPlay==false)
		{
			btnPlayPause.setIcon(iconPause);
			toPlay=true;
			lstLastCases.setToolTipText(null);
			lstLastCases.clearSelection();
		}
		else
		{
			btnPlayPause.setIcon(iconPlay);
			toPlay=false;
			lstLastCases.setToolTipText("Select  To Revise");
		}
		
	}

	protected void onFrameLoading() {
		((CasesListModel)lstLastCases.getModel()).removeAll();
		slider.setValue(0);
		lstLastCases.setBackground(Color.white);
	}

	
	/*
	 * Auxiliary Functions
	 */
	
	private ArrayList<ImageIcon> getPainIcons() {
		
		ClassLoader loader = this.getClass().getClassLoader();
		ArrayList<ImageIcon> list = new ArrayList<ImageIcon>();
		for(int index = 1 ; index <= 9; index++){
			list.add(new ImageIcon(loader.getResource("resources/painExpressions/pain"+index+".png")));
		}
		
		return list;
	}
	
	private void handleServerStatus(String arg) {
		if(arg.equals(Server.SERVER_STOPPED) || arg.equals(Server.SERVER_CLOSED)){
			closeWindow();
			return;
		}
		if(arg.equals(Server.SERVER_STARTED)){
			openWindow();
			return;
		}
	}
	
	private void setSliderValue(double painMeasure) {
		slider.setValue((int)(painMeasure*100));
		slider.repaint();
	}

	private void setImageFace(double painMeasure){
		if(painMeasure < 0.2)
		{
			Image = painIcons.get(0);
			
		}
		if(painMeasure > 0.2 && painMeasure<0.4){
			Image = painIcons.get(1);

		}
			
		if(painMeasure >= 0.4 && painMeasure < 0.7){
			Image = painIcons.get(2);

		}
		if(painMeasure >= 0.7 && painMeasure < 0.8){
			Image = painIcons.get(3);
		}
		if(painMeasure >= 0.8 && painMeasure < 0.90){
			Image = painIcons.get(4);

		}
		if(painMeasure >= 0.9 && painMeasure < 0.95){
			Image = painIcons.get(5);

		}
		if(painMeasure > 0.95){
			Image = painIcons.get(6);	
		}
		lblFaceImage.setIcon(Image);
		lblFaceImage.repaint();
	}
	/*
	 * Gui Components
	 */
	private JFrame frame;
	private JList<String> lstLastCases;
	private JScrollPane scrollPane;
	private JLabel lblFaceImage;
	private JButton btnPlayPause;
	private ImageIcon iconPlay;
	private ImageIcon iconPause;
	private JSlider slider;
	
	private static class MySliderUI extends BasicSliderUI {

	    private static float[] fracs = {0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f};
	    private LinearGradientPaint p;

	    public MySliderUI(JSlider slider) {
	        super(slider);
	        
	    }

	    @Override
	    public void paintTrack(Graphics g) {
	        Graphics2D g2d = (Graphics2D) g;
	        Rectangle t = trackRect;
	        Point2D start = new Point2D.Float(t.x, t.y);
	        Point2D end = new Point2D.Float(t.width, t.height);
	        Color[] colors = {Color.white, Color.white, Color.white,
	            Color.white, Color.white, Color.red};
	        p = new LinearGradientPaint(start, end, fracs, colors);
	        g2d.setPaint(p);
	        g2d.fillRect(t.x, t.y, t.width, t.height);
	    }

	    @Override
	    public void paintThumb(Graphics g) {
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setRenderingHint(
	            RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
	        Rectangle t = thumbRect;
	        g2d.setColor(Color.black);
	        g2d.setBackground(Color.black);
	        int tw2 = t.width / 2;
	        int xpoints[] = {t.x, t.x + t.width - 1, t.x + tw2};
	        int ypoints[] = {t.y, t.y,  t.y + t.height};
	        int npoints = 3;

	        g2d.fillPolygon(xpoints, ypoints, npoints);
	        //g2d.drawLine(t.x, t.y, t.x + t.width - 1, t.y);
	       //g2d.drawLine(t.x, t.y, t.x + tw2, t.y + t.height);
	        //g2d.drawLine(t.x + t.width - 1, t.y, t.x + tw2, t.y + t.height);
	    }
	}

}

