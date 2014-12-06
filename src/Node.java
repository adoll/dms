import java.io.File;

public class Node {
        
    public String name;
    public String address;
    
    /**
       Encrypts a file with a random key k, encrypts that file with the key,
       splits k into n shares, of which m are sufficient for reconstructing
       the key. Contacts the server, obtains a list of participating nodes,
       and chooses $n$ random servers to distribute the key shares to. */
    public File encryptAndSendKey(int m, int n, File input) {
        
        // so that this compiles
        return null;
    }

}
