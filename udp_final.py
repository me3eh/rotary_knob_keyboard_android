import socket
from pynput import keyboard
androidAddressPort1   = ("172.16.69.196", 3000)
UDPClientSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
        

def on_press(key):
    global pressedAt, lastPressed, sock, HOST, PORT
    key_str = str(key)
    if key_str == "Key.media_volume_down": #spostamento a destra del rotary knob, invia 2 
        print("left")
        xd = UDPClientSocket.sendto(b"1", androidAddressPort1)
    elif key_str == "Key.media_volume_up": #spostamento a sinistra del rotary knob, invia 0
        print("right")
        xd = UDPClientSocket.sendto(b"2", androidAddressPort1)
    elif key_str == "Key.media_volume_mute":#pressione del rotary knob, invia 1
        print("OK")
        xd = UDPClientSocket.sendto(b"3", androidAddressPort1)
    elif key_str == "Key.media_volume_": #pressione del tasto esc sulla tastiera del computer, per uscire
        exit(1)
def on_release(key):
    global pressedAt, lastPressed

# Collect events until released
with keyboard.Listener(
        on_press=on_press,
        on_release=on_release) as listener:
    listener.join()
