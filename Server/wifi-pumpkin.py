#!/usr/bin/env python2.7
from sys import argv,exit
from os import getuid
from PyQt4.QtGui import QApplication,QIcon
from Core.Main import Initialize
from Core.loaders.checker.Privilege import frm_privelege
from Core.loaders.checker.check_depen import check_dependencies
from Core.Utils import Refactor

def ExecRootApp(root):
    app = Initialize()
    app.setWindowIcon(QIcon('rsc/icon.ico'))
    app.center(),app.show()
    exit(root.exec_())

if __name__ == '__main__':
    check_dependencies()
    main = QApplication(argv)
    if not getuid() == 0:
        priv = frm_privelege()
        priv.setWindowIcon(QIcon('rsc/icon.ico'))
        priv.show(),main.exec_()
        exit(Refactor.threadRoot(priv.Editpassword.text()))
    ExecRootApp(main)
