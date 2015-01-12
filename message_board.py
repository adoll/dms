from flask import Flask
from flask import request
import json

app = Flask(__name__)

messages = []

def add_message(to, sig, message):
    messages.append({"to":to, "message": message, "signature": sig})
    return "Success"

@app.route('/', methods=["GET", "POST"])
def access_server():
    if request.method == 'POST':
        return add_message(request.form['to'], 
                request.form['signature'], 
                request.form['message'])
    else:
        return json.dumps(messages)

if __name__ == "__main__":
    app.debug = True
    app.run(port=5001)
