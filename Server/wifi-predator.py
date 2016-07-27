#!/usr/bin/env python2.7

from sys import argv,exit,version_info
if version_info.major != 2:
    exit('[!] WiFi-Predator need Python 2 :(')

if __name__ == '__main__':
    from Core.loaders.checker.check_depen import check_dep_Predator,RED,ENDC
    check_dep_Predator()
    from os import getuid
    if not getuid() == 0:
        exit('[{}!{}] WiFi-Predator must be run as root.'.format(RED,ENDC))

    from PyQt4.QtGui import QApplication,QIcon
    main = QApplication(argv)

    from Core.Main import Initialize
    print('Loading GUI...')
    app = Initialize()
    app.setWindowIcon(QIcon('Icons/icon.ico'))
    app.center()
    app.show()

    print('WiFi-Predator Running!')
    exit(main.exec_())
