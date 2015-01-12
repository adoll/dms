import requests
import sys
import time
import json
import random
import runjava

from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto.Hash import SHA
from Crypto.Signature import PKCS1_PSS

check_in_command = "CHECKIN"
add_command = "ADD"

directory_server = "http://127.0.0.1:5000"
message_board = "http://127.0.0.1:5001"

key = RSA.importKey(open('dms_key.pem').read())
pubkey = key.publickey()
pubkey_s = pubkey.exportKey("PEM")

iden_h = SHA.new()
iden_h.update(pubkey_s)
own_id = iden_h.hexdigest()[:10]

directory = {}
safes = []
shares = []
id_list = []

def check_in(to):

    to_key = RSA.importKey(directory[to])
    ts = time.time()

    check_in = {"command": check_in_command, "from": own_id, "timestamp": ts}
    check_in_s = json.dumps(check_in)

    cipher = PKCS1_OAEP.new(to_key)
    ciphertext = cipher.encrypt(check_in_s)

    h = SHA.new()
    h.update(ciphertext)
    signer = PKCS1_PSS.new(key)
    signature = signer.sign(h)

    payload = {"to": to, "message": ciphertext.encode("base64"), "signature": signature.encode("base64")}
    r = requests.post(message_board, data=payload)

def add(to, period, share):

    to_key = RSA.importKey(directory[to])
    ts = time.time()

    message = {"command": add_command, "from": own_id, "period": period, "share": share}
    message_s = json.dumps(message)

    cipher = PKCS1_OAEP.new(to_key)
    ciphertext = cipher.encrypt(message_s)

    h = SHA.new()
    h.update(ciphertext)
    signer = PKCS1_PSS.new(key)
    signature = signer.sign(h)

    payload = {"to": to, "message": ciphertext.encode("base64"), "signature": signature.encode("base64")}
    r = requests.post(message_board, data=payload)

def get_directory():
    r = requests.get(directory_server)
    j = json.loads(r.text)
    for i in j:
        directory[i["id"]] = i["pubkey"]
        id_list.append(i["id"])

def select_safes(n):
    N = len(id_list)
    safe_indices = random.sample(range(0, N), n)
    f = open("safes", "w")

    for i in safe_indices:
        f.write(id_list[i])
        f.write("\n")
        safes.append(id_list[i])

def read_safes():
    f = open("safes", "r")

    for l in f.readlines():
        safes.append(l.strip())

def get_shares(m, n, f):
    command = "java -cp bin:lib/* com.forbes.dms.Node + "+ str(m) +" "+ str(n) +" "+ f
    r = runjava.run_java(command)

    for l in r:
        shares.append(l)

def main():
    if sys.argv[1] == "ACTIVATE":
        try:
            filename = sys.argv[2]
            m = int(sys.argv[3])
            n = int(sys.argv[4])
            t = int(sys.argv[5])
        except:
            print "Usage: python client.py ACTIVATE filename m n period"
            return

        if (m > n):
            print "m is greater than n"
            return

        get_shares(m, n, filename)
        get_directory()

        N = len(id_list)
        if (n > N):
            print "not enough nodes"
            return

        select_safes(n)

        i = 0
        for s in safes:
            add(s, shares[i], t)
            i += 1

    elif sys.argv[1] == "CHECKIN":

        get_directory()
        read_safes()

        for s in safes:
            check_in(s)

if __name__ == "__main__":
    main()
