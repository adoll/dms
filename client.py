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
        print i["id"]

def select_safes(n):
    N = len(id_list)

    if (n > N):
        print "n is too Large"
        return

    safe_indices = random.sample(range(0, N), n)
    f = open("safes", "w")

    for i in safe_indices:
        f.write(id_list[i])
        safes.append(id_list[i])

def read_safes():
    f = open("safes", "r")

    for l in f.readline():
        safes.append(l)

def get_shares():
    command = "java -cp bin:lib/* com.forbes.dms.Node + 5 10 sample.txt"
    r = run_java(command)

    for l in r:
        shares.append(l)
