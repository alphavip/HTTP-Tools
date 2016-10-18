package cn.yy.window;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 运维测试工具窗体
 * 
 * @author 杨研
 * @date 2016年5月13日
 */
@SuppressWarnings("serial")
public class Main extends JFrame {
	/** 端口号输入框 */
	private JTextField txtPort;
	/** ip选择框 */
	private JComboBox<String> cmbIP;
	/** 服务名选择框 */
	private JComboBox<String> cmbService;
	/** 报文文件名选择框 */
	private JComboBox<String> cmbFileName;
	/** 退出按钮 */
	private JButton btnExit;
	/** 发送按钮 */
	private JButton btnSend;
	/** 请求报文文本域 */
	private JTextArea txtAreaReqXml;
	/** 响应报文文本域 */
	private JTextArea txtAreaRespXML;
	/** 发送请求线程 */
	private Thread thread = new Thread(new SendRequestThread());
	/** 调用结果 */
	private JTable lsResult;
	/** lsResult的列名 */
	private String[] columns = new String[] { "Result", "Code" };

	/**
	 * 程序入口
	 * 
	 * @author 杨研
	 * @date 2016年5月13日
	 * @param args
	 *            。。。。。
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// 启动UI线程
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	private Main() throws Exception {
		// 绘制窗体
		init();
		// 退出按钮监听
		btnExitListener();
		// 窗体加载事件
		windowLoadListener();
		// IP下拉选框监听
		cmbIpDoTxtPort();
		// 选择XML文件txtAreaReqXml加载内容
		txtAreaReqXml();
		// 发送按钮监听
		sendButtonListener();
	}

	/**
	 * 绘制窗体 禁止手动修改。可以使用WindowsBuilder界面工具修改
	 * 
	 * @author 杨研
	 * @date 2016年5月10日
	 * @throws Exception
	 */
	private void init() throws Exception {
		setTitle("HTTP POST");
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1253, 614);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		this.setLocationRelativeTo(null);
		JLabel lblNewLabel = new JLabel("IP：");
		txtPort = new JTextField();
		txtPort.setColumns(10);
		JLabel lblPort = new JLabel("Port：");
		cmbService = new JComboBox<String>();
		btnSend = new JButton("Send");
		JSeparator separator = new JSeparator();
		JSeparator separator_1 = new JSeparator();
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		JSeparator separator_2 = new JSeparator();
		btnExit = new JButton("Exit");
		cmbFileName = new JComboBox<String>();
		JLabel lblNewLabel_1 = new JLabel("Service：");
		cmbIP = new JComboBox<String>();
		cmbIP.setEditable(true);
		JLabel lblXml = new JLabel("XML");

		JScrollPane scrollPane_2 = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addComponent(separator_1, GroupLayout.DEFAULT_SIZE, 1227, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 1221, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(separator, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup().addComponent(lblNewLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cmbIP, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(lblPort)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(lblNewLabel_1)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cmbService, GroupLayout.PREFERRED_SIZE, 365, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(lblXml)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cmbFileName, GroupLayout.PREFERRED_SIZE, 322, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 69, Short.MAX_VALUE).addComponent(btnSend))
				.addComponent(separator_2, GroupLayout.DEFAULT_SIZE, 1227, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(btnExit))
				.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 1227, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel)
								.addComponent(btnSend)
								.addComponent(cmbIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPort)
								.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_1)
								.addComponent(cmbService, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblXml).addComponent(cmbFileName, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(8)
						.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 163,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnExit)));

		lsResult = new JTable();
		lsResult.setModel(new DefaultTableModel(new Object[][] {}, columns));
		scrollPane_2.setViewportView(lsResult);
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		txtAreaRespXML = new JTextArea();
		scrollPane.setViewportView(txtAreaRespXML);
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		txtAreaReqXml = new JTextArea();
		scrollPane_1.setViewportView(txtAreaReqXml);
		contentPane.setLayout(gl_contentPane);
	}

	/**
	 * 窗体加载事件
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 */
	private void windowLoadListener() {
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				// IP下载选框加载数据
				cmbIPLoadData();
				// service下拉选框加载数据
				cmbServiceLoadData();
				// cmbFileName下拉选框加载数据
				cmbFileNameLoadData();
			}
		});
	}

	/**
	 * 退出按钮事件处理
	 * 
	 * @author 杨研
	 * @date 2016年5月10日
	 */
	private void btnExitListener() {
		this.btnExit.addActionListener((ActionEvent e) -> {
			System.exit(1);
		});
	}

	/**
	 * cmbIP改变txtPort的值
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 */
	private void cmbIpDoTxtPort() {
		this.cmbIP.addActionListener((ActionEvent e) -> {
			String temp = Main.this.cmbIP.getSelectedItem().toString();
			if (temp.contains(":")) {
				Main.this.cmbIP.setSelectedItem(temp.substring(0, temp.indexOf(":")));
				Main.this.txtPort.setText(temp.substring(temp.indexOf(":") + 1, temp.length()));
			}
		});
	}

	/**
	 * 选择FileName改变txtAreaReqXml的值
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 */
	private void txtAreaReqXml() {

		this.cmbFileName.addItemListener((ItemEvent e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (!this.cmbFileName.getSelectedItem().toString().contains("请选择报文")) {
					// 读取报文文件
					readRequestXML();
				} else
					Main.this.txtAreaReqXml.setText("");
			}
		});
	}

	/**
	 * SendButton发送请求
	 * 
	 * @author 杨研
	 * @date 2016年5月12日
	 */
	private void sendButtonListener() {

		this.btnSend.addActionListener((ActionEvent e) -> {
			if (validURL()) {
				if (Main.this.cmbFileName.getSelectedItem() != null
						&& !Main.this.cmbFileName.getSelectedItem().toString().contains("请选择报文")) {
					// 发送请求
					snedRequest();
					Main.this.txtAreaRespXML.setText("");
					Main.this.cmbService.setEnabled(false);
					Main.this.cmbIP.setEnabled(false);
					Main.this.txtPort.setEnabled(false);
					Main.this.cmbFileName.setEnabled(false);
					Main.this.lsResult.setModel(new DefaultTableModel(null, columns));
				} else
					JOptionPane.showMessageDialog(null, "请选择报文！", "提示", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "请填写正确服务地址！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	/**
	 * 从文件中加载Ip信息
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 */
	private void cmbIPLoadData() {
		try {
			new SwingWorker<String[], String>() {

				@Override
				protected String[] doInBackground() throws Exception {
					File file = new File("config/IpList");
					if (file.exists()) {
						FileInputStream fileInputStream = new FileInputStream(file);
						InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,
								Main.this.getProperties("Encode"));
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						StringBuffer sb = new StringBuffer("------请选择IP------\n");
						String temp = "";
						while ((temp = bufferedReader.readLine()) != null) {
							sb.append(temp).append("\n");
						}
						bufferedReader.close();
						inputStreamReader.close();
						fileInputStream.close();
						return sb.toString().split("\n");
					}
					return null;
				}

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				protected void done() {
					try {
						String[] result = get();
						if (result != null && result.length > 0) {
							cmbIP.setModel(new DefaultComboBoxModel(result));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从文件中加载ServiceName信息
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 */
	private void cmbServiceLoadData() {
		try {
			new SwingWorker<String[], String>() {

				@Override
				protected String[] doInBackground() throws Exception {
					File file = new File("config/ServiceList");
					if (file.exists()) {
						FileInputStream fileInputStream = new FileInputStream(file);
						InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,
								Main.this.getProperties("Encode"));
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						StringBuffer sb = new StringBuffer("---------------请选择服务名------------------\n");
						String temp = "";
						while ((temp = bufferedReader.readLine()) != null) {
							sb.append(temp).append("\n");
						}
						bufferedReader.close();
						inputStreamReader.close();
						fileInputStream.close();
						return sb.toString().split("\n");
					}
					return null;
				}

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				protected void done() {
					try {
						String[] result = get();
						if (result != null && result.length > 0)
							cmbService.setModel(new DefaultComboBoxModel(result));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载报文文件名
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 */
	private void cmbFileNameLoadData() {
		new SwingWorker<String[], String>() {

			@Override
			protected String[] doInBackground() throws Exception {
				File file = new File("requestXML");
				File[] files = file.listFiles();
				StringBuffer sb = new StringBuffer("---------------请选择报文---------------\n");
				if (files != null && files.length > 0) {
					for (int i = 0; i < files.length; i++) {
						sb.append(files[i].getName()).append("\n");
					}
				}
				return sb.toString().split("\n");
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			protected void done() {
				try {
					String[] result = get();
					if (result != null & result.length > 1)
						Main.this.cmbFileName.setModel(new DefaultComboBoxModel(result));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}

	/**
	 * 加载报文信息
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 * @param file
	 */
	private void readRequestXML() {
		new SwingWorker<String, String>() {

			@Override
			protected String doInBackground() throws Exception {
				try {
					File file = new File("RequestXML/" + Main.this.cmbFileName.getSelectedItem().toString());
					StringBuffer sb = new StringBuffer();
					InputStreamReader is = new InputStreamReader(new FileInputStream(file),
							Main.this.getProperties("Encode"));
					BufferedReader bufferedReader = new BufferedReader(is);
					String line = "";
					while ((line = bufferedReader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					bufferedReader.close();
					return Main.this.formatXml(sb.toString());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				return null;
			}

			@Override
			protected void done() {
				try {
					Main.this.txtAreaReqXml.setText(get());
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.done();
			}
		}.execute();
	}

	/**
	 * 通过窗体控件拼成URL地址
	 * 
	 * @author 杨研
	 * @date 2016年5月12日
	 * @return
	 */
	private String getURL() {
		try {
			String ip = this.cmbIP.getSelectedItem().toString().trim();
			String port = this.txtPort.getText().trim();
			String service = this.cmbService.getSelectedItem().toString().trim();
			if (service.indexOf("--") != -1) {
				service = service.substring(0, service.indexOf("--"));
			}
			StringBuffer sb = new StringBuffer("http://");
			sb.append(ip).append(":").append(port).append("/").append(service);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送请求
	 * 
	 * @author 杨研
	 * @date 2016年5月12日
	 * @param urlSrt
	 */
	private void snedRequest() {
		if (!thread.isAlive()) {
			thread = new Thread(new SendRequestThread());
			thread.start();
		} else {
			JOptionPane.showMessageDialog(null, "请求正在发送，请稍后再试！！！！！", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * 将请求报文写到文件
	 * 
	 * @author 杨研
	 * @date 2016年5月12日
	 * @param respXML
	 */
	private void writerFile(String respXML) {
		Thread thread = new Thread(() -> {
			try {
				String fileName = Main.this.cmbFileName.getSelectedItem().toString();
				String before = "";
				String after = "";
				if (!fileName.isEmpty()) {
					before = fileName.substring(0, fileName.lastIndexOf('.'));
					after = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
				}
				File file = new File("ResponseXML/" + before + "-"
						+ new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date()) + after);
				if (file.exists())
					file.delete();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(respXML.getBytes());
				fileOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	/**
	 * 校验URL地址
	 * 
	 * @author 杨研
	 * @date 2016年5月12日
	 * @return
	 */
	private boolean validURL() {
		String ip = "";
		String port = "";
		String service = "";
		if (this.cmbIP.getSelectedItem() != null)
			ip = this.cmbIP.getSelectedItem().toString().trim();
		if (this.txtPort.getText() != null)
			port = this.txtPort.getText().trim();
		if (this.cmbService.getSelectedItem() != null)
			service = this.cmbService.getSelectedItem().toString().trim();

		String ipValid = "((25[0-5]|1\\d\\d|\\d{1,2})\\.){3}(25[0-5]|1\\d\\d?|\\d{1,2})";
		String portValid = "([0-9]|[1-9][0-9]|[1-9][0-9]{2}|[1-9][0-9]{3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])";

		boolean ipBool = Pattern.compile(ipValid).matcher(ip).matches();
		boolean portBool = Pattern.compile(portValid).matcher(port).matches();
		boolean serviceBool = !service.contains("请选择服务名") && !service.equals("");
		if (ipBool && portBool && serviceBool)
			return true;
		else
			return false;
	}

	/**
	 * 在配置文件获取参数
	 * 
	 * @author 杨研
	 * @date 2016年5月11日
	 * @param paramName
	 *            参数名
	 * @return
	 */
	private String getProperties(String paramName) {
		try {
			Properties properties = new Properties();
			File file = new File("config/Config");
			FileInputStream fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);
			fileInputStream.close();
			return properties.getProperty(paramName).trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送请求线程
	 * 
	 * @author 杨研
	 * @date 2016年5月13日
	 */
	class SendRequestThread implements Runnable {

		@Override
		public void run() {
			try {
				String reqXML = Main.this.txtAreaReqXml.getText();
				URL url = new URL(Main.this.getURL());
				URLConnection con = url.openConnection();
				con.setDoOutput(true);
				con.setRequestProperty("Pragma:", "no-cache");
				con.setRequestProperty("Cache-Control", "no-cache");
				con.setRequestProperty("Content-Type", "text/xml");
				con.setConnectTimeout(Integer.valueOf(Main.this.getProperties("ConnectTimeout")));
				con.setReadTimeout(Integer.valueOf(Main.this.getProperties("ReadTimeout")));
				OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
				out.write(new String(reqXML.getBytes(Main.this.getProperties("Encode")),
						Main.this.getProperties("SendXMLEncode")));
				out.flush();
				out.close();

				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line = "";
				StringBuffer sb = new StringBuffer();
				for (line = br.readLine(); line != null; line = br.readLine()) {
					sb.append(new String(line.getBytes(Main.this.getProperties("RespXMLEncode"))));
				}
				String result = Main.this.formatXml(sb.toString());
				Main.this.writerFile(result);
				this.done(result);
				if (Main.this.cmbService.getSelectedItem().toString().contains("TestTools/Test")
						|| Main.this.cmbService.getSelectedItem().toString().contains("Services/KSInsuranceService"))
					Main.this.ParseXML(result);
			} catch (ConnectException e) {
				JOptionPane.showMessageDialog(null, "远程主机连接失败！！！！！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				JOptionPane.showMessageDialog(null, "远程主机连接超时！！！！！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e.printStackTrace();
			} catch (NoRouteToHostException e) {
				JOptionPane.showMessageDialog(null, "无法连接远程主机！！！！！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "请求地址不存在！！！！！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "读取返回结果失败！！！！！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e.printStackTrace();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "出现未知错误！！！！！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e.printStackTrace();
			} finally {
				Main.this.cmbService.setEnabled(true);
				Main.this.cmbIP.setEnabled(true);
				Main.this.txtPort.setEnabled(true);
				Main.this.cmbFileName.setEnabled(true);
			}
		}

		public void done(String Result) {
			try {
				Main.this.txtAreaRespXML.setText(Result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 解析报文显示结果
	 * 
	 * @author 杨研
	 * @date 2016年5月20日
	 * @param xml
	 */
	private void ParseXML(String xml) {
		Thread thread = new Thread(() -> {
			try {
				SAXReader reader = new SAXReader();
				Document document = reader.read(new InputStreamReader(
						new ByteArrayInputStream(xml.getBytes(Main.this.getProperties("RespXMLEncode")))));
				Element rootElement = document.getRootElement();
				Element responseHeadVo = rootElement.element("Body").element("getBusNcdResInfoResponse")
						.element("return").element("responseHeadVo");
				String[] errorMessages = responseHeadVo.element("errorMessage").getText().split(",");
				String[] insurerArea = responseHeadVo.element("insurerArea").getText().split(",");
				String[] queryFailedArea = responseHeadVo.element("queryFailedArea").getText().split(",");
				Map<String, String> map = new HashMap<String, String>();
				String[] newArr = new String[insurerArea.length + queryFailedArea.length];
				int i = 0;
				for (String area : insurerArea) {
					if (area != null && !"".equals(area)) {
						newArr[i] = area;
					}
					i++;
				}

				for (String area : queryFailedArea) {
					if (area != null && !"".equals(area)) {
						newArr[i] = area;
					}
					i++;
				}
				for (String area : newArr) {
					for (String errorMessage : errorMessages) {
						if (area != null && errorMessage != null && errorMessage.contains(area))
							map.put(area, errorMessage.substring(errorMessage.indexOf(']') + 1, errorMessage.length()));
					}
				}
				String[][] results = new String[map.size()][2];
				int index = 0;
				for (String key : map.keySet()) {
					String[] result = new String[2];
					result[0] = key;
					result[1] = map.get(key);
					results[index] = result;
					index++;
				}
				Main.this.lsResult.setModel(new DefaultTableModel(results, columns));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	public String formatXml(String resXml) throws Exception {
		Document document = null;
		document = DocumentHelper.parseText(resXml);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		format.setNewLineAfterDeclaration(false);
		StringWriter writer = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		xmlWriter.write(document);
		xmlWriter.close();
		return writer.toString();
	}
}


