from __future__ import print_function
import kivy
from kivy.app import App
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.gridlayout import GridLayout
from kivy.core.window import Window
from kivy.properties import StringProperty, NumericProperty, ObjectProperty, ListProperty
kivy.require('1.9.0')
import time

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
    def fire2(self, obj):
        print(timenow +","+"fire Plastic")
        f.write(timenow +","+'Plastic\n')
    def fire3(self, obj):
        print(timenow +","+"fire Paper")
        f.write(timenow +","+'Paper\n')
    def fire4(self, obj):
        print(timenow +","+"fire Landfill")
        f.write(timenow +","+'Landfill\n')


class Main(App):
    def build(self):
        return GUI()

if __name__ == '__main__':
    Main().run()
    f.write("time"+","+"command\n")

