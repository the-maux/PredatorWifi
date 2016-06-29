
from sys import exit,stdout

from time import sleep,asctime,strftime
from datetime import datetime
import threading
from threading import Thread
import Queue
from scapy.all import *
from PyQt4.QtCore import *
from PyQt4.QtGui import *
import logging

class ThreadProbeScan(QThread):
    def __init__(self,interface):
        self.myLogProbeScan = open("./LogProbeScan", 'a')
        QThread.__init__(self)
        self.interface  = interface
        self.finished   = False

    def run(self):
        print "Starting Thread:" + self.objectName()
        self.ProbeResqest()

    def Startprobe(self,q):
        while not self.finished:
            try:
                sniff(iface = self.interface,count = 10, prn = lambda x : q.put(x))
            except:pass
            if self.finished:break

    def ProbeResqest(self):
        q = Queue.Queue()
        sniff = Thread(target =self.Startprobe, args = (q,))
        sniff.daemon = True
        sniff.start()
        while (not self.finished):
            try:
                pkt = q.get(timeout = 1)
                self.sniff_probe(pkt)
            except Queue.Empty:
              pass

    def LogServerMobile(self, mac_address, ssid, devices):
        if ssid != 'Hidden':
            time = str(datetime.now())
            time = (time[time.find(" ")+1:time.find(".")])[:-3]
            self.myLogProbeScan.write("Probe:%s#%s*%s;%s/%s\n" % (time, "true", devices, ssid, mac_address));
            self.myLogProbeScan.flush()

    def sniff_probe(self,p):
        if (p.haslayer(Dot11ProbeReq)):
                mac_address=(p.addr2)
                ssid=p[Dot11Elt].info
                ssid=ssid.decode('utf-8','ignore')
                if ssid == '':ssid='Hidden'
                try:
                    devices = EUI(mac_address)
                    devices = devices.oui.registration().org
                except:
                    devices = 'Inconnu'
                self.LogServerMobile(mac_address, ssid, devices)
                self.emit(SIGNAL("Activated( QString )"),mac_address + '|' + ssid + '|' + devices)

    def stop(self):
    	print "close"
        print "Stop thread:" + self.objectName()
        self.myLogProbeScan.close()
        self.finished = True