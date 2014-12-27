package view;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.Date;
import java.util.Vector;

import javax.swing.JTextField;

import utilitiy.MyBase64;
import utilitiy.MyUtility;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;

import model.Mail;
import controller.PopController;
import controller.SmtpController;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private JPanel contentPane;
	private JTextField addrs_text;
	private JTextField subject_text;
	private JTextArea textArea;
	private JScrollPane scrollPane_text;
	private JScrollPane scrollPane_table;
	private JTable table;
	private JButton btn_back;
	private JButton btn_save;
	private JButton btn_delete;
	private JButton btn_reply;
	private JEditorPane textArea_emailbox;
	private JButton btnSend;
	private String addr;
	private String pass;
	private String smtp_addr;
	private String pop_addr;
	
	private int curMailIndex;
	private Vector<Mail> mails = new Vector<Mail>();
	private PopController popController;
	private LoginDialog loginDialog;
	/**
	 * Create the frame.
	 */
	public MainFrame(String addr, String pass, String smtp_addr, String pop_addr,LoginDialog loginDialog) {
		setResizable(false);
		this.loginDialog = loginDialog;
		this.addr = addr;
		this.pass = pass;
		this.smtp_addr = smtp_addr;
		this.pop_addr = pop_addr;
		setTitle("简易邮件客户端");
		// 统一字体
		Font font = new Font("微软雅黑", Font.PLAIN, 17);
		MyUtility.InitGlobalFont(font);

		setType(Type.POPUP);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 837, 585);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBounds(0, 0, 831, 555);
		// tabbedPane.set
		contentPane.add(tabbedPane);

		JPanel panel_write = new JPanel();
		panel_write.setBounds(52, 239, 126, 96);

		JPanel panel_box = new JPanel();
		panel_box.setBounds(242, 203, 74, 154);

		tabbedPane.addTab("写信", new ImageIcon("write.png"), panel_write);
		panel_write.setLayout(null);

		JLabel label = new JLabel("收信人地址：");
		label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label.setBounds(26, 13, 129, 18);
		panel_write.add(label);

		addrs_text = new JTextField();
		addrs_text.setToolTipText("多个地址请用英文分号间隔");
		addrs_text.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		addrs_text.setBounds(137, 12, 412, 24);
		panel_write.add(addrs_text);
		addrs_text.setColumns(10);

		JLabel label_1 = new JLabel("主  题 ：");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		label_1.setBounds(26, 44, 77, 18);
		panel_write.add(label_1);

		subject_text = new JTextField();
		subject_text.setToolTipText("");
		subject_text.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		subject_text.setColumns(10);
		subject_text.setBounds(137, 44, 412, 24);
		panel_write.add(subject_text);

		textArea = new JTextArea();
		textArea.setBounds(26, 81, 688, 412);
		panel_write.add(textArea);

		btnSend = new JButton("发送");
		btnSend.setActionCommand("Send");
		btnSend.addActionListener(listener);
		btnSend.setBounds(601, 510, 113, 27);
		panel_write.add(btnSend);
		tabbedPane.addTab("收信", new ImageIcon("box.png"), panel_box);
		panel_box.setLayout(null);

		textArea_emailbox = new JEditorPane();
		textArea_emailbox.setBounds(96, 0, 578, 500);

		scrollPane_text = new JScrollPane(textArea_emailbox);
		scrollPane_text.setBounds(0, 0, 777, 500);

		panel_box.add(scrollPane_text);

		btn_back = new JButton("返回");
		btn_back.setActionCommand("back");
		btn_back.addActionListener(listener);
		btn_back.setBounds(632, 513, 93, 27);
		panel_box.add(btn_back);

		btn_save = new JButton("储存");
		btn_save.setActionCommand("save");
		btn_save.addActionListener(listener);
		btn_save.setBounds(311, 513, 93, 27);
		panel_box.add(btn_save);

		btn_delete = new JButton("删除");
		btn_delete.setActionCommand("delete");
		btn_delete.addActionListener(listener);
		btn_delete.setBounds(525, 513, 93, 27);
		panel_box.add(btn_delete);

		btn_reply = new JButton("回复");
		btn_reply.setActionCommand("reply");
		btn_reply.addActionListener(listener);
		btn_reply.setBounds(418, 513, 93, 27);
		panel_box.add(btn_reply);

		scrollPane_table = new JScrollPane();

		scrollPane_table.setBounds(0, 0, 777, 550);
		panel_box.add(scrollPane_table);

		table = new JTable();
		scrollPane_table.setViewportView(table);

		setTableVisible();
		new Thread(new Runnable() {
			@Override
			public void run() {
				initPop();
			}
		}).start();

	}

	private void setEmailContentVisible() {
		btn_back.setVisible(true);
		btn_save.setVisible(true);
		btn_delete.setVisible(true);
		btn_reply.setVisible(true);
		scrollPane_text.setVisible(true);
		scrollPane_table.setVisible(false);
	}

	private void setTableVisible() {
		btn_back.setVisible(false);
		btn_save.setVisible(false);
		btn_delete.setVisible(false);
		btn_reply.setVisible(false);
		scrollPane_text.setVisible(false);
		scrollPane_table.setVisible(true);
	}

	/**
	 * 收取邮件
	 */
	private void initPop() {
		popController = new PopController(pop_addr, addr, pass , MainFrame.this);
		int count = popController.getMailCount();
		if(count == -1){
			showMyDialog("登陆失败!");
			this.setVisible(false);
		}else {
			loginDialog.setVisible(false);
			this.setVisible(true);
		}
		
		mails = new Vector<Mail>();
		Mail mail;
		for (int i = 1; i < count + 1; i++) {
			mail = new Mail(popController.getItemString(i + ""));
			mails.add(mail);
		}

		// 插入表格
		TableModel model = new TableModel() {

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex == 0) {
					return mails.get(rowIndex).getTo_list().size() > 0 ? mails
							.get(rowIndex).getTo_list().get(0) : "匿名";
				}
				if (columnIndex == 1)
					return mails.get(rowIndex).getSubject();
				return null;
			}

			@Override
			public int getRowCount() {
				return mails.size();
			}

			@Override
			public String getColumnName(int columnIndex) {
				switch (columnIndex) {
				case 0:
					return "发件人";
				case 1:
					return "标题";
				default:
					break;
				}
				return null;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public void addTableModelListener(TableModelListener l) {

			}
		};

		table.setModel(model);
		table.setRowHeight(35);
		table.addMouseListener(new MouseAdapter() {
			/**
			 * 鼠标点击事件
			 */
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2) {
					int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); // 获得行位置
					//System.out.println("双击鼠标第" + row + "行");
					curMailIndex = row ;
					textArea_emailbox.setText(mails.get(row).toString());
					setEmailContentVisible();
				}
			}

		});
		//TODO dele

	}

	private ActionListener listener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Send":
				doSend();

				break;
			case "back":
				setTableVisible();
				break;
			case "reply":
				doReply();
				break;
			case "delete":
				doDelete();
				break;
			case "save":
				doSave();
				break;
			//TODO
			default:
				break;
			}

		}
	};


	protected void initSmtpAndSend() {
		
		SmtpController smtpController = new SmtpController(smtp_addr, addr,
				pass);

		Vector<String> to_list = MyUtility.getTo_listFromString(addrs_text.getText());
		
		Mail mail = new Mail(subject_text.getText(), addr, to_list,textArea.getText());
		String message = smtpController.send(mail) ? "发送成功" : "发送失败";
		smtpController.quitSmtpServer();
		JOptionPane.showMessageDialog(MainFrame.this, message);
	}

	protected void doSave() {
		String fileName = "邮件_"+mails.get(curMailIndex).getSubject() + System.currentTimeMillis() +".txt";
		File file = new File(fileName);
		try {
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(mails.get(curMailIndex).getDataString().getBytes());
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		showMyDialog("以保存到文件:"+file.getAbsolutePath());
		
	}

	protected void doSend() {
		btnSend.setEnabled(false);
		btnSend.setText("请稍候..");
		new Thread(new Runnable() {
			@Override
			public void run() {
				initSmtpAndSend();
				enableBtn();
			}
		}).start();
	}

	protected void doReply() {
		if(mails.get(curMailIndex).getTo_list().size() == 0) JOptionPane.showMessageDialog(MainFrame.this, "该用户匿名，无法回复！");
		else{
			setTableVisible();
			tabbedPane.setSelectedIndex(0);
			addrs_text.setText(MyUtility.getStringFromTo_list(mails.get(curMailIndex).getTo_list()));
			subject_text.setText("回复邮件:"+mails.get(curMailIndex).getSubject());
		}
	}

	protected void doDelete() {
		if(popController.deleteItem(curMailIndex+"")){
			showMyDialog("删除成功！");
			setTableVisible();
			//刷新数据
			initPop();
		}else {
			showMyDialog("删除失败！");
		}
	}

	private void enableBtn() {
		btnSend.setEnabled(true);
		btnSend.setText("发送");
	}
	
	public void showMyDialog(String msg) {
		JOptionPane.showMessageDialog(MainFrame.this, msg);
	}

}
