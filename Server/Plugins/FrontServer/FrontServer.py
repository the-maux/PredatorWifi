from PyQt4 import QtGui
from PyQt4 import QtNetwork
from PyQt4 import QtCore
import time

class LogSniffer(QtCore.QThread):
    def __init__(self, parent):
        QtCore.QThread.__init__(self, parent)
        self.Log = open('Logs/LogProbeScan', 'r')
        QtCore.QObject.connect(parent, QtCore.SIGNAL("stop"), self.stop)
        QtCore.QObject.connect(parent, QtCore.SIGNAL("Acknowledge client"), self.logging)
        self.rcx = 0

    def run(self):
        print "LogSniffer launched"
        self.logString = self.Log.readline()
        if not self.logString:
            self.rcx += 1
            self.logging()
        else :
            self.emit(QtCore.SIGNAL("LogToSend"))


    def logging(self):
        self.logString = self.Log.readline()
        if not self.logString:
            self.rcx += 1
        else:
            self.emit(QtCore.SIGNAL("LogToSend"))
        if self.rcx == 4:
            self.rcx = 0
            self.logging()

    def stop(self):
        print "Log:Sniffer Stop"

class ServerFront(QtCore.QObject):
    def __init__(self, threadFather):
        super(ServerFront, self).__init__()
        self.tcpServer = QtNetwork.QTcpServer()
        self.logger = LogSniffer(self)
        QtCore.QObject.connect(threadFather, QtCore.SIGNAL("launch front"), self.run)

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
            if not self.logger.isRunning():
                self.logger.start()
        except Exception as ex:
            QtGui.QMessageBox.information(None, "Error", ex.message)

    def receiveMessage(self):
        if self.clientConnection.bytesAvailable() > 0:
            self.Message = self.stream.readRawData(self.clientConnection.bytesAvailable())
            print "Received Message :" + str(self.Message)
            self.clientConnection.nextBlockSize = 0
            if "Ack" in str(self.Message):
                self.logger.ack = True
                self.emit(QtCore.SIGNAL('Acknowledge client'))
            else:
                MsgTmp = self.Message
                for message in MsgTmp.split("\n"):
                    self.Message = message
                    self.emit(QtCore.SIGNAL('DataReceived'))
    def run(self):
        print "FrontServer : run"
        self.tcpServer.listen(QtNetwork.QHostAddress.Any, 1234)
        self.tcpServer.newConnection.connect(self.addConnection)
        QtCore.QObject.connect(self.logger, QtCore.SIGNAL("LogToSend"), self.sendMessage)

    def sendMessage(self):
        self.request = QtCore.QByteArray()
        self.stream.writeRawData(self.logger.logString)
        print "Sended : " + self.logger.logString

    def removeConnection(self):
        pass

    def socketError(self):
        pass

    def shutdownServer(self):
        self.tcpServer.close()