package chat_app_package;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;


public class ChatClientThread extends Thread{
	private Socket socket;
	private Client_chat client = null;
	private DataInputStream streamIn = null;


	//initializing the socket and client name 
	public ChatClientThread(Client_chat client_1,Socket socket_1) {
		client = client_1;
		socket = socket_1;
		
	}

	//creating input stream for the socket
	public void open() {
		try{			
			streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));			
		}catch(IOException e){
			System.out.println("Error receieving data" + e);
			client.stop();
		}		
	}
	
	// reading the format - username:text/file/image:message/imageName/fileName
	//splitting the message into username,text/file/image and message 
	//sending image,file,video,audio etc according to which button is pressed
	public void run	(){			
			while(true){
					try{			
							String str = (String)streamIn.readUTF();
							
							// encoding image and sending to client as bytestream
							if(str.contains("image")){
								StringTokenizer st = new StringTokenizer(str,":");
								String client_name = st.nextToken();						
								String lastToken = str.substring(str.lastIndexOf("\\") + 1);
								
								String imageString = (String)streamIn.readUTF();
								BufferedImage image = null;
								byte[] imageByte;	
								
								imageByte = Base64.getDecoder().decode(imageString);
								ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
								image = ImageIO.read(bis);
								bis.close();
								
								String lastTokenImg = lastToken;
								File outputfile = new File(lastTokenImg);
								ImageIO.write(image, "png", outputfile);
								
								//display the image name received by the user
								client.handleImg(lastTokenImg,client_name);
								
								// encoding file : video,pdf,doc,audio nd sending to client
							}else if(str.contains("file")){
								StringTokenizer st = new StringTokenizer(str,":");
								String client_name = st.nextToken();
								String fileType = st.nextToken();
								String path = st.nextToken();
								String lastToken = str.substring(str.lastIndexOf("\\") + 1);							
								String lastTokenImg = lastToken;
								String fileString = (String)streamIn.readUTF();
								byte[] fileByte;								
								fileByte = Base64.getDecoder().decode(fileString);	
								File outputfile = new File(lastTokenImg);								
								OutputStream fos = new FileOutputStream(outputfile);								
								fos.write(fileByte);								
								fos.close();
								
								// display the file name received by the user																
								client.handleImg(lastTokenImg,client_name);		
							}else{
								//sending text to user
								client.handle(str);
							}
					}catch (IOException ioe) {
						ioe.printStackTrace();
						client.stop();
					}
			}
		
	}
	
	

}