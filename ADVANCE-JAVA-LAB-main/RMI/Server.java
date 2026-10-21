import java.rmi.Naming;
public class Server {

    public static void main(String[] args) {

        try {
            MyRemote obj = new MyRemote();

            Naming.rebind("rmi://localhost/Add", obj);

            System.out.println("Server is ready...");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}