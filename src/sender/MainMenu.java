package sender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import threads.RecievingThread;
import threads.SendingThread;

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
}
