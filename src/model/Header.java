package model;

import java.io.Serializable;

public class Header implements Serializable 
{	/**
	 * 
	 */
	private static final long serialVersionUID = -6053496373639637028L;
	private String filename;
	private long filesize;
	private int packetsize;

	public Header() 
	{
	}	

	public int getPacketsize() {
		return packetsize;
	}

	public void setPacketsize(int packetsize) {
		this.packetsize = packetsize;
	}
	
	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public long getFilesize() {
		return filesize;
	}


	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
}
