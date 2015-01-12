import requests
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
message_board = "http://127.0.0.1:5001"

key = RSA.importKey(open('dms_key.pem').read())
pubkey = key.publickey()
pubkey_s = pubkey.exportKey("PEM")

iden_h = SHA.new()
iden_h.update(pubkey_s)
own_id = iden_h.hexdigest()[:10]

directory = {}

def get_directory():
    r = requests.get(directory_server)
    j = json.loads(r.text)
    for i in j:
        directory[i["id"]] = i["pubkey"]

def read_board():
    r = requests.get(message_board)
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
                    print "CHECKIN "+" from "+str(mj["from"])+" at "+str(mj["timestamp"])
                elif mj["command"] == add_command:
                    print "ADD "+" from "+str(mj["from"])+" period "+str(mj["period"])+" share "+str(mj["share"])
            else:
                print "FAIL"


