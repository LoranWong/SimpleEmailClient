package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import utilitiy.MyBase64;
import view.MainFrame;

import com.sun.org.apache.bcel.internal.generic.POP;

public class PopController {
	final int POP_PORT = 110;
	String pop_server = "";
	String my_email_addr = "";
	String my_email_pass = "";
	Socket pop;
	BufferedReader pop_in;
	PrintWriter pop_out;
	MainFrame mainFrame;
	
	
	public PopController(String pop_server, String my_email_addr,
			String my_email_pass,MainFrame mainFrame) {
		super();
		this.pop_server = pop_server;
		this.my_email_addr = my_email_addr;
		this.my_email_pass = my_email_pass;
		login2PopServer();
	}
	
	
	public int getMailCount() {
		Vector<String> lines = getLines("LIST");
		System.out.println(lines.get(0));
		if(lines.size() == 0 || lines.get(0).startsWith("-ERR")){
			return -1;
		}
		return lines.size()-2;
	}
	
	public boolean deleteItem(String number){
		String line = getSingleLine("DELE "+number);
		if(line.length() == 0 || !line.startsWith("+OK")){
			return false;
		}else {
			return true;
		}
	}
	
	public Vector<String> getItemString(String number){
		return getLines("RETR "+number);
	}
	

	/**
	 * 进行多行回复命令的处理
	 * @return 
	 */
	private Vector<String> getLines(String command) {
		Vector<String> lines = new Vector<String>();
		try {
			boolean cont = true;
			String buf = null;

			// 发送命令
			pop_out.print(command + "\r\n");
			pop_out.flush();
			System.out.println("send-->" + command);
			// 读取回复
			String res = pop_in.readLine();
			lines.addElement(res);
			
			System.out.println("receive-->" + res);
			//  如果传回的回复不是 + OK ...
			if (!("+OK".equals(res.substring(0, 3)))) {
				//pop.close();
				//System.out.println("关闭？");
				return lines;
			}
			
			while (cont) {
				buf = pop_in.readLine();
				lines.addElement(buf);
				
				System.out.println("receive-->" + buf);
				// 用一行开头是单一句号结束
				if (".".equals(buf))
					cont = false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	/**
	 * 处理只有一行回复的命令
	 * 
	 * @param command
	 * @return 
	 */
	private String getSingleLine(String command) {
		String res = null;
		try {
			pop_out.print(command + "\r\n");
			pop_out.flush();
			System.out.println("send-->" + command);
			res = pop_in.readLine();
			System.out.println("receive-->" + res);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	/**
	 * 连接到服务器
	 */
	private void login2PopServer() {
		try {
			pop = new Socket(pop_server, POP_PORT);
			pop_in = new BufferedReader(new InputStreamReader(
					pop.getInputStream()));
			pop_out = new PrintWriter(pop.getOutputStream());
			// 读取登陆回复
			String res = pop_in.readLine();
			System.out.println("receive--->" + res);
			//  如果传回的回复不是 + OK ....
			if (!("+OK".equals(res.substring(0, 3)))) {
				pop.close();
				System.out.println("关闭？");
			}
			getSingleLine("USER " + my_email_addr);
			getSingleLine("PASS " + my_email_pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * quitPop方法 pop3对话结束
	 */
	public void quitPop() {
		// QUIT
		getSingleLine("QUIT");
		try {
			pop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
