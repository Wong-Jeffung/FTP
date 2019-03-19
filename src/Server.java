import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import java.io.File; 

public class Server {
	ServerSocket ss = null;
    String dir = "";//显示目录
	String cmd = "";
	DataOutputStream dos;
	String direcFile = "";
	File rootDirectory;
	String shareFile;
	DataInputStream dis ;
	ArrayList<File> fileArrayList = new ArrayList<File>();
	private String shareFiledirectory;
	public static void main(String[] args) {
		new Server("F:\\Eclipse Mars 2\\workspace\\test");
	}
	public Server(String shareFiledirectory){
		this.shareFiledirectory = shareFiledirectory; 
		//建立套接字
		try {
			ss  = new ServerSocket(2466);//建立提供服务器的端口号为学号后3位+2000
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try{
		
		while(true){
			
			boolean flag = true;
			
			while(flag){
				Socket s = ss.accept();//阻塞式，等待客户端请求连接
				System.out.println("已有客户端连接");
				//接受对方的要求：
				dis = new DataInputStream(new BufferedInputStream(s
						.getInputStream()));//BufferedInputStream 利用缓冲区来提高读效率，
		
				 
				//BufferedInputStream(InputStream in) 参数in指定需要被装饰的输入流
				cmd= dis.readUTF();//从dis输入流读取若干字节，把它转换为采用UTF-8字符编码的字符串,并将其放在cmd String变量里
				//UTF-8对ASCII字符采用一个字节形式的编码，对非ASCII字符则采用两个或两个以上字节形式的编码
				dis.close();//关闭输入流
				s.close();//关闭socket
				   
				System.out.println("输出读入的字符串："+cmd);
			
				//接受后放在cmd里用于判断：
				if(cmd.equals("get"))
					get();
				else if(cmd.equals("put"))
					put();
				else if(cmd.equals("cd"))
					cd();
				else if(cmd.equals("pwd"))
					pwd();
				else if(cmd.equals("dir"))
					dir();
				else if(cmd == "quit")
					flag = false;
				}	
	}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
		
		public void get(){
			Socket s = null;//连接为空
			try{
				s = ss.accept();
				//接受对方的要求的文件名：
				dis = new DataInputStream(new BufferedInputStream(s
					.getInputStream()));//
			   String filePath = dis.readUTF();//
			   System.out.println("要下载的文件路径为:"+filePath);
			   //传输文件：
			   
			   dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
	    		
	    		File file = new File(filePath);
	    		dos.writeUTF(file.getName());
	    		dos.flush();
	    		dos.writeLong(file.length());
	    		dos.flush();
	    		
	    		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
	    		
	    		int BUFSIZE = 8192;
	    		byte [] buf = new byte[BUFSIZE];
	    		
	    		while(true){
	    			int read = 0;
	    			if(dis != null){
	    				read = dis.read(buf);
	    			}else{
	    				System.out.println("no file founded!");
	    				break;
	    			}
	    			if (read == -1){
	    				break;
	    			}
	    			dos.write(buf, 0, read);
	    		}
	    		dos.flush();
			   
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					dos.close();
					dis.close();
				s.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
		}
		public void put(){
			System.out.println("put");
			Socket s = null;
			try{
				s = ss.accept();
				//下载文件
		        dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));//从客户端接收存放上传文件路径的输出流
		    
		    	int bufferSize = 8192;   
		        // 缓冲区   
		        byte[] buf = new byte[bufferSize];   
		        int passedlen = 0;   
		        
		        long len = 0;   
		        String savePath = "D:/upload";
		        // 获取文件名称             保存上传 文件的路径名
		        savePath =  savePath+File.separator+dis.readUTF();   
		        //在本地路径建一个数据流
		        DataOutputStream fileOut = new DataOutputStream(   
		                new BufferedOutputStream( new FileOutputStream(savePath)));   
		        // 获取文件长度   
		        len = dis.readLong(); //从输入流  中读取8个字节

		        System.out.println("文件的长度为:" + len + "字节");   
		        System.out.println("开始接收文件!");   

		        // 获取文件   
		        while (true) {   
		            int read = 0;   
		            if (dis != null) {   
		                read = dis.read(buf); //从输入流将数据读到缓冲区中，并将返回结果赋给read  
		            }   
		            passedlen += read;   
		            if (read == -1) {   
		                break;   
		            }   
		            System.out.println("文件接收了" + (passedlen * 100 / len) + "%");   
		            fileOut.write(buf, 0, read);   
		        }   
		        System.out.println("接收完成，文件存为" + savePath);   
		        fileOut.close();   
				dis.close();
				s.close();
			}catch(IOException e ){
				
			}
			
		}
		public void cd(){
			Socket s = null;
			try{
				s = ss.accept();
				//读取要到达的改变的目录：
				dis = new DataInputStream(new BufferedInputStream(s
					.getInputStream()));
			   shareFiledirectory = dis.readUTF();
			   System.out.println("更改后的目录为:"+shareFiledirectory);
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				
			}
			
		}
		
		public void pwd(){
			System.out.println("当前目录为:" + shareFiledirectory);
			try{
				Socket s = ss.accept();
				dos = new DataOutputStream(
					new BufferedOutputStream(s.getOutputStream()));
				byte []buf = shareFiledirectory.getBytes();
				dos.write(buf);
				dos.flush();
				dos.close();
				s.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		public void dir() throws IOException{
			rootDirectory = new File(shareFiledirectory);//shareFiledirectory表示共享文件的路径
			fileArrayList.clear();
			initFileArrayList();
			System.out.println("文件目录为:");
			for(int i =0;i<fileArrayList.size();i++){
			System.out.println(fileArrayList.get(i).getAbsolutePath());
			direcFile = direcFile+fileArrayList.get(i).getAbsolutePath()+'\n';
			}
			try{
				Socket s = ss.accept();
			dos = new DataOutputStream(
					new BufferedOutputStream(s.getOutputStream()));
			byte []buf = direcFile.getBytes();
			dos.write(buf);
			dos.flush();
			dos.close();
			s.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		public void initFileArrayList() { // 将目录下所有文件放在一个数组列表里面fileArrayList
			if (rootDirectory.isDirectory()) {
				// 遍历目录下面的文件和子目录
				File[] fileList = rootDirectory.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					// 如果是文件,添加到文件列表中
					if (fileList[i].isFile()) {
						fileArrayList.add(new File(fileList[i].getAbsolutePath()));
					}
					// 否则递归遍历子目录
					else if (fileList[i].isDirectory()) {
						fileList[i].mkdir();//
						rootDirectory = fileList[i];
						initFileArrayList();
					}

				}
			}

		}
}
