import socket
import sys
import time
import signal

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
log = open('./../LogProbeScan', 'r')
server_address = ("10.0.0.1", 42429)
print >>sys.stderr, 'starting up on %s port %s' % server_address
sock.bind(server_address)
sock.listen(1)
print >>sys.stderr, 'waiting for a connection' 

def reloading(sock, str):
	print "reloading"
	sock.close()
	time.sleep(1)
	while sock == 0x00:
		try :
			sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			sock.bind(server_address)
			sock.listen(1)
			Myloop(str, sock)
		except socket.error :
			sock = 0x00

def Myloop(str, sock):
	connection, client_address = sock.accept()
	print >>sys.stderr, 'client connected:', client_address
	try:
		while (True):
			res = log.readline()
   		 	if (res == ""):
   		 		time.sleep(1)
   		 	else:
   		 		print "data { " + res + "}" 
        		connection.sendall(res)
        		dataRcv = ""
        		while dataRcv == "":
        			dataRcv = connection.recv(42)
        			if dataRcv == "@+" : reloading(sock)
        			print "data rcv {" + dataRcv + "}"
        			if dataRcv == "" : time.sleep(1)
	except:
		print "connection closed"
	finally:
		reloading(sock, str)
	
str = "toto"
Myloop(str, sock)
