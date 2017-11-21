import java.io.*;
import java.net.*;
import java.util.*;

/****************************************************
 *
 * This Class is intended to serve as a general utility class which should not
 * be used in a non-static way.
 *
 ****************************************************/
class Net_Util {
	public static final BufferedReader USER_INPUT = new BufferedReader(new InputStreamReader(System.in));

	public static Socket connectToServer(String serverIP, int portNumber) {
		try {
			return new Socket(serverIP, portNumber);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * In order to prevent memory leak, the user of this method is responsible
	 * for closing the Socket it gets.
	 *
	 * NOTE: this does not actually create a thread for it. It simply listnes
	 * One time and then returns.
	 *
	 **/
	public static Socket welcomeClient(int portNumber) {
		ServerSocket welcome;
		Socket connectionSocket;
		try {
			welcome = new ServerSocket(portNumber);

			connectionSocket = welcome.accept();

			System.out.println("Connection Established With " + connectionSocket.getInetAddress().toString());

			welcome.close();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return connectionSocket;
	}

	// TODO: Edit this to actually list commands.
	public static String promptInput() {// return a string asking the user to
										// enter commands and list the commands
		String out = "Please Enter a Command:\n> ";
		return out;
	}

	public static BufferedReader getReader(Socket readSocket) {
		BufferedReader b;
		try {
			b = new BufferedReader(new InputStreamReader(readSocket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}

	/**
	 * This is the start of the Send Functions. This is an overloaded method
	 * that takes every type we may need and sends it to the server without
	 * worry.
	 **/
	public static void send(Socket s, int i) {
		sendToServer("INT", i + "", s);
	}

	/*
	 * This is adaptable to any type. I just didnt feel like doing all types of
	 * arrays.
	 */
	public static void send(Socket s, int[] i) {
		String out = "";
		for (int c : i)
			out += c + "|";
		out = out.substring(0, out.length() - 1);
		sendToServer("INTARR", out, s);
	}

	public static void send(Socket s, boolean b) {
		String bool = (b) ? "True" : "False";
		sendToServer("BOOL", bool, s);
	}

	// public static void send(Socket s, Object o){
	// if(o instanceof Cerealizable)
	// sendToServer("OBJ",o.cerealize(),s);
	// }
	public static void send(Socket s, double d) {
		sendToServer("DOUBLE", d + "", s);
	}

	public static void send(Socket s, String str) {
		sendToServer("STRING", str + "\n", s);
	}

	public static void send(Socket s, String[] starr) {
		String out = "";
		for (String str : starr)
			out += str + "|";
		out = out.substring(0, out.length() - 1) + "\n";
		sendToServer("STRINGARR", out, s);
	}

	/*
	 * recieves file request, creates string array out of file and sends to
	 * requester
	 *
	 * @return if successful
	 */
	public static boolean sendStringFile(Socket requester, String filepath) {
		boolean successful = true;
		try {
			String requestedFile = recString(requester);
			File readFile = new File(filepath + requestedFile);
			InputStream reader = new FileInputStream(readFile);
			ArrayList<String> content = new ArrayList<String>();
			BufferedReader bufRead = new BufferedReader(new InputStreamReader(reader));
			String lineContent = bufRead.readLine();
			while (null != lineContent) {
				content.add(lineContent);
				lineContent = bufRead.readLine();
			}
			String[] sendFile = (String[]) content.toArray();
			send(requester, sendFile);
			requester.close();
			reader.close();
			bufRead.close();
		} catch (Exception e) {
			successful = false;
		}
		return successful;
	}

	/**
	 * This is the end of the Send block.
	 **/

	/*
	 * sends a file request and creates new file out of strings
	 *
	 * @return if successful
	 */
	public static boolean requestStringFile(Socket socket, String fileName) {
		boolean successful = true;
		try {
			send(socket, fileName); // send request for file
			File localCopy = new File("./sharingFiles/" + fileName); // create
																		// new
																		// file
			FileOutputStream writer = new FileOutputStream(localCopy);
			String[] contents = recStrArr(socket); // recieve file contents as
													// string

			// create a new file and write the strings from requested file to it
			if (localCopy.createNewFile()) {
				for (String lineContent : contents) {
					byte[] cont = lineContent.getBytes();
					writer.write(cont);
				}

			} else {
				successful = false;
			}

		} catch (Exception e) {
			successful = false;
		}
		return successful;
	}

	public static int recInt(Socket inSocket) throws IOException {
		String type = "INT";
		String[] tokens;

		// This does the thing below.
		BufferedReader in = getReader(inSocket);
		// BufferedReader in=new BufferedReader(new
		// InputStreamReader(inSocket.getInputStream()));

		tokens = in.readLine().split("::");
		if (!type.equals(tokens[0])) {
			System.out.println("Wrong type recieved. Expected " + type + ". Recieved" + tokens[0]);
			return (Integer) null;
		}
		String message = tokens[1];

		return Integer.parseInt(message);
	}

	public static int[] recIntArr(Socket inSocket) throws IOException {
		String type = "INTARR";
		BufferedReader in = getReader(inSocket);
		ArrayList<Integer> list = new ArrayList<>();
		String[] tokens;
		tokens = in.readLine().split("::");
		if (!type.equals(tokens[0])) {
			System.out.println("Wrong type recieved. Expected " + type + ". Recieved" + tokens[0]);
			return null;
		}
		StringTokenizer tok = new StringTokenizer(tokens[1], "|");
		while (tok.hasMoreTokens()) {
			list.add(Integer.parseInt(tok.nextToken()));
		}
		int[] arr = new int[list.size()];
		int count = 0;
		for (int i : list) {
			arr[count++] = i;
		}
		return arr;
	}

	public static boolean recBool(Socket inSocket) throws IOException {
		String type = "BOOL";
		BufferedReader in = getReader(inSocket);
		boolean bool;
		String[] tokens;
		tokens = in.readLine().split("::");
		if (!type.equals(tokens[0])) {
			System.out.println("Wrong type recieved. Expected " + type + ". Recieved" + tokens[0]);
			return (Boolean) null;
		}
		// FIXME:may not work
		bool = Boolean.parseBoolean(tokens[1]);
		return bool;
	}

	public static double recDouble(Socket inSocket) throws IOException {
		String type = "DOUBLE";
		BufferedReader in = getReader(inSocket);
		double dub;
		String[] tokens;
		tokens = in.readLine().split("::");
		if (!type.equals(tokens[0])) {
			System.out.println("Wrong type recieved. Expected " + type + ". Recieved" + tokens[0]);
			return (Double) null;
		}

		dub = Double.parseDouble(tokens[1]);
		return dub;
	}

	public static String recString(Socket inSocket) throws IOException {
		String type = "STRING";
		BufferedReader in = getReader(inSocket);
		String[] tokens;
		tokens = in.readLine().split("::");
		if (!type.equals(tokens[0])) {
			System.out.println("Wrong type recieved. Expected " + type + ". Recieved" + tokens[0]);
			return null;
		}
		return tokens[1];
	}

	public static String[] recStrArr(Socket inSocket) throws IOException {
		System.out.println("RECIEVING STRING ARRAY");

		String type = "STRINGARR";
		BufferedReader in = getReader(inSocket);
		ArrayList<String> list = new ArrayList<>();
		String[] tokens;
		String temp = in.readLine();
		tokens=temp.split("::");
		if (!type.equals(tokens[0])) {
			System.out.println("Wrong type recieved. Expected " + type + ". Recieved" + tokens[0]);
			return null;
		}
		StringTokenizer tok = new StringTokenizer(tokens[1], "|");
		while (tok.hasMoreTokens()) {
			list.add(tok.nextToken());
		}
		String[] arr = new String[list.size()];
		int count = 0;
		for (String str : list) {
			arr[count++] = str;
		}

		return arr;
	}

	// The user must deCerealize this string on their own. Hopefully they know
	// what the object is.
	public static String recObj(Socket inSocket) throws IOException {
		String type = "OBJ";
		String[] tokens;

		// This does the thing below.
		BufferedReader in = getReader(inSocket);
		// BufferedReader in=new BufferedReader(new
		// InputStreamReader(inSocket.getInputStream()));

		tokens = in.readLine().split("::");
		if (!type.equals(tokens[0])) {

			return "";
		}
		String message = tokens[1];
		return message;
	}

	/**
	 *
	 * Type and Message are separated by :: Arrays are separated by | type is
	 * the same as sendToServer
	 *
	 **/
	private static boolean recieveFromClient(String type, Socket inSocket) throws IOException {

		String inString = "";
		String[] tokens;
		String message;
		BufferedReader in = getReader(inSocket);
		inString = in.readLine();
		tokens = inString.split("::");
		if (!type.equals(tokens[0])) {
			return false;
		}
		message = tokens[1];

		switch (tokens[0]) {
		case "INT":
			Integer.parseInt(message);
			break;
		case "INTARR":
			break;
		case "BOOL":
			break;
		case "OBJ":
			break;
		case "DOUBLE":
			break;
		case "STRING":
			break;
		case "STRINGARR":
			break;
		}
		return false;
	}

	/**
	 *
	 * Type Inputs are as follows: INT INTARR BOOL OBJ DOUBLE STRING STRINGARR
	 *
	 **/

	private static void sendToServer(String type, String message, Socket s) {
		try {
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeBytes(type + "::" + message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// switch(type){
		// case "INT":
		// out.writeBytes(message);
		// break;
		// case "INTARR":
		// break;
		// case "BOOL":
		// break;
		// case "OBJ":
		// break;
		// case "DOUBLE":
		// break;
		// case "STRING":
		// break;
		// case "STRINGARR":
		// break;

	}

}
