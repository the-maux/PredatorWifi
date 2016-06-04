from PyQt4.QtGui import *
from PyQt4.QtCore import *
from re import search

class PopUpPlugins(QWidget):
    def __init__(self, FSettings):
        QWidget.__init__(self)
        self.FSettings = FSettings
        self.layout = QVBoxLayout(self)
        self.title = QLabel('::Available Plugins::')
        self.check_sslstrip = QCheckBox('::ssLstrip')
        self.check_netcreds = QCheckBox('::net-creds')
        self.check_dns2proy = QCheckBox('::dns2proxy')
        self.check_dns2proy.clicked.connect(self.checkBoxDns2proxy)
        self.check_sslstrip.clicked.connect(self.checkBoxSslstrip)
        self.check_netcreds.clicked.connect(self.checkBoxNecreds)
        self.layout.addWidget(self.title)
        self.layout.addWidget(self.check_sslstrip)
        self.layout.addWidget(self.check_netcreds)
        self.layout.addWidget(self.check_dns2proy)

    # control checkbox plugins
    def checkBoxSslstrip(self):
        if not self.check_sslstrip.isChecked():
            self.unset_Rules('sslstrip')
            self.FSettings.xmlSettings('sslstrip_plugin', 'status', 'False', False)
        elif self.check_sslstrip.isChecked():
            self.set_sslStripRule()
            self.FSettings.xmlSettings('sslstrip_plugin', 'status', 'True', False)

    def checkBoxDns2proxy(self):
        if not self.check_dns2proy.isChecked():
            self.unset_Rules('dns2proxy')
            self.FSettings.xmlSettings('dns2proxy_plugin', 'status', 'False', False)
        elif self.check_dns2proy.isChecked():
            self.set_Dns2proxyRule()
            self.FSettings.xmlSettings('dns2proxy_plugin', 'status', 'True', False)

    def checkBoxNecreds(self):
        if self.check_netcreds.isChecked():
            self.FSettings.xmlSettings('netcreds_plugin', 'status', 'True', False)
        else:
            self.FSettings.xmlSettings('netcreds_plugin', 'status', 'False', False)

    # set rules to sslstrip
    def set_sslStripRule(self):
        item = QListWidgetItem()
        item.setText('iptables -t nat -A PREROUTING -p ' +
                     'tcp --destination-port 80 -j REDIRECT --to-port ' + self.FSettings.redirectport.text())
        item.setSizeHint(QSize(30, 30))
        self.FSettings.ListRules.addItem(item)

    # set redirect port rules dns2proy
    def set_Dns2proxyRule(self):
        item = QListWidgetItem()
        item.setText('iptables -t nat -A PREROUTING -p ' +
                     'udp --destination-port 53 -j REDIRECT --to-port 53')
        item.setSizeHint(QSize(30, 30))
        self.FSettings.ListRules.addItem(item)

    def unset_Rules(self, type):
        items = []
        for index in xrange(self.FSettings.ListRules.count()):
            items.append(str(self.FSettings.ListRules.item(index).text()))
        for i, j in enumerate(items):
            if type == 'sslstrip':
                if search(str('tcp --destination-port 80 -j REDIRECT --to-port ' +
                                      self.FSettings.redirectport.text()), j):
                    self.FSettings.ListRules.takeItem(i)
            elif type == 'dns2proxy':
                if search('udp --destination-port 53 -j REDIRECT --to-port 53', j):
                    self.FSettings.ListRules.takeItem(i)

