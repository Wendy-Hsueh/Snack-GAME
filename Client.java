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
	static GamePanel p1 = new GamePanel();//創建遊戲面板
	static InformationPanel p2 = new InformationPanel();//創建資訊面板
	static connect p3 = new connect();//創建遊戲面板
	static connect2 p4 = new connect2();//創建遊戲面板
	static chat p5 = new chat();//創建遊戲面板
	static chat2 p6 = new chat2();//創建遊戲面板
	
	public Client()
	{//配置框架的佈局
		setLayout(new BorderLayout());
		add(p1,BorderLayout.CENTER);
		add(p2,BorderLayout.EAST);
	}
 
	public static void main(String[] args)
	{
		JFrame frame = new Client();//新建框架
 
		//配置框架
		frame.setTitle("Snake Game");
		frame.setSize(1100, 800);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
 
		//新建執行緒
		
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
	public static final int PER_UNIT_LENGTH = 20;//單位長度
	public static final int MULTIPLE = 15;//倍數
	public static final int HALF_SIDE = MULTIPLE * PER_UNIT_LENGTH;//遊戲邊框的一半長 = 倍數 * 單位長度
 
	private boolean isFirstRun = true;//判斷是否需要初始化
	private boolean isStarted = false;//判斷是否開始
	private boolean isPaused = false;//判斷是否暫停
	private static int score2 = 0;//遊戲分數
	private static int score = 0;//遊戲分數
	private static int information = 0;//傳遞遊戲資訊
	private static char dir = 'R';//遊戲分數
	
	private static Snake snake = new Snake();//新建一條蛇
	private static Snake snake2 = new Snake();//新建一條蛇
	private static Dot dessert = new Dot();//新建一個點心
	private static boolean isConnet = false;//預設為未連線狀態
 
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
 
		//獲取中點座標
		int xCentre = getWidth() / 2;
		int yCentre = getHeight() / 2;

		//獲取一個隨機點座標
		int xRandom, yRandom;
 
		if(isFirstRun)
		{
			isFirstRun = false;
 
			//初始化遊戲邊框
			g.drawRect(xCentre - HALF_SIDE, yCentre - HALF_SIDE, HALF_SIDE * 2, HALF_SIDE * 2);
 
			//初始化蛇頭
			snake.getHead().setX(xCentre+80);
			snake.getHead().setY(yCentre);
			g.setColor(Color.ORANGE);
			g.fillRect(snake.getHead().getX(), snake.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
			
			snake2.getHead().setX(xCentre-80);
			snake2.getHead().setY(yCentre);
			g.setColor(Color.BLUE);
			g.fillRect(snake2.getHead().getX(), snake2.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
 
			//初始化點心
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
			//繪畫遊戲邊框
			g.drawRect(xCentre - HALF_SIDE, yCentre - HALF_SIDE, HALF_SIDE * 2, HALF_SIDE * 2);
 
			//繪畫蛇身
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
 
			//繪畫蛇頭
			g.setColor(Color.ORANGE);
			g.fillRect(snake.getHead().getX(), snake.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
			
			g.setColor(Color.BLUE);
			g.fillRect(snake2.getHead().getX(), snake2.getHead().getY(), PER_UNIT_LENGTH, PER_UNIT_LENGTH);
 
			//如果蛇吃到點心，則更新點心
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
 
			//如果遊戲結束，則追加繪畫GAME OVER
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
		//配置面板屬性
		setFocusable(true);
		setFont(new Font("Californian FB", Font.BOLD, 80));
 
		//註冊鍵盤監聽器
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
						{//如果遊戲結束，則重置資料重新開始遊戲
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
	{//控制蛇自動前進
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
				{//如果遊戲結束，則重置資料重新開始遊戲
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
 
		//更新蛇頭位置
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
		//根據蛇頭資訊和是否吃到點心更新蛇身位置
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
		//重畫並獲取焦點
		repaint();
		requestFocus();
	}
 
	public boolean isEncountered()
	{//判斷是否吃到點心
		if(snake.getHead().getX() == dessert.getX() 
		&& snake.getHead().getY() == dessert.getY()){
			return true;
		}
		else{return false;}
	}
	
	public boolean isEncountered2()
	{//判斷是否吃到點心
		if(snake2.getHead().getX() == dessert.getX() 
		&& snake2.getHead().getY() == dessert.getY()){
			return true;
		}
		else{return false;}
	}
 
	public boolean isCrushed()
	{//判斷遊戲是否結束
		//先判斷是否碰觸邊框
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
 
		//再判斷是否碰觸自身
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
	Box box = Box.createVerticalBox();//創建一個垂直盒子容器
	JLabel[] help = new JLabel[4];//顯示?明資訊
	JLabel score = new JLabel("score：");//顯示分數
	JLabel score2 = new JLabel("score：");//顯示分數
	JLabel show = new JLabel();//顯示資訊
	static JTextArea textA;
	JTextField textF;
	String msg;
	
	public InformationPanel()
	{
		//初始化陣列
		for(int i = 0;i < help.length;i++)
			help[i] = new JLabel();
		
		//配置字體
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
 
		//help[0].setText("先【按C鍵】：啟動伺服器連線(Connect)");
		help[0].setText("Enter：開始遊戲/重新開始");
		help[1].setText("方向鍵：移動蛇");
		help[2].setText("空格鍵：暫停遊戲");
		help[3].setText("按鍵F1-F5：調節蛇速(F1:最快；F5:最慢)");
		
		
		//配置資訊面板
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
	{//更新遊戲資訊
		while(true)
		{
			String string1 = "snack one score：" + Integer.toString(GamePanel.getScore());
			score.setText(string1);
			String string12 = "snack two score：" + Integer.toString(GamePanel.getScore2());
			score2.setText(string12);
			String string2 = null;
			switch(GamePanel.getInformation()){
				case 0:break;
				case 1:string2 = "你撞穿牆壁了！";break;
				case 2:string2 = "snake one 自殺了！";break;
				case 3:string2 = "snake two 自殺了！";break;
				case 4:string2 = "蛇互撞了啦!!!!!";break;
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
	Socket sk;   // 伺服端ServerSocket
    Socket client;      // 接收的客戶端Socket
    BufferedReader theInputStream;  // 讀取客戶端資料的緩衝區
    PrintStream theOutputStream; 
    
	public connect(){dir = 'R';}
	
	public void run()
	{//更新遊戲資訊
		
	  try 
	  {
	   sk = new Socket("127.0.0.1",5050);    //建立Socket物件，並設定ip(127.0.0.1是自己的主機)和埠號
	   System.out.println("已連線Server5050");
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
	Socket sk;   // 伺服端ServerSocket
    Socket client;      // 接收的客戶端Socket
    BufferedReader theInputStream;  // 讀取客戶端資料的緩衝區
    PrintStream theOutputStream; 
    
	public void run()
	{//更新遊戲資訊
		
	  try 
	  {
	   sk = new Socket("127.0.0.1",5051);    //建立Socket物件，並設定ip(127.0.0.1是自己的主機)和埠號
	   System.out.println("已連線Server5051");
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
	Socket sk;   // 伺服端ServerSocket
    BufferedReader theInputStream;  // 讀取客戶端資料的緩衝區
    PrintStream theOutputStream; 
    static int sen = 0;
    static String sendsmg="";
	public void run()
	{//更新遊戲資訊
	  try 
	  {
	   skt = new ServerSocket(5055);  //建立ServerSocket物件，並設定埠號5050
	   System.out.println("waiting for client");   
	   sk = skt.accept();   
	   //Client提出請求，accept()會傳回一個Socket物件，並讓sk指向它
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
	Socket sk;   // 伺服端ServerSocket
    BufferedReader theInputStream;  // 讀取客戶端資料的緩衝區
    PrintStream theOutputStream; 
    static int sen = 0;
    static String sendsmg="";
	public void run()
	{//更新遊戲資訊
	  try 
	  {
	   skt = new ServerSocket(5056);  //建立ServerSocket物件，並設定埠號5050
	   System.out.println("waiting for client");   
	   sk = skt.accept();   
	   //Client提出請求，accept()會傳回一個Socket物件，並讓sk指向它
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
{//蛇類
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
{//點類
	int x = 0;
	int y = 0;
	public Dot(){}
	public Dot(int x, int y){this.x = x;this.y = y;}
	public int getX(){return x;}
	public int getY(){return y;}
	public void setX(int x){this.x = x;}
	public void setY(int y){this.y = y;}
}