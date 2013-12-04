package PresentationGui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import businessLogic.RunTimeCase;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import dataLayer.ProjectConfig;

public class PainMeasureGui  implements Observer{

	private JFrame frame;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JLabel background;
	
	private ImageIcon Image = new ImageIcon();
	private ArrayList<ImageIcon> painIcons;
	
	
	JSlider slider;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PainMeasureGui window = new PainMeasureGui();
					window.frame.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PainMeasureGui() {
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
		frame = new JFrame();
		background=new JLabel();
		frame.setPreferredSize(new Dimension(500,700));
		frame.setResizable(false);
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
		
		frame.setBounds(350, 50, 500, 700);
		frame.getContentPane().setLayout(null);
		background.setIcon(painIcons.get(0));
	  
		background.setBounds(60, 10, 363, 302);
		frame.add(background);
		frame.add(slider);
		frame.add(sliderMinPainImage);
		frame.add(sliderMaxPainImage);
		frame.repaint();
		
		textArea = new JTextArea();
		textArea.setBounds(25, 281, 422, 225);
		textArea.setEditable(false);
		
		 scrollPane = new JScrollPane (textArea, 
		 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		 scrollPane.setBounds(25, 410, 422, 225);
		frame.getContentPane().add(scrollPane);
		
		frame.pack();
		//frame.getContentPane().add(textArea);
		
		
		
		
	}
	protected void onFrameLoading() {
		textArea.setText("");
	}

	public void openWindow(){
		
		this.frame.setVisible(true);
	}

	public void closeWindow(){
		this.frame.setVisible(false);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(!(arg instanceof RunTimeCase))
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
		background.setIcon(Image);
		background.repaint();
		
		if(painMeasure > ProjectConfig.getOptDouble("PAIN_SENSITIVITY")){
			textArea.append(String.valueOf(painMeasure)+" - Pain..Pain..Pain!!\n");
			background.setIcon(Image);
			background.repaint();
	
		}
		else
		{
			textArea.append(String.valueOf(painMeasure)+"\n");
		}
		
		final int length = textArea.getText().length();
		textArea.setCaretPosition(length);
		textArea.repaint();
		
		
	}

}
