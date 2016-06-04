from Core.Utils import Refactor
from os import system, path, popen, mkdir
from shutil import move


def CoreSettings(view):
    range_dhcp = view.FSettings.xmlSettings('Iprange', 'range', None, False)
    view.ConfigTwin['PortRedirect'] = view.FSettings.xmlSettings('redirect', 'port', None, False)
    view.SettingsAP = {
        'interface':
            [
                'ifconfig %s up' % (view.ConfigTwin['AP_iface']),
                'ifconfig %s 10.0.0.1 netmask 255.255.255.0' % (view.ConfigTwin['AP_iface']),
                'ifconfig %s mtu 1400' % (view.ConfigTwin['AP_iface']),
                'route add -net 10.0.0.0 netmask 255.255.255.0 gw 10.0.0.1'
            ],
        'kill':
            [
                'iptables --flush',
                'iptables --table nat --flush',
                'iptables --delete-chain',
                'iptables --table nat --delete-chain',
                'ifconfig %s 0' % (view.ConfigTwin['AP_iface']),
                'killall dhpcd',
                'killall dnsmasq'
            ],
        'hostapd':
            [
                'interface={}\n'.format(str(view.selectCard.currentText())),
                'ssid={}\n'.format(str(view.EditApName.text())),
                'channel={}\n'.format(str(view.EditChannel.text())),
            ],
        'dhcp-server':
            [
                'authoritative;\n',
                'default-lease-time 600;\n',
                'max-lease-time 7200;\n',
                'subnet 10.0.0.0 netmask 255.255.255.0 {\n',
                'option routers 10.0.0.1;\n',
                'option subnet-mask 255.255.255.0;\n',
                'option domain-name \"%s\";\n' % (str(view.EditApName.text())),
                'option domain-name-servers 10.0.0.1;\n',
                'range %s;\n' % range_dhcp,
                '}',
            ],
        'dnsmasq':
            [
                'interface=%s\n' % (view.ConfigTwin['AP_iface']),
                'dhcp-range=10.0.0.1,10.0.0.50,12h\n',
                'dhcp-option=3, 10.0.0.1\n',
                'dhcp-option=6, 10.0.0.1\n',
            ]
    }
    Refactor.set_ip_forward(1)
    for i in view.SettingsAP['kill']: popen(i)
    for i in view.SettingsAP['interface']: popen(i)
    dhcp_select = view.FSettings.xmlSettings('dhcp', 'dhcp_server', None, False)
    if dhcp_select != 'dnsmasq':
        with open('Settings/dhcpd.conf', 'w') as dhcp:
            for i in view.SettingsAP['dhcp-server']: dhcp.write(i)
            dhcp.close()
            if path.isfile('/etc/dhcp/dhcpd.conf'):
                system('rm -f /etc/dhcp/dhcpd.conf')
            if not path.isdir('/etc/dhcp/'): mkdir('/etc/dhcp')
            move('Settings/dhcpd.conf', '/etc/dhcp/')
    else:
        with open('Core/config/dnsmasq.conf', 'w') as dhcp:
            for i in view.SettingsAP['dnsmasq']:
                dhcp.write(i)
            dhcp.close()
