public class OpenThread {
    static int sz = 0;
    static Thread []thread = new Thread[10];

    /**
     * add thread which open for call
     * @param newthread is new open thread
     */
    static public void addThread(Thread newthread) {
        if(sz == 0) {
            thread[sz] = newthread;
            sz++;
        }
    }

    /**
     * delete thread and end call in that voice chat
     */
    static public void delete() {
        if(sz == 0) return;
        thread[sz-1].stop();
        thread[sz-1] = null;
        sz--;
    }

    /**
     * get last thread which opened for call
     * @return last thread
     */
    static public Thread getLast() {
        return thread[sz-1];
    }
}
