package com.jetty.server;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyStart {
	
	private static Logger logger = LoggerFactory.getLogger(JettyStart.class);
	private static int port = 8080;
	private static int maxThreads = 100;
	private static String contextPath;
	private static String war;
	
	public static void main(String[] args) throws Exception {
		if (null == args || args.length == 0) { // 读取默认配置文件
			logger.debug("read default property file {}","jetty.properties");
			String path = "../conf/jetty.properties";
			logger.debug("path : {}", path);
			config(path);
			//config(JettyStart.class.getClassLoader().getResource("jetty.properties").openStream());
		} else {
			config(args[0]);
		}
		Server server = new Server();
		QueuedThreadPool threadPool = new QueuedThreadPool();
		// 设置线程池
		threadPool.setMaxThreads(maxThreads);
		server.setThreadPool(threadPool);
		// 设置连接参数
		Connector connector = new SelectChannelConnector();
		// 设置监听端口
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });
		WebAppContext context = new WebAppContext();
		// 访问项目地址
		context.setContextPath(contextPath);
		// 启动的war包
		context.setWar(findWar(war));
		server.addHandler(context);
		server.setStopAtShutdown(true);
		server.setSendServerVersion(true);
		server.start();
		logger.info("server started listen {} port,context path is {} ",port,contextPath);
		server.join();
	}

	public static void config(String path) throws IOException{
		File file  =new File(path);
		if (!file.exists()) {
			logger.info(path + "  is not exists ..");
			throw new IllegalArgumentException(path + "  is not exists ..");
		} else {
			Properties prop = new Properties(); 
			FileReader fr = new FileReader(file);
			prop.load(fr);
			port = Integer.parseInt(prop.getProperty("jetty.port"));
			maxThreads = Integer.parseInt(prop.getProperty("jetty.maxThreads"));
			contextPath = prop.getProperty("jetty.context","");
			war = prop.getProperty("jetty.warFolder");
			fr.close();
		}
	}
	public static String findWar(String folder){
		File file = new File(folder);
		if (!file.exists()) {
			throw new IllegalArgumentException(folder + "  is not exists ..");
		} else {
			File[] files = file.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					if (pathname.getName().endsWith(".war")) {
						return true;
					}
					return false;
				}
			});
			if (files == null || files.length == 0) {
				logger.info("no war app find .. in {}",folder);
				throw new IllegalArgumentException("no war app find ..");
			}
			return files[0].getPath();
		}
		
	}
	/*public static void config(InputStream in) throws IOException{
		Properties prop = new Properties(); 
		prop.load(in);
		port = Integer.parseInt(prop.getProperty("jetty.port"));
		maxThreads = Integer.parseInt(prop.getProperty("jetty.maxThreads"));
		contextPath = prop.getProperty("jetty.context","");
		war = prop.getProperty("jetty.war");
		in.close();
	}*/
}