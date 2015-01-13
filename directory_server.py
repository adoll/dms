from flask import Flask
from flask import request

from Crypto.Signature import PKCS1_PSS
from Crypto.Hash import SHA
from Crypto.PublicKey import RSA

import json

app = Flask(__name__)

keys = []
shares = []

def add_user(sig, pubkey_s):
    try:
        pubkey = RSA.importKey(pubkey_s)
    except ValueError:
        return "Failure"
    message = "To be signed"
    h = SHA.new()
    h.update(message)
    verifier = PKCS1_PSS.new(pubkey)
    if verifier.verify(h, sig.decode("base64")):

        iden_h = SHA.new()
        iden_h.update(pubkey_s)
        iden =iden_h.hexdigest()[:10]
        key = {"id":iden, "pubkey":pubkey_s}

        if key not in keys:
            keys.append(key)
        return "Success"
    else:
        return "Failure"

@app.route('/', methods=["GET", "POST"])
def access_server():
    if request.method == 'POST':
        return add_user(request.form['signature'], 
                request.form['pubkey'])
    else:
        return json.dumps(keys)

@app.route('/shares', methods=["GET", "POST"])
def post_shares():
    if request.method == 'POST':
        shares.append(request.form['share'])
        return "Success"
    else:
        return json.dumps(shares)


if __name__ == "__main__":
    app.debug = True
    app.run()
