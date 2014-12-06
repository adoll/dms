package com.forbes.dms;

import com.tiemens.secretshare.engine.SecretShare.ShareInfo;

import java.io.File;
import java.security.Key;
import java.util.List;

public class Node {

    public String name;
    public String address;

    /**
       Encrypts a file with a random key k, encrypts that file with the key,
       splits k into n shares, of which m are sufficient for reconstructing
       the key. Contacts the server, obtains a list of participating nodes,
       and chooses n random servers to distribute the key shares to. */
    public File encryptAndSendKey(int m, int n, File input) throws Exception {
        Key key = CryptoUtils.generateKey();
        File output = new File(input.getPath() + "_encrypted");
        CryptoUtils.encrypt(key, input, output);
        List<ShareInfo> shares = CryptoUtils.splitKey(key, m, n);
        // TODO(adoll):
        // 1. Get list of nodes from server.
        // 2. Send one share to each of n randomly chosen nodes.
        if (!input.equals(decryptFromShares(output, shares))) System.err.println("wadasddas");
        return output;
    }

    public File decryptFromShares(File input, List<ShareInfo> shares)
        throws Exception {
        Key key = CryptoUtils.combineShares(shares);
        File output = new File(input.getPath() + "_decrypted");
        CryptoUtils.decrypt(key, input, output);
        return output;
    }

    /**
       Testing main
    */
    public static void main(String[] args) {
        Node node = new Node();
        try {
            File encrypted = node.encryptAndSendKey(5, 10, new File(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
