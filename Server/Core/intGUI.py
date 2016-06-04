from PyQt4.QtGui import *
from PyQt4.QtCore import *
import Modules as pkg
from os import getcwd
from Core.PopUpServer import PopUpServer
from Core.helpers.update import frm_githubUpdate

def show_arp_posion(view):
    view.Farp_posion = pkg.frm_Arp_Poison()
    view.Farp_posion.setGeometry(0, 0, 450, 300)
    view.Farp_posion.show()


def show_update(view):
    view.FUpdate = frm_githubUpdate("0.1v")
    view.FUpdate.resize(480, 280)
    view.FUpdate.show()


def show_settings(view):
    view.FSettings.show()


def show_windows_update(view):
    view.FWinUpdate = pkg.frm_update_attack()
    view.FWinUpdate.setGeometry(QRect(100, 100, 450, 300))
    view.FWinUpdate.show()


def show_dhcpDOS(view):
    view.Fstar = pkg.frm_dhcp_Attack()
    view.Fstar.setGeometry(QRect(100, 100, 450, 200))
    view.Fstar.show()


def showProbe(view):
    view.Fprobe = pkg.frm_PMonitor()
    view.Fprobe.setGeometry(QRect(100, 100, 400, 800))
    view.Fprobe.show()


def formDauth(view):
    view.Fdeauth = pkg.frm_deauth()
    view.Fdeauth.setGeometry(QRect(100, 100, 200, 200))
    view.Fdeauth.show()


def form_mac(view):
    view.Fmac = pkg.frm_mac_generator()
    view.Fmac.setGeometry(QRect(100, 100, 300, 100))
    view.Fmac.show()


def show_dns_spoof(view):
    view.Fdns = pkg.frm_DnsSpoof()
    view.Fdns.setGeometry(QRect(100, 100, 450, 300))
    view.Fdns.show()


def show_PhishingManager(view):
    view.FPhishingManager = view.FormPopup.Ftemplates
    view.FPhishingManager.txt_redirect.setText('0.0.0.0')
    view.FPhishingManager.show()


def credentials(view):
    view.Fcredentials = pkg.frm_get_credentials()
    view.Fcredentials.setWindowTitle('Phishing Logger')
    view.Fcredentials.show()


def logsnetcreds(view):
    view.FnetCreds = pkg.frm_NetCredsLogger()
    view.FnetCreds.setWindowTitle('NetCreds Logger')
    view.FnetCreds.show()


def logdns2proxy(view):
    view.Fdns2proxy = pkg.frm_dns2proxy()
    view.Fdns2proxy.setWindowTitle('Dns2proxy Logger')
    view.Fdns2proxy.show()

def intGUI(view):
    view.myQMenuBar = QMenuBar(view)
    view.myQMenuBar.setFixedWidth(400)
    view.StatusBar = QStatusBar()
    view.StatusBar.setFixedHeight(15)
    view.StatusBar.addWidget(QLabel("::Access|Point::"))
    view.StatusDhcp = QLabel("")
    view.connectedCount = QLabel('')
    view.StatusDhcp = QLabel('')
    view.StatusBar.addWidget(view.StatusDhcp)
    view.Started(False)
    view.StatusBar.addWidget(QLabel(" " * 21))
    view.StatusBar.addWidget(QLabel("::Clients::"))
    view.connectedCount.setText("0")
    view.connectedCount.setStyleSheet("QLabel {  color : yellow; }")
    view.StatusBar.addWidget(view.connectedCount)

    Menu_file = view.myQMenuBar.addMenu('&File')
    exportAction = QAction('Export Html', view)
    deleteAction = QAction('Clear Logger', view)
    exitAction = QAction('Exit', view)
    exitAction.setIcon(QIcon('rsc/close-pressed.png'))
    deleteAction.setIcon(QIcon('rsc/delete.png'))
    exportAction.setIcon(QIcon('rsc/export.png'))
    # Menu_file.addAction(exportAction)
    Menu_file.addAction(deleteAction)
    Menu_file.addAction(exitAction)
    exitAction.triggered.connect(exit)
    deleteAction.triggered.connect(view.delete_logger)
    exportAction.triggered.connect(view.exportHTML)

    # Menu_View = self.myQMenuBar.addMenu('&View')
    phishinglog = QAction('Monitor Phishing', view)
    netcredslog = QAction('Monitor NetCreds', view)
    dns2proxylog = QAction('Monitor Dns2proxy', view)
    # connect
    phishinglog.triggered.connect(credentials)
    netcredslog.triggered.connect(logsnetcreds)
    dns2proxylog.triggered.connect(logdns2proxy)
    # icons
    phishinglog.setIcon(QIcon('rsc/password.png'))
    netcredslog.setIcon(QIcon('rsc/logger.png'))
    dns2proxylog.setIcon(QIcon('rsc/proxy.png'))
    # Menu_View.addAction(phishinglog)
    # Menu_View.addAction(netcredslog)
    # Menu_View.addAction(dns2proxylog)

    # tools Menu
    # Menu_tools = self.myQMenuBar.addMenu('&Tools')
    ettercap = QAction('Active Ettercap', view)
    btn_drift = QAction('Active DriftNet', view)
    btn_drift.setShortcut('Ctrl+Y')
    ettercap.setShortcut('Ctrl+E')
    ettercap.triggered.connect(view.start_etter)
    btn_drift.triggered.connect(view.start_dift)

    # icons tools
    ettercap.setIcon(QIcon('rsc/ettercap.png'))
    btn_drift.setIcon(QIcon('rsc/capture.png'))
    # Menu_tools.addAction(ettercap)
    # Menu_tools.addAction(btn_drift)

    # menu module
    Menu_module = view.myQMenuBar.addMenu('&Modules')
    btn_deauth = QAction('Deauth Attack', view)
    btn_probe = QAction('Probe Request', view)
    btn_mac = QAction('Mac Changer', view)
    btn_dhcpStar = QAction('DHCP S. Attack', view)
    btn_winup = QAction('Windows Update', view)
    btn_arp = QAction('Arp Posion Attack', view)
    btn_dns = QAction('Dns Spoof Attack', view)
    btn_phishing = QAction('Phishing Manager', view)
    action_settings = QAction('Settings', view)

    # connect buttons
    btn_probe.triggered.connect(showProbe)
    btn_deauth.triggered.connect(formDauth)
    btn_mac.triggered.connect(form_mac)
    btn_dhcpStar.triggered.connect(show_dhcpDOS)
    btn_winup.triggered.connect(show_windows_update)
    btn_arp.triggered.connect(show_arp_posion)
    btn_dns.triggered.connect(show_dns_spoof)
    btn_phishing.triggered.connect(show_PhishingManager)
    action_settings.triggered.connect(show_settings)

    # icons Modules
    btn_arp.setIcon(QIcon('rsc/arp_.png'))
    btn_winup.setIcon(QIcon('rsc/arp.png'))
    btn_dhcpStar.setIcon(QIcon('rsc/dhcp.png'))
    btn_mac.setIcon(QIcon('rsc/mac.png'))
    btn_probe.setIcon(QIcon('rsc/probe.png'))
    btn_deauth.setIcon(QIcon('rsc/deauth.png'))
    btn_dns.setIcon(QIcon('rsc/dns_spoof.png'))
    btn_phishing.setIcon(QIcon('rsc/page.png'))
    action_settings.setIcon(QIcon('rsc/setting.png'))

    # add modules menu
    # Menu_module.addAction(btn_deauth)
    Menu_module.addAction(btn_probe)
    # Menu_module.addAction(btn_mac)
    # Menu_module.addAction(btn_dhcpStar)
    # Menu_module.addAction(btn_winup)
    # Menu_module.addAction(btn_arp)
    # Menu_module.addAction(btn_dns)
    # Menu_module.addAction(btn_phishing)
    # Menu_module.addAction(action_settings)

    # menu extra
    # Menu_extra= self.myQMenuBar.addMenu('&Help')
    Menu_update = QAction('Update', view)
    Menu_about = QAction('About', view)
    Menu_issue = QAction('Submit issue', view)
    Menu_about.setIcon(QIcon('rsc/about.png'))
    Menu_issue.setIcon(QIcon('rsc/report.png'))
    Menu_update.setIcon(QIcon('rsc/update.png'))
    Menu_about.triggered.connect(view.about)
    Menu_issue.triggered.connect(view.issue)
    Menu_update.triggered.connect(show_update)
    # Menu_extra.addAction(Menu_issue)
    # Menu_extra.addAction(Menu_update)
    # Menu_extra.addAction(Menu_about)

    view.EditGateway = QLineEdit(view)
    view.EditApName = QLineEdit(view)
    view.EditChannel = QLineEdit(view)
    view.selectCard = QComboBox(view)
    view.EditGateway.setFixedWidth(120)
    view.EditApName.setFixedWidth(120)
    view.EditChannel.setFixedWidth(120)

    # table information AP connected
    view.TabInfoAP = QTableWidget(5, 3)
    view.TabInfoAP.setRowCount(100)
    view.TabInfoAP.setFixedHeight(150)
    view.TabInfoAP.setSelectionBehavior(QAbstractItemView.SelectRows)
    view.TabInfoAP.setEditTriggers(QAbstractItemView.NoEditTriggers)
    view.TabInfoAP.resizeColumnsToContents()
    view.TabInfoAP.resizeRowsToContents()
    view.TabInfoAP.horizontalHeader().resizeSection(0, 90)
    view.TabInfoAP.horizontalHeader().resizeSection(1, 120)
    view.TabInfoAP.horizontalHeader().resizeSection(2, 100)
    view.TabInfoAP.verticalHeader().setVisible(False)
    view.TabInfoAP.setHorizontalHeaderLabels(view.THeaders.keys())

    # edits
    view.mConfigure()
    view.FormGroup1 = QFormLayout()
    view.FormGroup2 = QFormLayout()
    view.FormGroup3 = QFormLayout()

    # get logo
    vbox = QVBoxLayout()
    vbox.setMargin(5)
    vbox.addStretch(20)
    view.FormGroup1.addRow(vbox)
    view.logo = QPixmap(getcwd() + '/rsc/logo.png')
    # self.logo.
    view.imagem = QLabel(view)
    view.imagem.setPixmap(view.logo)
    view.FormGroup1.addRow(view.imagem)

    # popup settings
    view.btnPlugins = QToolButton(view)
    view.btnPlugins.setFixedHeight(25)
    view.btnPlugins.setIcon(QIcon('rsc/plugins.png'))
    view.btnPlugins.setText('[::Plugins::]')
    view.btnPlugins.setPopupMode(QToolButton.MenuButtonPopup)
    view.btnPlugins.setMenu(QMenu(view.btnPlugins))
    action = QWidgetAction(view.btnPlugins)
    action.setDefaultWidget(view.PopUpPlugins)
    view.btnPlugins.menu().addAction(action)

    view.btnHttpServer = QToolButton(view)
    view.btnHttpServer.setFixedHeight(25)
    view.btnHttpServer.setIcon(QIcon('rsc/phishing.png'))
    view.FormPopup = PopUpServer(view.FSettings)
    view.btnHttpServer.setPopupMode(QToolButton.MenuButtonPopup)
    view.btnHttpServer.setMenu(QMenu(view.btnHttpServer))
    action = QWidgetAction(view.btnHttpServer)
    action.setDefaultWidget(view.FormPopup)
    view.btnPlugins.menu().addAction(action)

    view.GroupAP = QGroupBox()
    view.GroupAP.setTitle('Access Point::')
    view.FormGroup3.addRow('Gateway:', view.EditGateway)
    view.FormGroup3.addRow('AP Name:', view.EditApName)
    view.FormGroup3.addRow('Channel:', view.EditChannel)
    view.GroupAP.setLayout(view.FormGroup3)

    # grid network adapter fix
    view.btrn_refresh = QPushButton('Refresh')
    view.btrn_refresh.setIcon(QIcon('rsc/refresh.png'))
    view.btrn_refresh.clicked.connect(view.refrash_interface)

    view.layout = QFormLayout()
    view.GroupAdapter = QGroupBox()
    view.GroupAdapter.setFixedWidth(120)
    view.GroupAdapter.setTitle('Network Adapter::')
    view.layout.addRow(view.selectCard)
    view.layout.addRow(view.btrn_refresh)
    view.layout.addRow(view.btnPlugins, view.btnHttpServer)
    view.GroupAdapter.setLayout(view.layout)

    view.btn_start_attack = QPushButton('Start Access Point', view)
    view.btn_start_attack.setIcon(QIcon('rsc/start.png'))
    view.btn_cancelar = QPushButton('Stop Access Point', view)
    view.btn_cancelar.setIcon(QIcon('rsc/Stop.png'))
    view.btn_cancelar.clicked.connect(view.kill)
    view.btn_start_attack.clicked.connect(view.StartApFake)

    hBox = QHBoxLayout()
    hBox.addWidget(view.btn_start_attack)
    hBox.addWidget(view.btn_cancelar)

    view.slipt = QHBoxLayout()
    view.slipt.addWidget(view.GroupAP)
    view.slipt.addWidget(view.GroupAdapter)

    view.FormGroup2.addRow(hBox)
    view.FormGroup2.addRow(view.TabInfoAP)
    view.FormGroup2.addRow(view.StatusBar)
    view.Main.addLayout(view.FormGroup1)
    view.Main.addLayout(view.slipt)
    view.Main.addLayout(view.FormGroup2)
    view.setLayout(view.Main)


