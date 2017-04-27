package chat_app_package;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;


import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Client_chat extends Frame implements ActionListener,Runnable {
	
	private Socket s;
	public String clientToChat;
	private DataInputStream din;
	private Thread thread = null;
	private ChatClientThread clientT  = null;
	private DataOutputStream dout;
	private String client_name;
	private String serverAddress;
	Button b;
	Button sendImage;
	Button sendFile;
	Button Browse;
	TextField tf;
	TextArea ta;
	
	JFileChooser chooser;
	JFrame f;
	
	String imageString = null;
	
	File imgFile;
	File FileT;
	String browsePath;
	
	public static void main(String[] args) {
	
		try {
			
			Client_chat client = new Client_chat();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
		public Client_chat(){
			
				// user input of serverAddress and host Name
			   serverAddress = JOptionPane.showInputDialog("Enter IP Address of a machine that is\n" +
		            "running the date service on port 2021:");
			   client_name = JOptionPane.showInputDialog("Enter host name : ");
			
			   
			   if(client_name != null){
				   f = new JFrame(client_name);
			   }
			   
			   //layout for the gui;frame
			   f.setLayout(new FlowLayout());
			   f.setBackground(Color.green);
			   
			   //creating button for send
			   b = new Button("send");
			   b.addActionListener(this);
			   
			   //creating button for sendImage
			   sendImage = new Button("sendImage");
			   sendImage.addActionListener(this);
			   
			   //button for sending file
			   sendFile = new Button("sendFile");
			   sendFile.addActionListener(this);
			   
			   //button for browse
			   Browse = new Button("Browse");
			   Browse.addActionListener(this);
			   
			   // close button for the chat window
			   f.addWindowListener(new W1());
			   
			   // text filed for sending message
			   tf = new TextField(20);
			   
			   // text area field for receiving message
			   ta = new TextArea(15,30);
			   ta.setBackground(Color.cyan);
			   
			   //adding all buttons,text area, text field in the layout
			   f.add(tf);
			   f.add(ta);
			   f.add(b);
			   f.add(sendImage);
			   f.add(Browse);
			   f.add(sendFile);
			   
			   try{
				   	// creating tcp socket for client to communicate with server
					s = new Socket(serverAddress,2021);
					System.out.println("connected:" + s);
					
					// creating new thread for each client
					clientT = new ChatClientThread(this, s);
					
					// creating datainput and data output stream
					clientT.open();
					
					// starting new thread for each client
					clientT.start();
					
					//starting new thread in the main for each client
					start();
					
					// creating output stream for socket
					dout = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

				}
					
				catch(UnknownHostException uhe){ 
					System.out.println("Host unknown: " + uhe.getMessage());
				}
				catch(IOException ioe){ 
					System.out.println("Unexpected exception: " + ioe.getMessage());
				}
			   
			   //setting font of letters and size of frame
			   setFont(new Font("Arial",Font.BOLD,20));
			   f.setSize(450,450);
			   f.setVisible(true);
			   f.setLocation(100,300);
			   f.validate();

		}
			
		// closing window sign 
		private class W1 extends WindowAdapter{
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		}
		
		//creating input stream for user input and new thread for each client
		public void start(){
			din = new DataInputStream(System.in);
			
			if( thread == null){
				
				thread = new Thread(this);
				thread.start();
				
			}
		}
		
		//closing dataInput stream, dataOutput Stream and socket
		public void stop(){
				if(thread != null){
				thread.stop();
				thread = null;
				}
				try{			
					
					if(din !=null) din.close();
					if(dout !=null) dout.close();
					
					if(s != null) s.close();
					
				}catch(IOException ioe)
		      {  System.out.println("Error closing ...");
		      		clientT.stop();
		      		
		      }
		}

		// running each client thread after 1000ms
		//it send the host name to the server
		public void run() {
		
					try {
						try {
							thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("clientName" + client_name);
						if(client_name != null){
							dout.writeUTF(client_name);
							dout.flush();
					    }
					}catch (IOException e) {
					    	e.printStackTrace();  	
					  }
		}
		
		// it permorms the action when buttons are pressed
		public void actionPerformed(ActionEvent ae){
			
			// it sends the message from host to intended client
			if(ae.getActionCommand().equals("send")){
				try {
					dout.writeUTF(tf.getText());
					dout.flush();
					ta.append("Msg Sent: " + tf.getText()+"\n");
					tf.setText("");
				} catch (IOException e) {
					e.printStackTrace();
					stop();
				}
			}
			
			// it is used to browse the file
			if(ae.getActionCommand().equals("Browse")){
				
				chooser = new JFileChooser("G:\\Sem2\\ACN");
			       	int rVal = chooser.showOpenDialog(f);
			        if (rVal == JFileChooser.APPROVE_OPTION) {
			        	File selectedFile = chooser.getSelectedFile();
			           browsePath = selectedFile.getAbsolutePath();
			        }
			}
			
			// it is used to send image
			if(ae.getActionCommand().equals("sendImage")){
				
				try{
						String compPath = tf.getText().concat(":").concat(browsePath);
						System.out.println("browsePath" + browsePath);
						System.out.println("compPath" + compPath);
						dout.writeUTF(compPath);
						dout.flush();
						tf.setText("");
						imgFile = new File(browsePath);
						FileInputStream imgFileInputStream = new FileInputStream(imgFile);
						byte[] imgData = new byte[(int)imgFile.length()];
						
						imgFileInputStream.read(imgData);
							
						
						imageString = Base64.getEncoder().encodeToString(imgData);
						
						dout.writeUTF(imageString);
						dout.flush();
						
						
						imgFileInputStream.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
			// it is used to send file
			if(ae.getActionCommand().equals("sendFile")){
				
				try{
						//to get the complete path with the format - username:file:browsePath 
						String compPath = tf.getText().concat(":").concat(browsePath);
						
						// send the comp path to the client
						dout.writeUTF(compPath);
						dout.flush();
						tf.setText("");
						
						// send the file by reading and encoding it in bytes
						
						FileT = new File(browsePath);
						FileInputStream ft = new FileInputStream(FileT);
						byte[] fileData = new byte[(int)FileT.length()];
						ft.read(fileData);
						
						String fileString = Base64.getEncoder().encodeToString(fileData);	
						dout.writeUTF(fileString);
						dout.flush();
						ft.close();
						
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
		}
		
		// displaying the text in the user window from the client
		public void handle(String msg) {
				System.out.println(msg);
				ta.append(msg+"\n");	
		}
		
		// to show in the window text area of user,that image is received 
		public void handleImg(String img,String client_name){
				System.out.println(img);
				ta.append(client_name + ":" +img+" received"+"\n");
			}
			


	}


