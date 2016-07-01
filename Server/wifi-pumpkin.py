#!/usr/bin/env python2.7
"""
Author : Marcos Nesster - mh4root@gmail.com  PocL4bs Team
Licence : GPL v3

Description:
    WiFi-Pumpkin - Framework for Rogue Wi-Fi Access Point Attack.

Copyright:
    Copyright (C) 2015 Marcos Nesster P0cl4bs Team
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
"""

from sys import argv,exit,version_info
if version_info.major != 2:
    exit('[!] WiFi-Pumpkin need Python 2 :(')

if __name__ == '__main__':
    from Core.loaders.checker.check_depen import check_dep_pumpkin,RED,ENDC
    check_dep_pumpkin()
    from os import getuid
    if not getuid() == 0:
        exit('[{}!{}] WiFi-Pumpkin must be run as root.'.format(RED,ENDC))

    from PyQt4.QtGui import QApplication,QIcon
    main = QApplication(argv)

    from Core.Main import Initialize
    print('Loading GUI...')
    app = Initialize()
    app.setWindowIcon(QIcon('Icons/icon.ico'))
    app.center()
    app.show()

    print('WiFi-Pumpkin Running!')
    exit(main.exec_())
