# Prova Finale Ingegneria del Software 2019  
## Gruppo AM19  
  
- ###   10536115    Daniele Chiappalupi ([@daniCh8](https://github.com/daniCh8))<br>daniele.chiappalupi@mail.polimi.it  
- ###   10520490    Stefano Formicola ([@ste7en](https://github.com/ste7en))<br>stefano.formicola@mail.polimi.it  
- ###   10583425    Elena Iannucci ([@eleiannu](https://github.com/eleiannu))<br>elena.iannucci@mail.polimi.it  
  
| Functionality | State |  
|:-----------------------|:------------------------------------:|  
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |  
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |  
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |  
| RMI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |  
| GUI | [![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#) |  
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |  
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |  
| Persistence | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |  
| Domination or Towers modes | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |  
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |  
  
<!--  
[![RED](https://placehold.it/15/f03c15/f03c15)](#)  
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)  
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)  
-->

## How to run
### Running the Server
From the directory [deliverables/JARS](./deliverables/JARS), you can run the application server with the command-line command

    java -jar Server-AM19.jar [--socket=<socket-port-number>] 
		                  [--rmi=<rmi-port-number>] 
		        	  [--waiting-room-timeout=<waiting-room-timeout-in-seconds>] 
	    		          [--operation-timeout=<operation-timeout-in-seconds>]  

If a `configuration.json` file exists in the same directory, CLI arguments will be overridden and the file will be used to setup the server. 
The structure of the JSON configuration file is as the following example:

    {  
     "socketPortNumber" : 3334,  
      "rmiPortNumber" : 4444,  
      "waitingRoomTimeout" : 30,  
      "operationTimeout" : 60  
    }
This is also the config file that will be used in case no arguments (or less then expected) will be passed to the program.

#### Multiple games
The server can handle multiple games. In order to start more than one game, more than five users are needed to login and every `waitingRoomTimeout` seconds a new game is started.

#### Persistence
At the end of every turn the server saves its state in `Adrenaline_saved_state.bin` and, if this file exists, the server will ask if a previous state has to be loaded or not. 

Otherwise, the command `--run-saved-state` will restore the server regardless of the other parameters.

### Running the Client - CLI
From the same [directory](./deliverables/JARS) of the server, you can run the CLI version of the client with the command-line command

    java -jar Adrenaline-CLI-AM19.jar [ < --socket | --rmi > 
                                        < serverAddress> 
                                        < serverPort> ]
If no arguments are provided, at the startup a command-line prompt will ask the user for the connection type, server IP address and server port.
