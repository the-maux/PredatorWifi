#@securitytube => https://gist.github.com/securitytube/5291959#file-ssid-sniffer-scapy-python-py
from scapy.all import *

#Is mon0 Occuped ?
#If its
ap_list = []
def PacketHandler(pkt):
    if pkt.haslayer(Dot11):
        if pkt.type == 0 and pkt.subtype == 8:
            if pkt.addr2 not in ap_list:
                ap_list.append(pkt.addr2)
                print "AP MAC: %s with SSID: %s " % (pkt.addr2, pkt.info)


#sniff(iface="mon0", prn=PacketHandler)
