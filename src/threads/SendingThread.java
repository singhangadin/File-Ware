package threads;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import model.Header;

public class SendingThread extends Thread
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