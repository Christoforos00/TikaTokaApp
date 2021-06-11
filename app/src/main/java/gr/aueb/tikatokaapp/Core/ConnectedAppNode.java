package gr.aueb.tikatokaapp.Core;

public class ConnectedAppNode {
    private static AppNode appNode;

    public static AppNode getAppNode() {
        return appNode;
    }

    public static void setAppNode(AppNode newAppNode) {
        appNode = newAppNode;
    }
}
