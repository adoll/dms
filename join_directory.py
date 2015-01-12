import sys
from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_PSS
from Crypto.Hash import SHA
import requests

if len(sys.argv) == 1:
    key = RSA.generate(2048)
    f = open('dms_key.pem', 'w')
    f.write(key.exportKey('PEM'))
    f.close()
    
if len(sys.argv) == 2:
    f = open(sys.argv[1], 'r')
    key = RSA.importKey(f.read())

pubkey = key.publickey()
pubkey_s = pubkey.exportKey('PEM')

message = "To be signed"
h = SHA.new()
h.update(message)

signer = PKCS1_PSS.new(key)
signature = signer.sign(h)

s = signature.encode("base64")

iden_h = SHA.new()
iden_h.update(pubkey_s)
iden = iden_h.hexdigest()[:10]

payload = {'pubkey': pubkey_s, 'signature': s}
r = requests.post("http://127.0.0.1:5000", data=payload)
print(r.text)

print "Your id is " + iden
