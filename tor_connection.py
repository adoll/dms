import urllib
import string
import socks

def post(datadict):
    s = socks.socksocket()
    s.setproxy(socks.PROXY_TYPE_SOCKS5, 'localhost', 9050)
    s.connect(('4tcztyo4nfpdx2ot.onion', 80))
    request = 'POST / HTTP/1.1\r\nContent-Type: application/x-www-form-urlencoded\r\n'
    payload = urllib.urlencode(datadict)
    request += 'Content-Length: ' + str(len(payload)) + '\r\n\r\n'
    request += payload
    s.send(request)

    data = ''
    buf = s.recv(1024)
    while len(buf):
        data += buf
        buf = s.recv(1024)
    s.close()
    header_end = string.find(data, '\r\n\r\n')
    return data[header_end + 4:]

def get():
    s = socks.socksocket()
    s.setproxy(socks.PROXY_TYPE_SOCKS5, 'localhost', 9050)
    s.connect(('4tcztyo4nfpdx2ot.onion', 80))
    s.send('GET / HTTP/1.1\r\n\r\n')

    data = ''
    buf = s.recv(1024)
    while len(buf):
        data += buf
        buf = s.recv(1024)
    s.close()
    header_end = string.find(data, '\r\n\r\n')
    return data[header_end + 4:]

def main():
    print get()
    print post({'id':'aaron'})

if __name__ == "__main__":
    main()



