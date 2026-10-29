import java.io.InputStreamReader;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

//Java classes used to read and write data types into machine ready binary format
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;


//code from https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
public class client {
public static void main(String args[]){
    //The first statement in the try-with resources statement creates a new Socket object and names it echoSocket. The Socket constructor used here requires the name of the computer and the port number to which you want to connect. The example program uses the first command-line argument as the name of the computer (the host name) and the second command line argument as the port number.
    if(args.length != 2){
        System.err.println("Please input right command");
        return;
    }
    String host = args[0];
    int portNumber = Integer.parseInt(args[1]);
    try{
            portNumber = Integer.parseInt(args[1]);
            if(portNumber < 1 || portNumber > 65535){
                throw new NumberFormatException();
            }
        }catch( NumberFormatException e){
            System.err.println("Port must be an integer");
            return;
        }

    try(
        Socket socket = new Socket(host, portNumber);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    ){
        System.out.println(in.readUTF());
        while(true){
            System.out.println("Enter command: ");
            String userinput = stdIn.readLine();

            if(userinput == null){
                userinput = "bye";
            }
            
            long start = System.nanoTime();
            out.writeUTF(userinput);
            out.flush();
            //waiting for response from the server
            String response = in.readUTF();

            if(response.equals("disconnected")){
                System.out.println("exit");
                break;
            }
            if(!response.equals("OK")){
                System.out.println(response);// Error from server
                continue;
            }


            int length = in.readInt();
            
            //limit image size
            if(length < 0 || length > 50 * 1024 * 1024){
                throw new IOException("Invalid image size: " + length);
            }

            byte[] image = new byte[length];
            in.readFully(image);

            String number = userinput.substring(5);
            String filename = "received_image" + number + ".jpg";

            Files.write(Paths.get(filename), image);
            double time = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("Took %.3f ms%n", time);
        }


    }catch(IOException e){
        System.err.println("Client error: " + e.getMessage());
    }    
}
}
