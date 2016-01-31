package sender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.Header;

public class MainMenu extends JFrame 
{	JButton send,recieve;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2011942966878472042L;

	public MainMenu() 
	{	setTitle("File Sharer");
		send=new JButton("Send");
		recieve=new JButton("Recieve");
		send.setBounds(70, 25, 100, 25);
		recieve.setBounds(70, 75, 100, 25);
		setBounds(0, 0, 240, 320);
		setLayout(null);
		add(send);
		add(recieve);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		send.addActionListener(new ActionListener() 
		{	@Override
			public void actionPerformed(ActionEvent e) 
			{	JFileChooser chooser=new JFileChooser();
				chooser.showOpenDialog(null);
				System.out.println(chooser.getSelectedFile());
				File file=chooser.getSelectedFile();
				if(file!=null)
				{	String IPADDRESS_PATTERN = 
					"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
					String ip=JOptionPane.showInputDialog("Enter the IP Address:");
					Pattern pattern=Pattern.compile(IPADDRESS_PATTERN);
	                Matcher matcher = pattern.matcher(ip);
	                if(matcher.matches())
	                {	System.out.println(ip);
	                	new SendingThread(file,ip);
					}
				}
			}
		});
		
		recieve.addActionListener(new ActionListener() 
		{	@Override
			public void actionPerformed(ActionEvent e) 
			{	String ip;
			    try 
			    {   Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			        out: while (interfaces.hasMoreElements()) 
			        {   NetworkInterface iface = interfaces.nextElement();
			            if (iface.isLoopback() || !iface.isUp())
			            {   continue;
			            }
			            Enumeration<InetAddress> addresses = iface.getInetAddresses();
			            while(addresses.hasMoreElements()) 
			            {   String IPADDRESS_PATTERN = 
			    				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			    				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			    				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			    				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
			            	InetAddress addr = addresses.nextElement();
			                ip = addr.getHostAddress();
			                Pattern pattern=Pattern.compile(IPADDRESS_PATTERN);
			                Matcher matcher = pattern.matcher(ip);
			                if(matcher.matches())
			                {	System.out.println(ip);
			                	new RecievingThread();
			                	break out;
			                }
			            }
			        }
			    } 
			    catch (SocketException e1) 
			    {   throw new RuntimeException(e1);
			    }
			}
		});
	}

	public static void main(String[] args) 
	{	new MainMenu();
	}
	
	class SendingThread extends Thread
	{	private String ip;
		private File file;
		
		public SendingThread(File file,String ip)
		{	this.file=file;
			this.ip=ip;
			start();
		}
	
		@Override
		public void run() 
		{	Socket sock = null;
			try 
        	{	sock=new Socket(ip,8888);
        		Header head=new Header();
        		head.setFilename(file.getName());
        		head.setFilesize(file.length());
        		head.setPacketsize(1024);
        		ObjectOutputStream oout=new ObjectOutputStream(sock.getOutputStream());
        		oout.writeObject(head);
        		BufferedInputStream buff=new BufferedInputStream(new FileInputStream(file));
        		OutputStream os=sock.getOutputStream();
        		long sent=0;
        		long size=file.length();
        		byte[] buffer = new byte[head.getPacketsize()];
        		while ((buff.read(buffer)) > 0)
        		{	sent+=buffer.length;
        			System.out.println("Sending:"+(sent/size)*100);
        			os.write(buffer, 0, buffer.length);
        		}
        		buff.close();
			} 
        	catch (IOException e1) 
        	{	e1.printStackTrace();
			}
			finally
			{	try 
				{	sock.close();
					System.out.println("Done");
				} 
				catch (IOException e) 
				{	e.printStackTrace();
				}
			}
		}
	}
	
	class RecievingThread extends Thread
	{	public RecievingThread() 
		{	start();
		}
		
		@Override
		public void run()
		{	ServerSocket rsock = null;
			try
			{	rsock=new ServerSocket(8888);
				Socket sock=rsock.accept();
				ObjectInputStream ois=new ObjectInputStream(sock.getInputStream());
				Object obj=ois.readObject();
				if (obj instanceof Header) 
				{	Header head = (Header) obj;
					long size=head.getFilesize();
					File file=new File(head.getFilename());
					InputStream in = sock.getInputStream();
					BufferedOutputStream buff=new BufferedOutputStream(new FileOutputStream(file));
					byte[] bytes=new byte[head.getPacketsize()];
					int count;
					long recieved=0;
					while((count=in.read(bytes))>0)
					{	recieved+=bytes.length;
	        			System.out.println("Recieved:"+(recieved/size)*100);
	        			buff.write(bytes,0,count);
					}
					buff.close();
				}
			}
			catch(IOException | ClassNotFoundException e)
			{	e.printStackTrace();
			}
			finally
			{	try 
				{	rsock.close();
					System.out.println("Done");
				} 
				catch (IOException |NullPointerException e) 
				{	e.printStackTrace();
				}
			}
		}
	}
}
