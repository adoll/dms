dms
===

Dead Man's Switch

Compilation: javac -d bin -sourcepath src -cp "lib/\*" src/com/forbes/dms/*.java

Execution: java -cp "bin:lib/*" com.forbes.dms.Node 5 10 sample.txt

Where sample.txt is a file to be encrypted

4tcztyo4nfpdx2ot.onion is the onion address of our lone message board server

Tor must be running, and a SOCKS proxy must be setup for port 9150 (this is done for you if you are running Tor from the Tor Browser bundle, otherwise the port is 9050 and you must change your torrc).

Python must be installed, and java 1.7 should be installed.
