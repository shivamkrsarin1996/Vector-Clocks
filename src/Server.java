//URL-https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
/*
 * @author-Shivam Kumar Sareen
 * @Student Id- 1001751987
 * */
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

//import Server.ClientThread;


public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	public static ArrayList<String> client_names;
	static int srvrPort=9928;
	
	public HashMap<String, Integer> vector_clk_a;
	public HashMap<String, Integer> vector_clk_b;
	public HashMap<String, Integer> vector_clk_c;
	
	public static void removeClientnames(String user)	{	
		System.out.println("Inside removeClient");
		if (client_names.contains(user)) {
			client_names.remove(user);
			System.out.println(user+" removed");
			System.out.println("client count from clienname= "+client_names.size());
		}
	}

	public static void addClientnames(String user)
	{			System.out.println("Inside addClient");
		if (!client_names.contains(user)) {
			client_names.add(user);
		}
	}
	
	public static boolean isClientOnline(String user) {
		System.out.println("Inside isClientOnline");
		return client_names.contains(user);
	}
	

	public Server(int port)
	{
		this(port, null);
	}
	
	public Server(int port, ServerGUI sg) 
	{
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		client_names=new ArrayList<String>();
		vector_clk_a= new HashMap<String, Integer>();
		vector_clk_b= new HashMap<String, Integer>();
		vector_clk_c= new HashMap<String, Integer>();
		System.out.println("Vector a="+vector_clk_a);
		System.out.println("Vector b="+vector_clk_b);
		System.out.println("Vector c="+vector_clk_c);
	}
	
	
	public void start() 
	{
		System.out.println("Inside server start");
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			display("Server waiting for Clients on port " + port + ".");
			
			// infinite loop to wait for connections
			while(keepGoing) 
			{
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList

					t.start();

			}
			
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
			catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
	
    
//For the GUI to stop the server
	protected void stop() 
	{
		keepGoing = false;
		// connect to myself as Client to exit statement 
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}
	
// Display an event (not a message) to the console or the GUI
	private void display(String msg) 
	{
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	
	
	
//URL- https://github.com/abhi195/Chat-Server/blob/master/src/Server.java	
	 // to broadcast a message to all Clients
	private synchronized void broadcast() 
	{
		for(String cl : client_names)
			System.out.println("cl-"+cl);
		
		System.out.println("vector a in broadcast="+ vector_clk_a);
		System.out.println("vector b in broadcast="+ vector_clk_b);
		System.out.println("vector c in broadcast="+ vector_clk_c);	
		
//selecting the sender randomly
		Random rand_num = new Random();
		int sndr_indx= rand_num.nextInt(client_names.size());
		System.out.println("sender index="+sndr_indx);
		String rndm_sndr= client_names.get(sndr_indx);
		System.out.println("random sender="+rndm_sndr);
		//random sender
		String sender=rndm_sndr;
		System.out.println("sender="+sender);

//URL- https://stackoverflow.com/questions/23283041/how-to-make-java-delay-for-a-few-seconds/48403623	
//waiting for 2 to 10 seconds randomly
		Random rndm =new Random();
		int rndm_time=rndm.nextInt((6)+1) + 2;
		System.out.println("random time-"+rndm_time);
		if (rndm_time>=2 && rndm_time <=10)
			System.out.println("random time-if-"+rndm_time);
		try {
			TimeUnit.SECONDS.sleep(rndm_time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//selecting the receiver randomly
		Random rand_no = new Random();
		int r= rand_no.nextInt(client_names.size());
		System.out.println("r="+r);
		String rndm_clnt= client_names.get(r);
		System.out.println("random clnt="+rndm_clnt);
		//random receiver
		String receiver=rndm_clnt;
		System.out.println("receiver ="+ receiver);
		

		String messageLf = "from " + sender + " to "+ receiver + "->";
		
//sender should neither send message to itself nor increment its counter
		if (!sender.equals(receiver))
		{
			
// if sender is A-> increment the value of 'a' in its own vector clock
			if (sender.equals("a") || sender.equals("A"))
			{
				System.out.println("sender-vector clock for a="+ vector_clk_a);
				int counter_a= vector_clk_a.get("a");
				System.out.println("a counter="+counter_a);
				System.out.println("incrementing the counter for a");
				counter_a= counter_a + 1;
				vector_clk_a.put("a", counter_a);
				System.out.println("vector clock for a="+ vector_clk_a.get("a"));
				System.out.println("updated clock="+ vector_clk_a);
			}
// if sender is B-> increment the value of 'b' in its own vector clock
			if (sender.equals("b") || sender.equals("B"))
			{
				System.out.println("sender-vector clock for b="+ vector_clk_b);
				int counter_b= vector_clk_b.get("b");
				System.out.println("b counter="+counter_b);
				System.out.println("incrementing the counter for b");
				counter_b= counter_b + 1;
				vector_clk_b.put("b", counter_b);
				System.out.println("vector clock for b="+ vector_clk_b.get("b"));
				System.out.println("updated clock="+ vector_clk_b);
	
			}
// if sender is C-> increment the value of 'c' in its own vector clock
			if (sender.equals("c") || sender.equals("C"))
			{
				System.out.println("sender-vector clock for c="+ vector_clk_c);
				int counter_c= vector_clk_c.get("c");
				System.out.println("c counter="+counter_c);
				System.out.println("incrementing the counter for c");
				counter_c= counter_c + 1;
				vector_clk_c.put("c", counter_c);
				System.out.println("vector clock for c="+ vector_clk_c.get("c"));
				System.out.println("updated clock="+ vector_clk_c);
			}
			
			System.out.println("vector a after sending="+ vector_clk_a);
			System.out.println("vector b after sending="+ vector_clk_b);
			System.out.println("vector c after sending="+ vector_clk_c);	
			
// display message on server GUI
			if (sender.equals("a") || sender.equals("A"))
				sg.appendRoom(messageLf+ " vector clock- " +vector_clk_a+"\n");     // append in the room window
			
			if (sender.equals("b") || sender.equals("B"))
				sg.appendRoom(messageLf+ " vector clock- " +vector_clk_b+"\n");
			
			if (sender.equals("c") || sender.equals("C"))
				sg.appendRoom(messageLf+ " vector clock- " +vector_clk_c+"\n");


	//for receiver
			
//if receiver is A
			if (receiver.equals("a") || receiver.equals("A"))
			{
				System.out.println("receiver-vector clock for a="+ vector_clk_a);
				int counter_a= vector_clk_a.get("a");
				System.out.println("a counter="+counter_a);
				System.out.println("incrementing the counter for a");
				counter_a= counter_a + 1;
				vector_clk_a.put("a", counter_a);
				System.out.println("vector clock for a="+ vector_clk_a.get("a"));
				System.out.println("updated clock="+ vector_clk_a);
				

				if (sender.equals("b") || sender.equals("B"))
				{
					int new_b_for_a_from_b = Math.max(vector_clk_b.get("b"), vector_clk_a.get("b"));
					vector_clk_a.put("b", new_b_for_a_from_b);
					int new_c_for_a_from_b = Math.max(vector_clk_b.get("c"), vector_clk_a.get("c"));
					vector_clk_a.put("c", new_c_for_a_from_b);
				}
				if (sender.equals("c") || sender.equals("C"))
				{
					int new_b_for_a_from_c = Math.max(vector_clk_c.get("b"), vector_clk_a.get("b"));
					vector_clk_a.put("b", new_b_for_a_from_c);
					int new_c_for_a_from_c = Math.max(vector_clk_c.get("c"), vector_clk_a.get("c"));
					vector_clk_a.put("c", new_c_for_a_from_c);
				}
				System.out.println("updated clock for a after reading="+ vector_clk_a);
			}

//if receiver is B
			if (receiver.equals("b") || receiver.equals("B"))
			{
				System.out.println("receiver-vector clock for b="+ vector_clk_b);
				int counter_b= vector_clk_b.get("b");
				System.out.println("b counter="+counter_b);
				System.out.println("incrementing the counter for b");
				counter_b= counter_b + 1;
				vector_clk_b.put("b", counter_b);
				


				if (sender.equals("a") || sender.equals("A"))
				{
					int new_a_for_b_from_a = Math.max(vector_clk_a.get("a"), vector_clk_b.get("a"));
					vector_clk_b.put("a", new_a_for_b_from_a);
					int new_c_for_b_from_a = Math.max(vector_clk_a.get("c"), vector_clk_b.get("c"));
					vector_clk_b.put("c", new_c_for_b_from_a);
					
				}
				if (sender.equals("c") || sender.equals("C"))
				{
					int new_a_for_b_from_c = Math.max(vector_clk_c.get("a"), vector_clk_b.get("a"));
					vector_clk_b.put("a", new_a_for_b_from_c);
					int new_c_for_b_from_c = Math.max(vector_clk_c.get("c"), vector_clk_b.get("c"));
					vector_clk_b.put("c", new_c_for_b_from_c);
				}

				System.out.println("updated clock for b after reading="+ vector_clk_b);
			}
			
//if receiver is C
			if (receiver.equals("c") || receiver.equals("C"))
			{
				System.out.println("receiver-vector clock for c="+ vector_clk_c);
				int counter_c= vector_clk_c.get("c");
				System.out.println("c counter="+counter_c);
				System.out.println("incrementing the counter for c");
				counter_c= counter_c + 1;
				vector_clk_c.put("c", counter_c);

				if (sender.equals("a") || sender.equals("A"))
				{
					int new_a_for_c_from_a = Math.max(vector_clk_a.get("a"), vector_clk_c.get("a"));
					vector_clk_c.put("a", new_a_for_c_from_a);
					int new_b_for_c_from_a = Math.max(vector_clk_a.get("b"), vector_clk_c.get("b"));
					vector_clk_c.put("b", new_b_for_c_from_a);
				}
				if (sender.equals("b") || sender.equals("B"))
				{
					int new_a_for_c_from_b = Math.max(vector_clk_b.get("a"), vector_clk_c.get("a"));
					vector_clk_c.put("a", new_a_for_c_from_b);
					int new_b_for_c_from_b = Math.max(vector_clk_b.get("b"), vector_clk_c.get("b"));
					vector_clk_c.put("b", new_b_for_c_from_b);
				}

				System.out.println("updated clock="+ vector_clk_c);
			}

			System.out.println("vector a after receiving="+ vector_clk_a);
			System.out.println("vector b after receiving="+ vector_clk_b);
			System.out.println("vector c after receiving="+ vector_clk_c);	
			
			
			// we loop in reverse order to find the mentioned username
			for(int y=0; y < al.size(); y++)
			{
				ClientThread ct1=al.get(y);
				String thread_usrname=ct1.getUsername();
				System.out.println("Current thread="+thread_usrname);
				if(!sender.equals(thread_usrname)) 
				{
					System.out.println("thread!=sender");
					if(thread_usrname.equals(receiver))
					{
						System.out.println("thread=reciever");
						if (receiver.equals("a") || receiver.equals("A")) {
							// try to write to the Client if it fails remove it from the list
							if(!ct1.writeMsg(messageLf+ " vector clock- " +vector_clk_a +"\n"))
							{
								al.remove(y);
								display("Disconnected Client " + ct1.username + " removed from list.");
							}
						}

						if (receiver.equals("b") || receiver.equals("B")) {
							// try to write to the Client if it fails remove it from the list
							if(!ct1.writeMsg(messageLf+ " vector clock- " +vector_clk_b +"\n"))
							{
								al.remove(y);
								display("Disconnected Client " + ct1.username + " removed from list.");
							}
						}

						if (receiver.equals("c") || receiver.equals("C")) {
							// try to write to the Client if it fails remove it from the list
							if(!ct1.writeMsg(messageLf+ " vector clock- " +vector_clk_c +"\n"))
							{
								al.remove(y);
								display("Disconnected Client " + ct1.username + " removed from list.");
							}
						}
					}
				}
				else if (sender.equals(thread_usrname)) 	// thread_username = sender
				{
					System.out.println("sender = thread");
					if (sender.equals("a") || sender.equals("A")) {
						ct1.writeMsg(messageLf+ " vector clock- " +vector_clk_a +"\n");
					}

					if (sender.equals("b") || sender.equals("B")) {
						ct1.writeMsg(messageLf+ " vector clock- " +vector_clk_b +"\n");
					}

					if (sender.equals("c") || sender.equals("C")) {
						ct1.writeMsg(messageLf+ " vector clock- " +vector_clk_c +"\n");
					}

				}
			}
		}
		if (sender.equals(receiver))
			broadcast();
	}


	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id)
	{
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i)
		{
			ClientThread ct = al.get(i);
			if(ct.id == id)
			{
				al.remove(i);
				return;
			}
		}
	}

	public static void main(String[] args) 
	{
		// start server on PortNumber that is specified 
		int portNumber = srvrPort;

		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	
	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
// my unique id (easier for deconnection)
		int id;
// the Username of the Client
		String username;
// the only type of message a will receive
		ChatMessage cm;	
		String date;
		int cl_count=0;
		
// Constructor for Client Thread
		ClientThread(Socket socket) 
		{
			id = ++uniqueId;		// a unique id
			this.socket = socket;
			
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{	
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				System.out.println("I/O stream created for Server");
				String status="reject";
				
				while(true)
				{
						
						System.out.println("Status="+status);
						// read the username
						username = (String) sInput.readObject();
						System.out.println("User name from server in Cl thread="+username);
			//Calling the method to check if the username entered is unique or not
						if(Server.isClientOnline(username))
						{
							sOutput.writeUTF("Reject");
							sOutput.flush();
							//continue;
						}
						else 
						{
							Server.addClientnames(username);
							sOutput.writeUTF("Accept");
							sOutput.flush();
							break;
						}					
				} //end-while
				
				display(username + " just connected.");
				if (username.equals("a") || username.equals("A"))
				{	
					vector_clk_a.put("a", 0);
					vector_clk_a.put("b", 0);
					vector_clk_a.put("c", 0);
				}
				if (username.equals("b") || username.equals("B"))
				{	
					vector_clk_b.put("a", 0);
					vector_clk_b.put("b", 0);
					vector_clk_b.put("c", 0);
				}
				if (username.equals("c") || username.equals("C"))
				{	
					vector_clk_c.put("a", 0);
					vector_clk_c.put("b", 0);
					vector_clk_c.put("c", 0);
				}
				
				ServerGUI.send_Clnt_name(username);
				sg.append_Clnt_List();
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}// end of constructor
		
		
		public String getUsername()
		{
			return username;
		}
			

// what will run forever
		public void run() {
			System.out.println("Inside server run");
			// to loop until LOGOUT
			boolean keepGoing = true;
			cl_count= client_names.size();
			System.out.println("vector a-run="+vector_clk_a);
			System.out.println("vector b-run="+vector_clk_b);
			System.out.println("vector c-run="+vector_clk_c);
			while(keepGoing) 
			{
					// read a String (which is an object)
					try {
						cm = (ChatMessage) sInput.readObject();
					}
					catch (IOException e) {
						display(username + " Exception reading Streams: " + e);
						break;				
					}
					catch(ClassNotFoundException e2) {
						break;
					}
					// the messaage part of the ChatMessage
				//	String message = cm.getMessage();

					// Switch on the type of message receive
					switch(cm.getType()) 
					{

					case ChatMessage.MESSAGE:
						System.out.println("client count>="+client_names.size());
						if (client_names.size()==3)
						{		System.out.println("prog will start now\n clients -");
									while(client_names.size()==3)
									{broadcast();}
							
						}
						break;
					case ChatMessage.LOGOUT:
						display(username + " disconnected with a LOGOUT message.");
						Server.removeClientnames(username);
						sg.append_Clnt_List();
						keepGoing = false;
						break;
					case ChatMessage.ONLINEUSER:
						writeMsg("\n");
						writeMsg("\n **************List of the users connected at " + sdf.format(new Date()) + "**********\n");
						// scan al the users connected
						for(int i = 0; i < al.size(); ++i) 
						{
							ClientThread ct = al.get(i);
							writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
						}
						writeMsg("\n");
						break;
					}
		//		}
			}
			// remove myself from the arrayList containing the list of the connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

//  Writes a String to the Client output stream
			private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}

