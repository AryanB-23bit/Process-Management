public class Node
{
    int processId;
    int processPriority;
    int processarrivalTime;
    int processRunTime;
    int processRemainingTime;

    Node next;

    public Node()
    {
        processId = 0;
        processPriority = 0;
        processarrivalTime = 0;
        processRunTime = 0;
        processRemainingTime = 0;

        next = null;
    }
}
