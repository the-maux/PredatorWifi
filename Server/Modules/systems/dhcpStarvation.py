from Core.packets.network import ThreadAttackStar
from Core.loaders.Stealth.PackagesUI import *


class frm_dhcp_Attack(PredatorModule):
    def __init__(self, parent=None):
        super(frm_dhcp_Attack, self).__init__(parent)
        self.loadtheme(self.configure.XmlThemeSelected())
        self.setWindowTitle("Dhcp Starvation Attack")
        self.Main       = QVBoxLayout()
        self.control    = None
        self.GUI()

    def GUI(self):
        self.form       = QFormLayout()
        self.list_log   = QListWidget()
        self.check      = QLabel("")
        self.btn_Start_attack   = QPushButton("Start Attack",self)
        self.btn_Stop_attack    = QPushButton("Stop Attack",self)
        self.check.setText("[ OFF ]")
        self.check.setStyleSheet("QLabel {  color : red; }")

        self.btn_Start_attack.clicked.connect(self.D_attack)
        self.btn_Stop_attack.clicked.connect(self.kill_thread)

        self.btn_Start_attack.setIcon(QIcon("Icons/start.png"))
        self.btn_Stop_attack.setIcon(QIcon("Icons/Stop.png"))

        self.form.addRow(self.list_log)
        self.form.addRow("Status Attack:",self.check)
        self.form.addRow(self.btn_Start_attack, self.btn_Stop_attack)
        self.Main.addLayout(self.form)
        self.setLayout(self.Main)


    def getloggerAttack(self,log):
        self.list_log.addItem(log)

    def D_attack(self):
        interface = Refactor.get_interfaces()['activated']
        if interface != None:
            self.check.setText("[ ON ]")
            self.check.setStyleSheet("QLabel {  color : green; }")
            self.threadstar = ThreadAttackStar(interface)
            self.connect(self.threadstar,SIGNAL("Activated ( QString )"),self.getloggerAttack)
            self.threadstar.setObjectName("DHCP Starvation")
            self.threadstar.start()
            return
        QMessageBox.information(self, 'Interface Not found', 'None detected network interface try again.')

    def attack_OFF(self):
        self.check.setStyleSheet("QLabel {  color : red; }")

    def kill_thread(self):
        self.threadstar.stop()
        self.attack_OFF()
        self.list_log.clear()