from Core.loaders.Stealth.PackagesUI import *

''' http://stackoverflow.com/questions/22332106/python-qtgui-qprogressbar-color '''
class ProgressBarWid(QProgressBar):
    def __init__(self, parent=None, total=0):
        super(ProgressBarWid, self).__init__()
        self.setMinimum(1)
        self.setMaximum(total)
        self._active = False
        self.setAlignment(Qt.AlignCenter)
        self._text = None

    def setText(self, text):
        self._text = text

    def text(self):
        if self._text != None:
            return QString(str(self._text))
        return QString('')

    def update_bar_simple(self, add):
        value = self.value() + add
        self.setValue(value)
        if value > 50:
            self.change_color("green")

    def update_bar(self, add):
        while True:
            time.sleep(0.01)
            value = self.value() + add
            self.setValue(value)
            if value > 50:
                self.change_color("green")
            qApp.processEvents()
            if (not self._active or value >= self.maximum()):
                break
        self._active = False

    def closeEvent(self, event):
        self._active = False

    def change_color(self, color):
        template_css = """QProgressBar::chunk { background: %s; }"""
        css = template_css % color
        self.setStyleSheet(css)