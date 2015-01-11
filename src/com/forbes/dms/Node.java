package com.forbes.dms;

import com.tiemens.secretshare.math.BigIntStringChecksum;
import com.tiemens.secretshare.engine.SecretShare.PublicInfo;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.math.BigInteger;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Node {
    /**
       Encrypts a file with a random key k, encrypts that file with the key,
       splits k into n shares, of which m are sufficient for reconstructing
       the key. Contacts the server, obtains a list of participating nodes,
       and chooses n random servers to distribute the key shares to. */
    public static File encryptFile(int m, int n, File input,
                                   List<ShareInfo> shares) throws Exception {
        Key key = CryptoUtils.generateSymmetricKey();
        File output = new File(input.getPath() + "_encrypted");
        CryptoUtils.encrypt(key, input, output);
        shares.addAll(CryptoUtils.splitKey(key, m, n));
        return output;
    }

    public static File decryptFromShares(File input, List<ShareInfo> shares)
        throws Exception {
        Key key = CryptoUtils.combineShares(shares);
        File output = new File(input.getPath() + "_decrypted");
        CryptoUtils.decrypt(key, input, output);
        return output;
    }

    public static String encodeShare(ShareInfo share) {
        JSONObject obj = new JSONObject();
        obj.put("share", BigIntStringChecksum.create(share.getShare())
                .toString()); 
        obj.put("index", new Integer(share.getIndex()));
        obj.put("order", new Integer(share.getPublicInfo().getK()));
        return obj.toString();
    }
    
    
    public static ShareInfo decodeEncodedShare(String encodedShare) {
        JSONObject obj = (JSONObject) JSONValue.parse(encodedShare);
        PublicInfo pub = new PublicInfo(null, 
                                        (int) (long) obj.get("order"),
                                        null, null);
        BigInteger big = BigIntStringChecksum.fromString(
            (String)obj.get("share")).asBigInteger();
    return new ShareInfo((int) (long) obj.get("index"), big, pub); 
    }

    /**
       Main
    */
    public static void main(String[] args) {
        if (args[0].equals("+")) {
            int m = Integer.parseInt(args[1]);
            int n = Integer.parseInt(args[2]);
            if (m > n) System.err.println("m must be less than or equal to n");
            List<ShareInfo> shares = new ArrayList<>();
            try {
                File encrypted = Node.encryptFile(m, n, 
                                                  new File(args[3]), shares);
                for (ShareInfo share : shares) {
                    System.out.println(Node.encodeShare(share));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (args[0].equals("-")) {
            BufferedReader stdin = new BufferedReader(
                new InputStreamReader(System.in));
            String line;
            List<ShareInfo> shares = new ArrayList<>();
            try {
                while ((line = stdin.readLine()) != null && line.length()!= 0) {
                    shares.add(decodeEncodedShare(line));
                } 
                File encrypted = Node.decryptFromShares(new File(args[1]),
                                                        shares);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
    }
}
