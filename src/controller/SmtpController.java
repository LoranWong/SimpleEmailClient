package controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;

import utilitiy.MyBase64;
import model.Mail;

public class SmtpController {
	final int SMTP_PORT = 25;
	String smtp_server = "smtp.qq.com";
	String my_email_addr = "";
	String my_email_pass = "";
	Socket smtp;
	BufferedReader smtp_in;
	PrintWriter smpt_out;
	
	public SmtpController(String smtp_server, String my_email_addr,
			String my_email_pass) {
		super();
		this.smtp_server = smtp_server;
		this.my_email_addr = my_email_addr;
		this.my_email_pass = my_email_pass;

		login2SmtpServer();

	}
	

/**
 * 执行认证到服务器
 */
	public void login2SmtpServer(){
		try {
			smtp = new Socket(smtp_server , SMTP_PORT);
			smtp_in = new BufferedReader(new InputStreamReader(smtp.getInputStream()));
			smpt_out = new PrintWriter(smtp.getOutputStream());
			String res = smtp_in . readLine();
			System.out.println("receive ---> " + res);
			//HELO
			sendCommandAndResultCheck("HELO "+my_email_addr);
			//AUTH
			sendCommandAndResultCheck("auth login");
			sendCommandAndResultCheck(MyBase64.getBASE64(my_email_addr));
			sendCommandAndResultCheck(MyBase64.getBASE64(my_email_pass));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/**
 * 执行退出连接
 */
	public void quitSmtpServer(){
		try{
		//QUIT
		sendCommandAndResultCheck("QUIT");
		smtp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
/**
 * 执行发送
 * @param mail
 * @param to_list
 * @return 
 * @throws IOException
 */
	public boolean send(Mail mail){
		try{
		//MAIL FROM
		sendCommandAndResultCheck("MAIL FROM:"+my_email_addr);
		
		//RCPT TO
		for (int i = 0; i < mail.getTo_list().size(); i++) {
			sendCommandAndResultCheck("RCPT TO:" + mail.getTo_list().get(i));
		}
		
		//DATA
		sendCommandAndResultCheck("DATA");
		smpt_out.print(mail.getDataString());
		return sendCommandAndResultCheck(".");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
	}

	//发送命令并接收回应
	private boolean sendCommandAndResultCheck(String command)  throws IOException{
		smpt_out.print(command + "\r\n");
		smpt_out.flush();
		System.out.println("send ---> " + command);
		String res = smtp_in . readLine();
		System.out.println("receive ---> " + res);
		return (res.substring(0, 3).equals("250"));
	}

	
}
