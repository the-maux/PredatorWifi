
import urllib2
from subprocess import call,Popen,PIPE,STDOUT
import threading
from os import path
from PyQt4.QtCore import QThread,SIGNAL,pyqtSignal
from PyQt4.QtGui import QMessageBox

class TimerThread(threading._Timer):
    def run(self):
        while True:
            self.finished.wait(self.interval)
            if self.finished.is_set():
                return
            else:
                self.function(*self.args, **self.kwargs)

class UrllibDownload(QThread):
    '''Qthread Urllib download Git ChangeLog'''
    data_downloaded = pyqtSignal(object)
    def __init__(self, url):
        QThread.__init__(self)
        self.url = url
        self.response = None
    def run(self):
        try:
            self.response = urllib2.urlopen(self.url).read()
            self.data_downloaded.emit(self.response)
        except urllib2.URLError:
            self.data_downloaded.emit('URLError')