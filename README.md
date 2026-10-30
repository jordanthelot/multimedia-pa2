Client server, sending JPEG images program

How to Run:

Make sure all files are in the same folder

Navigate to folder within terminal, make sure to hava JAVA installed

Compile the two files using - javac server.java client.java

Initiate the server connection on one host/terminal window using - java server (portnumber)

If the server connection was initated on a different host-

    Start the client connection using- java client (ip_address_of_host) (portnumber)

If the server conneciton was initiated on a different termianl window, but on the same host

    Start the client connection using- java client localhost (portnumber)

Start the image transfer using the commands - JPEG 1, JPEG 2, or JPEG 3

Every other command gets an "invalid command" response

End the conneciton by saying - bye - to the server
