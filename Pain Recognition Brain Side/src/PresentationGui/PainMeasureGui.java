package PresentationGui;

import java.awt.EventQueue;

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
	
	private ImageIcon Image;
	private static String[] imageList =  { 
        "C:\\forPain\\1.png", "C:\\forPain\\2.png", "C:\\forPain\\2.png", "C:\\forPain\\4.png"};
	
	
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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JLabel sliderMinPainImage;
		JLabel sliderMaxPainImage;
		frame = new JFrame();
		background=new JLabel();
		sliderMinPainImage=new JLabel(new ImageIcon("c:\\forPain\\netural.jpg"));
		sliderMaxPainImage=new JLabel(new ImageIcon("c:\\forPain\\pain.jpg"));
		
		slider = new JSlider();
		slider.setBounds(150, 260, 180, 35);
		sliderMinPainImage.setBounds(95,250,55,55);
		sliderMaxPainImage.setBounds(333,250,55,55);
		slider.setMinimum(0);
		slider.setMaximum(100);
		
		frame.setBounds(350, 50, 500, 600);
		frame.getContentPane().setLayout(null);
		Image = new ImageIcon(imageList[0]);
		background.setIcon(Image);
	  
		background.setBounds(100, 10, 279, 234);
		frame.add(background);
		frame.add(slider);
		frame.add(sliderMinPainImage);
		frame.add(sliderMaxPainImage);
		frame.repaint();
		
		textArea = new JTextArea();
		textArea.setBounds(25, 281, 422, 225);
		
		 scrollPane = new JScrollPane (textArea, 
		   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		 scrollPane.setBounds(25, 310, 422, 225);
		frame.getContentPane().add(scrollPane);
		
		//frame.pack();
		//frame.getContentPane().add(textArea);
		
		
		
		
	}
	public void openWindow(){
		
		this.frame.setVisible(true);
	}

	public void closeWindow(){
		this.frame.setVisible(false);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		RunTimeCase rtCase=(RunTimeCase)arg;
		double painMeasure =rtCase.getSolutionOutput()[0];
		if(painMeasure < 0.2)
		{
			Image = new ImageIcon(imageList[0]);
			
			
		}
		if(painMeasure > 0.2 && painMeasure<0.5){
			Image = new ImageIcon(imageList[0]);
		}
		if(painMeasure > 0.5 && painMeasure<0.7){
			Image = new ImageIcon(imageList[1]);
			
		}
		if(painMeasure > 0.7 && painMeasure<0.8){
			Image = new ImageIcon(imageList[2]);
			
		}
		if(painMeasure > 0.8 && painMeasure<0.9){
			Image = new ImageIcon(imageList[3]);
		}
		if(painMeasure > 0.9){
			Image = new ImageIcon(imageList[3]);
			
		}
		
		slider.setValue((int)(painMeasure*100));
		
		slider.repaint();
		background.setIcon(Image);
		background.repaint();
		
		if(painMeasure > ProjectConfig.PAIN_SENSITIVITY){
			textArea.append(String.valueOf(painMeasure)+" - Pain..Pain..Pain!!\n");
			Image = new ImageIcon(imageList[3]);
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
