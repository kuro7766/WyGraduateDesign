# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


# Press the green button in the gutter to run the script.
import subprocess
import os

if __name__ == '__main__':
    procId = subprocess.Popen('adb -s 80f2a364 shell', stdin=subprocess.PIPE)
    comu=procId.communicate(b'su\ncp /data/data/com.example.flutter_wine/databases/wine2.db3 /sdcard/Download/wine.db3\n')
    os.system('adb  -s 80f2a364  pull /sdcard/Download/wine.db3')
    print(comu)
