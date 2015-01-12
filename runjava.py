import subprocess

def run_java(command):
    c_list = command.split(' ')
    s = subprocess.Popen(c_list, stdout=subprocess.PIPE)

    results = []

    for l in iter(s.stdout.readline, ''):
        results.append(l.strip())

    return results

