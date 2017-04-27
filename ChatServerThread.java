package chat_app_package;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;



public class ChatServerThread extends Thread{
	private Server_chat server = null;
	private Socket socket = null;
	private int id= -1;
	private DataInputStream streamIn;
	private DataOutputStream streamOut;
	
	String user_name;
	String clientName;
	
	
	public ChatServerThread(Server_chat server_1 , Socket socket_1, String clientName){
		super();
		server = server_1;
		socket = socket_1;
		id = socket.getPort();
		this.clientName = clientName;
		
		
	}
	
	// send the message received from the host to client it want to send the message
	public void send(String msg){
		try{
			streamOut.writeUTF(msg);
			streamOut.flush();
			System.out.println("sending to client");
		}catch(IOException ioe){
			System.out.println(id + " Error in sending " + ioe.getMessage());
			server.remove(clientName);
			stop();
			
		}
	}
	
	//send the image stream received from the host to client it want to send
	public void imageSave(String imageString,String imgPath,String clientName){
			try{
				String client_name= clientName;
				String msg = "image:";
				String msgPath = client_name.concat(":").concat(msg).concat(imgPath);
				streamOut.writeUTF(msgPath);
				streamOut.flush();
				streamOut.writeUTF(imageString);
				streamOut.flush();
				
			}catch(IOException e){
				e.printStackTrace();
			}
		
		}
	//send the file stream received from the host to the client it want to send 
	public void fileSave(String fileString,String filePath,String clientName){
		try{
			String client_name= clientName;
			String msg = "file:";
			String msgPath = client_name.concat(":").concat(msg).concat(filePath);
			streamOut.writeUTF(msgPath);
			streamOut.flush();
			streamOut.writeUTF(fileString);
			streamOut.flush();
			
		}catch(IOException e){
			e.printStackTrace();
		}	
	}
	
	//reading the stream from the host and sending to the intended client
	public void run(){
				while(true){
					try{					
						String str = (String)streamIn.readUTF();
						StringTokenizer st = new StringTokenizer(str,":");
						user_name = st.nextToken();
						String mode = st.nextToken();						
						String msg = st.nextToken();
						
						if(mode.equalsIgnoreCase("image")){
							String msgPath = st.nextToken();
							String ImageString = (String)streamIn.readUTF();
							server.imageDownload(user_name,ImageString,msgPath,clientName);
							
						}else if(mode.equalsIgnoreCase("text")){
							server.handle(user_name,msg,clientName);
							
						}else if(mode.equalsIgnoreCase("file")){
							String msgPath = st.nextToken();
							String fileString = (String)streamIn.readUTF();
							server.fileDownload(user_name,fileString,msgPath,clientName);
							}			
						}	
					
					// making the connection reliable; that is as and when host is disconnected it will
					//send message to the client to which it was communication that user is disconnected
					catch (IOException ioe) {
						ioe.printStackTrace();
						server.reliableConnection(clientName,user_name);
						server.remove(clientName);
						stop();
					}					
				}
	}
		
	// creating InputStream and outputStream for the socket created for each host
	public void open() {
		try{
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		}catch(IOException e){
			e.printStackTrace();
		}	
	}
	
	// closing the socket 
	public void close() {
		try{
			if(socket!=null) socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	

	
	
}
