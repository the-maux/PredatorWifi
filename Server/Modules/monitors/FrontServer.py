from Core.loaders.Stealth.PackagesUI import *
from threading import Thread
import socket
import sys
import time
import os

class FrontServer(Thread):
    def __init__(self, main):
        Thread.__init__(self)
        self.main = main

    def run(self):
        self.Init()

    def Init(self):
        print "Current working dir : %s" % os.getcwd()
#        logDico = ((), (), (), (), ())
        self.main.form_widget.btn_probe.trigger()
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.log = open('Logs/LogProbeScan', 'r')
        self.server_address = ("localhost", 42429)
        print >> sys.stderr, 'starting up on %s port %s' % self.server_address
        self.sock.bind(self.server_address)
        self.sock.listen(1)
        print >> sys.stderr, 'waiting for a connection'
        self.myLoop()

    def reloading(self):
        print "reloading"
        self.sock.close()
        time.sleep(1)
        while self.sock == 0x00:
            try:
                self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self.sock.bind(self.server_address)
                self.sock.listen(1)
                self.myLoop()
            except self.socket.error:
                sock = 0x00

    def myLoop(self):
        connection, client_address = self.sock.accept()
        print >> sys.stderr, 'client connected:', client_address
        try:
            while (True):
                res = log.readline()
                if res == "":
                    time.sleep(1)
                else:
                    print "data { " + res + "}"
                    connection.sendall(res)
                buffIN = ""
                while buffIN == "" :
                    buffIN = connection.recv(42)
                    if buffIN == "@+" : self.reloading()
                    print "data rcv {" + buffIN + "}"
                    if buffIN == "" : time.sleep(1)
        except:
            print "connection closed"
        finally:
            self.reloading()
