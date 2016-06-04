from PyQt4.QtCore import *
from Plugins.sslstrip.StrippingProxy import StrippingProxy
from Plugins.sslstrip.URLMonitor import URLMonitor
from Plugins.sslstrip.CookieCleaner import CookieCleaner
from twisted.web import http
from twisted.internet import reactor


class Threadsslstrip(QThread):
    def __init__(self, port):
        QThread.__init__(self)
        self.port = port

    def run(self):
        print 'Starting Thread:' + self.objectName()
        listenPort = self.port
        spoofFavicon = False
        killSessions = True
        print 'SSLstrip v0.9 by Moxie Marlinspike Thread::online'
        URLMonitor.getInstance().setFaviconSpoofing(spoofFavicon)
        CookieCleaner.getInstance().setEnabled(killSessions)
        strippingFactory = http.HTTPFactory(timeout = 10)
        strippingFactory.protocol = StrippingProxy
        reactor.listenTCP(int(listenPort), strippingFactory)
        reactor.run(installSignalHandlers = False)

    def stop(self):
        print 'Stop thread:' + self.objectName()
        try:
            reactor.stop()
            reactor.crach()
        except:
            pass
