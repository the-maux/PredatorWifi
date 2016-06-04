from PyQt4.QtGui import *
from PyQt4.QtCore import *
from subprocess import Popen, PIPE, STDOUT
from Core.Utils import setup_logger
import logging
from time import asctime

class ThRunDhcp(QThread):
    sendRequest = pyqtSignal(object)
    def __init__(self,args):
        QThread.__init__(self)
        self.args    = args
        self.process = None

    def run(self):
        print 'Starting Thread: ' + self.objectName()
        self.process = Popen(self.args, stdout=PIPE,stderr=STDOUT)
        setup_logger('dhcp', './Logs/AccessPoint/dhcp.log')
        loggerDhcp = logging.getLogger('dhcp')
        loggerDhcp.info('---[ Start DHCP ' + asctime() + ']---')
        for line,data in enumerate(iter(self.process.stdout.readline, b'')):
            if 'DHCPREQUEST for' in data.rstrip():
                self.sendRequest.emit(data.split())
            elif 'DHCPACK on' in data.rstrip():
                self.sendRequest.emit(data.split())
            loggerDhcp.info(data.rstrip())

    def stop(self):
        print 'Stop thread:' + self.objectName()
        if self.process is not None:
            self.process.terminate()
            self.process = None