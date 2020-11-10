import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame
{
	static GamePanel p1 = new GamePanel();//�ЫعC�����O
	static InformationPanel p2 = new InformationPanel();//�Ыظ�T���O
	static connect p3 = new connect();//�ЫعC�����O
	static connect2 p4 = new connect2();//�ЫعC�����O
	static chat p5 = new chat();//�ЫعC�����O
	static chat2 p6 = new chat2();//�ЫعC�����O
	
	public Client()
	{//�t�m�ج[���G��
		setLayout(new BorderLayout());
		add(p1,BorderLayout.CENTER);
		add(p2,BorderLayout.EAST);
	}
 
	public static void main(String[] args)
	{
		JFrame frame = new Client();//�s�خج[
 
		//�t�m�ج[
		frame.setTitle("Snake Game");
		frame.setSize(1100, 800);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
 
		//�s�ذ����
		
		Thread thread1 = new Thread(p1);
		thread1.start();
		Thread thread2 = new Thread(p2);
		thread2.start();
		Thread thread3 = new Thread(p3);
		thread3.start();
		Thread thread4 = new Thread(p4);
		thread4.start();
		Thread thread5 = new Thread(p5);
		thread5.start();
		Thread thread6 = new Thread(p6);
		thread6.start();
	}
}
 
class GamePanel extends JPanel implements Runnable
{
	public static final int PER_UNIT_LENGTH = 20;//������
	public static final int MULTIPLE = 15;//����
	public static final int HALF_SIDE = MULTIPLE * PER_UNIT_LENGTH;//�C����ت��@�b�� = ���� * ������
 
	private boolean isFirstRun = true;//�P�_�O�_�ݭn��l��
	private boolean isStarted = false;//�P�_�O�_�}�l
	private boolean isPaused = false;//�P�_�O�_�Ȱ�
	private static int score2 = 0;//�C������
	private static int score = 0;//�C������
	private static int information = 0;//�ǻ��C����T
	private static char dir = 'R';//�C������
	
	private static Snake snake = new Snake();//�s�ؤ@���D
	private static Snake snake2 = new Snake();//�s�ؤ@���D
	private static Dot dessert = new Dot();//�s�ؤ@���I��
	private static boolean isConnet = false;//�w�]�����s�u���A
 
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
 
		//������I�y��
		int xCentre = getWidth() / 2;
		int yCentre = getHeight() / 2;

		//����@���H���I�y��
		int xRandom, yRandom;
 
		if(isFirstRun)
		{
			isFirstRun = false;
 
			//��l�ƹC�����
			g.drawRect(xCentre - HALF_SIDE, yCentre - HALF_SIDE, HALF_SIDE * 2, HALF_SIDE * 2);
 
			//��l�ƳD�Y
			snake.getHead().setX(xCentre+80);
			snake.getHead().setY(yCentre);
			g.setColor(Color.ORANGE);
			g.fillRect(snake.getHead().getX(), snake.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
			
			snake2.getHead().setX(xCentre-80);
			snake2.getHead().setY(yCentre);
			g.setColor(Color.BLUE);
			g.fillRect(snake2.getHead().getX(), snake2.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
 
			//��l���I��
			do{
				xRandom = xCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
				yRandom = yCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
			}while(xRandom == snake.getHead().getX() && yRandom == snake.getHead().getY() && xRandom == snake2.getHead().getX() && yRandom == snake2.getHead().getY());
			dessert.setX(xRandom);
			dessert.setY(yRandom);
			g.setColor(Color.DARK_GRAY);
			g.fillOval(dessert.getX(), dessert.getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
		}
		else
		{
			//ø�e�C�����
			g.drawRect(xCentre - HALF_SIDE, yCentre - HALF_SIDE, HALF_SIDE * 2, HALF_SIDE * 2);
 
			//ø�e�D��
			g.setColor(Color.MAGENTA);
			for(int i = 0;i < snake.getBody().size();i++)
			{
				g.fillRect(snake.getBody().get(i).getX(), snake.getBody().get(i).getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
			}
			
			g.setColor(Color.CYAN);
			for(int i = 0;i < snake2.getBody().size();i++)
			{
				g.fillRect(snake2.getBody().get(i).getX(), snake2.getBody().get(i).getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
			}
 
			//ø�e�D�Y
			g.setColor(Color.ORANGE);
			g.fillRect(snake.getHead().getX(), snake.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
			
			g.setColor(Color.BLUE);
			g.fillRect(snake2.getHead().getX(), snake2.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
 
			//�p�G�D�Y���I�ߡA�h��s�I��
			if(isEncountered()||isEncountered2())
			{
				do
				{
					xRandom = xCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
					yRandom = yCentre - HALF_SIDE + ((int)(Math.random() * MULTIPLE * 2)) * PER_UNIT_LENGTH;
				}while(xRandom == snake.getHead().getX() && yRandom == snake.getHead().getY() && xRandom == snake2.getHead().getX() && yRandom == snake2.getHead().getY());
				dessert.setX(xRandom);
				dessert.setY(yRandom);
			}
			g.setColor(Color.DARK_GRAY);
			g.fillOval(dessert.getX(), dessert.getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
 
			//�p�G�C�������A�h�l�[ø�eGAME OVER
			if(isCrushed()){
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();
				int stringWidth = fm.stringWidth("GAME OVER");
				int stringAscent = fm.getAscent();
				int xCoordinate = xCentre - stringWidth / 2;
				int yCoordinate = yCentre - stringAscent / 2;
				g.drawString("GAME OVER", xCoordinate, yCoordinate);
			}
		}
	}
 
	public GamePanel()
	{
		//�t�m���O�ݩ�
		setFocusable(true);
		setFont(new Font("Californian FB", Font.BOLD, 80));
 
		//���U��L��ť��
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				int direction = snake.getDirection();

				switch(e.getKeyCode()){
					case KeyEvent.VK_UP:
						if(isStarted && !isPaused && !isCrushed())
						{
							if(direction != Snake.DIRECTION_UP && direction != Snake.DIRECTION_DOWN){
							snake.setDirection(Snake.DIRECTION_UP);
							dir = 'U';
							//changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_DOWN:
						if(isStarted && !isPaused && !isCrushed())
						{
							if(direction != Snake.DIRECTION_UP && direction != Snake.DIRECTION_DOWN){
							snake.setDirection(Snake.DIRECTION_DOWN);
							dir = 'D';
							//changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_LEFT:
						if(isStarted && !isPaused && !isCrushed())
						{
							if(direction != Snake.DIRECTION_LEFT && direction != Snake.DIRECTION_RIGHT){
							snake.setDirection(Snake.DIRECTION_LEFT);
							dir = 'L';
							//changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_RIGHT:
						if(isStarted && !isPaused && !isCrushed())
						{
							if(direction != Snake.DIRECTION_LEFT && direction != Snake.DIRECTION_RIGHT){
							snake.setDirection(Snake.DIRECTION_RIGHT);
							dir = 'R';
							//changeSnakeLocation();
							}
						}
						break;
					case KeyEvent.VK_ENTER:
						if(isCrushed())
						{//�p�G�C�������A�h���m��ƭ��s�}�l�C��
							snake.setDirection(Snake.DIRECTION_RIGHT);
							snake.setSpeed(Snake.SPEED_3);
							snake.setBody(new LinkedList<Dot>());
							snake2.setDirection(Snake.DIRECTION_RIGHT);
							snake2.setSpeed(Snake.SPEED_3);
							snake2.setBody(new LinkedList<Dot>());
							
							isFirstRun = true;
							isStarted = false;
							isPaused = false;
							dir = 'E';
							score = 0;
							score2 = 0;
							information = 0;
							repaint();
						}
						else
						{
							dir = 'Z';
							System.out.print("ready1");
							isStarted = true;
						}
						break;
					case KeyEvent.VK_SPACE:
						if(isStarted && !isCrushed()) 
						{
							isPaused = !isPaused;
							dir = 'P';}
						break;
					case KeyEvent.VK_F1:snake.setSpeed(Snake.SPEED_1);break;
					case KeyEvent.VK_F2:snake.setSpeed(Snake.SPEED_2);break;
					case KeyEvent.VK_F3:snake.setSpeed(Snake.SPEED_3);break;
					case KeyEvent.VK_F4:snake.setSpeed(Snake.SPEED_4);break;
					case KeyEvent.VK_F5:snake.setSpeed(Snake.SPEED_5);break;
					default:
				}

			}
		});
	}
 
	public void run()
	{//����D�۰ʫe�i
		while(true)
		{
			int direction2 = snake2.getDirection();
			switch(connect.getdir())
			{
			case 'U':
				if(isStarted && !isPaused && !isCrushed()){
					if(direction2 != Snake.DIRECTION_UP && direction2 != Snake.DIRECTION_DOWN){
					snake2.setDirection(Snake.DIRECTION_UP);
					}
				}
				break;

			case 'D':
				if(isStarted && !isPaused && !isCrushed()){
					if(direction2 != Snake.DIRECTION_UP && direction2 != Snake.DIRECTION_DOWN){
					snake2.setDirection(Snake.DIRECTION_DOWN);
					changeSnakeLocation2();
					changeSnakeLocation();
					}
				}
				break;
				
			case 'L':
				if(isStarted && !isPaused && !isCrushed()){
					if(direction2 != Snake.DIRECTION_LEFT && direction2 != Snake.DIRECTION_RIGHT){
					snake2.setDirection(Snake.DIRECTION_LEFT);
					changeSnakeLocation2();
					changeSnakeLocation();
					}
				}
				break;

			case 'S':
				if(isStarted && !isPaused && !isCrushed())
				{
					if(direction2 != Snake.DIRECTION_LEFT && direction2 != Snake.DIRECTION_RIGHT){
					snake2.setDirection(Snake.DIRECTION_RIGHT);
					changeSnakeLocation2();
					changeSnakeLocation();
					}
				}
				break;
				
			case 'A':
				if(isCrushed())
				{//�p�G�C�������A�h���m��ƭ��s�}�l�C��
					snake.setDirection(Snake.DIRECTION_RIGHT);
					snake.setSpeed(Snake.SPEED_3);
					snake.setBody(new LinkedList<Dot>());
					snake2.setDirection(Snake.DIRECTION_RIGHT);
					snake2.setSpeed(Snake.SPEED_3);
					snake2.setBody(new LinkedList<Dot>());
					isFirstRun = true;
					isStarted = false;
					isPaused = false;
					score = 0;
					score2 = 0;
					information = 0;
					repaint();
				}
				break;
			case 'Z':
				System.out.print("z");
					dir = 'R';
					isStarted = true;
					Thread.yield();
				break;
			case 'B':
				if(isStarted && !isCrushed())
					isPaused = !isPaused;
				break;
			case 'Q':snake2.setSpeed(Snake.SPEED_1);break;
			case 'W':snake2.setSpeed(Snake.SPEED_2);break;
			case 'E':snake2.setSpeed(Snake.SPEED_3);break;
			case 'R':snake2.setSpeed(Snake.SPEED_4);break;
			case 'T':snake2.setSpeed(Snake.SPEED_5);break;
			default:break;
			}
			if(isStarted && !isPaused && !isCrushed())
			{
				changeSnakeLocation();
				changeSnakeLocation2();
			}
			try{Thread.sleep(snake.getSpeed());}
			catch(InterruptedException ex){ex.printStackTrace();}
		}
	}
 
	public synchronized void changeSnakeLocation()
	{
		int xPrevious = snake.getHead().getX();
		int yPrevious = snake.getHead().getY();
 
		switch(snake.getDirection())
		{
			case Snake.DIRECTION_UP:snake.getHead().setY(yPrevious - PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_DOWN:snake.getHead().setY(yPrevious + PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_LEFT:snake.getHead().setX(xPrevious - PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_RIGHT:snake.getHead().setX(xPrevious + PER_UNIT_LENGTH);break;
			default:
		}
		System.out.println("1x"+snake.getHead().getX());
		System.out.println("1y"+snake.getHead().getY());

		if(isEncountered())
		{
			score++;
			snake.getBody().addFirst(new Dot(xPrevious, yPrevious));
		}
		else
		{
			snake.getBody().addFirst(new Dot(xPrevious, yPrevious));
			snake.getBody().removeLast();
		}
		repaint();
		requestFocus();
	}
	
	public synchronized void changeSnakeLocation2()
	{
		int xPrevious2 = snake2.getHead().getX();
		int yPrevious2 = snake2.getHead().getY();
 
		//��s�D�Y��m
		switch(snake2.getDirection())
		{
			case Snake.DIRECTION_UP:snake2.getHead().setY(yPrevious2 - PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_DOWN:snake2.getHead().setY(yPrevious2 + PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_LEFT:snake2.getHead().setX(xPrevious2 - PER_UNIT_LENGTH);break;
			case Snake.DIRECTION_RIGHT:snake2.getHead().setX(xPrevious2 + PER_UNIT_LENGTH);break;
			default:
		}
		System.out.println("2x"+snake2.getHead().getX());
		System.out.println("2y"+snake2.getHead().getY());
		//�ھڳD�Y��T�M�O�_�Y���I�ߧ�s�D����m
		if(isEncountered2())
		{
			score2++;
			snake2.getBody().addFirst(new Dot(xPrevious2, yPrevious2));
		}
		else
		{
			snake2.getBody().addFirst(new Dot(xPrevious2, yPrevious2));
			snake2.getBody().removeLast();
		}
		//���e������J�I
		repaint();
		requestFocus();
	}
 
	public boolean isEncountered()
	{//�P�_�O�_�Y���I��
		if(snake.getHead().getX() == dessert.getX() 
		&& snake.getHead().getY() == dessert.getY()){
			return true;
		}
		else{return false;}
	}
	
	public boolean isEncountered2()
	{//�P�_�O�_�Y���I��
		if(snake2.getHead().getX() == dessert.getX() 
		&& snake2.getHead().getY() == dessert.getY()){
			return true;
		}
		else{return false;}
	}
 
	public boolean isCrushed()
	{//�P�_�C���O�_����
		//���P�_�O�_�IĲ���
		boolean isCrushedByBorder = snake.getHead().getX() >= getWidth() / 2 + HALF_SIDE  
		|| snake.getHead().getX() < getWidth() / 2 - HALF_SIDE 
		|| snake.getHead().getY() >= getHeight() / 2 + HALF_SIDE 
		|| snake.getHead().getY() < getHeight() / 2 - HALF_SIDE
		|| snake2.getHead().getX() < getWidth() / 2 - HALF_SIDE 
		|| snake2.getHead().getY() >= getHeight() / 2 + HALF_SIDE 
		|| snake2.getHead().getY() < getHeight() / 2 - HALF_SIDE
		|| snake2.getHead().getX() >= getWidth() / 2 + HALF_SIDE;
		if(isCrushedByBorder){
			information = 1;
			return true;
		}
 
		//�A�P�_�O�_�IĲ�ۨ�
		boolean isCrushedByItself = false;
		boolean isCrushedByItself2 = false;
		boolean isCrushedByItself3 = false;
		for(int i = 0;i < snake.getBody().size();i++)
		{
			if(snake.getHead().getX() == snake.getBody().get(i).getX() 
			&& snake.getHead().getY() == snake.getBody().get(i).getY() && !isCrushedByItself)
				isCrushedByItself = true;
			if(snake2.getHead().getX() == snake.getBody().get(i).getX() && snake2.getHead().getY() == snake.getBody().get(i).getY())
						isCrushedByItself3 = true;
			if(snake.getHead().getX() == snake2.getHead().getX() && snake.getHead().getY() == snake2.getHead().getY())
				isCrushedByItself3 = true;
		}
		
		for(int i = 0;i < snake2.getBody().size();i++)
		{
			if(snake2.getHead().getX() == snake2.getBody().get(i).getX() 
			&& snake2.getHead().getY() == snake2.getBody().get(i).getY() && !isCrushedByItself)
				isCrushedByItself2 = true;
			if(snake.getHead().getX() == snake2.getBody().get(i).getX() && snake.getHead().getY() == snake2.getBody().get(i).getY())
				isCrushedByItself3 = true;
		}
		
		if(isCrushedByItself)
		{
			information = 2;
			return true;
		}
		else if(isCrushedByItself2)
		{
			information = 3;
			return true;
		}
		else if(isCrushedByItself3)
		{
			information = 4;
			return true;
		}
		else{return false;}
	}
	public static int getScore(){return score;}
	public static int getScore2(){return score2;}
	public static char getdir(){return dir;}
	public static int getdesx(){return dessert.getX();}
	public static int getdesy(){return dessert.getY();}
	public static int getInformation(){return information;}
}
 
class InformationPanel extends JPanel implements Runnable
{
	Box box = Box.createVerticalBox();//�Ыؤ@�ӫ������l�e��
	JLabel[] help = new JLabel[4];//���?����T
	JLabel score = new JLabel("score�G");//��ܤ���
	JLabel score2 = new JLabel("score�G");//��ܤ���
	JLabel show = new JLabel();//��ܸ�T
	static JTextArea textA;
	JTextField textF;
	String msg;
	
	public InformationPanel()
	{
		//��l�ư}�C
		for(int i = 0;i < help.length;i++)
			help[i] = new JLabel();
		
		//�t�m�r��
		Font font1 = new Font("DialogInput", Font.BOLD, 20);
		Font font2 = new Font("DialogInput", Font.BOLD + Font.ITALIC, 25);
		for(int i = 0;i < help.length;i++)
			help[i].setFont(font1);
		score.setFont(font2);
		score.setForeground(Color.ORANGE);
		score2.setFont(font2);
		score2.setForeground(Color.ORANGE);
		show.setFont(font2);
		show.setForeground(Color.RED);
 
		//help[0].setText("���i��C��j�G�Ұʦ��A���s�u(Connect)");
		help[0].setText("Enter�G�}�l�C��/���s�}�l");
		help[1].setText("��V��G���ʳD");
		help[2].setText("�Ů���G�Ȱ��C��");
		help[3].setText("����F1-F5�G�ո`�D�t(F1:�̧֡FF5:�̺C)");
		
		
		//�t�m��T���O
		add(box);
		box.add(Box.createVerticalStrut(150));
		for(int i = 0;i < help.length;i++)
		{
			box.add(help[i]);
			box.add(Box.createVerticalStrut(10));
		}
		box.add(Box.createVerticalStrut(10));
		box.add(score);
		box.add(Box.createVerticalStrut(10));
		box.add(score2);
		box.add(Box.createVerticalStrut(50));
		box.add(show);


	}
 
	public void run()
	{//��s�C����T
		while(true)
		{
			String string1 = "snack one score�G" + Integer.toString(GamePanel.getScore());
			score.setText(string1);
			String string12 = "snack two score�G" + Integer.toString(GamePanel.getScore2());
			score2.setText(string12);
			String string2 = null;
			switch(GamePanel.getInformation()){
				case 0:break;
				case 1:string2 = "�A��������F�I";break;
				case 2:string2 = "snake one �۱��F�I";break;
				case 3:string2 = "snake two �۱��F�I";break;
				case 4:string2 = "�D�����F��!!!!!";break;
				default:
			}
			show.setText(string2);
		}
	}
	public static void setString(String m)
	{
		if(textA.getText()!="")
			textA.setText(textA.getText()+m);
    	else 
    		textA.setText(m);
	}
}
 
class connect implements Runnable
{
	static char dir;
	static char dirt;
	static int dx;
	static int dy;
	String msg;
	Socket sk;   // ���A��ServerSocket
    Socket client;      // �������Ȥ��Socket
    BufferedReader theInputStream;  // Ū���Ȥ�ݸ�ƪ��w�İ�
    PrintStream theOutputStream; 
    
	public connect(){dir = 'R';}
	
	public void run()
	{//��s�C����T
		
	  try 
	  {
	   sk = new Socket("127.0.0.1",5050);    //�إ�Socket����A�ó]�wip(127.0.0.1�O�ۤv���D��)�M��
	   System.out.println("�w�s�uServer5050");
	   theInputStream = new BufferedReader(new InputStreamReader(sk.getInputStream()));
	   theOutputStream = new PrintStream(sk.getOutputStream());
	   while(sk.isConnected())
	   {
		   msg = theInputStream.readLine();
		    if(msg==null){ break;}
		    dirt = msg.charAt(0);
		    if(dirt == 'U'||dirt == 'Z'||dirt == 'D'||dirt == 'L'||dirt == 'R'||dirt == 'E'||dirt == 'B')
		    	dir = dirt;
		    Thread.yield();
	   }
	   
	  }catch (IOException e) { e.printStackTrace(); }
	}
	
	public static char getdir(){return dir;}
	
}

class connect2 implements Runnable
{

	String msg;
	Socket sk;   // ���A��ServerSocket
    Socket client;      // �������Ȥ��Socket
    BufferedReader theInputStream;  // Ū���Ȥ�ݸ�ƪ��w�İ�
    PrintStream theOutputStream; 
    
	public void run()
	{//��s�C����T
		
	  try 
	  {
	   sk = new Socket("127.0.0.1",5051);    //�إ�Socket����A�ó]�wip(127.0.0.1�O�ۤv���D��)�M��
	   System.out.println("�w�s�uServer5051");
	   theInputStream = new BufferedReader(new InputStreamReader(sk.getInputStream()));
	   theOutputStream = new PrintStream(sk.getOutputStream());
	   while(sk.isConnected())
	   {
		   theOutputStream.println(GamePanel.getdir()+"&"+GamePanel.getdesx()+"&"+GamePanel.getdesy());
		   theOutputStream.flush();
		   Thread.yield();
	   }
	   
	  }catch (IOException e) { e.printStackTrace();}
	}
}

class chat implements Runnable
{
	ServerSocket skt;
	String msg="";
	Socket sk;   // ���A��ServerSocket
    BufferedReader theInputStream;  // Ū���Ȥ�ݸ�ƪ��w�İ�
    PrintStream theOutputStream; 
    static int sen = 0;
    static String sendsmg="";
	public void run()
	{//��s�C����T
	  try 
	  {
	   skt = new ServerSocket(5055);  //�إ�ServerSocket����A�ó]�w��5050
	   System.out.println("waiting for client");   
	   sk = skt.accept();   
	   //Client���X�ШD�Aaccept()�|�Ǧ^�@��Socket����A����sk���V��
	   System.out.println("Clinet connected5055");
	   theInputStream = new BufferedReader(new InputStreamReader(sk.getInputStream()));
	   theOutputStream = new PrintStream(sk.getOutputStream());
	   while(sk.isConnected())
	   {
		   if(sen==1) 
		   {
			theOutputStream.println(sendsmg);
			theOutputStream.flush();
			sen=0;
			Thread.yield();
		   }
		   System.out.println(msg);
	   }
	  }catch (IOException e) { e.printStackTrace(); }
	}
	public static void sendmsg(String msg)
	{
		System.out.print(msg);
		sendsmg = msg;
		sen=1;
	}
}

class chat2 implements Runnable
{
	ServerSocket skt;
	String msg="";
	Socket sk;   // ���A��ServerSocket
    BufferedReader theInputStream;  // Ū���Ȥ�ݸ�ƪ��w�İ�
    PrintStream theOutputStream; 
    static int sen = 0;
    static String sendsmg="";
	public void run()
	{//��s�C����T
	  try 
	  {
	   skt = new ServerSocket(5056);  //�إ�ServerSocket����A�ó]�w��5050
	   System.out.println("waiting for client");   
	   sk = skt.accept();   
	   //Client���X�ШD�Aaccept()�|�Ǧ^�@��Socket����A����sk���V��
	   System.out.println("Clinet connected5056");
	   theInputStream = new BufferedReader(new InputStreamReader(sk.getInputStream()));
	   theOutputStream = new PrintStream(sk.getOutputStream());
	   while(sk.isConnected())
	   {
		   msg = theInputStream.readLine();
		   System.out.println(msg);
		    if(msg==null){ break;}
		    else 
			{
		    	if(msg != "")
		    		InformationPanel.setString(msg);
		    	msg="";
		    	Thread.yield();
		    }
	   }
	  }catch (IOException e) {e.printStackTrace();}

	}
}
class Snake
{//�D��
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	public static final int DIRECTION_LEFT = 3;
	public static final int DIRECTION_RIGHT = 4;
	public static final int SPEED_1 = 300;
	public static final int SPEED_2 = 200;
	public static final int SPEED_3 = 150;
	public static final int SPEED_4 = 100;
	public static final int SPEED_5 = 30;
	int direction = DIRECTION_RIGHT;
	int speed = SPEED_3;
	Dot head = new Dot();
	LinkedList<Dot> body = new LinkedList<Dot>();
 
	public Snake(){}
	public Dot getHead(){return head;}
	public LinkedList<Dot> getBody(){return body;}
	public int getDirection(){return direction;}
	public int getSpeed(){return speed;}
	public void setBody(LinkedList<Dot> body){this.body = body;}
	public void setDirection(int direction){this.direction = direction;}
	public void setSpeed(int speed){this.speed = speed;}
}
 
class Dot
{//�I��
	int x = 0;
	int y = 0;
	public Dot(){}
	public Dot(int x, int y){this.x = x;this.y = y;}
	public int getX(){return x;}
	public int getY(){return y;}
	public void setX(int x){this.x = x;}
	public void setY(int y){this.y = y;}
}