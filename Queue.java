public class Queue
{
    Node front;
    Node rear;

    public Queue()
    {
        front = rear = null;
    }
    public void enqueue (Node t)
    {
        //System.out.println("front = "+front+ "   t = "+t);
        if (front == null)
        {
            front = rear = t;
            t.next = null;
        }
        else
        {
            rear.next = t;
            t.next = null;
            rear = t;
        }
    }
    public Node dequeue ()
    {
        Node t = null;

        t = front;
        front = front.next;
        if (front == null)
            rear = null;


        return t;
    }
    public boolean isEmpty()
    {
        return front == null;
    }
    public void remove(Node t)
    {

        if (front !=null)
        {
            Node p = null;
            Node c = front;
            while (c != t)
            {
                p = c;
                c = c.next;
            }
            if (p == null)    // node to be removed is the first node
            {
                front = front.next;
            }
            else             // node is not the first node
            {
                p.next = c.next;
            }
        }
    }
}
