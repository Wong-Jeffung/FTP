import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
    String cmd = "";//从标准输入流接收字符串，放在cmd中
	Socket s;//
	BufferedReader br;//传输数据
	DataOutputStream dos;//
	DataInputStream dis;
	public Client (String serName){
		
		try {
			
		boolean flag = true;
		
		while(flag){
			s = new Socket(serName ,2466);
		    //从标准IO中获得输入的命令
			System.out.println("连接成功，请输入命令(get,put,cd,pwd,dir,quit)");
			br = new BufferedReader(new InputStreamReader(System.in));//将输入的命令放到BufferedReader类变量br中
			cmd = br.readLine();//将br的内容读出放到cmd中
			
		    //发送命令
			DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(s.getOutputStream()));//向服务器发送相关命令,如get,put,cd,dir
			//将字符集转换成字节序列
			byte []buf = cmd.getBytes();//使用平台默认的字符集将此 String 解码为字节序列，并将结果存储到一个新的字节数组中。 
			//返回：结果字节数组
			
			dos.writeUTF(cmd);//用UTF编码将一个字符串写入基础输入流，即写到服务端的输入流
			dos.flush();//清空此数据输出流
			dos.close();//关闭输出流
			s.close();//关闭socket
		
			//进行对于应得操作：
			if(cmd.equals("get"))
				get(serName);
			else if(cmd.equals("put"))
				put(serName);
			else if(cmd.equals("cd"))
				cd (serName);
			else if(cmd.equals("pwd"))
				pwd(serName);
			else if(cmd.equals("dir"))
				dir(serName);
			else if(cmd.equals("quit")){
				flag = false;
				System.out.println("退出成功");
			}
		}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    		if(s != null){
    			try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    		
    	}
		
		
	}
	

	//下载
	public void get(String serName){
		System.out.println("请输入目录：");
		try{
			//建立连接：从标准输入中获得要下载的文件的路径放在br
			Socket s = new Socket(serName,2466);
			br = new BufferedReader(new InputStreamReader(System.in));
			String downFile = br.readLine();//读取一个文本行。通过下列字符之一即可认为某行已终止，换行 ('\n')、回车 ('\r') 或回车后直接跟着换行。
	        dos = new DataOutputStream(
				new BufferedOutputStream(s.getOutputStream()));
	        dos.writeUTF(downFile);
	        dos.flush();
	    
		
	        //下载文件
	        dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
    
	        int bufferSize = 8192;   
	        // 缓冲区   
	        byte[] buf = new byte[bufferSize];   
	        int passedlen = 0;   
        
	        long len = 0;   
	        String savePath = "D:\\save";
	        // 获取文件名称   路径加文件名
	        savePath =  savePath+File.separator+dis.readUTF();   
	        //在本地路径建一个数据流
	        DataOutputStream fileOut = new DataOutputStream(   
                new BufferedOutputStream( new FileOutputStream(savePath)));   
	        // 获取文件长度   
	        len = dis.readLong();   

	        System.out.println("文件的长度为:" + len + "字节");   
	        System.out.println("开始接收文件!");   

	        // 获取文件   
	        while (true) {   
            int read = 0;   
            if (dis != null) {   
                read = dis.read(buf);   
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
        
		}catch(IOException e ){
			e.printStackTrace();
		}
		
		try{
			dis.close();
			dos.close();
			s.close();
		}catch(IOException e ){
			e.printStackTrace();
		}
		
		
		
	}
	public void put(String serName){
		System.out.println("请输入要上传的文件的路径:");
		Socket s = null;
		try{
			s = new Socket (serName,2466);
			
			//从标准输入流输入要传输的文件在本地的的路径：
			br = new BufferedReader(new InputStreamReader(System.in));
		    String upFile = br.readLine();//将该要上传的文件放到upFile
		
		    //传输文件：
			   
			dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));//建立一个输出流对象
	  	
	  		File file = new File(upFile);//定义File对象upFile
    		dos.writeUTF(file.getName());//  file.getname  ()  返回由此抽象路径名表示的文件或目录的名称。
	    		
	    	dos.flush();//清空输出流
	    	dos.writeLong(file.length());//向输出流写入一个long类型的数据
	    	dos.flush();
	    		
	    	dis = new DataInputStream(new BufferedInputStream(new FileInputStream(upFile)));
	    	
	   		int BUFSIZE = 8192;
	   		byte [] buf = new byte[BUFSIZE];
	   		int passedlen = 0; 
	   		
	   		System.out.println("文件的长度为:" + file.length() + "字节");   
	        System.out.println("开始上传文件!");
	        
	   		while(true){
	    		int read = 0;
	    		if(dis != null){
	    			read = dis.read(buf);
	    			passedlen += read;
	   			}else{
	   				System.out.println("no file founded!");
	   				break;
	   			}
    			if (read == -1){
    				break;
	    		}
    			System.out.println("文件上传了" + (passedlen * 100 / file.length()) + "%");   
	    		dos.write(buf, 0, read);
	    	}
	    	dos.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				dis.close();
				dos.close();
			    s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public void cd(String serName){
		System.out.println("请输入要更改到的路径:");
		try{
			Socket s = new Socket(serName,2466);
			br = new BufferedReader(new InputStreamReader(System.in));
			String changedDir = br.readLine();
				
			//发送命令
			DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(s.getOutputStream()));
			dos.writeUTF(changedDir);
			dos.flush();
			dos.close();
			s.close();
				
		}catch(IOException e){
				e.printStackTrace();	
			}
	}
	
	public void pwd(String serName) {
		System.out.println("当前目录为：");
		try {
			s = new Socket(serName,2466);
			dis = new DataInputStream(new BufferedInputStream(s
					.getInputStream()));//定义数据输入流
			int BUFSIZE = 8912;
			byte[] buf = new byte[BUFSIZE];
			
			while (true) {	
				int data = 0;//read用来保存从文件中读取过来的数据
				if (dis != null) {
				data= dis.read(buf);//遇到输入流的末尾，返回-1
				String str = new String(buf);
				System.out.println(str);
				break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} 
	
	
	public void dir (String serName){
		
		System.out.println("以下是目录：");
		try {
			
			s = new Socket(serName,2466);//连接服务器
			dis = new DataInputStream(new BufferedInputStream(s
					.getInputStream()));//定义数据输入流

			int BUFSIZE = 8912;
			byte[] buf = new byte[BUFSIZE];
			while (true) {
				
				int data = 0;//read用来保存从文件中读取过来的数据
				if (dis != null) {
					data= dis.read(buf);//遇到输入流的末尾，返回-1
					String str = new String(buf);
					System.out.println(str);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void main(String[] args) {
		new Client("127.0.0.1");

	}

}

