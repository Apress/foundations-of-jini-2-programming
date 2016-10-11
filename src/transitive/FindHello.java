package transitive;

interface FindHello extends java.rmi.Remote {
    Hello doFind() throws java.rmi.RemoteException;
}
