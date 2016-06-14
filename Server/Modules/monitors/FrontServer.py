from PyQt4 import QtGui
from PyQt4 import QtNetwork
from PyQt4 import QtCore
import time

class LogSniffer(QtCore.QThread):
    def __init__(self, parent):
        QtCore.QThread.__init__(self, parent)
        self.listLogs = [open('Logs/LogProbeScan', 'r'), open('Logs/LogProbeScan', 'r'), open('Logs/LogProbeScan', 'r'), open('Logs/LogProbeScan', 'r')]
        QtCore.QObject.connect(parent, QtCore.SIGNAL("stop"), self.stop)

    def run(self):
        while 1:
            rcx = 0
            for log in self.listLogs:
                self.logString = log.readline()
                if not self.logString: rcx += 1
                else: self.emit(QtCore.SIGNAL("LogToSend"))
            if rcx == 4:
                time.sleep(2)
    def stop(self):
        print "Log:Sniffer Stop"

class FrontServer(QtCore.QObject):
    def __init__(self):
        super(FrontServer, self).__init__()
        self.tcpServer = QtNetwork.QTcpServer()
        self.tcpServer.listen(QtNetwork.QHostAddress("127.0.0.1"), 1234)
        print "FrontServer: Listening"
        self.tcpServer.newConnection.connect(self.addConnection)
        self.logger = LogSniffer(self)
        QtCore.QObject.connect(self.logger, QtCore.SIGNAL("LogToSend"), self.sendMessage)

    def addConnection(self):
        print "FrontServer: Adding Connection Front"
        try:
            self.clientConnection = self.tcpServer.nextPendingConnection()
            self.clientConnection.nextBlockSize = 0
            self.clientConnection.readyRead.connect(self.receiveMessage)
            self.clientConnection.disconnected.connect(self.removeConnection)
            self.clientConnection.error.connect(self.socketError)
            self.stream = QtCore.QDataStream(self.clientConnection)
            self.stream.setVersion(QtCore.QDataStream.Qt_4_2)
            self.logger.start()
        except Exception as ex:
            QtGui.QMessageBox.information(None, "Error", ex.message)

    def receiveMessage(self):
        print "FrontServer: in Received"
        if self.clientConnection.bytesAvailable() > 0:
            self.Message = self.stream.readRawData(self.clientConnection.bytesAvailable())
            print "Received Message :" + str(self.Message)
            self.clientConnection.nextBlockSize = 0

            self.emit(QtCore.SIGNAL('DataReceived'))
            self.clientConnection.nextBlockSize = 0

    def sendMessage(self):
        print "FrontServer: in sendMessage"
        self.request = QtCore.QByteArray()
        self.stream.writeRawData(self.logger.logString)

    def removeConnection(self):
        pass

    def socketError(self):
        pass