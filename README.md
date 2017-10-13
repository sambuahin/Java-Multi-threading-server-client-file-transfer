# Java-Multi-threading-server-client-file-transfer

The program was coded in Java with the IntelliJ IDE but has been made to run in terminal based on how the file structure has been set up. The Server and Client are in two separate folders. Files are sent from one of the folders to the other and vice-versa. For example, to send from Client to Server, put file in Client and it will send to Server, and vice-versa. You only have to enter the filename of a file that is in either the Client of Server folder. The program will attach a path automatically. The Server is a multithreading Server which does not stop running after a Client disconnects and can keep running until you hit CTRl – C key combinations.
To run the programs:
1.	Extract the contents of the Zip file into your desired location.
2.	Open two terminals. One for the client and the other for the server.
3.	In the Server terminal, navigate into the folder named Server.
4.	In the Client terminal, navigate into the folder named Client.
Server:
5.	Start the Server program by typing:  javac HttpServerNew.java . 
6.	Enter: java HttpServerNew 
7.	Respond to the Server’s prompt by entering a port number to get started
8.	To stop the Server, hit the CTRL-C key combination.
Client:
9.	Start the Client by typing:  javac HttpClient.java . 
10.	Enter: java HttpClientNew
11.	Respond to the command on the client by entering the host, port, command and filename.

Input format should be:
host port command filename

eg: localhost 9999 get schoolpix.jpg

