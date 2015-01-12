import requests
import tor_connection
import sys
import time
import json

from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto.Hash import SHA
from Crypto.Signature import PKCS1_PSS

check_in_command = "CHECKIN"
add_command = "ADD"

directory_server = "http://127.0.0.1:5000"
message_board = "4tcztyo4nfpdx2ot.onion"

key = RSA.importKey(open('dms_key.pem').read())
pubkey = key.publickey()
pubkey_s = pubkey.exportKey("PEM")

iden_h = SHA.new()
iden_h.update(pubkey_s)
own_id = iden_h.hexdigest()[:10]

directory = {}

checkins = {}
shares = {}

def get_directory():
    r = requests.get(directory_server)
    j = json.loads(r.text)
    for i in j:
        directory[i["id"]] = i["pubkey"]

def read_board():
    r = tor_connection.get(message_board)
    j = json.loads(r.text)
    for i in j:
        if (i["to"] == own_id):
            ciphertext = i["message"].decode("base64")
            cipher = PKCS1_OAEP.new(key)
            message = cipher.decrypt(ciphertext)
            mj = json.loads(message)
            their_key = RSA.importKey(directory[mj["from"]])
            
            h = SHA.new()
            h.update(ciphertext)
            verifier = PKCS1_PSS.new(their_key)
            if verifier.verify(h, i["signature"].decode("base64")):
                if mj["command"] == check_in_command:
                    from_id = str(mj["from"])
                    ts = mj["timestamp"]

                    if from_id in checkins:
                        lts = checkins[from_id]
                        if ts > lts:
                            checkins[from_id] = ts
                    else:
                        checkins[from_id] = ts
                elif mj["command"] == add_command:
                    from_id = str(mj["from"])
                    period = str(mj["period"])
                    share = str(mj["share"])
                    shares[from_id] = (share, period)
            else:
                print "FAIL"

def write_checkins():
    with open("checkins", "w") as f:
        for k,v in checkins.iteritems():
            f.write(str(k) + " " + str(v) + "\n")

def write_shares():
    with open("shares", "w") as f:
        for k,v in shares.iteritems():
            f.write(str(k) + " " + str(v[0]) + " " +str(v[1]) +  "\n")

def read_checkins():
    with open("checkins", "r") as f:
        for l in f.readlines():
            c = l.split(" ")
            checkins[c[0]] = float(c[1])

def read_shares():
    with open("shares", "r") as f:
        for l in f.readlines():
            c = l.split(" ")
            shares[c[0]] = (c[1], int(c[2]))

def main():

    get_directory()
    
    try:
        read_checkins()
        read_shares()
    except:
        pass

    read_board()

    write_checkins()
    write_shares()

if __name__ == "__main__":
    main()
