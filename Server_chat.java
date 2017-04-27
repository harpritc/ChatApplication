
package chat_app_package;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class Server_chat implements Runnable {
	private Socket socket;
	private DataInputStream dis;
	private ServerSocket    listener;
	private Thread thread=null;
	private ChatServerThread clients[] = new ChatServerThread[50];
	private int clientCount = 0;
	private String clientName;
	
	ArrayList<String> clientToChatList = new ArrayList<String>();
	int count=0;
	
			public static void main(String[] args){	
				Server_chat server = null;
				server = new Server_chat();
				server.chatServer();
			
			}
	
			//server socket is created in the port 2021
				public void chatServer() {		
					try{
						System.out.println("Binding to port 2021, please wait  ...");
						listener = new ServerSocket(2021); 
						System.out.println("Server started: " + listener);
				        start();
					}
					catch (IOException ioe){
						System.out.println("Server not started" + ioe);
					}
				}
		
				/* here server accepts the connection with client and created input data stream.
				   Each client will run in a separate thread, it created new thread 
				   as and when new client is added */
				
				public void run(){
					while (thread != null){
						try{
							System.out.println("Waiting for a client ...");
							socket = listener.accept();
							System.out.println("adding thread");
							open();
							addThread(socket);
						}catch(IOException e){
							System.out.println("Client not accepted" + e);
							stop();
						}
					}
				}
				
				// finding the thread number of client to whom host wants to chat from the list
								
				private int findClient(String clientToChat){
					int i =0;
					for (String s: clientToChatList){
						if(s.equals(clientToChat)){
							return i;
						}else{
							i=i+1;
						}
					}
					return i;	
				}
				
				//  Send message to the user to which host wants to chat 
				public synchronized void handle(String clientToChat, String input_text, String hostName){
					try{
						clients[findClient(clientToChat)].send(hostName + ": " + input_text);
					}catch(Exception e){
						e.printStackTrace();
					}		
				}
				
				// send message to the user that client to which it was talking is disconnected if user has closed the chat.
				public void reliableConnection(String clientName, String user_name){
					clients[findClient(user_name)].send("user: " + clientName + " is disconnected");
				}
				
				//Method to download image and send to selected client 
				public synchronized void imageDownload(String clientToChat, String imageString, String imgPath, String clientName){
						clients[findClient(clientToChat)].imageSave(imageString,imgPath,clientName);
				}
				
				//Method to download file and send to selected client
				public synchronized void fileDownload(String clientToChat, String fileT, String filePath, String clientName){
					
						clients[findClient(clientToChat)].fileSave(fileT,filePath,clientName);
					
				}
				// remove client from the list when is disconnected
				
				synchronized void remove(String clientToChat) {
					try{
							int position = findClient(clientToChat);
							if(position > 0 ){
								ChatServerThread ClientToTerminate = clients[position];
								System.out.println("Removing client from thread with " + clientToChat + " at " + position);
								if(position < clientCount-1){
									for(int i = position+1;i < clientCount;i++){
										clients[i-1] = clients[i];									
									}
									clientCount--;
								}
								ClientToTerminate.close();
							}
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
				
				//Adding new thread for each client
				private void addThread(Socket socket){
					if (clientCount < clients.length){
						
						clients[clientCount] = new ChatServerThread(this, socket,clientName);
				        clients[clientCount].open();
						clients[clientCount].start();
						clientCount++;
						System.out.println("clientCount = " + clientCount);
					}else{
						System.out.println("Maximum clients limit reached " + clients.length);
					}
				}
				
					
				// creating input data stream to receive input message from host
				// Also adding all the client in the list
				public void open() {
					try {
				        	dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				   		    System.out.println("Client accepted: " + socket);
				   		    // host name is taken as user input
							clientName = (String)dis.readUTF();   
						} catch (IOException e) {
							e.printStackTrace();
						}
						clientToChatList.add(clientName);
						System.out.println("client_list" + clientToChatList);
				}
				
				// closing the dataInput stream and socket when connection is closed
				public void stop(){
						try{				
							if( dis!= null) dis.close();
							if (socket != null ) socket.close();
						}catch(IOException e){
							e.printStackTrace();
					}
				}
		
				// creating new thread 
				public void start() {
					if (thread == null){
						thread = new Thread(this);
						thread.start();
					}
				}
			
			
}
		
		
	


