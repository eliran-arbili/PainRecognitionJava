package PresentationGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import businessLogic.RunTimeCase;
import businessLogic.casesServer.Server;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;

import dataLayer.ProjectConfig;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PainMeasureGui  implements Observer{

	
	private Server painRecognitionServer ;
	private ImageIcon Image = new ImageIcon();
	private ArrayList<ImageIcon> painIcons;
	private int alarmCycle;
	private boolean toPlay;
	

	/**
	 * Create the application.
	 */
	public PainMeasureGui(Server painRecognitionServer) {
		this.painRecognitionServer = painRecognitionServer;
		alarmCycle=ProjectConfig.getOptInt("CYCLES_FOR_ALARM"); 
		painIcons = getPainIcons();
		initialize();
		
	}

	private ArrayList<ImageIcon> getPainIcons() {
		
		ClassLoader loader = this.getClass().getClassLoader();
		ArrayList<ImageIcon> list = new ArrayList<ImageIcon>();
		for(int index = 1 ; index <= 9; index++){
			list.add(new ImageIcon(loader.getResource("resources/painExpressions/pain"+index+".png")));
		}
		
		return list;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JLabel sliderMinPainImage;
		JLabel sliderMaxPainImage;
		iconPlay=new ImageIcon(this.getClass().getClassLoader().getResource("resources/play.png"));
		iconPause=new ImageIcon(this.getClass().getClassLoader().getResource("resources/pause.png"));
		frame = new JFrame();
		frame.setResizable(false);
		lblFaceImage=new JLabel();
		frame.setPreferredSize(new Dimension(500, 730));
		frame.addWindowListener(new WindowAdapter() {
			@Override
            public void windowActivated(java.awt.event.WindowEvent e) {
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
		
		frame.setBounds(350, 50, 500, 737);
		frame.getContentPane().setLayout(null);
		lblFaceImage.setIcon(painIcons.get(0));
	  
		lblFaceImage.setBounds(60, 10, 363, 302);
		frame.getContentPane().add(lblFaceImage);
		frame.getContentPane().add(slider);
		frame.getContentPane().add(sliderMinPainImage);
		frame.getContentPane().add(sliderMaxPainImage);
		frame.repaint();
		
		lstLastCases = new JList<String>(new CasesListModel());
		lstLastCases.setBounds(25, 281, 422, 225);
		lstLastCases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstLastCases.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				onClickCasesList(e);
			}
		});
		 scrollPane = new JScrollPane (lstLastCases, 
		   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		 scrollPane.setBounds(26, 465, 422, 225);
		frame.getContentPane().add(scrollPane);
		
		btnPlayPause = new JButton();
		btnPlayPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClickPlayPause(arg0);
			}
		});
		toPlay=false;
		btnPlayPause.setIcon(iconPlay);
		btnPlayPause.setBounds(198, 406, 89, 59);
		btnPlayPause.setOpaque(false);
		btnPlayPause.setContentAreaFilled(false);
		btnPlayPause.setBorderPainted(false);
		frame.getContentPane().add(btnPlayPause);
		
		frame.pack();
		//frame.getContentPane().add(textArea);
	}
	protected void onClickCasesList(MouseEvent evt) {
	    if(evt.getSource() != lstLastCases || toPlay==true){
	    	return;
	    }
        if (evt.getClickCount() == 2 && ! evt.isConsumed()) {
        	
        	evt.consume();
            RunTimeCase rtCase = ((CasesListModel)lstLastCases.getModel()).getCase(lstLastCases.getSelectedIndex());
            String newSolOutput = JOptionPane.showInputDialog(this.frame, "Case is: "+rtCase+"\nEnter Your Solution","Revise System Solution",JOptionPane.OK_CANCEL_OPTION);
            if(newSolOutput == null){
            	return;
            }
            try{
            	String [] sols = newSolOutput.split(",");
                double [] newSol = new double[sols.length];
                for(int i = 0 ; i < newSol.length; i++){
                	newSol[i] = Double.parseDouble(sols[i]);
                }
                boolean isSuccess = painRecognitionServer.handleReviseRequest(rtCase, newSol);
                if(isSuccess){
                	JOptionPane.showMessageDialog(this.frame, "Revise Done!", "Revise", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                	JOptionPane.showMessageDialog(this.frame, "Revise Failed!", "Revise", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this.frame, "Wrong Number Format, Please Try Again","Operation Couldn't be completed",JOptionPane.ERROR_MESSAGE);
            }
        } 	
	}

	protected void onClickPlayPause(ActionEvent arg0) {
		
		if(toPlay==false)
		{
			btnPlayPause.setIcon(iconPause);
			toPlay=true;
			lstLastCases.setToolTipText(null);
		}
		else
		{
			btnPlayPause.setIcon(iconPlay);
			toPlay=false;
			lstLastCases.setToolTipText("Select  To Revise");
		}
		
	}

	protected void onFrameLoading() {
		lstLastCases.removeAll();
	}

	public void openWindow(){
		
		this.frame.setVisible(true);
	}

	public void closeWindow(){
		this.frame.setVisible(false);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(!(arg instanceof RunTimeCase) || toPlay==false)
			return;
		RunTimeCase rtCase=(RunTimeCase)arg;
		double painMeasure =rtCase.getSolutionOutput()[0];
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
		
		slider.setValue((int)(painMeasure*100));
		
		slider.repaint();
		lblFaceImage.setIcon(Image);
		lblFaceImage.repaint();
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
	
}
