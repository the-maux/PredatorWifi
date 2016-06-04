from PyQt4.QtCore import *
from PyQt4.QtGui import *
from re import search
from Core.Utils import Refactor
import Modules as pkg


class PopUpServer(QWidget):
    def __init__(self, FSettings):
        QWidget.__init__(self)
        self.FSettings = FSettings
        self.Ftemplates = pkg.frm_PhishingManager()
        self.layout = QVBoxLayout(self)
        self.FormLayout = QFormLayout()
        self.GridForm = QGridLayout()
        self.StatusLabel = QLabel(self)
        self.title = QLabel('::Server-HTTP::')
        self.btntemplates = QPushButton('Phishing M.')
        self.btnStopServer = QPushButton('Stop Server')
        self.btnRefresh = QPushButton('ReFresh')
        self.txt_IP = QLineEdit(self)
        self.txt_IP.setVisible(False)
        self.ComboIface = QComboBox(self)
        self.StatusServer(False)
        # icons
        self.btntemplates.setIcon(QIcon('rsc/page.png'))
        self.btnStopServer.setIcon(QIcon('rsc/close.png'))
        self.btnRefresh.setIcon(QIcon('rsc/refresh.png'))

        # conects
        self.refrash_interface()
        self.btntemplates.clicked.connect(self.show_template_dialog)
        self.btnStopServer.clicked.connect(self.StopLocalServer)
        self.btnRefresh.clicked.connect(self.refrash_interface)
        self.connect(self.ComboIface, SIGNAL("currentIndexChanged(QString)"), self.discoveryIface)

        # layout
        self.GridForm.addWidget(self.ComboIface, 0, 1)
        self.GridForm.addWidget(self.btnRefresh, 0, 2)
        self.GridForm.addWidget(self.btntemplates, 1, 1)
        self.GridForm.addWidget(self.btnStopServer, 1, 2)
        self.FormLayout.addRow(self.title)
        self.FormLayout.addRow(self.GridForm)
        self.FormLayout.addRow('Status::', self.StatusLabel)
        self.layout.addLayout(self.FormLayout)

    def emit_template(self, log):
        if log == 'started':
            self.StatusServer(True)

    def StopLocalServer(self):
        self.StatusServer(False)
        self.Ftemplates.killThread()

    def StatusServer(self, server):
        if server:
            self.StatusLabel.setText('[ ON ]')
            self.StatusLabel.setStyleSheet('QLabel {  color : green; }')
        elif not server:
            self.StatusLabel.setText('[ OFF ]')
            self.StatusLabel.setStyleSheet('QLabel {  color : red; }')

    def refrash_interface(self):
        self.ComboIface.clear()
        n = Refactor.get_interfaces()['all']
        for i, j in enumerate(n):
            if search('at', j) or search('wlan', j):
                self.ComboIface.addItem(n[i])
                self.discoveryIface()

    def discoveryIface(self):
        iface = str(self.ComboIface.currentText())
        ip = Refactor.get_Ipaddr(iface)
        self.txt_IP.setText(ip)

    def show_template_dialog(self):
        self.connect(self.Ftemplates, SIGNAL('Activated ( QString ) '), self.emit_template)
        self.Ftemplates.txt_redirect.setText(self.txt_IP.text())
        self.Ftemplates.show()
