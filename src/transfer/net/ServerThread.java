package transfer.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 监听文件发送的服务端线程
 * @author EsauLu
 *
 */
public class ServerThread extends TransferBase implements Runnable {

    /**
     * 服务端套接字
     */
    private ServerSocket mServerSocket;

    /**
     * 文件保存路径
     */
    private String mFileSavaPath;

    /**
     * 用于保证只有一个ServerThread实例在运行
     */
    private static ServerThread mServerThread;

    /**
     * 收到发送请求时用户的操作接口
     */
    public interface ReceiveOper {
        public void operate(ReceiveThread receive);
    }
    private ReceiveOper mReceiveOper;   //接口实例

    /**
     * 构造函数
     * @param mPort	端口号
     */
    private ServerThread(String ip,int mPort,String path) {
        // Auto-generated constructor stub
        this.mIp=ip;
        this.mPort=mPort;
        this.mFileSavaPath=path;
        this.mThread=new Thread(this);
    	this.mHostName="";
        init();
    }

    /**
     * 构造函数
     * @param ip IP
     * @param mPort 端口号
     * @param path 文件保存路径
     * @param hostName 主机名
     */
    private ServerThread(String ip,int mPort,String path ,String hostName) {
        // Auto-generated constructor stub
    	this.mHostName=hostName;
        this.mIp=ip;
        this.mPort=mPort;
        this.mFileSavaPath=path;
        this.mThread=new Thread(this);
        init();
    }

    /**
     * 构造函数
     * @param mPort	端口号
     */
    private ServerThread(int mPort,String path) {
        // Auto-generated constructor stub
        this.mIp=null;
        this.mPort=mPort;
        this.mFileSavaPath=path;
        this.mThread=new Thread(this);
    	this.mHostName="";
        init();
    }

    private void init(){
    	
    	//设置主机名
//    	mHostName="测试名称";
    	if(mHostName==null||mHostName.equals("")){
    		try {
				InetAddress ad=InetAddress.getLocalHost();
				mHostName=ad.getHostName();
			} catch (UnknownHostException e) {
				// Auto-generated catch block
				mHostName=mThread.getId()+""+(Math.random()*1000)%255;
			}
    	}
    	
        //默认的收到发送请求的实现接口，将会无条件接收发送过来的文件
        mReceiveOper=new ReceiveOper() {
            @Override
            public void operate(ReceiveThread receive) {
                receive.start();
            }
        };
    }

    /**
     * 返回一个ServerThread实例
     * @param ip    指定ip
     * @param port  端口
     * @param path  文件保存路径
     * @return
     */
    public static synchronized ServerThread createServerThread(String ip,int port,String path){
        if(mServerThread!=null){
        	mServerThread.close();
        	mServerThread=null;
        }
        mServerThread=new ServerThread(ip,port,path);
        return mServerThread;
    }

    /**
     * 返回一个ServerThread实例
     * @param ip    指定ip
     * @param port  端口
     * @param path  文件保存路径
     * @return
     */
    public static synchronized ServerThread createServerThread(String ip,int port,String path ,String hostName){
        if(mServerThread!=null){
        	mServerThread.close();
        	mServerThread=null;
        }
        mServerThread=new ServerThread(ip,port,path,hostName);
        return mServerThread;
    }

    /**
     * 返回一个ServerThread实例
     * @param port  端口
     * @param path  路径
     * @return
     */
    public static synchronized ServerThread createServerThread(int port,String path){
        if(mServerThread!=null){
        	mServerThread.close();
        	mServerThread=null;
        }
        mServerThread=new ServerThread(port,path);
        return mServerThread;
    }

    /**
     * 注册接收到发送请求后的操作接口
     * @param mReceiveOper
     */
    public void registerReceiveOper(ReceiveOper mReceiveOper){

        this.mReceiveOper=mReceiveOper;

    }

    @Override
    public void run(){
        // Auto-generated method stub
        try{
            while (true) {
				try {
					if (mIp == null) {
						mServerSocket = new ServerSocket(mPort);
						mIp=mServerSocket.getLocalSocketAddress().toString();	
					} else {
						mServerSocket = new ServerSocket(mPort, 0,
								InetAddress.getByName(mIp));       
					}
					break;
				} catch (Exception e) {
					// Auto-generated catch block
					mPort++;
				}
			}
			while(true){
                try {
                    Socket sc=mServerSocket.accept();//等待连接
					mReceiveOper.operate(new ReceiveThread(sc, mFileSavaPath,getHostName()));   //收到发送连接后，处理该连接
				} catch (Exception e) {
					// Auto-generated catch block
				}
            }
            
        }catch(Exception e){
        	mServerThread=null;
        }finally{
            try{
                if(mServerSocket!=null)
                    mServerSocket.close();
            }catch(IOException e){
            	
            }
        	mServerThread=null;
        }
    }

    /**
     * 返回当前文件保存路径
     * @return
     */
    public String getFileSavaPath() {
        return mFileSavaPath;
    }

    /**
     * 设置文件保存位置
     * @param FileSavaPath
     */
    public void setFileSavaPath(String FileSavaPath) {
        this.mFileSavaPath = FileSavaPath;
    }
    
    /**
     * 中断
     */
    public void interrupt(){
    	mThread.interrupt();
    }
    
    /**
     * 是否中断
     * @return
     */
    public boolean isInterrupted(){
    	return mThread.isInterrupted();
    }
    
    public boolean close() {
		// Auto-generated method stub
    	try {
			if(mServerSocket!=null){
				mServerSocket.close();
				return true;
			}
		} catch (IOException e) {
			// Auto-generated catch block
		}
    	return false;
	}
    
}












































