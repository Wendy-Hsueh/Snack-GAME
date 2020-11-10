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
	private static client p3 = new client();//�ЫعC�����O
	private static client2 p4 = new client2();//�ЫعC�����O
 
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

	public void run(){//��s�C����T
		try 
		{
			skt = new ServerSocket(5050);  //�إ�ServerSocket����A�ó]�w��5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();   
		   //Client���X�ШD�Aaccept()�|�Ǧ^�@��Socket����A����sk���V��
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

	public void run(){//��s�C����T
		try 
		{
			skt = new ServerSocket(5054);  //�إ�ServerSocket����A�ó]�w��5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();   
		   //Client���X�ШD�Aaccept()�|�Ǧ^�@��Socket����A����sk���V��
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
	{//��s�C����T
		try 
		{
		   skt = new ServerSocket(5051);  //�إ�ServerSocket����A�ó]�w��5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();                   //Client���X�ШD�Aaccept()�|�Ǧ^�@��Socket����A����sk���V��
		   System.out.println("Clinet connected to 5565");
		   theInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
		   theOutputStream = new PrintStream(client.getOutputStream());
		   while(client.isConnected()){                    //��Socket����b�s���ɡA�N���U������
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
	{//��s�C����T
		try 
		{
		   skt = new ServerSocket(5052);  //�إ�ServerSocket����A�ó]�w��5050
		   System.out.println("waiting for client");   
		   Socket client = skt.accept();                   //Client���X�ШD�Aaccept()�|�Ǧ^�@��Socket����A����sk���V��
		   System.out.println("Clinet connected to 5566");
		   theInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
		   theOutputStream = new PrintStream(client.getOutputStream());
		   while(client.isConnected()){                    //��Socket����b�s���ɡA�N���U������
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

