# ChatApplication
Designed a reliable multiparty chat application which can also send Multimedia.

Analysis and approach
We analyzed the problem statement and divided it into following steps:
Step 1: One Client to Server communication
Initially, we established communication between single client and single server is by creating sockets
Step 2: Multiple Client to Server communication
Next, multiple clients must be created using multithreading. For each client two independent thread should be there for writing to OutputStream and reading InputStream
Step 3: Sending text messages 
Once multiple clients are created, transfer of text messages should be implemented by using FileInputstream. The graphical user interface should be created to facilitate server and client communication
Step 4: Sending text and pdf files
A client should be able to browse and attach file. Finally, files should be sent to destination client
Step 5: Sending image files
A client should be able to browse and attach image file. Finally, files should be sent to destination client
Step 6: Sending audio and video files
A client should be able to browse and attach audio and video file. Finally, files should be sent to destination client
Step7: Making Connection reliable
Whenever a client is disconnected. Then other clients and server are notified.
