from Plugin import PluginProxy

class noscroll(PluginProxy):
    ''' this module proxy not work noscroll on html page.'''
    _name          = 'noscroll'
    _activated     = False
    _instance      = None
    _requiresArgs  = False

    @staticmethod
    def getInstance():
        if noscroll._instance is None:
            noscroll._instance = noscroll()
        return noscroll._instance

    def __init__(self):
        self.LoggerInjector()
        self.args = None

    def setInjectionCode(self, code):
        self.args = code

    def inject(self, data, url):
        injection_code = '''</head> <!-- Put an invisible div over everything -->
<div style="position:fixed;width:100%;height:100%;z-index:9001;opacity:0;"></div>'''
        self.logging.info("Injected: %s" % (url))
        return data.replace('</head>',injection_code)


