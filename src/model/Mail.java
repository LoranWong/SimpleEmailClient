package model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.spi.DirStateFactory.Result;

import utilitiy.MyBase64;

public class Mail {
	String subject = "";
	String from = "";
	Vector<String> to_list = new Vector<String>();
	String content = "";

	public Mail(String subject, String from, Vector<String> to_list,
			String content) {
		super();
		this.subject = subject;
		this.from = from;
		this.to_list = to_list;
		this.content = content;
	}

	public Mail(Vector<String> lines) {
		super();
		initByLines(lines);
	}

	private void initByLines(Vector<String> lines) {
		//决定当前行是否解码
		boolean decodeWithBase64 = false;
		//排除前一行的干扰
		boolean isPreLine = false;
		//是否已经收集完内容  不要html
		boolean hasContent = false;
		for (int i = 0; i < lines.size(); i++) {
			String buf = lines.get(i);
			isPreLine = false;
			if (buf.startsWith("Return-Path:")) {
				String regex = "<(.*)>";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(buf);
				boolean cont = true;
				while (m.find()) {
					// System.out.println(m.group(1));
					if (cont) {
						to_list = new Vector<>();
						to_list.addElement(m.group(1));
						cont = false;
					}

				}
			}else if (buf.startsWith("Subject:")) {
				boolean isUTF8 = false;
				String regex = "=\\?(?i)UTF-8\\?B\\?(.*)\\?=";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(buf);
				while (m.find()) {
					isUTF8 = true;
					subject = MyBase64.getFromBASE64(m.group(1));
				}
				if(!isUTF8) subject = buf.substring(8);

			}else if (buf.startsWith("From:")) {
				from = buf.substring(5);
			}else if (buf.endsWith("base64")) {
				if(!hasContent){
					decodeWithBase64 = true;
					isPreLine = true;
				}
				hasContent = true;
			}else if (buf.startsWith("--")) {
				decodeWithBase64 = false;
			}
			
			if (decodeWithBase64 && !isPreLine) {
				content += MyBase64.getFromBASE64(buf);
			}
			
		}

	}

	public String getDataString() {
		String to = "";
		for (int i = 0; i < to_list.size(); i++) {
			to += to_list.get(i) + ";";
		}
		String result = "From:" + from + "\r\n" + "To:" + to + "\r\n"
				+ "Subject:" + subject + "\r\n" + content + "\r\n";
		return result;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Vector<String> getTo_list() {
		return to_list;
	}

	public void setTo_list(Vector<String> to_list) {
		this.to_list = to_list;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		String to = "";
		for (int i = 0; i < to_list.size(); i++) {
			to+= to_list.get(i)+";";
		}
		return "主题:"+subject+"\r\n"+
				"来自:"+from+"\r\n"+
				"回复:"+to+"\r\n"+
				"正文:"+content;
	}
	
	

}
