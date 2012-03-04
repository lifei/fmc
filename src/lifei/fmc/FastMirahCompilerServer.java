package lifei.fmc;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * FastMirahCompiler
 * @author lifei
 */
public class FastMirahCompilerServer extends Thread {

	private static FastMirahCompilerServer server = new FastMirahCompilerServer();
	
	private ServerSocket  serversocket = null;
	
	private int port = 12580;
	
	private boolean isOnline = false;
	Socket socket = null;

	private FastMirahCompilerServer() {
		
		while (true) {
			try {
				this.serversocket =  new ServerSocket (this.port);
				break;
			} catch (IOException e) {
			}
			port ++;
		}
	}
	
	/**
	 * @return String
	 */
	public static String memory() {
		return String.format("当前虚拟机最大可用内存为:%dM\r\n当前虚拟机已占用内存:%dM\r\n当前虚拟机可用内存:%dM\r\n",
				Runtime.getRuntime().maxMemory()/1024/1024,
				Runtime.getRuntime().totalMemory()/1024/1024,
				Runtime.getRuntime().freeMemory()/1024/1024); 
	}
	
	/**
	 * 
	 */
	public static void begin() {
		server.start();
	}
	
	/**
	 * @param port
	 */
	public static void begin(int port) {
		server.start(port);
	}
	
	private void start(int port2) {
		this.port = port2;
		this.start();
	}

	/**
	 * 
	 */
	public static void logStop() {
		if(server.isOnline) {
			if(server.socket != null)
			{
				try {
					server.socket.close();
					server.isOnline = false;				
				} catch (IOException e) {
				}
			}
		}
		try {
			server.serversocket.close();
		} catch (IOException e) {
		}
		server.interrupt();
	}
	
	public void run() {
		while(!interrupted()) {
			try {
				isOnline = false;
				socket = this.serversocket.accept();
				isOnline = true;
				InputStream socketIn=socket.getInputStream();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(socketIn));
				
				OutputStream socketOut=socket.getOutputStream();
				PrintWriter writer = new PrintWriter(socketOut,true);
				writer.println("Hello, 亲爱的管理员，请输入您的接入Code：");
				
				String msg;
				
				while((msg=buffer.readLine().trim())!=null){
					
					if(new File(msg).exists()) {
						String[] argv = {"compile", msg};
						try {
							org.mirah.MirahCommand.main(argv);
						} catch(Exception e) {
							e.printStackTrace(System.err);
						}
					}              
	                
	                if(msg.equals("bye")) {
	                	writer.println("欢迎再次光临\n");
	                	socket.close();
	                    break;
	                }
	                
	                if(msg.equals("shutdown")) {
	                	writer.println("系统正在关闭...");
	                	writer.println("系统已经关闭...");
	                	this.interrupt();
	                	socket.close();
	                    break;
	                }

	                writer.println("请输入新指令:");
	            }
			} catch (IOException e) {
				try {
					if(socket != null)
						socket.close();
				} catch (IOException e1) {
				}
			}
		}
	}

}