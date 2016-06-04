from sys import argv

from re import search


from ast import literal_eval

from os import system, path, chdir, popen, listdir

from isc_dhcp_leases.iscdhcpleases import IscDhcpLeases

from Core.intGUI import *
from Core.Utils import ProcessThread, Refactor, set_monitor_mode
from Core.PopUpPlugins import  PopUpPlugins
from Core.config.Settings import frm_Settings
from Core.helpers.about import frmAbout


if search('/usr/share/', argv[0]): chdir('/usr/share/WiFi-Pumpkin/')

author = ' P0cl4bs Team [mh4root | S4mb3ck | 7h3-m4ux | M4wu3n4]'
emails = ['mh4root@gmail.com', 'p0cl4bs@gmail.com', 'dthe-maux@hotmail.fr']
license = ' GNU GPL 3'
update = '29/12/2015'
desc = ['Framework for Rogue Wi-Fi Access Point Attacks']


class Initialize(QMainWindow):
    def __init__(self, parent=None):
        super(Initialize, self).__init__(parent)
        self.form_widget = SubMain(self)
        self.FSettings = frm_Settings()
        self.setCentralWidget(self.form_widget)
        self.setWindowTitle('Epitech Sniffer' + "0.1v")
        self.loadtheme(self.FSettings.XmlThemeSelected())

    def loadtheme(self, theme):
        sshFile = ("Core/%s.qss" % (theme))
        with open(sshFile, "r") as fh:
            self.setStyleSheet(fh.read())

    def center(self):
        framegm = self.frameGeometry()
        centerPoint = QDesktopWidget().availableGeometry().center()
        framegm.moveCenter(centerPoint)
        self.move(framegm.topLeft())

    def closeEvent(self, event):
        outputiwconfig = popen('iwconfig').readlines()
        for i in outputiwconfig:
            if search('Mode:Monitor', i):
                reply = QMessageBox.question(self, 'About Exit', 'Are you sure to quit?',
                                             QMessageBox.Yes | QMessageBox.No, QMessageBox.No)
                if reply == QMessageBox.Yes:
                    event.accept()
                    set_monitor_mode(i.split()[0]).setDisable()
                    return
                event.ignore()

class SubMain(QWidget):
    def __init__(self, parent=None):
        super(SubMain, self).__init__(parent)
        self.myLogProbeScan = open("./LogProbeScan", 'a')
        # self.create_sys_tray()
        self.Main = QVBoxLayout()
        self.Apthreads = {'RougeAP': []}
        self.APclients = {}
        self.ConfigTwin = {
            'ProgCheck': [], 'AP_iface': None,
            'PortRedirect': None, 'interface': 'None'}
        self.THeaders = {'ip-address': [], 'device': [], 'mac-address': []}
        self.FSettings = frm_Settings()
        self.PopUpPlugins = PopUpPlugins(self.FSettings)
        self.setGeometry(0, 0, 300, 400)
        self.checkPlugins()
        intGUI(self)

    def checkPlugins(self):
        if literal_eval(self.FSettings.xmlSettings('sslstrip_plugin', 'status', None, False)):
            self.PopUpPlugins.check_sslstrip.setChecked(True)
            self.PopUpPlugins.set_sslStripRule()
        if literal_eval(self.FSettings.xmlSettings('netcreds_plugin', 'status', None, False)):
            self.PopUpPlugins.check_netcreds.setChecked(True)
        if literal_eval(self.FSettings.xmlSettings('dns2proxy_plugin', 'status', None, False)):
            self.PopUpPlugins.check_dns2proy.setChecked(True)
            self.PopUpPlugins.set_Dns2proxyRule()

    def Started(self, bool):
        if bool:
            self.StatusDhcp.setText("[ON]")
            self.StatusDhcp.setStyleSheet("QLabel {  color : green; }")
        else:
            self.StatusDhcp.setText("[OFF]")
            self.StatusDhcp.setStyleSheet("QLabel {  color : red; }")

    def StatusDHCPRequests(self, key):
        print('Connected::[{}] hostname::[{}]'.format(key, self.APclients[key]['device']))

    def GetDHCPRequests(self, data):
        print "in GetDHCPRequests"
        if len(data) == 8:
            if Refactor.check_is_mac(data[4]):
                if data[4] not in self.APclients.keys():
                    self.myLogProbeScan.write("TargetConnection#%s*%s;%s\n" % (data[2], data[5], data[4]));
                    self.myLogProbeScan.flush();
                    self.APclients[data[4]] = {'IP': data[2], 'device': data[5],
                                               'in_tables': False,}
                    self.StatusDHCPRequests(data[4])
        elif len(data) == 9:
            if Refactor.check_is_mac(data[5]):
                if data[5] not in self.APclients.keys():
                    self.myLogProbeScan.write("TargetConnection#%s*%s;%s\n" % (data[2], data[6], data[5]));
                    self.myLogProbeScan.flush();
                    self.APclients[data[5]] = {'IP': data[2], 'device': data[6],
                                               'in_tables': False,}
                    self.StatusDHCPRequests(data[5])
        elif len(data) == 7:
            if Refactor.check_is_mac(data[4]):
                if data[4] not in self.APclients.keys():
                    leases = IscDhcpLeases('Settings/dhcp/dhcpd.leases')
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
                    if hostname == None: hostname = 'unknown'
                    self.APclients[data[4]] = {'IP': data[2], 'device': hostname,
                                               'in_tables': False,}
                    self.StatusDHCPRequests(data[4])
                    self.APclients[data[4]] = {'IP': data[2], 'device': hostname, 'in_tables': False,}
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

    def GetHostapdStatus(self, data):
        if self.APclients != {}:
            if data in self.APclients.keys():
                print('Disconnected::[{}] hostname::[{}]'.format(data, self.APclients[data]['device']))
        for row in xrange(0, self.TabInfoAP.rowCount()):
            if self.TabInfoAP.item(row, 1) != None:
                if self.TabInfoAP.item(row, 1).text() == data:
                    self.TabInfoAP.removeRow(row)
                    if data in self.APclients.keys():
                        del self.APclients[data]
        for mac_tables in self.APclients.keys(): self.APclients[mac_tables]['in_tables'] = False
        self.THeaders = {'ip-address': [], 'device': [], 'mac-address': []}
        self.connectedCount.setText(str(len(self.APclients.keys())))

    def mConfigure(self):
        self.get_interfaces = Refactor.get_interfaces()
        try:
            self.EditGateway.setText(
                [self.get_interfaces[x] for x in self.get_interfaces.keys() if x == 'gateway'][0])
        except:
            pass
        self.EditApName.setText(self.FSettings.xmlSettings('AP', 'name', None, False))
        self.EditChannel.setText(self.FSettings.xmlSettings('channel', 'mchannel', None, False))
        self.ConfigTwin['PortRedirect'] = self.FSettings.redirectport.text()
        for i, j in enumerate(self.get_interfaces['all']):
            if search('wlan', j): self.selectCard.addItem(self.get_interfaces['all'][i])
        driftnet = popen('which driftnet').read().split('\n')
        ettercap = popen('which ettercap').read().split('\n')
        dhcpd = popen('which dhcpd').read().split("\n")
        dnsmasq = popen('which dnsmasq').read().split("\n")
        hostapd = popen('which hostapd').read().split("\n")
        lista = ['/usr/sbin/airbase-ng', ettercap[0], driftnet[0], dhcpd[0], dnsmasq[0], hostapd[0]]
        for i in lista: self.ConfigTwin['ProgCheck'].append(path.isfile(i))

    def exportHTML(self):
        contents = Refactor.exportHtml()
        filename = QFileDialog.getSaveFileNameAndFilter(self,
                                                        'Save File Logger HTML', 'report.html', 'HTML (*.html)')
        if len(filename) != 0:
            with open(str(filename[0]), 'w') as filehtml:
                filehtml.write(contents['HTML']), filehtml.close()
            QMessageBox.information(self, 'WiFi Pumpkin', 'file has been saved with success.')

    def refrash_interface(self):
        self.selectCard.clear()
        n = Refactor.get_interfaces()['all']
        for i, j in enumerate(n):
            if search('wlan', j):
                self.selectCard.addItem(n[i])

    def kill(self):
        if self.Apthreads['RougeAP'] == []: return
        self.FSettings.xmlSettings('statusAP', 'value', 'False', False)
        for i in self.Apthreads['RougeAP']: i.stop()
        for kill in self.SettingsAP['kill']: popen(kill)
        set_monitor_mode(self.ConfigTwin['interface']).setDisable()
        self.Started(False)
        self.Apthreads['RougeAP'] = []
        self.APclients = {}
        with open('Settings/dhcp/dhcpd.leases', 'w') as dhcpLease:
            dhcpLease.write(''), dhcpLease.close()
        self.btn_start_attack.setDisabled(False)
        Refactor.set_ip_forward(0)
        self.TabInfoAP.clearContents()
        try:
            self.FormPopup.Ftemplates.killThread()
            self.FormPopup.StatusServer(False)
        except AttributeError as e:
            print e

    def delete_logger(self):
        content = Refactor.exportHtml()
        if listdir('Logs') != '':
            resp = QMessageBox.question(self, 'About Delete Logger',
                                        'do you want to delete Logs?', QMessageBox.Yes |
                                        QMessageBox.No, QMessageBox.No)
            if resp == QMessageBox.Yes:
                system('rm Logs/Caplog/*.cap')
                for keyFile in content['Files']:
                    with open(keyFile, 'w') as f:
                        f.write(''), f.close()

    def start_etter(self):
        if self.ConfigTwin['ProgCheck'][1]:
            if search(str(self.ConfigTwin['AP_iface']), str(popen('ifconfig').read())):
                Thread_Ettercap = ProcessThread(['sudo', 'xterm', '-geometry', '73x25-1+50',
                                                 '-T', 'ettercap', '-s', '-sb', '-si', '+sk', '-sl',
                                                 '5000', '-e', 'ettercap', '-p', '-u', '-T', '-q', '-w',
                                                 'Logs/passwords', '-i', self.ConfigTwin['AP_iface']])
                Thread_Ettercap.setName('Tool::Ettercap')
                self.Apthreads['RougeAP'].append(Thread_Ettercap)
                Thread_Ettercap.start()
            return
        QMessageBox.information(self, 'ettercap', 'ettercap not found.')

    def start_dift(self):
        if self.ConfigTwin['ProgCheck'][2]:
            if search(str(self.ConfigTwin['AP_iface']), str(popen('ifconfig').read())):
                Thread_driftnet = ProcessThread(['sudo', 'xterm', '-geometry', '75x15+1+200',
                                                 '-T', 'DriftNet', '-e', 'driftnet', '-i', self.ConfigTwin['AP_iface']])
                Thread_driftnet.setName('Tool::Driftnet')
                self.Apthreads['RougeAP'].append(Thread_driftnet)
                Thread_driftnet.start()
            return
        QMessageBox.information(self, 'driftnet', 'driftnet not found.')





    def create_sys_tray(self):
        self.sysTray = QSystemTrayIcon(self)
        self.sysTray.setIcon(QIcon('rsc/icon.ico'))
        self.sysTray.setVisible(True)
        self.connect(self.sysTray, SIGNAL('activated(QSystemTrayIcon::ActivationReason)'),
                     self.on_sys_tray_activated)
        self.sysTrayMenu = QMenu(self)
        self.sysTrayMenu.addAction('FOO')

    def on_sys_tray_activated(self, reason):
        if reason == 3:
            self.showNormal()
        elif reason == 2:
            self.showMinimized()

    def about(self):
        self.Fabout = frmAbout(author, emails, "0.1v", update, license, desc)
        self.Fabout.show()

    def issue(self):
        url = QUrl('https://github.com/P0cL4bs/WiFi-Pumpkin/issues/new')
        if not QDesktopServices.openUrl(url):
            QMessageBox.warning(self, 'Open Url', 'Could not open url')
