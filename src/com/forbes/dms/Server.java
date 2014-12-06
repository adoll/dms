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

        if (k < 1 || k > nodeSet.size()) {
            return null;
        }
        
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

    public int numNodes() {
    
        return nodeSet.size();

    }

    public void addNode(Node n) {

        nodeSet.add(n);
    
    }

    public void removeNode(Node n) {

        nodeSet.remove(n);
    
    }

    public static void main(String[] args) {
    
        Node n1 = new Node();
        n1.name = "test 1";
        n1.address = "test 1";

        Node n2 = new Node();
        n2.name = "test 2";
        n2.address = "test 2";

        Node n3 = new Node();
        n3.name = "test 3";
        n3.address = "test 3";

        Server server = new Server();
        server.addNode(n1);
        server.addNode(n2);
        server.addNode(n3);

        assert (server.numNodes() == 3);

        server.removeNode(n1);

        assert (server.numNodes() == 2);

        server.addNode(n1);

        assert (server.numNodes() == 3);

        Set<Node> result = server.getRandomSubset(0);

        assert (result == null);

        result = server.getRandomSubset(1);

        assert (result.size() == 1);

        result = server.getRandomSubset(2);

        assert (result.size() == 2);

        result = server.getRandomSubset(3);

        assert (result.size() == 3);

    }
}
