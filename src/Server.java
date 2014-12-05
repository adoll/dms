import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.security.SecureRandom;

public class Server {

    Set<Node> nodeSet;
    SecureRandom rand;

    public Server() {

        nodeSet = new HashSet<>();
        rand = new SecureRandom();
    
    }
    
    public Set<Node> getRandomSubset(int k) {

        List<Node> nodeSetCopy = new ArrayList<>(nodeSet);
        Set<Node> result = new HashSet<>();

        for (int i = 0; i < k; i++) {
            int size = nodeSetCopy.size();
            int r = rand.nextInt(size);
            Node n = nodeSetCopy.get(r);
            result.add(n);
            nodeSetCopy.remove(r);
        }

        return result;
    
    }

    public void addNode(Node n) {

        nodeSet.add(n);
    
    }

    public void removeNode(Node n) {

        nodeSet.remove(n);
    
    }
}
