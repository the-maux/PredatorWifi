#!/usr/bin/env python2.7
from sys import argv, exit
from os import getuid
from PyQt4.QtGui import QApplication, QIcon
from Core.Main import Initialize
from Core.loaders.checker.Privilege import frm_privelege
from Core.loaders.checker.check_depen import check_dependencies
from Core.Utils import Refactor
from Modules.monitors.FrontServer import *

def RemoteControl():
    print "Entering Remote Control\t\t\t[+]"
    if "/launch" in server.Message :
        switcher = {
            "/launch ProbeMonitor": app.form_widget.btn_probe.trigger(),
            "/launch StartAttack": app.form_widget.btn_start_attack.click(),
            "/launch StopAttack": app.form_widget.btn_cancelar.click(),
        }
        print "launching Control=>" + server.Message
        switcher.get(server.Message)()
    elif "/ApName" in server.Message:
        app.form_widget.EditApName.setText(server.Message[len("/ApName "):])
    elif "/Channel" in server.Message:
        app.form_widget.EditChannel.setText(server.Message[len("/ApName "):])


if __name__ == '__main__':
    check_dependencies()
    main = QApplication(argv)
    if not getuid() == 0:
        priv = frm_privelege()
        priv.setWindowIcon(QIcon('rsc/icon.ico'))
        priv.show(), main.exec_()
        exit(Refactor.threadRoot(priv.Editpassword.text()))
    app = Initialize()
    server = FrontServer()
    QtCore.QObject.connect(server, QtCore.SIGNAL("DataReceived"), RemoteControl)
    app.setWindowIcon(QIcon('rsc/icon.ico'))
    app.center()
    app.show()
    exit(main.exec_())
