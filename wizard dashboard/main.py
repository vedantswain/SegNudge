from __future__ import print_function
import kivy
from kivy.app import App
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.gridlayout import GridLayout
from kivy.core.window import Window
kivy.require('1.9.0')
import time
import sys
import json
import requests

timenow = time.strftime("%H:%M:%S")

state = 0
btn1 = Button(text="Aluminum")
btn2 = Button(text="Plastic")
btn3 = Button(text="Paper")
btn4 = Button(text="Landfill")
f = open('myapp.csv','a')

class GUI(GridLayout):

    def __init__(self, **kwargs):
        super(GUI, self).__init__(**kwargs)

        self._keyboard = Window.request_keyboard(self._keyboard_closed, self)
        self._keyboard.bind(on_key_down=self._on_keyboard_down)

        self.cols = 4

        btn1.bind(on_release=self.fire)
        self.add_widget(btn1)

        self.add_widget(btn2)
        btn2.bind(on_release=self.fire2)

        self.add_widget(btn3)
        btn3.bind(on_release=self.fire3)

        self.add_widget(btn4)
        btn4.bind(on_release=self.fire4)

    def _keyboard_closed(self):
        self._keyboard.unbind(on_key_down=self._on_keyboard_down)
        self._keyboard = None

    def _on_keyboard_down(self, keyboard, keycode, text, modifiers):
        if keycode[1] == 'a':
            btn1._do_press()
            self.fire(self)
        elif keycode[1] == 's':
            btn2._do_press()
            self.fire2(self)
        elif keycode[1] == 'd':
            btn3._do_press()
            self.fire3(self)
        elif keycode[1] == 'f':
            btn4._do_press()
            self.fire4(self)
        return True


    def fire(self, obj):
        print(timenow +","+"Aluminum")
        f.write(timenow +","+'Aluminum\n')
        self.push("aluminum")
    def fire2(self, obj):
        print(timenow +","+"fire Plastic")
        f.write(timenow +","+'Plastic\n')
        self.push("plastic")
    def fire3(self, obj):
        print(timenow +","+"fire Paper")
        f.write(timenow +","+'Paper\n')
        self.push("paper")
    def fire4(self, obj):
        print(timenow +","+"fire Landfill")
        f.write(timenow +","+'Landfill\n')
        self.push("landfill")

    def push(self, bintype):
        print ("teststs")
        url="https://fcm.googleapis.com/fcm/send"
        server_key="AIzaSyB3QhKVX5vEVa4VM7Wb0WvNuIH469HNZJA"
        alpha=10
        headers = {'Content-type': 'application/json', 'Authorization': 'key='+server_key }
        topic = "/topics/research-intervention"
        reg_ids = ["d65g_WO0z18:APA91bHxuB07aSpJyhOoMyhm087ZRLlWsl6Gym5DCA1OBvWNAVLWCXuGKRa1vBr4OlhrmrAJPgZ8n5Roi1kEHCcsiBJ1pFxk6TQjVKhE_oEijqlGXBNlLnkcvCC08zdvKvP1BmjQkMV8"]

        data = {
            "bin_type" : bintype,
        }

        body={
            "data": data,
            "registration_ids": reg_ids,
            # "to": "/topics/"+sys.argv[1]
        }

        r = requests.post(url, data=json.dumps(body), headers=headers)

        print(r.status_code, r.reason)
        print(r.text + '...')
        # print (body)


class Main(App):
    def build(self):
        return GUI()

if __name__ == '__main__':
    Main().run()
    f.write("time"+","+"command\n")

