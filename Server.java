import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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

public class Server extends JFrame
{
	private static client p3 = new client();//創建遊戲面板
	private static client2 p4 = new client2();//創建遊戲面板
 
	public static void main(String[] args)
	{
		Thread thread3 = new Thread(p3);
		thread3.start();
		Thread thread4 = new Thread(p4);
		thread4.start();
		
	}
}



class client implements Runnable
{
	ServerSocket skt;
    Socket client;
	BufferedReader theInputStream;
    PrintStream theOutputStream;

	public void run(){//更新遊戲資訊
		try 
		{
			skt = new ServerSocket(5050);  //建立ServerSocket物件，並設定埠號5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();   
		   //Client提出請求，accept()會傳回一個Socket物件，並讓sk指向它
		   System.out.println("Clinet connected to 5563");
		   theInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
		   theOutputStream = new PrintStream(client.getOutputStream());
		   while(client.isConnected())
		   { 
				theOutputStream.println(client3.getdir());
			   theOutputStream.flush();
			   Thread.yield();
		   }
		  } catch (IOException e) {e.printStackTrace(); }
	}

}

class client4 implements Runnable
{
	ServerSocket skt;
    Socket client;
	BufferedReader theInputStream;
    PrintStream theOutputStream;

	public void run(){//更新遊戲資訊
		try 
		{
			skt = new ServerSocket(5054);  //建立ServerSocket物件，並設定埠號5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();   
		   //Client提出請求，accept()會傳回一個Socket物件，並讓sk指向它
		   System.out.println("Clinet connected to 5564");
		   theInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
		   theOutputStream = new PrintStream(client.getOutputStream());
		   while(client.isConnected())
		   { 
				theOutputStream.println(client2.getdir()+"&"+client2.getdx()+"&"+client2.getdy());
			   theOutputStream.flush();
			   Thread.yield();
		   	}
		  } catch (IOException e) { e.printStackTrace(); }
	}

}

class client2 implements Runnable
{
	ServerSocket skt;
    Socket client;
	private static char dir;
	private static char dirt;
	BufferedReader theInputStream;
    PrintStream theOutputStream;
    static String msg;
    
	public client2(){dir = 'R';}
 
	public void run()
	{//更新遊戲資訊
		try 
		{
		   skt = new ServerSocket(5051);  //建立ServerSocket物件，並設定埠號5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();                   //Client提出請求，accept()會傳回一個Socket物件，並讓sk指向它
		   System.out.println("Clinet connected to 5565");
		   theInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
		   theOutputStream = new PrintStream(client.getOutputStream());
		   while(client.isConnected()){                    //當Socket持續在連接時，就做下面的事
			msg = theInputStream.readLine();
		    if(msg==null){
		     break;
		    }
		    dirt = msg.charAt(0);
		    if(dirt == 'U'||dirt == 'Z'||dirt == 'D'||dirt == 'L'||dirt == 'R'||dirt == 'E'||dirt == 'B')
		    	dir = dirt;
		   	}
		   Thread.yield();
		  } catch (IOException e) {e.printStackTrace();}
	}
	
	public static char getdir(){return dir;}
	public static int getdx(){return Integer.valueOf(msg.split("&")[1]);}
	public static int getdy(){return Integer.valueOf(msg.split("&")[2]);}
}

class client3 implements Runnable
{
	ServerSocket skt;
    Socket client;
	private static char dir;
	private static char dirt;
	BufferedReader theInputStream;
    PrintStream theOutputStream;
    String msg;
    
	public client3(){dir = 'R';}
 
	public void run()
	{//更新遊戲資訊
		try 
		{
		   skt = new ServerSocket(5052);  //建立ServerSocket物件，並設定埠號5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();                   //Client提出請求，accept()會傳回一個Socket物件，並讓sk指向它
		   System.out.println("Clinet connected to 5566");
		   theInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
		   theOutputStream = new PrintStream(client.getOutputStream());
		   while(client.isConnected()){                    //當Socket持續在連接時，就做下面的事
			msg = theInputStream.readLine();
		    if(msg==null){break;}
		    dirt = msg.charAt(0);
		    if(dirt == 'U'||dirt == 'Z'||dirt == 'D'||dirt == 'L'||dirt == 'R'||dirt == 'E'||dirt == 'B')
		    	dir = dirt;
		   	}
		   Thread.yield();
		  } catch (IOException e) { e.printStackTrace();}
	}
	
	public static char getdir(){return dir;}
}

