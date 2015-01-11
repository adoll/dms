package com.forbes.dms;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.engine.SecretShare.PublicInfo;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.math.BigIntUtilities.Human;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final int KEY_SIZE = 256;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC";

    /**
       Generates a random key for use in AES Encryption.
    */
    public static Key generateSymmetricKey() throws NoSuchAlgorithmException {
        // TODO(adoll): Check whether we need to set preference order for
        // our Random Generator/ possibly make keyGen a singleton.
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        Key secretKey = keyGen.generateKey();
        return secretKey;
    }

    /**
       Encrypts inputFile with key, and places encryption in outputFile
    */
    public static void encrypt(Key key, File inputFile, File outputFile)
        throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    /**
       Decrypts inputFile with key, and places encryption in outputFile
    */
    public static void decrypt(Key key, File inputFile, File outputFile)
        throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    // TODO(adoll): check whether we need to specify cipher mode (aka this
    // doesn't use ECB mode right -- lol it does, change this once everything
    // works?
    private static void doCrypto(int cipherMode, Key key, File inputFile,
                                 File outputFile) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, key);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                 | InvalidKeyException | BadPaddingException
                 | IllegalBlockSizeException | IOException ex) {
            throw new RuntimeException("Error encrypting/decrypting file", ex);
        }
    }

    /**
       Splits a Key k into n shares, s.t m are sufficient for reconstructing
       the key.
    */
    public static List<ShareInfo> splitKey(Key k, int m, int n) {
        PublicInfo publicInfo = new PublicInfo(n, m, null, "");
        SecretShare secretShare = new SecretShare(publicInfo);
        System.out.println(k.getEncoded().length);
        BigInteger secret = new BigInteger(1, k.getEncoded());
        SecretShare.SplitSecretOutput generated = secretShare.split(secret);
        return generated.getShareInfos();
    }

    public static Key combineShares(final List<ShareInfo> shares) {
        PublicInfo publicInfo = shares.get(0).getPublicInfo();
        SecretShare secretShare = new SecretShare(publicInfo);
        BigInteger secret = secretShare.combine(shares).getSecret();
        System.out.println(bigIntegerToBytes(secret, 32));
        return new SecretKeySpec(bigIntegerToBytes(secret, 32), ALGORITHM);

    }
    
    // We should maybe move this to a standalone file.
    public static byte[] bigIntegerToBytes(BigInteger x, int outputSize) {
        // Convert the BigInteger x to a byte-array
        // x must be non-negative
        // outputSize is the expected size of the output array
        //     (you need to supply this because the code here can't tell how
        //      big an array the caller wants)
        //
        // This operation is the inverse of <bytesToBigInteger>.

        assert x.compareTo(BigInteger.ZERO) >= 0;

        byte[] rawByteArray = x.toByteArray();
        if(rawByteArray.length==outputSize)    return rawByteArray;

        byte[] ret = new byte[outputSize];
        if(rawByteArray.length > outputSize){
            assert (rawByteArray.length == outputSize+1);
            // a sign-extension byte got added; remove it
            for(int i=0; i<outputSize; ++i){
                ret[i] = rawByteArray[i+1];
            }
        }else{  // rawByteArray.length < outputSize
            int diff = outputSize-rawByteArray.length;
            for(int i=0; i<outputSize; ++i){
                ret[i] = (i<diff) ? 0 : rawByteArray[i-diff];
            }
        }
        return ret;
    }
}
