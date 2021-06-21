package gr.aueb.tikatokaapp.Core;

public class ConnectedAppNode {
    private static AppNode appNode;

    public static AppNode getAppNode() {
        return appNode;
    }

    public static void setAppNode(AppNode newAppNode) {
        appNode = newAppNode;
    }

    public static void clearAppNode()  {
        appNode.disconnect();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appNode = null;
    }
}
