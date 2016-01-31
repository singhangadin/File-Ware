package threads;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import model.Header;

public class RecievingThread extends Thread
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