server: Server.java ServerClientHandler.java
	clear
	javac Server.java && java Server

client: Client.java Server.java
	clear
	javac Client.java && java Client

.PHONY: clean
clean:
	rm *.class
